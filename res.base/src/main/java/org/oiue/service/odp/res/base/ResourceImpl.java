package org.oiue.service.odp.res.base;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.oiue.service.cache.CacheServiceManager;
import org.oiue.service.cache.Type;
import org.oiue.service.log.LogService;
import org.oiue.service.log.Logger;
import org.oiue.service.odp.bmo.BMO;
import org.oiue.service.odp.dmo.CallBack;
import org.oiue.service.odp.dmo.IDMO;
import org.oiue.service.odp.event.api.Event;
import org.oiue.service.odp.event.api.EventConvertService;
import org.oiue.service.odp.event.api.EventField;
import org.oiue.service.odp.event.api.EventFilter;
import org.oiue.service.odp.event.api.EventResultFilter;
import org.oiue.service.odp.res.api.IResource;
import org.oiue.service.odp.res.dmo.IRes;
import org.oiue.service.system.analyzer.AnalyzerService;
import org.oiue.service.system.analyzer.TimeLogger;
import org.oiue.table.structure.StructureTable;
import org.oiue.table.structure.TableExt;
import org.oiue.table.structure.TableModel;
import org.oiue.tools.StatusResult;
import org.oiue.tools.map.MapUtil;
import org.oiue.tools.serializ.CloneTools;
import org.oiue.tools.string.StringUtil;

public class ResourceImpl extends BMO implements IResource {
	private static final long serialVersionUID = 1L;
	private static Map<String, Map<String, TableModel>> service_events = new Hashtable<String, Map<String, TableModel>>();
	private static Logger logger;
	private static TimeLogger tLogger;
	private static LogService logService;
	private static CacheServiceManager cacheService;

	private static Map<String, EventFilter> beforeEventFilter = new HashMap<String, EventFilter>();
	private static Map<Integer, String> beforeFilterSort = new TreeMap<Integer, String>();

	private static Map<String, EventResultFilter> afterEventFilter = new HashMap<String, EventResultFilter>();
	private static Map<Integer, String> afterFilterSort = new TreeMap<Integer, String>();

	public ResourceImpl(LogService logService,AnalyzerService analyzerService, CacheServiceManager cacheService) {
		logger = logService.getLogger(this.getClass());
		tLogger = analyzerService.getLogger(this.getClass());

		ResourceImpl.cacheService = cacheService;
		ResourceImpl.logService = logService;
	}
	public ResourceImpl() {}

	@Override
	public boolean Update(TableModel tableModel) throws Throwable {
		IDMO dmo = this.getIDMO(IRes.class.getName());
		return dmo.Update(tableModel);
	}

	@Override
	public boolean Update(List<TableModel> tm) throws Throwable {
		IDMO dmo = this.getIDMO(IRes.class.getName());
		return dmo.Update(tm);
	}

	@Override
	public List Query(TableModel tableModel) throws Throwable {
		IDMO dmo = this.getIDMO(IRes.class.getName());
		return dmo.Query(tableModel);
	}

	@Override
	public TableModel QueryObj(TableModel tableModel) throws Throwable {
		IDMO dmo = this.getIDMO(IRes.class.getName());
		return dmo.QueryObj(tableModel);
	}

	@Override
	public boolean haveTable(String name) throws Throwable {
		IRes res = (IRes) this.getIDMO(IRes.class.getName());
		return res.haveTable(name);
	}

	@Override
	public boolean updateTable(TableExt dt) throws Throwable {
		IRes res = (IRes) this.getIDMO(IRes.class.getName());
		return res.updateTable(dt);
	}

	@Override
	public boolean updateTable(TableExt dt, TableExt ret) throws Throwable {
		IRes res = (IRes) this.getIDMO(IRes.class.getName());
		return res.updateTable(dt, ret);
	}

	@Override
	public TableModel getEvent(String event_name, String data_type) throws Throwable {
		TableModel returnTM = null;

		Map<String, TableModel> events = service_events.get(data_type);
		if (events != null) {
			returnTM = events.get(event_name);
		}
		if (events == null || returnTM == null) {
			IRes res = (IRes) this.getIDMO(IRes.class.getName());
			if (events == null) {
				StructureTable st = new StructureTable();
				st.setCmdKey(0);
				List rtn = res.Query(st);
				if (rtn != null) {
					StructureTable t;
					String en;
					Map<String, TableModel> enm;
					String dt;
					for (Object object : rtn) {
						t = (StructureTable) object;
						dt = t.getValue("DATA_TYPE_CLASS") + "";
						enm = service_events.get(dt);
						if (enm == null) {
							enm = new Hashtable<String, TableModel>();
							service_events.put(dt, enm);
						}
						en = t.getValue("SERVICE_EVENT") + "";
						if (data_type.equals(dt) && event_name.equals(en)) {
							returnTM = t;
						}
						enm.put(en, t);
					}
				}
			} else {

			}
		}
		return returnTM;
	}

	@Override
	public Object callEvent(String event_name, String service_name, String bundle_service_name, String bundle_name, String data_source_name, Map map) throws Throwable {
		return callEvent(event_name, service_name, bundle_service_name, bundle_name, data_source_name, map, null);
	}

