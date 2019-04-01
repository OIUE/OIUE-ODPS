package org.oiue.service.odp.event.sql.structure.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.oiue.service.cache.CacheServiceManager;
import org.oiue.service.log.LogService;
import org.oiue.service.log.Logger;
import org.oiue.service.odp.event.api.EventField;
import org.oiue.service.odp.event.sql.structure.StructureService;
import org.oiue.service.odp.event.sql.structure.StructureServiceManager;
import org.oiue.service.system.analyzer.AnalyzerService;
import org.oiue.service.system.analyzer.TimeLogger;
import org.oiue.tools.json.JSONUtil;
import org.oiue.tools.list.ListUtil;
import org.oiue.tools.map.MapUtil;
import org.oiue.tools.string.StringUtil;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

@SuppressWarnings({ "rawtypes", "serial", "unused" })
public class StructureServiceManagerImpl implements Serializable, StructureServiceManager {
	
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
		String rule = MapUtil.getString((Map<String, Object>) event, "rule");
		String content = MapUtil.getString((Map<String, Object>) event, "content");
		String expression = MapUtil.getString((Map<String, Object>) event, "expression");
		String event_type = MapUtil.getString((Map<String, Object>) event, "event_type");
		
		if (rule == null || StringUtil.isEmptys(rule))
			rule = "";
		
