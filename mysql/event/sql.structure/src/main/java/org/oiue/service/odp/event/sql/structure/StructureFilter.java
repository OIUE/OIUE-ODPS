package org.oiue.service.odp.event.sql.structure;

import java.io.Serializable;
import java.util.Map;

import org.oiue.tools.StatusResult;

public interface StructureFilter extends Serializable {
	StatusResult convertFilter(Map column, Object value);
}
