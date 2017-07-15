package org.oiue.service.odp.res.event.resultfilter.convert2tree;

import java.util.Dictionary;

import org.oiue.service.log.LogService;
import org.oiue.service.odp.res.api.IResource;
import org.oiue.service.osgi.FrameActivator;
import org.oiue.service.osgi.MulitServiceTrackerCustomizer;

public class Activator extends FrameActivator {

	@Override
	public void start() throws Exception {
		this.start(new MulitServiceTrackerCustomizer() {
			ConvertToTree eventResultFilter;
			@Override
			public void removedService() {
			}

			@Override
			public void addingService() {
				LogService logService = getService(LogService.class);
				IResource iResource  = getService(IResource.class);
				
				eventResultFilter = new ConvertToTree(logService);
				iResource.registerEventResultFilter(ConvertToTree.requestEvent, eventResultFilter, 1);
			}

			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public void updated(Dictionary<String, ?> props) {
				try {
					eventResultFilter.updated(props);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}, LogService.class, IResource.class);
	}

	@Override
	public void stop() throws Exception {
	}
}