package com.ysd.springcloud.api.work.support;

import com.google.common.cache.CacheLoader;
import com.google.common.collect.ImmutableMap;
import com.jfinal.kit.Ret;
import com.ysd.springcloud.common.kit.ObjKit;
import com.ysd.springcloud.common.service.CacheService;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 劳务对接服务
 */
public class LaborService extends CacheService implements LaborInterface {
	
	public static final LaborService me = new LaborService();
	
	/**
	 * 实时人数统计
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Integer> countWorker(String project) {
		Object obj = this.get(project);
		if (ObjKit.empty(obj)) {
			// 总人数/管理人员/工人
			return ImmutableMap.of(
					"total", NumberUtils.INTEGER_ZERO, 
					"managementSum", NumberUtils.INTEGER_ZERO, 
					"workerSum", NumberUtils.INTEGER_ZERO);
		}
		return (Map<String, Integer>) obj;
	}
	
	/**
	 * 本周人数统计
	 */
	@Override
	public Ret workerOnWeek(String project) {
		return getDelegate(project).workerOnWeek(project);
	}
	
	/**
	 * 当天人数统计
	 */
	@Override
	public Ret workerOnDay(String project) {
		return getDelegate(project).workerOnDay(project);
	}
	
	/**
	 * 实时工种统计
	 */
	@Override
	public Ret workerOnType(String project) {
		return getDelegate(project).workerOnType(project);
	}
	
	/**
	 * 获取工种人员
	 */
	@Override
	public Ret getCraftWorker(String project, String carft) {
		return getDelegate(project).getCraftWorker(project, carft);
	}
	
	/**
	 * 获取施工监控
	 */
	@Override
	public Ret getMonitoring(String project) {
		return getDelegate(project).getMonitoring(project);
	}
	
	/**
	 * 获取噪声和扬尘数据
	 */
	@Override
	public Map<String, String> getPMData(String project) {
		return getDelegate(project).getPMData(project);
	}

	private LaborService() {
		super(5L, TimeUnit.MINUTES);
		this.hgDelegate = new LaborHgService();
		this.fsDelegate = new LaborFsService();
		this.dbDelegate = new LaborDbService();
		this.venDelegate = new LaborVenService();
		this.hqDelegate = new LaborHQService();
	}
	
	@Override
	protected CacheLoader<String, Object> createCacheLoader() {
		return new CacheLoader<String, Object>() {
			@Override
			public Object load(String key) throws Exception {
				return createValue(key);
			}
		};
	}
	
	private Object createValue(String key) {
		// 总人数/管理人员/工人
		Map<String, Integer> map = getDelegate(key).countWorker(key);
		/*if (ObjKit.notEmpty(map)) {
			// 抓取监控视频图片
			EventKit.post(new CameraEvent(key));
		}*/
		return map;
	}
	
	private LaborInterface getDelegate(String project) {
		String type = getProjectType(project);
		if (TYPE_HG.equals(type)) return hgDelegate;
		if (TYPE_FS.equals(type)) return fsDelegate;
		if (TYPE_VE.equals(type)) return venDelegate;
		if (TYPE_VE.equals(type)) return hqDelegate;
		return dbDelegate;
	}
	
	private final LaborHgService hgDelegate;
	private final LaborFsService fsDelegate;
	private final LaborDbService dbDelegate;
	private final LaborVenService venDelegate;
	private final LaborHQService hqDelegate;

	
	public Ret getSurveillanceVideo(String project) {
		return getDelegate(project).getSurveillanceVideo(project);
	}

	public Ret getCheckingInCount(String project, Integer size) {
		return getDelegate(project).getCheckingInCount(project, size);
	}

	public Ret getMonthSceneNumAvg(String project) {
		return getDelegate(project).getMonthSceneNumAvg(project);
	}
	
}
