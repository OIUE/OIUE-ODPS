package org.oiue.service.odp.event.dmo.postgresql.select;

import java.util.Dictionary;

import org.oiue.service.log.LogService;
import org.oiue.service.odp.base.FactoryService;
import org.oiue.service.odp.event.api.Event;
import org.oiue.service.osgi.FrameActivator;
import org.oiue.service.osgi.MulitServiceTrackerCustomizer;


public class Activator extends FrameActivator {

	@Override
	public void start() throws Exception {
		this.start(new MulitServiceTrackerCustomizer() {
			private FactoryService factoryService;

			@Override
			public void removedService() {
				factoryService.unRegisterDmo("select", "postgresql");
			}

			@Override
			public void addingService() {
				LogService logService = getService(LogService.class);
				Event eventMysqlService = new SelectEventPostgresqlService(logService);

				factoryService = getService(FactoryService.class);
				factoryService.registerDmo("select", "postgresql", eventMysqlService);
				factoryService.registerDmo("call", "postgresql", eventMysqlService);
			}

			@Override
			public void updated(Dictionary<String, ?> props) {

			}
		}, LogService.class, FactoryService.class);
	}

	@Override
	public void stop() throws Exception {}
}
