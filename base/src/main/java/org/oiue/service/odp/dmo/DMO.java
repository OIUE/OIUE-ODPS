package org.oiue.service.odp.dmo;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.oiue.service.odp.dmo.p.IJDBC_DMO;
import org.oiue.service.odp.proxy.ProxyFactory;
import org.oiue.table.structure.TableModel;
import org.oiue.tools.StatusResult;
import org.oiue.tools.serializ.CloneTools;
import org.oiue.tools.sql.SQL;

/**
 * 用户实现的各数据库个性化接口方法
 * @author Every
 *
 */
@SuppressWarnings({ "serial", "unchecked", "rawtypes" })
public abstract class DMO implements IDMO {
	private ProxyFactory proxyFactory = ProxyFactory.getInstance();
	
	public <T> T getIDMO(String name, Object... o) {
		IDMO dmo = proxyFactory.getOp().getIDMO(name, proxyFactory.getOp().getData_source_class(getConn()), o);
		((IJDBC_DMO) idmo).setConn(getConn());
		return (T) dmo;
	}

	private IDMO_DB idmo;
	
	@Override
	public abstract IDMO clone() throws CloneNotSupportedException;
	
	@Override
	public void setIdmo(IDMO_DB idmo) {
		this.idmo = idmo;
	}
	
	@Override
	public IDMO_DB getIdmo() {
		return idmo;
	}
	
	@Override
	public SQL getCutPageSQL(String sql, TableModel tm) {
		return idmo.getCutPageSQL(sql, tm);
	}
	
	@Override
	public SQL getRowCountSQL(String sql, TableModel rm) {
		return idmo.getRowCountSQL(sql, rm);
	}
	
	@Override
	public SQL getInsertSql(TableModel tm) {
		return idmo.getInsertSql(tm);
	}
	
	@Override
	public SQL getInsertSql(Map dm, String tableName) {
		return idmo.getInsertSql(dm, tableName);
	}
	
	@Override
	public SQL getUpdateSql(TableModel tm) {
		return idmo.getUpdateSql(tm);
	}
	
	@Override
	public SQL getDelSql(TableModel tm) {
		return idmo.getDelSql(tm);
	}
	
	@Override
	public SQL getQuerySql(TableModel tm) {
		return idmo.getQuerySql(tm);
	}
	
	@Override
	public SQL getQuerySql(TableModel tm, String scope) {
		return idmo.getQuerySql(tm, scope);
	}
	
	@Override
	public SQL AnalyzeSql(String sourceStr, TableModel tb) {
		return idmo.AnalyzeSql(sourceStr, tb);
	}
	
	@Override
	public boolean Update(TableModel tm) {
		return idmo.Update(tm);
	}
	
	@Override
	public boolean Update(List<TableModel> tm) {
		return idmo.Update(tm);
	}
	
	@Override
	public boolean UpdateTree(TableModel tm) {
		return idmo.UpdateTree(tm);
	}
	
	@Override
	public List Query(TableModel tm) {
		return idmo.Query(tm);
	}
	
	@Override
	public TableModel QueryObj(TableModel tm) {
		return idmo.QueryObj(tm);
	}
	
	/**
	 * Method 复制对象
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@Override
	public Object deepCopy() throws ClassNotFoundException, IOException {
		return CloneTools.clone(this);
	}
	
	@Override
	public Connection getConn() {
		return idmo.getConn();
	}
	
	@Override
	public void setConn(Connection conn) {
		idmo.setConn(conn);
	}
	
	@Override
	public StatusResult execute(String sql, Collection queryParams) {
		return idmo.execute(sql, queryParams);
	}
	
	@Override
	public ResultSet getRs() {
		return idmo.getRs();
	}
	
	@Override
	public PreparedStatement getPstmt() {
		return idmo.getPstmt();
	}
	
	@Override
	public void setPstmt(PreparedStatement pstmt) {
		idmo.setPstmt(pstmt);;
	}
	
	@Override
	public void setQueryParams(Collection queryParams) {
		idmo.setQueryParams(queryParams);
	}
	
	@Override
	public CallableStatement getStmt() {
		return idmo.getStmt();
	}
	
	@Override
	public boolean close() {
		return idmo.close();
	}
	
	@Override
	public List<Map> getResult(ResultSet rs) {
		return idmo.getResult(rs);
	}
	
	@Override
	public Map getMapResult(ResultSet rs) {
		return idmo.getMapResult(rs);
	}
}