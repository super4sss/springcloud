package com.ysd.overview.front.project.doc;

import static java.util.stream.Collectors.toMap;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.google.common.collect.Lists;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.ysd.overview.common.auth.UserPrincipal;
import com.ysd.overview.common.kit.ObjKit;
import com.ysd.overview.common.model.LogDocument;
import com.ysd.overview.common.page.Paginator;
import com.ysd.overview.common.page.QueryHelper;
import com.ysd.overview.common.service.LfsService;
import com.ysd.overview.front.index.ClientService;

/**
 * 图档操作服务
 */
public class DocService {

	public static final DocService me = new DocService();
	private static final LogDocument dao = new LogDocument().dao();
	
	public Page<Record> paginate(String project, int pageNo, int pageSize) {
		if (pageNo < 1) pageNo = 1;
		if (pageSize < 2) pageSize = 2;
		int totalRow = getQueryCount(project, pageSize);
		List<Record> list = getQueryList(project, pageNo, pageSize);
		return new Page<Record>(list, pageNo, pageSize, totalRow/pageSize, totalRow);
	}
	
	private int getQueryCount(String project, int pageSize) {
		StringBuilder sql = new StringBuilder();
		sql.append("select date_format(createAt,'%Y-%m-%d') as dateStr");
		sql.append(" from log_document where project=? order by createAt limit 1");
		String dateStr = Db.queryStr(sql.toString(), project);
		if (StrKit.isBlank(dateStr)) return pageSize;
		
		//long days = ObjKit.getDays(dateStr, ObjKit.getDateStr())+1;
		long days = ObjKit.getDays(dateStr, ObjKit.getDateStr())/7+1;
		long num = days % pageSize;
		if (num > 0) days += pageSize - num;
		return (int) days;
	}
	
	private List<Record> getQueryList(String project, int pageNo, int pageSize) {
		//List<Date> dates = getQueryDate(pageNo, pageSize);
		Date end = ObjKit.getDate();
		if (pageNo > 1) {
			int amount = (pageNo - 1) * pageSize * -7;
			end = DateUtils.addDays(end, amount);
		}
		Date start = DateUtils.addDays(end, (pageSize*-7+1));
		
		QueryHelper<?> query = new QueryHelper<>();
		query.append("select t.dateStr,t.docNum,t.modNum,d.creatorName");
		query.append(" from (select date_format(createAt,'%Y-%m-%d') as dateStr,");
		query.append(" count(case when type=1 then 1 end) as docNum,");
		query.append(" count(case when type=2 then 1 end) as modNum,");
		query.append(" max(id) as lastOne from log_document");
		query.where("project=?").param(project).where("createAt between ? and ?");
		
		//query.param(dates.get(dates.size()-1)).param(dates.get(0));
		query.param(start).param(getLastTime(end));
		
		query.append(" group by dateStr) t");
		query.append(" join log_document d on t.lastOne = d.id");
		
		List<Record> list = Db.find(query.getSql(), query.getParas());
		//return getQueryList(list, dates);
		return getQueryList(list, start, end);
	}
	
	private List<Record> getQueryList(List<Record> list, Date start, Date end) {
		Map<String, Record> map = list.stream()
				.collect(toMap(r -> r.getStr("dateStr"), r -> r));
		
		List<Record> result = Lists.newArrayList();
		int index = 0; 
		while (end.compareTo(start) >= 0) {
			String date = DateFormatUtils.format(end, "yyyy-MM-dd");
			Record r1 = getQueryRecord(date, (index++ / 7), result);
			Record r2 = map.get(date);
			if (ObjKit.notEmpty(r2)) {
				r1.set("docNum", r2.getInt("docNum")+r1.getInt("docNum"));
				r1.set("modNum", r2.getInt("modNum")+r1.getInt("modNum"));
				if ("无".equals(r1.getStr("creatorName"))) 
					r1.set("creatorName", r2.getStr("creatorName"));
			}
			end = DateUtils.addDays(end, -1);
		}
		return result;
	}
	
	private Record getQueryRecord(String date, int index, List<Record> list) {
		if (list.size() > index) {
			return list.get(index).set("end", date);
		}
		Record record = new Record()
				.set("start", date).set("end", date)
				.set("docNum", 0).set("modNum", 0)
				.set("creatorName", "无");
		list.add(record);
		return record;
	}
	
