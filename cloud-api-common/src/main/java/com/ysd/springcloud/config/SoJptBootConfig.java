//package com.ysd.springcloud.config;
//
///**
// * @author daixin
// * @create 2020/5/21 11:35
// */
//
//import com.alibaba.druid.filter.stat.StatFilter;
//import com.alibaba.druid.wall.WallFilter;
//import com.jfinal.config.Constants;
//import com.jfinal.config.Handlers;
//import com.jfinal.config.Interceptors;
//import com.jfinal.config.Plugins;
//import com.jfinal.core.JFinal;
//import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
//import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
//import com.jfinal.plugin.druid.DruidPlugin;
//import com.jfinal.template.Engine;
//import com.sojpt.boot.ActiveRecordPluginProperties;
//import com.sojpt.boot.SoJptConfig;
//import com.ysd.springcloud.kit.NetKit;
//import com.ysd.springcloud.model._MappingKit;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//
//import java.sql.Connection;
//
//@Configuration(value = "soJptBootConfig")
//public class SoJptBootConfig extends SoJptConfig {
//
//  @Autowired
//  private ActiveRecordPluginProperties arpProperties;
//
//  /**
//   * 配置JFinal常量
//   */
//  @Override
//  public void configConstant(Constants me) {
//
//    // 设置当前是否为开发模式
//    me.setDevMode(arpProperties.getIsDevMode());
//    // me.setError404View("/index.html");
//  }
//
//  /**
//   * 配置JFinal插件 数据库连接池 ORM 缓存等插件 自定义插件
//   */
//  @Override
//  public void configPlugin(Plugins me) {
//    try {
//      NetKit.SSh();
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//    System.out.println(arpProperties.getJdbcUrl());
////    DruidPlugin dp = new DruidPlugin(arpProperties.getJdbcUrl(), arpProperties.getUsername(), arpProperties.getPassword());
//    DruidPlugin dp = new DruidPlugin("jdbc:mysql://localhost:9253/ysd_overview?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&serverTimezone=UTC", "root", "prod1&2019$");
//  dp.setValidationQuery("select 1 from dual");
//    dp.addFilter(new StatFilter());
//    dp.setDriverClass("com.mysql.jdbc.Driver");
//    WallFilter wall = new WallFilter();
//    dp.addFilter(wall);
//    me.add(dp);
//
//    ActiveRecordPlugin arp = new ActiveRecordPlugin(dp);
//    arp.setTransactionLevel(Connection.TRANSACTION_READ_COMMITTED);
//    arp.setShowSql(arpProperties.getIsDevMode());
//    arp.setDialect(new MysqlDialect());
//
//    // ******** 在此添加dao层sql文件 *********//*
//    //arp.addSqlTemplate("sql/all_sqls.sql");
//
//    _MappingKit.mapping(arp);
//
//    // 初始化任务调度插件,参数为配置文件名
//    // me.add(new Cron4jPlugin(sysProp));
//
//    me.add(arp);
//    NetKit.close();
//  }
//
//  /**
//   * 配置全局拦截器
//   */
//  @Override
//  public void configInterceptor(Interceptors me) {
//    // me.addGlobalActionInterceptor(new DuplicateLoginInterceptor());
//  }
//
//  /**
//   * 配置全局处理器
//   */
//  @Override
//  public void configHandler(Handlers me) {
//    // druid 统计页面功能
//    //me.add(DruidKit.getDruidStatViewHandler());
//    //me.add(new JavaMelodyHandler("/monitoring.*", true));
//  }
//
//  @Override
//  public void beforeJFinalStop() {
//  }
//
//  /**
//   *
//   * 配置模板引擎
//   */
//  @Override
//  public void configEngine(Engine me) {
//    // 这里只有选择JFinal TPL的时候才用
//    me.addSharedObject("RESOURCE_HOST", JFinal.me().getContextPath());
//    me.addSharedObject("WEB_HOST", JFinal.me().getContextPath());
//    // 配置共享函数模板
//    // me.addSharedFunction("/view/common/layout.html")
//  }
//
//  @Override
//  public void afterJFinalStart() {
//    System.err.println("SoJpt Boot 启动成功!");
//  }
//
//}
