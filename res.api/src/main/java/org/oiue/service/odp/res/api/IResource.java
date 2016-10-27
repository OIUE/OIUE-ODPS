package org.oiue.service.odp.res.api;

import java.util.List;
import java.util.Map;

import org.oiue.service.odp.bmo.IBMO;
import org.oiue.service.odp.res.dmo.CallBack;
import org.oiue.table.structure.TableExt;
import org.oiue.table.structure.TableModel;

@SuppressWarnings("rawtypes")
public interface IResource extends IBMO {
    /**
     * 根据表名查询资源中是否定义了该表
     * 
     * @param name
     * @return
     * @throws Throwable
     */
    public boolean haveTable(String name) throws Throwable;

    /**
     * 将数据库中的表格添加到资源中
     * 
     * @param dt 新表 数据库中已存在的表
     * @return
     */
    public boolean updateTable(TableExt dt) throws Throwable;

    /**
     * 修改表格
     * 
     * @param nTable 新表 数据库中已存在的表
     * @param oTable 旧表 资源中的表
     * @return
     */
    public boolean updateTable(TableExt dt, TableExt ret) throws Throwable;

    /**
     * 根据操作对象实现插入及修改
     * 
     * @param tablemodel 抽象出来的表格对象
     * @return
     */
    public boolean Update(TableModel tableModel) throws Throwable;

    /**
     * 批量操作，根据操作对象实现插入及修改
     * 
     * @param tm
     * @return
     * @throws Throwable
     */
    public boolean Update(List<TableModel> tm) throws Throwable;

    /**
     * 根据操作对象部分属性获取对象集合
     * 
     * @param tableModel 抽象出来的表格对象
     * @return
     */
    public List Query(TableModel tableModel) throws Throwable;

    /**
     * 根据操作对象部分属性获取对象
     * 
     * @param tableModel 抽象出来的表格对象
     * @return
     */
    public TableModel QueryObj(TableModel tableModel) throws Throwable;

    /**
     * 通过事件名称及数据源类型获取事件属性
     * 
     * @param event_name
     * @param data_type_class
     * @return
     * @throws Throwable
     */
    public TableModel getEvent(String event_name, String data_type_class) throws Throwable;

    /**
     * 根据 服务事件名称、数据源名称、服务名、bundle服务名、bundle名 查找事件并执行
     * 
     * @param event_name
     * @param data_source_name
     * @param service_name
     * @param bundle_service_name
     * @param bundle_name
     * @param map
     * @return
     * @throws Throwable
     */
    public Object callEvent(String event_name, String data_source_name, String service_name, String bundle_service_name, String bundle_name, Map map) throws Throwable;

    /**
     * 
     * @param event_name
     * @param data_source_name
     * @param service_name
     * @param bundle_service_name
     * @param bundle_name
     * @param map
     * @param callBack
     * @return
     * @throws Throwable
     */
    public Object callEvent(String event_name, String data_source_name, String service_name, String bundle_service_name, String bundle_name, Map map, CallBack callBack) throws Throwable;

    /**
     * 
     * @param event_id
     * @param map
     * @return
     * @throws Throwable
     */
    public Object callEvent(String event_id, String data_source_name, Map map) throws Throwable;

    /**
     * 
     * @param event_id
     * @param map
     * @param callBack
     * @return
     * @throws Throwable
     */
    public Object callEvent(String event_id, String data_source_name, Map map, CallBack callBack) throws Throwable;

    /**
     * 
     * @param event_map
     * @param callBack
     * @return
     * @throws Throwable
     */
    public Object callEvent(Map event_map, String data_source_name, CallBack callBack) throws Throwable;
}
