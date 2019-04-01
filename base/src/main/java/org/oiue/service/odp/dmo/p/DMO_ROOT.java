package org.oiue.service.odp.dmo.p;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.oiue.service.odp.proxy.ProxyFactory;
import org.oiue.table.structure.TableModel;
import org.oiue.tools.StatusResult;
import org.oiue.tools.exception.OIUEException;
import org.oiue.tools.sql.SQL;

/**
 * DB操作的抽象类
 *
 * @author Every{王勤}
 */
@SuppressWarnings({ "unused", "serial", "rawtypes", "unchecked" })
public abstract class DMO_ROOT extends JDBC_DMO implements IDMO_ROOT {
	
	private ProxyFactory proxyFactory = ProxyFactory.getInstance();
	
	public DMO_ROOT() {}
	
	/**
	 * 获取分页数据总行数
	 *
	 * @param sqlStr sql
	 * @param tm 对象
	 * @return 可执行sql对象
	 */
	public SQL getRowCountSQL(String sqlStr, TableModel tm) {
		StringBuffer s = new StringBuffer();
		if (sqlStr == null) {
			return null;
		}
		String s1 = sqlStr.toLowerCase();
		if (s1.indexOf("%order by%") > 0) {
			s1 = sqlStr.substring(0, s1.indexOf("%order by%"));
		} else if (s1.indexOf("order by") > 0) {
			s1 = sqlStr.substring(0, s1.indexOf("order by"));
		}
		s.append("select count(*)");
		
		if (s1.indexOf("%from%") > 0) {
			s.append(sqlStr.substring(s1.indexOf("%from%")));
		} else {
			s.append(sqlStr.substring(s1.indexOf(" from ")));
		}
		SQL sql = new SQL();
		sql.sql = "" + s;
		return sql;
	}
	
	/**
	 *
	 * 得到增加sql
	 *
	 * @param tm 对象
	 * @return 可执行sql对象
	 * @throws Throwable 异常
	 */
	public SQL getInsertSql(TableModel tm) {
		StringBuffer s = new StringBuffer();
		StringBuffer ts = new StringBuffer();
		tm.put(tm.getMapRemoveID());
		if (tm.getMapData() == null) {
			return null;
		}
		Collection<Object> pers = new ArrayList<Object>();
		s.append("insert into " + tm.getTableName());
		Iterator it = tm.getMapData().keySet().iterator();
		if (it != null) {
			s.append("(");
			ts.append(" values(");
			while (it.hasNext()) {
				String field = (String) it.next();
				s.append(field);
				ts.append("?");
				pers.add(tm.getValue(field));
				if (it.hasNext()) {
					s.append(",");
					ts.append(",");
				}
			}
			s.append(")");
			ts.append(")");
		}
		SQL sql = new SQL();
		sql.sql = "" + s + ts;
		sql.pers = pers;
		return sql;
	}
	
	public SQL getInsertSql(Map dm, String tableName) {
		StringBuffer s = new StringBuffer();
		StringBuffer ts = new StringBuffer();
		if (dm == null) {
			return null;
		}
		Collection<Object> pers = new ArrayList<Object>();
		s.append("insert into " + tableName);
		Iterator it = dm.keySet().iterator();
		if (it != null) {
			s.append("(");
			ts.append(" values(");
			while (it.hasNext()) {
				String field = (String) it.next();
				s.append(dm.get(field));
				ts.append("?");
				pers.add(field);
				if (it.hasNext()) {
					s.append(",");
					ts.append(",");
				}
			}
			s.append(")");
			ts.append(")");
		}
		SQL sql = new SQL();
		sql.sql = "" + s + ts;
		sql.pers = pers;
		return sql;
	}
	
	/**
	 *
	 * 得到更新sql
	 *
	 * @param tm 对象
	 * @return 可执行sql对象
	 * @throws Throwable 异常
	 */
	public SQL getUpdateSql(TableModel tm) {
		StringBuffer s = new StringBuffer();
		tm.put(tm.getMapRemoveID());
		if (tm.getMapData() == null) {
			return null;
		}
		String id = tm.getTableIDFieldName();
		if (tm.getValue(id) == null) {
			return null;
		}
		Collection<Object> pers = new ArrayList<Object>();
		s.append("update " + tm.getTableName());
		Iterator it = tm.getMapData().keySet().iterator();
		if (it != null) {
			s.append(" set ");
			while (it.hasNext()) {
				String field = (String) it.next();
				if (!field.equals(id)) {
					s.append(field + "=?");
					s.append(",");
					pers.add(tm.getValue(field));
				}
			}
			s.deleteCharAt(s.length() - 1);
			s.append(" where " + id + "=?");
			pers.add(tm.getValue(id));
		}
		SQL sql = new SQL();
		sql.sql = "" + s;
		sql.pers = pers;
		return sql;
	}
	
