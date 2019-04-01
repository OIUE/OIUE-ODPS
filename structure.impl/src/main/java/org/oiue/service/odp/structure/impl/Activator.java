package org.oiue.service.odp.structure.impl;

import java.util.Map;

import org.oiue.service.log.LogService;
import org.oiue.service.odp.structure.StructureService;
import org.oiue.service.osgi.FrameActivator;
import org.oiue.service.osgi.MulitServiceTrackerCustomizer;
import org.oiue.service.system.analyzer.AnalyzerService;

public class Activator extends FrameActivator {
	
	@Override
	public void start() {
		this.start(new MulitServiceTrackerCustomizer() {
			@Override
			public void removedService() {}
			
			@Override
			public void addingService() {
				LogService logService = getService(LogService.class);
				AnalyzerService analyzerService = getService(AnalyzerService.class);
				
				StructureService structureService = new StructureServiceManagerImpl(logService, analyzerService);
				
				registerService(StructureService.class, structureService);
			}
			
			@Override
			public void updatedConf(Map<String, ?> props) {
			
			}
		}, LogService.class, AnalyzerService.class);
	}
	
	@Override
	public void stop() {}
}
