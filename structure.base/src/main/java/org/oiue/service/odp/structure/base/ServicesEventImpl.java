package org.oiue.service.odp.structure.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.oiue.service.cache.CacheServiceManager;
import org.oiue.service.log.LogService;
import org.oiue.service.log.Logger;
import org.oiue.service.odp.base.FactoryService;
import org.oiue.service.odp.bmo.BMO;
import org.oiue.service.odp.event.api.EventField;
import org.oiue.service.odp.res.api.IResource;
import org.oiue.service.odp.structure.api.IServicesEvent;
import org.oiue.service.system.analyzer.AnalyzerService;
import org.oiue.service.system.analyzer.TimeLogger;
import org.oiue.table.structure.TableModel;
import org.oiue.tools.StatusResult;
import org.oiue.tools.exception.OIUEException;
import org.oiue.tools.list.ListUtil;
import org.oiue.tools.map.MapUtil;
import org.oiue.tools.string.StringUtil;

@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
public class ServicesEventImpl extends BMO implements IServicesEvent {
	private static final long serialVersionUID = 1L;
	private static Map<String, Map<String, TableModel>> service_events = new Hashtable<String, Map<String, TableModel>>();
	private static Logger logger;
	private static TimeLogger tLogger;
	private static LogService logService;
	private static CacheServiceManager cacheService;
	protected static FactoryService factoryService;
	
	public ServicesEventImpl(LogService logService, AnalyzerService analyzerService, CacheServiceManager cacheService) {
		logger = logService.getLogger(this.getClass());
		tLogger = analyzerService.getLogger(this.getClass());
		
		ServicesEventImpl.cacheService = cacheService;
		ServicesEventImpl.logService = logService;
	}
	public static String _system_colnum = "system_id";
	
	public ServicesEventImpl() {}
	
	public void updated(Map<String, ?> props) {
		
	}
	
