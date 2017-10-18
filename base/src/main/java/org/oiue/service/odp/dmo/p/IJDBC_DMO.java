package org.oiue.service.odp.dmo.p;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.oiue.tools.StatusResult;

public interface IJDBC_DMO extends Serializable {
	Connection getConn();

	void setConn(Connection conn);

	ResultSet getRs();

	StatusResult execute(String sql,Collection queryParams);

	//	void setRs(ResultSet rs);

	PreparedStatement getPstmt();

	//	void setPstmt(PreparedStatement pstmt);

	CallableStatement getStmt();

	//	void setStmt(CallableStatement stmt);

	boolean close();

	List<Map> getResult(ResultSet rs) throws SQLException;
	Map getMapResult(ResultSet rs) throws SQLException;
}
