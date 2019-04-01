package org.oiue.service.odp.event.dmo.mysql.t;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface EventDmo extends Serializable {
	@SuppressWarnings("rawtypes")
	public Map<?, ?> resolve(List<Map> resultList, Map<?, ?> paramMap);
	
	@SuppressWarnings("rawtypes")
	public Map<?, ?> resolve(Map resultList, Map<?, ?> paramMap);
}
