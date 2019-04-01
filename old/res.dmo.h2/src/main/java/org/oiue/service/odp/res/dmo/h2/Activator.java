package org.oiue.service.odp.res.dmo.h2;

import java.util.Map;

import org.oiue.service.log.LogService;
import org.oiue.service.odp.base.FactoryService;
import org.oiue.service.odp.res.dmo.IRes;
import org.oiue.service.osgi.FrameActivator;
import org.oiue.service.osgi.MulitServiceTrackerCustomizer;

public class Activator extends FrameActivator {
	
	@Override
	public void start() {
		this.start(new MulitServiceTrackerCustomizer() {
			FactoryService factoryService;
			
			@Override
			public void removedService() {
				factoryService.unRegisterDmo(IRes.class.getName(), "h2");
			}
			
			@Override
			public void addingService() {
				LogService logService = getService(LogService.class);
				
				factoryService = getService(FactoryService.class);
				
				factoryService.registerDmo(IRes.class.getName(), "h2", new Res());
				
				logService.getLogger(this.getClass()).info("register Dmo:" + IRes.class + ",DBType :h2");
			}
			
			@Override
			public void updatedConf(Map<String, ?> props) {
			
			}
		}, LogService.class, FactoryService.class);
	}
	
	@Override
	public void stop() {}
}
