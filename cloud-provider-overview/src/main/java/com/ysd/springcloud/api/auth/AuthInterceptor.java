package com.ysd.springcloud.api.auth;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.ysd.springcloud.common.auth.UserPrincipal;
import com.ysd.springcloud.common.controller.BaseController;
import com.ysd.springcloud.common.kit.ObjKit;
import com.ysd.springcloud.common.model.App;
import com.ysd.springcloud.common.service.AppCacheService;

import javax.servlet.http.HttpServletRequest;

/**
 * 授权拦截器
 */
public class AuthInterceptor implements Interceptor {

  private App appDao = new App().dao();

  @Override
	public void intercept(Invocation inv) {
		Controller c = inv.getController();
		HttpServletRequest request = c.getRequest();
		App app;
		String token = getRequestToken(request);
		if (StrKit.isBlank(token)) {
			c.renderJson(Ret.fail("unauthorized", "AuthToken不能为空"));
			return;
		}
    System.out.println(token);
    System.out.println("a"+request.getHeader("appid"));
    if (StrKit.notBlank(request.getHeader("appid"))){
      String appid =request.getHeader("appid");
//      app = appDao.findById(appid);
      token = Db.findFirst("select * from sys_app_token where appid=? ORDER BY loginat DESC",appid).get("authToken");
      System.out.println(token);
    }

		UserPrincipal user = AppCacheService.me.getLoginUser(token);
    System.out.println(request.getHeader("appid"));
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
