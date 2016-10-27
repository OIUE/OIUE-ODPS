package org.oiue.service.odp.dmo.p;

import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URL;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.oiue.service.odp.dmo.BlobType;
import org.oiue.service.odp.dmo.ClobType;
import org.oiue.service.odp.dmo.IJDBC_DMO;
import org.oiue.table.structure.TableModel;
import org.oiue.tools.sql.SQL;

@SuppressWarnings({"rawtypes","serial"})
public abstract class JDBC_DMO extends DMO_USR implements Serializable,IJDBC_DMO {
    
    protected Connection conn;
    protected PreparedStatement pstmt = null;
    protected CallableStatement stmt = null;
    protected ResultSet rs = null;
    protected DatabaseMetaData dbMetaData = null;

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public ResultSet getRs() {
        return rs;
    }

    public void setRs(ResultSet rs) {
        this.rs = rs;
    }

    public PreparedStatement getPstmt() {
        return pstmt;
    }

    public void setPstmt(PreparedStatement pstmt) {
        this.pstmt = pstmt;
    }

    public CallableStatement getStmt() {
        return stmt;
    }

    public void setStmt(CallableStatement stmt) {
        this.stmt = stmt;
    }


    /**
     * 参JDBCUtil实现 设置prepared的参数
     * 
     * @param column 参数的标号
     * @param obj Object obj是参数值
     * @throws SQLException
     */
    public void setParameter(int column, Object obj) throws java.sql.SQLException {
        try {
            if (obj instanceof java.lang.String) {
                String keyStrs = (String) obj;
                pstmt.setString(column, keyStrs);
            } else if (obj instanceof Integer) {
                pstmt.setInt(column, ((Integer) obj).intValue());
            } else if (obj instanceof Float) {
                pstmt.setFloat(column, ((Float) obj).floatValue());
            } else if (obj instanceof Long) {
                pstmt.setLong(column, ((Long) obj).longValue());
            } else if (obj instanceof Date) {
                pstmt.setTimestamp(column, new Timestamp(((Date) obj).getTime()));
            } else if (obj instanceof BigDecimal) {
                pstmt.setBigDecimal(column, (BigDecimal) obj);
                // ------Blob,Clob,Binary--------
            } else if (obj instanceof Blob) {
                BlobType blobType = new BlobType();
                blobType.set(pstmt, obj, column);
            } else if (obj instanceof Clob) {
                ClobType clobType = new ClobType();
                clobType.set(pstmt, obj, column);
            } else if (obj instanceof URL) {
                pstmt.setString(column, ((URL) obj).getPath());
            } else if (obj instanceof URI) {
                pstmt.setString(column, ((URI) obj).getPath());
            } else {// if(obj instanceof Boolean)
                pstmt.setObject(column, obj);
            }
            // else logger.error("不支持的参数类型!");

        } catch (Exception e) {
            throw new RuntimeException("参数设置出错[" + column + "," + obj + "]：" + e);
        }
    }

    /**
     * 根据参数值queryParams集合
     * 
     * @param queryParams
     * @throws Exception
     */
    public void setQueryParams(Collection queryParams) throws Exception {
        if ((queryParams == null) || (queryParams.isEmpty())) {
            return;
        }
        Iterator iter = queryParams.iterator();
        int i = 1;
        while (iter.hasNext()) {
            Object key = iter.next();
            setParameter(i, key);
            i++;
        }
    }

    /**
     * 根据操作对象实现插入及修改
     * 
     * @param tablemodel 抽象出来的表格对象
     * @return
     */
    public boolean Update(TableModel tm) throws Throwable {
        switch (tm.getCmdKey()) {
            case 0:// select table field for table filed mapcode
                SQL s = this.getInsertSql(tm);
                pstmt = this.getConn().prepareStatement(s.sql);
                setQueryParams(s.pers);
                tm.setRowNum(pstmt.executeUpdate());
                break;
        }
        return true;
    }

    /**
     * 根据操作对象实现插入及修改
     * 
     * @param tablemodel 抽象出来的表格对象
     * @return
     */
    public boolean Update(List<TableModel> tm) throws Throwable {
        if (tm.size() == 0)
            return false;
        switch (tm.get(0).getCmdKey()) {
            case 0:// select table field for table filed mapcode
                SQL s = this.getInsertSql(tm.get(0));
                pstmt = this.getConn().prepareStatement(s.sql);
                for (TableModel tableModel : tm) {
                    tableModel.put(tableModel.getMapRemoveID());
                    setQueryParams(tableModel.getMapData().values());
                    pstmt.addBatch();
                }
                try {
                    pstmt.executeBatch();
                } catch (Exception e) {
                    throw e;
                }
                break;
        }
        return true;
    }


    /**
     * 根据操作对象实现Tree插入及修改
     * 
     * @param tablemodel 抽象出来的表格对象
     * @return
     */
    public boolean UpdateTree(TableModel tm) throws Throwable {
        String newAutoCode = "";
        String sql = null;
        try {
            switch (tm.getCmdKey()) {
                case 0:
                    // insertTree(tableName VARCHAR(32),parentCode VARCHAR(64),name
                    // VARCHAR(64),nextNodeID int,
                    // filedStr text,valueStr text,filedIDName varchar(64),out
                    // returnVarchar VARCHAR(255))
                    sql = "call insertTree(?,?,?,?,?,?,?,?);";
                    stmt = this.getConn().prepareCall(sql);
                    stmt.setString(1, tm.getTableName());
                    stmt.setString(2, tm.getValue("autoCode") + "");
                    stmt.setString(3, tm.getValue("name") + "");
                    stmt.setInt(4, Integer.parseInt(tm.getValue("position") + ""));
                    stmt.setString(5, tm.getKeyRemoveStruture());
                    stmt.setString(6, tm.getValueStr());
                    stmt.setString(7, tm.getTableIDFieldName());
                    stmt.registerOutParameter(8, Types.VARCHAR);
                    break;

                case 1:
                    // updateTree(in tableName VARCHAR(32),in filedID int(11),in
                    // autoCode VARCHAR(64),in name VARCHAR(64),
                    // in nextNodeID int(11),in filedStr text,filedIDName
                    // varchar(64))
                    sql = "call updateTree(?,?,?,?,?,?,?);";
                    stmt = this.getConn().prepareCall(sql);
                    stmt.setString(1, tm.getTableName());
                    stmt.setInt(2, Integer.parseInt(tm.getValue(tm.getTableIDFieldName()) + ""));
                    stmt.setString(3, tm.getValue("autoCode") + "");
                    stmt.setString(4, tm.getValue("name") + "");
                    stmt.setInt(5, Integer.parseInt(tm.getValue("position") + ""));
                    stmt.setString(6, tm.getKeyValueRemoveStructure());
                    stmt.setString(7, tm.getTableIDFieldName());
                    break;
                default:
                    break;
            }

            boolean hadResults = stmt.execute();
            while (hadResults) {
            }
            switch (tm.getCmdKey()) {
                case 0:
                    newAutoCode = stmt.getString(8);
                    tm.put("autoCode", newAutoCode);
                    break;
            }
        } catch (Throwable e) {

        } finally {
            stmt.close();
            stmt = null;
        }
        return true;
    }
}