	// {entity_id,user_id,fields:{precision,scale,name,alias,entity_column_id,type,},entity_desc,query,expression_query,insert,expression_insert,update,expression_update,delete,expression_delete}
	@Override
	public void insertServiceEvent(Map data) {
		IResource iresource;
		String data_source_name = null;
		
		String entity_id = MapUtil.getString(data, "entity_id");
		String table_name = MapUtil.getString(data, "table_name");
		String user_id = MapUtil.getString(data, "user_id");
		
		List<Map> fields = (List) data.get("fields");
		List<String> allFields = new ArrayList<>();
		List<String> allPkFields = new ArrayList<>();
		List<String> otherFields = new ArrayList<>();
		List<String> insertValues = new ArrayList<>();
		for (Map field : fields) {
			String name = MapUtil.getString(field, "column_name");
			String component_instance_id =MapUtil.getString(field, "component_instance_id");
			// String alias = MapUtil.getString(field, "alias");
			allFields.add(name);
			insertValues.add(_system_colnum.equals(name) ? "uuid_generate_v4()" : "fm_managed_ui_point".equals(component_instance_id)?"ST_SetSRID(st_geomfromgeojson(?),4326)":
				"fm_managed_ui_line".equals(component_instance_id)?"ST_SetSRID(st_geomfromgeojson(?),4326)":
				"fm_managed_ui_polygon".equals(component_instance_id)?"ST_SetSRID(st_geomfromgeojson(?),4326)":"?");//ST_SetSRID(st_geomfromgeojson(?),4326)
			
			if (MapUtil.getBoolean(field, "ispk", false)) {
				allPkFields.add(name);
			} else {
				otherFields.add(name);
			}
		}
		
		String allFieldstr = ListUtil.ListJoin(allFields, ",");
		String allPkFieldstr = ListUtil.ListJoin(allPkFields, ",");
		String allPkFieldstrv = ListUtil.ListJoin(allPkFields, "= ? ,") + "=? ";
		String query_se_id = UUID.randomUUID().toString().replaceAll("-", "");
		String insert_se_id = UUID.randomUUID().toString().replaceAll("-", "");
		String update_se_id = UUID.randomUUID().toString().replaceAll("-", "");
		String delete_se_id = UUID.randomUUID().toString().replaceAll("-", "");
		
		// service_id ,name ,description ,type ,content ,expression,user_id
		Map selectMap = new HashMap<>();
		selectMap.put("service_id", "fm_system_service_execute");
		selectMap.put("user_id", user_id);
		selectMap.put("name", "execute");
		selectMap.put("desc", MapUtil.getString(data, "entity_desc", entity_id));
		selectMap.put("type", "query");
		// selectMap.put("rule", "intelligent");
		// selectMap.put("expression", MapUtil.getString(data, "expression_query", "{\"conjunction\":\"and\",\"filters\":[]}"));
		selectMap.put("content", MapUtil.getString(data, "query", "select " + allFieldstr + " from " + table_name));
		iresource = this.getIBMO(IResource.class.getName());
		Map selecto = iresource.callEvent("fm_system_add_services_event", data_source_name, selectMap);// insert service event query
		query_se_id= MapUtil.getString(selecto,"service_event_id");
		
		// service_event_parameters_id,entity_id,service_event_id
		Map eventEntity = new HashMap<>();
		eventEntity.put("service_event_parameters_id",query_se_id);
		eventEntity.put("entity_id", entity_id);
		eventEntity.put("service_event_id", query_se_id);
		eventEntity.put("operation_type", "query");
		eventEntity.put("entity_type", "system_ds");
		iresource = this.getIBMO(IResource.class.getName());
		iresource.callEvent("38b6c070-0133-470f-ad60-7344b31a1f34", data_source_name, eventEntity);// insert service event entity
		
		Map insertMap = new HashMap<>();
		insertMap.put("service_id", "fm_system_service_execute");
		insertMap.put("user_id", user_id);
		insertMap.put("name", "execute");
		insertMap.put("desc", MapUtil.getString(data, "entity_desc", entity_id) + "_insert");
		insertMap.put("type", "insert");
		insertMap.put("rule", "");
		insertMap.put("content", MapUtil.getString(data, "insert", "insert into " + table_name + "(" + allFieldstr + ") values(" + ListUtil.ListJoin(insertValues, ",") + ")" + " returning *"));
		insertMap.put("expression", MapUtil.getString(data, "expression_insert", allFieldstr.replace(","+_system_colnum, "").replace(_system_colnum+",", "")).replace("\"", ""));
		iresource = this.getIBMO(IResource.class.getName());
		Map inserto = iresource.callEvent("fm_system_add_services_event", data_source_name, insertMap);// insert service event insert
		insert_se_id = MapUtil.getString(inserto,"service_event_id");
		
		eventEntity.put("service_event_parameters_id", insert_se_id);
		eventEntity.put("service_event_id", insert_se_id);
		eventEntity.put("operation_type", "insert");
		iresource = this.getIBMO(IResource.class.getName());
		iresource.callEvent("38b6c070-0133-470f-ad60-7344b31a1f34", data_source_name, eventEntity);// insert service event entity
		
		String delete_service_event_id = null;
		if (allPkFields.size() > 0 || data.containsKey("delete")) {
			Map deleteMap = new HashMap<>();
			deleteMap.put("service_id", "fm_system_service_execute");
			deleteMap.put("user_id", user_id);
			deleteMap.put("name", "execute");
			deleteMap.put("desc", MapUtil.getString(data, "entity_desc", entity_id) + "_delete");
			deleteMap.put("type", "delete");
			deleteMap.put("rule", "");
			deleteMap.put("content", MapUtil.getString(data, "delete", "delete from " + table_name + " where " + allPkFieldstrv + " returning *"));
			deleteMap.put("expression", MapUtil.getString(data, "expression_delete", allPkFieldstr).replace("\"", ""));
			iresource = this.getIBMO(IResource.class.getName());
			Map deleteo = iresource.callEvent("fm_system_add_services_event", data_source_name, deleteMap);// insert service event delete
			delete_service_event_id = MapUtil.getString(deleteo, "service_event_id");
			
			eventEntity.put("service_event_parameters_id", deleteo.get("service_event_id"));
			eventEntity.put("service_event_id", deleteo.get("service_event_id"));
			eventEntity.put("operation_type", "delete");
			iresource = this.getIBMO(IResource.class.getName());
			iresource.callEvent("38b6c070-0133-470f-ad60-7344b31a1f34", data_source_name, eventEntity);// insert service event entity
		}
		
		String update_service_event_id = null;
		if (allPkFields.size() > 0 || data.containsKey("update")) {
			Map updateMap = new HashMap<>();
			updateMap.put("service_id", "fm_system_service_execute");
			updateMap.put("user_id", user_id);
			updateMap.put("name", "execute");
			updateMap.put("desc", MapUtil.getString(data, "entity_desc", entity_id) + "_update");
			updateMap.put("type", "update");
			updateMap.put("rule", "");
			updateMap.put("content", MapUtil.getString(data, "update", "update " + table_name + " set " + ListUtil.ListJoin(otherFields, "= ? ,") + "=?" + " where " + allPkFieldstrv + " returning *"));
			updateMap.put("expression", MapUtil.getString(data, "expression_update", ListUtil.ListJoin(otherFields, ",") + "," + allPkFieldstr).replace("\"", ""));
			iresource = this.getIBMO(IResource.class.getName());
			Map updateo = iresource.callEvent("fm_system_add_services_event", data_source_name, updateMap);// insert service event update
			update_service_event_id = MapUtil.getString(updateo, "service_event_id");
			
			eventEntity.put("service_event_parameters_id", updateo.get("service_event_id"));
			eventEntity.put("service_event_id", updateo.get("service_event_id"));
			eventEntity.put("operation_type", "update");
			iresource = this.getIBMO(IResource.class.getName());
			iresource.callEvent("38b6c070-0133-470f-ad60-7344b31a1f34", data_source_name, eventEntity);// insert service event entity
		}
		
		// service_event_id,entity_id,alias,entity_column_id,desc,data_type_id,precision,scale,sort,config_type,user_id,service_event_parameters_id,component_instance_id
		Map insertConfigMap = new HashMap<>();
		insertConfigMap.put("user_id", user_id);
		insertConfigMap.put("entity_id", entity_id);
		int sort = 1;
		boolean addOperation = MapUtil.getBoolean(data, "addOperation", true);
		for (Map field : fields) {// precision,scale,name,alias,entity_column_id,type,ispk
			insertConfigMap.put("precision", field.get("precision"));
			insertConfigMap.put("scale", field.get("scale"));
			insertConfigMap.put("alias", MapUtil.getString(field, "alias", field.get("entity_column_id") + ""));
			insertConfigMap.put("entity_column_id", field.get("entity_column_id"));
			insertConfigMap.put("desc", MapUtil.getString(field, "column_desc", field.get("column_name") + ""));
			insertConfigMap.put("data_type_id", MapUtil.getString(field, "data_type_id", field.get("type") + ""));
			insertConfigMap.put("sort", sort++);
			insertConfigMap.put("null_able", field.get("null_able"));
			insertConfigMap.put("status", MapUtil.getInt(field, "status",1));
			
			insertConfigMap.put("component_instance_id", null);
			insertConfigMap.put("config_type", "insert");
			insertConfigMap.put("service_event_id", ((Map) inserto).get("service_event_id"));
			insertConfigMap.put("component_instance_id", MapUtil.getString(field, "component_instance_id"));
			iresource = this.getIBMO(IResource.class.getName());
			if (!_system_colnum.equals(field.get("column_name")))
				iresource.callEvent("e535fb44-4d1a-46f9-907f-9aa931c8502f", data_source_name, insertConfigMap);// insert service event config insert field
				
			if (MapUtil.getBoolean(field, "ispk", false)) {
				if (addOperation) {
					addOperation = false;
					insertConfigMap.put("desc", "添加");
					insertConfigMap.put("component_instance_id", "fm_lt_operation_insert");
					insertConfigMap.put("config_type", "operation");
					insertConfigMap.put("service_event_id", ((Map) inserto).get("service_event_id"));
					insertConfigMap.put("service_event_parameters_id", ((Map) inserto).get("service_event_id"));
					iresource = this.getIBMO(IResource.class.getName());
					iresource.callEvent("e535fb44-4d1a-46f9-907f-9aa931c8502f", data_source_name, insertConfigMap);// insert service event config insert
					
					if (!StringUtil.isEmptys(delete_service_event_id)) {
						insertConfigMap.put("desc", "删除");
						insertConfigMap.put("component_instance_id", "fm_lt_operation_delete");
						insertConfigMap.put("config_type", "operation");
						insertConfigMap.put("service_event_id", delete_service_event_id);
						insertConfigMap.put("service_event_parameters_id", delete_service_event_id);
						iresource = this.getIBMO(IResource.class.getName());
						iresource.callEvent("e535fb44-4d1a-46f9-907f-9aa931c8502f", data_source_name, insertConfigMap);// insert service event config delete
					}
					
					if (!StringUtil.isEmptys(update_service_event_id)) {
						insertConfigMap.put("desc", "修改");
						insertConfigMap.put("component_instance_id", "fm_lt_operation_update");
						insertConfigMap.put("config_type", "operation");
						insertConfigMap.put("service_event_id", update_service_event_id);
						insertConfigMap.put("service_event_parameters_id", update_service_event_id);
						iresource = this.getIBMO(IResource.class.getName());
						iresource.callEvent("e535fb44-4d1a-46f9-907f-9aa931c8502f", data_source_name, insertConfigMap);// insert service event config update
					}
					
					insertConfigMap.remove("service_event_id");
					insertConfigMap.remove("service_event_parameters_id");
				}
				
				insertConfigMap.put("desc", MapUtil.getString(field, "desc", field.get("name") + ""));
				insertConfigMap.put("component_instance_id", null);
				insertConfigMap.put("service_event_id", delete_service_event_id);
				insertConfigMap.put("config_type", "delete");
				iresource = this.getIBMO(IResource.class.getName());
				iresource.callEvent("e535fb44-4d1a-46f9-907f-9aa931c8502f", data_source_name, insertConfigMap);// insert service event config delete
				
				insertConfigMap.put("service_event_id", update_service_event_id);
				insertConfigMap.put("config_type", "updateKey");
				insertConfigMap.put("component_instance_id", MapUtil.getString(field, "component_instance_id"));
				iresource = this.getIBMO(IResource.class.getName());
				iresource.callEvent("e535fb44-4d1a-46f9-907f-9aa931c8502f", data_source_name, insertConfigMap);// insert service event config update
			} else {
				insertConfigMap.put("config_type", "update");
				insertConfigMap.put("component_instance_id", MapUtil.getString(field, "component_instance_id"));
				insertConfigMap.put("service_event_id", update_service_event_id);
				iresource = this.getIBMO(IResource.class.getName());
				if (!_system_colnum.equals(field.get("column_name")))
					iresource.callEvent("e535fb44-4d1a-46f9-907f-9aa931c8502f", data_source_name, insertConfigMap);// insert service event config update
					
				insertConfigMap.put("service_event_id", selecto.get("service_event_id"));
				insertConfigMap.put("config_type", "result");
				iresource = this.getIBMO(IResource.class.getName());
				iresource.callEvent("e535fb44-4d1a-46f9-907f-9aa931c8502f", data_source_name, insertConfigMap);// insert service event config result
				
				insertConfigMap.put("config_type", "filter");
				iresource = this.getIBMO(IResource.class.getName());
				if (!_system_colnum.equals(field.get("column_name")))
					iresource.callEvent("e535fb44-4d1a-46f9-907f-9aa931c8502f", data_source_name, insertConfigMap);// insert service event config filter
			}
		}
		if (addOperation) {
			addOperation = false;
			insertConfigMap.put("desc", "添加");
			insertConfigMap.put("component_instance_id", "fm_lt_operation_insert");
			insertConfigMap.put("config_type", "operation");
			insertConfigMap.put("service_event_id", ((Map) inserto).get("service_event_id"));
			insertConfigMap.put("service_event_id", ((Map) inserto).get("service_event_id"));
			insertConfigMap.put("service_event_parameters_id", ((Map) inserto).get("service_event_id"));
			iresource = this.getIBMO(IResource.class.getName());
			iresource.callEvent("e535fb44-4d1a-46f9-907f-9aa931c8502f", data_source_name, insertConfigMap);// insert service event config insert
			
			if (!StringUtil.isEmptys(delete_service_event_id)) {
				insertConfigMap.put("desc", "删除");
				insertConfigMap.put("component_instance_id", "fm_lt_operation_delete");
				insertConfigMap.put("config_type", "operation");
				insertConfigMap.put("service_event_id", delete_service_event_id);
				insertConfigMap.put("service_event_id", delete_service_event_id);
				insertConfigMap.put("service_event_parameters_id", delete_service_event_id);
				iresource = this.getIBMO(IResource.class.getName());
				iresource.callEvent("e535fb44-4d1a-46f9-907f-9aa931c8502f", data_source_name, insertConfigMap);// insert service event config delete
			}
			
			if (!StringUtil.isEmptys(update_service_event_id)) {
				insertConfigMap.put("desc", "修改");
				insertConfigMap.put("component_instance_id", "fm_lt_operation_update");
				insertConfigMap.put("config_type", "operation");
				insertConfigMap.put("service_event_id", update_service_event_id);
				insertConfigMap.put("service_event_id", update_service_event_id);
				insertConfigMap.put("service_event_parameters_id", update_service_event_id);
				iresource = this.getIBMO(IResource.class.getName());
				iresource.callEvent("e535fb44-4d1a-46f9-907f-9aa931c8502f", data_source_name, insertConfigMap);// insert service event config update
			}
		}
	}
	
