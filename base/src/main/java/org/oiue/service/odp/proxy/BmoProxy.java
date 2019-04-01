package org.oiue.service.odp.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.oiue.service.log.Logger;
import org.oiue.service.odp.base.ProxyDBSource;
import org.oiue.service.odp.bmo.IBMO;
import org.oiue.tools.exception.OIUEException;

/**
 * @author Every(王勤) E-mail/MSN:mwgjkf@hotmail.com QQ:30130942
 * @version bmoProxy.java Apr 27, 2010
 */
public class BmoProxy implements InvocationHandler {
	private IBMO object;
	// private String name;
	
	/**
	 * 定义系统及非代理方法
	 */
	private static final List<String> skipMethod = new ArrayList<String>();
	private static ProxyFactory proxyFactory = ProxyFactory.getInstance();
	private static Logger logger = proxyFactory.getLogger(BmoProxy.class);
	// private static TimeLogger tlogger = proxyFactory.getTimeLogger(BmoProxy.class);
	static {
		skipMethod.add("getUniqueIdentifier");
		skipMethod.add("setCallerUID");
		skipMethod.add("getCallerUID");
		skipMethod.add("setCallerRoot");
		skipMethod.add("getCallerRoot");
		skipMethod.add("setConn");
		skipMethod.add("setDefault_connName");
		skipMethod.add("getDefault_connName");
		skipMethod.add("getIBMO");
		skipMethod.add("getIDMOByDsType");
		skipMethod.add("getIDMO");
		skipMethod.add("toString");
	}
	
	/**
	 * 
	 * @param object 业务对象
	 * @param name 业务类对象名
	 */
	public BmoProxy(IBMO object, String name) {
		this.object = object;
		// this.name = name;
	}
	
	/**
	 * (non-Javadoc)
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[]) 代理的bmo方法需要进行事务处理，因此对应的方法需要配置相应的处理连接， 考虑到可能会在多个数据服务器（多个客户子系统）,因此连接可能有多个，每一个bmo的一个方法都将开启一个事务 并将连接存储到map中 业务方法代理 大多数业务中涉及到事务处理， 此处代理业务方法的事务处理，解决单一业务，多业务的事务管理，事务嵌套管理 在此处考虑多连接，嵌套事务的松耦合处理 目前暂未实现事务嵌套处理， @ 2010-11-16
	 */
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (logger.isDebugEnabled())
			logger.debug(object.getUniqueIdentifier() + "--------------->" + "&time:" + System.nanoTime());
		if (skipMethod.contains(method.getName())) {// 代理对象的一些方法不需要通过代理执行，这些方法直接调用并返回
			return method.invoke(object, args);// 调用真实对象的代理方法
		}
		if (logger.isDebugEnabled())
			logger.debug("#" + object.getUniqueIdentifier() + "|" + System.nanoTime() + "|" + "method.getName():" + method.getName() + "&time:" + System.nanoTime());
		/**
		 * 判断是否为业务内部业务 当对象调用者id大于0 则表示有调用者 该方法为业务内部业务 如果是业务内部业务，则直接调用，
		 */
		if (object.getCallerUID() != null && object.getCallerRoot() != null) {
			return method.invoke(object, args);
		}
		Object o = null;
		Connection conn = null;
		String dbName = null;
		
		// BmoConfig bc = proxyFactory.getOp().getBmoConfig(name); // 获取业务对象封装Bean
		// List<String> connList = bc.getConnName(method.getName());// 获取业务对象对应方法的连接名称
		// Map<String, Connection> connMap = new HashMap<String, Connection>();
		// try {
		// for (Iterator<String> iterator = connList.iterator(); iterator.hasNext();) {
		// dbName = iterator.next();
		// try {
		// conn = proxyFactory.getOp().getDs().getConn(dbName); // 根据连接名称获取对应连接
		// conn.setAutoCommit(false); // 设置开启事务
		// connMap.put(dbName, conn);
		// } catch (SQLException e) {
		// throw new OIUEException(StatusResult._conn_error,"get connection or set auto commit for [" + dbName + "] is error:" + e.getMessage(), e);
		// }
		// }
		//
		// } finally {
		// if (connMap.size() > 0)
		// for (Iterator<Connection> iterator = connMap.values().iterator(); iterator.hasNext();) {
		// conn = iterator.next();
		// if (conn != null)
		// conn.close();
		// }
		// connMap.clear();
		// }
		//
		//// if (logger.isDebugEnabled())
		//// logger.debug("#" + object.getUniqueIdentifier() + "|" + System.nanoTime() + "|" + "&" + connMap);
		// proxyFactory.getOp().getBmoConn().put(object.getUniqueIdentifier(), connMap);// 用对象的唯一标识存储连接
		Map<String, Connection> connMap = new HashMap<String, Connection>();
		proxyFactory.getOp().getBmoConn().put(object.getUniqueIdentifier(), connMap);// 用对象的唯一标识存储连接
		try {
			if (logger.isDebugEnabled())
				logger.debug("#" + object.getUniqueIdentifier() + "|" + System.nanoTime() + "|" + "执行代理方法:" + method);
			o = method.invoke(object, args);// 调用真实对象的代理方法
			if (logger.isDebugEnabled())
				logger.debug("#" + object.getUniqueIdentifier() + "|" + System.nanoTime() + "|" + "执行代理方法结束:" + method);
			for (String _dbName : connMap.keySet()) {
				conn = connMap.get(_dbName);
				if (conn != null) {
					conn.commit();
					if (logger.isDebugEnabled())
						logger.debug("#" + object.getUniqueIdentifier() + "|" + System.nanoTime() + "|" + "执行代理方法提交事务！" + _dbName);
				}
			}
			if (logger.isDebugEnabled())
				logger.debug(object.getUniqueIdentifier() + "---------------<" + "&time:" + System.nanoTime());
			return o;
		} catch (OIUEException e) {
			throw e;
		} catch (Throwable e) {
			if (e instanceof InvocationTargetException)
				e = ((InvocationTargetException) e).getTargetException();
			if (e instanceof UndeclaredThrowableException)
				e = ((UndeclaredThrowableException) e).getUndeclaredThrowable();
			if (e instanceof InvocationTargetException)
				e = ((InvocationTargetException) e).getTargetException();
			if (e instanceof UndeclaredThrowableException)
				e = ((UndeclaredThrowableException) e).getUndeclaredThrowable();
			throw e;
		} finally {
			// 业务方法执行完成后关闭连接
			int cmd = 0;
			while (true) {
				if (connMap != null && connMap.size() > 0)
					for (Iterator<String> iterator = connMap.keySet().iterator(); iterator.hasNext();) {
						dbName = iterator.next();
						conn = connMap.get(dbName);
						if (conn != null) {
							try {
								if (logger.isDebugEnabled())
									logger.debug("#" + object.getUniqueIdentifier() + "|" + System.nanoTime() + "|" + "回滚!" + dbName);
								conn.rollback();
								conn.close();
								iterator.remove();
							} catch (Throwable e) {
								logger.error(e.getMessage(), e);
							}
						} else {
							iterator.remove();
						}
					}
				if (cmd >= ProxyDBSource.DEFAULT_CONN_CLOSENUM || connMap.size() == 0)
					break;
				try {
					Thread.sleep(20);
				} catch (Throwable e) {}
				cmd++;
			}
			dbName = null;
			conn = null;
			connMap.clear();
			connMap = null;
		}
	}
}
