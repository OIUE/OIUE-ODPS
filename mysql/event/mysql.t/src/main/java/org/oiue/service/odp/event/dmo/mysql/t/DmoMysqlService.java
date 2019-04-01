package org.oiue.service.odp.event.dmo.mysql.t;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.oiue.service.log.LogService;
import org.oiue.service.log.Logger;

public class DmoMysqlService implements Serializable, EventDmo {
	
	private static final long serialVersionUID = -4714277975732691078L;
	private Logger logger;
	
	public DmoMysqlService(LogService logService) {
		logger = logService.getLogger(this.getClass());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<?, ?> resolve(List<Map> resultList, Map<?, ?> paramMap) {
		if (resultList == null || resultList.size() == 0) {
			logger.error("get an empty result");
			throw new RuntimeException("get an empty result");
		}
		String fmContent = (String) resultList.get(0).get("FM_CONTENT");
		String fmType = (String) resultList.get(0).get("FM_TYPE");
		if (fmContent == null || fmContent.isEmpty()) {
			logger.error("the type or content is null");
			throw new RuntimeException("the type or content is null");
		}
		Map map = paramMap;
		try {
			Token token = ParserFactory.creatToken(fmContent, map);
			map.put("fmSql", token.getSql());
			if (token.getCountSql() != null) {
				map.put("fmCountSql", token.getCountSql());
			}
			if (!token.getConditionList().isEmpty()) {
				map.put("fmConditionList", token.getConditionList());
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		}
		map.put("FM_TYPE", fmType);
		return map;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Map<?, ?> resolve(Map resultList, Map<?, ?> paramMap) {
		if (resultList == null || resultList.size() == 0) {
			logger.error("get an empty result");
			throw new RuntimeException("get an empty result");
		}
		String fmContent = (String) resultList.get("FM_CONTENT");
		String fmType = (String) resultList.get("FM_TYPE");
		if (fmContent == null || fmContent.isEmpty()) {
			logger.error("the type or content is null");
			throw new RuntimeException("the type or content is null");
		}
		Map map = paramMap;
		try {
			Token token = ParserFactory.creatToken(fmContent, map);
			map.put("fmSql", token.getSql());
			if (token.getCountSql() != null) {
				map.put("fmCountSql", token.getCountSql());
			}
			if (!token.getConditionList().isEmpty()) {
				map.put("fmConditionList", token.getConditionList());
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		}
		map.put("FM_TYPE", fmType);
		return map;
	}
}
