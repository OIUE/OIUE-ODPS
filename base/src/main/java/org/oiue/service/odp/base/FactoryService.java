package org.oiue.service.odp.base;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Map;

import org.oiue.service.odp.bmo.IBMO;
import org.oiue.service.odp.dmo.IDMO;
import org.oiue.service.odp.dmo.IDMO_DB;
import org.oiue.service.odp.objpool.BmoConfig;
import org.oiue.service.odp.proxy.ProxyFactory;

/**
 * @author Every(王勤) E-mail/MSN:mwgjkf@hotmail.com QQ:30130942
 * @version FactoryService.java Apr 27, 2010
 */
public interface FactoryService extends Serializable {
	public boolean registerDmo(String name, String DBType, IDMO dmo);
	
	public boolean registerDmoDb(String DBType, IDMO_DB dmo);
	
	public boolean registerDmoForce(String name, String DBType, IDMO dmo);
	
	public boolean unRegisterDmo(String name, String DBType);
	
	public boolean unRegisterDmo(String name);
	
	public boolean registerBmo(String name, IBMO bmo);
	
	public boolean registerBmo(String name, BmoConfig bmoc);
	
	public boolean registerBmoForce(String name, BmoConfig bmoc);
	
	public boolean unRegisterBmo(String name);
	
	public <T> T getDmo(String name, String DBType, Object... o);
	
	public Map<String, IDMO> getDmosByName(String name);
	
	public Map<String, IDMO> getDmosByDBType(String DBType);
	
	public void setData_source_class(Map<String, String> data_source_class);
	
	public void putData_source_class(String connName, String DBType);
	
	public <T> T getBmo(String name);
	
	public <T> T getBmoByProcess(String name, String processKey);
	
	public void CommitByProcess(String processKey) throws SQLException;
	
	public void RollbackByProcess(String processKey) throws SQLException;
	
	public ProxyFactory getProxyFactory();
}
