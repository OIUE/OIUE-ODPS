package org.oiue.service.odp.base;

import java.util.Map;

import org.oiue.service.log.LogService;
import org.oiue.service.odp.proxy.ProxyFactory;
import org.oiue.service.osgi.FrameActivator;
import org.oiue.service.osgi.MulitServiceTrackerCustomizer;
import org.oiue.service.sql.SqlService;
import org.oiue.service.system.analyzer.AnalyzerService;

public class Activator extends FrameActivator {
	
	@Override
	public void start() {
		this.start(new MulitServiceTrackerCustomizer() {
			private FactoryService factoryService;
			
			@Override
			public void removedService() {}
			
			@Override
			public void addingService() {
				LogService logService = getService(LogService.class);
				AnalyzerService analyzerService = getService(AnalyzerService.class);
				SqlService sqlService = getService(SqlService.class);
				
				ProxyFactory pf = ProxyFactory.getInstance();
				pf.getOp().setDs(new ProxyDBSourceImpl(sqlService, logService));
				pf.setLogService(logService, analyzerService);
				
				factoryService = new FactoryServiceImpl(pf);
				
				registerService(FactoryService.class, factoryService);
			}
			
			@Override
			public void updatedConf(Map<String, ?> props) {
			
			}
		}, LogService.class, SqlService.class, AnalyzerService.class);
	}
	
	@Override
	public void stop() {}
}
