package org.oiue.service.odp.event.dmo.mysql.t.handler;

import org.oiue.service.odp.event.dmo.mysql.t.Token;
import org.oiue.service.odp.event.dmo.mysql.t.exception.ParsingException;

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
    public void putCache() throws ParsingException {
        token.putCache();
    }

    @Override
    public void putChar(char c) throws ParsingException {
        token.putChar(c);
    }

    @Override
    public void exit() throws ParsingException {
        putCache();
        contextHandler.setCurrentHandler(contextHandler.getTextHandler());
    }

}
