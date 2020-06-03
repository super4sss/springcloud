package com.ysd.overview.front.common;

import javax.servlet.http.HttpServletRequest;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import com.jfinal.log.Log;
import com.ysd.overview.common.kit.ReqKit;

/**
 * 异常拦截器
 */
public class ExceptionInterceptor implements Interceptor {
	
	private final static Log log = Log.getLog(ExceptionInterceptor.class);

	@Override
	public void intercept(Invocation inv) {
		try {
			inv.invoke();
		} catch (Exception e) {
			log.error("全局处理异常捕获：", e);
			processException(inv.getController(), e);
		}
	}
	
	private void processException(Controller c, Exception e) {
		HttpServletRequest request = c.getRequest();
		if (ReqKit.isAjax(request)) {
			c.renderJson(Ret.fail("msg", "操作处理失败，请联系管理员"));
		} else {
			c.setAttr("error", exceptionMsgForInner(e));
			c.render("/WEB-INF/views/error/500.html");
		}
	}
	
	private String exceptionMsgForInner(Throwable e) {
		String errorMessage = e.getLocalizedMessage();
		if (null == errorMessage){
		    errorMessage = "";
		}
		errorMessage += "\r\n";
		if (null != e.getCause()) {
		    errorMessage += exceptionMsgForInner(e.getCause());
		} else {
		    for (StackTraceElement stackTraceElement:e.getStackTrace()) {
		        errorMessage += stackTraceElement.toString() + "\r\n";
			}
		}
		return errorMessage.trim();
	}

}
