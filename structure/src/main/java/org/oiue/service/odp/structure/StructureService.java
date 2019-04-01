package org.oiue.service.odp.structure;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface StructureService extends Serializable {
	List<Map<?, ?>> parse(Map<?, ?> event, Map<String, Object> parameter);
}
