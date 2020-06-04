package com.ysd.springcloud.admin.app;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.ysd.springcloud.common.model.App;
import com.ysd.springcloud.common.model.AppToken;
import com.ysd.springcloud.common.service.AppCacheService;
import com.ysd.springcloud.common.service.PropCacheService;

/**
 * 应用授权后端服务
 */
public class AppAdminService {

	public static final AppAdminService me = new AppAdminService();
	private AppCacheService srv = AppCacheService.me;
	
	public Page<App> paginate(int pageNum) {
		String sql = "from sys_app order by id desc";
		return srv.getAppDao().paginate(pageNum, 10, "select *", sql);
	}
	
	public App findById(Long appId) {
		return srv.getAppDao().findById(appId);
	}
	
	public List<AppToken> findTokenByApp(Long appId) {
		String sql = "select * from sys_app_token where appId=? order by loginAt desc";
		return srv.getTokenDao().find(sql, appId);
	}
	
	public Map<String, String> findConfigByChannel(String channel) {
		if (StrKit.isBlank(channel)) {
			return Collections.emptyMap();
		}
		return PropCacheService.me.getConfigMap(channel);
	}
	
	/**
	 * 判断 授权账号 是否存在
	 * @param appId 当前 app 对象的 id 号，如果 app 对象还未创建，提供一个小于 0 的值即可
	 * @param appKey 授权KEY
	 * ----------------------------
	 * 新建时传入 -1 ，匹配所有的id
	 * 更新时传入id的值，匹配除当前id外的所有数据
	 */
	public boolean exists(Long appId, String appKey) {
		appKey = appKey.trim();
		String sql = "select id from sys_app where appKey = ? and id != ? limit 1";
		return Db.queryLong(sql, appKey, appId) != null;
	}
	
	/**
	 * 创建应用
	 */
	public Ret save(App app) {
		if (exists(-1L, app.getAppKey())) {
			return Ret.fail("msg", "授权账号已经存在，请重新输入");
		}
		
		app.setChannel(app.getChannel().trim());
		app.setAppKey(app.getAppKey().trim());
		app.setAppSecret(app.getAppSecret().trim());
		app.setStatus(App.STATUS_OK);
		app.save();
		return Ret.ok("msg", "应用创建成功");
	}
	
	/**
	 * 更新应用
	 */
	public Ret update(App app) {
		if (exists(app.getId(), app.getAppKey())) {
			return Ret.fail("msg", "授权账号已经存在，请重新输入");
		}

		app.setChannel(app.getChannel().trim());
		app.setAppKey(app.getAppKey().trim());
		app.setAppSecret(app.getAppSecret().trim());
		app.update();
		return Ret.ok("msg", "应用更新成功");
	}
	
	/**
	 * 禁用工具
	 */
	public Ret disable(Long appId) {
		int n = Db.update("update sys_app set status=? where id=?", App.STATUS_NO, appId);
		if (n > 0) {
			srv.clearLoginUser(appId);
			Db.delete("delete from sys_app_token where appId=?", appId);
			return Ret.ok("msg", "禁用成功");
		} else {
			return Ret.fail("msg", "禁用失败");
		}
	}
	
	/**
	 * 启用应用
	 */
	public Ret enable(Long appId) {
		int n = Db.update("update sys_app set status=? where id=?", App.STATUS_OK, appId);
		if (n > 0) {
			return Ret.ok("msg", "启用成功");
		} else {
			return Ret.fail("msg", "启用失败");
		}
	}
	
	/**
	 * 添加应用配置
	 */
	public Ret addConfig(String channel, String name, String text) {
		if (StrKit.isBlank(name) 
				|| StrKit.isBlank(text) 
				|| StrKit.isBlank(channel)) {
			return Ret.fail("msg", "配置参数不能为空，请检查");
		}
		Record record = new Record()
				.set("project", channel)
				.set("name", name)
				.set("text", text)
				.set("propSet", PropCacheService.APP_CONFIG_SET);
		Db.save("pro_prop", record);
		PropCacheService.me.clearConfigMap(channel);
		return Ret.ok("msg", "添加配置成功");
	}
	
	/**
	 * 删除应用配置
	 */
	public Ret delConfig(String channel, String name) {
		String sql = "delete from pro_prop where project=? and propSet=? and name=?";
		Db.delete(sql, channel, PropCacheService.APP_CONFIG_SET, name);
		PropCacheService.me.clearConfigMap(channel);
		return Ret.ok("msg", "删除配置成功");
	}
	
	/**
	 * 删除授权码
	 */
	public Ret delAuthToken(String authToken) {
		Db.delete("delete from sys_app_token where authToken=?", authToken);
		srv.clearLoginUser(authToken);
		return Ret.ok("msg", "删除授权码成功");
	}
	
}
