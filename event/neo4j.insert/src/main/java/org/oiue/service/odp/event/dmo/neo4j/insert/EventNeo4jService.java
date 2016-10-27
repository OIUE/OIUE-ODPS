package org.oiue.service.odp.event.dmo.neo4j.insert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.oiue.service.log.LogService;
import org.oiue.service.log.Logger;
import org.oiue.service.odp.dmo.neo4j.DMO;
import org.oiue.service.odp.event.api.Event;
import org.oiue.service.odp.res.dmo.CallBack;
import org.oiue.table.structure.TableModel;

public class EventNeo4jService extends DMO implements Event{

	private static final long serialVersionUID = -4232372066134799100L;
	private static Logger logger;

	public EventNeo4jService(LogService logService) {
		logger = logService.getLogger(this.getClass());
	}

	public EventNeo4jService() {
		
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
        return executeUpdate(map);
	}
	
	
	@SuppressWarnings({"rawtypes"})
    public Map executeUpdate(Map map) throws Throwable {
	    Map rtnMap = new HashMap<>();
        return rtnMap;
	}
	
}
