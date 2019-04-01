package org.oiue.service.odp.event.dmo.postgresql.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.oiue.service.log.LogService;
import org.oiue.service.log.Logger;
import org.oiue.service.odp.dmo.CallBack;
import org.oiue.service.odp.dmo.DMO;
import org.oiue.service.odp.event.api.Event;
import org.oiue.service.odp.event.api.EventConvertService;
import org.oiue.service.odp.event.api.EventField;
import org.oiue.table.structure.TableModel;
import org.oiue.tools.StatusResult;
import org.oiue.tools.exception.OIUEException;
import org.oiue.tools.map.MapUtil;

public class EventPostgresqlService extends DMO implements Event {
	
	private static final long serialVersionUID = -4232372066134799100L;
	private Logger logger;
	private LogService logService;
	
	public EventPostgresqlService(LogService logService) {
		logger = logService.getLogger(this.getClass());
		this.logService = logService;
	}
	
	@Override
	public Event clone() throws CloneNotSupportedException {
		return new EventPostgresqlService(logService);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public List Query(TableModel tableModel) {
		return null;
	}
	
	@Override
	public TableModel QueryObj(TableModel tableModel) {
		return null;
	}
	
	@Override
	public Object call(Map event, Map data, CallBack callBack) {
		Object rtnObject;
		EventConvertService eventConvert = this.getIDMO(EventConvertService.class.getName());
		
		int limit = 20;
		int start = 0;
		try {
			limit = MapUtil.getInt(data, "limit");
			start = MapUtil.getInt(data, "start");
		} catch (Throwable e) {}
		StringBuffer sb = new StringBuffer();
		if (limit > 0)
			sb.append(" limit ").append(limit).append(" offset ").append(start);
		
		List<Map<?, ?>> events = eventConvert.convert(event, data);
		logger.debug("events:" + events + "|event:" + event + "|data:" + data);
		if (events == null || events.size() == 0) {
			throw new RuntimeException("event can not found ！event:" + event + "|data:" + data);
		}
		if (events.size() == 1) {
			Map event_t = events.get(0);
			event_t.put("cutPage", sb.toString());
			if (event_t == null || event_t.get(EventField.event_type) == null)
				throw new RuntimeException("event error ！events:" + events + "|event:" + event + "|data:" + data);
			rtnObject = call(event_t, callBack);
		} else {
			List rtn = new ArrayList<>();
			for (Map mapevent : events) {
				rtn.add(call(mapevent, callBack));
			}
			rtnObject = rtn;
		}
		return rtnObject;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object call(Map map, CallBack callBack) {
		if (map == null || map.isEmpty()) {
			logger.error("the parameter map is null");
			throw new RuntimeException("the parameter map is null");
		}
		String countSql = (String) map.get(EventField.contentCount);
		if (countSql == null) {
			String content = (String) map.get(EventField.content);
			int index = content.toLowerCase().lastIndexOf("from");
			int indexs = content.toLowerCase().indexOf("from");
			if (index == indexs) {
				int order_index = content.toLowerCase().indexOf("order by", index);
				map.put(EventField.contentCount, "select count(1) as COUNT " + content.substring(index, order_index > 0 ? order_index : content.length()));
			} else {
				map.put(EventField.contentCount, "select count(1) as COUNT from (" + content + ")t");
			}
		}
		return executeQuery(map, callBack);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map executeQuery(Map map, CallBack callBack) {
		String sql = (String) map.get(EventField.content);
		String countSql = (String) map.get(EventField.contentCount);
		if (sql == null || sql.isEmpty() || countSql == null || countSql.isEmpty()) {
			logger.error("the sql is null");
			return null;
		}
		List<String> fmConditionList = (List<String>) map.get(EventField.contentList);
		Map result = new HashMap();
		String cutPage = null;
		try {
			cutPage = MapUtil.getString(map, "cutPage");
		} catch (Throwable e) {}
		
		if (MapUtil.getBoolean(map, "getData", true)) {
			this.execute(sql + (cutPage == null ? "" : " " + cutPage), fmConditionList);
			if (callBack == null) {
				result.put("root", getResult(this.getRs()));
			} else {
				try {
					while (this.getRs().next()) {
						callBack.callBack(getMapResult(this.getRs()));
					}
				} catch (Exception e) {
					throw new OIUEException(StatusResult._blocking_errors, map, e);
				}
			}
		}
		this.execute(countSql, fmConditionList);
		List<Map> cr = getResult(this.getRs());
		result.put("totalProperty", cr.size() > 0 ? cr.get(0).get("count") : 0);
		return result;
	}
}
