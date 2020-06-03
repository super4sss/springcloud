package com.ysd.overview.front.index;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.client.fluent.Request;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.Ret;
import com.jfinal.log.Log;
import com.ysd.overview.common.auth.UserPrincipal;
import com.ysd.overview.common.kit.ObjKit;

/**
 * 用户角色服务
 * 【具体参考凯宁的接口API：http://192.168.78.253:3000/project/27/interface/api 】
 */
public class UcenterService {
	
	public static final UcenterService me = new UcenterService();
	private static final Log log = Log.getLog(UcenterService.class);
	
	public Ret getLoginUser(String cookies) {
		return execute("user/get_logined_status", ImmutableMap.of("cookies", cookies));
	}
	
	public boolean hasPermission(UserPrincipal user, String perKey) {
		if (ObjKit.empty(perKey)) {
			return false;
		}
		Ret ret = execute("role/check_project_permisstion", 
				ImmutableMap.of("user", user.getUser(), 
						"project", user.getProject(), "per_titles", perKey));
		JSONObject obj = ret.isOk() ? ret.getAs("data") : null;
		if (ObjKit.empty(obj)) {
			log.warn("获取操作权限为空，" + ret.getStr("msg"));
			return false;
		}
		return "1".equals(obj.getString("is_perm"));
	}
	
	public boolean hasPermission(UserPrincipal user, Long roleId) {
		if (ObjKit.empty(roleId)) {
			return false;
		}
		Ret ret = execute("role/check_user_in_role_chain", 
				ImmutableMap.of("project", user.getProject(), 
						"user", user.getUser(), "role", roleId.toString()));
		JSONObject obj = ret.isOk() ? ret.getAs("data") : null;
		if (ObjKit.empty(obj)) {
			log.warn("获取所属角色为空，" + ret.getStr("msg"));
			return false;
		}
		return "1".equals(obj.getString("check_res"));
	}
	
	/**
	 * 获取所有的权限菜单
	 * @param user
	 * @return
	 */
	public Set<String> getPermissions(UserPrincipal user) {
		Ret ret = execute("role/get_user_perm_list", 
				ImmutableMap.of("project", user.getProject(), "uid", user.getUser()));
		JSONObject obj = ret.isOk() ? ret.getAs("data") : null;
		if (ObjKit.empty(obj)) {
			log.warn("获取用户权限为空，" + ret.getStr("msg"));
			return Collections.emptySet();
		}
		JSONArray list = obj.getJSONArray("permission_list");
		if (ObjKit.empty(list)) {
			return Collections.emptySet();
		}
		return list.stream().map(o -> ((JSONObject) o).getString("per_title")).collect(toSet());
	}
	
	public Ret getMembers(String project, Long... roleIds) {
		if (ObjKit.empty(roleIds)) {
			return Ret.fail("msg", "角色参数为空，请检查");
		}
		Set<Long> set = Sets.newHashSet();
		Map<Long, JSONObject> map = Maps.newTreeMap();
		for (Long rid : roleIds) {
			if (set.contains(rid)) {
				continue;
			}
			JSONObject obj = getMembers(project, rid.toString());
			if (ObjKit.empty(obj)) {
				continue;
			}
			map.put(obj.getLong("role_id"), toMember(obj, set, map));
		}
		JSONArray array = new JSONArray();
		for (JSONObject obj : map.values()) array.add(obj);
		return Ret.ok("data", array);
	}
	
	public JSONObject getMembers(String project, String role) {
		Ret ret = execute("role/get_role_chain_and_users", 
				ImmutableMap.of("project", project, "role", role));
		JSONObject obj = ret.isOk() ? ret.getAs("data") : null;
		if (ObjKit.empty(obj)) {
			log.warn("获取角色用户为空，" + ret.getStr("msg"));
			return null;
		}
		return obj.getJSONObject("role_tree");
	}
	
	private JSONObject toMember(JSONObject obj, 
			Set<Long> set, Map<Long, JSONObject> map) {
		JSONObject group = new JSONObject();
		group.put("type", "group");
		group.put("pid", obj.get("parent_id"));
		group.put("code", obj.getString("role_id"));
		group.put("name", obj.getString("role_name"));
		JSONArray array = new JSONArray();
		Long roleId = obj.getLong("role_id");
		array.addAll(toMember(obj.getJSONArray("childs"), set, map));
		array.addAll(toMember(obj.getJSONArray("users"), roleId));
		group.put("children", array);
		if (map.containsKey(roleId)) map.remove(roleId);
		set.add(roleId);
		return group;
	}
	
	private JSONObject toMember(JSONObject obj, Long pid) {
		JSONObject user = new JSONObject();
		user.put("type", "user");
		user.put("pid", pid);
		user.put("code", obj.getString("user_id"));
		user.put("name", obj.getString("user_name"));
		return user;
	}
	
	private List<JSONObject> toMember(
			JSONArray array, Set<Long> set, Map<Long, JSONObject> map) {
		if (ObjKit.empty(array)) {
			return Collections.emptyList();
		}
		return array.stream().map(o -> toMember((JSONObject) o, set, map)).collect(toList());
	}
	
	private List<JSONObject> toMember(JSONArray array, Long pid) {
		if (ObjKit.empty(array)) {
			return Collections.emptyList();
		}
		return array.stream().map(o -> toMember((JSONObject) o, pid)).collect(toList());
	}
	
	public Ret execute(String method) {
		return execute(method, Collections.emptyMap());
	}

	public Ret execute(String method, Map<String, String> params) {
		StringBuilder url = new StringBuilder();
		url.append(getPath()).append("/").append(method).append("/");
		int index = 0;
		for (String key : params.keySet()) {
			url.append((index++ == 0 ? "?" : "&"));
			url.append(key).append("=").append(params.get(key));
		}
		
		if (log.isDebugEnabled()) {
			log.debug("Request to " + url.toString());
		}
		
		String response = null;
		try {
			response = Request.Get(url.toString())
					.execute().returnContent().asString();
		} catch (Exception e) {
			log.error("Http请求异常", e);
			return Ret.fail("msg", "调用用户中心接口失败");
		}
		
		Map<String, Object> map = json2Map(response);
		if (ObjKit.empty(map)) {
			return Ret.fail("msg", "调用用户中心接口结果数据转换失败");
		}
		
		String code = map.get("result") + "";
		if (!"0".equals(code)) {
			return Ret.fail("msg", "对接用户中心接口失败: " + map.get("message"));
		}
		
		return Ret.ok("data", map.get("data"));
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Object> json2Map(String json) {
		try {
			return JsonKit.parse(json, Map.class);
		} catch (Exception e) {
			log.error("Json转换异常", e);
			return null;
		}
	}
	
	private String getPath() {
		return PropKit.get("ucenter.path");
	}
	
}
