package com.ysd.overview.front.index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.client.fluent.Request;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.ysd.overview.common.auth.UserPrincipal;
import com.ysd.overview.common.dto.DictDTO;
import com.ysd.overview.common.dto.DirDTO;
import com.ysd.overview.common.dto.MenusDTO;
import com.ysd.overview.common.dto.UserDTO;
import com.ysd.overview.common.kit.ObjKit;

/**
 * 客户端服务
 */
public class ClientService {

	public static final ClientService me = new ClientService();
	private static final Log log = Log.getLog(ClientService.class);
	
	/**
	 * 获取阶段
	 * @param user
	 * @return
	 */
	public Ret getStages(UserPrincipal user) {
		StringBuilder buf = new StringBuilder();
		buf.append("action=GetStage");
		buf.append("&SolutionID=").append(user.getProject());
		buf.append("&userId=").append(user.getUser());
		
		Ret ret = execute(buf.toString(), user.genToken());
		if (ret.isOk()) {
			JSONArray array = ret.getAs("data");
			ret.set("data", toDict(array, "ID", "Name"));
		}
		return ret;
	}
	
	/**
	 * 获取成员
	 * @param user
	 * @param stage
	 * @param group
	 * @return
	 */
	public Ret getMembers(UserPrincipal user, String stage, String group) {
		StringBuilder buf = new StringBuilder();
		buf.append("action=GetGroupMemberTree");
		buf.append("&solutionID=").append(user.getProject());
		if (StrKit.notBlank(stage)) {
			buf.append("&stageID=").append(stage);
		}
		if (StrKit.notBlank(group)) {
			buf.append("&groupName=").append(group);
		}
		return execute(buf.toString(), user.genToken());
	}
	
	/**
	 * 是否是管理员
	 * @param stage
	 * @param user
	 * @return
	 */
	public boolean isOwner(String stage, UserPrincipal user) {
		Ret ret = getStageInfo(user.genToken(), stage);
		if (ret.isOk()) {
			JSONObject obj = ret.getAs("data");
			if (ObjKit.notEmpty(obj)) {
				/*String owner = obj.getString("Owner");
				return ArrayUtils.contains(StringUtils.split(owner, ";"), user.getAccount());*/
				Long roleId = obj.getLong("OwnerRoleID");
				return UcenterService.me.hasPermission(user, roleId);
			}
		}
		return false;
	}
	
	/**
	 * 获取文档标识ID
	 * @param stage
	 * @param user
	 * @return
	 */
	public String getDocument(String stage, UserPrincipal user) {
		Ret ret = getStageInfo(user.genToken(), stage);
		if (ret.isOk()) {
			JSONObject obj = ret.getAs("data");
			if (ObjKit.notEmpty(obj)) 
				return obj.getString("DocumentID");
		}
		return null;
	}
	
	/**
	 * 获取成员角色
	 * @param stage
	 * @param user
	 * @return
	 */
	public Long getMemberRole(String stage, UserPrincipal user) {
		Ret ret = getStageInfo(user.genToken(), stage);
		if (ret.isOk()) {
			JSONObject obj = ret.getAs("data");
			if (ObjKit.notEmpty(obj)) 
				return obj.getLong("MemberRoleID");
		}
		return null;
	}
	
	/**
	 * 获取阶段名称
	 * @param stage	阶段ID
	 * @param user		用户实体
	 * @return
	 */
	public String getStageName(String stage, UserPrincipal user) {
		Ret ret = getStageInfo(user.genToken(), stage);
		if (ret.isOk()) {
			JSONObject obj = ret.getAs("data");
			if (ObjKit.notEmpty(obj)) 
				return obj.getString("Name");
		}
		return null;
	}
	
	/**
	 * 获取阶段信息
	 * @param token
	 * @param stage
	 * @return
	 */
	public Ret getStageInfo(String token, String stage) {
		if (StrKit.isBlank(stage)) {
			return Ret.fail("msg", "阶段标识参数不能为空");
		}
		String paras = "action=GetStageInfo&stageID=" + stage;
		return execute(paras, token);
	}
	
