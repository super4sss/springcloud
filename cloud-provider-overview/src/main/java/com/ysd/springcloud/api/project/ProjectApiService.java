package com.ysd.springcloud.api.project;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.ysd.springcloud.common.auth.UserPrincipal;
import com.ysd.springcloud.common.dto.EventDTO;
import com.ysd.springcloud.common.dto.FileDTO;
import com.ysd.springcloud.common.kit.FileKit;
import com.ysd.springcloud.common.kit.ImgKit;
import com.ysd.springcloud.common.kit.ObjKit;
import com.ysd.springcloud.common.model.Picture;
import com.ysd.springcloud.common.page.Paginator;
import com.ysd.springcloud.common.page.QueryHelper;
import com.ysd.springcloud.common.service.LfsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.*;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
//import com.ysd.springcloud.common.service.PropCacheService;

/**
 * 项目数据服务
 */
@Slf4j
public class ProjectApiService {

	public static final ProjectApiService me = new ProjectApiService();
	//private static final String PRO_DOCUMENT = "技术文档";
	private static final String[] EXCLUDE = {"施工","施工管理","采购管理","招标管理","设计管理","初步设计","方案设计","施工图设计","会议资料","报批报建"};
	
	public Map<String, String> findPropMap(UserPrincipal user) {
		String sql = "select * from pro_prop where project=?";
		List<Record> list = Db.find(sql, user.getProject());
		if (ObjKit.empty(list)) {
			return Collections.emptyMap();
		}
		return list.stream().collect(toMap(r -> r.getStr("name"), r -> r.getStr("text")));
	}
	
	public List<EventDTO> findTopEvent(String project, int top) {
		String sql = "select id,title,createAt from pro_event where project=? order by id desc limit ?";
		List<Record> list = Db.find(sql, project, top);
		return list.stream().map(EventDTO::onRecord).collect(toList());
	}
	
	public Record findEventById(Long eventId) {
		if (ObjKit.empty(eventId)) {
			return null;
		}
		Record record = Db.findById("pro_event", eventId);
		if (ObjKit.empty(record)) {
			return null;
		}
		Date date = record.getDate("createAt");
		if (ObjKit.notEmpty(date)) {
			record.set("createAt", DateFormatUtils.format(date, "yyyy年MM月dd日 HH:mm"));
		}
		return record;
	}
	
	public List<FileDTO> getFiles(String pid, String project) {
		/*if (StrKit.isBlank(pid)) {
			pid = PropCacheService.me.getConfigItem(project, PRO_DOCUMENT);
		}
		if (StrKit.isBlank(pid)) {
			return Collections.emptyList();
		}*/
		if (StrKit.isBlank(pid)) {
			return ClientApiService.me.getStages(project);
		}
		Ret ret = LfsService.me.getFiles(pid);
		if (ret.isFail()) {
			return Collections.emptyList();
		}
		Map<String, Object> map = ret.getAs("data");
		return toFile((JSONArray) map.get("rows"));
	}
	
	private List<FileDTO> toFile(JSONArray array) {
		if (ObjKit.empty(array)) {
			return Collections.emptyList();
		}
		List<FileDTO> list = Lists.newArrayListWithExpectedSize(array.size());
		for (int i = 0; i < array.size(); i++) {
			JSONObject obj = array.getJSONObject(i);
			FileDTO file = new FileDTO();
			file.setId(obj.getString("id"));
			file.setName(obj.getString("name"));
			file.setSize(obj.getString("showSize"));
			file.setLastModifyTime(obj.getString("lastModifyDate"));
			file.setDirectory(obj.getBooleanValue("isDirectory"));
			list.add(file);
		}
		return list;
	}
	
