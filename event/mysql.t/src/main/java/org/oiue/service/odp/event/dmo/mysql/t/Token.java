package org.oiue.service.odp.event.dmo.mysql.t;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.oiue.service.odp.event.dmo.mysql.t.exception.ParsingException;

/**
 *
 * @author zhangcaijie
 *
 */
public class Token {

	private Map<String, ?> map;
	private StringBuilder sb = new StringBuilder();
	private StringBuilder cache = new StringBuilder();
	private List<StringBuilder> list = new ArrayList<>();
	private String countsb;
	private List<String> conditionList = new ArrayList<>();

	public void putChar(char c) throws ParsingException {
		cache.append(c);
	}

	public void putString(String str) throws ParsingException {
		cache.append(str);
	}

	public void putCache() throws ParsingException {
		putList();
		if(list.size() > 0) {
			for(StringBuilder builder : list) {
				if(length() > 0 && charAt(length() - 1) == builder.charAt(0) && builder.charAt(0) == Characters.SPACE) {
					builder.deleteCharAt(0);
				}
				sb.append(builder);
			}
			list.clear();
		}
	}

	public void putList() throws ParsingException {
		if(cache.length() > 0) {
			list.add(cache);
			cache = new StringBuilder();
		}
	}

	public List<StringBuilder> getCacheList() {
		return list;
	}

	public void addConditionParameter(String parameter) {
		conditionList.add(parameter);
	}

	public List<String> getConditionList() {
		return conditionList;
	}

	public int length() {
		return sb.length();
	}

	public void setMap(Map<String, ?> map) {
		this.map = map;
	}

	public Map<String, ?> getMap() {
		return map;
	}

	public char charAt(int index) {
		return sb.charAt(index);
	}

	public void setCountSql(int positon) {
		countsb = "select count(1) count from" + sb.substring(positon, sb.length());
	}

	public String getCountSql() {
		return countsb;
	}

	public String getSql() {
		return sb.toString();
	}

	public void clear() {
		if(cache.length() > 0) {
			cache = new StringBuilder();
		}
	}

	public StringBuilder getCache() {
		return cache;
	}

	public void deleteLastWord() {
		char pre = 0;
		for(int i = length() - 1; i >= 0; i--) {
			if(charAt(i) != Characters.SPACE) {
				pre = charAt(i);
				sb.deleteCharAt(i);
			} else if(pre == 0){
				sb.deleteCharAt(i);
			} else {
				break;
			}
		}
	}

	public void paging(int start, int limit) {
		sb = new StringBuilder().append(sb).append(" limit ").append(start).append(",").append(limit);
	}
}
