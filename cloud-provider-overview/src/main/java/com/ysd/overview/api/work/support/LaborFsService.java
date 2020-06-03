package com.ysd.overview.api.work.support;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.Consts;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.ysd.overview.common.kit.Md5Kit;
import com.ysd.overview.common.kit.ObjKit;

/**
 * 消防站劳务对接服务
 */
public class LaborFsService implements LaborInterface {
	
	private static final Log log = Log.getLog(LaborFsService.class);

	@Override
	public Map<String, Integer> countWorker(String project) {
		// 总人数/管理人员/工人
		return ImmutableMap.of(
				"total", NumberUtils.INTEGER_ZERO, 
				"managementSum", NumberUtils.INTEGER_ZERO, 
				"workerSum", NumberUtils.INTEGER_ZERO);
	}

	@Override
	public Ret workerOnWeek(String project) {
		Date today = ObjKit.getDate();
		Map<String, Object> map = Maps.newLinkedHashMapWithExpectedSize(7);
		for (int i = -7; i < 0; i++) {
			Date day = DateUtils.addDays(today, i);
			String key = DateFormatUtils.format(day, "M月dd日");
			map.put(key, ImmutableMap.of(
					"planNum", NumberUtils.INTEGER_ZERO, 
					"realNum", NumberUtils.INTEGER_ZERO));
		}
		return Ret.ok("data", map);
	}

	@Override
	public Ret workerOnDay(String project) {
		Map<String, String> worker = ImmutableMap.of(
				"today", "0/0", "yesterday", "0/0", "left", "0");
		Map<String, String> management = ImmutableMap.of(
				"constructionUnit", "0/0", "builderUnit", "0/0", "supervisorUnit", "0/0");
		Map<String, String> live = ImmutableMap.of(
				"worker", "0", "construction", "0", "builder", "0", "supervisor", "0");
		return Ret.ok("data", ImmutableMap.<String, Object>builder()
				.put("worker", worker)
				.put("management", management)
				.put("live", live)
				.put("noPrintCertificate", "0")
				.put("noSafetyEducation", "0")
				.put("needNameReg", "0")
				.put("certificateIsAboutToExpire", "0").build());
	}

	@Override
	public Ret workerOnType(String project) {
		Map<String, String> type1 = ImmutableMap.of(
				"cardHolding", "632", 
				"punchCardCountYoday", "118", 
				"punchCardCountYesterday", "167");
		Map<String, String> type2 = ImmutableMap.of(
				"cardHolding", "635", 
				"punchCardCountYoday", "181", 
				"punchCardCountYesterday", "217");
		Map<String, String> type3 = ImmutableMap.of(
				"cardHolding", "93", 
				"punchCardCountYoday", "36", 
				"punchCardCountYesterday", "36");
		return Ret.ok("data", ImmutableMap.of(
				"钢筋工", type1, "木工", type2, "混凝土工", type3));
	}

	@Override
	public Ret getCraftWorker(String project, String carft) {
		Map<String, String> map = ImmutableMap.<String, String>builder()
				.put("projWorkNum", "000")
				.put("name", "张三").put("age", "33")
				.put("appPhotoPath", PropKit.get("system.picDomain")+"/labor/default_icon.jpg")
				.put("officeName", "广州阳光劳务有限公司")
				.put("groupName", carft+"班组")
				.put("carftName", carft).build();
		return Ret.ok("data", ImmutableList.of(map));
	}

	@Override
	public Ret getMonitoring(String project) {
		JSONObject camera1 = new JSONObject();
		camera1.put("cameraName", "探头1");
		camera1.put("path", "http://hls.open.ys7.com/openlive/f44ab925012d44cc8cc543f8e056f802.m3u8");
		JSONObject camera2 = new JSONObject();
		camera2.put("cameraName", "探头2");
		camera2.put("path", "http://hls.open.ys7.com/openlive/a9f59532c778423a8b2bd486d71fca2d.m3u8");
		return Ret.ok("data", new JSONObject(
				ImmutableMap.of("camera1", camera1, "camera2", camera2)));
	}

	@Override
	public Map<String, String> getPMData(String project) {
		JSONObject real = getPMRealData(project);
		JSONObject hour = real != null ? getPMHourData(project) : null;
		return ImmutableMap.<String, String>builder()
				// 噪声(实时值/时均值)
				.put("B03Real", nullSafe(real, "B03-Avg"))
				.put("B03Hour", nullSafe(hour, "B03-Avg"))
				// PM10(实时值/时均值)
				.put("PM10Real", nullSafe(real, "PM10-Avg"))
				.put("PM10Hour", nullSafe(real, "PM10-Avg"))
				// PM25(实时值/时均值)
				.put("PM25Real", nullSafe(real, "PM25-Avg"))
				.put("PM25Hour", nullSafe(real, "PM25-Avg")).build();
	}
	
	private JSONObject getPMRealData(String project) {
		Ret ret = execute(project, "getRealData", 
				ImmutableMap.of("siteIds", getProjectSite(project)));
		JSONArray array = ret.isOk() ? ret.getAs("data") : null;
		if (ObjKit.empty(array)) {
			log.warn("获取扬尘实时值为空，" + ret.getStr("msg"));
			return null;
		}
		return array.getJSONObject(0);
	}
	
	private JSONObject getPMHourData(String project) {
		Ret ret = execute(project, "getHourData", 
				ImmutableMap.of("siteIds", getProjectSite(project)));
		JSONArray array = ret.isOk() ? ret.getAs("data") : null;
		if (ObjKit.empty(array)) {
			log.warn("获取扬尘时均值为空，" + ret.getStr("msg"));
			return null;
		}
		return array.getJSONObject(0);
	}
	
	private String nullSafe(JSONObject obj, String key) {
		String str = obj != null ? obj.getString(key) : null;
		return StrKit.notBlank(str) ? str : "0";
	}
	
	public Ret execute(String project, String method) {
		return execute(project, method, Collections.emptyMap());
	}
	
	public Ret execute(String project, String method, Map<String, String> params) {
		Form form = getBodyForm(project);
		if (ObjKit.empty(form)) {
			return Ret.fail("msg", "获取对接密钥失败");
		}
		for (String key : params.keySet()) {
			form.add(key, params.get(key));
		}
		
		String path = getProjectPath(project) + "/" + method;
		if (log.isDebugEnabled()) {
			log.debug("Request to " + path);
		}
		
		String response = null;
		try {
			response = Request.Post(path)
					.bodyForm(form.build(), Consts.UTF_8)
					.execute().returnContent().asString(Consts.UTF_8);
		} catch (Exception e) {
			log.error("消防站请求异常", e);
			return Ret.fail("msg", "调用远程接口失败");
		}
		
		Map<String, Object> map = json2Map(response);
		if (ObjKit.empty(map)) {
			return Ret.fail("msg", "调用远程接口结果数据转换失败");
		}
		
		String code = map.get("code") + "";
		if (!"200".equals(code)) {
			Object msg = map.get("msg");
			if (null == msg) msg = map.get("message");
			return Ret.fail("msg", "对接远程接口失败: " + msg);
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
	
	private Form getBodyForm(String project) {
		String name = getProjectName(project);
		String pswd = getProjectNum(project);
		if (StrKit.isBlank(name) 
				|| StrKit.isBlank(pswd)) {
			return null;
		}
		String token = name + pswd + ObjKit.getDateStr();
		return Form.form().add("userName", name)
				.add("token", Md5Kit.encrypt(token));
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
