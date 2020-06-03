package com.ysd.overview.api.work;

import java.time.LocalTime;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.jfinal.kit.Ret;
import com.ysd.overview.api.problem.ProblemApiService;
import com.ysd.overview.api.work.support.LaborService;
import com.ysd.overview.common.dto.CameraDTO;
import com.ysd.overview.common.kit.ObjKit;
import com.ysd.overview.common.service.PropCacheService;

/**
 * 施工数据服务
 */
public class WorkApiService {

	public static final WorkApiService me = new WorkApiService();
	
	/**
	 * 实时总数据
	 */
	public Map<String, Object> realtime(String project) {
		return ImmutableMap.<String, Object>builder()
				.put("worker", LaborService.me.countWorker(project))
				.put("schedule", getScheduleData(project))
				.put("problem", ProblemApiService.me.statByProject(project)).build();
	}
	
	/**
	 * 获取项目进度
	 */
	private Map<String, Object> getScheduleData(String project) {
		String startTime = PropCacheService.me.getConfigItem(project, "开工时间");
		String endTime = PropCacheService.me.getConfigItem(project, "竣工时间");
		
		String now = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
		long productionDays = ObjKit.getDays(startTime, now);
		long remainingDays = ObjKit.getDays(now, endTime);
		long totalDays = ObjKit.getDays(startTime, endTime);
		
		return ImmutableMap.<String, Object>builder()
				// 进度(开工时间/竣工时间/生产天数/剩余天数/完成率)
				.put("startTime", startTime)
				.put("endTime", endTime)
				.put("productionDays", productionDays)
				.put("remainingDays", remainingDays)
				.put("completionRates", calcRate(productionDays, totalDays)).build();
	}
	
	private String calcRate(Number num1, Number num2) {
        double d1 = num1 != null ? num1.doubleValue() : 0;
        double d2 = num2 != null ? num2.doubleValue() : 0;
        long rate = 0;
        if (d1 > 0 && d2 > 0) {
        	rate = Math.round((d1/d2*100));
        }
        return rate > 0 ? rate + "%" : "0";
    }
	
	/**
	 * 获取施工监控
	 */
	public Ret getMonitoring(String project) {
		Ret ret = LaborService.me.getMonitoring(project);
		if (ret.isFail()) return ret;
		
		JSONObject map = ret.getAs("data");
		if (ObjKit.empty(map)) return Ret.fail("msg", "获取施工监控为空");
		
		Set<CameraDTO> list = Sets.newTreeSet();
		for (String key : map.keySet()) {
			CameraDTO dto = new CameraDTO();
			JSONObject obj = map.getJSONObject(key);
			dto.setId(NumberUtils.toInt(StringUtils.substringAfter(key, "camera"), 0));
			dto.setName(obj.getString("cameraName"));
			dto.setVcrPath(obj.getString("path"));
			dto.setPicPath("/camera/"+project+"/"+key+".jpg");
			list.add(dto);
		}
		return ret.set("data", list);
	}
	
	/**
	 * 绿色施工
	 */
	public Map<String, Object> green(String project) {
		// 获取绿色施工数据（暂时使用随机数）

		double v1 = Math.floor(Math.random()*(65-60)+60);
		double v2 = Math.random()*(1.4-1.1)+1.0;
		
		LocalTime time = LocalTime.now();
        double num = time.getHour()*60*60+time.getMinute()*60+time.getSecond();
        double all = 12*60*60;
        double v3= 100/all*num;
        double v4= 300/all*num;
		
		return ImmutableMap.<String, Object>builder()
				// 噪声(控制值/当前值/单位)
				.put("noiseControl", "70")
				.put("noiseCurrent", String.valueOf((long) v1))
				.put("noiseUnit", "dB")
				// 扬尘(控制值/当前值/单位)
				.put("dustControl", "1.5")
				.put("dustCurrent", String.format("%.1f", v2))
				.put("dustUnit", "m")
				// 节水(控制值/当前值/单位)
				.put("waterControl", "300")
				.put("waterCurrent", String.format("%.2f", v3))
				.put("waterUnit", "t")
				// 节电(控制值/当前值/单位)
				.put("electricControl", "1000")
				.put("electricCurrent", String.format("%.2f", v4))
				.put("electricUnit", "KWh").build();
	}
	
	/**
	 * 劳务
	 */
	public Map<String, Object> labor(String project) {
		// 获取劳务数据（暂时使用随机数）
		
		Random random = new Random();
		int v1 = random.nextInt(10) + 30;
		int v2 = random.nextInt(10) + 80;
		
		// 施工人员/管理人员/工人
		return ImmutableMap.of("builders", v1+v2, "managers", v1, "workers", v2);
	}
	
	/**
	 * 进度
	 */
	public Map<String, Object> schedule(String project) {
		String now = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
		String startTime = PropCacheService.me.getConfigItem(project, "开工时间");
		String endTime = PropCacheService.me.getConfigItem(project, "竣工时间");
		long productionDays = ObjKit.getDays(startTime, now);
		long remainingDays = ObjKit.getDays(now, endTime);
		long totalDays = ObjKit.getDays(startTime, endTime);
		
		Random random = new Random();
		int v1 = random.nextInt(10)+1;
		int v2 = random.nextInt(v1);
		int v3 = random.nextInt(10)+1;
		int v4 = random.nextInt(v3);
		
		return ImmutableMap.<String, Object>builder()
				// 进度(开工时间/竣工时间/生产天数/剩余天数/完成率)
				.put("startTime", startTime)
				.put("endTime", endTime)
				.put("productionDays", productionDays)
				.put("remainingDays", remainingDays)
				.put("completionRates", calcRate(productionDays, totalDays))
				// 质量问题(发现/解决)
				.put("qualityFind", v1)
				.put("qualitySolve", v2)
				// 安全问题(发现/解决)
				.put("safetyFind", v3)
				.put("safetySolve", v4).build();
	}

	/**
	 * 获取静态的视频监控
	 * @param type
	 * @return
	 */
	public Ret getSurveillanceVideo(String project) {
		return LaborService.me.getSurveillanceVideo(project);
	}

	public Ret getCheckingInCount(String project, Integer size) {
		return LaborService.me.getCheckingInCount(project, size);
	}

	public Ret getMonthSceneNumAvg(String project) {
		return LaborService.me.getMonthSceneNumAvg(project);
	}
	
}
