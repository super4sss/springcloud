package com.ysd.springcloud.api.work.support;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.ysd.springcloud.common.kit.ObjKit;
import com.ysd.springcloud.common.model.CheckInCount;
import com.ysd.springcloud.common.model.SurveVdo;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.client.fluent.Request;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 风投劳务服务
 * @author Administrator
 * @date 2019年12月16日
 *
 */
public class LaborVenService implements LaborInterface{
	
	private static final SurveVdo vdoDao = new SurveVdo().dao();
	private static final CheckInCount checkDao = new CheckInCount();
	private static final Log log = Log.getLog(LaborVenService.class);
	private static final String MONTH_DAY = "MM月dd日";
	private static final String YEAR_MONTH = "yyyy年MM月";

	@Override
	public Map<String, Integer> countWorker(String project) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Ret workerOnWeek(String project) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Ret workerOnDay(String project) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Ret workerOnType(String project) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 获取工种信息
	 */
	@Override
	public Ret getCraftWorker(String project, String date) {
		Map<String,String> paramMap = new HashMap<>();
		paramMap.put("date", date);
		
		List<Map<String,Object>> resultArrMap = executeAndGet(project, "getRecentWorkTypeUser", paramMap);
//		if(ObjKit.notEmpty(resultArrMap)) {
//			resultArrMap = fmtMap(resultArrMap);
//			sort(resultArrMap, MONTH_DAY);
//		}
		return Ret.ok("data", resultArrMap);
	}

	@Override
	public Ret getMonitoring(String project) {

		return null;
	}

	@Override
	public Map<String, String> getPMData(String project) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * 获取监控视频
	 * @param project
	 * @param type
	 * @return
	 */
	private Ret findMonitoring(String project, String type) {
			String sql = "SELECT id,name,vdo_path as vcrPath,pic_path as picPath FROM cmd_surve_vdo WHERE project=? AND type=? AND is_act = ? AND is_del != ? ORDER BY sort ";
			List<SurveVdo> vdoList = vdoDao.find(sql, project, type, SurveVdo.IS_ACT, SurveVdo.IS_DEL);
			return ObjKit.notEmpty(vdoList) ? Ret.ok("data", vdoList) : Ret.ok("data", Collections.emptyList());
	}

	@Override
	public Ret getSurveillanceVideo(String project) {
		return findMonitoring(project, TYPE_VE);
	}

	/**
	 * 最近10天考勤统计
	 */
	@Override
	public Ret getCheckingInCount(String project, Integer size) {
		int endNum = 0;
		int beginNum = endNum - size;
		
		Map<String,String> paramMap = new HashMap<>();
		paramMap.put("beginDate", formatDate(getBeforeOrAfter(new Date(), beginNum)));
		paramMap.put("endDate", formatDate(getBeforeOrAfter(new Date(), endNum)));
		
		List<Map<String,Object>> resultArrMap = executeAndGet(project, "getRecentStatistics", paramMap);
		if(ObjKit.notEmpty(resultArrMap)) {
			resultArrMap = fmtMap(resultArrMap);
			sort(resultArrMap, MONTH_DAY);
			return Ret.ok("data", resultArrMap);
		}
		//返回静态数据
		return  findChekcingIn(project, TYPE_VE, size);
	}


	private List<Map<String, Object>> fmtMap(List<Map<String, Object>> resultArrMap) {
		List<Map<String, Object>> newResultArrMap = new ArrayList<>();
		Map<String,Object> resultMap;
		for (Map<String, Object> map : resultArrMap) {
			resultMap = new HashMap<>();
			resultMap.put("gmt_date", map.get("date"));
			resultMap.put("check_in_num", map.get("userCount"));
			resultMap.put("scene_num", map.get("count"));
			resultMap.put("avg_val", map.get("count"));
			newResultArrMap.add(resultMap);
		}
		return newResultArrMap;
	}

