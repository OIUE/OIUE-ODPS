package org.oiue.service.odp.event.dmo.mysql.select;

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

public class SelectEventMysqlService extends DMO implements Event {
	
	private static final long serialVersionUID = -4232372066134799100L;
	private Logger logger;
	private LogService logService;
	
	public SelectEventMysqlService(LogService logService) {
		logger = logService.getLogger(this.getClass());
		this.logService = logService;
	}
	
	@Override
	public Event clone() throws CloneNotSupportedException {
		return new SelectEventMysqlService(logService);
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
		
		List<Map<?, ?>> events = eventConvert.convert(event, data);
		logger.debug("events:" + events + "|event:" + event + "|data:" + data);
		if (events == null || events.size() == 0) {
			throw new RuntimeException("event can not found ！event:" + event + "|data:" + data);
		}
		if (events.size() == 1) {
			Map event_t = events.get(0);
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
	
	public Object call(Map map, CallBack callBack) {
		if (map == null || map.isEmpty()) {
			logger.error("the parameter map is null");
			throw new RuntimeException("the parameter map is null");
		}
		return executeQuery(map, callBack);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map executeQuery(Map map, CallBack callBack) {
		String sql = (String) map.get(EventField.content);
		if (sql == null || sql.isEmpty()) {
			logger.error("the sql is null");
			return null;
		}
		List<String> fmConditionList = (List<String>) map.get(EventField.contentList);
		List<Object> fmConditionListPrepare = null;
		if (fmConditionList != null && !fmConditionList.isEmpty()) {
			fmConditionListPrepare = new ArrayList<>();
			for (int i = 0; i < fmConditionList.size(); i++) {
				fmConditionListPrepare.add(MapUtil.get(map, fmConditionList.get(i)));
			}
		}
		this.execute(sql, fmConditionListPrepare);
		
		Map result = new HashMap();
		if (callBack == null) {
			try {
				if (this.getRs().next()) {
					try {
						result.putAll(getMapResult(this.getRs()));
					} catch (Throwable e) {
						throw new RuntimeException("map:" + map + "|error:" + e.getMessage(), e);
					}
				}
			} catch (Exception e) {
				throw new OIUEException(StatusResult._blocking_errors, map, e);
			}
		} else {
			callBack.callBack(result);
		}
		return result;
	}
}
