package org.oiue.service.odp.event.dmo.mysql.t.handler;

import org.oiue.service.odp.event.dmo.mysql.t.Characters;
import org.oiue.service.odp.event.dmo.mysql.t.Token;
import org.oiue.service.odp.event.dmo.mysql.t.exception.ParsingException;

public class OrderFieldHandler extends AbstractHandler {

    private StringBuilder builder = new StringBuilder();
    private Token token;
    private boolean isStart = false;
    private boolean needDelete = false;
    
    public OrderFieldHandler(Token token, ContextHandler contextHandler) {
        super(token, contextHandler);
        this.token = token;
    }

    @Override
    public void putChar(char c) throws ParsingException {
        if (c == Characters.PERCENT) {
            isStart = true;
            return;
        }
        if (isStart) {
            if (Characters.isValidCharOfName(c)) {
                builder.append(c);
            } else {
                isStart = false;
                getField(false, c);
            }
            return;
        }
        //要么是逗号，要么是空格
        if (needDelete) {
            if (c == Characters.COMMA) {
                needDelete = false;
                deleteCache(false);
            }
            return;
        }
        super.putChar(c);
    }
    
    @Override
    public void end() throws ParsingException {
        getField(true, (char) 0);
    }

    /**
     * 
     * @param isEnd 为true时c = 0,否则为false
     * @param c c为' '或')'或','或0
     * @throws ParsingException
     */
    private void getField(boolean isEnd, char c) throws ParsingException {
        if (builder.length() == 0) {
            if (isEnd) {
                return;
            }
            throw new ParsingException("There should be a alias after '%' or format error");
        } else {
            String fieldTemp = Characters.convertColumnName(builder.toString());
            String value = (String) token.getMap().get(fieldTemp);
            builder.setLength(0);
            if (value == null) {
                if (isEnd || c == Characters.CLOSE_PARENTHESIS) {
                    boolean isField = deleteCache(true);
                    int size = token.getCacheList().size();
                    if (!isField && size > 0) {
                        if ("order by".equalsIgnoreCase(token.getCacheList().get(size - 1).toString().trim())) {
                            token.getCacheList().remove(size - 1);
                        }
                    }
                    super.putChar(c);
                    super.exit();
                } else if (c == Characters.COMMA) {
                    deleteCache(false);
                } else {
                    deleteCache(false);
                    needDelete = true;
                }
            } else if ("asc".equalsIgnoreCase(value) || "desc".equalsIgnoreCase(value)) {
                super.putChar(Characters.SPACE);
                token.putString(value);
            } else {
                throw new ParsingException("The value of order by field is error, the field: " + fieldTemp + ", the value: " + value);
            }
        }
    }
    
    /**
     * 删除token的cache最后一个以逗号分隔的单词，如果逗号之前还有字母，返回true，否则返回false
     * @param isDeleteComma
     * @return 逗号前是否还有字母
     */
    private boolean deleteCache(boolean isDeleteComma) {
        StringBuilder str = token.getCache();
        int size = str.length();
        boolean isField = false;
        if(size > 0) {
            boolean isWord = false;
            for(int i = size - 1; i >= 0; i--) {
                if (str.charAt(i) != Characters.COMMA && !isWord) {
                    str.deleteCharAt(i);
                } else if (str.charAt(i) == Characters.COMMA && !isWord){
                    if (isDeleteComma) {
                        str.deleteCharAt(i);
                    }
                    isWord = true;
                } else if (Characters.isValidCharOfName(str.charAt(i))) {
                    isField = true;
                    break;
                }
            }
        }
        return isField;
    }
    
}
