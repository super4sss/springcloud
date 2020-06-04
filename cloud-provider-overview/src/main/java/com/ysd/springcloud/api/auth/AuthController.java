package com.ysd.springcloud.api.auth;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.ysd.springcloud.common.controller.BaseController;
import com.ysd.springcloud.common.kit.ReqKit;

/**
 * 授权控制器
 */
@Clear(AuthInterceptor.class)
public class AuthController extends BaseController {

	AuthService srv = AuthService.me;
	
	/**
     * @api {post} /auth/login 1.登录
     * @apiGroup auth
     * @apiVersion 1.0.0
     *
     * @apiParam {String} userName <code>必须参数</code> 账号
     * @apiParam {String} password <code>必须参数</code> 密码
     *
     * @apiSuccess {String} authToken 授权码
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "authToken":"ba0e0047db774e8a92592df97c585c8b"
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	@Before(LoginValidator.class)
	public void login() {
    	String userName = getPara("userName");
    	String password = getPara("password");
    	String loginIp = ReqKit.getIpAddress(getRequest());
		Ret ret = srv.login(userName, password, loginIp);
		renderJson(ret);
    }
	
	/**
     * @api {get} /auth/logout 2.注销
     * @apiGroup auth
     * @apiVersion 1.0.0
     * @apiHeader {String} Auth-Token-Overview 授权码
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "msg":"注销成功"
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	public void logout() {
		Ret ret = srv.logout(getAuthToken());
		renderJson(ret);
    }
	
	private String getAuthToken() {
		String name = PropKit.get("auth.token");
		String token = getHeader(name);
		return StrKit.notBlank(token) ? token : getPara(name);
	}
	
}
