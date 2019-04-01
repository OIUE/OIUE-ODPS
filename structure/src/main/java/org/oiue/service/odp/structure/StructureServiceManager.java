package org.oiue.service.odp.structure;

import java.io.Serializable;

public interface StructureServiceManager extends StructureService, Serializable {
	boolean registerStructure(String DBType, String type, StructureService structure);
	
	boolean registerStructureConvert(String DBType, String type, StructureConvert structure);
	
	StructureService getStructureService(String type);
	
	StructureService getStructureService(String DBType, String type);
	
	StructureConvert getStructureConvert(String DBType, String type);
}
