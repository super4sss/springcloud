package com.ysd.overview.api.schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.ysd.overview.common.dto.ScheduleDTO;

/**
 * 计划进度业务处理层
 * 
 * @author Administrator
 * @date 2019年7月8日
 *
 */
public class ScheduleService {

	public static final ScheduleService me = new ScheduleService();
	private static final String YEAR_MONTH = "yyyy-MM"; // 定义年月格式
	private static final String PRD_DB = "ysd_scheduling";						//生产的数据库名
	private static final String DEV_DB = "ysd_scheduling_0218";			//开发与测试的数据库名

	/**
	 * 计划进度统计
	 * @param project 项目标识
	 * @return
	 */
	public Map<String, Object> scheduleStatistics(String project) {
		String dbName = null;
		if(isDevMode()){
			dbName = PRD_DB;
		}else{
			dbName = DEV_DB;
		}
		Record minym = getMinYearMonth(project,dbName);
		Record maxym = getMaxYearMonth(project,dbName);
		if (StrKit.isBlank(minym.getStr("minYm")) || StrKit.isBlank(maxym.getStr("maxYm"))) {
			return ImmutableMap.<String, Object> builder().put("taskTotal", 0).put("taskTodo", 0).put("taskDone", 0)
					.build();
		}
		
		/* 这里定义从开始日期至结束日期的默认值，然后分别传入三个字段中，设置值 */
		List<Record> taskTotal = setTaskTotal(getTaskTotal(project,dbName),
				getMonthBetweenDate(minym.getStr("minYm"), maxym.getStr("maxYm")));
		List<Record> taskTodo = setTaskTodo(getTodoNum(project,dbName),
				getMonthBetweenDate(minym.getStr("minYm"), maxym.getStr("maxYm")));
		List<Record> taskDone = setTaskDone(getDoneNum(project,dbName),
				getMonthBetweenDate(minym.getStr("minYm"), maxym.getStr("maxYm")));

		return ImmutableMap.<String, Object> builder().put("taskTotal", taskTotal).put("taskTodo", taskTodo)
				.put("taskDone", taskDone).build();
	}

	/* 设置计划进度待办数 */
	private List<Record> setTaskTodo(List<Record> taskTodo, List<Record> list) {
		for (Record todo : taskTodo) {
			for (Record record : list) {
				if (record.get("month").equals(todo.get("month"))) {
					record.set("total", todo.get("total"));
				}
			}
		}
		return list;
	}

	/* 设置计划进度完成数 */
	private List<Record> setTaskDone(List<Record> taskDone, List<Record> list) {
		for (Record done : taskDone) {
			for (Record record : list) {
				if (record.get("month").equals(done.get("month"))) {
					record.set("total", done.get("total"));
				}
			}
		}
		return list;
	}

	/* 设置计划进度总数 */
	private List<Record> setTaskTotal(List<Record> taskTotal, List<Record> list) {
		for (Record total : taskTotal) {
			for (Record record : list) {
				if (record.get("month").equals(total.get("month"))) {
					record.set("total", total.get("total"));
				}
			}
		}
		return list;
	}

	/* 分月获取总任务数 */
	private List<Record> getTaskTotal(String project, String dbName) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT count(bt.id) total ,DATE_FORMAT(bt.createAt,'%Y-%m') AS month FROM ");
		sql.append(dbName).append(".bus_task bt, ");
		sql.append(dbName).append(".bus_job bj  ");
//		sql.append("FROM ysd_scheduling_0218.bus_task bt,ysd_scheduling_0218.bus_job bj ");
//		sql.append("FROM ysd_scheduling.bus_task bt,ysd_scheduling.bus_job bj ");
		sql.append("WHERE bj.project=? and bt.jobId=bj.id and bt.status < 2 ");
		sql.append("GROUP BY month ");
		sql.append("ORDER BY month asc");
		return Db.find(sql.toString(), project);
	}

	/* 分月获取待办数量 */
	private List<Record> getTodoNum(String project, String dbName) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT count(bt.id) total ,DATE_FORMAT(bt.createAt,'%Y-%m') AS month FROM ");
		sql.append(dbName).append(".bus_task bt, ");
		sql.append(dbName).append(".bus_job bj ");
