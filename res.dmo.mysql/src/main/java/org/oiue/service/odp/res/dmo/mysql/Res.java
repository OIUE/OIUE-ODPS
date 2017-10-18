package org.oiue.service.odp.res.dmo.mysql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.oiue.service.odp.dmo.DMO;
import org.oiue.service.odp.dmo.IDMO;
import org.oiue.service.odp.res.dmo.IRes;
import org.oiue.table.structure.TableExt;
import org.oiue.table.structure.TableModel;
import org.oiue.tools.sql.SQL;

@SuppressWarnings({ "rawtypes","unchecked"})
public class Res extends DMO implements IRes {
	private static final long serialVersionUID = 1999L;
	@Override
	public boolean haveTable(String name) throws Throwable {
		return false;
	}

	@Override
	public boolean updateTable(TableExt arg0) throws Throwable {
		return false;
	}

	@Override
	public boolean updateTable(TableExt arg0, TableExt arg1) throws Throwable {
		return false;
	}

	@Override
	public List Query(TableModel tm) throws Throwable {
		List list = new ArrayList();
		String sql;
		boolean returnMap = tm.getValue("returnMap") != null;
		switch (tm.getCmdKey()) {
		case 0:
			sql = "select sep.*,dtc.name data_type_class,se.name service_event "
					+ "from fm_service_event_parameters  sep,data_type_class dtc,service_event se "
					+ "where se.service_event_id=sep.service_event_id and dtc.data_type_class_id=sep.data_type_class_id ";
			// sql = this.AnalyzeSql(sql, tm);
			this.execute(sql, null);
			break;
		case 1:
			sql = "select t.type as fm_type, t.content as fm_content, t.expression as fm_expression, t.remark as fm_cache_remark, m.name as fm_name, m.entity_id as fm_entity_id, m.alias as fm_alias, m.remark as fm_remark, m.config_type as fm_config_type, m.type_name as fm_type_name"
					+ " from (select se.type, sep.content, sep.expression,sep.remark,sep.service_event_id,s.bundle_service_id"
					+ " from fm_service_event se, fm_service_event_parameters sep, fm_data_source ds, fm_service s"
					+ " where se.service_event_id = sep.service_event_id and sep.data_type_class_id = ds.data_type_class_id and s.service_id = se.service_id and s.service_id = sep.service_id"
					+ " and se.name=? and ds.name=? and s.service_id=? and s.bundle_service_id=?) t"
					+ " LEFT JOIN (select fsec.bundle_service_id,fsec.service_event_id,fec.name,fec.entity_id,fsec.alias,fsec.remark,fsec.config_type,fdt.name as type_name"
					+ " from fm_service_event_config fsec, fm_entity_column fec, fm_data_type fdt"
					+ " where fsec.entity_id = fsec.entity_id and fsec.entity_column_id = fec.entity_column_id and fsec.data_type_id = fdt.data_type_id) m"
					+ " on t.service_event_id = m.service_event_id and t.bundle_service_id = m.bundle_service_id";
			String sqlT = "select t.type as fm_type, t.content as fm_content, t.expression as fm_expression, t.remark as fm_cache_remark, m.name as fm_name, m.entity_id as fm_entity_id, m.alias as fm_alias, m.remark as fm_remark, m.config_type as fm_config_type, m.type_name as fm_type_name"
					+ " from (select se.type, sep.content, sep.expression,sep.remark,sep.service_event_id"
					+ " from fm_service_event se, fm_service_event_parameters sep, fm_data_source ds"
					+ " where se.service_event_id = sep.service_event_id and sep.data_type_class_id = ds.data_type_class_id and se.service_id = sep.service_id"
					+ " and se.name=? and ds.name=? and se.service_id=?) t"
					+ " LEFT JOIN (select fsec.service_event_id,fec.name,fec.entity_id,fsec.alias,fsec.remark,fsec.config_type,fdt.name as type_name"
					+ " from fm_service_event_config fsec, fm_entity_column fec, fm_data_type fdt"
					+ " where fsec.entity_id = fsec.entity_id and fsec.entity_column_id = fec.entity_column_id and fsec.data_type_id = fdt.data_type_id) m"
					+ " on t.service_event_id = m.service_event_id";
			Collection<Object> pers = new ArrayList<Object>();
			pers.add(tm.getValue("service_event_name"));
			pers.add(tm.getValue("data_source_name"));
			pers.add(tm.getValue("service_id"));
			if(tm.getValue("bundle_service_id") == null) {
				this.execute(sqlT, pers);
			} else {
				pers.add(tm.getValue("bundle_service_id"));
				this.execute(sql, pers);
			}
			break;

			//		case 11:
			//            sql = "select t.type as fm_type, t.content as fm_content, t.expression as fm_expression, t.remark as fm_cache_remark, m.name as fm_name, m.entity_id as fm_entity_id, m.alias as fm_alias, m.remark as fm_remark, m.config_type as fm_config_type, m.type_name as fm_type_name"
			//                    + " from (select se.type, sep.content, sep.expression,sep.remark,sep.service_event_id"
			//                    + " from fm_service_event se, fm_service_event_parameters sep, fm_data_source ds"
			//                    + " where se.service_event_id = sep.service_event_id and sep.data_type_class_id = ds.data_type_class_id and se.service_id = sep.service_id"
			//                    + " and se.name=? and ds.name=? and se.service_id=?) t"
			//                    + " LEFT JOIN (select fsec.service_event_id,fec.name,fec.entity_id,fsec.alias,fsec.remark,fsec.config_type,fdt.name as type_name"
			//                    + " from fm_service_event_config fsec, fm_entity_column fec, fm_data_type fdt"
			//                    + " where fsec.entity_id = fsec.entity_id and fsec.entity_column_id = fec.entity_column_id and fsec.data_type_id = fdt.data_type_id) m"
			//                    + " on t.service_event_id = m.service_event_id";
			//            Collection<Object> pers = new ArrayList<Object>();
			//            pers.add(tm.getValue("service_event_name"));
			//            pers.add(tm.getValue("data_source_name"));
			//            pers.add(tm.getValue("service_id"));
			//            if(tm.getValue("bundle_service_id") == null) {
			//                pstmt = this.getConn().prepareStatement(sqlT);
			//            } else {
			//                pers.add(tm.getValue("bundle_service_id"));
			//                pstmt = this.getConn().prepareStatement(sql);
			//            }
			//            this.setQueryParams(pers);
			//            break;

		}
		try {
			if(returnMap) {
				list = getResult(this.getRs());
			} else {
				while (this.getRs().next()) {
					TableModel models = tm.getClass().newInstance();
					models.set(this.getRs());
					list.add(models);
				}
			}
		} catch (Throwable e) {
			throw e;
		} finally {
			this.close();
		}
		return list;
	}

	@Override
	public TableModel QueryObj(TableModel tm) throws Throwable {
		SQL s;
		String sql;
		switch (tm.getCmdKey()) {
		case 0:
			sql = "select * from fm_service_event_parameters where data_type_class_id=[data_type_class_id] and service_event_id=[service_event_id]";
			s = this.AnalyzeSql(sql, tm);
			this.execute(s.sql, s.pers);
			break;
		case 1:
			sql = "select sep.* from fm_service_event_parameters  sep,data_type_class dtc,service_event se where se.service_event_id=sep.service_event_id and dtc.data_type_class_id=sep.data_type_class_id and dtc.name=[data_type_class] and se.name=[service_event]";
			s = this.AnalyzeSql(sql, tm);
			this.execute(s.sql, s.pers);
			break;
		}
		try {
			while (this.getRs().next()) {
				tm.set(this.getRs());
			}
		} catch (Throwable e) {
			throw e;
		} finally {
			this.getRs();
		}
		return tm;
		// return super.QueryObj(tm);
	}

	@Override
	public IDMO clone() throws CloneNotSupportedException {
		return new Res();
	}
}