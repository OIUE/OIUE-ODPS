package org.oiue.service.odp.structure;

import java.io.Serializable;
import java.util.Map;

import org.oiue.tools.StatusResult;

public interface StructureConvert extends Serializable {
	StatusResult convert(Map column);
}