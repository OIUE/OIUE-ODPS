package org.oiue.service.odp.objpool;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.oiue.service.odp.base.ProxyDBSource;
import org.oiue.service.odp.dmo.IDMO;
import org.oiue.service.odp.dmo.IJDBC_DMO;
import org.oiue.tools.Reflection;
import org.oiue.tools.string.StringUtil;

/**
 * 对象池 通过此类管理获取对象
 * 
 * @author Every(王勤) E-mail/MSN:mwgjkf@hotmail.com QQ:30130942
 * @version ObjectPools.java Apr 27, 2010
 */
@SuppressWarnings({ "unused", "unchecked", "rawtypes" })
public class ObjectPools {

    private static ObjectPools op = new ObjectPools();

    private ProxyDBSource proxyDBSource = null;

    public ProxyDBSource getDs() {
        return proxyDBSource;
    }

    public void setDs(ProxyDBSource ds) {
        this.proxyDBSource = ds;
    }

    /**
     * 存储持久层对象map映射 2011-1-13 增加对多连接的支持
     */
    private Map<String, Map<String, IDMO>> dmoByDBType = new ConcurrentHashMap<String, Map<String, IDMO>>();
    private Map<String, Map<String, IDMO>> dmoByName = new ConcurrentHashMap<String, Map<String, IDMO>>();

    /**
     * 初始化对象池
     */
    private ObjectPools() {}

    /**
     * 获取对象池工厂
     * 
     * @return 唯一对象池工厂实例
     */
    public static ObjectPools getInstance() {
        return op;
    }

    /**
     * 注册DMO
     * 
     * @param DBType
     * @param name
     * @param idmo
     * @return
     */
    public boolean registerDMO(String DBType, String name, IDMO idmo) {
        if (StringUtil.isEmptys(DBType) || StringUtil.isEmptys(name) || idmo == null)
            return false;

        Map dmoM = dmoByDBType.get(DBType);
        if (dmoM == null) {
            dmoM = new Hashtable<String, IDMO>();
            dmoByDBType.put(DBType, dmoM);
        }
        if (dmoM.containsKey(name)) {
            throw new RuntimeException("Duplicate registration![" + DBType + "," + name + "]");
        }
        dmoM.put(name, idmo);

        Map dmoN = dmoByName.get(name);
        if (dmoN == null) {
            dmoN = new Hashtable<String, IDMO>();
            dmoByName.put(name, dmoN);
        }
        dmoN.put(DBType, idmo);

        return true;
    }

    public boolean registerDMOForce(String DBType, String name, IDMO idmo) {
        if (StringUtil.isEmptys(DBType) || StringUtil.isEmptys(name) || idmo == null)
            return false;

        Map dmoM = dmoByDBType.get(DBType);
        if (dmoM == null) {
            dmoM = new Hashtable<String, IDMO>();
            dmoByDBType.put(DBType, dmoM);
        }
        dmoM.put(name, idmo);

        Map dmoN = dmoByName.get(name);
        if (dmoN == null) {
            dmoM = new Hashtable<String, IDMO>();
            dmoByName.put(name, dmoM);
        }
        dmoN.put(DBType, idmo);

        return true;
    }

    public boolean unRegisterDmo(String name, String DBType) {
        if (StringUtil.isEmptys(DBType) || StringUtil.isEmptys(name))
            return false;

        Map dmoM = dmoByDBType.get(DBType);
        if (dmoM != null)
            dmoM.remove(name);

        Map dmoN = dmoByName.get(name);
        if (dmoN != null)
            dmoN.remove(DBType);

        return true;
    }

    public boolean unRegisterDmo(String name) {
        if (StringUtil.isEmptys(name))
            return false;

        Map dmoN = dmoByName.get(name);
        for (Iterator iterator = dmoN.keySet().iterator(); iterator.hasNext();) {
            String DBType = (String) iterator.next();
            Map dmoM = dmoByDBType.get(DBType);
            if (dmoM != null)
                dmoM.remove(name);
        }
        dmoByName.remove(name);
        return true;
    }

