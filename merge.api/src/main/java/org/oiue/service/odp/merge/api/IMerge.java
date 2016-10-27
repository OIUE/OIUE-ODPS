package org.oiue.service.odp.merge.api;

import java.util.Map;

import org.oiue.service.odp.bmo.IBMO;

@SuppressWarnings("rawtypes")
public interface IMerge extends IBMO {
    Map getData(String key, Map map);
}
