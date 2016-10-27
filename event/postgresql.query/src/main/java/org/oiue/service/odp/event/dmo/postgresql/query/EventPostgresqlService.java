package org.oiue.service.odp.event.dmo.postgresql.query;

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
import org.oiue.service.odp.dmo.postgresql.DMO;
import org.oiue.service.odp.event.api.Event;
import org.oiue.service.odp.event.api.EventField;
import org.oiue.service.odp.res.dmo.CallBack;
import org.oiue.table.structure.TableModel;

public class EventPostgresqlService extends DMO implements Event{

	private static final long serialVersionUID = -4232372066134799100L;
	private static Logger logger;

	public EventPostgresqlService(LogService logService) {
		logger = logService.getLogger(this.getClass());
	}

	public EventPostgresqlService() {
		
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

	@SuppressWarnings("rawtypes")
    @Override
	public Object call(Map map) throws Throwable {
	    return call(map, null);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
	public Object call(Map map, CallBack callBack) throws Throwable {
	    if(map == null || map.isEmpty()) {
            logger.error("the parameter map is null");
            throw new RuntimeException("the parameter map is null");
        }
        String countSql = (String) map.get(EventField.contentCount);
        if(countSql == null) {
            String content = (String) map.get(EventField.content);
            int index = content.indexOf("from");
            map.put(EventField.contentCount, "select count(1) as COUNT "+ content.substring(index, content.length()));
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
	    List<String> fmConditionList = (List<String>) map.get(EventField.contentList);
		pstmt = this.getConn().prepareStatement(sql);
		if(fmConditionList != null && !fmConditionList.isEmpty()) {
			for(int i = 0; i < fmConditionList.size(); i++) {
				this.setParameter(i + 1, fmConditionList.get(i));
			}
		}
		rs = pstmt.executeQuery();
		if(logger.isDebugEnabled()) {
			logger.debug("mysql sql: " + sql);
			logger.debug("mysql parameters: " + map.toString());
		}
		pstmt = this.getConn().prepareStatement(countSql);
        if(fmConditionList != null && !fmConditionList.isEmpty()) {
            for(int i = 0; i < fmConditionList.size(); i++) {
                this.setParameter(i + 1, fmConditionList.get(i));
            }
        }
        Map result = new HashMap();
        if(callBack == null) {
            result.put("root", getResult(rs));
        } else {
            while (rs.next()) {
                callBack.callBack(getMapResult(rs));
            }
            callBack.callBack(result);
        }
        rs.close();
        rs = pstmt.executeQuery();
        result.put("totalProperty", getResult(rs).get(0).get("count"));
		return result;
	}
	
	
	@SuppressWarnings({"rawtypes", "unchecked"})
    private List<Map> getResult(ResultSet rs) throws SQLException{
		ResultSetMetaData rsmd = rs.getMetaData();
	    List<Map> listMap = new ArrayList<Map>();
		while(rs.next()){
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
		    	String key = rsmd.getColumnName(i);
		    	row.put(key, value == null ? "" : value);
		    }
		    listMap.add(row);
	    }
	    return listMap;
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
	    	String key = rsmd.getColumnName(i);
	    	row.put(key.toUpperCase(), value == null ? "" : value);
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