	/**
	 * 获取用户信息
	 * @param token
	 * @param users
	 * @return
	 */
	public Ret getUserInfo(String token, String... users) {
		if (ObjKit.empty(users)) {
			return Ret.fail("msg", "用户标识参数不能为空");
		}
		String paras = "action=GetUserPhone&userNames=" + StrKit.join(users, ";");
		return execute(paras, token);
	}
	
	/**
	 * 获取用户
	 * @param token
	 * @param user
	 * @return
	 */
	public UserDTO getUser(String token, String user) {
		Ret ret = getUserInfo(token, user);
		if (ret.isFail()) {
			return null;
		}
		JSONArray array = ret.getAs("data");
		if (ObjKit.empty(array)) {
			return null;
		}
		JSONObject obj = array.getJSONObject(0);
		return UserDTO.getBuilder()
				.withCode(obj.getString("Name"))
				.withName(obj.getString("FullName"))
				.withEmail(obj.getString("Email"))
				.withPhone(obj.getString("PhoneNum")).build();
	}
	
	/**
	 * 获取项目信息
	 * @param token
	 * @param project
	 * @return
	 */
	public Ret getProjectInfo(String token, String project) {
		if (ObjKit.empty(project)) {
			return Ret.fail("msg", "项目标识参数不能为空");
		}
		String paras = "action=GetSolutionInfo&solutionID=" + project;
		return execute(paras, token);
	}
	
	/**
	 * 获取项目信息
	 * @param token
	 * @param project
	 * @param propName
	 * @return
	 */
	public String getProjectInfo(String token, String project, String propName) {
		Ret ret = getProjectInfo(token, project);
		if (ret.isOk()) {
			JSONObject obj = ret.getAs("data");
			if (ObjKit.notEmpty(obj)) 
				return obj.getString(propName);
		}
		return null;
	}
	
	/**
	 * 获取目录列表
	 * @param stage
	 * @param user
	 * @return
	 */
	public Ret getFolders(String stage, UserPrincipal user) {
		if (StrKit.isBlank(stage)) {
			return Ret.fail("msg", "阶段标识参数不能为空");
		}
		String paras = "action=GetMajorFileList&stageID=" + stage;
		Ret ret = execute(paras, user.genToken());
		if (ret.isOk()) {
			JSONArray array = ret.getAs("data");
			List<DirDTO> list = Lists.newArrayList();
			ret.set("data", toDir(list, array));
		}
		return ret;
	}
	
	private List<DirDTO> toDir(List<DirDTO> list, JSONArray array) {
		if (ObjKit.empty(array)) {
			return list;
		}
		for (int i = 0; i < array.size(); i++) {
			JSONObject obj = array.getJSONObject(i);
			DirDTO dir = new DirDTO();
			dir.setId(obj.getString("DocumentID"));
			dir.setText(obj.getString("text"));
			toDir(dir.getChildren(), obj.getJSONArray("children"));
			list.add(dir);
		}
		return list;
	}
	
	private List<DictDTO> toDict(JSONArray array, String code, String name) {
		if (ObjKit.empty(array)) {
			return Collections.emptyList();
		}
		List<DictDTO> list = Lists.newArrayList();
		for (int i = 0; i < array.size(); i++) {
			JSONObject obj = array.getJSONObject(i);
			list.add(new DictDTO(obj.getString(code), obj.getString(name)));
		}
		return list;
	}
	
