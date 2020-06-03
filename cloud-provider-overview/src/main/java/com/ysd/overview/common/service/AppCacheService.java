package com.ysd.overview.common.service;

import java.util.List;

import com.google.common.cache.CacheLoader;
import com.jfinal.plugin.activerecord.Db;
import com.ysd.overview.common.auth.UserPrincipal;
import com.ysd.overview.common.kit.ObjKit;
import com.ysd.overview.common.model.App;
import com.ysd.overview.common.model.AppToken;

/**
 * 应用授权服务
 */
public class AppCacheService extends CacheService {

	public static final AppCacheService me = new AppCacheService();
	private App appDao = new App().dao();
	private AppToken tokenDao = new AppToken().dao();
	
	@Override
	protected CacheLoader<String, Object> createCacheLoader() {
		return new CacheLoader<String, Object>() {
			@Override
			public Object load(String key) throws Exception {
				return createValue(key);
			}
		};
	}
	
	private Object createValue(String key) {
		AppToken token = findByAuthToken(key);
		if (ObjKit.empty(token)) {
			return null;
		}
		App app = appDao.findById(token.getAppId());
		if (ObjKit.empty(app)) {
			return null;
		}
		return UserPrincipal.getBuilder()
				.withProject(app.getChannel())
				.withAccount(app.getAppKey())
				.withAccessedTime(token.getLoginAt().getTime()).build();
	}
	
	private AppToken findByAuthToken(String authToken) {
		String sql = "select * from sys_app_token where authToken=?";
		return tokenDao.findFirst(sql, authToken);
	}
	
	public UserPrincipal getLoginUser(String token) {
		return (UserPrincipal) get(token);
	}
	
	public void clearLoginUser(String token) {
		remove(token);
	}
	
	public void clearLoginUser(Long appId) {
		String sql = "select authToken from sys_app_token where appId=?";
		List<String> list = Db.query(sql, appId);
		if (ObjKit.notEmpty(list)) list.forEach(this::remove);
	}

	public App getAppDao() {
		return appDao;
	}

	public AppToken getTokenDao() {
		return tokenDao;
	}
	
}
