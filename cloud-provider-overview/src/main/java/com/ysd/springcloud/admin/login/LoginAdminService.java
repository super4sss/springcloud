package com.ysd.springcloud.admin.login;

import com.jfinal.kit.HashKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.Ret;
import com.ysd.springcloud.common.auth.UserPrincipal;
import com.ysd.springcloud.common.kit.ObjKit;

/**
 * 后台登录业务
 */
public class LoginAdminService {

	public static final LoginAdminService me = new LoginAdminService();
	public static final String DEFAULT_SALT = "4yv6jJeuXcC7lzI9ZF5Ky-C1BjdZvHOh";
	public static final String SESSION_ADMIN = "OVERVIEW_ADMIN";
	
	
	public Ret login(String userName, String password, String loginIp) {
		userName = userName.trim();
		password = password.trim();
		
		String adminUser = getAdminUser();
		String adminPswd = getAdminPswd();
		if (ObjKit.notEquals(adminUser, userName) 
				|| ObjKit.notEquals(adminPswd, HashKit.sha256(DEFAULT_SALT+password))) {
			return Ret.fail("msg", "用户名或密码不正确");
		}
		
		return Ret.ok("user", UserPrincipal.getBuilder()
				.withAccount("userName").withUsername("管理员").build());
	}
	
	private String getAdminUser() {
		return PropKit.get("system.adminUser");
    }
	
	private String getAdminPswd() {
		return PropKit.get("system.adminPswd");
    }
	
	public static void main(String[] args) {
		String pswd = "ysd_overview";
		System.out.println(HashKit.sha256(DEFAULT_SALT+pswd));
	}
	
}
