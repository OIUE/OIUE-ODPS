/**
 * 
 */
package org.oiue.service.odp.res.dmo;

import org.oiue.service.odp.dmo.IDMO;
import org.oiue.table.structure.TableExt;

/**
 * <p>
 * 数据库操作类
 * </p>
 * 操作数据库的接口，提供对数据库资源结构操作的接口方法
 * @author Every{王勤}
 *
 */
public interface IRes extends IDMO {
	/**
	 * 根据表名查询资源中是否定义了该表
	 * @param name 表名
	 * @return 是否有
	 * @throws Throwable 异常
	 */
	public boolean haveTable(String name);
	
	/**
	 * 将数据库中的表格添加到资源中
	 * @param dt 新表 数据库中已存在的表
	 * @return 修改结果
	 * @throws Throwable 异常
	 */
	public boolean updateTable(TableExt dt);
	
	/**
	 * 修改表格
	 * @param dt nTable 新表 数据库中已存在的表
	 * @param ret oTable 旧表 资源中的表
	 * @return 修改结果
	 * @throws Throwable 异常
	 */
	public boolean updateTable(TableExt dt, TableExt ret);
}
