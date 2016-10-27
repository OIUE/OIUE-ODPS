package org.oiue.service.odp.event.dmo.mysql.t.handler;

import org.oiue.service.odp.event.dmo.mysql.t.exception.ParsingException;

/**
 * @author zhangcaijie
 *
 */
public interface IHandler {
    
    public void putCache() throws ParsingException;
    
    public void putChar(char c) throws ParsingException;
    
    public void exit() throws ParsingException;
    
    public void end() throws ParsingException;
}
