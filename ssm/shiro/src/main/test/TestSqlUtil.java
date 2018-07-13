import com.shiro.pojo.TableField;
import com.shiro.util.SQLUtil;
import com.shiro.util.TableUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author:pms
 * @createtime:2018/7/12-14:07
 * @qq 718195578
 * @since 1.0
 */
public class TestSqlUtil {
    public static void main(String[] args){

      /*  Map<String, Object> map = new HashMap<>();
        map.put("id","123");
        map.put("name","王宇");
        map.put("age","12");
        map.put("email",null);
        map.put("createdate","2018-09-12");
        String sql = SQLUtil.getInsertSql("ac_mgmt",map,"createdate");
        System.out.println(sql);
        sql = SQLUtil.getUpdateSql("ac_mgmt",map,"id = '1'","createdate");
        System.out.println(sql);*/
        Map<String,Object> map = new HashMap<>();
        map.put("id","1");
        map.put("account","admin");
        map.put("isvalid",1);
        map  = TableUtil.getTableValue(map,"shiro","sys_user");

    }
}
