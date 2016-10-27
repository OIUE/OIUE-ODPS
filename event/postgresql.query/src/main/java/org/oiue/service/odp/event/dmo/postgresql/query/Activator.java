package org.oiue.service.odp.event.dmo.postgresql.query;

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
                factoryService.unRegisterDmo("query", "postgresql");
            }

            @Override
            public void addingService() {
                LogService logService = getService(LogService.class);
                factoryService = getService(FactoryService.class);
                Event eventMysqlService = new EventPostgresqlService(logService);
                factoryService.registerDmo("query", "postgresql", eventMysqlService);
            }

            @Override
            public void updated(Dictionary<String, ?> props) {

            }
        }, LogService.class, FactoryService.class);
    }

    @Override
    public void stop() throws Exception {}
}