	/**
	 *
	 * 得到删除sql
	 *
	 * @param tm 对象
	 * @return 可执行sql对象
	 * @throws Throwable 异常
	 */
	public SQL getDelSql(TableModel tm) {
		StringBuffer s = new StringBuffer();
		tm.put(tm.getMapRemoveID());
		if (tm.getMapData() == null) {
			return null;
		}
		String id = tm.getTableIDFieldName();
		s.append("delete from " + tm.getTableName());
		s.append(" where " + id + "=?");
		List pers = new ArrayList();
		pers.add(tm.getValue(id));
		SQL sql = new SQL();
		sql.sql = "" + s;
		sql.pers = pers;
		return sql;
	}
	
	/**
	 *
	 * 查询sql
	 *
	 * @param tm 对象
	 * @return 可执行sql对象
	 * @throws Throwable 异常
	 */
	public SQL getQuerySql(TableModel tm) {
		StringBuffer s = new StringBuffer();
		String id = tm.getTableIDFieldName();
		Map data = tm.getMapData();
		if (data != null) {
			Iterator it = data.keySet().iterator();
			if (it != null) {
				s.append("select ");
				while (it.hasNext()) {
					String field = (String) it.next();
					s.append(field);
					if (it.hasNext()) {
						s.append(",");
					}
				}
			}
		} else {
			// s.append("select *");
			//
			// Iterator it = obj.getTable().getFields().values().iterator();
			// if (it != null) {
			// s.append("select ");
			// while (it.hasNext()) {
			// DBField field = (DBField) it.next();
			// if (!field.isLazy()) {
			// s.append(field.getName());
			// s.append(",");
			// }
			// }
			// it = obj.getTable().getClassField().values().iterator();
			// while (it.hasNext()) {
			// DBField field = (DBField) it.next();
			// if (fenet.fap.dmo.ManyToOneField.class != field.getClass() &&
			// (fenet.fap.dmo.ManyToManyField.class != field.getClass())) {
			// s.append(field.getName());
			// s.append(",");
			// }
			// }
			// }
			
		}
		if (s.toString().endsWith(","))
			s.deleteCharAt(s.length() - 1);
		s.append(" from " + tm.getTableName());
		s.append(" where " + id + "=?");
		List pers = new ArrayList();
		pers.add(tm.getValue(id));
		SQL sql = new SQL();
		sql.sql = "" + s;
		sql.pers = pers;
		return sql;
		
	}
	
	/**
	 *
	 * 查询sql
	 *
	 * @param tm 对象
	 * @param scope 范围
	 * @return 可执行sql对象
	 * @throws Throwable 查询异常
	 */
	public SQL getQuerySql(TableModel tm, String scope) {
		StringBuffer s = new StringBuffer();
		if (tm == null) {
			return null;
		}
		// String id=obj.getTable().getId();
		Map data = tm.getMapData();
		if (data != null) {
			Iterator it = data.keySet().iterator();
			if (it != null) {
				s.append("select ");
				while (it.hasNext()) {
					String field = (String) it.next();
					s.append(field);
					if (it.hasNext()) {
						s.append(",");
					}
				}
			}
		} else {
			// s.append("select * ");
			//
			// Iterator it = obj.getTable().getFields().values().iterator();
			// if (it != null) {
			// s.append("select ");
			// while (it.hasNext()) {
			// DBField field = (DBField) it.next();
			// if (!field.isLazy()) {
			// s.append(field.getName());
			// s.append(",");
			// }
			// }
			// it = obj.getTable().getClassField().values().iterator();
			// while (it.hasNext()) {
			// DBField field = (DBField) it.next();
			// if (fenet.fap.dmo.ManyToOneField.class != field.getClass() &&
			// (fenet.fap.dmo.ManyToManyField.class != field.getClass())) {
			// s.append(field.getName());
			// s.append(",");
			// }
			// }
			// }
			
		}
		if (s.toString().endsWith(","))
			s.deleteCharAt(s.length() - 1);
		s.append(" from " + tm.getTableName());
		s.append(" where " + scope);
		SQL sql = new SQL();
		sql.sql = "" + s;
		return sql;
	}
	
	/**
	 * 通过反射 将字段属性替换到Sql语句中(注意大小写)
	 *
	 * @param sourceStr 源串
	 * @param tb 对象
	 * @return 可执行sql对象
	 */
	public SQL AnalyzeSql(String sourceStr, TableModel tb) {
		Collection<Object> pers = new ArrayList<Object>();
		StringBuffer s = new StringBuffer();
		String sourcetemp = sourceStr;
		String splitStr = "@";
		sourcetemp = sourcetemp.replace("[", splitStr);
		String[] SQLtemps = sourcetemp.split(splitStr);
		s.append(SQLtemps[0]);
		for (int i = 1; i < SQLtemps.length; i++) {
			String tt = SQLtemps[i].replace("]", splitStr);
			String[] temps = tt.split(splitStr);
			if (temps.length == 2) {
				pers.add(tb.getValue(temps[0]));
				s.append("?").append(temps[1]);
			} else {
				if (tt.length() == temps[0].length() + 1) {
					pers.add(tb.getValue(temps[0]));
					s.append("?");
				} else
					throw new RuntimeException("");
			}
		}
		SQL sql = new SQL();
		sql.sql = "" + s;
		sql.pers = pers;
		return sql;
	}
	
