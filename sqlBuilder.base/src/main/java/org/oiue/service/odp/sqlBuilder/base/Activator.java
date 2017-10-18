package org.oiue.service.odp.sqlBuilder.base;

import java.util.Dictionary;

import org.oiue.service.log.LogService;
import org.oiue.service.odp.base.FactoryService;
import org.oiue.service.osgi.FrameActivator;
import org.oiue.service.osgi.MulitServiceTrackerCustomizer;
import org.oiue.service.sql.SqlService;

public class Activator extends FrameActivator {

	@Override
	public void start() throws Exception {
		this.start(new MulitServiceTrackerCustomizer() {
			private FactoryService factoryService;

			@Override
			public void removedService() {}

			@SuppressWarnings("unused")
			@Override
			public void addingService() {
				LogService logService = getService(LogService.class);
				SqlService sqlService = getService(SqlService.class);

				registerService(FactoryService.class, factoryService);
			}

			@Override
			public void updated(Dictionary<String, ?> props) {

			}
		}, LogService.class, SqlService.class);
	}

	@Override
	public void stop() throws Exception {}
}
