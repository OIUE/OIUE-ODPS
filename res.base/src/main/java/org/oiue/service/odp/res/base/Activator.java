package org.oiue.service.odp.res.base;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.oiue.service.log.LogService;
import org.oiue.service.log.Logger;
import org.oiue.service.odp.base.FactoryService;
import org.oiue.service.odp.objpool.BmoConfig;
import org.oiue.service.odp.res.api.IResource;
import org.oiue.service.osgi.FrameActivator;
import org.oiue.service.osgi.MulitServiceTrackerCustomizer;
import org.oiue.service.system.analyzer.AnalyzerService;
import org.oiue.tools.string.StringUtil;

public class Activator extends FrameActivator {

	@Override
	public void start() throws Exception {
		this.start(new MulitServiceTrackerCustomizer() {
			ResourceImpl res;
			Logger logger;

			@Override
			public void removedService() {
			}

			@Override
			public void addingService() {
				LogService logService = getService(LogService.class);
				AnalyzerService analyzerService = getService(AnalyzerService.class);
				logger = logService.getLogger(this.getClass());
				res = new ResourceImpl(logService, analyzerService);
			}

			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public void updated(Dictionary<String, ?> props) {
				res.updated(props);

				try {
					String conn_names = (String) props.get("connNames");
					if (!StringUtil.isEmptys(conn_names)) {
						String[] conn_name = conn_names.split(",");

						List<String> conns = Arrays.asList(conn_name);

						FactoryService factoryService = getService(FactoryService.class);

						Map method_iRes = new HashMap();
						method_iRes.put("default", conns);
						BmoConfig bc_iRes = new BmoConfig(res, method_iRes);
						factoryService.registerBmo(IResource.class.getName(), bc_iRes);

						try {
							registerService(IResource.class, factoryService.getBmo(IResource.class.getName()));
							logger.info("register service:" + IResource.class);
						} catch (Throwable e) {
							logger.error("register service error:" + e.getMessage(), e);
						}
					} else {

					}

				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}, LogService.class, AnalyzerService.class, FactoryService.class);
	}

	@Override
	public void stop() throws Exception {
	}
}