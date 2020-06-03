package com.ysd.overview.api.problem;

import static java.util.stream.Collectors.toMap;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateFormatUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.ysd.overview.common.auth.UserPrincipal;
import com.ysd.overview.common.kit.ObjKit;
import com.ysd.overview.common.model.Problem;
import com.ysd.overview.common.page.Paginator;
import com.ysd.overview.common.page.QueryHelper;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.result.ExcelImportResult;


/**
 * 项目问题数据服务
 */
public class ProblemApiService {

	public static final ProblemApiService me = new ProblemApiService();
	
	public Page<Record> paginate(Paginator page, Problem form) {
		QueryHelper<?> query = new QueryHelper<>();
		query.append("from pro_problem");
		query.where("project = ?").param(form.getProject());
		query.where("type = ?", form.getType());
		query.where("category = ?", form.getCategory());
		query.where("specialty = ?", form.getSpecialty());
		query.where("area = ?", form.getArea());
		query.where("status = ?", form.getStatus());
		query.where("belongsGroup = ?", form.getBelongsGroup());
		if (ObjKit.notEmpty(form.getBeginTime())) {
			Date start = form.getBeginTime();
			Date end = ObjKit.getLastDayOfMonth(start);
			query.where("beginTime between ? and ?").param(start).param(end);
		}
		query.order("id desc");
		return Db.paginate(page.getPageNo(), page.getPageSize(), 
				"select *", query.getSql(), query.getParas());
	}
	
	/**
	 * 汇总项目总的问题个数
	 */
	public Record statByProject(String project) {
		StringBuilder sql = new StringBuilder("select ");
		// 质量问题(发现/解决)
		sql.append("count(case when type='质量' then 1 end) as qualityFind,");
		sql.append("count(case when type='质量' and (status='已完成' or status='已整改') then 1 end) as qualitySolve,");
		// 安全问题(发现/解决)
		sql.append("count(case when type='安全' then 1 end) as safetyFind,");
		sql.append("count(case when type='安全' and (status='已完成' or status='已整改') then 1 end) as safetySolve");
		sql.append(" from pro_problem where project = ?");
		return Db.findFirst(sql.toString(), project);
	}
	
	/**
	 * 按月份汇总类型问题
	 */
	public Map<String, Record> statByMonth(Problem form) {
		List<Date> dates = getQueryDate(12);
		
		QueryHelper<Problem> query = new QueryHelper<>();
		query.append("select date_format(beginTime,'%Y年%m月') as dateStr,");
		query.append(" count(beginTime) as findNum,");
		query.append(" count(case when status='已完成' then 1 end) as solveNum");
		query.append(" from pro_problem");
		query.where("project = ?").param(form.getProject());
		query.where("type = ?", form.getType());
		query.where("area = ?", form.getArea());
		query.where("beginTime between ? and ?").param(dates.get(0)).param(dates.get(11));
		query.append(" group by dateStr");
		List<Record> list = Db.find(query.getSql(), query.getParas());
		Map<String, Record> records = list.stream()
				.collect(toMap(r -> r.getStr("dateStr"), r -> r));
		
		return dates.stream().collect(toMap(d -> getDateKey(d), d -> {
			Record record = records.get(getDateKey(d));
			if (ObjKit.empty(record)) 
				record = new Record().set("findNum", 0).set("solveNum", 0);
			return record.set("solveRate", ObjKit.calcRate(
					record.getInt("solveNum"), record.getInt("findNum")));
		}, (k1, k2) -> k2, LinkedHashMap::new));
	}
	
	private List<Date> getQueryDate(int times) {
		Date last = ObjKit.getLastDayOfMonth();
		Builder<Date> builder = ImmutableList.builder();
		for (int i = times - 1; i > 0; i--) {
			builder.add(ObjKit.getBeforeMonth(last, i));
		}
		return builder.add(last).build();
	}
	
	private String getDateKey(Date date) {
		return DateFormatUtils.format(date, "yyyy年MM月");
	}
	
	/**
	 * 按专业汇总类型问题
	 */
	public Map<String, Integer> statBySpecialty(Problem form) {
		QueryHelper<Problem> query = new QueryHelper<>();
		query.append("select specialty as name,");
		query.append(" count(distinct code) as value");
		query.append(" from pro_problem");
		query.where("project = ?").param(form.getProject());
		query.where("type = ?", form.getType());
		query.where("area = ?", form.getArea());
		query.append(" group by specialty");
		List<Record> list = Db.find(query.getSql(), query.getParas());
		return list.stream().collect(toMap(r -> r.getStr("name"), r -> r.getInt("value")));
	}
	
	/**
	 * 按分包公司汇总类型问题
	 */
	public Map<String, Integer> statByGroup(Problem form) {
		QueryHelper<Problem> query = new QueryHelper<>();
		query.append("select belongsGroup as name,");
		query.append(" count(distinct code) as value");
		query.append(" from pro_problem");
		query.where("project = ?").param(form.getProject());
		query.where("type = ?", form.getType());
		query.where("area = ?", form.getArea());
		query.append(" group by belongsGroup");
		List<Record> list = Db.find(query.getSql(), query.getParas());
		return list.stream().collect(toMap(r -> r.getStr("name"), r -> r.getInt("value")));
	}

	public Ret importProb(UserPrincipal userDO, UploadFile file) {
		//导入参数
		ImportParams importParams = new ImportParams();
		//导入返回类,类型Map
		ExcelImportResult<Map<String,Object>> result = ExcelImportUtil.importExcelMore(file.getFile(), Map.class, importParams);
		//转换list数据
		List<Map<String, Object>> lists = result.getList();
		if(ObjKit.empty(lists)){
			return Ret.fail("msg", "导入失败，数据为空");
		}
		int success = 0;
		int fail = 0;
		for (Map<String, Object> map : lists) {
			try{
				boolean flag = saveToDB(map, userDO);
				if(flag){
					success++;
				}else{
					fail++;
				}
			}catch(Exception e){
				return Ret.fail("msg", "导入失败, 异常代码：" + e);
			}
		}
		return Ret.ok("data", "导入成功, 成功 " + success + " 条，失败 " + fail + " 条");
	}

	private boolean saveToDB(Map<String, Object> map, UserPrincipal userDO) {
		Record record = new Record();
		record.set("code", map.get("序号"));
		record.set("type", map.get("问题类型"));
		record.set("category", map.get("问题类别"));
		record.set("specialty", map.get("专业"));
		record.set("description", map.get("问题描述"));
		record.set("area", map.get("发生区域（各地块）"));
		record.set("position", map.get("位置"));
		record.set("beginTime", map.get("发生时间"));
		record.set("endTime", map.get("解决时间"));
		record.set("contactMan", map.get("责任人"));
		record.set("status", map.get("状态"));
		record.set("project", "310");
		return Db.save("pro_problem", record);
	}
	
}
