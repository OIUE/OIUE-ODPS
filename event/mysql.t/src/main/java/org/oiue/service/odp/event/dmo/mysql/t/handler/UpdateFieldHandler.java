package org.oiue.service.odp.event.dmo.mysql.t.handler;

import java.util.List;

import org.oiue.service.odp.event.dmo.mysql.t.Characters;
import org.oiue.service.odp.event.dmo.mysql.t.Token;
import org.oiue.service.odp.event.dmo.mysql.t.exception.ParsingException;

/**
 * 
 * @author zhangcaijie
 *
 */
public class UpdateFieldHandler extends AbstractHandler {

    private StringBuilder builder = new StringBuilder();
    private Token token;
    
    public UpdateFieldHandler(Token token, ContextHandler contextHandler) {
        super(token, contextHandler);
        this.token = token;
    }

    @Override
    public void putChar(char c) throws ParsingException {
        if (c != Characters.SPACE && c != Characters.COMMA) {
            builder.append(c);
        } else {
            getField();
            super.putChar(c);
            super.exit();
        }
    }

    @Override
    public void end() throws ParsingException {
        getField();
        super.exit();
    }
    
    private void getField() throws ParsingException {
        List<StringBuilder> list = token.getCacheList();
        if (builder.length() == 0) {
            int size = list.size();
            if(size < 2) {
                throw new ParsingException("parse error");
            }
            builder.append(list.get(size - 2));
        }
        String fieldTemp = Characters.convertColumnName(builder.toString());
        token.addConditionParameter(fieldTemp);
        builder.setLength(0);
    }
}
