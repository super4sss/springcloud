package com.ysd.springcloud.test;

import com.jfinal.config.Routes;
import org.springframework.context.ApplicationContext;

/**
 * api路由
 */
public class ApiRoutes extends Routes {
  private ApplicationContext applicationContext ;
	@Override
	public void config() {
		// 添加api拦截器，将拦截在此方法中注册的所有 Controller

		add("/test", HelloController.class);
		add("/test1", TestController.class);


  }
}




