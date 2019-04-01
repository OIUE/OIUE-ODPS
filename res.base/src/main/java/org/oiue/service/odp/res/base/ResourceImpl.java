package org.oiue.service.odp.res.base;

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
import org.oiue.tools.json.JSONUtil;
import org.oiue.tools.map.MapUtil;
import org.oiue.tools.serializ.CloneTools;
import org.oiue.tools.string.StringUtil;

@SuppressWarnings("rawtypes")
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
	
	public ResourceImpl(LogService logService, AnalyzerService analyzerService, CacheServiceManager cacheService) {
		logger = logService.getLogger(this.getClass());
		tLogger = analyzerService.getLogger(this.getClass());
		
		ResourceImpl.cacheService = cacheService;
		ResourceImpl.logService = logService;
	}
	
	public ResourceImpl() {}
	
	@Override
	public boolean Update(TableModel tableModel) {
		IDMO dmo = this.getIDMO(IRes.class.getName());
		return dmo.Update(tableModel);
	}
	
	@Override
	public boolean Update(List<TableModel> tm) {
		IDMO dmo = this.getIDMO(IRes.class.getName());
		return dmo.Update(tm);
	}
	
	@Override
	public List Query(TableModel tableModel) {
		IDMO dmo = this.getIDMO(IRes.class.getName());
		return dmo.Query(tableModel);
	}
	
	@Override
	public TableModel QueryObj(TableModel tableModel) {
		IDMO dmo = this.getIDMO(IRes.class.getName());
		return dmo.QueryObj(tableModel);
	}
	
	@Override
	public boolean haveTable(String name) {
		IRes res = (IRes) this.getIDMO(IRes.class.getName());
		return res.haveTable(name);
	}
	
	@Override
	public boolean updateTable(TableExt dt) {
		IRes res = (IRes) this.getIDMO(IRes.class.getName());
		return res.updateTable(dt);
	}
	
	@Override
	public boolean updateTable(TableExt dt, TableExt ret) {
		IRes res = (IRes) this.getIDMO(IRes.class.getName());
		return res.updateTable(dt, ret);
	}
	
	@Override
	public TableModel getEvent(String event_name, String data_type) {
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
	public Map getEventByIDType(String event_id, String data_type_name) {
		if (StringUtil.isEmptys(event_id)) {
			throw new RuntimeException("event_id can't be null or empty");
		}
		if (StringUtil.isEmptys(data_type_name))
			data_type_name = default_data_source_name;
		String eventId = data_type_name + "%" + event_id;
		Map eventMap = (Map) cacheService.get("_system_event_", eventId);
		if (eventMap == null || !openCache) {
			EventConvertService eventConvertService = (EventConvertService) this.getIDMO(EventConvertService.class.getName(), data_type_name);
			Map<String, Object> event_query = new HashMap<>();
			event_query.put(EventField.service_event_id, event_id);
			event_query.put(EventField.data_type_name, data_type_name);
			event_query.put("cmd_key", 10);
			eventMap = eventConvertService.query(event_query);
			cacheService.put("_system_event_", eventId, eventMap, Type.ONE);
		}
		return eventMap;
	}
	
	@Override
	public Map getEventByIDName(String event_id, String data_source_name) {
		if (StringUtil.isEmptys(event_id)) {
			throw new RuntimeException("event_id can't be null or empty");
		}
		if (StringUtil.isEmptys(data_source_name))
			data_source_name = default_data_source_name;
		String eventId = data_source_name + "%" + event_id;
		Map eventMap = (Map) cacheService.get("_system_event_", eventId);
		if (eventMap == null || !openCache) {
			EventConvertService eventConvertService = (EventConvertService) this.getIDMO(EventConvertService.class.getName(), data_source_name);
			Map<String, Object> event_query = new HashMap<>();
			event_query.put(EventField.service_event_id, event_id);
			event_query.put(EventField.data_source_name, data_source_name);
			// event_query.put("cmd_key", 11);
			// event_query.put("cmd_key", 12);
			event_query.put("cmd_key", 13);
			eventMap = eventConvertService.query(event_query);
			cacheService.put("_system_event_", eventId, eventMap, Type.ONE);
		}
		return eventMap;
	}
	
	@Override
	public <T> T callEvent(String event_id, String data_source_name, Map map) {
		return this.callEvent(event_id, data_source_name, map, null);
	}
	
	@Override
	public <T> T callEvent(String event_id, String data_source_name, Map map, CallBack callBack) {
		Map eventMap = this.getEventByIDName(event_id, data_source_name);
		String query_data_source_name = MapUtil.getString(map, "data_source_name");
		return this.executeEvent(eventMap, StringUtil.isEmptys(query_data_source_name) ? data_source_name : query_data_source_name, map, callBack);
	}
	
	@Override
	public final <T> T executeEvent(Map event, String data_source_name, Map map, CallBack callBack) {
		if (StringUtil.isEmptys(data_source_name)){
			String query_data_source_name = MapUtil.getString(map, "data_source_name");
			data_source_name = StringUtil.isEmptys(query_data_source_name) ?default_data_source_name:query_data_source_name ;
		}
		
		StatusResult afr = null;
		T rtnObject = null;
		
		long startbfTime = System.currentTimeMillis();
		boolean run = true;
		try {
			anchor: while (run) {
				// -----------------------before filter start-------------------------------------
				logger.debug("event eventPoolFilter :{}", beforeEventFilter);
				
				for (EventFilter afilter : beforeEventFilter.values()) {
					long startTime = System.currentTimeMillis();
					
					afr = afilter.doFilter(event, data_source_name, map, callBack);
					
					if (tLogger.isDebugEnabled()) {
						long endTime = System.currentTimeMillis();
						Map<String, Object> para = new HashMap<>();
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
					
					logger.debug("run before EventFilter [{}]：{};map={}", afilter, afr, map);
					
					if (afr.getResult() == StatusResult._SUCCESS_OVER) {
						map.put("status", afr.getResult());
						map.put("msg", afr.getDescription());
						rtnObject = (T) map;
						run = false;
						break anchor;
					} else if (afr.getResult() == StatusResult._SUCCESS) {
						continue;
					} else if (afr.getResult() < StatusResult._ncriticalAbnormal) {
						map.put("status", afr.getResult());
						map.put(afr.getResult() <= StatusResult._permissionDenied ? "exception" : "msg", afr.getDescription());
						rtnObject = (T) map;
						run = false;
						break anchor;
					}
				}
				if (tLogger.isDebugEnabled()) {
					long endTime = System.currentTimeMillis();
					Map<String, Object> para = new HashMap<>();
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
				logger.debug("event eventRPoolFilter :{}", afterEventFilter);
				
				startbfTime = System.currentTimeMillis();
				for (EventResultFilter afilter : afterEventFilter.values()) {
					long startTime = System.currentTimeMillis();
					
					afr = afilter.doFilter(rtnObject, event, data_source_name, map, callBack);
					
					if (tLogger.isDebugEnabled()) {
						Map<String, Object> para = new HashMap<>();
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
						run = false;
						break anchor;
					} else if (afr.getResult() == StatusResult._SUCCESS) {
						continue;
					} else if (afr.getResult() < StatusResult._ncriticalAbnormal) {
						run = false;
						break anchor;
					}
				}
				run = false;
			}
		} finally {
			// -----------------------after filter start-------------------------------------
			if (tLogger.isDebugEnabled()) {
				long endTime = System.currentTimeMillis();
				Map<String, Object> para = new HashMap<>();
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
		}
		
		return rtnObject;
	}
	
	private final <T> T callEvent(Map event_map, String data_source_name, Map data, CallBack callBack) {
		List<Map> events = null;
		String eventsstr = (String) MapUtil.getVauleMatchCase(event_map, "EVENTS");
		if (!StringUtil.isEmptys(eventsstr)) {
			events = (List<Map>) JSONUtil.parserStrToList(eventsstr, false);
		}
		if (events == null || events.size() == 0) {
			throw new RuntimeException("event error:" + event_map);
		} else if (events.size() == 1) {
			Map event_p = events.get(0);
			Object event_type = event_map.get(EventField.event_type);
			if (event_type == null) {
				event_type = event_map.get(EventField.event_type.toUpperCase());
				if (event_type == null)
					throw new RuntimeException("event error:" + event_p);
			}
			Event event = (Event) this.getIDMO((String) event_type, data_source_name, logService);
			if (event != null) {
				event_p.put("event_type", event_type);
				return event.call(event_p, data, callBack);
			} else {
				throw new RuntimeException(this.toString() + " get event error");
			}
		} else {
			for (Iterator iterator = events.iterator(); iterator.hasNext();) {
				Map event_p = (Map) iterator.next();
				String event_id = MapUtil.getString(event_p, "content");
				String e_data_source_name = MapUtil.getString(event_p, "expression");
				data.put(event_id, this.callEvent(event_id, e_data_source_name, data, callBack));
			}
			return (T) data;
		}
	}
	
	private static String default_data_source_name = "";
	private static boolean openCache = true;
	
	public void updated(Map<String, ?> props) {
		default_data_source_name = (String) props.get("defaultConn");
		
		try {
			String openCacheStr = props.get("openCache") + "";
			if (StringUtil.isTrue(openCacheStr)) {
				openCache = true;
			} else {
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
