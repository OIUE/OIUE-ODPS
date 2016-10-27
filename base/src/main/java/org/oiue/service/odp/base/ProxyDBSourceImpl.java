/**
 * 
 */
package org.oiue.service.odp.base;

import java.sql.Connection;

import org.oiue.service.log.LogService;
import org.oiue.service.log.Logger;
import org.oiue.service.sql.SqlService;

/**
 * @author Every(王勤) E-mail/MSN:mwgjkf@hotmail.com
 *               QQ:30130942
 * @version DBSource.java Apr 27, 2010 TODO
 */
public class ProxyDBSourceImpl implements ProxyDBSource{

	private static final long serialVersionUID = 1L;
	
	SqlService sqlService;
	
	Logger logger;
	public ProxyDBSourceImpl(SqlService sqlService,LogService logService){
		this.sqlService=sqlService;
		logger=logService.getLogger(this.getClass());
	}
	
	/**
	 * 根据连接名称获取连接
	 * @param dbName
	 * @return
	 * @throws Throwable
	 */
	public Connection getConn(String dbName) throws Throwable{
		return sqlService.getConnection(dbName);
	}
}
