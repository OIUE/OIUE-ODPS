package org.oiue.service.odp.base;

import java.sql.SQLException;
import java.util.Map;

import org.oiue.service.odp.bmo.IBMO;
import org.oiue.service.odp.dmo.IDMO;
import org.oiue.service.odp.dmo.IDMO_DB;
import org.oiue.service.odp.objpool.BmoConfig;
import org.oiue.service.odp.proxy.ProxyFactory;

public class FactoryServiceImpl implements FactoryService {
	
	private static final long serialVersionUID = 2420654720215576150L;
	
	private ProxyFactory proxyFactory;
	
	public FactoryServiceImpl(ProxyFactory pf) {
		this.proxyFactory = pf;
	}
	
	@Override
	public boolean registerDmo(String name, String DBType, IDMO dmo) {
		return proxyFactory.getOp().registerDMO(DBType, name, dmo);
	}
	
	@Override
	public boolean registerDmoDb(String DBType, IDMO_DB dmo) {
		return proxyFactory.getOp().registerDMODB(DBType, dmo);
	}
	
	@Override
	public boolean registerDmoForce(String name, String DBType, IDMO dmo) {
		return proxyFactory.getOp().registerDMOForce(DBType, name, dmo);
	}
	
	@Override
	public boolean unRegisterDmo(String name, String DBType) {
		return proxyFactory.getOp().unRegisterDmo(name, DBType);
	}
	
	@Override
	public boolean unRegisterDmo(String name) {
		return proxyFactory.getOp().unRegisterDmo(name);
	}
	
	@Override
	public boolean registerBmo(String name, IBMO bmo) {
		return false;
	}
	
	@Override
	public boolean registerBmo(String name, BmoConfig bmoc) {
		return proxyFactory.getOp().registerBMO(name, bmoc);
	}
	
	@Override
	public boolean registerBmoForce(String name, BmoConfig bmoc) {
		return proxyFactory.getOp().registerBMO(name, bmoc);
	}
	
	@Override
	public boolean unRegisterBmo(String name) {
		return false;
	}
	
	@Override
	public <T> T getDmo(String name, String DBType, Object... o) {
		return proxyFactory.getOp().getIDMO(name, DBType, o);
	}
	
	@Override
	public Map<String, IDMO> getDmosByName(String name) {
		return proxyFactory.getOp().getDmoByName().get(name);
	}
	
	@Override
	public Map<String, IDMO> getDmosByDBType(String DBType) {
		return proxyFactory.getOp().getDmoByDBType().get(DBType);
	}
	
	@SuppressWarnings("static-access")
	@Override
	public <T> T getBmo(String name) {
		return proxyFactory.factorys(name);
	}
	
	@Override
	public <T> T getBmoByProcess(String name, String processKey) {
		return proxyFactory.factorys(name, processKey);
	}
	
	@Override
	public void CommitByProcess(String processKey) throws SQLException {
		proxyFactory.getOp().Commit(processKey);
	}
	
	@Override
	public void RollbackByProcess(String processKey) throws SQLException {
		proxyFactory.getOp().Rollback(processKey);
	}
	
	@Override
	public void setData_source_class(Map<String, String> data_source_class) {
		proxyFactory.getOp().setData_source_class(data_source_class);
	}
	
	@Override
	public void putData_source_class(String connName, String DBType) {
		proxyFactory.getOp().getData_source_class().put(connName, DBType);
	}

	@Override
	public ProxyFactory getProxyFactory() {
		return proxyFactory;
	}
	
	
}