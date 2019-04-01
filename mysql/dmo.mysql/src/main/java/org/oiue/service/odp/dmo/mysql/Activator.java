package org.oiue.service.odp.dmo.mysql;

import java.util.Map;

import org.oiue.service.log.LogService;
import org.oiue.service.odp.base.FactoryService;
import org.oiue.service.osgi.FrameActivator;
import org.oiue.service.osgi.MulitServiceTrackerCustomizer;

public class Activator extends FrameActivator {
	
	@Override
	public void start() {
		this.start(new MulitServiceTrackerCustomizer() {
			
			@Override
			public void removedService() {}
			
			@Override
			public void addingService() {
				FactoryService factoryService = getService(FactoryService.class);
				factoryService.registerDmoDb("mysql", new DMO_DB());
			}
			
			@Override
			public void updatedConf(Map<String, ?> props) {}
		}, LogService.class, FactoryService.class);
	}
	
	@Override
	public void stop() {}
}