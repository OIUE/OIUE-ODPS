package org.oiue.service.odp.dmo.p;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.oiue.service.odp.proxy.ProxyFactory;
import org.oiue.table.structure.TableModel;
import org.oiue.tools.sql.SQL;

/**
 * DB操作的抽象类
 *
 * @author Every{王勤}
 */
@SuppressWarnings({ "unused", "serial" })
public abstract class DMO_ROOT extends JDBC_DMO implements IDMO_ROOT {

	private ProxyFactory proxyFactory = ProxyFactory.getInstance();

	public DMO_ROOT() {
	}

	/**
	 * 获取分页数据总行数
	 *
	 * @param sqlStr
	 *            sql
	 * @param tm
	 *            对象
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
	 * @param tm
	 *            对象
	 * @return 可执行sql对象
	 * @throws Throwable
	 *             异常
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
	 * @param tm
	 *            对象
	 * @return 可执行sql对象
	 * @throws Throwable
	 *             异常
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
	 * @param tm
	 *            对象
	 * @return 可执行sql对象
	 * @throws Throwable
	 *             异常
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
	 * @param tm
	 *            对象
	 * @return 可执行sql对象
	 * @throws Throwable
	 *             异常
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
	 * @param tm
	 *            对象
	 * @param scope
	 *            范围
	 * @return 可执行sql对象
	 * @throws Throwable
	 *             查询异常
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
	 * @param sourceStr
	 *            源串
	 * @param tb
	 *            对象
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
			} else
				throw new RuntimeException("");
		}
		SQL sql = new SQL();
		sql.sql = "" + s;
		sql.pers = pers;
		return sql;
	}

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
		}
		return true;
	}

	public TableModel QueryObj(TableModel tm) throws Throwable {
		switch (tm.getCmdKey()) {
		default:
			SQL tsql = this.getQuerySql(tm);
			pstmt = this.getConn().prepareStatement(tsql.sql);
			this.setQueryParams(tsql.pers);
		}
		try {
			if (pstmt != null) {
				rs = pstmt.executeQuery();
				while (rs.next()) {
					tm.set(rs);
				}
			}
		} catch (Throwable e) {
			throw e;
		}
		return tm;
	}

	public List Query(TableModel tm) throws Throwable {
		List<TableModel> list = new ArrayList<TableModel>();
		switch (tm.getCmdKey()) {
		default:
			SQL tsql = this.getQuerySql(tm);
			pstmt = this.getConn().prepareStatement(tsql.sql);
			this.setQueryParams(tsql.pers);
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
			throw e;
		}
		return list;
	}
}