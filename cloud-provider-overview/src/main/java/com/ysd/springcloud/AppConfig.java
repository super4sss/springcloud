package com.ysd.springcloud;

import com.jfinal.config.*;
import com.jfinal.core.JFinal;
import com.jfinal.ext.handler.ContextPathHandler;
import com.jfinal.json.MixedJsonFactory;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.Engine;
import com.ysd.springcloud.admin.common.AdminRoutes;
import com.ysd.springcloud.api.common.ApiRoutes;
import com.ysd.springcloud.common.handler.UrlSeoHandler;
import com.ysd.springcloud.common.model._MappingKit;
import com.ysd.springcloud.front.common.FrontRoutes;
import net.dreamlu.event.EventPlugin;

import java.sql.Connection;

/**
 * API引导式配置
 */
public class AppConfig extends JFinalConfig {

	/**
	 * 启动入口，运行此 main 方法可以启动项目，此main方法可以放置在任意的Class类定义中，不一定要放于此
	 * 
	 * 使用本方法启动过第一次以后，会在开发工具的 debug、run configuration 中自动生成
	 * 一条启动配置项，可对该自动生成的配置再继续添加更多的配置项，例如 VM argument 可配置为：
	 * -XX:PermSize=64M -XX:MaxPermSize=256M
	 * 上述 VM 配置可以缓解热加载功能出现的异常
	 */
	public static void main(String[] args) {
		/**
		 * 特别注意：Eclipse 之下建议的启动方式
		 */
//		JFinal.start("src/main/webapp", 8080, "/ysdOverview", 5);
		
		/**
		 * 特别注意：IDEA 之下建议的启动方式
		 */
//		JFinal.start("ysdOverview/src/main/webapp", 9081, "/ysdOverview");
		JFinal.start("G:\\IdeaProjects\\ysdOverview\\src\\main\\webapp", 8080, "/");

	}

	@Override
	public void configConstant(Constants me) {
		//读取配置文件
		PropKit.use("config.properties");
		//设置当前是否为开发模式
		me.setDevMode(isDevMode());
		//设置JSON
		me.setJsonFactory(MixedJsonFactory.me());
		//设置UrlPara分隔符
		me.setUrlParaSeparator("_");
		//设置json转换时日期格式
		me.setJsonDatePattern("yyyy-MM-dd");
	}

	@Override
	public void configRoute(Routes me) {
		me.add(new FrontRoutes());
	    me.add(new AdminRoutes());
	    me.add(new ApiRoutes());
	}

	@Override
	public void configEngine(Engine me) {
		me.setDevMode(isDevMode());
		
		me.addSharedFunction("/WEB-INF/views/common/_admin_layout.html");
		me.addSharedFunction("/WEB-INF/views/common/_admin_paginate.html");
	}

	@Override
	public void configPlugin(Plugins me) {
		DruidPlugin druidPlugin = getDruidPlugin();
		me.add(druidPlugin);
		
		ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
	    arp.setTransactionLevel(Connection.TRANSACTION_READ_COMMITTED);
	    _MappingKit.mapping(arp);
	    me.add(arp);
        arp.setShowSql(isDevMode());
        
    	EventPlugin plugin = new EventPlugin();
    	plugin.async();
    	plugin.scanPackage("com.ysd.springcloud");
    	me.add(plugin);
	}

	@Override
	public void configInterceptor(Interceptors me) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void configHandler(Handlers me) {
		me.add(new UrlSeoHandler());  // index、detail 两类 action 的 url seo
		me.add(new ContextPathHandler("ctx"));
	}
	
	private boolean isDevMode() {
		return PropKit.getBoolean("system.devMode", false);
	}
	
	public static DruidPlugin getDruidPlugin() {
		return new DruidPlugin(
				PropKit.get("jdbc.url"), 
				PropKit.get("jdbc.username"), 
				PropKit.get("jdbc.password"), 
				PropKit.get("jdbc.driver"));
	}

}
