package org.oiue.service.odp.dmo.p;
import java.util.Map;

import org.oiue.table.structure.TableModel;
import org.oiue.tools.sql.SQL;

/**
 * 通用数据库操作接口
 * 
 * @author Every
 */
@SuppressWarnings("rawtypes")
public interface IDMO_DB extends IDMO_USR {
	/**
	 * 获取分页sql语句
	 * 
	 * @param sql
	 * @param tm
	 * @return
	 */
	public SQL getCutPageSQL(String sql, TableModel tm);
	/**
	 * 获取分页数据总行数
	 * 
	 * @param sql
	 * @param rm
	 * @return
	 */
	public SQL getRowCountSQL(String sql, TableModel rm);

	/**
	 * 获取对象的插入语句
	 * 
	 * @param tm
	 * @return
	 */
	public SQL getInsertSql(TableModel tm) throws Throwable;

	/**
	 * 获取对象的插入语句
	 * 
	 * @param dm
	 * @param tableName
	 * @return
	 */
	public SQL getInsertSql(Map dm, String tableName);

	/**
	 * 获取对象的修改语句
	 * 
	 * @param tm
	 * @return
	 */
	public SQL getUpdateSql(TableModel tm) throws Throwable;

	/**
	 * 获取对象操作删除语句
	 * 
	 * @param tm
	 * @return
	 */
	public SQL getDelSql(TableModel tm) throws Throwable;

	/**
	 * 获取对象查询语句
	 * 
	 * @param tm
	 * @return
	 */
	public SQL getQuerySql(TableModel tm) throws Throwable;

	/**
	 * 获取对象查询语句
	 * 
	 * @param tm
	 * @param scope
	 * @return
	 */
	public SQL getQuerySql(TableModel tm, String scope) throws Throwable;
}
