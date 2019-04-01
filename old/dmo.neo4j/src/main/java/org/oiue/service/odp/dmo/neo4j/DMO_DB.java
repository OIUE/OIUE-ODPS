package org.oiue.service.odp.dmo.neo4j;

import org.oiue.service.odp.dmo.IDMO_DB;
import org.oiue.service.odp.dmo.p.DMO_ROOT;
import org.oiue.table.structure.TableModel;
import org.oiue.tools.sql.SQL;

@SuppressWarnings("serial")
public class DMO_DB extends DMO_ROOT implements IDMO_DB {
	@Override
	public IDMO_DB clone() throws CloneNotSupportedException {
		return new DMO_DB();
	}
	
	@Override
	public SQL getCutPageSQL(String sql, TableModel tm) throws RuntimeException {
		StringBuffer s = new StringBuffer(sql);
		if (sql == null) {
			return null;
		}
		if (sql.toLowerCase().indexOf(" limit ") < 0) {
			try {
				int pageRecNumber = (Integer) tm.getValue("pageRecNumber");
				int pageIndex = (Integer) tm.getValue("pageIndex");
				int start = ((pageIndex - 1) * (Integer) tm.getValue("pageRecNumber"));
				
				s.append(" skip ").append(start).append(" limit ").append(pageRecNumber);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}
		SQL sq = new SQL();
		sq.sql = "" + s;
		return sq;
	}
}
