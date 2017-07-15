package org.oiue.service.odp.res.dmo.neo4j;

import java.util.ArrayList;
import java.util.List;

import org.oiue.service.odp.dmo.DMO;
import org.oiue.service.odp.dmo.IDMO;
import org.oiue.service.odp.res.dmo.IRes;
import org.oiue.table.structure.TableExt;
import org.oiue.table.structure.TableModel;
import org.oiue.tools.sql.SQL;

public class Res extends DMO implements IRes {
	private static final long serialVersionUID = 1L;

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

	@SuppressWarnings({ "rawtypes" })
	@Override
	public List Query(TableModel tm) throws Throwable {
		List<TableModel> list = new ArrayList<TableModel>();
		switch (tm.getCmdKey()) {
		case 0:
			String sql = "select sep.*,dtc.name data_type_class,se.name service_event from service_event_parameters  sep,data_type_class dtc,service_event se where se.service_event_id=sep.service_event_id and dtc.data_type_class_id=sep.data_type_class_id";
			this.execute(sql, null);
			break;
		}
		try {
			while (this.getRs().next()) {
				TableModel models = tm.getClass().newInstance();
				models.set(this.getRs());
				list.add(models);
			}
		} catch (Throwable e) {
			throw e;
		} finally {
			this.close();
		}
		//		if (list.size() > 0)
		return list;
		//		return super.Query(tm);
	}

	@Override
	public TableModel QueryObj(TableModel tm) throws Throwable {
		SQL s;
		String sql;
		switch (tm.getCmdKey()) {
		case 0:
			sql = "select * from service_event_parameters where data_type_class_id=[data_type_class_id] and service_event_id=[service_event_id]";
			s = this.AnalyzeSql(sql, tm);
			this.execute(s.sql, s.pers);
			break;
		case 1:
			sql = "select sep.* from service_event_parameters  sep,data_type_class dtc,service_event se where se.service_event_id=sep.service_event_id and dtc.data_type_class_id=sep.data_type_class_id and dtc.name=[data_type_class] and se.name=[service_event]";
			s = this.AnalyzeSql(sql, tm);
			this.execute(s.sql, s.pers);
			break;
		case 2:
			sql = "select sep.*,se.type from service_event_parameters  sep,data_source  ds,service_event  se where sep.data_type_class_id = ds.data_type_class_id and sep.service_event_id =se.service_event_id and se.name=[service_event_name] and ds.name=[data_source_name]";
			s = this.AnalyzeSql(sql, tm);
			this.execute(s.sql, s.pers);
			break;

		default:
			sql=tm.getValueStr("CONTENT");
			this.execute(sql, tm.getValueListByKey("EXPRESSION"));
		}
		try {
			while (this.getRs().next()) {
				tm.set(this.getRs());
			}
		} catch (Throwable e) {
			throw e;
		} finally {
			this.close();
		}
		return tm;
		// return super.QueryObj(tm);
	}

	@Override
	public boolean Update(TableModel tm) throws Throwable {
		return super.Update(tm);
	}
	@Override
	public IDMO clone() throws CloneNotSupportedException {
		return new Res();
	}
}