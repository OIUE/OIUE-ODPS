/**
 * 
 */
package org.oiue.service.odp.event.api;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.oiue.service.odp.dmo.IDMO;

/**
 * @author Every
 *
 */
public interface EventConvertService extends IDMO, Serializable {

    List<Map<?, ?>> convert(Map<?, ?> event, Map<String, Object> data) throws Throwable;
    Map<?, ?> query(Map<?, ?> event_query) throws Throwable;
}
