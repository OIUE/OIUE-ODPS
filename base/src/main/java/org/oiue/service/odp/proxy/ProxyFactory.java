package org.oiue.service.odp.proxy;

import java.lang.reflect.Proxy;

import org.oiue.service.log.LogService;
import org.oiue.service.log.Logger;
import org.oiue.service.odp.bmo.IBMO;
import org.oiue.service.odp.dmo.IDMO;
import org.oiue.service.odp.objpool.ObjectPools;
import org.oiue.service.system.analyzer.AnalyzerService;
import org.oiue.service.system.analyzer.TimeLogger;
import org.oiue.table.structure.TableModel;
import org.oiue.tools.StatusResult;
import org.oiue.tools.exception.OIUEException;

/**
 * 代理工厂 解决事务等
 *
 * @author Every(王勤) E-mail/MSN:mwgjkf@hotmail.com QQ:30130942
 * @version ProxyFactory.java Apr 27, 2010
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ProxyFactory {
	private static ProxyFactory proxyFactory = new ProxyFactory();
	
	private LogService logService;
	private AnalyzerService analyzer;
	
	public void setLogService(LogService logService, AnalyzerService analyzer) {
		this.logService = logService;
		this.analyzer = analyzer;
	}
	
	public Logger getLogger(Class<?> c) {
		return logService.getLogger(c);
	}
	public LogService getLogService() {
		return logService;
	}
	
	public TimeLogger getTimeLogger(Class<?> c) {
		return analyzer.getLogger(c);
	}
	
	/**
	 * 初始化对象池
	 */
	private ObjectPools objectPools = null;
	
	public ObjectPools getOp() {
		return objectPools;
	}
	
	public void setOp(ObjectPools op) {
		this.objectPools = op;
	}
	
	/**
	 * 读取配置文件 初始化代理工厂
	 */
	private ProxyFactory() {
		objectPools = ObjectPools.getInstance();
	}
	
	/**
	 * 获取代理工厂
	 *
	 * @return 唯一代理工厂实例
	 */
	public static ProxyFactory getInstance() {
		return proxyFactory;
	}
	
	public static <T> T factorys(String name) {
		return proxyFactory.factory(name, true);
	}
	
	public <T> T factorys(String name, String processKey, Object... o) {
		IBMO ibmo;
		if (o != null && o.length > 0) {
			ibmo = this.factory(name, false, o);
		} else {
			ibmo = this.factory(name, false);
		}
		ibmo.setCallerUID(processKey);
		ibmo.setCallerRoot(processKey);
		return (T) ibmo;
	}
	
	/**
	 * 获取业务操作对象的实例 业务模型对象初始动态代理 根据对象映射中指定的名称获取操作对象的实例 此实例的方法均被代理
	 *
	 * @param name 调用的对象映射名称
	 * @param main 是否是主要的方法 对于展现层调用，则是主要方法，对于业务层调用，则不是主要方法
	 * @param o 初始参数
	 * @return 返回业务对象
	 */
	public <T> T factory(String name, boolean main, Object... o) {
		IBMO ibmo = getOp().getIBMO(name, o);
		if (main) {
			Class cls = ibmo.getClass();
			ibmo = (IBMO) Proxy.newProxyInstance(cls.getClassLoader(), cls.getInterfaces(), new BmoProxy(ibmo, name));
		}
		if (ibmo == null)
			throw new OIUEException(StatusResult._service_init_error, "service can not init！");
		return (T) ibmo;
	}
	
	/**
	 * 获取持久层操作对象 持久层对象初始动态代理 根据对象映射中指定的名称获取操作对象实例 此实例所有方法均被代理
	 *
	 * @param name 名称
	 * @param connName 连接名称
	 * @param bmoUniqueIdentifier 主业务实体类的唯一标识 此值用于唯一定位到jvm中的业务对象实体
	 * @param o 初始化参数
	 * @return 持久化对象
	 */
	public <T> T getIDMO(String name, String connName, String bmoUniqueIdentifier, Object... o) {
		IDMO object = getOp().getIDMOByConn(name, connName, bmoUniqueIdentifier, o);
		Class cls = object.getClass();
		return (T) Proxy.newProxyInstance(cls.getClassLoader(), cls.getInterfaces(), new DmoProxy(object, bmoUniqueIdentifier, o));
	}
	
	/**
	 * 获取持久层操作对象 此方法将代理简单的数据操作提交 持久层对象初始动态代理 根据对象映射中指定的名称获取操作对象实例 此实例所有方法均被代理 此方法同时代理操作查询数据
	 *
	 * @param name 名称
	 * @param connName 连接名称
	 * @param tModel 返回对象
	 * @param bmoUniqueIdentifier 主业务实体类的唯一标识 此值用于唯一定位到jvm中的业务对象实体
	 * @param o 初始化参数
	 * @return 持久化对象
	 */
	public <T> T getIDMOProxy(String name, String connName, TableModel tModel, String bmoUniqueIdentifier, Object... o) {
		IDMO object = getOp().getIDMOByConn(name, connName, bmoUniqueIdentifier, o);
		Class cls = object.getClass();
		return (T) Proxy.newProxyInstance(cls.getClassLoader(), cls.getInterfaces(), new DmoProxy(object, tModel, bmoUniqueIdentifier, o));
	}
}
