package com.ysd.springcloud.api.doc;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.client.fluent.Request;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.Ret;
import com.jfinal.log.Log;
import com.ysd.springcloud.common.dto.DocDTO;
import com.ysd.springcloud.common.dto.OrganizationDTO;
import com.ysd.springcloud.common.kit.ObjKit;

/*
 * File DocClientService.java
 * ----------------------------
 * 技术图档客户端服务。用于访问李工的接口，并拼接封装返回的数据。
 */
public class DocClientService {

	protected static final DocClientService me = new DocClientService();
	private static final Log log = Log.getLog(DocClientService.class);
	DocType type = new DocType();
	
	/**
	 * 根据参数获取对应的图档文件
	 * @param project		项目标识
	 * @param typeVal		后缀名，如：
	 * @return
	 */
	protected List<DocDTO> getDocList(String project, String typeVal){
		String url = getRequestPath(project,typeVal);
		System.out.println(url);
		Ret ret = execute(url);
		System.out.println(ret);
		JSONArray array = ret.isOk() ? ret.getAs("data") : null;
		if(ObjKit.empty(array)){
			log.warn("获取文件失败，"+ ret.getStr("msg"));
			return Collections.emptyList();
		}
		/*创建一个空数组，并赋值*/
		List<DocDTO> list = Lists.newArrayListWithExpectedSize(array.size());
		for (int i = 0; i < array.size(); i++) {
			JSONObject obj = array.getJSONObject(i);
			DocDTO dto = new DocDTO();
			dto.setId(obj.getString("ID"));
			dto.setName(obj.getString("Name"));
			dto.setPath(obj.getString("Path"));
			dto.setPathIds(obj.getString("PathIds"));
			list.add(dto);
		}
		return list;
	}
	
	/**
	 * 获取组织
	 */
	protected List<OrganizationDTO> getOrganization(String project){
		String url = getOrganizationPath(project);
		Ret ret = execute(url);
		System.out.println(ret);
		JSONArray array = ret.isOk() ? ret.getAs("data") : null;
		if(ObjKit.empty(array)){
			log.warn("获取组织失败，"+ ret.getStr("msg"));
			return Collections.emptyList();
		}
		/*创建一个空数组，并赋值*/
		List<OrganizationDTO> list = Lists.newArrayListWithExpectedSize(array.size());
		for (int i = 0; i < array.size(); i++) {
			JSONObject obj = array.getJSONObject(i);
			OrganizationDTO dto = new OrganizationDTO();
			dto.setCode(obj.getString("id"));
			dto.setName(obj.getString("group_name"));
			list.add(dto);
		}
		return list;
		
	}
	

	/**
	 * 获取请求路径
	 * @param project
	 * @param suffixArr  ['.jpg','png']
	 * @return
	 */
	private String getRequestPath(String project, String suffix) {
		suffix = getParmType(suffix);
		StringBuilder url = new StringBuilder();
		url.append(getUrlPath()).append("?action=GetFilesByType");
		url.append("&soluId=").append(project);
		url.append("&typeLst=").append(suffix);
		return url.toString();
	}

	/**
	 * 判断参数类型
	 * @param suffixArr
	 * @return
	 */
	@SuppressWarnings("static-access")
	private String getParmType(String suffixArr) {
		if(type.BIMVIZ_MODEL.equals(suffixArr)){
			return type.BIMVIZ_MODEL_TYPE;
		}else if(type.OFFICE.equals(suffixArr)){
			return type.OFFICE_TYPE;
		}else if(type.PICTURE.equals(suffixArr)){
			return type.PICTURE_TYPE;
		}else if(type.VIDEO.equals(suffixArr)){
			return type.VIDEO_TYPE;
		}else if(type.NWD.equals(suffixArr)){
			return type.NWD_TYPE;
		}else{
			return null;
		}
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

	/**
	 * 字符串转 Map
	 * @param response
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> json2Map(String response) {
		try{
			return JsonKit.parse(response, Map.class);
		}catch(Exception e){
			log.error("Json转换异常", e);
			return null;
		}
	}

	/**
	 * 获取配置文件中的公共访问路径
	 * @return
	 */
	private static String getUrlPath(){
		return PropKit.get("api.path");
	}
	

	/**
	 * 获取组织列表的URL地址
	 * @param project
	 * @return
	 */
	private String getOrganizationPath(String project) {
		StringBuilder url = new StringBuilder();
		url.append(getUrlPath());
		url.append("?action=GetGroupTree");
		url.append("&soluId=").append(project);
		return url.toString();
	}
	
	
}
