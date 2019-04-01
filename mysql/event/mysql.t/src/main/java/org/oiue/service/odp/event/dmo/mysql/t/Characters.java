package org.oiue.service.odp.event.dmo.mysql.t;

/**
 * static class
 *
 * @author zhangcaijie
 *
 */
public class Characters {
	
	public static boolean convertField = false;
	
	public static final char SPACE = ' ';
	public static final char DOLLAR = '$';
	public static final char AT = '@';
	public static final char DOUBLE_QUOTE = '"';
	public static final char SINGLE_QUOTE = '\'';
	public static final char NEWLINE_RETURN = '\r';
	public static final char NEWLINE = '\n';
	public static final char QUESTION = '?';
	public static final char DOT = '.';
	public static final char SLASH = '/';
	public static final char BACKSLASH = '\\';
	public static final char TAB = '\t';
	public static final char LT = '<';
	public static final char GT = '>';
	public static final char EQUAL = '=';
	public static final char EXCLAMATION = '!';
	public static final char COMMA = ',';
	public static final char CLOSE_PARENTHESIS = ')';
	public static final char START_PARENTHESIS = '(';
	public static final char END = ';';
	public static final char PERCENT = '%';
	
	/**
	 * @param c
	 * @return true if a-z | A-Z | 0-9 | _ |.
	 */
	public static boolean isValidCharOfName(char c) {
		if (c == '_' || c == Characters.DOT) {
			return true;
		}
		return Character.isLetterOrDigit(c);
	}
	
	public static boolean isWhiteSpace(char c) {
		if (c == Characters.SPACE || c == Characters.NEWLINE || c == Characters.TAB || c == Characters.NEWLINE_RETURN) {
			return true;
		}
		return false;
	}
	
	public static boolean equalsIgnoreCase(char a, char b) {
		return (a == b || Character.toLowerCase(a) == Character.toLowerCase(b));
	}
	
	/**
	 * table column name to name of java field. ex) user_name -&gt; userName.
	 *
	 * A_a/A_A/a_a -&gt; aA
	 *
	 * a1 -&gt; a1, a_1 -&gt; a_1 : It keeps '_' because to support reverse conversion.
	 *
	 * a__a -&gt; a_A
	 *
	 * @param columnNameInSQL table column name
	 * @return name of java fields
	 */
	public static String convertColumnName(String columnNameInSQL) {
		if (convertField) {
			if (columnNameInSQL == null || columnNameInSQL.trim().isEmpty()) {
				return null;
			}
			columnNameInSQL = columnNameInSQL.trim();
			char[] chs = columnNameInSQL.toCharArray();
			char[] result = new char[chs.length];
			char pre = 0;
			int m = 0;
			int j = 0;
			
			for (int i = 0; i < chs.length; i++) {
				char ch = chs[i];
				if (ch == Characters.SPACE || ch == Characters.DOLLAR || ch == Characters.QUESTION) {
					continue;
				}
				if (pre == Characters.DOT) {
					if (Character.isUpperCase(ch))
						ch = (char) (ch + 32); // down
						
					pre = ch;
					m = j;
					continue;
				}
				if (pre == 0) {
					if (Character.isUpperCase(ch))
						ch = (char) (ch + 32); // down
						
					pre = ch;
					continue;
				}
				if (pre == '_') {
					if (Character.isLowerCase(ch)) {
						ch = (char) (ch - 32); // up
					} else if (Character.isUpperCase(ch)) {
						;
					} else
						result[j++] = pre;
				} else if (Character.isUpperCase(ch)) {
					ch = (char) (ch + 32); // down
					result[j++] = pre;
				} else {
					result[j++] = pre;
				}
				pre = ch;
			}
			result[j++] = pre; // last one
			return String.valueOf(result, m, j).trim();
		} else {
			return columnNameInSQL;
		}
	}
	
	/**
	 * javaName to sqlName, ex) userName -&gt; user_name
	 *
	 * @param javaName name of java variable
	 * @return name of table column
	 */
	public static String convertJavaName(String javaName) {
		char[] chs = javaName.toCharArray();
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < chs.length; i++) {
			char ch = chs[i];
			if (Character.isUpperCase(ch)) {
				sb.append('_');
				sb.append((char) (ch + 32));
			} else
				sb.append(ch);
		}
		
		return sb.toString();
	}
}
