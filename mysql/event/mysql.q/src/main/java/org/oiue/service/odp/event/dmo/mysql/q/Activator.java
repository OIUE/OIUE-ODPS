package org.oiue.service.odp.event.dmo.mysql.q;

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
				factoryService.unRegisterDmo("query", "mysql");
				factoryService.unRegisterDmo("insert", "mysql");
				factoryService.unRegisterDmo("update", "mysql");
				factoryService.unRegisterDmo("delete", "mysql");
			}
			
			@Override
			public void addingService() {
				LogService logService = getService(LogService.class);
				EventMysqlService eventMysqlService = new EventMysqlService(logService);
				
				factoryService = getService(FactoryService.class);
				factoryService.registerDmo("query", "mysql", eventMysqlService);
				factoryService.registerDmo("insert", "mysql", eventMysqlService);
				factoryService.registerDmo("update", "mysql", eventMysqlService);
				factoryService.registerDmo("delete", "mysql", eventMysqlService);
			}
			
			@Override
			public void updatedConf(Map<String, ?> props) {
			
			}
		}, LogService.class, FactoryService.class);
	}
	
	@Override
	public void stop() {}
}
