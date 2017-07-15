package org.oiue.service.odp.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.oiue.service.log.Logger;
import org.oiue.service.odp.base.ProxyDBSource;
import org.oiue.service.odp.bmo.IBMO;
import org.oiue.service.odp.objpool.BmoConfig;
import org.oiue.service.odp.objpool.ObjectPools;

/**
 * @author Every(王勤) E-mail/MSN:mwgjkf@hotmail.com QQ:30130942
 * @version bmoProxy.java Apr 27, 2010
 */
public class BmoProxy implements InvocationHandler {
    private IBMO object;
    private String name;

    /**
     * 定义系统及非代理方法
     */
    private static final List<String> skipMethod = new ArrayList<String>();
    ProxyFactory proxyFactory = ProxyFactory.getInstance();
    ObjectPools objectPools = ObjectPools.getInstance();

    /**
     * 
     * @param object 业务对象
     * @param name 业务类对象名
     */
    public BmoProxy(IBMO object, String name) {
        skipMethod.add("getUniqueIdentifier");
        skipMethod.add("setCallerUID");
        skipMethod.add("getCallerUID");
        skipMethod.add("setCallerRoot");
        skipMethod.add("getCallerRoot");
        this.object = object;
        this.name = name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method,
     * java.lang.Object[]) 代理的bmo方法需要进行事务处理，因此对应的方法需要配置相应的处理连接，
     * 考虑到可能会在多个数据服务器（多个客户子系统）,因此连接可能有多个，每一个bmo的一个方法都将开启一个事务 并将连接存储到map中
     * 
     * 业务方法代理 大多数业务中涉及到事务处理， 此处代理业务方法的事务处理，解决单一业务，多业务的事务管理，事务嵌套管理 在此处考虑多连接，嵌套事务的松耦合处理 目前暂未实现事务嵌套处理，
     * 
     * @ 2010-11-16
     */
    @SuppressWarnings("rawtypes")
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object o = null;
        Logger logger = proxyFactory.getLogger(getClass());
        if (logger.isDebugEnabled())
            logger.debug(object.getUniqueIdentifier() + "--------------->" + "&time:" + System.nanoTime());
        Map<String, Connection> connMap = new HashMap<String, Connection>();
        Connection conn = null;
        String dbName = null;
        /**
         * 代理对象的一些方法不需要通过代理执行，这些方法直接调用并返回
         */
        if (skipMethod.contains(method.getName())) {
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


        BmoConfig bc = objectPools.getBmoConfig(name); // 获取业务对象封装Bean
        List<String> connList = bc.getConnName(method.getName());// 获取业务对象对应方法的连接名称
        try {
            for (Iterator iterator = connList.iterator(); iterator.hasNext();) {
                dbName = (String) iterator.next();
                try {
                    conn = proxyFactory.getOp().getDs().getConn(dbName); // 根据连接名称获取对应连接
                    conn.setAutoCommit(false); // 设置开启事务
                    connMap.put(dbName, conn);
                } catch (Throwable e) {
                    new RuntimeException("get connection or set auto commit for [" + dbName + "] is error:" + e.getMessage(), e);
                }
            }
            
        } catch (RuntimeException re) {
            if(connMap.size()>0)
                for (Iterator iterator = connMap.values().iterator(); iterator.hasNext();) {
                    conn = (Connection) iterator.next();
                    if(conn!=null)
                    conn.close();
                }
            connMap.clear();
            throw re;
        }
        
        if (logger.isDebugEnabled())
            logger.debug("#" + object.getUniqueIdentifier() + "|" + System.nanoTime() + "|" + "&" + connMap);
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
        } catch (Throwable e) {
//            String msg = "#" + object.getUniqueIdentifier() + "|" + System.nanoTime() ;
//            logger.error(msg + "|" + "捕获所有异常信息：" + e.getMessage(), e);
            for (String _dbName : connMap.keySet()) {
                conn = connMap.get(_dbName);
                if (conn != null) {
                    try {
                        if (logger.isDebugEnabled())
                            logger.debug("#" + object.getUniqueIdentifier() + "|" + System.nanoTime() + "|" + "回滚!" + _dbName);
                        conn.rollback();
                        conn.close();
                    } catch (Throwable ex) {
                        logger.error(ex.getMessage(), ex);
                    }
                }
            }
            throw e;
        } finally {
            // 业务方法执行完成后关闭连接
            int cmd = 0;
            while (true) {
                if (cmd >= ProxyDBSource.DEFAULT_CONN_CLOSENUM || connMap.size() == 0)
                    break;
                for (Iterator iterator = connMap.keySet().iterator(); iterator.hasNext();) {
                    dbName = (String) iterator.next();
                    conn = connMap.get(dbName);
                    if (logger.isDebugEnabled())
                        logger.debug("#" + object.getUniqueIdentifier() + "|" + System.nanoTime() + "|" + "关闭连接!" + dbName);
                    if (conn != null) {
                        try {
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
                } catch (Throwable e) {
                    logger.error(e.getMessage(), e);
                }
                cmd++;
            }
            dbName = null;
            conn = null;
            connMap.clear();
            connMap = null;
        }
        if (logger.isDebugEnabled())
            logger.debug(object.getUniqueIdentifier() + "---------------<" + "&time:" + System.nanoTime());
        return o;
    }
}
