package com.ysd.springcloud.front.upload;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import com.jfinal.kit.LogKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.upload.UploadFile;
import com.ysd.springcloud.common.kit.ImgKit;
import com.ysd.springcloud.common.kit.ObjKit;

/**
 * 上传服务
 */
public class UploadService {

	public static final UploadService me = new UploadService();
	
	/**
	 * ueditor上传图片
	 */
	public Ret uploadImage(UploadFile uf) {
		if (ObjKit.empty(uf) 
				|| ObjKit.empty(uf.getFile())) {
			return Ret.create("state", "上传图片对象不存在，请检查");
		}
		try {
			if (ImgKit.notImage(uf.getFileName())) {
				return Ret.create("state", "上传图片不支持，只支持类型：jpg、jpeg、png、bmp");
			}
			
			Date now = new Date();
			String savePath = "/event/" + DateFormatUtils.format(now, "yyyyMMdd");
			String fileName = DateFormatUtils.format(now, "HHmmss") + ImgKit.getRandomName(6) + ".jpg";
			ImgKit.zoom(uf.getFile(), getSaveDir()+savePath, fileName);
			
			return Ret.create("state", "SUCCESS")
					.set("url", savePath+"/"+fileName)
					.set("title", fileName)
					.set("original", uf.getOriginalFileName());
		} catch (Exception e) {
			LogKit.error("缩放图片失败", e);
			return Ret.create("state", "上传图片保存失败，请重试");
		} finally {
			uf.getFile().delete();
		}
	}
	
	public String getImageTempDir() {
		return "/editor/" + StrKit.getRandomUUID();
	}
	
	public String getSaveDir() {
		return PropKit.get("system.picPath");
	}
	
	/**
	 * 保存概况图片
	 */
	public Ret savePic(UploadFile uf, String resize, String savePath) {
		if (ObjKit.empty(uf)) {
			return Ret.fail("msg", "上传图片对象不存在，请检查");
		}
		try {
			if (ImgKit.notImage(uf.getFileName())) {
				return Ret.fail("msg", "上传图片不支持，只支持类型：jpg、jpeg、png、bmp");
			}
			String picPath = savePath + "/";
			doResize(uf.getFile(), resize, picPath);
			return Ret.ok("data", picPath);
		}
		catch (Exception e) {
			LogKit.error("缩放图片失败", e);
			return Ret.fail("msg", "上传图片保存失败，请重试");
		} finally {
			uf.getFile().delete();
		}
	}
	
	private void doResize(File srcFile, String resize, String savePath) {
		savePath = getSaveDir() + savePath;
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
	
}
