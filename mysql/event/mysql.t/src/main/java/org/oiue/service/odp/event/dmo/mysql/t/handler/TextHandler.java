package org.oiue.service.odp.event.dmo.mysql.t.handler;

import org.oiue.service.odp.event.dmo.mysql.t.Characters;
import org.oiue.service.odp.event.dmo.mysql.t.Token;

/**
 * 
 * @author zhangcaijie
 *
 */
public class TextHandler extends AbstractHandler {
	
	private Token token;
	private char pre;
	private ContextHandler contextHandler;
	private boolean isStart = false;
	private StringBuilder builder = new StringBuilder();
	
	public TextHandler(Token token, ContextHandler contextHandler) {
		super(token, contextHandler);
		this.token = token;
		this.contextHandler = contextHandler;
	}
	
	@Override
	public void putChar(char c) {
		if (c == Characters.SPACE && !isStart) {
			super.putChar(c);
			token.putList();
			pre = c;
			return;
		}
		boolean isOperators = c == Characters.LT || c == Characters.GT || c == Characters.EQUAL || c == Characters.EXCLAMATION;
		// '<>' '!=' '<' '>' '<=' '>=' '='七个操作符前如果没有空格则添加空格，即：
		// '<' '>' '!' '='四个符号前如果不是字符或数字则添加空格
		if (isOperators && Characters.isValidCharOfName(pre)) {
			super.putChar(Characters.SPACE);
			token.putList();
		}
		// 删除可选参数中已经删除的连接符 'and'或'or'
		if (contextHandler.isDeleConnector()) {
			if (c != Characters.SPACE) {
				isStart = true;
				builder.append(c);
			} else {
				if (!builder.toString().trim().equalsIgnoreCase("and") && !builder.toString().trim().equalsIgnoreCase("or")) {
					// 需要删除可选参数前面的连接符 'and'或'or'
					token.deleteLastWord();
					token.putString(builder.toString());
					super.putChar(c);
					builder = new StringBuilder();
				}
				isStart = false;
				contextHandler.setDeleConnector(false);
			}
			pre = c;
			return;
		}
		super.putChar(c);
		pre = c;
	}
	
	@Override
	public void end() {
		if (builder.length() > 0 && !builder.toString().trim().equalsIgnoreCase("and") && !builder.toString().trim().equalsIgnoreCase("or")) {
			token.deleteLastWord();
			token.putString(builder.toString());
		}
	}
	
}
