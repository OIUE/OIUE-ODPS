package org.oiue.service.odp.res.dmo;

import java.io.Serializable;
import java.util.Map;

@SuppressWarnings("rawtypes")
public abstract class CallBack implements Serializable {

	private static final long serialVersionUID = 5397529637873685948L;
	
    public abstract boolean callBack(Map paramMap);
}
