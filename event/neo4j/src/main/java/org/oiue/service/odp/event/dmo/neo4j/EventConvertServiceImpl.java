package org.oiue.service.odp.event.dmo.neo4j;

import java.util.List;
import java.util.Map;

import org.oiue.service.odp.dmo.DMO;
import org.oiue.service.odp.dmo.IDMO;
import org.oiue.service.odp.event.api.EventConvertService;

@SuppressWarnings("serial")
public class EventConvertServiceImpl extends DMO implements EventConvertService {
	@Override
	public List<Map<?, ?>> convert(Map<?, ?> event, Map<String, Object> data) throws Throwable {
		return null;
	}

	@Override
	public Map<?, ?> query(Map<?, ?> event_query) throws Throwable {
		return null;
	}

	@Override
	public IDMO clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return new EventConvertServiceImpl();
	}

}