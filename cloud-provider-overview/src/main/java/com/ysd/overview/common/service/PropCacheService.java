package com.ysd.overview.common.service;

import static java.util.stream.Collectors.toMap;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.cache.CacheLoader;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.ysd.overview.common.kit.ObjKit;

/**
 * 项目属性服务
 */
public class PropCacheService extends CacheService {
	
	public static final PropCacheService me = new PropCacheService();
	public static final String OVERVIEW_SET = "Overview";
	public static final String APP_CONFIG_SET = "AppConfig";

	@Override
	protected CacheLoader<String, Object> createCacheLoader() {
		return new CacheLoader<String, Object>() {
			@Override
			public Object load(String key) throws Exception {
				Matcher matcher = getKeyPattern().matcher(key);
				return matcher.matches() ? findProp(matcher.group(1), matcher.group(2)) : null;
			}
		};
	}
	
	private Pattern getKeyPattern() {
		String sets = String.join("|", OVERVIEW_SET, APP_CONFIG_SET);
		return Pattern.compile("^(" + sets + ")_(\\d+)$");
	}
	
	private Map<String, String> findProp(String propSet, String project) {
		String sql = "select * from pro_prop where project=? and propSet=?";
		List<Record> list = Db.find(sql, project, propSet);
		if (ObjKit.empty(list)) {
			return null;
		}
		return list.stream().collect(toMap(r -> r.getStr("name"), r -> r.getStr("text")));
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, String> getOverviewMap(String project) {
		String key = OVERVIEW_SET + "_" + project;
		Object obj = this.get(key);
		if (ObjKit.empty(obj)) {
			return Collections.emptyMap();
		}
		return (Map<String, String>) obj;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, String> getConfigMap(String project) {
		String key = APP_CONFIG_SET + "_" + project;
		Object obj = this.get(key);
		if (ObjKit.empty(obj)) {
			return Collections.emptyMap();
		}
		return (Map<String, String>) obj;
	}
	
	public String getConfigItem(String project, String name) {
		return getConfigMap(project).get(name);
	}
	
	public void clearOverviewMap(String project) {
		remove(OVERVIEW_SET + "_" + project);
	}
	
	public void clearConfigMap(String project) {
		remove(APP_CONFIG_SET + "_" + project);
	}

}
