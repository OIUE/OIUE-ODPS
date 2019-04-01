package org.oiue.service.odp.event.dmo.neo4j.insert;

import java.util.Map;

import org.oiue.service.log.LogService;
import org.oiue.service.odp.base.FactoryService;
import org.oiue.service.osgi.FrameActivator;
import org.oiue.service.osgi.MulitServiceTrackerCustomizer;

public class Activator extends FrameActivator {
	
	@Override
	public void start() {
		this.start(new MulitServiceTrackerCustomizer() {
			FactoryService factoryService;
			
			@Override
			public void removedService() {
				factoryService.unRegisterDmo("insert", "neo4j");
			}
			
			@Override
			public void addingService() {
				LogService logService = getService(LogService.class);
				EventNeo4jService eventNeo4jService = new EventNeo4jService(logService);
				
				factoryService = getService(FactoryService.class);
				factoryService.registerDmo("insert", "neo4j", eventNeo4jService);
			}
			
			@Override
			public void updatedConf(Map<String, ?> props) {
			
			}
		}, LogService.class, FactoryService.class);
	}
	
	@Override
	public void stop() {}
}
