package org.oiue.service.odp.structure.api;

import java.util.Map;

import org.oiue.service.odp.bmo.IBMO;

@SuppressWarnings("rawtypes")
public interface IServicesEvent extends IBMO {
	void insertServiceEvent(Map data);
	
	void updateServiceEvent(Map data);
	
	void selectEventInfo(Map data);
	
	void createServiceEvent(Map data);
	
	Object inorUpServiceEvent(Map data, Map event, String tokenid);
	
	Object testServiceEvent(Map data, Map event, String tokenid);
}