	public Ret execute(String queryParas, String token) {
		String url = getApiUrl(queryParas);
		
		if (log.isDebugEnabled()) {
			log.debug("Request to " + url);
		}
		
		String response = null;
		try {
			response = Request.Get(url)
					.addHeader(getApiToken(), token)
					.execute().returnContent().asString();
		} catch (Exception e) {
			log.error("Http请求异常", e);
			return Ret.fail("msg", "调用应用接口失败");
		}
		
		Map<String, Object> map = json2Map(response);
		if (ObjKit.empty(map)) {
			return Ret.fail("msg", "调用应用接口结果数据转换失败");
		}
		
		Object obj = map.get("code");
		if (ObjKit.empty(obj)) {
			return Ret.fail("msg", "对接应用接口失败");
		}
		int code = NumberUtils.toInt(obj.toString());
		if (code == 301) {
			return Ret.fail("unauthorized", getAppDomain());
		}
		if (code != 200) {
			return Ret.fail("msg", map.get("msg"));
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
	
	private String getApiUrl(String queryParas) {
		if (StrKit.isBlank(queryParas)) {
			return getApiPath();
		}
		return getApiPath() + "?" + queryParas;
	}
	
	public static String getApiPath() {
		return PropKit.get("api.path");
	}
	
	public static String getApiToken() {
		return PropKit.get("api.token");
	}
	
	public static String getAppDomain() {
		return PropKit.get("app.domain");
	}

	/**
	 * 通过用户查找组织
	 * @param user
	 * @return
	 */
	public Map<String, Object> getOrganizationByCode(UserPrincipal user){
		Map<String, Object> orgName = getOrganization(user.getProject(),user.getUser(),user.genToken());
		return orgName;
		
	}

	/**
	 * 通过项目ID与用户ID用户所在的组织
	 * @param project
	 * @param token 
	 * @param user
	 * @return
	 */
	private Map<String, Object> getOrganization(String project, String userId, String token) {
		StringBuilder buf = new StringBuilder();
		buf.append("action=GetGroupPath");
		buf.append("&soluId=").append(project);
		buf.append("&userId=").append(userId);
		Ret ret = execute(buf.toString(), token);
		if (ret.isOk()) {
			JSONArray array = ret.getAs("data");
			if(array.size() > 0){
				JSONObject obj = (JSONObject) array.get(0);
				System.out.println(obj);
				Map<String, Object> map = new TreeMap<String, Object>();
				map.put("id", obj.get("ID"));
				map.put("name", obj.get("Name"));
				return map;
			}
		}
		return null;
	}

	/**
	 * 获取平台菜单自定义名称
	 * @param user
	 */
	public List<MenusDTO> getMenusName(UserPrincipal user){
		List<MenusDTO> list = new ArrayList<MenusDTO>();
		String url = getMenusPath(user);
		Ret ret = execute(url);
		JSONArray array = ret.isOk() ? ret.getAs("data") : null;
		if(ObjKit.notEmpty(array)){
			for (int i = 0; i < array.size(); i++) {
				JSONObject obj = (JSONObject) array.get(i);
				MenusDTO bean = new MenusDTO();
				bean.setToken(obj.getString("FuncName"));
				bean.setCustomName(obj.getString("CustomName"));
				list.add(bean);
			}
		}
		return list;
	}

	/**
	 * 获取总的请求路径
	 * http://192.168.78.253:91/OpenAPIHandler.ashx?action=GetSoluFuncCustomNames&soluId =289
	 * @param user
	 * @return
	 */
	private String getMenusPath(UserPrincipal user) {
		StringBuffer urlPath = new StringBuffer();
		urlPath.append(getApiPath());
		urlPath.append("?action=GetSoluFuncCustomNames");
		urlPath.append("&soluId=").append(user.getProject());
		return urlPath.toString();
	}
	
	/**
	 * 执行客户端请求
	 * @param url		客户端请求路径
	 * @return
	 */
	private Ret execute(String url) {
		if(log.isDebugEnabled()){
			log.debug("Request to " + url);
		}
		String response = null;
		try {
			response = Request.Get(url).execute().returnContent().asString();
		} catch (IOException e) {
			log.error("Http请求异常",e);
			return Ret.fail("msg", "调用应用接口失败");
		}
		Map<String, Object> map = json2Map(response);
		if (ObjKit.empty(map)) {
			return Ret.fail("msg", "调用应用接口结果数据转换失败");
		}
		Object obj = map.get("code");
		if(ObjKit.empty(obj)){
			return Ret.fail("msg", "对接应用接口失败");
		}
		int code = NumberUtils.toInt(obj.toString());
		if(code != 200){
			return Ret.fail("msg", map.get("msg"));
		}
		return Ret.ok("data", map.get("data"));
	}

	public UserPrincipal getProjectInfo(UserPrincipal user) {
		String paras = getApiPath() + "?action=GetSolutionInfo&solutionID=" + user.getProject();
		Ret ret = execute(paras);
		if (ret.isOk()) {
			JSONObject obj = ret.getAs("data");
			JSONArray ownArr = obj.getJSONArray("OwnerIDs");
			user.setProject(obj.getString("ID"));
			user.setProjectName(obj.getString("Name"));
			user.setOwner(ownArr.contains(Integer.valueOf(user.getUser())));
		}
		return user;
	}
	

}
