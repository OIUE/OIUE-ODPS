/**
 * 
 */
package org.oiue.service.odp.base;

import java.io.Serializable;
import java.sql.Connection;

/**
 * @author Every(王勤) E-mail/MSN:mwgjkf@hotmail.com QQ:30130942
 * @version DBSource.java Apr 27, 2010 TODO
 */
public interface ProxyDBSource extends Serializable {
	public static int DEFAULT_CONN_CLOSENUM = 10;

	/**
	 * 根据连接名称获取连接
	 * 
	 * @param dbName 数据源名称
	 * @return 连接
	 * @throws Throwable 连接异常
	 */
	public Connection getConn(String dbName) throws Throwable;
}
