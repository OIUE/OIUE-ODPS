package org.oiue.service.odp.dmo;

import java.util.List;
import java.util.Map;

import org.oiue.service.odp.dmo.p.IDMO_ROOT;
import org.oiue.table.structure.TableModel;
import org.oiue.tools.sql.SQL;

/**
 * 通用数据库操作接口
 *
 * @author Every
 */
@SuppressWarnings("rawtypes")
public interface IDMO_DB extends IDMO_ROOT {
	IDMO_DB clone() throws CloneNotSupportedException;
	
	/**
	 * 获取分页sql语句
	 *
	 * @param sql string
	 * @param tm 查询对象
	 * @return 可执行sql对象
	 */
	public SQL getCutPageSQL(String sql, TableModel tm);
	
	/**
	 * 获取分页数据总行数
	 *
	 * @param sql sql语句
	 * @param rm 对象
	 * @return 可执行sql对象
	 */
	public SQL getRowCountSQL(String sql, TableModel rm);
	
	/**
	 * 获取对象的插入语句
	 *
	 * @param tm 对象
	 * @return 可执行sql对象
	 */
	public SQL getInsertSql(TableModel tm);
	
	/**
	 * 获取对象的插入语句
	 *
	 * @param dm map
	 * @param tableName 表名
	 * @return 可执行sql对象
	 */
	public SQL getInsertSql(Map dm, String tableName);
	
	/**
	 * 获取对象的修改语句
	 *
	 * @param tm 对象
	 * @return 可执行sql对象
	 */
	public SQL getUpdateSql(TableModel tm);
	
	/**
	 * 获取对象操作删除语句
	 *
	 * @param tm 对象
	 * @return 可执行sql对象
	 */
	public SQL getDelSql(TableModel tm);
	
	/**
	 * 获取对象查询语句
	 *
	 * @param tm 对象
	 * @return 可执行sql对象
	 */
	public SQL getQuerySql(TableModel tm);
	
	/**
	 * 获取对象查询语句
	 *
	 * @param tm 对象
	 * @param scope 范围
	 * @return 可执行sql对象
	 */
	public SQL getQuerySql(TableModel tm, String scope);
	
	/**
	 * 通过反射 将字段属性替换到Sql语句中(注意大小写)
	 *
	 * @param sourceStr 源串
	 * @param tb 对象
	 * @return 可执行sql对象
	 */
	public SQL AnalyzeSql(String sourceStr, TableModel tb);
	
	/**
	 * 根据操作对象实现插入及修改
	 *
	 * @param tm TableModel 抽象出来的表格对象
	 * @return 更新结果
	 */
	public boolean Update(TableModel tm);
	
	/**
	 * 根据操作对象实现插入及修改
	 *
	 * @param tm TableModels 抽象出来的表格对象
	 * @return 更新结果
	 */
	public boolean Update(List<TableModel> tm);
	
	/**
	 * 根据操作对象实现Tree插入及修改
	 *
	 * @param tm tablemodel 抽象出来的表格对象
	 * @return 更新结果
	 */
	public boolean UpdateTree(TableModel tm);
	
	/**
	 * 根据操作对象部分属性获取对象集合
	 *
	 * @param tm TableModel 抽象出来的表格对象
	 * @return 查询结果
	 */
	public List Query(TableModel tm);
	
	/**
	 * 根据操作对象部分属性获取对象
	 *
	 * @param tm TableModel 抽象出来的表格对象
	 * @return 查询结果
	 */
	public TableModel QueryObj(TableModel tm);
}
