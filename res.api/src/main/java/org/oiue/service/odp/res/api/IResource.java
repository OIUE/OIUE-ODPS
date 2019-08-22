package org.oiue.service.odp.res.api;

import java.util.List;
import java.util.Map;

import org.oiue.service.odp.bmo.IBMO;
import org.oiue.service.odp.dmo.CallBack;
import org.oiue.service.odp.event.api.EventFilter;
import org.oiue.service.odp.event.api.EventResultFilter;
import org.oiue.table.structure.TableExt;
import org.oiue.table.structure.TableModel;

@SuppressWarnings("rawtypes")
public interface IResource extends IBMO {
	/**
	 * 根据表名查询资源中是否定义了该表
	 *
	 * @param name 表名
	 * @return 是否定义
	 */
	public boolean haveTable(String name);
	
	/**
	 * 将数据库中的表格添加到资源中
	 *
	 * @param dt 新表 数据库中已存在的表
	 * @return 修改成功与否
	 */
	public boolean updateTable(TableExt dt);
	
	/**
	 * 修改表格
	 *
	 * @param dt 新表 数据库中已存在的表
	 * @param ret 旧表 资源中的表
	 * @return 修改成功与否
	 */
	public boolean updateTable(TableExt dt, TableExt ret);
	
	/**
	 * 根据操作对象实现插入及修改
	 *
	 * @param tableModel 抽象出来的表格对象
	 * @return 修改成功与否
	 */
	public boolean Update(TableModel tableModel);
	
	/**
	 * 批量操作，根据操作对象实现插入及修改
	 *
	 * @param tm 修改对象集
	 * @return 修改成功与否
	 */
	public boolean Update(List<TableModel> tm);
	
	/**
	 * 根据操作对象部分属性获取对象集合
	 *
	 * @param tableModel 抽象出来的表格对象
	 * @return 查询结果
	 */
	public List Query(TableModel tableModel);
	
	/**
	 * 根据操作对象部分属性获取对象
	 *
	 * @param tableModel 抽象出来的表格对象
	 * @return 查询结果
	 */
	public TableModel QueryObj(TableModel tableModel);
	
	/**
	 * 通过事件名称及数据源类型获取事件属性
	 *
	 * @param event_name 事件名称
	 * @param data_type_class 数据源类型
	 * @return 结果
	 */
	public TableModel getEvent(String event_name, String data_type_class);
	
	public Map getEventByIDType(String event_id, String data_type_name);
	
	public Map getEventByIDName(String event_id, String data_source_name);
	
	/**
	 * 构建事件
	 * @param event_id 事件id
	 * @param data_source_name 数据源名
	 * @param map 参数
	 * @return 结果
	 */
	public <T> T buildEvent(String event_id, String data_source_name, Map map);
	
	/**
	 * 调用事件
	 * @param event_id 事件id
	 * @param data_source_name 数据源名
	 * @param map 参数
	 * @return 结果
	 */
	public <T> T callEvent(String event_id, String data_source_name, Map map);
	
	/**
	 * 调用事件
	 * @param event_id 时间id
	 * @param data_source_name 数据源名
	 * @param map 参数
	 * @param callBack 回调
	 * @return 结果
	 */
	public <T> T callEvent(String event_id, String data_source_name, Map map, CallBack callBack);
	
	/**
	 * 执行事件
	 * @param event 事件信息
	 * @param data_source_name 数据源名称
	 * @param data 参数
	 * @param callBack 回调
	 * @return 返回结果
	 */
	public <T> T executeEvent(Map event, String data_source_name, Map data, CallBack callBack);
	
	public boolean registerEventFilter(String requestEvent, EventFilter eventFilter, int index);
	
	public void unregisterEventFilter(String requestEvent);
	
	public boolean registerEventResultFilter(String requestEvent, EventResultFilter eventResultFilter, int index);
	
	public void unregisterEventResultFilter(String requestEvent);
	
	public void unregisterAllEventFilter();
}
