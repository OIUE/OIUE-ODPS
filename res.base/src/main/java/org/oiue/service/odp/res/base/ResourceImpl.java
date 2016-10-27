package org.oiue.service.odp.res.base;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.oiue.service.log.LogService;
import org.oiue.service.log.Logger;
import org.oiue.service.odp.bmo.BMO;
import org.oiue.service.odp.dmo.p.IDMO_ROOT;
import org.oiue.service.odp.event.api.Event;
import org.oiue.service.odp.event.api.EventConvertService;
import org.oiue.service.odp.event.api.EventField;
import org.oiue.service.odp.res.api.IResource;
import org.oiue.service.odp.res.dmo.CallBack;
import org.oiue.service.odp.res.dmo.IRes;
import org.oiue.table.structure.StructureTable;
import org.oiue.table.structure.TableExt;
import org.oiue.table.structure.TableModel;
import org.oiue.tools.string.StringUtil;

@SuppressWarnings({ "serial", "rawtypes" })
public class ResourceImpl extends BMO implements IResource {

    private static Map<String, Map<String, TableModel>> service_events = new Hashtable<String, Map<String, TableModel>>();
    @SuppressWarnings("unused")
    private static Logger logger;
    private static LogService logService;

    public ResourceImpl(LogService logService) {
        logger = logService.getLogger(this.getClass());
        ResourceImpl.logService = logService;
    }
    public ResourceImpl() {}

    @Override
    public boolean Update(TableModel tableModel) throws Throwable {
        IDMO_ROOT dmo = (IDMO_ROOT) this.getIDMO(IRes.class.getName());
        return dmo.Update(tableModel);
    }

    @Override
    public boolean Update(List<TableModel> tm) throws Throwable {
        IDMO_ROOT dmo = (IDMO_ROOT) this.getIDMO(IRes.class.getName());
        return dmo.Update(tm);
    }

    @Override
    public List Query(TableModel tableModel) throws Throwable {
        IDMO_ROOT dmo = (IDMO_ROOT) this.getIDMO(IRes.class.getName());
        return dmo.Query(tableModel);
    }

    @Override
    public TableModel QueryObj(TableModel tableModel) throws Throwable {
        IDMO_ROOT dmo = (IDMO_ROOT) this.getIDMO(IRes.class.getName());
        return dmo.QueryObj(tableModel);
    }

    @Override
    public boolean haveTable(String name) throws Throwable {
        IRes res = (IRes) this.getIDMO(IRes.class.getName());
        return res.haveTable(name);
    }

    @Override
    public boolean updateTable(TableExt dt) throws Throwable {
        IRes res = (IRes) this.getIDMO(IRes.class.getName());
        return res.updateTable(dt);
    }

    @Override
    public boolean updateTable(TableExt dt, TableExt ret) throws Throwable {
        IRes res = (IRes) this.getIDMO(IRes.class.getName());
        return res.updateTable(dt, ret);
    }

    @Override
    public TableModel getEvent(String event_name, String data_type) throws Throwable {
        TableModel returnTM = null;

        Map<String, TableModel> events = service_events.get(data_type);
        if (events != null) {
            returnTM = events.get(event_name);
        }
        if (events == null || returnTM == null) {
            IRes res = (IRes) this.getIDMO(IRes.class.getName());
            if (events == null) {
                StructureTable st = new StructureTable();
                st.setCmdKey(0);
                List rtn = res.Query(st);
                if (rtn != null) {
                    StructureTable t;
                    String en;
                    Map<String, TableModel> enm;
                    String dt;
                    for (Object object : rtn) {
                        t = (StructureTable) object;
                        dt = t.getValue("DATA_TYPE_CLASS") + "";
                        enm = service_events.get(dt);
                        if (enm == null) {
                            enm = new Hashtable<String, TableModel>();
                            service_events.put(dt, enm);
                        }
                        en = t.getValue("SERVICE_EVENT") + "";
                        if (data_type.equals(dt) && event_name.equals(en)) {
                            returnTM = t;
                        }
                        enm.put(en, t);
                    }
                }
            } else {

            }
        }
        return returnTM;
    }