	/*private List<Record> getQueryList(List<Record> list, List<Date> dates) {
		Map<String, Record> map = list.stream()
				.collect(toMap(r -> r.getStr("dateStr"), r -> r));
		
		return dates.stream().map(d -> {
			String key = DateFormatUtils.format(d, "yyyy-MM-dd");
			Record record = map.get(key);
			if (ObjKit.empty(record)) {
				record = new Record()
						.set("dateStr", key)
						.set("docNum", 0)
						.set("modNum", 0)
						.set("creatorName", "无");
			}
			return record;
		}).collect(toList());
	}
	
	private List<Date> getQueryDate(int pageNo, int pageSize) {
		Date date = ObjKit.getDate();
		if (pageNo > 1) {
			int num = (pageNo-1) * pageSize * -1;
			date = DateUtils.addDays(date, num);
		}
		Builder<Date> builder = ImmutableList.builder();
		builder.add(getLastTime(date));
		for (int i = 1; i < pageSize; i++) {
			builder.add(DateUtils.addDays(date, i*-1));
		}
		return builder.build();
	}*/
	
	private Date getLastTime(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c.getTime();
	}
	
	public Page<Record> paginate(DocParam form, int pageNo, int pageSize) {
		Date start = form.toStartDate();
		if (ObjKit.empty(start)) {
			List<Record> list = Collections.emptyList();
			return new Page<Record>(list, pageNo, pageSize, 0, 0);
		}
		Date end = form.toEndDate();
		if (ObjKit.empty(end)) end = start;
		
		QueryHelper<?> query = new QueryHelper<>();
		query.append("from log_document");
		query.where("project = ?").param(form.getProject());
		query.where("type = ?").param(form.getType());
		query.where("createAt between ? and ?").param(start).param(getLastTime(end));
		query.order("createAt desc");
		return Db.paginate(pageNo, pageSize, 
				"select *", query.getSql(), query.getParas());
	}
	
	public Ret save(DocParam param, UserPrincipal user) {
		Map<String, Object> map = ClientService.me.getOrganizationByCode(user);
		Integer orgId = null;String orgName = null;
		if(ObjKit.notEmpty(map)){
			orgId = (Integer) map.get("id");
			orgName = (String) map.get("name");
		}
		Db.save("log_document", new Record()
				.set("code", param.getCode())
				.set("name", param.getName())
				.set("path", param.getPath())
				.set("pathName", param.getPathName())
				.set("type", param.getType())
				.set("stageId", param.getStageId())
				.set("stageName", param.getStageName())
				.set("organizationId", orgId)
				.set("organization", orgName)
				.set("creator", user.getUser())
				.set("creatorName", user.getUsername())
				.set("createAt", new Date())
				.set("project", user.getProject()));
		return Ret.ok("msg", "记录保存成功");
	}

	/**
	 * 分页汇总图档更新日志
	 * @param project
	 * @param paginator
	 * @param dateStr 
	 * @return
	 */
	public Page<LogDocument> getDocLogSum(String project, Paginator paginator, String dateStr) {
		QueryHelper<LogDocument> helper = new QueryHelper<>();
		helper.append("SELECT ");
		helper.append(" DATE_FORMAT(createAt,'%Y-%m-%d') as dateStr,");
		helper.append(" COUNT(id) as uploadNum,");
		helper.append(" count(DISTINCT creator) as peopNum");
		helper.append(" FROM log_document");
		helper.where(" project=?").param(project);
//		helper.where(" DATE_FORMAT(createAt,'%Y-%m-%d')=?", dateStr);
		helper.where(" type=?").param(NumberUtils.INTEGER_ONE);
		helper.group(" DATE_FORMAT(createAt,'%Y-%m-%d'),creator");
		helper.order(" DATE_FORMAT(createAt,'%Y-%m-%d') DESC");
		return helper.paginate(dao, paginator.getPageNo(), paginator.getPageSize());
	}
	
