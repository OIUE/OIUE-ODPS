package org.oiue.service.odp.proxy;

import java.io.Serializable;

public interface DataSourceAdmin extends Serializable {
	public Object getDataSourceByType(String data_type_class, String name);
	
	public Object getDataSourceByConn(String connName, String name);
}