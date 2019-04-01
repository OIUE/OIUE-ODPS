package org.oiue.service.odp.event.dmo.mysql.t.handler;

/**
 * @author zhangcaijie
 *
 */
public interface IHandler {
	
	public void putCache();
	
	public void putChar(char c);
	
	public void exit();
	
	public void end();
}
