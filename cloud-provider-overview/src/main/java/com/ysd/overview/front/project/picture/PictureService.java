package com.ysd.overview.front.project.picture;

import java.io.*;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;
import com.ysd.overview.common.auth.UserPrincipal;
import com.ysd.overview.common.kit.FileKit;
import com.ysd.overview.common.kit.ImgKit;
import com.ysd.overview.common.kit.ObjKit;
import com.ysd.overview.common.model.Picture;
import com.ysd.overview.common.page.Paginator;
import com.ysd.overview.common.page.QueryHelper;

/**
 * 项目图片服务
 */
public class PictureService {

	public static final PictureService me = new PictureService();
	private Picture dao = new Picture().dao();
	
	public Page<Picture> paginate(Paginator page, Picture form) {
		QueryHelper<Picture> query = new QueryHelper<>();
		query.append("select id,name,customName,path,uploadTime,site from pro_picture");
		query.where("type = ?", form.getType());
		query.where("project = ?").param(form.getProject());
		query.order("id desc");
		return query.paginate(dao, page.getPageNo(), page.getPageSize());
	}
	
	public List<Picture> findByType(Integer type, UserPrincipal user) {
		if (!ObjKit.isDigits(type)) {
			return Collections.emptyList();
		}
		QueryHelper<Picture> query = new QueryHelper<>();
		query.append("select id,name,customName,path,uploadTime,site from pro_picture");
		query.where("type = ?").param(type);
		query.where("project = ?").param(user.getProject());
		return query.order("id desc").find(dao);
	}
	
	/**
	 * 保存项目图片
	 */
	public Ret save(UploadFile uf, Picture picture, String resize) {
		if (ObjKit.empty(uf)) {
			return Ret.fail("msg", "上传图片对象不存在，请检查");
		}
		try {
			if (ImgKit.notImage(uf.getFileName())) {
				return Ret.fail("msg", "上传图片不支持，只支持类型：jpg、jpeg、png、bmp");
			}
			if (ObjKit.empty(picture.getType())) {
				return Ret.fail("msg", "图片类别不能为空");
			}
			doResize(uf.getFile(), resize, picture.getPath());
			picture.set("name", getFileName(uf)).save();
			return Ret.ok("msg", "图片保存成功");
		}
		catch (Exception e) {
			LogKit.error("缩放图片失败", e);
			return Ret.fail("msg", "图片保存失败，请重试");
		} finally {
			uf.getFile().delete();
		}
	}
	
	/**
	 * 保存项目图片,并返回路径
	 */
	public Ret saveFile(UploadFile uf, Picture picture, String resize) {
		if (ObjKit.empty(uf)) {
			return Ret.fail("msg", "上传图片对象不存在，请检查");
		}
		try {
			if (ImgKit.notImage(uf.getFileName())) {
				return Ret.fail("msg", "上传图片不支持，只支持类型：jpg、jpeg、png、bmp");
			}
			if (ObjKit.empty(picture.getType())) {
				return Ret.fail("msg", "图片类别不能为空");
			}
			File file =uf.getFile();

      FileKit.write(new FileInputStream(file),new FileOutputStream(new File("D:\\ysd\\picture\\originalFile\\"+file.getName())));
			doResize(uf.getFile(), resize, picture.getPath());
			picture.set("name", getFileName(uf));
			return Ret.ok("data", picture);
		}
		catch (Exception e) {
			LogKit.error("缩放图片失败", e);
			return Ret.fail("msg", "图片保存失败，请重试");
		} finally {
			uf.getFile().delete();
		}
	}
	
	private String getFileName(UploadFile uf) {
		return StringUtils.substringBefore(uf.getFileName(), ".");
	}
	
