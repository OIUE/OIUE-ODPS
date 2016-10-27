package org.oiue.service.odp.event.dmo.mysql.t;

import java.util.Dictionary;

import org.oiue.service.log.LogService;
import org.oiue.service.osgi.FrameActivator;
import org.oiue.service.osgi.MulitServiceTrackerCustomizer;

public class Activator extends FrameActivator {

    @Override
    public void start() throws Exception {
        this.start(new MulitServiceTrackerCustomizer() {
            private EventDmo dmoMysqlService;

            @Override
            public void removedService() {}

            @Override
            public void addingService() {
                LogService logService = getService(LogService.class);
                dmoMysqlService = new DmoMysqlService(logService);
                registerService(EventDmo.class, dmoMysqlService);
            }

            @Override
            public void updated(Dictionary<String, ?> props) {

            }
        }, LogService.class);
    }

    @Override
    public void stop() throws Exception {}
}