package org.oiue.service.odp.event.dmo.mysql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.oiue.service.odp.dmo.mysql.DMO;
import org.oiue.service.odp.event.api.EventConvertService;
import org.oiue.service.odp.event.api.EventField;
import org.oiue.service.odp.event.dmo.mysql.t.ParserFactory;
import org.oiue.service.odp.event.dmo.mysql.t.Token;
import org.oiue.table.structure.StructureTable;
import org.oiue.table.structure.TableModel;
import org.oiue.tools.map.MapUtil;
import org.oiue.tools.sql.SQL;

@SuppressWarnings({ "serial", "rawtypes", "unchecked" })
public class EventConvertServiceImpl extends DMO implements EventConvertService {

    @Override
    public List Query(TableModel tm) throws Throwable {
        switch (tm.getCmdKey()) {

        }
        return null;
    }

    @Override
    public TableModel QueryObj(TableModel tm) throws Throwable {
        SQL s;
        switch (tm.getCmdKey()) {
            case 0:
                sql = "select sep.*,se.type as event_type from fm_service_event se, fm_service s,fm_bundle_service bs,fm_bundle b,fm_data_source ds,fm_service_event_parameters sep " + "where se.name =["+EventField.event_name+"] and s.name=["+EventField.service_name+"] and bs.name=["+EventField.bundle_service_name+"] and b.name=["+EventField.bundle_name+"] and ds.name=["+EventField.data_source_name+"] and "
                        + "se.service_id = s.service_id and s.bundle_service_id=bs.bundle_service_id and bs.bundle_id =b.bundle_id and ds.data_type_class_id=sep.data_type_class_id and se.service_event_id = sep.service_event_id";
                s = this.AnalyzeSql(sql, tm);
                pstmt = this.getConn().prepareStatement(s.sql);
                this.setQueryParams(s.pers);
                break;

            case 10:
                sql = "select sep.*,se.type as event_type from fm_service_event se,fm_data_source ds,fm_service_event_parameters sep " + "where sep.service_event_id =["+EventField.service_event_id+"] and ds.name=["+EventField.data_source_name+"] and ds.data_type_class_id=sep.data_type_class_id and se.service_event_id = sep.service_event_id";
                s = this.AnalyzeSql(sql, tm);
                pstmt = this.getConn().prepareStatement(s.sql);
                this.setQueryParams(s.pers);
                break;

            case 20:
                sql = "select sep.* from fm_service_event se,fm_service_event_parameters sep " + "where sep.service_event_parameters_id =["+EventField.event_parameters_id+"] and se.service_event_id = sep.service_event_id";
                s = this.AnalyzeSql(sql, tm);
                pstmt = this.getConn().prepareStatement(s.sql);
                this.setQueryParams(s.pers);
                break;
        }
        try {
            if (pstmt != null) {
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    tm.set(rs);
                }
            }
        } catch (Throwable e) {
            throw e;
        } finally {
            if (rs != null)
                rs.close();
            if (pstmt != null)
                pstmt.close();
            rs = null;
            pstmt = null;
        }
        return tm;
    }

    @Override
    public List<Map<?, ?>> convert(Map<?, ?> event, Map<String, Object> data) throws Throwable {
        if (event == null) {
            throw new RuntimeException("event can't be null");
        }

        Token token = ParserFactory.creatToken(MapUtil.getVauleMatchCase(event, "content")+ "", data);
        data.put(EventField.content, token.getSql());
        if (token.getCountSql() != null) {
            data.put(EventField.contentCount, token.getCountSql());
        }
        if (!token.getConditionList().isEmpty()) {
            data.put(EventField.contentList, token.getConditionList());
        }
        data.put(EventField.event_type, MapUtil.getVauleMatchCase(event, "event_type"));
        List rtn = new ArrayList<>();
        rtn.add(data);
        return rtn;
    }

    @Override
    public Map<?, ?> query(Map<?, ?> event_query) throws Throwable {
        if (event_query == null) {
            throw new RuntimeException("event can't be null");
        }
        TableModel tm = new StructureTable();
        tm.setCmdKey(MapUtil.getInt(((Map<String, Object>) event_query), "cmd_key"));
        tm.put(event_query);

        tm = this.QueryObj(tm);
        return tm.getMapData();
    }

}
