package org.oiue.service.odp.event.dmo.mysql.t.handler;

import org.oiue.service.odp.event.dmo.mysql.t.Token;

/**
 * 
 * @author zhangcaijie
 *
 */
public abstract class AbstractHandler implements IHandler {
	
	private Token token;
	private ContextHandler contextHandler;
	
	public AbstractHandler(Token token, ContextHandler contextHandler) {
		this.token = token;
		this.contextHandler = contextHandler;
	}
	
	public Token getToken() {
		return this.token;
	}
	
	@Override
	public void putCache() {
		token.putCache();
	}
	
	@Override
	public void putChar(char c) {
		token.putChar(c);
	}
	
	@Override
	public void exit() {
		putCache();
		contextHandler.setCurrentHandler(contextHandler.getTextHandler());
	}
	
}
