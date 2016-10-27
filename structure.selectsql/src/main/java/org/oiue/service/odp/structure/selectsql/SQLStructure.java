package org.oiue.service.odp.structure.selectsql;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.oiue.service.log.Logger;
import org.oiue.service.odp.structure.StructureService;
import org.oiue.service.system.analyzer.TimeLogger;
import org.oiue.tools.json.JSONUtil;
import org.oiue.tools.map.MapUtil;

@SuppressWarnings({"unused","serial"})
public class SQLStructure implements StructureService {

    private Logger logger;
	private TimeLogger timeLogger;
    
	@Override
	public List<Map<?, ?>> parse(Map<?, ?> event, Map<String, Object> parameter) {
        if (logger.isDebugEnabled()) {
            logger.debug("event:" + event + ",parameter:" + parameter);
        }
        String content = MapUtil.getString((Map<String, Object>) event, "CONTENT");
        String expression = MapUtil.getString((Map<String, Object>) event, "EXPRESSION");
        String event_type = MapUtil.getString((Map<String, Object>) event, "EVENT_TYPE");
        
        List<Map<?, ?>> rtnList = new ArrayList();
        
        String[]  forms = content.toUpperCase().split("FROM");
        String[]  wheres = content.toUpperCase().split("WHERE");
        
        if(forms.length==2||wheres.length==2){
        	Map expressions = JSONUtil.parserStrToMap(expression);
        	
        	Map<String,Map> filter = (Map) expressions.get("filter");
        	
        	Set<String> filters = new HashSet<>(filter.keySet());
        	filters.retainAll(parameter.keySet());
        	
        	for (String column : filters) {
        		filter.get(column).put("value", parameter.get(column));
			}
        	for (Entry entry : filter.entrySet()) {
				String name =  (String) entry.getKey();
				Map column = (Map) entry.getValue();
				
			}
        	
        }
        
        String[]  orders = content.toUpperCase().split("ORDER BY");
        String[]  groups = content.toUpperCase().split("GROUP BY");
		return null;
	}

}