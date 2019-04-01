package org.oiue.service.odp.res.event.resultfilter.convert2tree;

import java.util.List;
import java.util.Map;

import org.oiue.service.log.LogService;
import org.oiue.service.log.Logger;
import org.oiue.service.odp.dmo.CallBack;
import org.oiue.service.odp.event.api.EventResultFilter;
import org.oiue.tools.StatusResult;
import org.oiue.tools.list.ListUtil;
import org.oiue.tools.map.MapUtil;

@SuppressWarnings("serial")
public class ConvertToTree implements EventResultFilter {
	
	protected static String requestEvent = "convertToTree";
	private static Logger logger;
	
	public ConvertToTree(LogService logService) {
		logger = logService.getLogger(getClass());
	}
	
	@Override
	public StatusResult doFilter(Object rtnObject, Map event, String data_source_name, Map map, CallBack callBack) {
		if (logger.isDebugEnabled()) {
			logger.debug("event:" + event + ",map:" + map + ",rtnObject:" + rtnObject);
		}
		StatusResult afr = new StatusResult();
		
		String event_id = MapUtil.getString(event, "service_event_id");
		if ("fm_system_query_childmenu_tree".equals(event_id)) {
			String menu_id = MapUtil.getString(map, "menu_id");
			Object rtnObjectt = ListUtil.convertToTree((List<Map>) rtnObject, menu_id, "id", "parent_id", "childs", "sort");
			((List<Map>) rtnObject).clear();
			((List<Map>) rtnObject).addAll((List<Map>) rtnObjectt);
		}
		
		afr.setResult(StatusResult._SUCCESS);
		return afr;
	}
	
	public void updated(Map<String, ?> props) {
		
	}
}
