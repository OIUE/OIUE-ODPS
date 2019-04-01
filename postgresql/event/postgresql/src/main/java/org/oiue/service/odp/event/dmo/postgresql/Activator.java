package org.oiue.service.odp.event.dmo.postgresql;

import java.util.Map;

import org.oiue.service.log.LogService;
import org.oiue.service.odp.base.FactoryService;
import org.oiue.service.odp.event.api.EventConvertService;
import org.oiue.service.odp.event.sql.structure.StructureService;
import org.oiue.service.osgi.FrameActivator;
import org.oiue.service.osgi.MulitServiceTrackerCustomizer;

public class Activator extends FrameActivator {
	
	@Override
	public void start() {
		this.start(new MulitServiceTrackerCustomizer() {
			private FactoryService factoryService;
			
			@Override
			public void removedService() {
				factoryService.unRegisterDmo(EventConvertService.class.getName(), "postgresql");
			}
			
			@Override
			public void addingService() {
				LogService logService = getService(LogService.class);
				StructureService structureService = getService(StructureService.class);
				
				factoryService = getService(FactoryService.class);
				EventConvertServiceImpl.structureService = structureService;
				EventConvertServiceImpl.logger = logService.getLogger(EventConvertServiceImpl.class);
				
				EventConvertService convertService = new EventConvertServiceImpl();
				factoryService.registerDmo(EventConvertService.class.getName(), "postgresql", convertService);
				
				logService.getLogger(this.getClass()).info("register Dmo:" + EventConvertService.class + ",DBType :postgresql");
			}
			
			@Override
			public void updatedConf(Map<String, ?> props) {
			
			}
		}, LogService.class, FactoryService.class, StructureService.class);
	}
	
	@Override
	public void stop() {}
}
