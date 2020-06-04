package com.ysd.springcloud.admin.app;

import java.util.List;
import java.util.Map;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.ysd.springcloud.common.model.App;
import com.ysd.springcloud.common.model.AppToken;

/**
 * 应用管理控制器
 */
public class AppAdminController extends Controller {
	
	AppAdminService srv = AppAdminService.me;

	public void index() {
		Page<App> appPage = srv.paginate(getParaToInt("p", 1));
		setAttr("appPage", appPage);
		render("index.html");
	}
	
	public void add() {
		render("add.html");
	}
	
	@Before(AppAdminValidator.class)
	public void save() {
		App app = getBean(App.class);
		Ret ret = srv.save(app);
		renderJson(ret);
	}
	
	public void edit() {
		App app = srv.findById(getParaToLong("id"));
		setAttr("app", app);
		render("edit.html");
	}
	
	@Before(AppAdminValidator.class)
	public void update() {
		App app = getBean(App.class);
		Ret ret = srv.update(app);
		renderJson(ret);
	}
	
	/**
	 * 应用禁用
	 */
	public void disable() {
		Ret ret = srv.disable(getParaToLong("id"));
		renderJson(ret);
	}

	/**
	 * 应用启用
	 */
	public void enable() {
		Ret ret = srv.enable(getParaToLong("id"));
		renderJson(ret);
	}
	
	/**
	 * 获取应用配置
	 */
	public void getConfig() {
		String channel = getPara("channel");
		Map<String, String> map = srv.findConfigByChannel(channel);
		renderJson(map);
	}
	
	/**
	 * 添加应用配置
	 */
	public void addConfig() {
		String channel = getPara("channel");
		Ret ret = srv.addConfig(channel, getPara("name"), getPara("text"));
		renderJson(ret);
	}
	
	/**
	 * 删除应用配置
	 */
	public void delConfig() {
		Ret ret = srv.delConfig(getPara("channel"), getPara("name"));
		renderJson(ret);
	}
	
	/**
	 * 获取访问授权
	 */
	public void getAuthToken() {
		List<AppToken> list = srv.findTokenByApp(getParaToLong("id"));
		renderJson(list);
	}
	
	/**
	 * 删除授权码
	 */
	public void delAuthToken() {
		Ret ret = srv.delAuthToken(getPara("authToken"));
		renderJson(ret);
	}
	
}
