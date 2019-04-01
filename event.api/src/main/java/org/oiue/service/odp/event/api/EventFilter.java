package org.oiue.service.odp.event.api;

import java.io.Serializable;
import java.util.Map;

import org.oiue.service.odp.dmo.CallBack;
import org.oiue.tools.StatusResult;

@SuppressWarnings("rawtypes")
public interface EventFilter extends Serializable {
	StatusResult doFilter(Map event, String data_source_name, Map map, CallBack callBack);
}
