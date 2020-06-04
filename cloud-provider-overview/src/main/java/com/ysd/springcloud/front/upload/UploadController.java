package com.ysd.springcloud.front.upload;

import com.jfinal.kit.Kv;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.render.JsonRender;
import com.jfinal.upload.UploadFile;
import com.ysd.springcloud.common.controller.BaseController;

/**
 * 上传控制器
 */
public class UploadController extends BaseController {

	UploadService srv = UploadService.me;
	
	/**
     * @api {get} /upload/config 1.获取ueditor配置
     * @apiGroup upload
     * @apiVersion 1.0.0
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "imageActionName":"uploadimage",
     *   "imageFieldName":"upfile",
     *   "imageMaxSize":2097152,
     *   "imageAllowFiles":[".png", ".jpg", ".jpeg", ".bmp"]
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	public void config() {
		Kv config = Kv.create()
				.set("imageActionName", "uploadimage")
				.set("imageFieldName", "upfile")
				.set("imageMaxSize", 2097152)
				.set("imageAllowFiles", new String[]{".jpg", ".jpeg", ".png", ".bmp"})
				.set("imageCompressEnable", false)
				.set("imageCompressBorder", 1600)
				.set("imageInsertAlign", "none")
				.set("imageUrlPrefix", PropKit.get("system.picDomain"))
				.set("imagePathFormat", "/event/{yyyy}{mm}{dd}/{hh}{ii}{ss}{rand:6}");
		
		String callback = getPara("callback");
		if (StrKit.isBlank(callback)) {
			renderJson(config);
			return;
		}
		renderJson(callback + "(" + config.toJson() + ")");
	}
	
	/**
     * @api {post} /upload/image 2.上传ueditor图片
     * @apiGroup upload
     * @apiVersion 1.0.0
     *
     * @apiParam {Multipart} upfile <code>必须参数</code> 图片文件
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"SUCCESS",
     *   "url":"/event/20180824/115000abcdef.jpg",
     *   "title":"115000abcdef.jpg",
     *   "original":"test.jpg"
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"上传图片失败，请联系管理员"
     * }
     */
	public void image() {
		UploadFile uf = null;
		try {
			uf = getFile("upfile", srv.getImageTempDir());
			Ret ret = srv.uploadImage(uf);
			render(new JsonRender(ret).forIE());
		} catch (Exception e) {
			LogKit.error("上传图片出现异常", e);
			if (uf != null) {
				uf.getFile().delete();
			}
			renderJson("state", "上传图片失败，请联系管理员");
		}
	}
	
	/**
     * @api {post} /upload/picture/{type} 3.上传缩略图
     * @apiGroup upload
     * @apiVersion 1.0.0
     *
     * @apiParam {String} type <code>必须参数</code> 应用类型
     * @apiParam {Multipart} file <code>必须参数</code> 图片文件
     * @apiParam {String} resize 缩放大小(240:120,875:380)
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "data":"/type/abcdefgh/1.jpg"
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	public void picture() {
		String type = getPara();
		if (StrKit.isBlank(type)) {
			renderJson(Ret.fail("msg", "应用类型不能为空，请检查"));
			return;
		}
		String uploadPath = "/"+type+"/"+StrKit.getRandomUUID();
		
		UploadFile uf = null;
		try {
			uf = getFile("file", uploadPath, 50*1024*1024);
			String resize = getPara("resize");
			Ret ret = srv.savePic(uf, resize, uploadPath);
			renderJson(ret);
		} catch (Exception e) {
			LogKit.error("上传图片出现异常", e);
			if (uf != null) {
				uf.getFile().delete();
			}
			renderJson(Ret.fail("msg", "上传图片失败，请联系管理员"));
		}
	}
	
}
