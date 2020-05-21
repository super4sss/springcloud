//package com.ysd.springcloud.commen;
//
//import com.jfinal.aop.Interceptor;
//import com.jfinal.aop.Invocation;
//import com.jfinal.core.Controller;
//import com.jfinal.kit.PropKit;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
///**
// * 跨域拦截器
// */
//public class CrossDomainInterceptor implements Interceptor {
//
//	@Override
//	public void intercept(Invocation inv) {
//		Controller c = inv.getController();
//		HttpServletRequest request = c.getRequest();
//		HttpServletResponse response = c.getResponse();
//
//		response.addHeader("Access-Control-Allow-Origin", "*");
//		response.addHeader("Access-Control-Allow-Methods", "POST,GET,OPTIONS,DELETE");
//		response.addHeader("Access-Control-Allow-Headers", getAllowHeaders());
//
//		if ("OPTIONS".equals(request.getMethod())) {
//			response.setStatus(HttpServletResponse.SC_ACCEPTED);
//			c.renderNull();
//            return;
//        }
//
//		inv.invoke();
//	}
//
//	private String getAllowHeaders() {
//		return PropKit.get("auth.token");
//	}
//
//}
