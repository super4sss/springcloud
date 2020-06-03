package com.ysd.overview.api.auth;

import javax.servlet.http.HttpServletRequest;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.ysd.overview.common.auth.UserPrincipal;
import com.ysd.overview.common.controller.BaseController;
import com.ysd.overview.common.kit.ObjKit;
import com.ysd.overview.common.service.AppCacheService;

/**
 * 授权拦截器
 */
public class AuthInterceptor implements Interceptor {

	@Override
	public void intercept(Invocation inv) {
		Controller c = inv.getController();
		HttpServletRequest request = c.getRequest();
		
		String token = getRequestToken(request);
		if (StrKit.isBlank(token)) {
			c.renderJson(Ret.fail("unauthorized", "AuthToken不能为空"));
			return;
		}
		UserPrincipal user = AppCacheService.me.getLoginUser(token);
		if (ObjKit.empty(user)) {
			c.renderJson(Ret.fail("unauthorized", "AuthToken无效"));
			return;
		}
		
		c.setAttr(BaseController.BUS_PROJECT_USER, user);
		inv.invoke();
	}
	
	private String getRequestToken(HttpServletRequest request) {
		String name = getAuthToken();
		String token = request.getHeader(name);
		return StrKit.notBlank(token) ? token : request.getParameter(name);
	}
	
	public static String getAuthToken() {
		return PropKit.get("auth.token");
    }

}