		List per = new ArrayList<>();
		switch (rule) {
			case "tradition":
				if (expression != null && !StringUtil.isEmptys(expression)) {
					String[] expressions = expression.split(",");
					for (String ep : expressions) {
						if (ep != null)
							ep = ep.trim();
						per.add(MapUtil.get(parameter, ep));
					}
				}
				break;
			
			case "intelligent":
				StringBuffer filtersb = null;
				Map<String, Object> replacem = new HashMap<>();
				try {
					if (expression != null && !StringUtil.isEmptys(expression)) {
						Map e = JSONUtil.parserStrToMap(expression);
						String conjunction = MapUtil.getString(e, "conjunction");
						List<Map> filters = (List<Map>) e.get("filters");
						if (filters != null && filters.size() > 0) {
							StringBuffer tsb = this.convert(filters, conjunction, per, replacem);
							if (tsb.length() > 0)
								filtersb = tsb;
						}
					}
				} catch (Throwable e) {}
				try {
					String conjunction = MapUtil.getString(parameter, "conjunction");
					List<Map> filters = (List<Map>) parameter.get("filters");
					if (filters != null && filters.size() > 0) {
						StringBuffer tsb = this.convert(filters, conjunction, per, replacem);
						StringBuffer tfsb = new StringBuffer();
						if (tsb.length() > 0) {
							if (filtersb != null) {
								tfsb.append("(").append(filtersb).append(")");
								tfsb.append(" and ").append("(").append(tsb).append(")");
							} else
								tfsb = tsb;
						}
						if (tfsb.length() > 0)
							filtersb = tfsb;
					}
				} catch (Throwable e) {}
				if (filtersb != null) {
					StringBuffer tsbs = new StringBuffer(content.replaceAll("[\\t\\n\\r]", " ").replaceAll("\\s+", " "));
					int index_where = tsbs.toString().toLowerCase().indexOf("where");
					int index_group = tsbs.toString().toLowerCase().indexOf("group");
					int index_order = tsbs.toString().toLowerCase().indexOf("order");
					if (index_group > 0 || index_order > 0) {
						int index = index_group < index_order && index_group > 0 ? index_group : index_order;
						tsbs.insert(index, ") ").insert(index, filtersb).insert(index, "(").insert(index, index_where < 0 ? "where " : "and ");
					} else {
						tsbs.append(index_where < 0 ? " where " : " and ").append("(").append(filtersb).append(")");
					}
					content = tsbs.toString();
				}
				if (replacem.size() > 0) {
					for (Entry<String, Object> entry : replacem.entrySet()) {
						content = content.replace("{" + entry.getKey() + "}", "" + entry.getValue());
					}
				}
				break;
			case "_intelligent":
				StringBuffer _filtersb = null;
				Map<String, Object> _replacem = new HashMap<>();
				try {
					String conjunction = "and";
					List<Map> filters = (List<Map>) parameter.get("and");
					if (filters != null && filters.size() > 0) {
						StringBuffer tsb = this.convert(filters, conjunction, per, _replacem);
						if (tsb.length() > 0)
							_filtersb = tsb;
					}
				} catch (Throwable e) {}
				try {
					String conjunction = "or";
					List<Map> filters = (List<Map>) parameter.get("or");
					if (filters != null && filters.size() > 0) {
						StringBuffer tsb = this.convert(filters, conjunction, per, _replacem);
						StringBuffer tfsb = new StringBuffer();
						if (tsb.length() > 0) {
							if (_filtersb != null) {
								tfsb.append("(").append(_filtersb).append(")");
								tfsb.append(" and ").append("(").append(tsb).append(")");
							} else
								tfsb = tsb;
						}
						if (tfsb.length() > 0)
							_filtersb = tfsb;
					}
				} catch (Throwable e) {}
				if (_filtersb != null) {
					StringBuffer tsbs = new StringBuffer(content.replaceAll("[\\t\\n\\r]", " ").replaceAll("\\s+", " "));
					int index_where = tsbs.toString().toLowerCase().indexOf("where");
					int index_group = tsbs.toString().toLowerCase().indexOf("group");
					int index_order = tsbs.toString().toLowerCase().indexOf("order");
					if (index_group > 0 || index_order > 0) {
						int index = index_group < index_order ? index_group : index_order;
						tsbs.insert(index, ") ").insert(index, _filtersb).insert(index, "(").insert(index, index_where < 0 ? "where " : "and ");
					} else {
						tsbs.append(index_where < 0 ? " where " : " and ").append("(").append(_filtersb).append(")");
					}
					content = tsbs.toString();
				}
				if (_replacem.size() > 0) {
					for (Entry<String, Object> entry : _replacem.entrySet()) {
						content = content.replace("{" + entry.getKey() + "}", "" + entry.getValue());
					}
				}
				break;
			
			default:
				StringBuffer filtrationsb = null;
				Map<String, Object> freplacem = new HashMap<>();
				
				if (expression != null && !StringUtil.isEmptys(expression)) {
					String[] expressions = expression.split(",");
					try {
						for (String ep : expressions) {
							if (ep != null)
								ep = ep.trim();
							per.add(MapUtil.get(parameter, ep));
						}
						
					} catch (Exception e) {
						logger.error(e.getMessage() + " parameter:" + parameter + " expressions:" + expressions, e);
					}
				}
				
				String filtration = MapUtil.getString((Map<String, Object>) event, "filtration");
				if (filtration != null && !StringUtil.isEmptys(filtration)) {
					List filtrations = JSONUtil.parserStrToList(filtration);
					try {
						if (filtrations != null && filtrations.size() > 0) {
							StringBuffer tsb = this.convert(filtrations, per, freplacem);
							if (tsb.length() > 0)
								filtrationsb = tsb;
						}
					} catch (Throwable e) {}
				}
				
				try {
					String conjunction = MapUtil.getString(parameter, "conjunction");
					List<Map> filters = (List<Map>) parameter.get("filters");
					if (filters != null && filters.size() > 0) {
						StringBuffer tsb = this.convert(filters, "not".equals(conjunction)?"or":conjunction, per, freplacem);
						StringBuffer tfsb = new StringBuffer();
						if (tsb.length() > 0) {
							if (filtrationsb != null) {
								tfsb.append("(").append(filtrationsb).append(")");
								tfsb.append(" and ").append("not".equals(conjunction)?conjunction:"").append(" (").append(tsb).append(")");
							} else {
								if("not".equals(conjunction))tsb.insert(0, "not (").append(")");
								tfsb = tsb;
							}
						}
						if (tfsb.length() > 0)
							filtrationsb = tfsb;
					}
				} catch (Throwable e) {
					logger.error(e.getMessage(), e);
				}
				if (filtrationsb != null) {
					StringBuffer tsbs = new StringBuffer(content.replaceAll("[\\t\\n\\r]", " ").replaceAll("\\s+", " "));
					// 子查询处理
					String[] froms = tsbs.toString().split("from");
					int index_where = 0;
					int index_group = 0;
					int index_order = 0;
					switch (froms.length) {
						case 1:
							index_where = tsbs.toString().toLowerCase().lastIndexOf("where");
							index_group = tsbs.toString().toLowerCase().indexOf("group", index_where);
							index_order = tsbs.toString().toLowerCase().indexOf("order", index_where);
							if (index_group > 0 || index_order > 0) {
								int index = index_group < index_order && index_group > 0 ? index_group : index_order;
								tsbs.insert(index, ") ").insert(index, filtrationsb).insert(index, "(").insert(index, index_where < 0 ? "where " : "and ");
							} else {
								tsbs.append(index_where < 0 ? " where " : " and ").append("(").append(filtrationsb).append(")");
							}
							content = tsbs.toString();
							break;
						
						default:
							if (froms.length > 1) {
								int start = froms[0].indexOf("(");
								int end = froms[froms.length - 1].indexOf("(");
								if (start > 0 && end > 0) { // 前后都有子查询
									tsbs.insert(0, "select * from (");
									tsbs.append(") t where ").append(filtrationsb);
									content = tsbs.toString();
								} else if (start > 0) { // 前面有子查询
									index_where = tsbs.toString().toLowerCase().lastIndexOf("where");
									index_group = tsbs.toString().toLowerCase().indexOf("group", index_where);
									index_order = tsbs.toString().toLowerCase().indexOf("order", index_where);
									if (index_group > 0 || index_order > 0) {
										int index = index_group < index_order && index_group > 0 ? index_group : index_order;
										tsbs.insert(index, ") ").insert(index, filtrationsb).insert(index, "(").insert(index, index_where < 0 ? "where " : "and ");
									} else {
										tsbs.append(index_where < 0 ? " where " : " and ").append("(").append(filtrationsb).append(")");
									}
									content = tsbs.toString();
								} else {// if(end >0) //后面有子查询
									if (tsbs.toString().toLowerCase().indexOf("union") < 0) {
										int lasts = tsbs.toString().toLowerCase().lastIndexOf(")");
										index_where = tsbs.toString().toLowerCase().indexOf("where", lasts);
										index_group = tsbs.toString().toLowerCase().indexOf("group", index_where);
										index_order = tsbs.toString().toLowerCase().indexOf("order", index_where);
										if (index_group > 0 || index_order > 0) {
											int index = index_group < index_order && index_group > 0 ? index_group : index_order;
											tsbs.insert(index, ") ").insert(index, filtrationsb).insert(index, "(").insert(index, index_where < 0 ? "where " : "and ");
										} else {
											tsbs.append(index_where < 0 ? " where " : " and ").append("(").append(filtrationsb).append(")");
										}
										content = tsbs.toString();
									} else {
										tsbs.insert(0, "select * from (");
										tsbs.append(") t where ").append(filtrationsb);
										content = tsbs.toString();
									}
								}
							}
							break;
					}
					
				}
				if (freplacem.size() > 0) {
					for (Entry<String, Object> entry : freplacem.entrySet()) {
						content = content.replace("{" + entry.getKey() + "}", "" + entry.getValue());
					}
				}
				break;
		}
		
