package org.oiue.service.odp.dmo.p;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;

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
}