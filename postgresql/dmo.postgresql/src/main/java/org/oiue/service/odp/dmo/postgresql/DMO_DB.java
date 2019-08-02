package org.oiue.service.odp.dmo.postgresql;

import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.oiue.service.odp.dmo.IDMO_DB;
import org.oiue.service.odp.dmo.p.DMO_ROOT;
import org.oiue.table.structure.TableModel;
import org.oiue.tools.StatusResult;
import org.oiue.tools.exception.OIUEException;
import org.oiue.tools.json.JSONUtil;
import org.oiue.tools.sql.SQL;
import org.postgresql.util.PGobject;

@SuppressWarnings("serial")
public class DMO_DB extends DMO_ROOT implements IDMO_DB {
	@Override
	public IDMO_DB clone() throws CloneNotSupportedException {
		return new DMO_DB();
	}
	
	@Override
	public SQL getCutPageSQL(String sql, TableModel tm)  {
		StringBuffer s = new StringBuffer(sql);
		if (sql == null) {
			return null;
		}
		/**
		 * select * from persons limit A offset B; 解释：A就是你需要多少行；B就是查询的起点位置
		 */
		if (sql.toLowerCase().indexOf(" limit ") < 0) {
			// s.append(" limit " +(first-1<0?first:first-1)+","+max);
			// ((Integer)model.getValue("pageIndex")-1)*(Integer)model.getValue("pageRecNumber")+","+(Integer)model.getValue("pageRecNumber")
			int pageIndex = 1;
			int pageRecNumber=20;
			try {
				pageIndex =(Integer) tm.getValue("pageIndex");
			} catch (Throwable e) {}
			try {
				pageRecNumber=(Integer) tm.getValue("pageRecNumber");
			} catch (Throwable e) {}
			s.append(" limit ").append((( pageIndex - 1) * pageRecNumber)).append(",").append(pageRecNumber);
		}
		
		SQL sq = new SQL();
		sq.sql = s.toString();
		return sq;
	}
	
	@Override
	public Map getMapResult(ResultSet rs) {
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			int sum = rsmd.getColumnCount();
			Hashtable row = new Hashtable();
			for (int i = 1; i < sum + 1; i++) {
				Object value = rs.getObject(i);
				if ((value instanceof BigDecimal)) {
					if (((BigDecimal) value).scale() == 0) {
						value = Long.valueOf(((BigDecimal) value).longValue());
					} else {
						value = Double.valueOf(((BigDecimal) value).doubleValue());
					}
				} else if ((value instanceof Clob)) {
					value = clobToString((Clob) value);
				} else if (value instanceof PGobject) {
					if ("json".equals(((PGobject) value).getType())) {
						value = ((PGobject) value).getValue();
						if (value.toString().startsWith("{")) {
							value = JSONUtil.parserStrToMap(value.toString());
						} else {
							value = JSONUtil.parserStrToList(value.toString());
						}
					}
				}
				
				// String key = rsmd.getColumnName(i);
				String key = rsmd.getColumnLabel(i);
				row.put(key, value == null ? "" : value);
			}
			return row;
		} catch (Exception e) {
			throw new OIUEException(StatusResult._blocking_errors, "", e);
		}
	}
	
	@Override
	public List<Map> getResult(ResultSet rs) {
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			List<Map> listMap = new ArrayList<Map>();
			while (rs.next()) {
				int sum = rsmd.getColumnCount();
				Hashtable row = new Hashtable();
				for (int i = 1; i < sum + 1; i++) {
					Object value = rs.getObject(i);
					if ((value instanceof BigDecimal)) {
						if (((BigDecimal) value).scale() == 0) {
							value = Long.valueOf(((BigDecimal) value).longValue());
						} else {
							value = Double.valueOf(((BigDecimal) value).doubleValue());
						}
					} else if (value instanceof PGobject) {
						if ("json".equals(((PGobject) value).getType())) {
							value = ((PGobject) value).getValue();
							if (value.toString().startsWith("{")) {
								value = JSONUtil.parserStrToMap(value.toString());
							} else {
								value = JSONUtil.parserStrToList(value.toString());
							}
						}
					} else if ((value instanceof Clob)) {
						value = clobToString((Clob) value);
					}
					// String key = rsmd.getColumnName(i);
					String key = rsmd.getColumnLabel(i);
					row.put(key, value == null ? "" : value);
				}
				listMap.add(row);
			}
			return listMap;
			
		} catch (Exception e) {
			throw new OIUEException(StatusResult._blocking_errors, "", e);
		}
	}
}
