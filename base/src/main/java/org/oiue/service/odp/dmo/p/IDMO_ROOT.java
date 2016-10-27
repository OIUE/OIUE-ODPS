package org.oiue.service.odp.dmo.p;

import java.io.Serializable;
import java.util.List;

import org.oiue.table.structure.TableModel;

/**
 * DB操作调用跟接口
 * 
 * @author Every{王勤}
 *
 */
@SuppressWarnings({ "rawtypes" })
public interface IDMO_ROOT extends Serializable {
	/**
	 * Method 复制对象
	 */
	public Object deepCopy() throws Throwable;

	/**
	 * 根据操作对象实现插入及修改
	 * 
	 * @param tablemodel
	 *            抽象出来的表格对象
	 * @return
	 */
	public boolean Update(TableModel tm) throws Throwable;

	/**
	 * 根据操作对象实现插入及修改
	 * 
	 * @param tablemodel
	 *            抽象出来的表格对象
	 * @return
	 */
	public boolean Update(List<TableModel> tm) throws Throwable;

	/**
	 * 根据操作对象实现Tree插入及修改
	 * 
	 * @param tablemodel
	 *            抽象出来的表格对象
	 * @return
	 */
	public boolean UpdateTree(TableModel tm) throws Throwable;

	/**
	 * 根据操作对象部分属性获取对象集合
	 * 
	 * @param TableModel
	 *            抽象出来的表格对象
	 * @return
	 */
	public List Query(TableModel tm) throws Throwable;

	/**
	 * 根据操作对象部分属性获取对象
	 * 
	 * @param TableModel
	 *            抽象出来的表格对象
	 * @return
	 */
	public TableModel QueryObj(TableModel tm) throws Throwable;

}
