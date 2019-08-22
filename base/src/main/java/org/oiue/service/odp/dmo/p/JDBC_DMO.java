package org.oiue.service.odp.dmo.p;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URL;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.oiue.service.log.Logger;
import org.oiue.service.odp.dmo.BlobType;
import org.oiue.service.odp.dmo.ClobType;
import org.oiue.service.odp.proxy.ProxyFactory;
import org.oiue.tools.StatusResult;
import org.oiue.tools.exception.OIUEException;
import org.oiue.tools.json.JSONUtil;

@SuppressWarnings({ "rawtypes", "serial", "unchecked" })
public abstract class JDBC_DMO implements IJDBC_DMO {
	protected Connection conn;
	protected PreparedStatement pstmt;
	protected CallableStatement cstmt;
	protected String sql;
	protected ResultSet rs;
	protected Statement stmt;

	protected ProxyFactory pf = ProxyFactory.getInstance();
	protected Logger log = pf.getLogger(this.getClass());
	
	@Override
	public Connection getConn() {
		if (conn == null)
			throw new RuntimeException("conn is null!");
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
	public void setPstmt(PreparedStatement pstmt) {
		this.pstmt = pstmt;
	}
	
	@Override
	public CallableStatement getStmt() {
		return cstmt;
	}
	
	@Override
	public StatusResult execute(String sql, Collection queryParams) {
		StatusResult sr = new StatusResult();
		try {
			if (this.getRs() != null) {
				this.getRs().close();
			}
			sql = sql.trim();
			if (sql.toLowerCase().startsWith("call")) {
				this.cstmt = this.getConn().prepareCall(sql);
				this.cstmt.setFetchSize(50);
				int i = 1;
				if (queryParams != null)
					for (Iterator iterator = queryParams.iterator(); iterator.hasNext();) {
						cstmt.setObject(i++, iterator.next());
					}
				cstmt.execute();
			} else if (sql.toLowerCase().startsWith("insert") || sql.toLowerCase().startsWith("update") || sql.toLowerCase().startsWith("delete")) {
				this.pstmt = this.getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				this.setQueryParams(queryParams);
				this.pstmt.setFetchSize(50);
				Map data = new HashMap<>();
				data.put("count", this.getPstmt().executeUpdate());
				ResultSet generatedKeys = this.getPstmt().getGeneratedKeys();
				if (generatedKeys != null)
					try {
						data.put("root", getResult(generatedKeys));
					} finally {
						if (generatedKeys != null)
							generatedKeys.close();
						generatedKeys = null;
					}
				sr.setData(data);
			} else if (sql.toLowerCase().startsWith("select") || sql.toLowerCase().startsWith("with")) {
				this.pstmt = this.getConn().prepareStatement(sql);
				this.setQueryParams(queryParams);
				this.pstmt.setFetchSize(50);
				this.rs = this.getPstmt().executeQuery();
			} else if (sql.toLowerCase().startsWith("create") || sql.toLowerCase().startsWith("alter")) {
				this.pstmt = this.getConn().prepareStatement(sql);
				this.getPstmt().execute();
			} else {
				throw new RuntimeException("sql:" + sql);
			}
		} catch (OIUEException e) {
			throw e;
		} catch (SQLException e) {
			Map per = new HashMap<>();
			per.put("errorcode", e.getErrorCode());
			per.put("sqlstate", e.getSQLState());
			per.put("sql", sql);
			per.put("queryParams", queryParams);
			log.error("per:"+per+"\t"+e.getMessage(), e);
			throw new OIUEException(StatusResult._sql_error, per, e);
		} catch (Throwable e) {
			Map per = new HashMap<>();
			per.put("sql", sql);
			per.put("queryParams", queryParams);
			log.error("per:"+per+"\t"+e.getMessage(), e);
			throw new OIUEException(StatusResult._data_error, per, e);
		}
		
		return sr;
	}
	
	/**
	 * 参JDBCUtil实现 设置prepared的参数
	 *
	 * @param column 参数的标号
	 * @param obj Object obj是参数值
	 */
	public void setParameter(int column, Object obj) {
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
			} else if (obj instanceof Map) {
				pstmt.setString(column, JSONUtil.parserToStr((Map) obj));
			} else if (obj instanceof List) {
				pstmt.setString(column, JSONUtil.parserToStr((List) obj));
			} else if (obj == null) {
//				pstmt.setString(column, null);
				pstmt.setNull(column, Types.NULL);
			} else {// if(obj instanceof Boolean)
				pstmt.setObject(column, obj);
			}
			// else logger.error("不支持的参数类型!");
			
		} catch (Exception e) {
			throw new OIUEException(StatusResult._blocking_errors, "参数设置出错[" + column + "," + obj + "]：" + e, e);
		}
	}
	
	@Override
	public List<Map> getResult(ResultSet rs) {
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			List<Map> listMap = new ArrayList<Map>();
			while (rs.next()) {
				int sum = rsmd.getColumnCount();
				Hashtable row = new Hashtable();
				for (int i = 1; i < sum + 1; i++) {
					Object value = rs.getObject(i);
					if ((value instanceof BigDecimal)) {
						if (((BigDecimal) value).scale() == 0) {
							value = Long.valueOf(((BigDecimal) value).longValue());
						} else {
							value = Double.valueOf(((BigDecimal) value).doubleValue());
						}
					} else if ((value instanceof Clob)) {
						value = clobToString((Clob) value);
					}
					// String key = rsmd.getColumnName(i);
					String key = rsmd.getColumnLabel(i);
					row.put(key, value == null ? "" : value);
				}
				listMap.add(row);
			}
			return listMap;
			
		} catch (Exception e) {
			throw new OIUEException(StatusResult._blocking_errors, "", e);
		}
	}
	
	@Override
	public Map getMapResult(ResultSet rs) {
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			int sum = rsmd.getColumnCount();
			Hashtable row = new Hashtable();
			for (int i = 1; i < sum + 1; i++) {
				Object value = rs.getObject(i);
				if ((value instanceof BigDecimal)) {
					if (((BigDecimal) value).scale() == 0) {
						value = Long.valueOf(((BigDecimal) value).longValue());
					} else {
						value = Double.valueOf(((BigDecimal) value).doubleValue());
					}
				} else if ((value instanceof Clob)) {
					value = clobToString((Clob) value);
				}
				// String key = rsmd.getColumnName(i);
				String key = rsmd.getColumnLabel(i);
				row.put(key, value == null ? "" : value);
			}
			return row;
		} catch (Exception e) {
			throw new OIUEException(StatusResult._blocking_errors, "", e);
		}
	}
	
	protected String clobToString(Clob clob) {
		if (clob == null) {
			return null;
		}
		try {
			Reader inStreamDoc = clob.getCharacterStream();
			char[] tempDoc = new char[(int) clob.length()];
			inStreamDoc.read(tempDoc);
			inStreamDoc.close();
			return new String(tempDoc);
		} catch (IOException | SQLException e) {
			throw new OIUEException(StatusResult._blocking_errors, "", e);
		}
	}
	
	/**
	 * 根据参数值queryParams集合
	 *
	 * @param queryParams 查询参数集合 @ 异常
	 */
	@Override
	public void setQueryParams(Collection queryParams) {
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
					log.error(e2.getMessage(), e2);
				}
				try {
					if (rs != null)
						rs.close();
					rs = null;
				} catch (Exception e2) {
					log.error(e2.getMessage(), e2);
				}
			}
			if (cstmt != null) {
				cstmt.close();
				cstmt = null;
			}
			if (pstmt != null) {
				pstmt.close();
				pstmt = null;
			}
		} catch (Throwable t) {
			return false;
		}
		if (pstmt == null && cstmt == null) {
			return true;
		}
		return false;
	}
	
}
