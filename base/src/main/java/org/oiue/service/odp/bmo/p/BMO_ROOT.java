/**
 * 
 */
package org.oiue.service.odp.bmo.p;

import java.util.UUID;

import org.oiue.service.odp.dmo.p.IDMO_ROOT;
import org.oiue.service.odp.proxy.ProxyFactory;
import org.oiue.table.structure.TableModel;

/**
 * 业务操作抽象类
 * 
 * @author Every{王勤}
 */
@SuppressWarnings({ "serial" })
public abstract class BMO_ROOT implements IBMO_ROOT {
	/**
	 * 获取对象的唯一标识
	 * 
	 * @return
	 */
	public String getUniqueIdentifier() {
		if (uniqueIdentifier == null) {
			uniqueIdentifier = UUID.randomUUID() + "";
		}
		return uniqueIdentifier;
	}

	private ProxyFactory proxyFactory = ProxyFactory.getInstance();
	/**
	 * 自身对象的唯一标识
	 */
	private String uniqueIdentifier = null;
	/**
	 * 调用者的唯一标识
	 */
	private String callerUID = null;
	/**
	 * 根调用者唯一标识
	 */
	private String callerRoot = null;

	/**
	 * 默认使用的连接
	 */
	private String default_connName;

	// /**
	// * 存储调用者的堆引用
	// */
	// private Map<String, Object> bmoStack = new LinkedHashMap<String,
	// Object>();
	//
	// public void put(String key, Object value){
	// bmoStack.put(key, value);
	// }
	// public Map<String, Object> getBmoStack() {
	// return this.bmoStack;
	// }
	// public void setBmoStack(Map<String, Object> bmoStack) {
	// this.bmoStack = bmoStack;
	// }
	/**
	 * 设置调用者
	 * 
	 * @param callerUID
	 */
	public void setCallerUID(String callerUID) {
		this.callerUID = callerUID;
	}

	/**
	 * 获取调用者
	 * 
	 * @return
	 */
	public String getCallerUID() {
		return callerUID;
	}

	/**
	 * 设置根调用者
	 * 
	 * @param callerRoot
	 */
	public void setCallerRoot(String callerRoot) {
		this.callerRoot = callerRoot;
	}

	/**
	 * 获取根调用者
	 * 
	 * @return
	 */
	public String getCallerRoot() {
		return callerRoot;
	}

	/**
	 * 获取默认使用连接
	 * 
	 * @return
	 */
	public String getDefault_connName() {
		return default_connName;
	}

	/**
	 * 设置默认使用连接
	 * 
	 * @param default_connName
	 */
	public void setDefault_connName(String default_connName) {
		this.default_connName = default_connName;
	}

	/**
	 * 设置默认使用连接
	 * 
	 * @param connName
	 */
	public void setConn(String connName) {
	    this.setDefault_connName(connName);
	}

//	/**
//	 * 获取业务类连接
//	 * 
//	 * @param connName
//	 * @return
//	 */
//	public Connection getConn(String connName) {
//		return this.getConns().get(StringUtil.isEmptys(connName) ? this.getDefault_connName() : connName);
//	}
//
//	public Map<String, Connection> getConns(){
//		return proxyFactory.getBmoConn().get(this.getCallerRoot() == null ? this.getUniqueIdentifier() : this.getCallerRoot());
//	}
	public String toString() {
		return "";
	}

	/**
	 * 获取持久层操作对象实例
	 * 
	 * @param name
	 * @param o
	 * @return
	 */
	public IDMO_ROOT getIDMO(String name, Object... o) {
		return proxyFactory.getIDMO(name, this.getDefault_connName(), this.getCallerRoot() == null ? this.getUniqueIdentifier() : this.getCallerRoot(), o);
	}

	/**
	 * 获取持久层操作对象实例
	 * 
	 * @param name
	 * @param connName
	 * @param o
	 * @return
	 */
	public IDMO_ROOT getIDMO(String name, String connName, Object... o) {
	    return proxyFactory.getOp().getIDMOByConn(name, connName,this.getCallerRoot() == null ? this.getUniqueIdentifier() : this.getCallerRoot(),o);
	}
	
	/**
	 * 获取持久层操作对象实例
	 * 
	 * @param name
	 * @param connName
	 * @param o
	 * @return
	 */
	public IDMO_ROOT getIDMOByDsType(String name, String data_type_class, Object... o) {
	    IDMO_ROOT idmo = proxyFactory.getOp().getIDMO(name, data_type_class, o);
	    return idmo;
	}

	/**
	 * 获取持久层操作对象实例 此方法代理简单数据操作
	 * 
	 * @param name
	 * @param tModel
	 * @param o
	 * @return
	 */
	public IDMO_ROOT getIDMOProxy(String name, TableModel tModel, Object... o) {
		return proxyFactory.getIDMOProxy(name, this.getDefault_connName(), tModel, this.getCallerUID() == null ? this.getUniqueIdentifier() : this.getCallerUID(), o);
	}

	/**
	 * 获取嵌套内部事务调用对象 调用此方法获取对象，将继承顶层事务的连接 此方法用于处理事务嵌套
	 * 
	 * @param name
	 * @param o
	 * @return
	 * @throws Throwable
	 */
	public IBMO_ROOT getIBMO(String name, Object... o) throws Throwable {
		IBMO_ROOT ibmo;
		if (o != null && o.length > 0) {
			ibmo = proxyFactory.factory(name, false, o);
		} else {
			ibmo = proxyFactory.factory(name, false);
		}
		ibmo.setCallerUID(this.getUniqueIdentifier());
		ibmo.setCallerRoot(this.getCallerRoot() == null ? this.getUniqueIdentifier() : this.getCallerRoot());
		return ibmo;
	}

}