	private void doResize(File srcFile, String resize, String savePath) {
		savePath = PropKit.get("system.picPath") + savePath;
		File dir = new File(savePath);
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				throw new RuntimeException("Directory " + savePath + " not exists and can not create directory.");
			}
		}
		
		List<int[]> list = ObjKit.collect(resize, (s1,s2) -> {
			return new int[]{ NumberUtils.toInt(s1, 200), NumberUtils.toInt(s2, 200) };
		});
		
		if (ObjKit.empty(list)) {
			ImgKit.resize(srcFile, 200, 200, savePath+"1.jpg");
			return;
		}
		for (int i = 0; i < list.size(); i++) {
			int[] size = list.get(i);
			ImgKit.resize(srcFile, size[0], size[1], savePath+(i+1)+".jpg");
		}
	}
	
	/**
	 * 删除项目图片
	 */
	public Ret delete(String ids, UserPrincipal user) {
		String[] array = StringUtils.split(ids, ",");
		if (ObjKit.empty(array)) {
			return Ret.fail("msg", "图片ID不能为空，请检查");
		}
		for (String id : array) {
			Picture model = dao.findById(id);
			if (ObjKit.empty(model)) {
				continue;
			}
			if (model.delete()) {
				String filePath = PropKit.get("system.picPath") + model.getPath();
				FileKit.deleteQuietly(filePath);
			}
		}
		return Ret.ok("msg", "图片删除成功");
	}
	
	public String getSaveDir() {
		Date now = new Date();
		String date = DateFormatUtils.format(now, "yyyyMMdd");
		return "/prop/" + date + "/" + StrKit.getRandomUUID();
	}

	/**
	 * 保存图片、用户输入等信息
	 * @param bean
	 * @param project
	 * @return
	 */
	public Ret saveInfo(Picture bean, String project) {
		if(ObjKit.notEmpty(bean.getId())){
			return Ret.fail("msg", "实体错误");
		}
		if(ObjKit.empty(bean.getType()) || !ObjKit.isDigits(bean.getType())){
			return Ret.fail("msg", "请输入正确类型");
		}
		if(StrKit.isBlank(bean.getName())){
			return Ret.fail("msg", "请输入文件名");
		}
		if(StrKit.isBlank(bean.getPath())){
			return Ret.fail("msg", "请输入文件路径");
		}
		bean.setProject(project);
		bean.setCreateTime(new Date());
		if(bean.save()){
			return Ret.ok("msg", "保存成功");
		}
		return Ret.fail("msg", "保存失败");
	}

	public Ret saveInfoV2(Picture bean, String project) {
		if(ObjKit.notEmpty(bean.getId())){
			return Ret.fail("msg", "实体错误");
		}
		if(ObjKit.empty(bean.getType()) || !ObjKit.isDigits(bean.getType())){
			return Ret.fail("msg", "请输入正确类型");
		}
		if(StrKit.isBlank(bean.getName())){
			return Ret.fail("msg", "请输入文件名");
		}
		if(StrKit.isBlank(bean.getPath())){
			return Ret.fail("msg", "请输入文件路径");
		}
		JSONArray parseArr = parseJson(bean.getPath());
		if(ObjKit.empty(parseArr)){
			return Ret.fail("msg", "path 参数值格式有误");
		}
		for(int i = 0; i < parseArr.size(); i++){
			JSONObject jsonObject = (JSONObject) parseArr.get(i);
			Picture picDO = new Picture();
				picDO.setName(bean.getName());
				picDO.setType(bean.getType());
				picDO.setCustomName(bean.getCustomName());
				picDO.setSite(bean.getSite());
				picDO.setUploadTime(bean.getUploadTime());
				picDO.setProject(project);
				picDO.setCreateTime(new Date());
				picDO.setPath(jsonObject.getString("name"));
			picDO.save();
		}
		return Ret.ok("msg", "保存成功");
	}

	private JSONArray parseJson(String path) {
		try{
			return JsonKit.parse(path, JSONArray.class);
		}catch(Exception e){
			LogKit.warn("JSON转换失败, " + e);
			return null;
		}
	}
	
}
