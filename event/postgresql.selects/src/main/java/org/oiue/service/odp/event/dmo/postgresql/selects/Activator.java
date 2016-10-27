package org.oiue.service.odp.event.dmo.postgresql.selects;

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
                factoryService.unRegisterDmo("selects", "postgresql");
            }

            @Override
            public void addingService() {
                LogService logService = getService(LogService.class);
                Event eventService = new SelectsEventService(logService);

                factoryService = getService(FactoryService.class);
                factoryService.registerDmo("selects", "postgresql", eventService);
            }

            @Override
            public void updated(Dictionary<String, ?> props) {

            }
        }, LogService.class, FactoryService.class);
    }

    @Override
    public void stop() throws Exception {}
}
