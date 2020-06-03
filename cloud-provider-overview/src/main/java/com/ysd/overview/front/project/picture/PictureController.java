package com.ysd.overview.front.project.picture;

import java.util.Date;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.template.stat.ast.For;
import com.jfinal.upload.UploadFile;
import com.ysd.overview.common.auth.Menu;
import com.ysd.overview.common.auth.MenuType;
import com.ysd.overview.common.auth.UserPrincipal;
import com.ysd.overview.common.controller.BaseController;
import com.ysd.overview.common.model.Picture;

/**
 * 项目图片控制器
 */
public class PictureController extends BaseController {

	PictureService srv = PictureService.me;
	
	/**
     * @api {get} /project/picture?type={type} 1.获取图片分页
     * @apiGroup picture
     * @apiVersion 1.0.0
     * @apiPermission 项目.概况.进入
     * 
     * @apiParam {Integer} type <code>必须参数</code> 图片类别(1:现场航拍,2:效果图,3:项目照片)
     * @apiParam {Integer} pageNo 第几页(默认:1)
     * @apiParam {Integer} pageSize 页记录数(默认:20)
     *
     * @apiSuccess {Integer} pageNumber 第几页
     * @apiSuccess {Integer} pageSize 页记录数
     * @apiSuccess {Integer} totalRow 总记录数
     * @apiSuccess {Integer} totalPage 总页数
     * @apiSuccess {Boolean} firstPage 是否首页
     * @apiSuccess {Boolean} lastPage 是否末页
     * @apiSuccess {List} list 图片列表
     * @apiSuccess (list) {Long} id 图片ID
     * @apiSuccess (list) {String} name 图片名称
     * @apiSuccess (list) {String} path 图片路径
     * @apiSuccess (list) {String} customName 事件名称
     * @apiSuccess (list) {String} uploadTime 时间
     * @apiSuccess (list) {String} site 地点
     *
     * @apiSuccessExample {json} Success-Response:
     * 
     * {
     *   "state": "ok",
     *   "page":{
     *   	"pageNumber": 1,
     *   	"pageSize": 9,
     *   	"totalRow": 2,
     *   	"totalPage": 1,
     *   	"firstPage": true,
     *   	"lastPage": false,
     *   	"list":[
	 *	 		{
	 *				"id": 1,
	 *				"name": "DJI_0124.jpg",
     *      		"path": "/prop/91adc1202ba74ab59235518dc73a02a1/",
     *      		"customName": "666666666",
     *             "id": 682,
     *             "uploadTime": "2019-08-19"
     *   		}
	 *   	]
     *   }
	 * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
//	@Menu("项目.概况.进入")
	public void index() {
		Picture form = getQueryBean(Picture.class);
		form.setProject(getBusProject());
		Page<Picture> page = srv.paginate(getPaginator(), form);
		renderJson(Ret.ok("page", page));
	}
	
	/**
     * @api {get} /project/picture/list/{type} 2.获取图片列表
     * @apiGroup picture
     * @apiVersion 1.0.0
     * @apiPermission 项目.概况.进入
     * 
     * @apiParam {Integer} type <code>必须参数</code> 图片类别(1:现场航拍,2:效果图,3:项目照片)
     *
     * @apiSuccess {Long} id 图片ID
     * @apiSuccess {String} name 图片名称
     * @apiSuccess {String} path 图片路径
     * @apiSuccess {String} customName 事件名称
     * @apiSuccess {String} uploadTime 时间
     * @apiSuccess {String} site 地点
     *
     * @apiSuccessExample {json} Success-Response:
     * 
     * {
     *   "state": "ok",
     *   "data":[
	 *	 	{
	 *			"id": 1,
	 *			"name": "DJI_0124.jpg",
     *      	"path": "/prop/91adc1202ba74ab59235518dc73a02a1/",
     *          "customName": "666666666",
     *          "id": 682,
     *          "uploadTime": "2019-08-19"
     *   	}
	 *   ]
	 * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
//	@Menu("项目.概况.进入")
	public void list() {
		UserPrincipal user = getProjectUser();
		List<Picture> list = srv.findByType(getParaToInt(), user);
		renderJson(Ret.ok("data", list));
	}
	
	/**
     * @api {post} /project/picture/save 3.添加图片
     * @apiGroup picture
     * @apiVersion 1.0.0
     * @apiPermission 概况.{type}.新增
     *
     * @apiParam {Multipart} file <code>必须参数</code> 图片文件
     * @apiParam {Integer} type <code>必须参数</code> 图片类别(1:现场航拍,2:效果图,3:项目照片)
     * @apiParam {String} resize 缩放大小(240:120,875:380)
     * @apiParam {String} customName 事件名称
     * @apiParam {String} uploadTime 时间
     * @apiParam {String} site 地点
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "msg":"图片保存成功"
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	@Menu(value="概况.{type}.新增", type=MenuType.BUTTON, replace={"1:现场航拍","2:效果图","3:项目图片"})
	public void save() {
		UserPrincipal user = getProjectUser();
		/*if (!user.isOwner()) {
			renderJson(Ret.fail("msg", "不允许添加，请联系负责人"));
			return;
		}*/
		String uploadPath = srv.getSaveDir();
		
