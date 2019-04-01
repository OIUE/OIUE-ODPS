package org.oiue.service.odp.event.dmo.mysql.t.handler;

import org.oiue.service.odp.event.dmo.mysql.t.Token;

/**
 * 
 * @author zhangcaijie
 *
 */
public class ContextHandler implements IHandler {
	
	private IHandler currentHandler;
	private RequiredFieldHandler requiredFieldHandler;
	private OptionalFieldHandler optionalFieldHandler;
	private InsertFieldHandler insertFieldHandler;
	private UpdateFieldHandler updateFieldHandler;
	private OrderFieldHandler orderFieldHandler;
	private TextHandler textHandler;
	private boolean isDeleConnector = false;
	
	public ContextHandler(Token token) {
		textHandler = new TextHandler(token, this);
		currentHandler = textHandler;
		requiredFieldHandler = new RequiredFieldHandler(token, this);
		optionalFieldHandler = new OptionalFieldHandler(token, this);
		insertFieldHandler = new InsertFieldHandler(token, this);
		updateFieldHandler = new UpdateFieldHandler(token, this);
		orderFieldHandler = new OrderFieldHandler(token, this);
	}
	
	@Override
	public void putCache() {
		currentHandler.putCache();
	}
	
	@Override
	public void putChar(char c) {
		currentHandler.putChar(c);
	}
	
	public void putString(String str) {
		for (char c : str.toCharArray()) {
			currentHandler.putChar(c);
		}
	}
	
	@Override
	public void end() {
		currentHandler.end();
	}
	
	@Override
	public void exit() {}
	
	public IHandler getCurrentHandler() {
		return currentHandler;
	}
	
	public void setCurrentHandler(IHandler currentHandler) {
		this.currentHandler = currentHandler;
	}
	
	public RequiredFieldHandler getRequiredFieldHandler() {
		return requiredFieldHandler;
	}
	
	public void setRequiredFieldHandler(RequiredFieldHandler requiredFieldHandler) {
		this.requiredFieldHandler = requiredFieldHandler;
	}
	
	public OptionalFieldHandler getOptionalFieldHandler() {
		return optionalFieldHandler;
	}
	
	public void setOptionalFieldHandler(OptionalFieldHandler optionalFieldHandler) {
		this.optionalFieldHandler = optionalFieldHandler;
	}
	
	public InsertFieldHandler getInsertFieldHandler() {
		return insertFieldHandler;
	}
	
	public void setInsertFieldHandler(InsertFieldHandler insertFieldHandler) {
		this.insertFieldHandler = insertFieldHandler;
	}
	
	public boolean isDeleConnector() {
		return isDeleConnector;
	}
	
	public void setDeleConnector(boolean isDeleConnector) {
		this.isDeleConnector = isDeleConnector;
	}
	
	public TextHandler getTextHandler() {
		return textHandler;
	}
	
	public void setTextHandler(TextHandler textHandler) {
		this.textHandler = textHandler;
	}
	
	public UpdateFieldHandler getUpdateFieldHandler() {
		return updateFieldHandler;
	}
	
	public void setUpdateFieldHandler(UpdateFieldHandler updateFieldHandler) {
		this.updateFieldHandler = updateFieldHandler;
	}
	
	public OrderFieldHandler getOrderFieldHandler() {
		return orderFieldHandler;
	}
	
	public void setOrderFieldHandler(OrderFieldHandler orderFieldHandler) {
		this.orderFieldHandler = orderFieldHandler;
	}
	
}