	@Override
	public Object callEvent(String event_name, String service_name, String bundle_service_name, String bundle_name, String data_source_name, Map map, CallBack callBack) throws Throwable {
		boolean isNull = event_name == null || event_name.isEmpty() || data_source_name == null || data_source_name.isEmpty() || service_name == null || service_name.isEmpty();
		if (isNull) {
			throw new RuntimeException(this.toString() + " the parameters of callEvent method can't be null or empty");
		}
		if (StringUtil.isEmptys(data_source_name))
			data_source_name = default_data_source_name;
		EventConvertService eventConvertService = (EventConvertService) this.getIDMO(EventConvertService.class.getName());
		Map<String, Object> event_query = new HashMap<>();
		event_query.put(EventField.bundle_name, bundle_name);
		event_query.put(EventField.bundle_service_name, bundle_service_name);
		event_query.put(EventField.service_name, service_name);
		event_query.put(EventField.event_name, event_name);
		event_query.put(EventField.data_source_name, data_source_name);
		event_query.put("cmd_key", 0);

		return this.convertCallEvent(eventConvertService.query(event_query), data_source_name, map, callBack);
	}

	@Override
	public Object callEvent(String event_id, String data_source_name, Map map) throws Throwable {
		return this.callEvent(event_id, data_source_name, map, null);
	}

	@Override
	public Object callEvent(String event_id, String data_source_name, Map map, CallBack callBack) throws Throwable {
		if (StringUtil.isEmptys(event_id)) {
			throw new RuntimeException("event_id can't be null or empty");
		}
		if (StringUtil.isEmptys(data_source_name))
			data_source_name = default_data_source_name;
		String eventId = data_source_name+"%"+event_id;
		Map eventMap = (Map) cacheService.get("_system_event_", eventId);
		if(eventMap==null||!openCache){
			EventConvertService eventConvertService = (EventConvertService) this.getIDMO(EventConvertService.class.getName(),data_source_name);
			Map<String, Object> event_query = new HashMap<>();
			event_query.put(EventField.service_event_id, event_id);
			event_query.put(EventField.data_source_name, data_source_name);
			event_query.put("cmd_key", 10);
			eventMap=eventConvertService.query(event_query);
			cacheService.put("_system_event_",eventId, eventMap,Type.ONE);
		}
		String query_data_source_name = MapUtil.getString(map, "data_source_name");
		return this.convertCallEvent(eventMap, StringUtil.isEmptys(query_data_source_name)?data_source_name:query_data_source_name, map, callBack);
	}

	@Override
	public Object convertCallEvent(Map event, String data_source_name, Map map,CallBack callBack)throws Throwable{

		StatusResult afr;
		Object rtnObject;

		// -----------------------before filter start-------------------------------------
		logger.debug("event eventPoolFilter :{}" , beforeEventFilter);

		long startbfTime = System.currentTimeMillis();
		for (EventFilter afilter : beforeEventFilter.values()) {
			long startTime = System.currentTimeMillis();

			afr = afilter.doFilter(event, data_source_name, map, callBack);

			if (tLogger.isDebugEnabled()) {
				long endTime = System.currentTimeMillis();
				Map para = new HashMap();
				para.put("startTime", startTime);
				para.put("endTime", endTime);
				para.put("desc", "EventFilter[" + afilter.getClass().getName() + "]" + afr);
				try {
					para.put("para", CloneTools.clone(map));
				} catch (Throwable e) {
					para.put("para", map);
				}
				tLogger.debug(para);
			}

			logger.debug("run before EventFilter [{}]：{};map={}" ,afilter,afr,map);

			if (afr.getResult() == StatusResult._SUCCESS_OVER) {
				map.put("status", afr.getResult());
				map.put("msg", afr.getDescription());
				return map;
			} else if (afr.getResult() == StatusResult._SUCCESS) {
				continue;
			} else if (afr.getResult() < StatusResult._NoncriticalAbnormal) {
				map.put("status", afr.getResult());
				map.put(afr.getResult() <= StatusResult._permissionDenied ? "exception" : "msg", afr.getDescription());
				return map;
			}
		}
		if (tLogger.isDebugEnabled()) {
			long endTime = System.currentTimeMillis();
			Map para = new HashMap();
			para.put("startTime", startbfTime);
			para.put("endTime", endTime);
			para.put("desc", "Before EventFilters [" + beforeEventFilter + "]");
			try {
				para.put("para", CloneTools.clone(map));
			} catch (Throwable e) {
				para.put("para", map);
			}
			tLogger.debug(para);
		}
		// -----------------------before filter end-------------------------------------

		rtnObject = callEvent(event, data_source_name, map, callBack);

		// -----------------------after filter start-------------------------------------
		logger.debug("event eventRPoolFilter :{}" ,afterEventFilter);

		startbfTime = System.currentTimeMillis();
		for (EventResultFilter afilter : afterEventFilter.values()) {
			long startTime = System.currentTimeMillis();

			afr = afilter.doFilter(rtnObject, event, data_source_name, map, callBack);

			if (tLogger.isDebugEnabled()) {
				Map para = new HashMap();
				para.put("startTime", startTime);
				long endTime = System.currentTimeMillis();
				para.put("endTime", endTime);
				para.put("desc", "after EventFilter[" + afilter.getClass().getName() + "]" + afr);
				try {
					para.put("para", CloneTools.clone(map));
				} catch (Throwable e) {
					para.put("para", map);
				}
				tLogger.debug(para);
			}

			if (afr.getResult() == StatusResult._SUCCESS_OVER) {
				return rtnObject;
			} else if (afr.getResult() == StatusResult._SUCCESS) {
				continue;
			} else if (afr.getResult() < StatusResult._NoncriticalAbnormal) {
				map.put("status", afr.getResult());
				map.put("eventData", rtnObject);
				map.put(afr.getResult() <= StatusResult._permissionDenied ? "exception" : "msg", afr.getDescription());
				return map;
			}
		}
		if (tLogger.isDebugEnabled()) {
			long endTime = System.currentTimeMillis();
			Map para = new HashMap();
			para.put("startTime", startbfTime);
			para.put("endTime", endTime);
			para.put("desc", "Before EventFilters [" + beforeEventFilter + "]");
			try {
				para.put("para", CloneTools.clone(map));
			} catch (Throwable e) {
				para.put("para", map);
			}
			tLogger.debug(para);
		}
		// -----------------------after filter start-------------------------------------

		return rtnObject;
	}

