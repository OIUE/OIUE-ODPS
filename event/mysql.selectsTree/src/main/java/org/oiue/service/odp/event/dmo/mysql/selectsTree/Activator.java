package org.oiue.service.odp.event.dmo.mysql.selectsTree;

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
                factoryService.unRegisterDmo("selects", "mysql");
            }

            @Override
            public void addingService() {
                LogService logService = getService(LogService.class);
                Event eventMysqlService = new SelectEventMysqlService(logService);

                factoryService = getService(FactoryService.class);
                factoryService.registerDmo("selectsTree", "mysql", eventMysqlService);
            }

            @Override
            public void updated(Dictionary<String, ?> props) {

            }
        }, LogService.class, FactoryService.class);
    }

    @Override
    public void stop() throws Exception {}
}
