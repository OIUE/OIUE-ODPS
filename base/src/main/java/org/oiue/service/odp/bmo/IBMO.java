/**
 *
 */
package org.oiue.service.odp.bmo;

import java.io.Serializable;

/**
 * 业务操作抽象类
 *
 * @author Every{王勤}
 */
public interface IBMO extends Serializable {
	/**
	 * 获取对象的唯一标识
	 * @return 标识
	 */
	String getUniqueIdentifier();
	
	/**
	 * 设置调用者
	 * @param callerUID 调用标识
	 */
	void setCallerUID(String callerUID);
	
	/**
	 * 获取调用者
	 * @return 调用者
	 */
	String getCallerUID();
	
	/**
	 * 设置根调用者
	 * @param callerRoot 根调用者
	 */
	void setCallerRoot(String callerRoot);
	
	/**
	 * 获取根调用者
	 * @return 根调用者
	 */
	String getCallerRoot();
	
	/**
	 * 设置默认使用连接
	 * @param connName 连接名称
	 */
	void setConn(String connName);
	
	/**
	 * 设置默认连接
	 * @param default_connName 连接名称
	 */
	void setDefault_connName(String default_connName);
	
	/**
	 * 获取默认连接
	 * @return 连接名称
	 */
	String getDefault_connName();
	
	<T> T getIBMO(String name, Object... o);
	
	<T> T getIBMO(String name, String processKey, Object... o);
	
	<T> T getIDMOByDsType(String name, String data_type_class, Object... o);
	
	<T> T getIDMO(String name, String connName, Object... o);
}