	// {entity_id,user_id,tablename,entity_desc,insert,expression_insert,update,expression_update}
	public void updateServiceEvent(Map data) {
		IResource iresource;
		String data_source_name = null;
		
		iresource = this.getIBMO(IResource.class.getName());
//		if (!data.containsKey("entity_column_id"))
//			data.put("entity_column_id", MapUtil.getString(data, "x"));
//		Object entity = iresource.callEvent("fa8f9b71-34ca-4d40-8b74-a03cf4c1f3d5", data_source_name, data);// insert service event query
//		if (entity == null || !(entity instanceof Map)) {
//			throw new RuntimeException();
//		}
//		Map rentity = (Map) entity;
		String entity_id = MapUtil.getString(data, "entity_id");
		String table_name = MapUtil.getString(data, "table_name");
		String user_id = MapUtil.getString(data, "user_id");
		
		iresource = this.getIBMO(IResource.class.getName());
		List<Map> fields = (List<Map>) iresource.callEvent("c51a1f14-0d47-4a64-b476-2fb1286b4d2a", data_source_name, data);// insert service event query
		List<String> allInsertFields = new ArrayList<>();
		List<String> allPkFields = new ArrayList<>();
		List<String> otherFields = new ArrayList<>();
		List<String> insertValues = new ArrayList<>();
		List<String> insertExpression = new ArrayList<>();
		
		List<String> geoFields = new ArrayList<>();
		for (Map field : fields) {
			String name = MapUtil.getString(field, "column_name");
			String data_type = MapUtil.getString(field, "data_type");
			allInsertFields.add(name);
			if ("POINT".equals(data_type) || "LINESTRING".equals(data_type) || "POLYGON".equals(data_type) || "GEOMETRY".equals(data_type)) {
				insertValues.add("ST_SetSRID(st_geomfromgeojson(?),4326)");
				// allSelectFields.add("st_asgeojson(" + name + ") as " + name);
				insertExpression.add(name);
				geoFields.add(name);
			} else if(_system_colnum.equals(name)||MapUtil.getInt(field, "primary_key") == 1){
				insertValues.add("uuid_generate_v4()");
				allPkFields.add(name);
			}else {
				insertValues.add("?");
				insertExpression.add(name);
				otherFields.add(name);
			}
		}
		
		String allInsertFieldstr = ListUtil.ListJoin(allInsertFields, ",");
		String allPkFieldstr = ListUtil.ListJoin(allPkFields, ",");
		String allPkFieldstrv = ListUtil.ListJoin(allPkFields, "= ? ,") + "=? ";
		
		// service_id ,name ,description ,type ,content ,expression,user_id
		Map selectMap = new HashMap<>();
		selectMap.put("entity_id", entity_id);
		selectMap.put("user_id", user_id);
		selectMap.put("description", MapUtil.getString(data, "entity_desc", entity_id));
		selectMap.put("operation_type", "query");
		// selectMap.put("rule", "intelligent");
		// selectMap.put("expression", MapUtil.getString(data, "expression_query", "{\"conjunction\":\"and\",\"filters\":[]}"));
		selectMap.put("content", MapUtil.getString(data, "query", "select " + allInsertFieldstr + " from " + table_name));
		iresource = this.getIBMO(IResource.class.getName());
		iresource.callEvent("2cea7527-2e31-4e98-9d2e-6feb1c8f15b0", data_source_name, selectMap);// update service event query
		
		Map insertMap = new HashMap<>();
		insertMap.put("entity_id", entity_id);
		insertMap.put("user_id", user_id);
		insertMap.put("operation_type", "insert");
		insertMap.put("content", MapUtil.getString(data, "insert", "insert into " + table_name + "(" + allInsertFieldstr + ") values(" + ListUtil.ListJoin(insertValues, ",") + ")" + " returning *"));
		insertMap.put("expression", MapUtil.getString(data, "expression_insert", ListUtil.ListJoin(insertExpression,",")).replace("\"", ""));
		iresource = this.getIBMO(IResource.class.getName());
		iresource.callEvent("2cea7527-2e31-4e98-9d2e-6feb1c8f15b0", data_source_name, insertMap);// update service event insert
		
		Map updateMap = new HashMap<>();
		updateMap.put("entity_id", entity_id);
		updateMap.put("user_id", user_id);
		updateMap.put("operation_type", "update");
		updateMap.put("content", MapUtil.getString(data, "update", "update " + table_name + " set " + ListUtil.ListJoin(otherFields, "= ? ,") + "=? ," + ListUtil.ListJoin(geoFields, "= ST_SetSRID(st_geomfromgeojson(?),4326) ,") + "= ST_SetSRID(st_geomfromgeojson(?),4326)  where " + allPkFieldstrv + " returning *"));
		updateMap.put("expression", MapUtil.getString(data, "expression_update", ListUtil.ListJoin(otherFields, ",") + "," + ListUtil.ListJoin(geoFields, ",") + "," + allPkFieldstr).replace("\"", ""));
		iresource = this.getIBMO(IResource.class.getName());
		iresource.callEvent("2cea7527-2e31-4e98-9d2e-6feb1c8f15b0", data_source_name, updateMap);// update service event update
	}
	
