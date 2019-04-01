package org.oiue.service.odp.event.sql.structure;

import java.io.Serializable;

public interface StructureServiceManager extends StructureService, Serializable {
	boolean registerStructure(String DBType, String type, StructureService structure);
	
	StructureService getStructureService(String type);
	
	StructureService getStructureService(String DBType, String type);
}
