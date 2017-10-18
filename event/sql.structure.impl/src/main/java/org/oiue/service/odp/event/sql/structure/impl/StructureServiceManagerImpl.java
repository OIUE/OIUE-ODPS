package org.oiue.service.odp.event.sql.structure.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.oiue.service.cache.CacheServiceManager;
import org.oiue.service.log.LogService;
import org.oiue.service.log.Logger;
import org.oiue.service.odp.event.api.EventField;
import org.oiue.service.odp.event.sql.structure.StructureService;
import org.oiue.service.odp.event.sql.structure.StructureServiceManager;
import org.oiue.service.system.analyzer.AnalyzerService;
import org.oiue.service.system.analyzer.TimeLogger;
import org.oiue.tools.json.JSONUtil;
import org.oiue.tools.map.MapUtil;
import org.oiue.tools.string.StringUtil;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

@SuppressWarnings({ "rawtypes", "serial", "unused" })
public class StructureServiceManagerImpl implements Serializable, StructureServiceManager, ManagedService {

	private Map<String, StructureService> default_strucures = new HashMap<>();
	private Map<String, Map<String, StructureService>> strucures = new HashMap<>();

	private Logger logger;
	private TimeLogger timeLogger;
	private CacheServiceManager cacheService;

	public StructureServiceManagerImpl(LogService logService, AnalyzerService analyzerService, CacheServiceManager cacheService) {
		this.logger = logService.getLogger(this.getClass());
		this.timeLogger = analyzerService.getLogger(this.getClass());
		this.cacheService = cacheService;
	}

	@Override
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

	@Override
	@SuppressWarnings("unchecked")
	public List<Map<?, ?>> parse(Map<?, ?> event, Map<String, Object> parameter) {
		List<Map<?, ?>> rtnList = new ArrayList<>();
		if (logger.isDebugEnabled()) {
			logger.debug("event:" + event + ",parameter:" + parameter);
		}
		String rule = MapUtil.getString((Map<String, Object>) event, "RULE");
		String content = MapUtil.getString((Map<String, Object>) event, "CONTENT");
		String expression = MapUtil.getString((Map<String, Object>) event, "EXPRESSION");
		String event_type = MapUtil.getString((Map<String, Object>) event, "EVENT_TYPE");

		if(rule==null||StringUtil.isEmptys(rule))
			rule="tradition";

		List per = new ArrayList<>();
		switch (rule) {
		case "tradition":
			if (expression != null&&!StringUtil.isEmptys(expression)) {
				String[] expressions = expression.split(",");
				for (String ep : expressions) {
					if(ep!=null)
						ep=ep.trim();
					per.add(MapUtil.get(parameter, ep));
				}
			}
			break;

		case "intelligent":
			try {
				if (expression != null&&!StringUtil.isEmptys(expression)) {
					Map e = JSONUtil.parserStrToMap(expression);
					String conjunction = MapUtil.getString(e, "conjunction");
					List<Map> filters = (List<Map>) e.get("filters");
					if(filters!=null&&filters.size()>0){
						StringBuffer tsb = new StringBuffer();
						for (Map object : filters) {
							String ruleValue = (String) object.get("rule");
							String service_event_config_id = (String) object.get("service_event_config_id");
							Object value =  object.get("value");
							String ruleTp = (String) cacheService.get("system_data_type_rule", ruleValue);
							String config_column = (String) cacheService.get("system_data_filter_column", service_event_config_id);
							tsb.append(" ").append(ruleTp.replaceAll("\\{column\\}", config_column));
							tsb.append(" ").append(conjunction);
							per.add(value);
							logger.debug("ruleValue:{} service_event_config_id:{} value:{} ruleTp:{} config_column:{}", ruleValue,service_event_config_id,value,ruleTp,config_column);
						}
						tsb.delete(tsb.lastIndexOf(conjunction), tsb.length());

						StringBuffer tsbs = new StringBuffer(content);
						if(tsb.length()>0){
							int index = tsbs.indexOf("where");
							if(index<0){
								tsbs.append(" where ");
							}else{
								tsbs.append(" and ");
							}
							tsbs.append(tsb);
						}
						content=tsbs.toString();
					}
				}
			} catch (Throwable e) {}
			try {
				String conjunction = MapUtil.getString(parameter, "conjunction");
				List<Map> filters = (List<Map>) parameter.get("filters");
				if(filters!=null&&filters.size()>0){
					StringBuffer tsb = new StringBuffer();
					for (Map object : filters) {
						String ruleValue = (String) object.get("rule");
						String service_event_config_id = (String) object.get("service_event_config_id");
						Object value =  object.get("value");
						String ruleTp = (String) cacheService.get("system_data_type_rule", ruleValue);
						String config_column = (String) cacheService.get("system_data_filter_column", service_event_config_id);
						tsb.append(" ").append(ruleTp.replaceAll("\\{column\\}", config_column));
						tsb.append(" ").append(conjunction);
						per.add(value);
						logger.debug("ruleValue:{} service_event_config_id:{} value:{} ruleTp:{} config_column:{}", ruleValue,service_event_config_id,value,ruleTp,config_column);
					}
					tsb.delete(tsb.lastIndexOf(conjunction), tsb.length());

					StringBuffer tsbs = new StringBuffer(content);
					if(tsb.length()>0){
						int index = tsbs.indexOf("where");
						if(index<0){
							tsbs.append(" where ");
						}else{
							tsbs.append(" and ");
						}
						tsbs.append("(").append(tsb).append(")");
					}
					content=tsbs.toString();
				}
			} catch (Throwable e) {}
			break;

		default:
			break;
		}

		Map rtn = new HashMap<>();
		rtn.put(EventField.contentList, per);
		rtn.put(EventField.content, content);
		rtn.put(EventField.event_type, event_type);
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
}
