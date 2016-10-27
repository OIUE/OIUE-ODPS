package org.oiue.service.odp.structure.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.oiue.service.log.LogService;
import org.oiue.service.log.Logger;
import org.oiue.service.odp.structure.StructureConvert;
import org.oiue.service.odp.structure.StructureService;
import org.oiue.service.odp.structure.StructureServiceManager;
import org.oiue.service.system.analyzer.AnalyzerService;
import org.oiue.service.system.analyzer.TimeLogger;
import org.oiue.tools.map.MapUtil;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

@SuppressWarnings({ "rawtypes", "serial", "unused" })
public class StructureServiceManagerImpl implements Serializable, StructureServiceManager, ManagedService {

    private Map<String, StructureService> default_strucures = new HashMap<>();
    private Map<String, Map<String, StructureService>> strucures = new HashMap<>();

    private Logger logger;
    private TimeLogger timeLogger;

    public StructureServiceManagerImpl(LogService logService, AnalyzerService analyzerService) {
        this.logger = logService.getLogger(this.getClass());
        this.timeLogger = analyzerService.getLogger(this.getClass());
    }

    @Override  //mysql select
    public boolean registerStructure(String DBType, String type, StructureService structure) {
        if (DBType == null) {
            if (default_strucures.containsKey(type)) {
                throw new RuntimeException("Duplicate registration![" + DBType + "," + structure.getClass().getSimpleName() + "]");
            } else
                default_strucures.put(type, structure);
        } else {
            Map<String, StructureService> strucurem = strucures.get(DBType);
            if (strucurem == null) {
                strucurem = new HashMap<>();
                strucures.put(DBType, strucurem);
            }
            if (strucurem.containsKey(type)) {
                throw new RuntimeException("Duplicate registration![" + DBType + "," + structure.getClass().getSimpleName() + "]");
            } else
                strucurem.put(type, structure);
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public List<Map<?, ?>> parse(Map<?, ?> event, Map<String, Object> parameter) {
        List<Map<?, ?>> rtnList = new ArrayList<>();
        if (logger.isDebugEnabled()) {
            logger.debug("event:" + event + ",parameter:" + parameter);
        }
        String content = MapUtil.getString((Map<String, Object>) event, "CONTENT");
        String expression = MapUtil.getString((Map<String, Object>) event, "EXPRESSION");
        String event_type = MapUtil.getString((Map<String, Object>) event, "EVENT_TYPE");

        List per = new ArrayList<>();
        if (expression != null) {
            String[] expressions = expression.split(",");
            for (String ep : expressions) {
                if(ep!=null)
                    ep=ep.trim();
                per.add(MapUtil.get(parameter, ep));
            }
        }
        Map rtn = new HashMap<>();
//        rtn.put(EventField.contentList, per);
//        rtn.put(EventField.content, content);
//        rtn.put(EventField.event_type, event_type);
        rtnList.add(rtn);

        return rtnList;
    }

    @Override
    public void updated(Dictionary props) throws ConfigurationException {
        try {
            if (props != null) {
            	
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

	@Override
	public StructureService getStructureService(String type) {
		return default_strucures.get(type);
	}

	@Override
	public StructureService getStructureService(String DBType, String type) {
		return DBType==null?this.getStructureService(type):strucures.get(DBType).get(type);
	}

	@Override
	public boolean registerStructureConvert(String DBType, String type, StructureConvert structure) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public StructureConvert getStructureConvert(String DBType, String type) {
		// TODO Auto-generated method stub
		return null;
	}
}
