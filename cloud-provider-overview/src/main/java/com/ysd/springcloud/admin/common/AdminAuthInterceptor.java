package com.ysd.springcloud.admin.common;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.ysd.springcloud.admin.login.LoginAdminService;
import com.ysd.springcloud.common.auth.UserPrincipal;
import com.ysd.springcloud.common.kit.ObjKit;

/**
 * 后台权限管理拦截器
 */
public class AdminAuthInterceptor implements Interceptor {

	@Override
	public void intercept(Invocation inv) {
		Controller c = inv.getController();
		UserPrincipal user = c.getSessionAttr(LoginAdminService.SESSION_ADMIN);
		if (ObjKit.empty(user)) {
			String queryString = c.getRequest().getQueryString();
			if (StrKit.isBlank(queryString)) {
				c.redirect("/admin/login?returnUrl=" + inv.getActionKey());
			} else {
				c.redirect("/admin/login?returnUrl=" + inv.getActionKey() + "?" + queryString);
			}
			return;
		}
		
		c.setAttr("loginUser", user);
		inv.invoke();
	}

}
