package com.ysd.springcloud.admin.common;

import com.jfinal.config.Routes;
import com.ysd.springcloud.admin.app.AppAdminController;
import com.ysd.springcloud.admin.index.IndexAdminController;
import com.ysd.springcloud.admin.login.LoginAdminController;

/**
 * 后台管理路由
 */
public class AdminRoutes extends Routes {

	@Override
	public void config() {
		// 添加后台管理拦截器，将拦截在此方法中注册的所有 Controller
		addInterceptor(new AdminAuthInterceptor());
		addInterceptor(new PjaxInterceptor());
		
		setBaseViewPath("/WEB-INF/views/admin");
		
		add("/admin", IndexAdminController.class, "/");
		add("/admin/login", LoginAdminController.class, "/login");
		add("/admin/app", AppAdminController.class, "/app");
	}

}
