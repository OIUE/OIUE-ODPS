package org.oiue.service.odp.bmo;

import java.util.UUID;

import org.oiue.service.odp.proxy.ProxyFactory;

/**
 * 业务操作抽象类
 *
 * @author Every{王勤}
 */
@SuppressWarnings("serial")
public class BMO implements IBMO {
	private ProxyFactory proxyFactory = ProxyFactory.getInstance();
	
	// 自身对象的唯一标识
	private final String uniqueIdentifier = UUID.randomUUID() + "";
	// 调用者的唯一标识
	private String callerUID = null;
	// 根调用者唯一标识
	private String callerRoot = null;
	// 默认使用的连接
	private String default_connName;
	
	@Override
	public String getUniqueIdentifier() {
		return uniqueIdentifier;
	}
	
	@Override
	public void setCallerUID(String callerUID) {
		this.callerUID = callerUID;
	}
	
	@Override
	public String getCallerUID() {
		return callerUID;
	}
	
	@Override
	public void setCallerRoot(String callerRoot) {
		this.callerRoot = callerRoot;
	}
	
	@Override
	public String getCallerRoot() {
		return callerRoot;
	}
	
	@Override
	public String getDefault_connName() {
		return default_connName;
	}
	
	@Override
	public void setDefault_connName(String default_connName) {
		this.default_connName = default_connName;
	}
	
	@Override
	public void setConn(String connName) {
		this.setDefault_connName(connName);
	}
	
	@Override
	public String toString() {
		return "uniqueIdentifier:" + uniqueIdentifier + "\tcallerRoot:" + callerRoot + "\n" + super.toString();
	}
	
	/**
	 * 获取持久层操作对象实例
	 *
	 * @param name 名称
	 * @param o 参数集合
	 * @return 持久层对象
	 */
	public final <T> T getIDMO(String name, Object... o) {
		return proxyFactory.getIDMO(name, this.getDefault_connName(), this.getCallerRoot() == null ? this.getUniqueIdentifier() : this.getCallerRoot(), o);
	}
	
	/**
	 * 获取持久层操作对象实例
	 *
	 * @param name 名称
	 * @param connName 连接名
	 * @param o 参数集合
	 * @return 持久层对象
	 */
	public final <T> T getIDMO(String name, String connName, Object... o) {
		return proxyFactory.getOp().getIDMOByConn(name, connName, this.getCallerRoot() == null ? this.getUniqueIdentifier() : this.getCallerRoot(), o);
	}
	
	/**
	 * 获取持久层操作对象实例
	 *
	 * @param name 名称
	 * @param data_type_class 连接类型
	 * @param o 参数集合
	 * @return 持久层对象
	 */
	public final <T> T getIDMOByDsType(String name, String data_type_class, Object... o) {
		T idmo = proxyFactory.getOp().getIDMO(name, data_type_class, o);
		return idmo;
	}
	
	// /**
	// * 获取持久层操作对象实例 此方法代理简单数据操作
	// *
	// * @param name 名称
	// * @param tModel 对象
	// * @param o 参数集合
	// * @return 持久层对象
	// */
	// public IDMO getIDMOProxy(String name, TableModel tModel, Object... o) {
	// return proxyFactory.getIDMOProxy(name, this.getDefault_connName(), tModel, this.getCallerUID() == null ? this.getUniqueIdentifier() : this.getCallerUID(), o);
	// }
	
	/**
	 * 获取嵌套内部事务调用对象 调用此方法获取对象，将继承顶层事务的连接 此方法用于处理事务嵌套
	 *
	 * @param name 名称
	 * @param o 参数集合
	 * @return 业务层对象
	 */
	public <T> T getIBMO(String name, Object... o) {
		T ibmo;
		if (o != null && o.length > 0) {
			ibmo = proxyFactory.factory(name, false, o);
		} else {
			ibmo = proxyFactory.factory(name, false);
		}
		((IBMO) ibmo).setCallerUID(this.getUniqueIdentifier());
		((IBMO) ibmo).setCallerRoot(this.getCallerRoot() == null ? this.getUniqueIdentifier() : this.getCallerRoot());
		return (T) ibmo;
	}
	
	public <T> T getIBMO(String name, String processKey, Object... o) {
		T ibmo;
		if (o != null && o.length > 0) {
			ibmo = proxyFactory.factory(name, false, o);
		} else {
			ibmo = proxyFactory.factory(name, false);
		}
		((IBMO) ibmo).setCallerUID(this.getUniqueIdentifier());
		((IBMO) ibmo).setCallerRoot(this.getCallerRoot() == null ? this.getUniqueIdentifier() : this.getCallerRoot());
		return (T) ibmo;
	}
}
