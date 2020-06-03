package com.ysd.overview.api.auth;

import java.util.Date;

import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.ysd.overview.common.kit.ObjKit;
import com.ysd.overview.common.model.App;
import com.ysd.overview.common.model.AppToken;
import com.ysd.overview.common.service.AppCacheService;

/**
 * 授权服务
 */
public class AuthService {

	public static final AuthService me = new AuthService();
	private AppCacheService srv = AppCacheService.me;
	
	public App findAppByUser(String userName) {
		String sql = "select * from sys_app where appKey=?";
		return srv.getAppDao().findFirst(sql, userName);
	}
	
	public App findAppByUser(String userName, String password) {
		String sql = "select * from sys_app where appKey=? and appSecret=?";
		return srv.getAppDao().findFirst(sql, userName, password);
	}
	
	public int countTokenByApp(Long appId) {
		String sql = "select count(*) from sys_app_token where appId=?";
		return Db.queryInt(sql, appId);
	}
	
	public AppToken findFirstToken(Long appId) {
		String sql = "select * from sys_app_token where appId=? order by loginAt limit 1";
		return srv.getTokenDao().findFirst(sql, appId);
	}
	
	public Ret login(String userName, String password, String loginIp) {
		userName = userName.trim();
		password = password.trim();
		
		App app = findAppByUser(userName, password);
		if (ObjKit.empty(app)) {
			return Ret.fail("msg", "用户名或密码不正确");
		}
		if (!app.isStatusOk()) {
			return Ret.fail("msg", "授权账号已被禁用");
		}
		
		/*String salt = app.getSalt();
		String hashedPass = HashKit.sha256(salt + password);
		if (app.getAppSecret().equals(hashedPass) == false) {
			return Ret.fail("msg", "用户名或密码不正确");
		}*/
		
		AppToken token = new AppToken()
				.set("appId", app.getId())
				.set("loginIp", loginIp)
				.set("loginAt", new Date())
				.set("authToken", StrKit.getRandomUUID());
		
		return Ret.ok("authToken", createToken(token));
	}
	
	private String createToken(AppToken token) {
		int count = countTokenByApp(token.getAppId());
		if (count < 30) {
			token.save();
		} else {
			AppToken bean = findFirstToken(token.getAppId());
			srv.clearLoginUser(bean.getAuthToken());
			String sql = "update sys_app_token set authToken=?,loginAt=?,loginIp=? where id=?";
			Db.update(sql, token.getAuthToken(), token.getLoginAt(), token.getLoginIp(), bean.getId());
		}
		return token.getAuthToken();
	}
	
	public Ret logout(String authToken) {
		if (StrKit.isBlank(authToken)) {
			return Ret.fail("msg", "AuthToken不能为空");
		}
		srv.clearLoginUser(authToken);
		Db.delete("delete from sys_app_token where authToken=?", authToken);
		return Ret.ok("msg", "注销成功");
	}
	
}