	@Override
	public void selectEventInfo(Map data) {
		
	}
	
	@Override
	public void createServiceEvent(Map data) {
		
		IResource iresource;
		String data_source_name = null;
		String type = MapUtil.getString(data, "type");
		String user_id = MapUtil.getString(data, "user_id");
		String entity_id = MapUtil.getString(data, "entity_id");
		
		List<Map> fields = (List) data.get("fields");
		
		// service_id ,name ,description ,type ,content ,expression,user_id
		data.put("service_id", "fm_system_service_execute");
		// data.put("user_id", user_id);
		// data.put("name", entity_id + "_select");
		data.put("desc", MapUtil.getString(data, "entity_desc", entity_id));
		// data.put("description", MapUtil.getString(data, "entity_desc", entity_id));
		// data.put("type", "query");
		// data.put("rule", "intelligent");
		// data.put("expression", MapUtil.getString(data, "expression_query", "{\"conjunction\":\"and\",\"filters\":[]}"));
		// data.put("content", MapUtil.getString(data, "query", "select " + allFieldstr + " from " + table_name));
		iresource = this.getIBMO(IResource.class.getName());
		Map selecto = iresource.callEvent("fm_system_add_services_event", data_source_name, data);// insert service event query
		
		// service_event_parameters_id,entity_id,service_event_id
		Map eventEntity = new HashMap<>();
		eventEntity.put("entity_id", entity_id);
		eventEntity.put("operation_type", data.get("type"));
		eventEntity.put("entity_type", "user_ds");
		eventEntity.put("service_event_id", selecto.get("service_event_id"));
		eventEntity.put("service_event_parameters_id", selecto.get("service_event_id"));
		iresource = this.getIBMO(IResource.class.getName());
		iresource.callEvent("38b6c070-0133-470f-ad60-7344b31a1f34", data_source_name, eventEntity);// insert service event entity
		
		Map insertConfigMap = new HashMap<>();
		insertConfigMap.put("user_id", user_id);
		insertConfigMap.put("entity_id", entity_id);
		int sort = 1;
		boolean addOperation = true;
		for (Map field : fields) {// precision,scale,name,alias,entity_column_id,type,ispk
			insertConfigMap.put("precision", field.get("precision"));
			insertConfigMap.put("scale", field.get("scale"));
			insertConfigMap.put("alias", MapUtil.getString(field, "alias", field.get("entity_column_id") + ""));
			insertConfigMap.put("entity_column_id", field.get("entity_column_id"));
			insertConfigMap.put("desc", MapUtil.getString(field, "desc", field.get("name") + ""));
			insertConfigMap.put("data_type_id", MapUtil.getString(field, "data_type_id", field.get("type") + ""));
			insertConfigMap.put("sort", sort++);
			insertConfigMap.put("null_able", field.get("null_able"));
			
			insertConfigMap.put("component_instance_id", null);
			insertConfigMap.put("config_type", "insert");
			insertConfigMap.put("service_event_id", ((Map) selecto).get("service_event_id"));
			iresource = this.getIBMO(IResource.class.getName());
			iresource.callEvent("e535fb44-4d1a-46f9-907f-9aa931c8502f", data_source_name, insertConfigMap);// insert service event config insert field
		}
	}
	