	public boolean Update(TableModel tm) {
		switch (tm.getCmdKey()) {
			case 0:// select table field for table filed mapcode
				SQL s = this.getInsertSql(tm);
				try {
					pstmt = this.getConn().prepareStatement(s.sql);
					setQueryParams(s.pers);
					tm.setRowNum(pstmt.executeUpdate());
				} catch (Exception e) {
					throw new OIUEException(StatusResult._blocking_errors, tm, e);
				}
				break;
		}
		return true;
	}
	
	public boolean Update(List<TableModel> tm) {
		if (tm.size() == 0)
			return false;
		switch (tm.get(0).getCmdKey()) {
			case 0:// select table field for table filed mapcode
				SQL s = this.getInsertSql(tm.get(0));
				try {
					pstmt = this.getConn().prepareStatement(s.sql);
					for (TableModel tableModel : tm) {
						tableModel.put(tableModel.getMapRemoveID());
						setQueryParams(tableModel.getMapData().values());
						pstmt.addBatch();
					}
					pstmt.executeBatch();
					
				} catch (Exception e) {
					throw new OIUEException(StatusResult._blocking_errors, tm, e);
				}
				break;
		}
		return true;
	}
	
	public boolean UpdateTree(TableModel tm) {
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
					cstmt = this.getConn().prepareCall(sql);
					cstmt.setString(1, tm.getTableName());
					cstmt.setString(2, tm.getValue("autoCode") + "");
					cstmt.setString(3, tm.getValue("name") + "");
					cstmt.setInt(4, Integer.parseInt(tm.getValue("position") + ""));
					cstmt.setString(5, tm.getKeyRemoveStruture());
					cstmt.setString(6, tm.getValueStr());
					cstmt.setString(7, tm.getTableIDFieldName());
					cstmt.registerOutParameter(8, Types.VARCHAR);
					break;
				
				case 1:
					// updateTree(in tableName VARCHAR(32),in filedID int(11),in
					// autoCode VARCHAR(64),in name VARCHAR(64),
					// in nextNodeID int(11),in filedStr text,filedIDName
					// varchar(64))
					sql = "call updateTree(?,?,?,?,?,?,?);";
					cstmt = this.getConn().prepareCall(sql);
					cstmt.setString(1, tm.getTableName());
					cstmt.setInt(2, Integer.parseInt(tm.getValue(tm.getTableIDFieldName()) + ""));
					cstmt.setString(3, tm.getValue("autoCode") + "");
					cstmt.setString(4, tm.getValue("name") + "");
					cstmt.setInt(5, Integer.parseInt(tm.getValue("position") + ""));
					cstmt.setString(6, tm.getKeyValueRemoveStructure());
					cstmt.setString(7, tm.getTableIDFieldName());
					break;
				default:
					break;
			}
			
			boolean hadResults = cstmt.execute();
			while (hadResults) {}
			switch (tm.getCmdKey()) {
				case 0:
					newAutoCode = cstmt.getString(8);
					tm.put("autoCode", newAutoCode);
					break;
			}
		} catch (Throwable e) {}
		return true;
	}
	
	public TableModel QueryObj(TableModel tm) {
		switch (tm.getCmdKey()) {
			default:
				SQL tsql = this.getQuerySql(tm);
				try {
					pstmt = this.getConn().prepareStatement(tsql.sql);
					this.setQueryParams(tsql.pers);
				} catch (Exception e) {
					throw new OIUEException(StatusResult._blocking_errors, tm, e);
				}
		}
		try {
			if (pstmt != null) {
				rs = pstmt.executeQuery();
				while (rs.next()) {
					tm.set(rs);
				}
			}
		} catch (Exception e) {
			throw new OIUEException(StatusResult._blocking_errors, tm, e);
		}
		return tm;
	}
	
	public List Query(TableModel tm) {
		List<TableModel> list = new ArrayList<TableModel>();
		switch (tm.getCmdKey()) {
			default:
				SQL tsql = this.getQuerySql(tm);
				try {
					pstmt = this.getConn().prepareStatement(tsql.sql);
					this.setQueryParams(tsql.pers);
				} catch (Exception e) {
					throw new OIUEException(StatusResult._blocking_errors, tm, e);
				}
		}
		try {
			if (pstmt != null) {
				rs = pstmt.executeQuery();
				while (rs.next()) {
					TableModel models = tm.getClass().newInstance();
					models.set(rs);
					list.add(models);
				}
			}
		} catch (Throwable e) {
			throw new OIUEException(StatusResult._blocking_errors, tm, e);
		}
		return list;
	}
}