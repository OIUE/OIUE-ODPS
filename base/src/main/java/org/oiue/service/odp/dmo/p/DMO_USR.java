package org.oiue.service.odp.dmo.p;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.oiue.table.structure.TableModel;
import org.oiue.tools.sql.SQL;

@SuppressWarnings({"rawtypes","serial","unchecked"})
public abstract class DMO_USR extends DMO_ROOT implements IDMO_USR {
	/**
	 * 获取分页数据总行数
	 * 
	 * @param sql
	 * @param tm
	 * @return
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
	 * @param obj
	 *            String
	 * @return String
	 * @throws Throwable 
	 * @see fenet.fap.dmo.sql.ISqlQuery#getInsertSql(fenet.fap.dmo.DBObject)
	 */
	public SQL getInsertSql(TableModel tm) throws Throwable {
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
	 * @param obj
	 *            String
	 * @return String
	 * @throws Throwable
	 */
    public SQL getUpdateSql(TableModel tm) throws Throwable {
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
	 * @param obj
	 *            DBObject
	 * @return
	 * @throws Throwable
	 */
    public SQL getDelSql(TableModel tm) throws Throwable {
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
	 * @param obj
	 *            DBObject
	 * @return String
	 * @throws Throwable
	 * @see fenet.fap.dmo.sql.ISqlQuery#getQuerySql(fenet.fap.dmo.DBObject)
	 */
	public SQL getQuerySql(TableModel tm) throws Throwable {
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
//			s.append("select *");
//
//			Iterator it = obj.getTable().getFields().values().iterator();
//			if (it != null) {
//				s.append("select ");
//				while (it.hasNext()) {
//					DBField field = (DBField) it.next();
//					if (!field.isLazy()) {
//						s.append(field.getName());
//						s.append(",");
//					}
//				}
//				it = obj.getTable().getClassField().values().iterator();
//				while (it.hasNext()) {
//					DBField field = (DBField) it.next();
//					if (fenet.fap.dmo.ManyToOneField.class != field.getClass() && (fenet.fap.dmo.ManyToManyField.class != field.getClass())) {
//						s.append(field.getName());
//						s.append(",");
//					}
//				}
//			}
	
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
	 * @param obj
	 *            DBObject
	 * @param scope
	 *            String
	 * @return String
	 * @throws Throwable
	 * @see fenet.fap.dmo.sql.ISqlQuery#getQuerySql(fenet.fap.dmo.DBObject,
	 *      java.lang.String)
	 */
	public SQL getQuerySql(TableModel tm, String scope) throws Throwable {
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
//			s.append("select * ");
//			
//			Iterator it = obj.getTable().getFields().values().iterator();
//			if (it != null) {
//				s.append("select ");
//				while (it.hasNext()) {
//					DBField field = (DBField) it.next();
//					if (!field.isLazy()) {
//						s.append(field.getName());
//						s.append(",");
//					}
//				}
//				it = obj.getTable().getClassField().values().iterator();
//				while (it.hasNext()) {
//					DBField field = (DBField) it.next();
//					if (fenet.fap.dmo.ManyToOneField.class != field.getClass() && (fenet.fap.dmo.ManyToManyField.class != field.getClass())) {
//						s.append(field.getName());
//						s.append(",");
//					}
//				}
//			}
			

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
	 * @param sourceStr
	 * @param tb
	 * @return
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
			} else
				throw new RuntimeException("");
		}
		SQL sql = new SQL();
		sql.sql = "" + s;
		sql.pers = pers;
		return sql;
	}
}
