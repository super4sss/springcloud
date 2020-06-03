package com.ysd.overview.api.project;

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
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.ysd.overview.common.dto.FileDTO;
import com.ysd.overview.common.kit.ObjKit;

/**
 * 客户端服务
 */
public class ClientApiService {

	public static final ClientApiService me = new ClientApiService();
	private static final Log log = Log.getLog(ClientApiService.class);
	
	public List<FileDTO> getStages(String project) {
		StringBuilder buf = new StringBuilder();
		buf.append("action=GetStage");
		buf.append("&SolutionID=").append(project);
		
		Ret ret = execute(buf.toString());
		JSONArray array = ret.isOk() ? ret.getAs("data") : null;
		if (ObjKit.empty(array)) {
			log.warn("获取项目阶段为空，" + ret.getStr("msg"));
			return Collections.emptyList();
		}
		List<FileDTO> list = Lists.newArrayListWithExpectedSize(array.size());
		for (int i = 0; i < array.size(); i++) {
			JSONObject obj = array.getJSONObject(i);
			FileDTO file = new FileDTO();
			file.setId(obj.getString("DocumentID"));
			file.setName(obj.getString("Name"));
			file.setDirectory(true);
			list.add(file);
		}
		return list;
	}
	
	public Ret execute(String queryParas) {
		String url = getApiUrl(queryParas);
		
		if (log.isDebugEnabled()) {
			log.debug("Request to " + url);
		}
		
		String response = null;
		try {
			response = Request.Get(url)
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
		String path = PropKit.get("api.path");
		return StrKit.isBlank(queryParas) ? path : path+"?"+queryParas;
	}
	
}
