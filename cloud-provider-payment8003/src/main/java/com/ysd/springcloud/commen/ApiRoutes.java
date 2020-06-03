//package com.ysd.springcloud.commen;
//
//import com.jfinal.config.Routes;
//import com.mysql.cj.exceptions.ExceptionInterceptor;
////import com.ysd.overview.api.auth.AuthController;
//
//
///**
// * api路由
// */
//public class ApiRoutes extends Routes {
//
//	@Override
//	public void config() {
//		// 添加api拦截器，将拦截在此方法中注册的所有 Controller
//		addInterceptor(new ExceptionInterceptor());
//		addInterceptor(new CrossDomainInterceptor());
////		addInterceptor(new AuthInterceptor());
//
////		add("/api/auth", AuthController.class);
//
//	}
//
//}
