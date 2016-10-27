package org.oiue.service.odp.dmo;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public interface IJDBC_DMO extends IDMO {
    Connection getConn();

    void setConn(Connection conn);

    ResultSet getRs();

    void setRs(ResultSet rs);

    PreparedStatement getPstmt();

    void setPstmt(PreparedStatement pstmt);

    CallableStatement getStmt();

    void setStmt(CallableStatement stmt);
}
