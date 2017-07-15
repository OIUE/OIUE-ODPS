package org.oiue.service.odp.dmo;

/**
 * 用户定义针对各数据库个性化接口
 *
 * @author Every
 *
 */
public interface IDMO extends IDMO_DB {
	void setIdmo(IDMO_DB idmo);
	Object deepCopy() throws Throwable;
}
