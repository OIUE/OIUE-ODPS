/**
 * 
 */
package org.oiue.service.odp.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.oiue.service.odp.dmo.IDMO;
import org.oiue.service.odp.dmo.IJDBC_DMO;
import org.oiue.table.structure.TableModel;

/**
 * @author Every(王勤) E-mail/MSN:mwgjkf@hotmail.com QQ:30130942
 * @version dmoProxy.java Apr 27, 2010
 */
@SuppressWarnings({ "unused" })
public class DmoProxy implements InvocationHandler {
    private IDMO object;
    private TableModel tModel;
    private String bmoUniqueIdentifier;
    private Object[] o;
    private boolean proxyDBCMD = false;

    public DmoProxy() {}

    ProxyFactory pf = ProxyFactory.getInstance();

    /**
     * 持久层代理方法
     * 
     * @param object
     * @param bmoUniqueIdentifier
     * @param o
     */
    public DmoProxy(IDMO object, String bmoUniqueIdentifier, Object... o) {
        this.object = object;
        this.bmoUniqueIdentifier = bmoUniqueIdentifier;
        this.o = o;
    }

    /**
     * 持久层代理方法
     * 
     * @param object
     * @param tModel
     * @param bmoUniqueIdentifier
     * @param o
     */
    public DmoProxy(IDMO object, TableModel tModel, String bmoUniqueIdentifier, Object... o) {
        this.object = object;
        this.tModel = tModel;
        this.proxyDBCMD = true;
        this.bmoUniqueIdentifier = bmoUniqueIdentifier;
        this.o = o;
    }

    /*
     * (non-Javadoc) 暂只实现同一种数据库，未实现同时对多种类型数据库的支持，将在后期补充
     * 
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method,
     * java.lang.Object[])
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object o = null;
        try {
            o = method.invoke(object, args);// 调用真实对象的代理方法
        } finally {
            if (object instanceof IJDBC_DMO) {
                IJDBC_DMO jdbc = (IJDBC_DMO) object;
                // 如果返回对象为空
                PreparedStatement pstmt = jdbc.getPstmt();
                ResultSet re = jdbc.getRs();
                CallableStatement stmt = jdbc.getStmt();
                while (true) {
                    try {
                        if (re != null) {
                            try {
                                if (re.getStatement() != null)
                                    re.getStatement().close();
                            } catch (Exception e2) {
                                e2.printStackTrace();
                            }
                            try {
                                if (re != null)
                                    re.close();
                                re = null;
                            } catch (Exception e2) {
                            }
                        }
                        if (stmt != null) {
                            stmt.close();
                            stmt = null;
                        }
                        if (pstmt != null) {
                            pstmt.close();
                            pstmt = null;
                        }
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                    if (pstmt == null) {
                        break;
                    }
                    try {
                        Thread.sleep(30);// System.out.println("等待关闭连接资源。。。");
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        // System.out.println("dmo_调用真实对象之后。。。。。");
        // if (method.getReturnType().toString().equals("booblean")) {
        // o=false;
        // }else if
        // (method.getReturnType().getSimpleName().toString().equals("tableModel"))
        // {
        //
        // }
        return o;
    }

}