	public Page<LogDocument> getDocLogDesc(String project, String dateStr, Paginator paginator) {
		QueryHelper< LogDocument> helper = new QueryHelper<>();
		helper.append("SELECT ");
		helper.append(" IFNULL(path,'--') AS path,IFNULL(pathName,'--') AS pathName,creator,creatorName,COUNT(id) AS uploadNum");
		helper.append(" FROM log_document");
		helper.where(" project=?").param(project);
		helper.where(" type=?").param(NumberUtils.INTEGER_ONE);
		helper.where(" DATE_FORMAT(createAt,'%Y-%m-%d')=?", dateStr);
		helper.group(" path,pathName,creator,creatorName");
		Page<LogDocument> paginate = helper.paginate(dao, paginator.getPageNo(), paginator.getPageSize());
		handleData(paginate);
		return paginate;
	}

	private void handleData(Page<LogDocument> paginate) {
		List<LogDocument> lists = paginate.getList();
		if(ObjKit.notEmpty(lists)){
			for (LogDocument locDO : lists) {
//				locDO.setPath(splitStr(locDO.getPath(), ","));
				locDO.setPathName(splitStr(locDO.getPathName(), "/"));
			}
		}
		
	}

	private static String splitStr(String value, String pattern) {
		if(StrKit.notBlank(value)){
			int indexOf = value.lastIndexOf(pattern);
			if(indexOf > 0){
				return value.substring(++indexOf);
			}
			return value;
		}
		return null;
	}
	
	private static String splitLastStr(String value, String pattern) {
		if(StrKit.notBlank(value)){
			int indexOf = value.lastIndexOf(pattern);
			if(indexOf != -1){
				return value.substring(0, indexOf);
			}
			return value;
		}
		return null;
	}

	public Ret setPathName(String project) {
		String sql = "SELECT id,code,name,path,pathName FROM log_document WHERE type=? AND project=? AND pathName IS NULL";
		 List<LogDocument> collection1 = dao.find(sql, NumberUtils.INTEGER_ONE, project);
		if(ObjKit.empty(collection1)){
			return Ret.fail("msg", "查询失败，数据为空。");
		}
		List<LogDocument> collection2  = collection1;
		String path1 = null;
		String path2 = null;
		String code = null;
		int success = 0;
		int error = 0;
		for (LogDocument logDO : collection1) {
			if(StrKit.isBlank(logDO.getPathName())){
					path1 = splitStr(logDO.getPath(), ",");
					code = logDO.getCode();
					//为空或不是正整数
					if(StrKit.isBlank(path1) || !matchNumber(code)) continue;
						//获取文档对象
						Record record  = findPath(logDO.getCode());
						if(ObjKit.empty(record)) continue;
						
						for (LogDocument logDO2 : collection2) {
							path2 = splitStr(logDO2.getPath(), ",");
							if(path1.equals(path2)){
								logDO2.setPathName(record.getStr("pathName"));
								if(logDO2.update()){
									success ++;
								}else{
									error ++;
								}
							}
						}
			}
		}
		/**************************************
		 * 274,276,277,285,289,290,291,301,307,309,310,311,315,320,322,330,332,333,338,348,349,350,362,363,364,365,367,369,370,372,377,378,384,385,386,388,389
		 *
		 *	SELECT id,code,name,path,pathName FROM log_document WHERE type=1 AND project=274 AND pathName IS NULL LIMIT 18;
		 */
		return Ret.ok("msg", "成功 " + success + " 条，失败 " + error + " 条");
	}

	@SuppressWarnings("unchecked")
	private Record findPath(String code) {
		Record record = null;
		//查询联用云
		Ret ret = LfsService.me.getFileInfo(code);
		if(!ret.isOk()){
			return null;
		}
		Map<String,Object> map = ret.getAs("data");
		if(!"SUCCESS".equals(map.get("status"))){
			return null;
		}
		Map<String,Object> rowMap = null;
		Object object = map.get("rows");
		if(object instanceof Map){
			rowMap = (Map<String, Object>) map.get("rows");
			record = new Record();
			record.set("pathids", splitLastStr(rowMap.get("pathids").toString(), ","));
			record.set("pathName", rowMap.get("path").toString());
		}
		return record;
	}

	public static boolean matchNumber(String code){
		String pattn = "^[1-9]\\d*$";
		if(Pattern.matches(pattn, code)) return true;
		return false;
	}
	
	
}