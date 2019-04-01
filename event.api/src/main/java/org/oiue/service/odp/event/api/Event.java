package org.oiue.service.odp.event.api;

import java.io.Serializable;
import java.util.Map;

import org.oiue.service.odp.dmo.CallBack;
import org.oiue.service.odp.dmo.IDMO;

@SuppressWarnings("rawtypes")
public interface Event extends IDMO, Serializable {
	/**
	 * 事件调用
	 * @param map 事件定义
	 * @param data 事件执行数据
	 * @param callBack 回调对象
	 * @return 事件执行返回对象
	 * @throws Throwable 执行出现的异常
	 */
	public <T> T call(Map map, Map data, CallBack callBack);
}
