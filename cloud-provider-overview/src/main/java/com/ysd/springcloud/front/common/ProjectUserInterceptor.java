package com.ysd.springcloud.front.common;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;
import com.ysd.springcloud.common.auth.AuthHelper;
import com.ysd.springcloud.common.auth.UserPrincipal;
import com.ysd.springcloud.common.controller.BaseController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 业务工程用户拦截器
 */
public class ProjectUserInterceptor implements Interceptor {

	@Override
	public void intercept(Invocation inv) {
		if (!isDevMode()) {
			AuthHelper helper = AuthHelper.on(inv);
			if (helper.doCheck()) inv.invoke();
			return;
		}
		
		Controller c = inv.getController();
		HttpServletRequest request = c.getRequest();
		HttpServletResponse response = c.getResponse();
		
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Methods", "POST,GET,OPTIONS,DELETE");
		response.addHeader("Access-Control-Allow-Headers", getAllowHeaders());
		
		if ("OPTIONS".equals(request.getMethod())) {
			response.setStatus(HttpServletResponse.SC_ACCEPTED);
			c.renderNull();
            return;
        }
		
//		String token = "18588763687;678;郑希彬;289;IT专用测试项目;49348;y;y";
//		String token = "18588763687;678;郑希彬;10324;新IT专用测试项目;49348;y;y";   //10324
		String token = "18211262294;378;莫江慧;3;新测试项目;142882;y;y";  
//		String token = "18588763687;678;郑希彬;310;中国风投大厦;49348;y;y";
//		String token = "18588763687;678;郑希彬;378;珠海-横琴新区市民服务中心项目;49348;y;y";
		UserPrincipal user = UserPrincipal.on(token);
		/*if (!AuthHelper.on(inv).doCheck(user)) {
			return;
		}*/
		
		c.setAttr(BaseController.BUS_PROJECT_USER, user);
		inv.invoke();
	}
	
	private boolean isDevMode() {
		return PropKit.getBoolean("app.devMode", false);
	}
	
	private String getAllowHeaders() {
		return String.join(",", PropKit.get("app.token"), PropKit.get("auth.token"));
	}

}
