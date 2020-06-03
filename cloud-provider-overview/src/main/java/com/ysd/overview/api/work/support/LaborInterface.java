package com.ysd.overview.api.work.support;

import java.util.Map;

import com.jfinal.kit.Ret;
import com.ysd.overview.common.service.PropCacheService;

/**
 * 劳务接口
 */
public interface LaborInterface {

	/**
	 * 实时人数统计
	 * @param project 工程标识
	 * @return
	 */
	public Map<String, Integer> countWorker(String project);
	
	/**
	 * 本周人数统计
	 * @param project 工程标识
	 * @return
	 */
	public Ret workerOnWeek(String project);
	
	/**
	 * 当天人数统计
	 * @param project 工程标识
	 * @return
	 */
	public Ret workerOnDay(String project);
	
	/**
	 * 实时工种统计
	 * @param project 工程标识
	 * @return
	 */
	public Ret workerOnType(String project);
	
	/**
	 * 获取工种人员
	 * @param project 工程标识
	 * @param carft 工种名称
	 * @return
	 */
	public Ret getCraftWorker(String project, String carft);
	
	/**
	 * 获取施工监控
	 * @param project 工程标识
	 * @return
	 */
	public Ret getMonitoring(String project);
	
	/**
	 * 获取噪声和扬尘数据
	 * @param project 工程标识
	 * @return
	 */
	public Map<String, String> getPMData(String project);
	
	default String getProjectNum(String project) {
		return PropCacheService.me.getConfigItem(project, "劳务工程编码");
    }
	
	default String getProjectName(String project) {
		return PropCacheService.me.getConfigItem(project, "劳务工程名称");
	}
	
	default String getProjectType(String project) {
		return PropCacheService.me.getConfigItem(project, "劳务工程类型");
	}
	
	default String getProjectPath(String project) {
		return PropCacheService.me.getConfigItem(project, "劳务工程地址");
	}
	
	default String getProjectSite(String project) {
		return PropCacheService.me.getConfigItem(project, "劳务工程站点");
	}
	
	public static final String TYPE_HG = "hgschool";
	public static final String TYPE_FS = "firehouse";
	public static final String TYPE_DB = "database";
	/*风投*/
	public static final String TYPE_VE = "venture";
	/*横琴*/
	public static final String TYPE_HQ = "hengqin";
	
	/**
	 * 获取监控视频
	 * @param project
	 * @return
	 *
	 * @date 2020年3月6日
	 */
	public Ret getSurveillanceVideo(String project);

	/**
	 * 最近10天统计
	 * @param project
	 * @param size
	 * @return
	 *
	 * @date 2020年3月6日
	 */
	public Ret getCheckingInCount(String project, Integer size);

	/**
	 * 月份统计
	 * @param project
	 * @return
	 *
	 * @date 2020年3月6日
	 */
	public Ret getMonthSceneNumAvg(String project);
	
}
