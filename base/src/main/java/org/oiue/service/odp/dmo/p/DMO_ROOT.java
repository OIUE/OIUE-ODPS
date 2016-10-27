package org.oiue.service.odp.dmo.p;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.oiue.service.odp.proxy.ProxyFactory;

/**
 * DB操作的抽象类
 * 
 * @author Every{王勤}
 */
@SuppressWarnings({ "unused", "serial" })
public abstract class DMO_ROOT implements Serializable, IDMO_ROOT {

    private ProxyFactory proxyFactory = ProxyFactory.getInstance();
    protected String sql = null;

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public DMO_ROOT() {
	}

	/**
	 * Method 复制对象
	 */
	public Object deepCopy() throws Throwable {
		// 将该对象序列化成流,因为写在流里的是对象的一个拷贝，而原对象仍然存在于JVM里面。所以利用这个特性可以实现对象的深拷贝
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(this);
		// 将流序列化成对象
		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
		ObjectInputStream ois = new ObjectInputStream(bis);
		return ois.readObject();
	}

//	/**
//	 * 获取表名
//	 * 
//	 * @return
//	 * @throws Throwable
//	 */
//	public List<String> showTables() throws Throwable {
//		List<String> list = new ArrayList<String>();
//		// 获取所有表
//		rs = this.getConn().getMetaData().getTables(null, "%", "%", new String[] { "TABLE" });
//		while (rs.next()) {
//			list.add(rs.getString("TABLE_NAME"));
//		}
//		return list;
//	}
//
//	/**
//	 * 是否存在该表
//	 * 
//	 * @param name
//	 * @return
//	 * @throws Throwable
//	 */
//	public boolean haveTable(String name) throws Throwable {
//		// 查询指定表是否存在
//		rs = this.getConn().getMetaData().getTables(null, null, name, null);
//		if (rs.next()) {
//			return true;
//		}
//		return false;
//	}
//
//	/**
//	 * 功能: 获得数据库的一些相关信息 作者: Every 创建日期:2012-7-5
//	 * 
//	 * @return
//	 * @throws Throwable 
//	 */
//	public Map getDataBaseInformations() throws Throwable {
//		Map dbis = new HashMap();
//		try {
//			dbMetaData = this.getConn().getMetaData();
//			dbis.put("URL", dbMetaData.getURL());
//			dbis.put("UserName", dbMetaData.getUserName() + ";");
//			dbis.put("isReadOnly", dbMetaData.isReadOnly() + ";");
//			dbis.put("DatabaseProductName", dbMetaData.getDatabaseProductName() + ";");
//			dbis.put("DatabaseProductVersion", dbMetaData.getDatabaseProductVersion() + ";");
//			dbis.put("DriverName", dbMetaData.getDriverName() + ";");
//			dbis.put("DriverVersion", dbMetaData.getDriverVersion());
//		} catch (Throwable e) {
//            throw e;
//		} finally {
//			dbMetaData = null;
//		}
//		return dbis;
//	}
//
//	/**
//	 * 功能:获得该用户下面的所有创建对象 作者: Every 创建日期:2012-7-5
//	 * 
//	 * @param schemaName
//	 * @param types
//	 *            Typical types are "TABLE", "VIEW", "SYSTEM TABLE",
//	 *            "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS","SYNONYM".
//	 * @return
//	 * @throws SQLException 
//	 */
//	public Map getAllObjectList(String schemaName, String[] types) throws SQLException {
//		Map aol = new HashMap();
//		ResultSetMetaData rsmd = null;
//		ResultSet rs = null;
//		try {
//			rs = dbMetaData.getTables(null, schemaName, "%", types);
//			rsmd = rs.getMetaData();
//			int sum = rsmd.getColumnCount();
//			Hashtable row = new Hashtable();
//			for (int i = 1; i < sum + 1; i++) {
//				Object value = rs.getObject(i);
//				if (value instanceof BigDecimal) {
//					value = ((BigDecimal) value).intValue();
//				}
//				String key = rsmd.getColumnName(i);
//				row.put(key, value == null ? "" : value);
//			}
//			while (rs.next()) {
////				Map table = new HashMap();
//				String tableName = rs.getString("TABLE_NAME");
//				String tableType = rs.getString("TABLE_TYPE");
//				String remarks = rs.getString("REMARKS"); // explanatory comment
//															// on the table
//			}
//		} catch (SQLException e) {
//            throw e;
//		}
//		return aol;
//	}
//	
//    /**
//     * 获取持久层操作对象实例
//     * 
//     * @param name
//     * @param connName
//     * @param o
//     * @return
//     */
//    public IDMO_ROOT getIDMO(String name, Object... o) {
//        Connection conn = this.getConn();
//        IDMO_ROOT idmo = proxyFactory.getOp().getIDMOByConn(name, proxyFactory.getData_source_class(conn), o);
//        idmo.setConn(conn);
//        return idmo;
//    }

}