	@Override
	public Object inorUpServiceEvent(Map data, Map event, String tokenid) {
		String service_event_id = MapUtil.getString(data, "service_event_id");
		String type = MapUtil.getString(data, "type");
		String user_id = MapUtil.getString(data, "user_id");
		String table_name = MapUtil.getString(data, "entity_id");
		List<Map> service_event_config = (List<Map>) data.get("service_event_config");
		Map service_event_parameter = null;
		try {
			List<Map> service_event_parameters = (List<Map>) data.get("service_event_parameters");
			if (service_event_parameters.size() > 0)
				service_event_parameter = service_event_parameters.get(0);
		} catch (Exception e) {
			throw new OIUEException(StatusResult._data_error, "参数错误！", null);
		}
		
		IResource iresource;
		String data_source_name = null;
		if (StringUtil.isEmptys(type)) {
			throw new OIUEException(StatusResult._data_error, "请选择API类型！", null);
		}
		data.put("service_id", MapUtil.getString(data, "service_id", "fm_system_service_execute"));
		int sort = 0;
		switch (type) {
			case "delete":
				if (StringUtil.isEmptys(service_event_id)) {
					service_event_id = "lt_" + UUID.randomUUID().toString().replaceAll("-", "");
					data.put("name", MapUtil.getString(data, "name", "execute"));
					data.put("service_event_id", service_event_id);
					iresource = this.getIBMO(IResource.class.getName());// service_id,name,desc,type ,rule,content ,expression,user_id,service_event_id
					iresource.callEvent("fm_system_add_services_event", data_source_name, data);// insert service event query
				} else {
					iresource = this.getIBMO(IResource.class.getName());// service_event_id
					iresource.callEvent("a35bd7f3-45c4-47e8-9a60-9fd6753c991b", data_source_name, data);// delete service event config
					iresource = this.getIBMO(IResource.class.getName());// service_event_id
					iresource.callEvent("f2efed89-7cb4-45b5-932a-020248f1aaff", data_source_name, data);// delete service event entity
				}
				
				StringBuffer td1 = new StringBuffer("delete from ");
				td1.append(table_name).append(" where ");
				
				List dpers = new ArrayList<>();
				for (Map cfg : service_event_config) {
					String entity_column_id = MapUtil.getString(cfg, "entity_column_id");
					String name = MapUtil.getString(cfg, "name");
					String alias = MapUtil.getString(cfg, "alias");
					String desc = MapUtil.getString(cfg, "desc");
					String remark = MapUtil.getString(cfg, "remark");
					String config_type = MapUtil.getString(cfg, "config_type");
					int status = MapUtil.getInt(cfg, "status", 1);
					cfg.put("status", status);
					cfg.put("sort", sort++);
					cfg.put("user_id", user_id);
					
					cfg.put("service_event_id", service_event_id);
					cfg.put("null_able", MapUtil.getInt(cfg, "null_able", 1));
					cfg.put("bundle_service_id", MapUtil.getString(cfg, "bundle_service_id", "org.oiue.service.event.execute.EventExecuteService"));
					
					if ("delete".equals(config_type)) {
						td1.append(name).append("=?,");
						dpers.add(alias);
						iresource = this.getIBMO(IResource.class.getName());// bundle_service_id,service_event_id,alias,desc,remark,status,content,sort,config_type,null_able,update_user_id,service_event_parameters_id,component_instance_id,service_event_id,entity_column_id
						iresource.callEvent("82b0cec4-1e1d-49eb-8bbd-79bf9c6e2061", data_source_name, cfg);// insert service event query
					}
				}
				td1.deleteCharAt(td1.length() - 1);
				data.put("content", td1.toString());
				data.put("expression", ListUtil.ListJoin(dpers, ",").replace("\"", ""));
				
				iresource = this.getIBMO(IResource.class.getName());// service_id,name,desc,type ,rule,content ,expression,user_id,service_event_id
				iresource.callEvent("fm_system_add_services_event", data_source_name, data);// insert service event query
				
				iresource = this.getIBMO(IResource.class.getName());// service_event_parameters_id,entity_id,service_event_id, operation_type, entity_type
				data.put("service_event_parameters_id", service_event_id);
				data.put("operation_type", type);
				data.put("entity_type", "rest_api");
				return iresource.callEvent("38b6c070-0133-470f-ad60-7344b31a1f34", data_source_name, data);// insert service event query
				
			case "insert":
				if (StringUtil.isEmptys(service_event_id)) {
					service_event_id = "lt_" + UUID.randomUUID().toString().replaceAll("-", "");
					data.put("name", MapUtil.getString(data, "name", "execute"));
					data.put("service_event_id", service_event_id);
					iresource = this.getIBMO(IResource.class.getName());// service_id,name,desc,type ,rule,content ,expression,user_id,service_event_id
					iresource.callEvent("fm_system_add_services_event", data_source_name, data);// insert service event query
				} else {
					iresource = this.getIBMO(IResource.class.getName());// service_event_id
					iresource.callEvent("a35bd7f3-45c4-47e8-9a60-9fd6753c991b", data_source_name, data);// delete service event config
					iresource = this.getIBMO(IResource.class.getName());// service_event_id
					iresource.callEvent("f2efed89-7cb4-45b5-932a-020248f1aaff", data_source_name, data);// delete service event entity
				}
				
				StringBuffer ti1 = new StringBuffer("insert into ");
				ti1.append(table_name).append("(");
				StringBuffer ti2 = new StringBuffer("values(");
				
				List ipers = new ArrayList<>();
				for (Map cfg : service_event_config) {
					String entity_column_id = MapUtil.getString(cfg, "entity_column_id");
					String name = MapUtil.getString(cfg, "name");
					String alias = MapUtil.getString(cfg, "alias");
					String desc = MapUtil.getString(cfg, "desc");
					String remark = MapUtil.getString(cfg, "remark");
					int status = MapUtil.getInt(cfg, "status", 1);
					cfg.put("status", status);
					cfg.put("sort", sort++);
					cfg.put("user_id", user_id);
					cfg.put("config_type", MapUtil.getString(cfg, "config_type", "insert"));
					
					cfg.put("service_event_id", service_event_id);
					cfg.put("null_able", MapUtil.getInt(cfg, "null_able", 1));
					cfg.put("bundle_service_id", MapUtil.getString(cfg, "bundle_service_id", "org.oiue.service.event.execute.EventExecuteService"));
					
					ti1.append(name).append(",");
					ti2.append("?,");
					ipers.add(alias);
					iresource = this.getIBMO(IResource.class.getName());// bundle_service_id,service_event_id,alias,desc,remark,status,content,sort,config_type,null_able,update_user_id,service_event_parameters_id,component_instance_id,service_event_id,entity_column_id
					iresource.callEvent("82b0cec4-1e1d-49eb-8bbd-79bf9c6e2061", data_source_name, cfg);// insert service event query
				}
				ti1.deleteCharAt(ti1.length() - 1);
				ti2.deleteCharAt(ti2.length() - 1);
				ti1.append(") ").append(ti2).append(")");
				data.put("content", ti1.toString());
				data.put("expression", ListUtil.ListJoin(ipers, ",").replace("\"", ""));
				
				iresource = this.getIBMO(IResource.class.getName());// service_id,name,desc,type ,rule,content ,expression,user_id,service_event_id
				iresource.callEvent("fm_system_add_services_event", data_source_name, data);// insert service event query
				
				iresource = this.getIBMO(IResource.class.getName());// service_event_parameters_id,entity_id,service_event_id, operation_type, entity_type
				data.put("service_event_parameters_id", service_event_id);
				data.put("operation_type", type);
				data.put("entity_type", "rest_api");
				return iresource.callEvent("38b6c070-0133-470f-ad60-7344b31a1f34", data_source_name, data);// insert service event query
				
			case "update":
				if (StringUtil.isEmptys(service_event_id)) {
					service_event_id = "lt_" + UUID.randomUUID().toString().replaceAll("-", "");
					data.put("name", MapUtil.getString(data, "name", "execute"));
					data.put("service_event_id", service_event_id);
					iresource = this.getIBMO(IResource.class.getName());// service_id,name,desc,type ,rule,content ,expression,user_id,service_event_id
					iresource.callEvent("fm_system_add_services_event", data_source_name, data);// insert service event query
				} else {
					iresource = this.getIBMO(IResource.class.getName());// service_event_id
					iresource.callEvent("a35bd7f3-45c4-47e8-9a60-9fd6753c991b", data_source_name, data);// delete service event config
					iresource = this.getIBMO(IResource.class.getName());// service_event_id
					iresource.callEvent("f2efed89-7cb4-45b5-932a-020248f1aaff", data_source_name, data);// delete service event entity
				}
				
				StringBuffer tu1 = new StringBuffer("update ");
				tu1.append(table_name).append(" set ");
				StringBuffer tu2 = new StringBuffer();
				
				List upers = new ArrayList<>();
				List upers2 = new ArrayList<>();
				for (Map cfg : service_event_config) {
					String entity_column_id = MapUtil.getString(cfg, "entity_column_id");
					String name = MapUtil.getString(cfg, "name");
					String alias = MapUtil.getString(cfg, "alias");
					String desc = MapUtil.getString(cfg, "desc");
					String remark = MapUtil.getString(cfg, "remark");
					String config_type = MapUtil.getString(cfg, "config_type");
					int status = MapUtil.getInt(cfg, "status", 1);
					cfg.put("status", status);
					cfg.put("sort", sort++);
					cfg.put("user_id", user_id);
					
					cfg.put("service_event_id", service_event_id);
					cfg.put("null_able", MapUtil.getInt(cfg, "null_able", 1));
					cfg.put("bundle_service_id", MapUtil.getString(cfg, "bundle_service_id", "org.oiue.service.event.execute.EventExecuteService"));
					
					switch (config_type) {
						case "update":
						case "updateKey":
							("update".equals(config_type) ? tu1 : tu2).append(name).append("=?,");
							("update".equals(config_type) ? upers : upers2).add(alias);
							break;
						
						default:
							continue;
					}
					iresource = this.getIBMO(IResource.class.getName());// bundle_service_id,service_event_id,alias,desc,remark,status,content,sort,config_type,null_able,update_user_id,service_event_parameters_id,component_instance_id,service_event_id,entity_column_id
					iresource.callEvent("82b0cec4-1e1d-49eb-8bbd-79bf9c6e2061", data_source_name, cfg);// insert service event query
				}
				tu1.deleteCharAt(tu1.length() - 1);
				tu2.deleteCharAt(tu2.length() - 1);
				tu1.append(" where ").append(tu2);
				upers.addAll(upers2);
				data.put("content", tu1.toString());
				data.put("expression", ListUtil.ListJoin(upers, ",").replace("\"", ""));
				
				iresource = this.getIBMO(IResource.class.getName());// service_id,name,desc,type ,rule,content ,expression,user_id,service_event_id
				iresource.callEvent("fm_system_add_services_event", data_source_name, data);// insert service event query
				
				iresource = this.getIBMO(IResource.class.getName());// service_event_parameters_id,entity_id,service_event_id, operation_type, entity_type
				data.put("service_event_parameters_id", service_event_id);
				data.put("operation_type", type);
				data.put("entity_type", "rest_api");
				return iresource.callEvent("38b6c070-0133-470f-ad60-7344b31a1f34", data_source_name, data);// insert service event query
				
			case "select":
			case "selects":
			case "query":
				if (StringUtil.isEmptys(service_event_id)) {
					service_event_id = "lt_" + UUID.randomUUID().toString().replaceAll("-", "");
					data.put("name", MapUtil.getString(data, "name", "execute"));
					data.put("service_event_id", service_event_id);
					iresource = this.getIBMO(IResource.class.getName());// service_id,name,desc,type ,rule,content ,expression,user_id,service_event_id
					iresource.callEvent("fm_system_add_services_event", data_source_name, data);// insert service event query
				} else {
					iresource = this.getIBMO(IResource.class.getName());// service_event_id
					iresource.callEvent("a35bd7f3-45c4-47e8-9a60-9fd6753c991b", data_source_name, data);// delete service event config
					iresource = this.getIBMO(IResource.class.getName());// service_event_id
					iresource.callEvent("f2efed89-7cb4-45b5-932a-020248f1aaff", data_source_name, data);// delete service event entity
				}
				StringBuffer tq1 = new StringBuffer("select ");
				
				List qpers = new ArrayList<>();
				for (Map cfg : service_event_config) {
					String entity_column_id = MapUtil.getString(cfg, "entity_column_id");
					String name = MapUtil.getString(cfg, "name");
					String alias = MapUtil.getString(cfg, "alias");
					String desc = MapUtil.getString(cfg, "desc");
					String remark = MapUtil.getString(cfg, "remark");
					String config_type = MapUtil.getString(cfg, "config_type");
					int status = MapUtil.getInt(cfg, "status", 1);
					cfg.put("status", status);
					cfg.put("sort", sort++);
					cfg.put("user_id", user_id);
					if (StringUtil.isEmptys(MapUtil.getString(cfg, "component_instance_id")))
						cfg.remove("component_instance_id");
					if (StringUtil.isEmptys(MapUtil.getString(cfg, "service_event_id")))
						cfg.remove("service_event_id");
					if (StringUtil.isEmptys(MapUtil.getString(cfg, "service_event_parameters_id")))
						cfg.remove("service_event_parameters_id");
					
					cfg.put("service_event_id", service_event_id);
					cfg.put("null_able", MapUtil.getInt(cfg, "null_able", 1));
					cfg.put("bundle_service_id", MapUtil.getString(cfg, "bundle_service_id", "org.oiue.service.event.execute.EventExecuteService"));
					
					switch (config_type) {
						case "result":
							tq1.append(name).append(" as ").append(alias).append(",");
							break;
						
						case "condition": // 先决条件
							// tq2.append(" ").append(name).append(" =? ").append("and");
							break;
						
						case "filter": // 过滤
							break;
						
						default:
							continue;
					}
					iresource = this.getIBMO(IResource.class.getName());// bundle_service_id,service_event_id,alias,desc,remark,status,content,sort,config_type,null_able,user_id,service_event_parameters_id,component_instance_id,service_event_id,entity_column_id
					try {
						iresource.callEvent("82b0cec4-1e1d-49eb-8bbd-79bf9c6e2061", data_source_name, cfg);// insert service event config
					} catch (Exception e) {
						logger.error("cfg:" + cfg, e);
						throw new OIUEException(StatusResult._data_error, e.getMessage(), e);
					}
				}
				tq1.deleteCharAt(tq1.length() - 1);
				tq1.append(" from ").append(table_name);
				data.put("content", tq1.toString());
				iresource = this.getIBMO(IResource.class.getName());// service_event_parameters_id,entity_id,service_event_id, operation_type, entity_type
				data.put("service_event_parameters_id", service_event_id);
				data.put("operation_type", type);
				data.put("entity_type", "rest_api");
				iresource.callEvent("38b6c070-0133-470f-ad60-7344b31a1f34", data_source_name, data);// insert service event query
				
				iresource = this.getIBMO(IResource.class.getName());// service_id,name,desc,type ,rule,content ,expression,user_id,service_event_id
				return iresource.callEvent("fm_system_add_services_event", data_source_name, data);// insert service event query
			default:
				break;
		}
		
		return null;
	}
	
	@Override
	public Object testServiceEvent(Map data, Map event, String tokenid) {
		IResource iresource;
		iresource = this.getIBMO(IResource.class.getName());// service_id,name,desc,type ,rule,content ,expression,user_id,service_event_id
		Map t_event = new HashMap<>();
		t_event.put(EventField.event_type, MapUtil.getString(data, "type"));
		List events = new ArrayList<>();
		t_event.put("EVENTS", events);
		Map _event = new HashMap<>();
		events.add(_event);
		_event.put("content", MapUtil.getString(data, "sql"));
		_event.put("rule", MapUtil.getString(data, "rule"));
		_event.put("expression", MapUtil.getString(data, "expression").replace("\"", ""));
		_event.put("event_type", MapUtil.getString(data, "type"));
		return iresource.executeEvent(t_event, null, data, null);
	}
	
	
}
