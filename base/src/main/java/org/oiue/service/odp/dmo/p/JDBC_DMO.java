package org.oiue.service.odp.dmo.p;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URL;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.oiue.service.odp.dmo.BlobType;
import org.oiue.service.odp.dmo.ClobType;
import org.oiue.tools.StatusResult;

@SuppressWarnings({ "rawtypes", "serial" })
public abstract class JDBC_DMO implements IJDBC_DMO {
	protected Connection conn;
	protected PreparedStatement pstmt;
	protected String sql;
	protected ResultSet rs;
	protected CallableStatement stmt;
	@Override
	public Connection getConn() {
		return conn;
	}

	@Override
	public void setConn(Connection conn) {
		this.conn = conn;
	}

	@Override
	public ResultSet getRs() {
		return rs;
	}

	@Override
	public PreparedStatement getPstmt() {
		return pstmt;
	}

	@Override
	public CallableStatement getStmt() {
		return stmt;
	}

	@Override
	public StatusResult execute(String sql,Collection queryParams){
		StatusResult sr = new StatusResult();
		try {
			if (this.getRs()!=null) {
				this.getRs().close();
			}
			sql = sql.trim().toLowerCase();
			if(sql.startsWith("call")){
				this.stmt=this.getConn().prepareCall(sql);
				int i=1;
				if(queryParams!=null)
					for (Iterator iterator = queryParams.iterator(); iterator.hasNext();) {
						stmt.setObject(i++ , iterator.next());
					}
				stmt.execute();
			}else if(sql.startsWith("insert")||sql.startsWith("update")||sql.startsWith("delete")){
				this.pstmt = this.getConn().prepareStatement(sql);
				this.setQueryParams(queryParams);
				this.getPstmt().executeUpdate();
			}else if(sql.startsWith("select")){
				this.pstmt = this.getConn().prepareStatement(sql);
				this.setQueryParams(queryParams);
				this.rs=this.getPstmt().executeQuery();
			}else{
				throw new RuntimeException("sql:"+sql);
			}
		} catch (Throwable e) {
			throw new RuntimeException("sql:"+sql+",queryParams:"+queryParams, e);
		}

		return sr;
	}
	/**
	 * 参JDBCUtil实现 设置prepared的参数
	 *
	 * @param column
	 *            参数的标号
	 * @param obj
	 *            Object obj是参数值
	 * @throws SQLException
	 *             sql异常
	 */
	public void setParameter( int column, Object obj) throws java.sql.SQLException {
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
	 *            查询参数集合
	 * @throws Exception
	 *             异常
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

	@Override
	public boolean close() {
		try {
			if (rs != null) {
				try {
					if (rs.getStatement() != null)
						rs.getStatement().close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				try {
					if (rs != null)
						rs.close();
					rs = null;
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
			if (pstmt != null) {
				pstmt.close();
				pstmt = null;
			}
		} catch (Throwable t) {
			return false;
		}
		if (pstmt == null && stmt == null) {
			return true;
		}
		return false;
	}

}
