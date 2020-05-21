import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.druid.DruidPlugin;
import com.ysd.springcloud.kit.NetKit;
import com.ysd.springcloud.model.ProVrshow;
import com.ysd.springcloud.model._MappingKit;

import java.util.List;

/**
 * @author daixin
 * @create 2020/4/21 18:02
 */
public class test {
  public static void main(String[] args) {
//
//    Vector v = new Vector();
    try {
      new NetKit().SSh();
    } catch (Exception e) {
      e.printStackTrace();
    }
    DruidPlugin dp = new DruidPlugin("jdbc:mysql://localhost:9253/ysd_overview?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&serverTimezone=UTC", "bim", "bim@prod1&2019$");
    ActiveRecordPlugin arp = new ActiveRecordPlugin(dp);
    _MappingKit.mapping(arp);


    // 与 jfinal web 环境唯一的不同是要手动调用一次相关插件的start()方法
    dp.start();
    arp.start();

    ProVrshow Dao = new ProVrshow().dao();
    List<ProVrshow> list = Dao.find("select * from pro_vrshow");
//    System.out.println(list.get(1).getCreatorName());
    System.out.println(Db.find("select * from sys_app" ));
//    System.out.println(Db.find("select * from pro_vrshow"));
  }
}