		Map rtn = new HashMap<>();
		rtn.put(EventField.contentList, per);
		rtn.put(EventField.content, content);
		rtn.put(EventField.event_type, event_type);
		rtn.put("getData", event.get("getData"));
		rtnList.add(rtn);
		
		return rtnList;
	}
	
	@SuppressWarnings("unchecked")
	private StringBuffer convert(List<Map> filtrations, List<Object> per, Map<String, Object> replacem) {
		StringBuffer tsb = new StringBuffer();
		String conjunction = "";
		for (Map object : filtrations) {
			String data_type_rule_id = MapUtil.getString(object, "data_type_rule_id", "4f1392a2-491f-42b3-81ff-73f3f98e555b");
			String config_column = MapUtil.getString(object, "alias");
			conjunction = MapUtil.getString(object, "conjunction", "and");
			String data_type_id = MapUtil.getString(object, "data_type_id", "");
			Object value = null;
			switch (data_type_id) {
				case "postgres_integer":
					value = MapUtil.getInt(object, "content");
					break;
				case "postgres_bigint":
					value = MapUtil.getLong(object, "content");
					break;
				case "postgres_numeric":
				case "postgres_double_precision":
				case "postgres_real":
					value = MapUtil.getDouble(object, "content");
					break;
				
				default:
					value = object.get("content");
					break;
			}
			
			String ruleTp = (String) cacheService.get("system_data_type_rule", data_type_rule_id);
			
			if ("596dd14b-a9e1-4344-90c0-dce71a530c2e".equals(data_type_rule_id)) {// between and
				if (value instanceof Map) {
					if("not".equals(conjunction)) {
						tsb.append(" not ");
						conjunction="and";
					}
					tsb.append(" ").append(ruleTp.replaceAll("\\{column\\}", config_column));
					tsb.append(" ").append(conjunction);
					per.add(((Map) value).get("min"));
					per.add(((Map) value).get("max"));
				} else if (value instanceof List) {
					if("not".equals(conjunction)) {
						tsb.append(" not ");
						conjunction="and";
					}
					tsb.append(" ").append(ruleTp.replaceAll("\\{column\\}", config_column));
					tsb.append(" ").append(conjunction);
					per.add(((List) value).get(0));
					per.add(((List) value).get(1));
				}
			} else if ("b7599da3-26e6-4c6c-a712-15d9f248da63".equals(data_type_rule_id)) {// in
				if (value instanceof List) {
					List values = new ArrayList<>();
					List pers = new ArrayList<>();
					for (Object vo : (List) value) {
						values.add("?");
						pers.add(vo);
					}
					if (values.size() > 0) {
						if("not".equals(conjunction)) {
							tsb.append(" not ");
							conjunction="and";
						}
						tsb.append(" ").append(ruleTp.replaceAll("\\{column\\}", config_column).replaceAll("\\{values\\}", "(" + ListUtil.ListJoin(values, ",") + ")"));
						tsb.append(" ").append(conjunction);
						per.addAll(pers);
					}
				}
			} else if ("ef27a63c-5113-4e39-aa38-16918324235a".equals(data_type_rule_id)) {// replace
				replacem.put(config_column, value);
			} else {
				if("not".equals(conjunction)) {
					tsb.append(" not ");
					conjunction="and";
				}
				tsb.append(" ").append(ruleTp.replaceAll("\\{column\\}", config_column));
				tsb.append(" ").append(conjunction);
				per.add(value);
			}
			logger.debug("ruleValue:{} config_column:{} value:{} ruleTp:{} ", data_type_rule_id, config_column, value, ruleTp);
		}
		tsb.delete(tsb.lastIndexOf(conjunction), tsb.length());
		return tsb;
	}
	
	@SuppressWarnings("unchecked")
	private StringBuffer convert(List<Map> filters, String conjunction, List<Object> per, Map<String, Object> replacem) {
		StringBuffer tsb = new StringBuffer();
		for (Map object : filters) {
			String ruleValue = (String) object.get("rule");
			String service_event_config_id = (String) object.get("service_event_config_id");
			Object value = object.get("value");
			String ruleTp = (String) cacheService.get("system_data_type_rule", ruleValue);
			String config_column = (String) cacheService.get("system_data_filter_column", service_event_config_id);
			if ("596dd14b-a9e1-4344-90c0-dce71a530c2e".equals(ruleValue)) {// between and
				if (value instanceof Map) {
					tsb.append(" ").append(ruleTp.replaceAll("\\{column\\}", config_column));
					tsb.append(" ").append(conjunction);
					per.add(((Map) value).get("min"));
					per.add(((Map) value).get("max"));
				} else if (value instanceof List) {
					tsb.append(" ").append(ruleTp.replaceAll("\\{column\\}", config_column));
					tsb.append(" ").append(conjunction);
					per.add(((List) value).get(0));
					per.add(((List) value).get(1));
				}
			} else if ("b7599da3-26e6-4c6c-a712-15d9f248da63".equals(ruleValue)) {// in
				if (value instanceof List) {
					List values = new ArrayList<>();
					List pers = new ArrayList<>();
					for (Object vo : (List) value) {
						values.add("?");
						pers.add(vo);
					}
					if (values.size() > 0) {
						tsb.append(" ").append(ruleTp.replaceAll("\\{column\\}", config_column).replaceAll("\\{values\\}", "(" + ListUtil.ListJoin(values, ",") + ")"));
						tsb.append(" ").append(conjunction);
						per.addAll(pers);
					}
				}
			} else if ("ef27a63c-5113-4e39-aa38-16918324235a".equals(ruleValue)) {// replace
				replacem.put(config_column, value);
			} else {
				tsb.append(" ").append(ruleTp.replaceAll("\\{column\\}", config_column));
				tsb.append(" ").append(conjunction);
				per.add(value);
			}
			logger.debug("ruleValue:{} service_event_config_id:{} value:{} ruleTp:{} config_column:{}", ruleValue, service_event_config_id, value, ruleTp, config_column);
		}
		if (tsb.length() > 0)
			tsb.delete(tsb.lastIndexOf(conjunction), tsb.length());
		return tsb;
	}
	
	@Override
	public StructureService getStructureService(String type) {
		return default_strucures.get(type);
	}
	
	@Override
	public StructureService getStructureService(String DBType, String type) {
		return DBType == null ? this.getStructureService(type) : strucures.get(DBType).get(type);
	}
}