	@Override
	public Object callEvent(Map event_map, String data_source_name,Map data, CallBack callBack) throws Throwable {
		if (StringUtil.isEmptys(data_source_name))
			data_source_name = default_data_source_name;

		Object event_type = event_map.get(EventField.event_type);
		if(event_type==null){
			event_type = event_map.get(EventField.event_type.toUpperCase());
			if(event_type==null)
				throw new RuntimeException("event error:"+event_map);
		}
		Event event = (Event) this.getIDMO(event_type + "", data_source_name, logService);
		if (event != null) {
			return event.call(event_map, data, callBack);
		} else {
			throw new RuntimeException(this.toString() + " get event error");
		}
	}

	private static String default_data_source_name = "";
	private static boolean openCache = true;

	public void updated(Dictionary<String, ?> props)  {
		default_data_source_name = (String) props.get("defaultConn");

		try {
			String openCacheStr =  props.get("openCache")+"";
			if(StringUtil.isTrue(openCacheStr)){
				openCache=true;
			}else{
				openCache = false;
			}
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		}
	}
	@Override
	public boolean registerEventFilter(String requestEvent, EventFilter eventFilter, int index) {
		if (beforeFilterSort.get(index) != null) {
			throw new RuntimeException("index conflict! name=" + requestEvent + ", old index is " + beforeFilterSort.get(index));
		}
		if (beforeEventFilter.get(requestEvent) == null) {
			beforeEventFilter.put(requestEvent, eventFilter);
			beforeFilterSort.put(index, requestEvent);

			Map<String, EventFilter> eventPoolFilterTemp = new LinkedHashMap<String, EventFilter>();
			for (Iterator iterator = beforeFilterSort.values().iterator(); iterator.hasNext();) {
				String value = (String) iterator.next();
				eventPoolFilterTemp.put(value, beforeEventFilter.get(value));
			}
			beforeEventFilter = eventPoolFilterTemp;
			return true;
		}
		return false;
	}
	@Override
	public void unregisterEventFilter(String requestEvent) {
		beforeEventFilter.remove(requestEvent);
		for (Iterator iterator = beforeFilterSort.values().iterator(); iterator.hasNext();) {
			String requestEvents = (String) iterator.next();
			if (requestEvent.equals(requestEvents))
				iterator.remove();
		}
	}
	@Override
	public boolean registerEventResultFilter(String requestEvent, EventResultFilter eventResultFilter, int index) {
		if (afterFilterSort.get(index) != null) {
			throw new RuntimeException("index conflict! name=" + requestEvent + ", old index is " + afterFilterSort.get(index));
		}
		if (afterEventFilter.get(requestEvent) == null) {
			afterEventFilter.put(requestEvent, eventResultFilter);
			afterFilterSort.put(index, requestEvent);

			Map<String, EventResultFilter> eventPoolFilterTemp = new LinkedHashMap<String, EventResultFilter>();
			for (Iterator iterator = afterFilterSort.values().iterator(); iterator.hasNext();) {
				String value = (String) iterator.next();
				eventPoolFilterTemp.put(value, afterEventFilter.get(value));
			}
			afterEventFilter = eventPoolFilterTemp;
			return true;
		}
		return false;
	}
	@Override
	public void unregisterEventResultFilter(String requestEvent) {
		afterEventFilter.remove(requestEvent);
		for (Iterator iterator = afterFilterSort.values().iterator(); iterator.hasNext();) {
			String requestEvents = (String) iterator.next();
			if (requestEvent.equals(requestEvents))
				iterator.remove();
		}
	}
	@Override
	public void unregisterAllEventFilter() {
		beforeEventFilter.clear();
		afterEventFilter.clear();
		beforeFilterSort.clear();
		afterFilterSort.clear();
	}
}
