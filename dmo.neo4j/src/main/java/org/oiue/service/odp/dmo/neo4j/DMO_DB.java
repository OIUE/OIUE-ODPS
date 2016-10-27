package org.oiue.service.odp.dmo.neo4j;

import java.util.List;

import org.oiue.service.odp.dmo.p.DMO_USR;
import org.oiue.service.odp.dmo.p.IDMO_DB;
import org.oiue.table.structure.TableModel;
import org.oiue.tools.sql.SQL;

@SuppressWarnings("serial")
public abstract class DMO_DB extends DMO_USR implements IDMO_DB {

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

    @Override
    public boolean Update(TableModel tm) throws Throwable {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean Update(List<TableModel> tm) throws Throwable {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean UpdateTree(TableModel tm) throws Throwable {
        // TODO Auto-generated method stub
        return false;
    }
}
