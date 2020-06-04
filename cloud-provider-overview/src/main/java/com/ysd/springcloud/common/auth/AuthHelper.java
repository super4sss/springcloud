package com.ysd.springcloud.common.auth;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.ysd.springcloud.common.kit.ObjKit;
import com.ysd.springcloud.common.kit.ReqKit;
import com.ysd.springcloud.front.index.UcenterService;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ysd.springcloud.common.controller.BaseController.BUS_PROJECT_USER;

public class AuthHelper {
	
	private static final String BUS_COOKIE_NAME = "bimsess";
	private static final String USER_COOKIE_NAME = "bimsessUser%s";
	private static final Pattern EVAL_PATTERN = Pattern.compile("\\{(.+?)\\}");
	private static final Set<String> NOT_PROJECT_KEYS = Collections.emptySet();
	
	private final Invocation inv;
	private final Controller c;
	
	private UserPrincipal user;
	private Menu menu;
	
	public AuthHelper(Invocation inv) {
		this.inv = inv;
		this.c = inv.getController();
	}
	
	public boolean doCheck() {
		user = getCookieUser();
		if (ObjKit.empty(user)) {
			render(AuthState.UNKNOWN);
			return false;
		}
		if(!activeToken()){
			/*打开项目*/
			if (!openProject()) {
				render(AuthState.UNOPENED);
				return false;
			}
		}
		if (!hasPermission()) {
			renderUnauthorized();
			return false;
		}
		c.setAttr(BUS_PROJECT_USER, user);
		return true;
	}
	
	public boolean doCheck(UserPrincipal user) {
		this.user = user;
		if (!hasPermission()) {
			renderUnauthorized();
			return false;
		}
		return true;
	}

	private UserPrincipal getCookieUser() {
		String cookie = c.getCookie(BUS_COOKIE_NAME);
		if (StrKit.isBlank(cookie)) {
			return null;
		}
		Ret ret = UcenterService.me.getLoginUser(cookie);
		JSONObject obj = ret.isOk() ? ret.getAs("data") : null;
		if (ObjKit.empty(obj)) {
			LogKit.warn("获取登录用户为空，" + ret.getStr("msg"));
			return null;
		}
		return UserPrincipal.on(obj.getJSONObject("member"));
	}
	
	private boolean openProject() {
		if (NOT_PROJECT_KEYS.contains(inv.getActionKey())) 
			return true;
		return user.openProject(uid -> {
			return c.getCookie(String.format(USER_COOKIE_NAME, uid));
		});
	}
	
	private boolean hasPermission() {
		menu = inv.getMethod().getAnnotation(Menu.class);
		if (ObjKit.empty(menu)) {
			return true;
		}
		Map<String, String> map = ObjKit.arrayToMap(menu.replace());
		String perKey = getPerKey(map);
		if (StrKit.isBlank(perKey)) {
			return false;
		}
		//System.out.println("======== " + perKey);
		return UcenterService.me.hasPermission(user, perKey);
	}
	
	private String getPerKey(Map<String, String> map) {
		StringBuffer buf = new StringBuffer();
		Matcher matcher = EVAL_PATTERN.matcher(menu.value());
		while (matcher.find()) {
			String name = matcher.group(1);
			String value = getRequestValue(name);
			if (StrKit.isBlank(value)) {
				LogKit.warn("[" + name + "] getRequestValue return null");
				return null;
			}
			if (map.containsKey(value)) value = map.get(value);
			matcher.appendReplacement(buf, value);
		}
		matcher.appendTail(buf);
		return buf.toString();
	}
	
	private String getRequestValue(String name) {
		String value = c.getPara(name);
		return StrKit.notBlank(value) ? value : getHeaderValue(name);
	}
	
	private String getHeaderValue(String name) {
		String value = c.getHeader(name);
		return StrKit.notBlank(value) ? value : getCookieValue(name);
	}
	
	private String getCookieValue(String name) {
		String value = c.getCookie(name);
		if (StrKit.notBlank(value)) {
			try {
				return URLDecoder.decode(value, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				LogKit.warn("[" + name + "] getCookieValue decode fail");
			}
		}
		return value;
	}
	
	private void renderUnauthorized() {
		if (menu.type() == MenuType.BUTTON) {
			AuthState state = AuthState.FAIL;
			c.renderJson(Ret.create()
					.set("state", state.getCode())
					.set("msg", state.getMsg()));
		} else {
			render(AuthState.UNAUTHORIZED);
		}
	}
	
	private void render(AuthState state) {
		if (ReqKit.isAjax(c.getRequest())) {
			c.renderJson(Ret.create()
					.set("state", state.getCode())
					.set("msg", state.getMsg()));
		} else {
			c.redirect(getRedirectUrl(state));
		}
	}
	
	private String getRedirectUrl(AuthState state) {
		String name = PropKit.get("app.token");
		String token = c.getHeader(name);
		String prefix = StrKit.notBlank(token) ? "app" : "web";
		return PropKit.get(prefix + "." + state.getCode());
	}

	public static AuthHelper on(Invocation inv) {
		return new AuthHelper(inv);
	}
	
	private  static boolean activeToken(){
		return PropKit.getBoolean("system.activetoken");
	}
	
}