//		sql.append("FROM ysd_scheduling_0218.bus_task bt,ysd_scheduling_0218.bus_job bj ");
//		sql.append("FROM ysd_scheduling.bus_task bt,ysd_scheduling.bus_job bj ");
		sql.append("where bt.status=? and bt.jobId=bj.id and bj.project=? ");
		sql.append("GROUP BY month ");
		sql.append("ORDER BY month asc");
		return Db.find(sql.toString(), ScheduleDTO.STATUS_OPEN, project);
	}

	/* 分月获取完成数量 */
	private List<Record> getDoneNum(String project, String dbName) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT count(bt.id) total ,DATE_FORMAT(bt.createAt,'%Y-%m') AS month FROM ");
		sql.append(dbName).append(".bus_task bt, ");
		sql.append(dbName).append(".bus_job bj ");
//		sql.append("FROM ysd_scheduling_0218.bus_task bt,ysd_scheduling_0218.bus_job bj  ");
//		sql.append("FROM ysd_scheduling.bus_task bt,ysd_scheduling.bus_job bj  ");
		sql.append("where bt.status=? and bt.jobId=bj.id and bj.project=? ");
		sql.append("GROUP BY month ");
		sql.append("ORDER BY month asc");
		return Db.find(sql.toString(), ScheduleDTO.STATUS_COMPLETED, project);
	}

	/* 获取最小月份 */
	private Record getMinYearMonth(String project, String dbName) {
		StringBuilder sql = new StringBuilder();
		sql.append("select min(DATE_FORMAT(bt.createAt,'%Y-%m')) as minYm FROM ");
		sql.append(dbName).append(".bus_task bt, ");
		sql.append(dbName).append(".bus_job bj ");
//		sql.append(" FROM ysd_scheduling_0218.bus_task bt,ysd_scheduling_0218.bus_job bj ");
//		sql.append(" FROM ysd_scheduling.bus_task bt,ysd_scheduling.bus_job bj ");
		sql.append("where bt.jobId=bj.id and bj.project=? and bt.status < 2");
		return Db.findFirst(sql.toString(), project);
	}

	/* 获取最大月份 */
	private Record getMaxYearMonth(String project, String dbName) {
		StringBuilder sql = new StringBuilder();
		sql.append("select max(DATE_FORMAT(bt.createAt,'%Y-%m')) as maxYm FROM ");
		sql.append(dbName).append(".bus_task bt, ");
		sql.append(dbName).append(".bus_job bj ");
//		sql.append(" FROM ysd_scheduling_0218.bus_task bt,ysd_scheduling_0218.bus_job bj ");
//		sql.append(" FROM ysd_scheduling.bus_task bt,ysd_scheduling.bus_job bj ");
		sql.append(" where bt.jobId=bj.id and bj.project=? and bt.status < 2");
		return Db.findFirst(sql.toString(), project);
	}

	/**
	 * 获取两个日期之间所有的月份集合
	 * 
	 * @param startTime
	 * @param endTime
	 * @return：YYYY-MM
	 */
	public static List<Record> getMonthBetweenDate(String startTime, String endTime) {
		SimpleDateFormat sdf = new SimpleDateFormat(YEAR_MONTH);
		// 声明保存日期集合
		List<Record> list = new ArrayList<>();
		Record record = new Record();

		try {
			// 转化成日期类型
			Date startDate = sdf.parse(startTime);
			Date endDate = sdf.parse(endTime);

			// 用Calendar 进行日期比较判断
			Calendar calendar = Calendar.getInstance();
			while (startDate.getTime() <= endDate.getTime()) {
				// 把日期添加到集合
				// list.add(sdf.format(startDate));
				record = new Record();
				record.set("total", 0);
				record.set("month", sdf.format(startDate));
				list.add(record);
				// 设置日期
				calendar.setTime(startDate);
				// 把日期增加一天
				calendar.add(Calendar.MONTH, 1);
				// 获取增加后的日期
				startDate = calendar.getTime();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/*判断是否是开发模式*/
	private boolean isDevMode() {
		return PropKit.getBoolean("app.prdMode", false);
	}

}