		UploadFile uf = null;
		try {
			uf = getFile("file", uploadPath, 50*1024*1024);
			Picture form = new Picture()
					.set("path", uploadPath+"/")
					.set("type", getParaToInt("type"))
					.set("project", user.getProject())
					.set("createTime",new Date())
					.set("customName",getPara("customName"))
					.set("uploadTime", getParaToDate("uploadTime"))
					.set("site", getPara("site"));
			Ret ret = srv.save(uf, form, getPara("resize"));
			renderJson(ret);
		} catch (Exception e) {
			LogKit.error("上传图片出现异常", e);
			if (uf != null) {
				uf.getFile().delete();
			}
			renderJson(Ret.fail("msg", "上传图片失败，请联系管理员"));
		}
	}
	
	/**
     * @api {get} /project/picture/delete?type={type}&id={id} 4.删除图片
     * @apiGroup picture
     * @apiVersion 1.0.0
     * @apiPermission 概况.{type}.删除
     *
     * @apiParam {Integer} type <code>必须参数</code> 图片类别(1:现场航拍,2:效果图,3:项目照片)
     * @apiParam {Stirng} id <code>必须参数</code> 图片ID(多个用英文逗号分隔)
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "msg":"图片删除成功"
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	@Menu(value="概况.{type}.删除", type=MenuType.BUTTON, replace={"1:现场航拍","2:效果图","3:项目图片"})
	public void delete() {
		UserPrincipal user = getProjectUser();
		/*if (!user.isOwner()) {
			renderJson(Ret.fail("msg", "不允许删除，请联系负责人"));
			return;
		}*/
		renderJson(srv.delete(getPara("id"), user));
	}
	
	
	/**
     * @api {post} /project/picture/uploadFile 5.上传文件
     * @apiGroup picture
     * @apiVersion 1.0.0
     *
     * @apiParam {Multipart} file <code>必须参数</code> 图片文件
     * @apiParam {Integer} type <code>必须参数</code> 图片类别(1:现场航拍,2:效果图,3:项目照片)
     * @apiParam {String} resize 缩放大小(240:120,875:380)
     * 
     * @apiSuccess {String} path 路径
     * @apiSuccess {String} name 图片名称
     * @apiSuccess {Number} type 图片类别(1:现场航拍,2:效果图,3:项目照片)
     *
     * @apiSuccessExample {json} Success-Response:
     *{
	 *    "data": {
	 *        "path": "/prop/20190819/203e030a45c3471281d473005b845e44/",
	 *        "name": "20190227011111507",
	 *        "type": 1
	 *    },
	 *    "state": "ok"
	 *}
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	public void uploadFile(){
		String uploadPath = srv.getSaveDir();
		UploadFile uf = null;
		try {
			uf = getFile("file", uploadPath, 50*1024*1024);
			Picture form = new Picture()
					.set("path", uploadPath+"/")
					.set("type", getParaToInt("type"));
			Ret ret = srv.saveFile(uf, form, getPara("resize"));
			renderJson(ret);
		} catch (Exception e) {
			LogKit.error("上传图片出现异常", e);
			if (uf != null) {
				uf.getFile().delete();
			}
			renderJson(Ret.fail("msg", "上传图片失败，请联系管理员"));
		}
	}
	
	/**
     * @api {post} /project/picture/saveInfo 6.保存图片信息
     * @apiGroup picture
     * @apiVersion 1.0.0
     * @apiPermission 概况.{type}.新增
     *
     * @apiParam {String} name <code>必须参数</code> 图片名称
     * @apiParam {Integer} type <code>必须参数</code> 图片类别(1:现场航拍,2:效果图,3:项目照片)
     * @apiParam {String} path <code>必须参数</code> 图片路径
     * @apiParam {String} customName 事件名称
     * @apiParam {String} uploadTime 时间
     * @apiParam {String} site 地点
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "msg":"图片保存成功"
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	@Menu(value="概况.{type}.新增", type=MenuType.BUTTON, replace={"1:现场航拍","2:效果图","3:项目图片"})
	public void saveInfo(){
		Picture bean = getBean(Picture.class,"");
		Ret ret = srv.saveInfo(bean,getBusProject());
		renderJson(ret);
	}
	
	/**
     * @api {post} /project/picture/save_info_v2 7.保存图片信息_v2
     * @apiGroup picture
     * @apiVersion 2.0.0
     * @apiPermission 概况.{type}.新增
     *
     * @apiParam {String} name <code>必须参数</code> 图片名称
     * @apiParam {Integer} type <code>必须参数</code> 图片类别(1:现场航拍,2:效果图,3:项目照片)
     * @apiParam {JsonArray} path <code>必须参数</code> 图片路径 例：[{"name": "xxx"}]
     * @apiParam {String} customName 事件名称
     * @apiParam {String} uploadTime 时间
     * @apiParam {String} site 地点
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "msg":"图片保存成功"
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	@Before(Tx.class)
	@Menu(value="概况.{type}.新增", type=MenuType.BUTTON, replace={"1:现场航拍","2:效果图","3:项目图片"})
	public void save_info_v2(){
		Picture bean = getBean(Picture.class,"");
		Ret ret = srv.saveInfoV2(bean,getBusProject());
		renderJson(ret);
	}
	
}
