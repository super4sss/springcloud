//package com.ysd.springcloud.config;
//
///**
// * @author daixin
// * @create 2020/4/21 15:57
// */
//import com.alibaba.druid.wall.WallFilter;
//import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
//import com.jfinal.plugin.druid.DruidPlugin;
//import com.ysd.springcloud.kit.NetKit;
//import com.ysd.springcloud.model._MappingKit;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
////import com.ysd.springcloud.model.Payment;
//
//@Configuration
//public class ActiveRecordPluginConfig {
//
//  // 以下三个信息在 src/mian/resources/application.properties配置的数据库连接信息
//  @Value("${spring.datasource.username}")
////  @Value("root")
//  private String username;
//  @Value("${spring.datasource.password}")
////  @Value("root")
//  private String password;
//  @Value("${spring.datasource.url}")
////  @Value("jdbc:mysql://localhost:3306/db2019?serverTimezone=UTC&useSSL=false&autoReconnect=true&tinyInt1isBit=false&useUnicode=true&characterEncoding=utf8")
//  private String url;
//
//
//
//
//  @Bean
//  public ActiveRecordPlugin initActiveRecordPlugin() {
//    //ssh连接
//    try {
//      new NetKit().SSh();
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//
//    DruidPlugin druidPlugin = new DruidPlugin(url, username, password);
//
//    // 加强数据库安全
//    WallFilter wallFilter = new WallFilter();
//
//    wallFilter.setDbType("mysql");
//
//    druidPlugin.addFilter(wallFilter);
//    // 添加 StatFilter 才会有统计数据
////    druidPlugin.addFilter(new StatFilter());
//
//    String name = String.valueOf(System.currentTimeMillis());
//    ActiveRecordPlugin arp = new ActiveRecordPlugin(name,druidPlugin);
////
//
//
//    arp.getEngine().setToClassPathSourceFactory();
//    _MappingKit.mapping(arp);
//
//    // sql文件路径在src/mian/resources/sql/all_sqls.sql
////    arp.addSqlTemplate("/sql/all_sqls.sql");
//
//    //	arp.addMapping("blog", Blog.class);
//
//    // 与 jfinal web 环境唯一的不同是要手动调用一次相关插件的start()方法
//    druidPlugin.start();
//    arp.start();
//
//
//
//    return arp;
//  }
//
//}
