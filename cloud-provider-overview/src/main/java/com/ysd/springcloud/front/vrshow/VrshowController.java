package com.ysd.springcloud.front.vrshow;

import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.ysd.springcloud.common.auth.Menu;
import com.ysd.springcloud.common.auth.MenuType;
import com.ysd.springcloud.common.auth.UserPrincipal;
import com.ysd.springcloud.common.controller.BaseController;
import com.ysd.springcloud.common.kit.ObjKit;
import com.ysd.springcloud.common.model.Folder;
import com.ysd.springcloud.common.model.Vrshow;
import com.ysd.springcloud.common.service.LfsService;
import com.ysd.springcloud.front.index.FolderService;

/**
 * VR展示控制器
 */
public class VrshowController extends BaseController {

	VrshowService srv = VrshowService.me;
	
	@Menu("项目.VR展示.进入")
	public void index() {
		redirect(PropKit.get("page.vrshow"), true);
	}

	/**
     * @api {get} /vrshow/list?stage={stage} 获取VR展示列表
     * @apiGroup vrshow
     * @apiVersion 1.0.0
     * @apiPermission VR展示.{阶段名称}.查看
     *
     * @apiParam {String} stage <code>必须参数</code>阶段编码
     *
     * @apiSuccess {Long} id VR展示ID
     * @apiSuccess {String} name 模型名称
     * @apiSuccess {String} viewPath 全景地址
     * @apiSuccess {String} picPath 图片地址
     * @apiSuccess {String} creator 创建人工号
     * @apiSuccess {String} creatorName 创建人名称
     * @apiSuccess {String} createAt 创建时间
     *
     * @apiSuccessExample {json} Success-Response:
     * 
     * {
     *   "state": "ok",
	 *   "data":[
	 *	 	{
	 *			"id": 1,
	 *			"name": "模型名称",
	 *			"viewPath": "http://www.xxx.com",
     *       	"picPath": "http://www.xxx.com",
     *      	"creator": "admin",
     *      	"creatorName": "管理员",
     *      	"createAt": "2018-07-20 11:49:21"
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
	@Menu(value="VR展示.{c_optStage}.查看", type=MenuType.BUTTON)
	public void list() {
		Vrshow bean = getQueryBean(Vrshow.class);
		bean.setProject(getBusProject());
		List<Vrshow> list = srv.findByStage(bean);
		renderJson(Ret.ok("data", list));
	}
	
	/**
     * @api {get} /vrshow/add 获取新增信息
     * @apiGroup vrshow
     * @apiVersion 1.0.0
     * @apiPermission VR展示.{阶段名称}.新增
     * 
     * @apiSuccess {String} fileToken 文件Token
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "fileToken":"ba0e0047db774e8a92592df97c585c8b"
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	@Menu(value="VR展示.{c_optStage}.新增", type=MenuType.BUTTON)
	public void add() {
		renderJson(Ret.ok("fileToken", StrKit.getRandomUUID()));
	}
	
	/**
     * @api {post} /vrshow/save 新增VR展示
     * @apiGroup vrshow
     * @apiVersion 1.0.0
     * @apiPermission VR展示.{阶段名称}.新增
     * 
     * @apiParam {String} name <code>必须参数</code> 模型名称
     * @apiParam {String} stage <code>必须参数</code> 阶段编码
     * @apiParam {String} fileToken <code>必须参数</code> 文件Token
     * @apiParam {String} viewPath 全景地址
     * @apiParam {String} picPath 图片地址
     * @apiParam {String} remark 备注
     * 
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "msg":"VR展示创建成功"
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	@Before(VrshowValidator.class)
	@Menu(value="VR展示.{c_optStage}.新增", type=MenuType.BUTTON)
	public void save(){
		Vrshow bean = getBean(Vrshow.class, "");
		String fileToken = getPara("fileToken");
		Ret ret = srv.save(bean, fileToken, getProjectUser());
		renderJson(ret);
	}
	
	/**
     * @api {get} /vrshow/edit/{id} 获取编辑信息
     * @apiGroup vrshow
     * @apiVersion 1.0.0
     * @apiPermission VR展示.{阶段名称}.编辑
     *
     * @apiParam {Long} id <code>必须参数</code> VR展示ID
     * 
     * @apiSuccess {String} fileToken 文件Token
     * @apiSuccess {Object} bean VR展示对象
     * @apiSuccess (bean) {Long} id VR展示ID
     * @apiSuccess (bean) {String} name 模型名称
     * @apiSuccess (bean) {String} viewPath 全景地址
     * @apiSuccess (bean) {String} picPath 图片地址
     * @apiSuccess (bean) {String} remark 备注
     * @apiSuccess (bean) {String} project 项目编码
     * @apiSuccess (bean) {String} stage 阶段编码
     * @apiSuccess (bean) {String} creator 创建人工号
     * @apiSuccess (bean) {String} creatorName 创建人名称
     * @apiSuccess (bean) {String} createAt 创建时间
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "bean":{
     *   	"id":1,
     *   	"name":"模型名称",
     *   	"viewPath":"http://www.xxx.com",
     *   	"picPath":"http://www.xxx.com",
     *   	"remark":"test",
     *   	"project":"289",
     *   	"stage":"936",
     *   	"creator":"admin",
     *   	"creatorName":"管理员",
     *   	"createAt":"2018-07-18 18:00"
     *   },
     *   "fileToken":"ba0e0047db774e8a92592df97c585c8b"
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	@Menu(value="VR展示.{c_optStage}.编辑", type=MenuType.BUTTON)
	public void edit() {
		Vrshow bean = srv.findById(getParaToLong());
		if (ObjKit.empty(bean)) {
			renderJson(Ret.fail("msg", "VR展示不存在，请检查"));
			return;
		}
		String fileToken = FolderService.me.getFileName(
				Folder.ENTITY_I_VRSHOW, bean.getId().toString());
		if (StrKit.isBlank(fileToken)) {
			fileToken = StrKit.getRandomUUID();
		}
		renderJson(Ret.ok("bean", bean).set("fileToken", fileToken));
	}
	
	/**
     * @api {post} /vrshow/update 修改VR展示
     * @apiGroup vrshow
     * @apiVersion 1.0.0
     * @apiPermission VR展示.{阶段名称}.编辑
     *
     * @apiParam {Long} id <code>必须参数</code> VR展示ID
     * @apiParam {String} name <code>必须参数</code> 模型名称
     * @apiParam {String} fileToken <code>必须参数</code> 文件Token
     * @apiParam {String} viewPath 全景地址
     * @apiParam {String} picPath 图片地址
     * @apiParam {String} remark 备注
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "msg": "VR展示更新成功"
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	@Before(VrshowValidator.class)
	@Menu(value="VR展示.{c_optStage}.编辑", type=MenuType.BUTTON)
	public void update(){
		Vrshow bean = getBean(Vrshow.class, "");
		Ret ret = srv.update(bean, getPara("fileToken"));
		renderJson(ret);
	}
	

	
	/**
     * @api {get} /vrshow/delete/{id} 删除VR展示
     * @apiGroup vrshow
     * @apiVersion 1.0.0
     * @apiPermission VR展示.{阶段名称}.删除
     *
     * @apiParam {Long} id <code>必须参数</code> VR展示ID
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "msg":"VR展示删除成功"
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	@Before(Tx.class)
	@Menu(value="VR展示.{c_optStage}.删除", type=MenuType.BUTTON)
	public void delete() {
		UserPrincipal user = getProjectUser();
		Ret ret = srv.delete(getParaToLong(), user);
		renderJson(ret);
	}
	
	/**
     * @api {get} /vrshow/authFile/{token}_{id} 获取上传授权
     * @apiGroup vrshow
     * @apiVersion 1.0.0
     * @apiPermission VR展示.{阶段名称}.上传附件
     * 
     * @apiParam {String} token <code>必须参数</code> 文件Token
     * @apiParam {Long} id VR展示ID
     * 
     * @apiSuccess {String} pathId 目录ID
     * @apiSuccess {String} userId 用户ID
     * @apiSuccess {String} sessionId 会话Id
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "pathId":"40888",
     *   "userId":"8",
     *   "sessionId":"JSESSIONID=B7DE27145BE1BC8D35209AC7635FD018"
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	@Menu(value="VR展示.{c_optStage}.上传附件", type=MenuType.BUTTON)
	public void authFile() {
		String token = getPara(0);
		if (StrKit.isBlank(token)) {
			renderJson(Ret.fail("msg", "上传标识不存在，请检查"));
			return;
		}
		UserPrincipal user = getProjectUser();
		String pathId = FolderService.me
				.getFilePath(token, Folder.ENTITY_P_VRSHOW, user);
		if (StrKit.isBlank(pathId)) {
			renderJson(Ret.fail("msg", "上传目录不存在，请联系管理员"));
			return;
		}
		srv.updateFolder(getParaToLong(1), token);
		Ret ret = LfsService.me.getSIDUID();
		renderJson(ret.set("pathId", pathId));
	}
	
	/**
     * @api {get} /vrshow/delFile/{token}_{fileIds} 删除上传文件
     * @apiGroup vrshow
     * @apiVersion 1.0.0
     * @apiPermission VR展示.{阶段名称}.删除附件
     *
     * @apiParam {String} token <code>必须参数</code> 文件Token
     * @apiParam {String} fileIds <code>必须参数</code> 文件ID(多个用逗号分隔)
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "msg":"文件删除成功"
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	@Menu(value="VR展示.{c_optStage}.删除附件", type=MenuType.BUTTON)
	public void delFile() {
		String token = getPara(0);
		String fileIds = getPara(1);
		if (StrKit.isBlank(token) 
				|| StrKit.isBlank(fileIds)) {
			renderJson(Ret.fail("msg", "文件标识不存在，请检查"));
			return;
		}
		UserPrincipal user = getProjectUser();
		Ret ret = srv.delFiles(token, fileIds, user);
		renderJson(ret);
	}
	
	/**
     * @api {get} /vrshow/listV2?v2Stage={v2Stage} 获取VR展示列表_v2
     * @apiGroup vrshow
     * @apiVersion 2.0.0
     *
     * @apiSuccess {Long} id VR展示ID
     * @apiSuccess {String} name 模型名称
     * @apiSuccess {String} viewPath 全景地址
     * @apiSuccess {String} picPath 图片地址
     * @apiSuccess {String} creator 创建人工号
     * @apiSuccess {String} creatorName 创建人名称
     * @apiSuccess {String} createAt 创建时间
     *
     * @apiSuccessExample {json} Success-Response:
     * 
     * {
     *   "state": "ok",
	 *   "data":[
	 *	 	{
	 *			"id": 1,
	 *			"name": "模型名称",
	 *			"viewPath": "http://www.xxx.com",
     *       	"picPath": "http://www.xxx.com",
     *      	"creator": "admin",
     *      	"creatorName": "管理员",
     *      	"createAt": "2018-07-20 11:49:21"
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
//	@Menu(value="VR展示.{c_optStage}.查看", type=MenuType.BUTTON)
	public void listV2() {
		Vrshow bean = getQueryBean(Vrshow.class);
		bean.setProject(getBusProject());
		List<Vrshow> list = srv.findByV2(bean);
		renderJson(Ret.ok("data", list));
	}
	
	/**
     * @api {get} /vrshow/addV2 获取新增信息_v2
     * @apiGroup vrshow
     * @apiVersion 2.0.0
     * 
     * @apiSuccess {String} fileToken 文件Token
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "fileToken":"ba0e0047db774e8a92592df97c585c8b"
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
//	@Menu(value="VR展示.{c_optStage}.新增", type=MenuType.BUTTON)
	public void addV2() {
		renderJson(Ret.ok("fileToken", StrKit.getRandomUUID()));
	}
	
	/**
     * @api {post} /vrshow/saveV2 新增VR展示_v2
     * @apiGroup vrshow
     * @apiVersion 2.0.0
     * 
     * @apiParam {String} name <code>必须参数</code> 模型名称
     * @apiParam {String} fileToken <code>必须参数</code> 文件Token
     * @apiParam {String} viewPath 全景地址
     * @apiParam {String} picPath 图片地址
     * @apiParam {String} remark 备注
     * 
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "msg":"VR展示创建成功"
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	@Before(VrshowValidator.class)
//	@Menu(value="VR展示.{c_optStage}.新增", type=MenuType.BUTTON)
	public void saveV2(){
		Vrshow bean = getBean(Vrshow.class, "");
		String fileToken = getPara("fileToken");
		Ret ret = srv.save(bean, fileToken, getProjectUser());
		renderJson(ret);
	}
	
	/**
     * @api {get} /vrshow/editV2/{id} 获取编辑信息_v2
     * @apiGroup vrshow
     * @apiVersion 2.0.0
     *
     * @apiParam {Long} id <code>必须参数</code> VR展示ID
     * 
     * @apiSuccess {String} fileToken 文件Token
     * @apiSuccess {Object} bean VR展示对象
     * @apiSuccess (bean) {Long} id VR展示ID
     * @apiSuccess (bean) {String} name 模型名称
     * @apiSuccess (bean) {String} viewPath 全景地址
     * @apiSuccess (bean) {String} picPath 图片地址
     * @apiSuccess (bean) {String} remark 备注
     * @apiSuccess (bean) {String} project 项目编码
     * @apiSuccess (bean) {String} stage 阶段编码(廢棄)
     * @apiSuccess (bean) {String} creator 创建人工号
     * @apiSuccess (bean) {String} creatorName 创建人名称
     * @apiSuccess (bean) {String} createAt 创建时间
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "bean":{
     *   	"id":1,
     *   	"name":"模型名称",
     *   	"viewPath":"http://www.xxx.com",
     *   	"picPath":"http://www.xxx.com",
     *   	"remark":"test",
     *   	"project":"289",
     *   	"stage":"936",
     *   	"creator":"admin",
     *   	"creatorName":"管理员",
     *   	"createAt":"2018-07-18 18:00"
     *   },
     *   "fileToken":"ba0e0047db774e8a92592df97c585c8b"
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
//	@Menu(value="VR展示.{c_optStage}.编辑", type=MenuType.BUTTON)
	public void editV2() {
		Vrshow bean = srv.findById(getParaToLong());
		if (ObjKit.empty(bean)) {
			renderJson(Ret.fail("msg", "VR展示不存在，请检查"));
			return;
		}
		String fileToken = FolderService.me.getFileName(
				Folder.ENTITY_I_VRSHOW, bean.getId().toString());
		if (StrKit.isBlank(fileToken)) {
			fileToken = StrKit.getRandomUUID();
		}
		renderJson(Ret.ok("bean", bean).set("fileToken", fileToken));
	}
	
	/**
     * @api {post} /vrshow/updateV2 修改VR展示_v2
     * @apiGroup vrshow
     * @apiVersion 2.0.0
     *
     * @apiParam {Long} id <code>必须参数</code> VR展示ID
     * @apiParam {String} name <code>必须参数</code> 模型名称
     * @apiParam {String} fileToken <code>必须参数</code> 文件Token
     * @apiParam {String} viewPath 全景地址
     * @apiParam {String} picPath 图片地址
     * @apiParam {String} remark 备注
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "msg": "VR展示更新成功"
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	@Before(VrshowValidator.class)
//	@Menu(value="VR展示.{c_optStage}.编辑", type=MenuType.BUTTON)
	public void updateV2(){
		Vrshow bean = getBean(Vrshow.class, "");
		Ret ret = srv.update(bean, getPara("fileToken"));
		renderJson(ret);
	}
	
	/**
     * @api {get} /vrshow/deleteV2/{id} 删除VR展示_v2
     * @apiGroup vrshow
     * @apiVersion 2.0.0
     *
     * @apiParam {Long} id <code>必须参数</code> VR展示ID
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "msg":"VR展示删除成功"
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	@Before(Tx.class)
//	@Menu(value="VR展示.{c_optStage}.删除", type=MenuType.BUTTON)
	public void deleteV2() {
		UserPrincipal user = getProjectUser();
		Ret ret = srv.delete(getParaToLong(), user);
		renderJson(ret);
	}
	
	/**
     * @api {get} /vrshow/authFileV2/{token}_{id} 获取上传授权_v2
     * @apiGroup vrshow
     * @apiVersion 2.0.0
     * 
     * @apiParam {String} token <code>必须参数</code> 文件Token
     * @apiParam {Long} id VR展示ID
     * 
     * @apiSuccess {String} pathId 目录ID
     * @apiSuccess {String} userId 用户ID
     * @apiSuccess {String} sessionId 会话Id
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "pathId":"40888",
     *   "userId":"8",
     *   "sessionId":"JSESSIONID=B7DE27145BE1BC8D35209AC7635FD018"
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
//	@Menu(value="VR展示.{c_optStage}.上传附件", type=MenuType.BUTTON)
	public void authFileV2() {
		String token = getPara(0);
		if (StrKit.isBlank(token)) {
			renderJson(Ret.fail("msg", "上传标识不存在，请检查"));
			return;
		}
		UserPrincipal user = getProjectUser();
		String pathId = FolderService.me
				.getFilePath(token, Folder.ENTITY_P_VRSHOW, user);
		if (StrKit.isBlank(pathId)) {
			renderJson(Ret.fail("msg", "上传目录不存在，请联系管理员"));
			return;
		}
		srv.updateFolder(getParaToLong(1), token);
		Ret ret = LfsService.me.getSIDUID();
		renderJson(ret.set("pathId", pathId));
	}
	
	/**
     * @api {get} /vrshow/delFileV2/{token}_{fileIds} 删除上传文件_v2
     * @apiGroup vrshow
     * @apiVersion 2.0.0
     *
     * @apiParam {String} token <code>必须参数</code> 文件Token
     * @apiParam {String} fileIds <code>必须参数</code> 文件ID(多个用逗号分隔)
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "msg":"文件删除成功"
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
//	@Menu(value="VR展示.{c_optStage}.删除附件", type=MenuType.BUTTON)
	public void delFileV2() {
		String token = getPara(0);
		String fileIds = getPara(1);
		if (StrKit.isBlank(token) 
				|| StrKit.isBlank(fileIds)) {
			renderJson(Ret.fail("msg", "文件标识不存在，请检查"));
			return;
		}
		UserPrincipal user = getProjectUser();
		Ret ret = srv.delFiles(token, fileIds, user);
		renderJson(ret);
	}
	
}
