package org.oiue.service.odp.event.dmo.postgresql.q;

import java.util.Dictionary;

import org.oiue.service.log.LogService;
import org.oiue.service.odp.base.FactoryService;
import org.oiue.service.osgi.FrameActivator;
import org.oiue.service.osgi.MulitServiceTrackerCustomizer;

public class Activator extends FrameActivator {

    @Override
    public void start() throws Exception {
        this.start(new MulitServiceTrackerCustomizer() {
            FactoryService factoryService;

            @Override
            public void removedService() {
                factoryService.unRegisterDmo("insert", "postgresql");
                factoryService.unRegisterDmo("update", "postgresql");
                factoryService.unRegisterDmo("delete", "postgresql");
            }

            @Override
            public void addingService() {
                LogService logService = getService(LogService.class);
                EventPostgresqlService eventMysqlService = new EventPostgresqlService(logService);

                factoryService = getService(FactoryService.class);
                factoryService.registerDmo("insert", "postgresql", eventMysqlService);
                factoryService.registerDmo("update", "postgresql", eventMysqlService);
                factoryService.registerDmo("delete", "postgresql", eventMysqlService);
            }

            @Override
            public void updated(Dictionary<String, ?> props) {

            }
        }, LogService.class, FactoryService.class);
    }

    @Override
    public void stop() throws Exception {}
}
