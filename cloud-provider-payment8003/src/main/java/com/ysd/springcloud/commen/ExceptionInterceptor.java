//package com.ysd.springcloud.commen;
//
//import com.jfinal.aop.Interceptor;
//import com.jfinal.aop.Invocation;
//import com.jfinal.core.Controller;
//import com.jfinal.kit.Ret;
//import com.jfinal.log.Log;
//
///**
// * 异常拦截器
// */
//public class ExceptionInterceptor implements Interceptor {
//
//	private final static Log log = Log.getLog(ExceptionInterceptor.class);
//
//	@Override
//	public void intercept(Invocation inv) {
//		try {
//			inv.invoke();
//		} catch (Exception e) {
//			log.error("操作处理异常：", e);
//			Controller controller = inv.getController();
//			controller.renderJson(Ret.fail("msg", "操作处理失败，请联系管理员"));
//		}
//	}
//
//}
