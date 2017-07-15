package org.oiue.service.odp.event.dmo.mysql.select;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
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

public class SelectEventMysqlService extends DMO implements Event{

	private static final long serialVersionUID = -4232372066134799100L;
	private Logger logger;
	private LogService logService;

	public SelectEventMysqlService(LogService logService) {
		logger = logService.getLogger(this.getClass());
		this.logService = logService;
	}
	@Override
	public Event clone() throws CloneNotSupportedException{
		return new SelectEventMysqlService(logService);
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

		List<Map<?, ?>> events = eventConvert.convert(event, data);
		logger.debug("events:"+events+"|event:"+event+"|data:"+data);
		if (events == null||events.size()==0){
			throw new RuntimeException("event can not found ！event:"+event+"|data:"+data);
		}
		if (events.size() == 1) {
			Map event_t =events.get(0);
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

	public Object call(Map map, CallBack callBack) throws Throwable {
		if(map == null || map.isEmpty()) {
			logger.error("the parameter map is null");
			throw new RuntimeException("the parameter map is null");
		}
		return executeQuery(map, callBack);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private Map executeQuery(Map map, CallBack callBack) throws Throwable {
		String sql = (String) map.get(EventField.content);
		if(sql == null || sql.isEmpty() ){
			logger.error("the sql is null");
			return null;
		}
		List<String> fmConditionList = (List<String>) map.get(EventField.contentList);
		List<Object> fmConditionListPrepare = null;
		if(fmConditionList != null && !fmConditionList.isEmpty()) {
			fmConditionListPrepare = new ArrayList<>();
			for(int i = 0; i < fmConditionList.size(); i++) {
				fmConditionListPrepare.add(MapUtil.get(map,fmConditionList.get(i)));
			}
		}
		this.execute(sql, fmConditionListPrepare);

		Map result = new HashMap();
		if(callBack == null) {
			if(this.getRs().next()) {
				try {
					result.putAll(getMapResult(this.getRs()));
				} catch (Throwable e) {
					throw new RuntimeException("map:"+map+"|error:"+e.getMessage(), e);
				}
			}
		} else {
			callBack.callBack(result);
		}
		return result;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private Map getMapResult(ResultSet rs) throws SQLException{
		ResultSetMetaData rsmd = rs.getMetaData();
		int sum = rsmd.getColumnCount();
		Hashtable row = new Hashtable();
		for (int i = 1; i < sum + 1; i++) {
			Object value = rs.getObject(i);
			if ((value instanceof BigDecimal)) {
				if (((BigDecimal)value).scale() == 0) {
					value = Long.valueOf(((BigDecimal)value).longValue());
				} else {
					value = Double.valueOf(((BigDecimal)value).doubleValue());
				}
			} else if ((value instanceof Clob)) {
				value = clobToString((Clob)value);
			}
			//			String key = rsmd.getColumnName(i);
			String key = rsmd.getColumnLabel(i);
			row.put(key, value == null ? "" : value);
		}
		return row;
	}

	private String clobToString(Clob clob) {
		if (clob == null) {
			return null;
		}
		try {
			Reader inStreamDoc = clob.getCharacterStream();
			char[] tempDoc = new char[(int)clob.length()];
			inStreamDoc.read(tempDoc);
			inStreamDoc.close();
			return new String(tempDoc);
		} catch (IOException | SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
