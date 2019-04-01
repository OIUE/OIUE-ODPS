package org.oiue.service.odp.dmo;

import java.io.Serializable;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

//import org.apache.log4j.Logger;

/**
 * 
 * <p>
 * BlobType.java
 * </p>
 * <p>
 * Blob类型字段
 * </p>
 * 
 * @author 孙天策
 * @since 2008-7-21 下午03:00:32
 */
@SuppressWarnings("serial")
public class BlobType implements Serializable {
	// private transient Logger logger = Logger.getLogger(BlobType.class);
	
	public BlobType() {
		
	}
	
	/**
	 * 
	 * 给statement加blob类型参数
	 * 
	 * @param st PreparedStatement
	 * @param obj Object
	 * @param index int @ 设置异常
	 * @author 孙天策
	 * @throws SQLException
	 */
	public void set(PreparedStatement st, Object obj, int index) throws SQLException {
		if (obj == null) {
			st.setNull(index, Types.BLOB);
		} else {
			if (obj instanceof Blob) {
				// st.setBlob(index, (Blob)obj);
				// st.setBinaryStream(2, ((BlobImpl)obj).getBinaryStream(),
				// ((BlobImpl)obj).getBinaryStream().available());
				Blob blob = (Blob) obj;
				st.setBinaryStream(index, blob.getBinaryStream(), (int) blob.length());
			} else {
				// logger.debug("It is not Blob type.");
			}
		}
	}
	
	/**
	 * 
	 * 得到blob类型字段对象
	 * 
	 * @param rs ResultSet
	 * @param name String
	 * @return Blob
	 * @throws SQLException 获取异常
	 * @author 孙天策
	 */
	public Blob get(ResultSet rs, String name) throws SQLException {
		return rs.getBlob(name);
	}
	
	/**
	 * 
	 * 得到blob类型字段对象
	 * 
	 * @param rs ResultSet
	 * @param index int
	 * @return Blob
	 * @throws SQLException 获取数据异常
	 * @author 孙天策
	 */
	public Blob get(ResultSet rs, int index) throws SQLException {
		return rs.getBlob(index);
	}
}
