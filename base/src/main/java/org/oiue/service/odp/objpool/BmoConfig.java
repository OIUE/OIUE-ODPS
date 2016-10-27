package org.oiue.service.odp.objpool;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.oiue.service.odp.bmo.IBMO;

import java.lang.reflect.Constructor;

/**
 * 业务类配置Bean 此类用于定义业务类与映射名
 * 
 * @author Every{王勤}
 */
@SuppressWarnings({ "unchecked", "serial", "rawtypes" })
public class BmoConfig implements Serializable {
	private String name;
	private Map method = new HashMap();
	private Map conn = new HashMap();
	private IBMO bmo = null;

	public BmoConfig() {
	}

	/**
	 * 初始化业务Bean
	 * 
	 * @param name
	 *            映射名称
	 * @param classPath
	 *            类路径
	 * @param method
	 *            方法与连接名称的对应
	 */
	public BmoConfig(IBMO bmo, Map method) {
		this.bmo = bmo;
		this.name = bmo.getClass().getName();
		this.method = method;
		if (method != null && method.get("default") != null) {
			Object conn = method.get("default");
			if (conn instanceof List) {
				bmo.setDefault_connName(((List) conn).get(0) + "");
			}
		}
	}

	@Override
	public String toString() {
		return name + "|" + bmo.getDefault_connName() + "|" + conn.toString();
	}

	/**
	 * 获取类对象实例（这是一个全新的实例）
	 * 
	 * @return
	 * @throws Throwable
	 */
	public IBMO getClasses(Object... o) throws Throwable {
		Class newoneClass = bmo.getClass();
		IBMO ibmo = null;
		if (o != null && o.length > 0) {
			Class[] argsClass = new Class[o.length];
			for (int i = 0, j = o.length; i < j; i++) {
				argsClass[i] = o[i].getClass();
			}
//			Constructor cons = newoneClass.getConstructor(argsClass);
            Constructor<?> cons = ConstructorUtils.getMatchingAccessibleConstructor(newoneClass, argsClass);
			ibmo = (IBMO) cons.newInstance(o);
		} else
			ibmo = (IBMO) newoneClass.newInstance();

		if (ibmo != null)
			ibmo.setDefault_connName(bmo.getDefault_connName());
		return ibmo;
//	    return bmo;
	}

	/**
	 * 根据方法名获取相应的连接名称 当方法没有配置连接时返回默认的连接名
	 * 
	 * @param method
	 *            方法名称
	 * @return 连接名称集合
	 */
	public List<String> getConnName(String method) {
		List<String> connList = (List<String>) this.getMethod().get(method);
		if (connList == null || connList.size() == 0) {
			connList = (List<String>) this.getMethod().get("default");
		}
		return connList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map getMethod() {
		return method;
	}

	public void setMethod(Map method) {
		this.method = method;
	}

	public Map getConn() {
		return conn;
	}

	public void setConn(Map conn) {
		this.conn = conn;
	}

	public IBMO getBmo() {
		return bmo;
	}

	public void setBmo(IBMO bmo) {
		this.bmo = bmo;
	}
}