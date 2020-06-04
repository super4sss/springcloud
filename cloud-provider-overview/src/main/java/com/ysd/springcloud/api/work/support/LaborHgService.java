package com.ysd.springcloud.api.work.support;

import static org.apache.commons.lang3.math.NumberUtils.toInt;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.client.fluent.Request;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.ysd.springcloud.common.kit.ObjKit;

/**
 * 华工劳务对接服务
 */
public class LaborHgService implements LaborInterface {

	private static final Log log = Log.getLog(LaborHgService.class);

	@Override
	public Map<String, Integer> countWorker(String project) {
		Ret ret = executeAndGet(project, "proWorkerSum");
		JSONObject obj = ret.isOk() ? ret.getAs("data") : null;
		if (ObjKit.empty(obj)) {
			log.warn("获取实时人数为空，" + ret.getStr("msg"));
			return null;
		}
		/*	
		 * 	Method: ImmutableMap.of
		 *  Vassalage(隶属): import com.google.common.collect.ImmutableMap;
		 *  Returns an immutable map containing the given entries, in order.
		 *  按顺序，返回一个规定键入的不可变的Map容器
		 *  --------------------------------------------------
		 *  Method: toInt
		 *  Vassalage(隶属): import static org.apache.commons.lang3.math.NumberUtils.toInt;
		 *  <p>Convert a <code>String</code> to an <code>int</code>, returning
	     *  <code>zero</code> if the conversion fails.</p>
	     *  把一个字符串类型转换成整数类型，如果转换失败，返回零。
	     *
	     *  <p>If the string is <code>null</code>, <code>zero</code> is returned.</p>
	     *  如果这个字符串类型的参数为 null , 那么返回零。
		 */
		// 总人数/管理人员/工人
		return ImmutableMap.of(
				"total", toInt(obj.getString("total")), 
				"managementSum", toInt(obj.getString("managementSum")), 
				"workerSum", toInt(obj.getString("workerSum")));
	}

	@Override
	public Ret workerOnWeek(String project) {
		Date today = ObjKit.getDate();
		Date start = DateUtils.addDays(today, -7);
		Date end = DateUtils.addDays(today, -1);
		/*	
		 * 	Method: ImmutableMap.of
		 *  Returns an immutable map containing the given entries, in order.
		 *  按顺序，返回一个规定键入的不可变的Map容器
		 */
		Map<String, String> params = ImmutableMap.of(
				"start", DateFormatUtils.format(start, "yyyy-MM-dd"), 
				"end", DateFormatUtils.format(end, "yyyy-MM-dd"));
		
		Ret ret = executeAndGet(project, "projecWeekly", params);
		JSONObject obj = ret.isOk() ? ret.getAs("data") : null;
		if (null == obj) log.warn("获取本周人数为空，" + ret.getStr("msg"));
		
		Map<String, Object> map = Maps.newLinkedHashMapWithExpectedSize(7);
		while (start.compareTo(end) <= 0) {
			String key = DateFormatUtils.format(start, "yyyy-MM-dd");
			map.put(DateFormatUtils.format(start, "M月dd日"), getObj(obj, key));
			start = DateUtils.addDays(start, 1);
		}
		return Ret.ok("data", map);
	}
	
	private Map<String, Integer> getObj(JSONObject obj, String key) {
		int planNum = 0, realNum = 0;
		if (ObjKit.notEmpty(obj)) {
			planNum = getInt(obj.getJSONObject("plan"), key);
			realNum = getInt(obj.getJSONObject("real"), key);
		}
		return ImmutableMap.of("planNum", planNum, "realNum", realNum);
	}
	
	private int getInt(JSONObject obj, String key) {
		return ObjKit.notEmpty(obj) ? toInt(obj.getString(key)) : 0;
	}

	@Override
	public Ret workerOnDay(String project) {
		return executeAndGet(project, "summary");
	}

	@Override
	public Ret workerOnType(String project) {
		return executeAndGet(project, "worktype");
	}

	@Override
	public Ret getCraftWorker(String project, String carft) {
		return executeAndGet(project, "workerCraftList", ImmutableMap.of("carft", carft));
	}

	@Override
	public Ret getMonitoring(String project) {
		return executeAndGet(project, "projectvcr");
	}
	
	@Override
	public Map<String, String> getPMData(String project) {
		return ImmutableMap.<String, String>builder()
				// 噪声(实时值/时均值)
				.put("B03Real", "0")
				.put("B03Hour", "0")
				// PM10(实时值/时均值)
				.put("PM10Real", "0")
				.put("PM10Hour", "0")
				// PM25(实时值/时均值)
				.put("PM25Real", "0")
				.put("PM25Hour", "0").build();
	}

	public Ret execute(String project, String method) {
		return execute(project, method, Collections.emptyMap());
	}
	
	public Ret execute(String project, String method, Map<String, String> params) {
		String projectNum = getProjectNum(project);
		if (StrKit.isBlank(projectNum)) {
			return Ret.fail("msg", "获取劳务工程编码失败");
		}
		StringBuilder url = new StringBuilder();
		url.append(getProjectPath(project)).append("/").append(method);
		url.append("?projectNum=").append(projectNum);
		for (String key : params.keySet()) {
			url.append("&").append(key).append("=").append(params.get(key));
		}
		
		if (log.isDebugEnabled()) {
			log.debug("Request to " + url.toString());
		}
		
		String response = null;
		try {
			response = Request.Get(url.toString())
					.execute().returnContent().asString();
		} catch (Exception e) {
			log.error("劳务请求异常", e);
			return Ret.fail("msg", "调用劳务接口失败");
		}
		
		Map<String, Object> map = json2Map(response);
		if (ObjKit.empty(map)) {
			return Ret.fail("msg", "调用劳务接口结果数据转换失败");
		}
		
		String code = map.get("resultCode") + "";
		if (!"0".equals(code)) {
			return Ret.fail("msg", "对接劳务接口失败: " + map.get("resultDesc"));
		}
		
		return Ret.ok("data", map.get("resultData"));
	}
	
	public Ret executeAndGet(String project, String method) {
		return executeAndGet(project, method, Collections.emptyMap());
	}
	
	public Ret executeAndGet(String project, String method, Map<String, String> params) {
		Ret ret = execute(project, method, params);
		if (ret.isOk()) {
			JSONObject obj = ret.getAs("data");
			if (ObjKit.notEmpty(obj)) {
				String key = getProjectName(project);
				if (StrKit.notBlank(key) && obj.containsKey(key)) 
					ret.set("data", obj.get(key));
			}
		}
		return ret;
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

	@Override
	public Ret getSurveillanceVideo(String project) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Ret getCheckingInCount(String project, Integer size) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Ret getMonthSceneNumAvg(String project) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
