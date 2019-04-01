package org.oiue.service.odp.event.dmo.postgresql;

import java.util.List;
import java.util.Map;

import org.oiue.service.log.Logger;
import org.oiue.service.odp.dmo.DMO;
import org.oiue.service.odp.dmo.IDMO;
import org.oiue.service.odp.event.api.EventConvertService;
import org.oiue.service.odp.event.api.EventField;
import org.oiue.service.odp.event.sql.structure.StructureService;
import org.oiue.table.structure.StructureTable;
import org.oiue.table.structure.TableModel;
import org.oiue.tools.StatusResult;
import org.oiue.tools.exception.OIUEException;
import org.oiue.tools.map.MapUtil;
import org.oiue.tools.sql.SQL;

@SuppressWarnings({ "serial", "rawtypes", "unchecked" })
public class EventConvertServiceImpl extends DMO implements EventConvertService {
	
	protected static Logger logger;
	protected static StructureService structureService;
	
	@Override
	public List Query(TableModel tm) {
		switch (tm.getCmdKey()) {
			
		}
		return null;
	}
	
	@Override
	public TableModel QueryObj(TableModel tm) {
		SQL s;
		String sql;
		switch (tm.getCmdKey()) {
			case 0:
				sql = "select sep.*,se.type as event_type from fm_service_event se, fm_service s,fm_bundle_service bs,fm_bundle b,fm_data_source ds,fm_service_event_parameters sep " + "where se.name =[" + EventField.event_name + "] and s.name=[" + EventField.service_name + "] and bs.name=[" + EventField.bundle_service_name + "] and b.name=[" + EventField.bundle_name + "] and ds.name=[" + EventField.data_source_name + "] and " + "se.service_id = s.service_id and s.bundle_service_id=bs.bundle_service_id and bs.bundle_id =b.bundle_id and ds.data_type_class_id=sep.data_type_class_id and se.service_event_id = sep.service_event_id";
				s = this.AnalyzeSql(sql, tm);
				this.execute(s.sql, s.pers);
				break;
			
			case 10:
				sql = "select sep.*,se.type as event_type,ds.name as event_dbtype from fm_service_event se,fm_data_type_class ds,fm_service_event_parameters sep " + "where sep.service_event_id =[" + EventField.service_event_id + "] and ds.name=[" + EventField.data_type_name + "] and ds.data_type_class_id=sep.data_type_class_id and se.service_event_id = sep.service_event_id";
				s = this.AnalyzeSql(sql, tm);
				this.execute(s.sql, s.pers);
				break;
			case 11:
				sql = "select sep.*,se.type as event_type,ds.name as event_dbtype from fm_service_event se,fm_data_source ds,fm_service_event_parameters sep " + "where sep.service_event_id =[" + EventField.service_event_id + "] and ds.name=[" + EventField.data_source_name + "] and ds.data_type_class_id=sep.data_type_class_id and se.service_event_id = sep.service_event_id";
				s = this.AnalyzeSql(sql, tm);
				this.execute(s.sql, s.pers);
				break;
			case 12:
				sql = "select (select array_to_json(array_agg(row_to_json(s)))::TEXT from ( select sep.*,ds.name as event_dbtype from fm_service_event_parameters sep,fm_data_source ds where ds.data_type_class_id=sep.data_type_class_id and sep.service_event_id =se.service_event_id and ds.name=[" + EventField.data_source_name + "] order by rule) s) events,se.type as event_type from fm_service_event se  where se.service_event_id =[" + EventField.service_event_id + "] and 1=1";
				s = this.AnalyzeSql(sql, tm);
				this.execute(s.sql, s.pers);
				break;
			case 13:
//				sql = "select (select array_to_json(array_agg(row_to_json(s)))::TEXT from (select sep.*,ds.name as event_dbtype,(select array_to_json(array_agg(row_to_json(sec)))::TEXT from (select * from fm_service_event_config sec where sec.service_event_id=sep.service_event_id and sec.config_type='filtration' order by sort) sec) as filtration from fm_service_event_parameters sep,fm_data_source ds where ds.data_type_class_id=sep.data_type_class_id and sep.service_event_id =se.service_event_id and ds.name=[" + EventField.data_source_name + "] order by rule) s) events,se.type as event_type from fm_service_event se  where se.service_event_id =[" + EventField.service_event_id + "]";
				sql = "select * from _oiue_service_event ([" + EventField.data_source_name + "],[" + EventField.service_event_id + "])";
				s = this.AnalyzeSql(sql, tm);
				this.execute(s.sql, s.pers);
				break;
			
			case 20:
				sql = "select sep.* from fm_service_event se,fm_service_event_parameters sep " + "where sep.service_event_parameters_id =[" + EventField.event_parameters_id + "] and se.service_event_id = sep.service_event_id";
				s = this.AnalyzeSql(sql, tm);
				this.execute(s.sql, s.pers);
				break;
		}
		try {
			if (this.getRs().next()) {
				tm.set(this.getRs());
			}
		} catch (Throwable e) {
			throw new OIUEException(StatusResult._blocking_errors, tm, e);
		} finally {
			this.close();
		}
		return tm;
	}
	
	@Override
	public List<Map<?, ?>> convert(Map<?, ?> event, Map<String, Object> data) {
		if (event == null) {
			throw new RuntimeException("event can't be null");
		}
		return structureService.parse(event, data);
	}
	
	@Override
	public Map<?, ?> query(Map<?, ?> event_query) {
		if (event_query == null) {
			throw new RuntimeException("event can't be null");
		}
		TableModel tm = new StructureTable();
		tm.setCmdKey(MapUtil.getInt(((Map<String, Object>) event_query), "cmd_key"));
		tm.put(event_query);
		
		tm = this.QueryObj(tm);
		return tm.getMapData();
	}
	
	@Override
	public IDMO clone() throws CloneNotSupportedException {
		return new EventConvertServiceImpl();
	}
	
}