	public Ret downloadFile(String id, String extName) {
		if (StrKit.isBlank(id) 
				|| StrKit.isBlank(extName)) {
			return Ret.fail("msg", "预览文件参数有误，请检查");
		}
		if (!FileKit.toPreview(extName)) {
			return Ret.fail("msg", "不支持该文件类型预览，请联系管理员");
		}
		try {
			Date now = new Date();
			String savePath = "/preview/" + DateFormatUtils.format(now, "yyyyMMdd");
			String fileName = DateFormatUtils.format(now, "HHmmss") + ImgKit.getRandomName(6) + "." + extName;
			String url = LfsService.me.download(id, savePath, fileName);
			return Ret.ok("filePath", url).set("fileType", FileKit.getFileType(extName));
		} catch (Exception e) {
			LogKit.error("下载预览文件失败", e);
			return Ret.fail("msg", "加载预览文件失败，请联系管理员");
		}
	}
	
	public Page<Record> findPictures(Paginator page, Picture form) {
		QueryHelper<?> query = new QueryHelper<>();
		query.append("from pro_picture");
		query.where("project = ?").param(form.getProject());
		query.where("type = ?", form.getType());
		query.where("name like ?", form.getName(), "%%%s%%");
		query.order("id desc");
		return Db.paginate(page.getPageNo(), page.getPageSize(), 
				"select id,name,path", query.getSql(), query.getParas());
	}
	
	public List<Record> getPictures(Picture form) {
		if (ObjKit.empty(form.getProject()) 
				|| ObjKit.empty(form.getType())) {
			return Collections.emptyList();
		}
		QueryHelper<?> query = new QueryHelper<>();
		query.append("select id,name,path from pro_picture");
		query.where("project = ?").param(form.getProject());
		query.where("type = ?").param(form.getType());
		query.where("name like ?", form.getName(), "%%%s%%");
		query.order("id desc");
		return Db.find(query.getSql(), query.getParas());
	}
	
	public List<Record> getVrShows(String project, String keyword) {
		String sql = "select name,viewPath from pro_vrshow where project=? order by id desc";
		List<Record> list = Db.find(sql, project);
		if (ObjKit.empty(list)) {
			return Collections.emptyList();
		}
		return list.stream().filter(r -> {
			String name = r.getStr("name");
			String path = r.getStr("viewPath");
			return StrKit.notBlank(name, path) 
					&& (StrKit.isBlank(keyword) || name.startsWith(keyword));
		}).collect(toList());
	}

	public Ret getFileList(String pid, Boolean root, String project) {
		if(StrKit.isBlank(pid)){
			pid = findPidByProject(project);
		}else if(!isDigits(pid)){
			return Ret.fail("msg", "参数值错误，请重新输入");
		}
		List<FileDTO> lists = new ArrayList<>();
    log.info("-----"+pid);
		Ret ret = LfsService.me.getFiles(pid);
		if(!ret.isOk()){
			LogKit.warn("对接服务失败, " + ret);
			return Ret.fail("msg", "对接服务失败");
		}
		try{
			Map<String,Object> map = ret.getAs("data");
			@SuppressWarnings("unchecked")
			List<Map<String,Object>> rowLists = (List<Map<String, Object>>) map.get("rows");
			for (Map<String, Object> map2 : rowLists) {
					if(root){
						/*排除不相符数据*/
						for (int i = 0; i < EXCLUDE.length; i++) {
							if(EXCLUDE[i].equals(map2.get("name")+"")) lists.add(wrapData(map2));
						}
					}
					else{
						lists.add(wrapData(map2));
					}
			}
		}catch(Exception e){
			LogKit.warn("类型转换错误，" + e);
			return Ret.fail("msg", "数据获取失败");
		}
		return Ret.ok("data", lists);
	}
	
	private FileDTO wrapData(Map<String, Object> map2) {
		FileDTO fileDO = new FileDTO();
		fileDO.setId(map2.get("id")+"");
		fileDO.setName(map2.get("name")+"");
		fileDO.setSize(map2.get("size")+"");
		fileDO.setLastModifyTime(map2.get("lastModifyDate")+"");
		fileDO.setDirectory((boolean) map2.get("isDirectory") ? true : false);
		return fileDO;
	}

	private String findPidByProject(String project) {
		String sql = "SELECT * FROM sys_app WHERE channel=?";
		Record record = Db.findFirst(sql, project);
		return ObjKit.notEmpty(record) ? record.getStr("docId") : null;
	}

	boolean isDigits(String val){
		return NumberUtils.isDigits(val);
	}
	
	

	
}
