//package com.ysd.springcloud.test;
//
//import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
//import com.jfinal.plugin.druid.DruidPlugin;
//import com.ysd.springcloud.kit.NetKit;
//import com.ysd.springcloud.model._MappingKit;
//
//import java.util.List;
//
///**
// * @author daixin
// * @create 2020/4/21 18:02
// */
//public class test {
//  public static void main(String[] args) {
////
//    try {
//      NetKit.SSh();
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//    DruidPlugin dp = new DruidPlugin("jdbc:mysql://localhost:9253/ysd_overview?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&serverTimezone=UTC", "root", "prod1&2019$");
//    ActiveRecordPlugin arp = new ActiveRecordPlugin(dp);
//    _MappingKit.mapping(arp);
//
//
//    // 与 jfinal web 环境唯一的不同是要手动调用一次相关插件的start()方法
//    dp.start();
//    arp.start();
//
//    List list = new TestService().getVrShows("392", null);
//    System.out.println(new TestService().getVrShows("392", null).get(0).getColumns());
//    NetKit.close();
////
////    Payment fileDao = new Payment().dao();
////    List<Payment> list = fileDao.find("select * from payment");
////    System.out.println(list.size());
//  }
//}