	private void sort(List<Map<String, Object>> resultArrMap, String parrten) {
		Collections.sort(resultArrMap, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> map1, Map<String, Object> map2) {
				long long1 = parseDate(map1.get("gmt_date"), parrten);
				long long2 = parseDate(map2.get("gmt_date"), parrten);
				if(long1 < long2){
					return -1;
				}else if(long1 == long2){
					return 0;
				}else{
					return 1;
				}
				
			}
			private long parseDate(Object object, String parrten) {
				SimpleDateFormat sdf = new SimpleDateFormat(parrten);
				try {
					Date parse = sdf.parse(object.toString());
					return parse.getTime();
				} catch (ParseException e) {
					e.printStackTrace();
				}
				return 0;
			}
		});
		
	}

	private String formatDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
	}
	
	@SuppressWarnings("static-access")
	private static Date getBeforeOrAfter(Date now, int num) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(now);
		calendar.add(calendar.DATE, num);
		return calendar.getTime();
	}
	
	

	
	public List<Map<String,Object>> executeAndGet(String project, String method, Map<String, String> params) {
		Ret ret = execute(project, method, params);
		List<Map<String,Object>> resultMap = new ArrayList<Map<String,Object>>();
		if (ret.isOk()) {
			JSONArray arr = ret.getAs("data");
			if (ObjKit.notEmpty(arr)) {
				for (int i = 0; i < arr.size(); i++) {
					JSONObject jsonObj = (JSONObject) arr.get(i);
					resultMap.add(toMap(jsonObj));
				}
			}
		}
		return resultMap;
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Object> toMap(JSONObject jsonObj) {
		Map<String, Object> map = new HashMap<>();
		try{
			map = JSONObject.parseObject(jsonObj.toJSONString(), Map.class);
		}catch(Exception e){
			log.error("类型转换错误", e);
		}
		return map;
	}

	public Ret execute(String project, String method, Map<String, String> params) {
		String projectNum = getProjectNum(project);
		if (StrKit.isBlank(projectNum)) {
			return Ret.fail("msg", "获取劳务工程编码失败");
		}
		StringBuilder url = new StringBuilder();
		url.append(getProjectPath(project)).append("/").append(method);
		url.append("?projectId=").append(projectNum);
    System.out.println(url);
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
		
		String code = map.get("code") + "";
		if (!"200".equals(code)) {
			return Ret.fail("msg", "对接劳务接口失败: " + map.get("msg"));
		}
		
		return Ret.ok("data", map.get("result"));
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
	

	/*静态数据 考勤统计*/
	private Ret findChekcingIn(String project, String typeVe, Integer size) {
		String sql = "SELECT DATE_FORMAT(Max(gmt_date),'%Y-%m-%d') as gmt_date FROM cmd_check_in_count WHERE type=? AND project=? AND is_deleted!=?";
		Record record = Db.findFirst(sql, typeVe, project, CheckInCount.IS_DELETED);
		if(ObjKit.notEmpty(record)){
			List<CheckInCount> lists = findCheckByDate(project, typeVe, size, record.getStr("gmt_date"));
			if(ObjKit.notEmpty(lists)){
				return Ret.ok("data", lists);
			}
		}
		return Ret.ok("data", Collections.emptyList());
	}

	private List<CheckInCount> findCheckByDate(String project, String typeVe, Integer size, String gmtDate) {
		StringBuffer sbf = new StringBuffer();
		sbf.append("SELECT id,DATE_FORMAT(gmt_date,'%m月%d日') AS gmt_date,check_in_num,scene_num");
		sbf.append(" FROM `cmd_check_in_count`");
		sbf.append(" WHERE").append(" DATE_FORMAT(gmt_date,'%Y-%m-%d') > DATE_SUB(?,INTERVAL ? DAY)");
		sbf.append(" AND").append(" type=?");
		sbf.append(" AND").append(" project=?");
		sbf.append(" AND").append(" is_deleted!=?");
		return checkDao.find(sbf.toString(), gmtDate, size, typeVe, project, CheckInCount.IS_DELETED);
	}

	@Override
	public Ret getMonthSceneNumAvg(String project) {
		
		Map<String,String> paramMap = new HashMap<>();
		paramMap.put("beginDate", formatDate(getBeforeOrAfterMonday(new Date(), -11)));
		paramMap.put("endDate", formatDate(getBeforeOrAfterMonday(new Date(), 0)));
		
		List<Map<String,Object>> resultArrMap = executeAndGet(project, "getMonthlyStatistics", paramMap);
		if(ObjKit.notEmpty(resultArrMap)) {
			resultArrMap = fmtMap2(resultArrMap);
			sort(resultArrMap, YEAR_MONTH);
			return Ret.ok("data", resultArrMap);
		}
		/*返回静态数据*/
		return staticMonthSceneNumAvg(project);
	}
	

	private List<Map<String, Object>> fmtMap2(List<Map<String, Object>> resultArrMap) {
		List<Map<String, Object>> newArrayList = new ArrayList<>();
		Map<String,Object> resultMap;
		for (Map<String, Object> map : resultArrMap) {
			resultMap = new HashMap<>();
			resultMap.put("gmt_date", map.get("date"));
			resultMap.put("avg_val", map.get("count"));
			newArrayList.add(resultMap);
		}
		return newArrayList;
	}

	@SuppressWarnings("static-access")
	private static Date getBeforeOrAfterMonday(Date now, int num) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(now);
		calendar.add(calendar.MONDAY, num);
		return calendar.getTime();
	}

	
	
	

	/**
	 * 获取年月平均在场人数
	 * 静态数据
	 * @param project
	 * @return
	 *
	 * @date 2020年3月6日
	 */
	private Ret staticMonthSceneNumAvg(String project) {
		List<Record> listRecd = findMonthSceneNumAvg(project);
		Map<String, Object> initDate = initDate();
		if(ObjKit.empty(listRecd)){
			return Ret.ok("data", initDate);
		}
		try{
			for (Record record : listRecd) {
				String yearMon = record.getStr("year_mon");
				@SuppressWarnings("unchecked")
				Map<String,Object> newMap = (Map<String, Object>) initDate.get(yearMon);
				if(yearMon.equals(newMap.get("date_str"))){
					newMap.put("avg_val", record.getInt("avg_val"));
					newMap.put("sum_val", record.getInt("sum_val"));
				}
			}
		}catch(Exception e){
			LogKit.warn("转换失败", e);
		}
		return Ret.ok("data", initDate);
	}

	private List<Record> findMonthSceneNumAvg(String project) {
		StringBuffer sbf = new StringBuffer();
		sbf.append("SELECT");
		sbf.append(" DATE_FORMAT(gmt_date,'%Y年%m月') as year_mon,SUM(DISTINCT scene_num) AS sum_val,AVG(DISTINCT scene_num) AS avg_val");
		sbf.append(" FROM").append(" cmd_check_in_count");
		sbf.append(" WHERE").append(" project=?");
		sbf.append(" AND").append(" type=?");
		sbf.append(" AND").append(" is_deleted!=?");
		sbf.append(" AND").append(" gmt_date > DATE_SUB(CURDATE(), INTERVAL ? YEAR)");
		sbf.append(" GROUP BY year_mon");
		return Db.find(sbf.toString(), project, TYPE_VE, CheckInCount.IS_DELETED, NumberUtils.INTEGER_ONE );
	}


	private static Map<String,Object> initDate(){
		Map<String,Object> curMap = new LinkedHashMap<String, Object>();
		Map<String,Object> curMonVal = null;
		String currMonth = null;
		Date currDate = new Date();
		for (int i = 11; i >=0; i--) {
			curMonVal = new HashMap<String, Object>();
			currMonth = getLast12Months(i,currDate);
			curMonVal.put("date_str", currMonth);
			curMonVal.put("sum_val", 0);
			curMonVal.put("avg_val", 0);
			curMap.put(currMonth, curMonVal);
		}
		return curMap;
	}
	
	// 获取最近12个月
	public static String getLast12Months(int monNum, Date currDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月");
		Calendar c = Calendar.getInstance();
		c.setTime(currDate);
		c.add(Calendar.MONTH, -monNum);
		Date m = c.getTime();
		return sdf.format(m);
	}

}
