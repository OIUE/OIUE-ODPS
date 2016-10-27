package org.oiue.service.odp.event.api;

import java.io.Serializable;
import java.util.Map;

import org.oiue.service.odp.dmo.IDMO;
import org.oiue.service.odp.res.dmo.CallBack;

@SuppressWarnings("rawtypes")
public interface Event extends IDMO,Serializable {
    public Object call(Map map) throws Throwable;
    public Object call(Map map, CallBack callBack) throws Throwable;
}
