package com.ysd.springcloud.config;

/**
 * @author daixin
 * @create 2020/4/21 15:57
 */

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.ysd.springcloud.kit.NetKit;
import com.ysd.springcloud.model._MappingKit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import java.sql.Connection;

//import com.ysd.springcloud.model.Payment;

@Configuration
public class ActiveRecordPluginConfig {


  // 以下三个信息在 src/mian/resources/application.properties配置的数据库连接信息
//  @Value("${spring.datasource.username}")
////  @Value("root")
//  private String username;
//  @Value("${spring.datasource.password}")
////  @Value("root")
//  private String password;
//  @Value("${spring.datasource.url}")
////  @Value("jdbc:mysql://localhost:3306/db2019?serverTimezone=UTC&useSSL=false&autoReconnect=true&tinyInt1isBit=false&useUnicode=true&characterEncoding=utf8")
//  private String url;

  private String url="jdbc:mysql://localhost:9253/ysd_overview?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&serverTimezone=GMT&autoReconnect=true";

  private String username="root";

  private String password="prod1&2019$";

  private String driver="com.mysql.jdbc.Driver";

  private ActiveRecordPlugin arp;

  private DruidPlugin druidPlugin;
  NetKit kit =new NetKit();




  @Bean
  public ActiveRecordPlugin initActiveRecordPlugin() {
//    if(ObjKit.notEmpty(arp)){
//      return arp;
//    }

    // 加强数据库安全
//    WallFilter wallFilter = new WallFilter();
//
//    wallFilter.setDbType("mysql");
//
//    druidPlugin.addFilter(wallFilter);
    // 添加 StatFilter 才会有统计数据
//    druidPlugin.addFilter(new StatFilter());
    DruidPlugin druidPlugin = initDruidPlugin();
    String name = String.valueOf(System.currentTimeMillis());
    arp = new ActiveRecordPlugin(name,druidPlugin);
//

    arp.setTransactionLevel(Connection.TRANSACTION_READ_COMMITTED);
    arp.setShowSql(true);
    arp.getEngine().setToClassPathSourceFactory();
    _MappingKit.mapping(arp);

    // sql文件路径在src/mian/resources/sql/all_sqls.sql
//    arp.addSqlTemplate("/sql/all_sqls.sql");

    //	arp.addMapping("blog", Blog.class);

    // 与 jfinal web 环境唯一的不同是要手动调用一次相关插件的start()方法

    druidPlugin.start();
    arp.start();



    return arp;
  }

  @Bean
  public DruidPlugin initDruidPlugin(){
    //ssh连接
    try {
      NetKit.SSh();
    } catch (Exception e) {
      e.printStackTrace();
    }

//    DruidPlugin druidPlugin = new DruidPlugin(url, username, password);
//    druidPlugin = new DruidPlugin("jdbc:mysql://localhost:9253/ysd_overview?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&serverTimezone=UTC", "root", "prod1&2019$");
    druidPlugin = new DruidPlugin(url,username,password,driver);
//    druidPlugin = new DruidPlugin("jdbc:mysql://localhost:9253/ysd_overview?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull", "bim", "bim@prod1&2019$");
    //防止数据库服务器断开连接
druidPlugin.setTestOnBorrow(true);
druidPlugin.setRemoveAbandoned(true);
druidPlugin.setRemoveAbandonedTimeoutMillis(180);
//druidPlugin.setRemoveAbandoned(true);
//druidPlugin.setRemoveAbandonedTimeoutMillis(1000);
    druidPlugin.setInitialSize(3);
    druidPlugin.setMinIdle(3);
    druidPlugin.setMaxActive(5) ;
    druidPlugin.setMinEvictableIdleTimeMillis(30000);
    return druidPlugin;
  }


  @PreDestroy
  public void preDestroy() {
    System.out.println("@PreDestroy 执行");
    kit.close();
    druidPlugin.stop();
    arp.stop();
    System.out.println("关闭连接");
  }


}
