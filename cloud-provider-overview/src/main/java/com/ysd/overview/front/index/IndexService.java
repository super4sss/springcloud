package com.ysd.overview.front.index;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.jfinal.kit.Kv;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.ysd.overview.common.auth.UserPrincipal;
import com.ysd.overview.common.dto.MenusDTO;
import com.ysd.overview.common.dto.TaskDTO;
import com.ysd.overview.common.kit.FileKit;
import com.ysd.overview.common.kit.ImgKit;
import com.ysd.overview.common.kit.ObjKit;
import com.ysd.overview.common.service.LfsService;

/**
 * 首页业务
 */
public class IndexService {

	public static final IndexService me = new IndexService();
	
	/**
	 * 获取用户和项目信息
	 */
	public Map<String, Object> getBusInfo(UserPrincipal user) {
		Set<String> perms = getUserPerms(user);
		return ImmutableMap.<String, Object>builder()
				.put("account", user.getAccount())
				.put("user", user.getUser())
				.put("username", user.getUsername())
				.put("project", user.getProject())
				.put("projectName", user.getProjectName())
				.put("isOwner", user.isOwner())
				.put("menus", getUserMenus(user, perms))
				.put("perms", perms).build();
	}
	
	/**
	 * 获取用户菜单
	 * @param user
	 * @param perms
	 * @return
	 */
	private List<Record> getUserMenus(UserPrincipal user, Set<String> perms) {
		if (ObjKit.empty(perms) 
				&& !user.isOwner()) {
			return Collections.emptyList();
		}
		String sql = "select * from pro_menu where status=1 order by sort";
		List<Record> list = Db.find(sql);
		return list.stream().filter(r -> {
			if (user.isOwner() && r.getInt("type") == 1) return true;
			return null != perms && perms.contains(r.getStr("code"));
		}).map(r -> {
			String url = r.getStr("url");
			r.set("title", setMenusName(r.get("title"),user));	//设置请求李工返回的菜单名
			if (StringUtils.startsWith(url, "/")) {
				if (url.indexOf('?') == -1) {
					url = url + "?soluId=" + user.getProject();
				} else {
					url = url + "&soluId=" + user.getProject();
				}
				r.set("url", url);
			}
			return r;
		}).collect(toList());
	}
	
	/**
	 * 设置菜单的名称
	 * @param title
	 * @param user 
	 * @return
	 */
	private String setMenusName(String title, UserPrincipal user) {
		List<MenusDTO> list = ClientService.me.getMenusName(user);
		for (MenusDTO bean : list) {
			String name = bean.getMenusName(bean.getToken());
			if(title.equals(name)){
				title = bean.getCustomName();
			}
		}
		return title;
	}
	
	/**
	 * 获取用户权限
	 * @param user
	 * @return
	 */
	public Set<String> getUserPerms(UserPrincipal user) {
		return UcenterService.me.getPermissions(user);
	}
	
	/**
	 * 获取项目信息
	 * @param user
	 * @return
	 */
	public Ret getProjectInfo(UserPrincipal user) {
		Ret ret = ClientService.me.getProjectInfo(user.genToken(), user.getProject());
		if (ret.isOk()) {
			JSONObject obj = ret.getAs("data");
			Kv data = Kv.create();
			if (ObjKit.notEmpty(obj)) {
				data.set("name", obj.getString("Name"))
					.set("startTime", amend(obj.getString("StartTime")))
					.set("endTime", amend(obj.getString("EndTime")))
					.set("timeLimit", obj.getString("TimeLimit"))
					.set("schedule", obj.getString("Schedule"));
			}
			ret.set("data", data);
		}
		return ret;
	}
	
	/**
	 * 获取阶段信息
	 */
	public Ret getStageInfo(String stage, UserPrincipal user) {
		Ret ret = ClientService.me.getStageInfo(user.genToken(), stage);
		JSONObject obj = ret.isOk() ? ret.getAs("data") : null;
		if (ObjKit.empty(obj)) {
			return Ret.fail("msg", "获取阶段信息失败，请联系管理员");
		}
		return ret.set("data", ImmutableMap.builder()
				.put("name", obj.getString("Name"))
				.put("startTime", amend(obj.getString("StartTime")))
				.put("endTime", amend(obj.getString("EndTime")))
				.put("timeLimit", obj.getString("TimeLimit"))
				.put("schedule", obj.getString("Schedule"))
				//.put("isOwner", extract(obj.getString("Owner"), user)).build());
				.put("isOwner", extract(obj.getLong("OwnerRoleID"), user)).build());
	}
	
