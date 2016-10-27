/**
 * 
 */
package org.oiue.service.odp.bmo.p;

import java.io.Serializable;

/**
 * 业务操作抽象接口
 * 
 * @author Every{王勤}
 */
public interface IBMO_ROOT extends Serializable {
	/**
	 * 获取对象的唯一标识
	 * 
	 * @return
	 */
	public String getUniqueIdentifier();

	/**
	 * 设置调用者
	 * 
	 * @param callerUID
	 */
	public void setCallerUID(String callerUID);

	/**
	 * 获取调用者
	 * 
	 * @return
	 */
	public String getCallerUID();

	/**
	 * 设置根调用者
	 * 
	 * @param callerRoot
	 */
	public void setCallerRoot(String callerRoot);

	/**
	 * 获取根调用者
	 * 
	 * @return
	 */
	public String getCallerRoot();

	/**
	 * 设置默认使用连接
	 * 
	 * @param connName
	 */
	public void setConn(String connName);

//	/**
//	 * 获取业务类连接
//	 * 
//	 * @param connName
//	 * @return
//	 */
//	public Connection getConn(String connName);
//	public Map<String, Connection> getConns();

	public void setDefault_connName(String default_connName);

	public String getDefault_connName();

	// /**
	// *
	// * @param key
	// * @param value
	// */
	// public void put(String key, Object value);
	// /**
	// *
	// * @return
	// */
	// public Map<String, Object> getBmoStack();

}