    /**
     * 获取持久接口
     * 
     * @param name
     * @param o
     * @return
     */
    public IDMO getIDMOByConn(String name, String connName, String bmoUniqueIdentifier, Object... o) {
        try {
            Connection conn = this.getBmoConn().get(bmoUniqueIdentifier).get(connName);
            String data_type_class = this.getData_source_class(connName);
            IDMO idmo = this.getIDMO(name, data_type_class, o);
            if (idmo instanceof IJDBC_DMO) {
                ((IJDBC_DMO) idmo).setConn(conn);
            }
            return idmo;
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException("持久层" + name + "找不到！" + e.getMessage(), e);
        }
    }

    /**
     * 获取持久接口
     * 
     * @param name
     * @param o
     * @return
     */
    public IDMO getIDMO(String name, String data_type_class, Object... o) {

        try {
            Class c = this.getDmoByDBType().get(data_type_class).get(name).getClass();
            if (o == null || o.length == 0)
                return (IDMO) c.newInstance();
            Reflection r = new Reflection();
            Class[] argsClass = new Class[o.length];
            for (int i = 0, j = o.length; i < j; i++) {
                argsClass[i] = o[i].getClass();
            }
            Constructor<?> cons = ConstructorUtils.getMatchingAccessibleConstructor(c, argsClass);
            // Constructor cons = c.getConstructor(argsClass);
            return (IDMO) cons.newInstance(o);
        } catch (Throwable e) {
            throw new RuntimeException("持久层" + name + "找不到！DBType：" + data_type_class + "。" + e.getMessage(), e);
        }
    }

    /**
     * 业务方法与连接
     */
    private Map<String, BmoConfig> bmo = new ConcurrentHashMap<String, BmoConfig>();

    public Map<String, BmoConfig> getBmo() {
        return bmo;
    }

    public void setBmo(Map<String, BmoConfig> bmo) {
        this.bmo = bmo;
    }

    public boolean registerBMO(String name, BmoConfig bmoc) {
        if (bmo.get(name) != null)
            return false;
        bmo.put(name, bmoc);
        return true;
    }

    /**
     * 获取配置属性对象
     * 
     * @param name
     * @return
     */
    public BmoConfig getBmoConfig(String name) {
        return getBmo().get(name);
    }

    /**
     * 获取业务接口
     * 
     * @param <T>
     * 
     * @param name
     * @param o
     * @return
     */
    public <T> T getIBMO(String name, Object... o) {
        try {
            BmoConfig t_bmoc = this.getBmo().get(name);
            return (T) t_bmoc.getClasses(o);
        } catch (Throwable e) {
            throw new RuntimeException("业务层" + name + "找不到！" + e.getMessage(), e);
        }
    }

    public Map<String, Map<String, IDMO>> getDmoByDBType() {
        return dmoByDBType;
    }

    public void setDmoByDBType(Map<String, Map<String, IDMO>> dmoByDBType) {
        this.dmoByDBType = dmoByDBType;
    }

    public Map<String, Map<String, IDMO>> getDmoByName() {
        return dmoByName;
    }

    public void setDmoByName(Map<String, Map<String, IDMO>> dmoByName) {
        this.dmoByName = dmoByName;
    }

    /**
     * 存储业务层对象对应的数据库连接
     */
    private Map<String, Map<String, Connection>> bmoConn = new ConcurrentHashMap<String, Map<String, Connection>>();

    public Map<String, Map<String, Connection>> getBmoConn() {
        return bmoConn;
    }

    public void setBmoConn(Map<String, Map<String, Connection>> bmoConn) {
        this.bmoConn = bmoConn;
    }

    /**
     * 数据源分类
     */
    private Map<String, String> data_source_class = new ConcurrentHashMap<String, String>();

    public String getData_source_class(String connName) {
        String data_type = this.data_source_class.get(connName);
        if (data_type == null)
            try {
                Connection conn = proxyDBSource.getConn(connName);
                data_type = this.getData_source_class(conn);
                this.data_source_class.put(connName, data_type);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }

        return data_type;
    }

    public String getData_source_class(Connection conn) {
        String data_type = null;
        try {
            data_type = conn.getMetaData().getDatabaseProductName();
            data_type = data_type.toLowerCase();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return data_type;
    }

    public Map<String, String> getData_source_class() {
        return data_source_class;
    }

    public void setData_source_class(Map<String, String> data_source_class) {
        if (data_source_class.size() == 0) {
            this.data_source_class = data_source_class;
        } else {
            throw new RuntimeException(" data_soutce_class is not null,can't set new value. ");
        }
    }

}
