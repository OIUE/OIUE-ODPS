package org.oiue.service.odp.dmo;

import java.io.Serializable;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

//import org.apache.log4j.Logger;
/**
 * 
 * <p>
 * ClobType.java
 * </p>
 * <p>
 * 处理数据类型是clob的字段
 * </p>
 * 
 * @author 孙天策
 * @since 2008-7-21 下午02:51:32
 */
@SuppressWarnings("serial")
public class ClobType implements Serializable {
	// private transient Logger logger = Logger.getLogger(ClobType.class);

	public ClobType() {
	}

	/**
	 * 
	 * 给statement设置clob类型参数
	 * 
	 * @param st
	 *            PreparedStatement
	 * @param obj
	 *            Object
	 * @param index
	 *            int
	 * @throws SQLException
	 * @author 孙天策
	 */
	public void set(PreparedStatement st, Object obj, int index) throws SQLException {
		if (obj == null) {
			st.setNull(index, Types.CLOB);
		} else {
			if (obj instanceof Clob) {
				Clob clob = (Clob) obj;
				st.setCharacterStream(index, clob.getCharacterStream(), (int) clob.length());
			} else {
				// logger.debug("It is not Clob type.");
			}
		}
	}

	/**
	 * 
	 * 得到clob类型字段对象
	 * 
	 * @param rs
	 *            ResultSet
	 * @param name
	 *            String
	 * @return Clob
	 * @throws SQLException
	 * @author 孙天策
	 */
	public Clob get(ResultSet rs, String name) throws SQLException {
		return rs.getClob(name);
	}

	/**
	 * 
	 * 得到clob类型字段对象
	 * 
	 * @param rs
	 *            ResultSet
	 * @param index
	 *            int
	 * @return Clob
	 * @throws SQLException
	 * @author 孙天策
	 */
	public Clob get(ResultSet rs, int index) throws SQLException {
		return rs.getClob(index);
	}
}
