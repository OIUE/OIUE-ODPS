package org.oiue.service.odp.event.dmo.mysql.t;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.oiue.service.odp.event.dmo.mysql.t.exception.ParsingException;
import org.oiue.service.odp.event.dmo.mysql.t.parser.Parser;

/**
 * 
 * @author zhangcaijie
 *
 */
public class ParserFactory {

    public static Token creatToken(String sql, Map<String, ?> map) throws ParsingException {
        Parser parser = new Parser(sql, map);
        parser.parse();
        return parser.getToken();
    }
    
    @SuppressWarnings("unused")
    public static void main(String[] args) throws ParsingException {
        String sql = "insert into lt_m_audio (audio_id, name, author, is_star) "
                    + "values (?b,IF(CONCAT(?id ,'')='',null,?a)";
        String sql1 = "select c.id from test_user c where c.id = ? and c.name like $ order by id%ab ,  ab%b ,c%c";
        String sql2 = "update lt_m_sort_album_mapping a set a.album_id = @b, a.name=@ where a.email=? and a.sort_id != $";
        Map<String, String> map = new HashMap<>();
        //map.put("name", "1");
        map.put("email", "woshiemail");
        map.put("id", "woshiid");
        map.put("b", "desc");
        Token token = ParserFactory.creatToken(sql1, map);
        System.out.println("count sql : " + token.getCountSql());
        System.out.println("sql : " + token.getSql());
        List<String> list = token.getConditionList();
        for(String str : list) {
            System.out.println("参数：" + str);
        }
    }

}
