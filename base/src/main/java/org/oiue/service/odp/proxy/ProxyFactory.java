package org.oiue.service.odp.proxy;

import java.lang.reflect.Proxy;
import java.util.LinkedHashMap;
import java.util.Map;

import org.oiue.service.log.LogService;
import org.oiue.service.log.Logger;
import org.oiue.service.odp.bmo.IBMO;
import org.oiue.service.odp.dmo.IDMO;
import org.oiue.service.odp.dmo.p.IDMO_ROOT;
import org.oiue.service.odp.objpool.ObjectPools;
import org.oiue.table.structure.TableModel;

/**
 * 代理工厂 解决事务等
 * 
 * @author Every(王勤) E-mail/MSN:mwgjkf@hotmail.com QQ:30130942
 * @version ProxyFactory.java Apr 27, 2010
 */
@SuppressWarnings({ "rawtypes" })
public class ProxyFactory {
    private static ProxyFactory proxyFactory = new ProxyFactory();

    private LogService logService;
    @SuppressWarnings("unused")
    private Logger logger;

    public void setLogService(LogService logService) {
        this.logService = logService;
        logger = logService.getLogger(this.getClass());
    }

    public Logger getLogger(Class<?> c) throws Throwable {
        return logService.getLogger(c);
    }

    /**
     * 初始化对象池
     */
    private ObjectPools objectPools = null;

    public ObjectPools getOp() {
        return objectPools;
    }

    public void setOp(ObjectPools op) {
        this.objectPools = op;
    }


    // private Map<String, DataSourceAdmin> dataSourceAdmin = new ConcurrentHashMap<String,
    // DataSourceAdmin>();
    //
    // public boolean registerDataSourceAdmin(String name, DataSourceAdmin dataSourceAdminImpl) {
    // if (dataSourceAdmin.containsKey(name)) {
    // throw new RuntimeException("register DataSourceAdmin is error,duplicate registration :" +
    // name);
    // }
    // dataSourceAdmin.put(name, dataSourceAdminImpl);
    // return true;
    // }
    //
    // public Object getDataSourceByType(String data_type_class, String name) {
    // Object rtn = null;
    // List<Throwable> exceptions = null;
    // for (Iterator iterator = dataSourceAdmin.values().iterator(); iterator.hasNext();) {
    // DataSourceAdmin dataSourceAdmin = (DataSourceAdmin) iterator.next();
    // try {
    // rtn = dataSourceAdmin.getDataSourceByType(data_type_class, name);
    // if (rtn != null)
    // break;
    // } catch (Throwable e) {
    // if (exceptions == null)
    // exceptions = new ArrayList<Throwable>();
    // exceptions.add(e);
    // }
    // }
    // if (rtn == null) {
    // if (exceptions != null)
    // throw new RuntimeException(exceptions + "");
    // }
    // return rtn;
    // }
    //
    // public Object getDataSourceByConn(String connName, String name) {
    // return this.getDataSourceByType(this.getData_source_class(connName), name);
    // }
    //
    // public Map<String, DataSourceAdmin> getDataSourceAdmin() {
    // return dataSourceAdmin;
    // }
    //
    // public void setDataSourceAdmin(Map<String, DataSourceAdmin> dataSourceAdmin) {
    // if (dataSourceAdmin == null)
    // this.dataSourceAdmin.clear();
    // else
    // this.dataSourceAdmin = dataSourceAdmin;
    // }

    /**
     * 存储调用者的堆引用
     */
    private Map<Integer, LinkedHashMap> bmoStack = new LinkedHashMap<Integer, LinkedHashMap>();

    public Map<Integer, LinkedHashMap> getBmoStack() {
        return bmoStack;
    }

    public void setBmoStack(Map<Integer, LinkedHashMap> bmoStack) {
        this.bmoStack = bmoStack;
    }

    /**
     * 读取配置文件 初始化代理工厂
     */
    private ProxyFactory() {
        objectPools = ObjectPools.getInstance();
    }

    /**
     * 获取代理工厂
     * 
     * @return 唯一代理工厂实例
     */
    public static ProxyFactory getInstance() {
        return proxyFactory;
    }

    /**
     * 
     * @param <T>
     * @param name
     * @return
     * @throws Throwable
     */
    public static <T> T factorys(String name) throws Throwable {
        return proxyFactory.factory(name, true);
    }

    /**
     * 获取业务操作对象的实例 业务模型对象初始动态代理 根据对象映射中指定的名称获取操作对象的实例 此实例的方法均被代理
     * 
     * @param <T>
     * 
     * @param name 调用的对象映射名称
     * @param main 是否是主要的方法 对于展现层调用，则是主要方法，对于业务层调用，则不是主要方法
     * @param o 初始参数
     * @return
     * @throws Throwable
     */
    @SuppressWarnings("unchecked")
    public <T> T factory(String name, boolean main, Object... o) throws Throwable {
        IBMO object = getOp().getIBMO(name, o);
        if (main) {
            Class cls = object.getClass();
            object = (IBMO) Proxy.newProxyInstance(cls.getClassLoader(), cls.getInterfaces(), new BmoProxy(object, name));
        }
        return (T) object;
    }

    /**
     * 获取持久层操作对象 持久层对象初始动态代理 根据对象映射中指定的名称获取操作对象实例 此实例所有方法均被代理
     * 
     * @param name
     * @param connName
     * @param bmoUniqueIdentifier 主业务实体类的唯一标识 此值用于唯一定位到jvm中的业务对象实体
     * @param o
     * @return
     */
    public IDMO getIDMO(String name, String connName, String bmoUniqueIdentifier, Object... o) {
        IDMO object = getOp().getIDMOByConn(name, connName, bmoUniqueIdentifier, o);
        Class cls = object.getClass();
        return (IDMO) Proxy.newProxyInstance(cls.getClassLoader(), cls.getInterfaces(), new DmoProxy(object, bmoUniqueIdentifier, o));
    }

    /**
     * 获取持久层操作对象 此方法将代理简单的数据操作提交 持久层对象初始动态代理 根据对象映射中指定的名称获取操作对象实例 此实例所有方法均被代理 此方法同时代理操作查询数据
     * 
     * @param name
     * @param tModel 返回对象
     * @param bmoUniqueIdentifier 主业务实体类的唯一标识 此值用于唯一定位到jvm中的业务对象实体
     * @param o
     * @return
     */
    public IDMO_ROOT getIDMOProxy(String name, String connName, TableModel tModel, String bmoUniqueIdentifier, Object... o) {
        IDMO object = getOp().getIDMOByConn(name, connName, bmoUniqueIdentifier, o);
        Class cls = object.getClass();
        return (IDMO) Proxy.newProxyInstance(cls.getClassLoader(), cls.getInterfaces(), new DmoProxy(object, tModel, bmoUniqueIdentifier, o));
    }
}
