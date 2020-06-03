package com.ysd.overview.admin.login;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import com.ysd.overview.common.auth.UserPrincipal;
import com.ysd.overview.common.kit.ReqKit;

/**
 * 后台登录控制器
 */
@Clear
public class LoginAdminController extends Controller {

	LoginAdminService srv = LoginAdminService.me;
	
	/**
	 * 显示登录界面
	 */
	public void index() {
		keepPara("returnUrl");  // 保持住 returnUrl 这个参数，以便在登录成功后跳转到该参数指向的页面
		render("index.html");
	}
	
	/**
	 * 登录
	 */
	@Before(LoginAdminValidator.class)
	public void doLogin() {
		String loginIp = ReqKit.getIpAddress(getRequest());
		Ret ret = srv.login(getPara("userName"), getPara("password"), loginIp);
		if (ret.isOk()) {
			UserPrincipal user = (UserPrincipal) ret.remove("user");
			setSessionAttr(LoginAdminService.SESSION_ADMIN, user);

			ret.set("returnUrl", getPara("returnUrl", "/admin"));    // 如果 returnUrl 存在则跳过去，否则跳去首页
		}
		renderJson(ret);
	}
	
	/**
	 * 退出登录
	 */
	@ActionKey("/admin/logout")
	public void logout() {
		removeSessionAttr(LoginAdminService.SESSION_ADMIN);
		redirect("/admin");
	}
	
	public void captcha() {
		renderCaptcha();
	}
	
}
