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
import org.oiue.tools.map.MapUtil;

public class EventPostgresqlService extends DMO implements Event{

	private static final long serialVersionUID = -4232372066134799100L;
	private Logger logger;
	private LogService logService;

	public EventPostgresqlService(LogService logService) {
		logger = logService.getLogger(this.getClass());
		this.logService = logService;
	}
	@Override
	public Event clone() throws CloneNotSupportedException{
		return new EventPostgresqlService(logService);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List Query(TableModel tableModel) throws Throwable {
		return null;
	}

	@Override
	public TableModel QueryObj(TableModel tableModel) throws Throwable {
		return null;
	}

	@Override
	public Object call(Map event, Map data, CallBack callBack) throws Throwable{
		Object rtnObject;
		EventConvertService eventConvert = this.getIDMO(EventConvertService.class.getName());

		int limit = 20;
		int start = 0;
		try {
			limit=MapUtil.getInt(data, "limit");
			start=MapUtil.getInt(data, "start");
		} catch (Throwable e) {}
		StringBuffer sb = new StringBuffer();
		if(limit>0)
			sb.append(" limit ").append(limit).append(" offset ").append(start);

		List<Map<?, ?>> events = eventConvert.convert(event, data);
		logger.debug("events:"+events+"|event:"+event+"|data:"+data);
		if (events == null||events.size()==0){
			throw new RuntimeException("event can not found ！event:"+event+"|data:"+data);
		}
		if (events.size() == 1) {
			Map event_t =events.get(0);
			event_t.put("cutPage", sb.toString());
			if(event_t==null||event_t.get(EventField.event_type)==null)
				throw new RuntimeException("event error ！events:"+events+"|event:"+event+"|data:"+data);
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
	public Object call(Map map, CallBack callBack) throws Throwable {
		if(map == null || map.isEmpty()) {
			logger.error("the parameter map is null");
			throw new RuntimeException("the parameter map is null");
		}
		String countSql = (String) map.get(EventField.contentCount);
		if(countSql == null) {
			String content = (String) map.get(EventField.content);
			int index = content.indexOf("from");
			int order_index = content.indexOf("order by");
			map.put(EventField.contentCount, "select count(1) as COUNT "+ content.substring(index, order_index>0?order_index:content.length()));
		}
		return executeQuery(map, callBack);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private Map executeQuery(Map map, CallBack callBack) throws Throwable {
		String sql = (String) map.get(EventField.content);
		String countSql = (String) map.get(EventField.contentCount);
		if(sql == null || sql.isEmpty() || countSql == null || countSql.isEmpty()){
			logger.error("the sql is null");
			return null;
		}
		String cutPage = null;
		try {
			cutPage=MapUtil.getString(map, "cutPage");
		} catch (Throwable e) {}

		List<String> fmConditionList = (List<String>) map.get(EventField.contentList);

		this.execute(sql+(cutPage==null?"":" "+cutPage), fmConditionList);
		Map result = new HashMap();
		if(callBack == null) {
			result.put("root", getResult(this.getRs()));
		} else {
			while (this.getRs().next()) {
				callBack.callBack(getMapResult(this.getRs()));
			}
			callBack.callBack(result);
		}
		this.execute(countSql, fmConditionList);
		result.put("totalProperty", getResult(this.getRs()).get(0).get("count"));
		return result;
	}
}
