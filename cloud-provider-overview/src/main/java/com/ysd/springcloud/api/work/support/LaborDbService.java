package com.ysd.springcloud.api.work.support;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.ysd.springcloud.common.kit.ObjKit;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.client.fluent.Request;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

/**
 * 导入劳务数据服务
 */
public  class LaborDbService implements LaborInterface {

	private static final Log log = Log.getLog(LaborDbService.class);

	@Override
	public Map<String, Integer> countWorker(String project) {
		String sql = "select sum(realNum) from labor_worker where project=? and workday=?";
		Integer workerSum = Db.queryInt(sql, project, ObjKit.getDate());
		// 总人数/管理人员/工人
		return ImmutableMap.of(
				"total", workerSum, 
				"managementSum", NumberUtils.INTEGER_ZERO, 
				"workerSum", workerSum);
	}

	@Override
	public Ret workerOnWeek(String project) {
		Date today = ObjKit.getDate();
		Date start = DateUtils.addDays(today, -7);
		Date end = DateUtils.addDays(today, -1);
		
		StringBuilder sql = new StringBuilder();
		sql.append("select workday,sum(realNum) realNum,sum(planNum) planNum");
		sql.append(" from labor_worker");
		sql.append(" where project=? and workday between ? and ?");
		sql.append(" group by workday");
		List<Record> list = Db.find(sql.toString(), project, start, end);
		Map<String, Record> obj = list.stream().collect(toMap(r -> {
			return DateFormatUtils.format(r.getDate("workday"), "M月dd日");
		}, r -> r.remove("workday")));
		
		Map<String, Object> map = Maps.newLinkedHashMapWithExpectedSize(7);
		while (start.compareTo(end) <= 0) {
			String key = DateFormatUtils.format(start, "M月dd日");
			if (obj.containsKey(key)) {
				map.put(key, obj.get(key));
			} else {
				map.put(key, ImmutableMap.of("realNum", 0, "planNum", 0));
			}
			start = DateUtils.addDays(start, 1);
		}
		return Ret.ok("data", map);
	}

	@Override
	public Ret workerOnDay(String project) {
		Date today = ObjKit.getDate();
		Date yesterday = DateUtils.addDays(today, -1);
		
		StringBuilder sql = new StringBuilder();
		sql.append("select sum(realNum) realNum,sum(planNum) planNum");
		sql.append(" from labor_worker where project=? and workday=?");
		Record r1 = Db.findFirst(sql.toString(), project, today);
		Record r2 = Db.findFirst(sql.toString(), project, yesterday);
		
		Map<String, String> worker = ImmutableMap.of(
				"today", toNum(r1), "yesterday", toNum(r2), "left", "0");
		Map<String, String> management = ImmutableMap.of(
				"constructionUnit", "0/0", "builderUnit", "0/0", "supervisorUnit", "0/0");
		Map<String, String> live = ImmutableMap.of(
				"worker", toSum(r1), "construction", "0", "builder", "0", "supervisor", "0");
		return Ret.ok("data", ImmutableMap.<String, Object>builder()
				.put("worker", worker)
				.put("management", management)
				.put("live", live)
				.put("noPrintCertificate", "0")
				.put("noSafetyEducation", "0")
				.put("needNameReg", "0")
				.put("certificateIsAboutToExpire", "0").build());
	}
	
	private String toNum(Record record) {
		int realNum = 0, planNum = 0;
		if (null != record) {
			realNum = NumberUtils.toInt(record.getStr("realNum"), 0);
			planNum = NumberUtils.toInt(record.getStr("planNum"), 0);
		}
		return realNum + "/" + planNum;
	}
	
	private String toSum(Record record) {
		int realNum = 0;
		if (null != record) {
			realNum = NumberUtils.toInt(record.getStr("realNum"), 0);
		}
		return String.valueOf(realNum);
	}

	@Override
	public Ret workerOnType(String project) {
		StringBuilder sql = new StringBuilder();
		sql.append("select carft,sum(realNum) realNum");
		sql.append(" from labor_worker");
		sql.append(" where project=? and workday=?");
		sql.append(" group by carft");
		List<Record> list = Db.find(sql.toString(), project, ObjKit.getDate());
		return Ret.ok("data", list.stream().collect(toMap(r -> r.getStr("carft"), r -> {
			return ImmutableMap.of(
					"cardHolding", "0", 
					"punchCardCountYoday", r.getStr("realNum"), 
					"punchCardCountYesterday", "0");
		})));
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

    System.out.println(url);

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
