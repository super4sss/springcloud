package com.ysd.overview.front.project.prop;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;

import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.ysd.overview.common.auth.UserPrincipal;
import com.ysd.overview.common.dto.ProjectDTO;
import com.ysd.overview.common.kit.ObjKit;
import com.ysd.overview.common.service.PropCacheService;

/**
 * 项目属性服务
 */
public class PropService {

	public static final PropService me = new PropService();
	private static final String NAME_PROP = "项目名称";
	
	public ProjectDTO findProject(UserPrincipal user) {
		//Map<String, String> map = PropCacheService.me.getOverviewMap(user.getProject());
		//String name = map.containsKey(NAME_PROP) ? map.get(NAME_PROP) : user.getProjectName();
		//return ProjectDTO.on(name).withPropMap(map).build();
		String sql = "select * from pro_prop where project=? and propSet=?";
		List<Record> list = Db.find(sql, user.getProject(), PropCacheService.OVERVIEW_SET);
		Map<String, String> map = list.stream().collect(
				toMap(r -> r.getStr("name"), r -> r.getStr("text")));
		String name = map.containsKey(NAME_PROP) ? map.get(NAME_PROP) : user.getProjectName();
		return ProjectDTO.on(name).withPropMap(map).build();
	}
	
	/**
	 * 保存项目属性
	 */
	public Ret save(String project, Map<String, String> propMap) {
		if (ObjKit.empty(propMap)) {
			return Ret.fail("msg", "项目属性不存在，请检查");
		}
		
		List<Record> list = propMap.entrySet().stream()
				.map(e -> toRecord(e, project)).collect(toList());
		
		Db.delete("delete from pro_prop where project=? and propSet=?", 
				project, PropCacheService.OVERVIEW_SET);
		Db.batchSave("pro_prop", list, list.size());
		//PropCacheService.me.clearOverviewMap(project);
		
		return Ret.ok("msg", "项目属性保存成功");
	}
	
	private Record toRecord(Map.Entry<String, String> me, String project) {
		return new Record()
				.set("project", project)
				.set("name", me.getKey())
				.set("text", me.getValue())
				.set("propSet", PropCacheService.OVERVIEW_SET);
	}
	
}
