package org.oiue.service.odp.event.dmo.mysql.t.handler;

import java.util.List;

import org.oiue.service.odp.event.dmo.mysql.t.Characters;
import org.oiue.service.odp.event.dmo.mysql.t.Token;
import org.oiue.tools.StatusResult;
import org.oiue.tools.exception.OIUEException;

/**
 * 
 * @author zhangcaijie
 *
 */
public class RequiredFieldHandler extends AbstractHandler {
	
	private StringBuilder builder = new StringBuilder();
	private Token token;
	
	public RequiredFieldHandler(Token token, ContextHandler contextHandler) {
		super(token, contextHandler);
		this.token = token;
	}
	
	@Override
	public void putChar(char c) {
		if (c != Characters.SPACE && c != Characters.CLOSE_PARENTHESIS) {
			builder.append(c);
		} else {
			getField();
			super.putChar(c);
			super.exit();
		}
	}
	
	@Override
	public void end() {
		getField();
		super.exit();
	}
	
	private void getField() {
		List<StringBuilder> list = token.getCacheList();
		if (builder.length() == 0) {
			int size = list.size();
			if (size < 2) {
				throw new OIUEException(StatusResult._blocking_errors, "parse error");
			}
			builder.append(list.get(size - 2));
		}
		String fieldTemp = Characters.convertColumnName(builder.toString());
		token.addConditionParameter(fieldTemp);
		builder.setLength(0);
	}
	
}