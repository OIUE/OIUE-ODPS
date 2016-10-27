package org.oiue.service.odp.event.sql.structure.impl;

import java.util.Dictionary;

import org.oiue.service.log.LogService;
import org.oiue.service.odp.event.sql.structure.StructureService;
import org.oiue.service.osgi.FrameActivator;
import org.oiue.service.osgi.MulitServiceTrackerCustomizer;
import org.oiue.service.system.analyzer.AnalyzerService;

public class Activator extends FrameActivator {

    @Override
    public void start() throws Exception {
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
            public void updated(Dictionary<String, ?> props) {

            }
        }, LogService.class, AnalyzerService.class);
    }

    @Override
    public void stop() throws Exception {}
}