    @Override
    public Object callEvent(String event_name, String data_source_name, String service_name, String bundle_service_name, String bundle_name, Map map) throws Throwable {
        return callEvent(event_name, data_source_name, service_name, bundle_service_name, bundle_name, map, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object callEvent(String event_name, String data_source_name, String service_name, String bundle_service_name, String bundle_name, Map map, CallBack callBack) throws Throwable {
        boolean isNull = event_name == null || event_name.isEmpty() || data_source_name == null || data_source_name.isEmpty() || service_name == null || service_name.isEmpty();
        if (isNull) {
            throw new RuntimeException(this.toString() + " the parameters of callEvent method can't be null or empty");
        }
        if (StringUtil.isEmptys(data_source_name))
            data_source_name = default_data_source_name;
        EventConvertService eventConvertService = (EventConvertService) this.getIDMO(EventConvertService.class.getName());
        Map<String, Object> event_query = new HashMap<>();
        event_query.put(EventField.bundle_name, bundle_name);
        event_query.put(EventField.bundle_service_name, bundle_service_name);
        event_query.put(EventField.service_name, service_name);
        event_query.put(EventField.event_name, event_name);
        event_query.put(EventField.data_source_name, data_source_name);
        event_query.put("cmd_key", 0);

        Map event = eventConvertService.query(event_query);
        EventConvertService eventConvert = (EventConvertService) this.getIDMO(EventConvertService.class.getName(), data_source_name);
        List<Map<?, ?>> events = eventConvert.convert(event, map);

        if (events == null)
            return null;
        if (events.size() == 1) {
            return callEvent(events.get(0), data_source_name, callBack);
        } else {
            List rtn = new ArrayList<>();
            for (Map mapevent : events) {
                rtn.add(callEvent(mapevent, data_source_name, callBack));
            }
            return rtn;
        }
    }

    public Object callEvent(String event_id, String data_source_name, Map map) throws Throwable {
        return this.callEvent(event_id, data_source_name, map, null);
    }

    @SuppressWarnings("unchecked")
    public Object callEvent(String event_id, String data_source_name, Map map, CallBack callBack) throws Throwable {
        if (StringUtil.isEmptys(event_id)) {
            throw new RuntimeException("event_id can't be null or empty");
        }
        if (StringUtil.isEmptys(data_source_name))
            data_source_name = default_data_source_name;
        EventConvertService eventConvertService = (EventConvertService) this.getIDMO(EventConvertService.class.getName());
        Map<String, Object> event_query = new HashMap<>();
        event_query.put(EventField.service_event_id, event_id);
        event_query.put(EventField.data_source_name, data_source_name);
        event_query.put("cmd_key", 10);

        Map event = eventConvertService.query(event_query);
        EventConvertService eventConvert = (EventConvertService) this.getIDMO(EventConvertService.class.getName(), data_source_name);
        List<Map<?, ?>> events = eventConvert.convert(event, map);

        if (events == null)
            return null;
        if (events.size() == 1) {
            return callEvent(events.get(0), data_source_name, callBack);
        } else {
            List rtn = new ArrayList<>();
            for (Map mapevent : events) {
                rtn.add(callEvent(mapevent, data_source_name, callBack));
            }
            return rtn;
        }
    }

    public Object callEvent(Map event_map, String data_source_name, CallBack callBack) throws Throwable {
        if (StringUtil.isEmptys(data_source_name))
            data_source_name = default_data_source_name;
        Event event = (Event) this.getIDMO(event_map.get(EventField.event_type) + "", data_source_name, logService);
        if (event != null) {
            return event.call(event_map, callBack);
        } else {
            throw new RuntimeException(this.toString() + " get event error");
        }
    }

    private static String default_data_source_name = "";

    public void updated(Dictionary<String, ?> props)  {
        default_data_source_name = (String) props.get("defaultConn");
    }
}