	private String amend(String time) {
		return StringUtils.substringBefore(time, StringUtils.SPACE);
	}
	
	/*private boolean extract(String owner, UserPrincipal user) {
		if (user.isOwner()) return true;
		return ArrayUtils.contains(StringUtils.split(owner, ";"), user.getAccount());
	}*/
	
	private boolean extract(Long roleId, UserPrincipal user) {
		if (user.isOwner()) return true;
		//return UcenterService.me.hasPermission(user, roleId);
		return false;
	}
	
	/**
	 * 汇总发起任务
	 */
	public Map<String, String> statStartTask(String project) {
		List<Date> dates = getQueryDate(5);
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DATE_FORMAT(t.startAt,'%Y年%m月') AS date_str,");
		sql.append(" COUNT(t.startAt) AS start_num,");
		sql.append(" COUNT(t.finishAt) AS finish_num");
		sql.append(" FROM ysd_scheduling.bus_plan t");
		sql.append(" WHERE t.project=? AND t.startAt BETWEEN ? AND ?");
		sql.append(" GROUP BY date_str");
		List<Record> records = Db.find(sql.toString(), project, dates.get(0), dates.get(4));
		return toStatMap(dates, records);
	}
	
	private List<Date> getQueryDate(int times) {
		Date last = ObjKit.getLastDayOfMonth();
		Builder<Date> builder = ImmutableList.builder();
		for (int i = times - 1; i > 0; i--) {
			builder.add(ObjKit.getBeforeMonth(last, i));
		}
		return builder.add(last).build();
	}
	
	private Map<String, String> toStatMap(List<Date> dates, List<Record> records) {
		Map<String, String> map = Maps.newLinkedHashMapWithExpectedSize(dates.size());
		for (Date date : dates) {
			String m = DateFormatUtils.format(date, "yyyy年MM月");
			Optional<Record> o = records.stream().filter(r -> m.equals(r.getStr("date_str"))).findAny();
			map.put(m, o.map(r -> ObjKit.calcRate(r.getInt("finish_num"), r.getInt("start_num"))).orElse("0"));
		}
		return map;
	}
	
	/**
	 * 查找本周任务
	 */
	public List<TaskDTO> findWeekTask(String project) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT j.id,CONCAT(j.place,'-',j.name) title,");
		sql.append("t.assigneeName executorName,t.createAt startAt");
		sql.append(" FROM ysd_scheduling.bus_task t, ysd_scheduling.bus_job j");
		sql.append(" WHERE j.id = t.jobId AND j.project = ?");
		sql.append(" AND t.status = 0 AND t.createAt >= ?");
		sql.append(" ORDER BY t.createAt DESC");
		List<Record> list = Db.find(sql.toString(), project, ObjKit.getMonday());
		return list.stream().map(TaskDTO::onRecord).collect(toList());
	}

	/**
	 * 文件预览
	 * @param documentId		文件ID
	 * @param suffix				文件后缀
	 * @return
	 */
	public Ret downloadFile(String documentId, String suffix) {
		if(StrKit.isBlank(documentId) || StrKit.isBlank(suffix)){
			return Ret.fail("msg", "参数异常，请检查");
		}
		if(!FileKit.toPreview(suffix)){
			return Ret.fail("msg", "该文件不支持预览，请联系管理员");
		}
		Date nowtime = new Date();
		String savePath = "/preview/" + DateFormatUtils.format(nowtime, "yyyyMMdd");
		String fileName = DateFormatUtils.format(nowtime, "HHmmss") + ImgKit.getRandomName(6) + "." + suffix;
		try {
			String url = LfsService.me.download(documentId, savePath, fileName);
			return Ret.ok("filePath", url).set("fileType", FileKit.getFileType(suffix));
		} catch (IOException e) {
			LogKit.error("预览文件下载失败",e);
			return Ret.fail("msg", "预览文件下载失败");
		}
	}
	
/*	public static void main(String[] args) {
		Date nowtime = new Date();
		String suffix = "jpg";
		String savePath = "/onlinePreview/" + DateFormatUtils.format(nowtime, "yyyyMMdd");
		String fileName = DateFormatUtils.format(nowtime, "HHmmss") + ImgKit.getRandomName(6) + "." + suffix;
		System.out.println(ImgKit.getRandomName(6));
		System.out.println(savePath);
		System.out.println(fileName);
	}*/
	
}
