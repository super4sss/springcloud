package com.ysd.springcloud;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.druid.DruidPlugin;
import com.ysd.springcloud.kit.NetKit;
import com.ysd.springcloud.model._MappingKit;

/**
 * @author daixin
 * @create 2020/6/9 20:02
 */
public class test {
  public static void main(String[] args) {
    NetKit NetKit = new NetKit();
//    File file = new File("cloud-provider-overview\\src\\main\\resources\\config.properties");
//    PropKit.use(file);
//    PropKit.use("config.properties");
//    System.out.println(PropKit.get("type")+PropKit.getBoolean("app.devMode"));
//    System.out.println(System.getProperty("user.dir"));
    try {
      NetKit.SSh();
    } catch (Exception e) {
      e.printStackTrace();
    }
    DruidPlugin dp = new DruidPlugin("jdbc:mysql://localhost:9253/ysd_overview?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&serverTimezone=UTC", "root", "prod1&2019$");
    ActiveRecordPlugin arp = new ActiveRecordPlugin(dp);
    _MappingKit.mapping(arp);


    // 与 jfinal web 环境唯一的不同是要手动调用一次相关插件的start()方法
    dp.start();
    arp.start();
    String appid = "7";
Db.find("select * from sys_app_token where appid=? ORDER BY loginat DESC",appid).get(0).get("authToken").toString();
Db.find("select * from sys_app_token where appid=? ORDER BY loginat DESC",appid).get(0).get("authToken").toString();
Db.find("select * from sys_app_token where appid=? ORDER BY loginat DESC",appid).get(0).get("authToken").toString();
Db.find("select * from sys_app_token where appid=? ORDER BY loginat DESC",appid).get(0).get("authToken").toString();
Db.find("select * from sys_app_token where appid=? ORDER BY loginat DESC",appid).get(0).get("authToken").toString();
Db.find("select * from sys_app_token where appid=? ORDER BY loginat DESC",appid).get(0).get("authToken").toString();
Db.find("select * from sys_app_token where appid=? ORDER BY loginat DESC",appid).get(0).get("authToken").toString();
Db.find("select * from sys_app_token where appid=? ORDER BY loginat DESC",appid).get(0).get("authToken").toString();

  }
}
