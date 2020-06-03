package com.ysd.overview.front.index;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.lang3.math.NumberUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.ysd.overview.common.auth.UserPrincipal;
import com.ysd.overview.common.dto.FileDTO;
import com.ysd.overview.common.kit.ObjKit;
import com.ysd.overview.common.model.Folder;
import com.ysd.overview.common.service.LfsService;

/**
 * 项目文件夹服务
 */
public class FolderService {

	public static final FolderService me = new FolderService();
	private Folder dao = new Folder().dao();
	
	public Folder findByEntity(String entity, String entityId) {
		String sql = "select * from pro_folder where entity=? and entityId=?";
		return dao.findFirst(sql, entity, entityId);
	}
	
	public Folder findByName(String name) {
		return dao.findFirst("select * from pro_folder where name=?", name);
	}
	
	public String getFileName(String entity, String entityId) {
		Folder bean = null;
		if (StrKit.notBlank(entity) 
				&& StrKit.notBlank(entityId)) {
			bean = findByEntity(entity, entityId);
		}
		return ObjKit.notEmpty(bean) ? bean.getName() : null;
	}
	
	public String getFilePath(String name, String entity, UserPrincipal user) {
		String sql = "select code from pro_folder where name=?";
		String dir = Db.queryStr(sql, name);
		if (StrKit.notBlank(dir)) {
			return dir;
		}
		return create(user, entity, name);
	}
	
	public String create(UserPrincipal user, String entity) {
		String pid = user.getDocument();
		if (StrKit.isBlank(pid)) {
			return null;
		}
		String dir = LfsService.me.createFolder(pid, entity);
		if (StrKit.notBlank(dir)) {
			new Folder().set("code", dir)
			    .set("name", entity)
				.set("entity", entity)
				.set("entityId", user.getProject()).save();
		}
		return dir;
	}
	
	public String create(UserPrincipal user, String entity, String name) {
		Folder bean = findByEntity(entity, user.getProject());
		String pid = ObjKit.empty(bean) ? create(user, entity) : bean.getCode();
		if (StrKit.isBlank(pid)) {
			return null;
		}
		String dir = LfsService.me.createFolder(pid, name);
		if (StrKit.notBlank(dir)) {
			new Folder().set("code", dir).set("name", name).save();
		}
		return dir;
	}
	
	public void updateByName(String name, String entity, String entityId) {
		String sql = "update pro_folder set entity=?,entityId=? where name=?";
		Db.update(sql, entity, entityId, name);
	}
	
	public Ret deleteByEntity(String entity, String entityId) {
		Folder bean = findByEntity(entity, entityId);
		if (ObjKit.notEmpty(bean)) {
			bean.delete();
			return LfsService.me.deleteFile(bean.getCode());
		}
		return Ret.fail("msg", "文件不存在，请检查");
	}
	
	public Ret delFiles(String name, String fileIds, Function<Long,Ret> function) {
		Folder bean = findByName(name);
		if (ObjKit.empty(bean)) {
			return Ret.fail("msg", "上传目录不存在，请检查");
		}
		if (NumberUtils.isDigits(bean.getEntityId())) {
			Ret ret = function.apply(NumberUtils.createLong(bean.getEntityId()));
			if (ret.isFail()) return ret;
		}
		return LfsService.me.deleteFile(fileIds);
	}
	
	public List<FileDTO> getFiles(String entity, String entityId) {
		Folder bean = findByEntity(entity, entityId);
		if (ObjKit.empty(bean)) {
			return Collections.emptyList();
		}
		Ret ret = LfsService.me.getFiles(bean.getCode());
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
	
}
