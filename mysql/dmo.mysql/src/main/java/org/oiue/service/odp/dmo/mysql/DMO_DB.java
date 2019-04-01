package org.oiue.service.odp.dmo.mysql;

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
			// s.append(" limit " +(first-1<0?first:first-1)+","+max);
			// ((Integer)model.getValue("pageIndex")-1)*(Integer)model.getValue("pageRecNumber")+","+(Integer)model.getValue("pageRecNumber")
			try {
				s.append(" limit ").append((((Integer) tm.getValue("pageIndex") - 1) * (Integer) tm.getValue("pageRecNumber"))).append(",").append(tm.getValue("pageRecNumber"));
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}
		
		SQL sq = new SQL();
		sq.sql = "" + s;
		return sq;
	}
}