package org.oiue.service.odp.dmo.h2;

import org.oiue.service.odp.dmo.p.IDMO_DB;
import org.oiue.service.odp.dmo.p.JDBC_DMO;
import org.oiue.table.structure.TableModel;
import org.oiue.tools.sql.SQL;

@SuppressWarnings("serial")
public abstract class DMO_DB extends JDBC_DMO implements IDMO_DB {

	public SQL getCutPageSQL(String sql,TableModel tm) throws RuntimeException{
		StringBuffer s = new StringBuffer(sql);
		if (sql == null) {
            return null;
		}
		if (sql.toLowerCase().indexOf(" limit ") < 0) {
			//s.append(" limit " +(first-1<0?first:first-1)+","+max);
			//((Integer)model.getValue("pageIndex")-1)*(Integer)model.getValue("pageRecNumber")+","+(Integer)model.getValue("pageRecNumber")
			try {
				s.append(" limit ").append((Integer)tm.getValue("pageRecNumber") ).append(" offset ").append((((Integer)tm.getValue("pageIndex")-1)*(Integer)tm.getValue("pageRecNumber")));
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}
        SQL sq = new SQL();
        sq.sql = "" + s;
        return sq;
	}
}