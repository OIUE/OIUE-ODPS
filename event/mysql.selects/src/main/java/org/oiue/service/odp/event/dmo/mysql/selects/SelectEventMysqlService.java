package org.oiue.service.odp.event.dmo.mysql.selects;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.oiue.service.log.LogService;
import org.oiue.service.log.Logger;
import org.oiue.service.odp.dmo.mysql.DMO;
import org.oiue.service.odp.event.api.Event;
import org.oiue.service.odp.event.api.EventField;
import org.oiue.service.odp.res.dmo.CallBack;
import org.oiue.table.structure.TableModel;

public class SelectEventMysqlService extends DMO implements Event{

	private static final long serialVersionUID = -4232372066134799100L;
	private static Logger logger;

	public SelectEventMysqlService(LogService logService) {
		logger = logService.getLogger(this.getClass());
	}

	public SelectEventMysqlService() {
		
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
	
	@SuppressWarnings("rawtypes")
    @Override
	public Object call(Map map, CallBack callBack) throws Throwable {
	    if(map == null || map.isEmpty()) {
            logger.error("the parameter map is null");
            throw new RuntimeException("the parameter map is null");
        }
        return executeQuery(map, callBack);
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
    private List<Map> executeQuery(Map map, CallBack callBack) throws Throwable {
	    String sql = (String) map.get(EventField.content);
	    if(sql == null || sql.isEmpty() ){
			logger.error("the sql is null");
			return null;
		}
	    List<String> fmConditionList = (List<String>) map.get(EventField.contentList);
		pstmt = this.getConn().prepareStatement(sql);
		if(fmConditionList != null && !fmConditionList.isEmpty()) {
			for(int i = 0; i < fmConditionList.size(); i++) {
				this.setParameter(i + 1, map.get(fmConditionList.get(i)));
			}
		}
		rs = pstmt.executeQuery();
		if(logger.isDebugEnabled()) {
			logger.debug("mysql sql: " + sql);
			logger.debug("mysql parameters: " + map.toString());
		}
		List rtn = new ArrayList<>();
        while(rs.next()) {
            if(callBack == null) {
                rtn.add(getMapResult(rs));
            } else {
                callBack.callBack(getMapResult(rs));
            }
        }
		return rtn;
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
