package com.ysd.overview.front.project.event;

import com.jfinal.aop.Before;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.ysd.overview.common.auth.Menu;
import com.ysd.overview.common.auth.MenuType;
import com.ysd.overview.common.auth.UserPrincipal;
import com.ysd.overview.common.controller.BaseController;
import com.ysd.overview.common.kit.ObjKit;
import com.ysd.overview.common.model.Event;
import com.ysd.overview.common.model.Folder;
import com.ysd.overview.common.service.LfsService;
import com.ysd.overview.front.index.FolderService;

/**
 * 项目事件控制器
 */
public class EventController extends BaseController {

	EventService srv = EventService.me;
	
	/**
     * @api {get} /project/event?pageNo={pageNo}&pageSize={pageSize} 1.获取事件列表
     * @apiGroup event
     * @apiVersion 1.0.0
     * @apiPermission 项目.概况.进入
     * 
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
     * @apiSuccess (list) {Long} id 事件ID
     * @apiSuccess (list) {String} title 事件标题
     * @apiSuccess (list) {String} creator 创建人工号
     * @apiSuccess (list) {String} creatorName 创建人名称
     * @apiSuccess (list) {String} createAt 创建时间
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state": "ok",
	 *   "page":{
	 *   	"pageNumber": 1,
	 *   	"pageSize": 20,
     *   	"totalRow": 5,
     *   	"totalPage": 1,
     *   	"firstPage": true,
     *   	"lastPage": true,
	 *   	"list":[
	 *	 		{
	 *				"id": 1,
	 *				"title": "重要事件",
     *      		"creator": "admin",
     *      		"creatorName": "管理员",
     *      		"createAt": "2018-07-20 11:49:21"
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
		UserPrincipal user = getProjectUser();
		Page<Event> list = srv.paginate(getPaginator(), user);
		renderJson(Ret.ok("data", list));
	}
	
	/**
     * @api {get} /project/event/add 2.获取新增信息
     * @apiGroup event
     * @apiVersion 1.0.0
     * @apiPermission 概况.重要事件.新增
     * 
     * @apiSuccess {String} entity 实体名称
     * @apiSuccess {String} fileToken 文件Token
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "entity":"proEvent",
     *   "fileToken":"ba0e0047db774e8a92592df97c585c8b"
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	@Menu(value="概况.重要事件.新增", type=MenuType.BUTTON)
	public void add() {
		/*UserPrincipal user = getProjectUser();
		if (!user.isOwner()) {
			renderJson(Ret.fail("msg", "不允许新建，请联系负责人"));
			return;
		}*/
		String token = StrKit.getRandomUUID();
		renderJson(Ret.ok("fileToken", token)
				.set("entity", Folder.ENTITY_I_EVENT));
	}
	
	/**
     * @api {post} /project/event/save 3.新增项目事件
     * @apiGroup event
     * @apiVersion 1.0.0
     * @apiPermission 概况.重要事件.新增
     * 
     * @apiParam {String} title <code>必须参数</code> 事件标题
     * @apiParam {String} content <code>必须参数</code> 事件内容
     * @apiParam {String} fileToken <code>必须参数</code> 文件Token
     * 
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "msg":"事件创建成功"
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	@Before(EventValidator.class)
	@Menu(value="概况.重要事件.新增", type=MenuType.BUTTON)
	public void save(){
		Event bean = getBean(Event.class, "");
		String fileToken = getPara("fileToken");
		Ret ret = srv.save(bean, fileToken, getProjectUser());
		renderJson(ret);
	}
	
	/**
     * @api {get} /project/event/edit/{id} 4.获取编辑信息
     * @apiGroup event
     * @apiVersion 1.0.0
     * @apiPermission 概况.重要事件.编辑
     *
     * @apiParam {Long} id <code>必须参数</code> 事件ID
     * 
     * @apiSuccess {String} entity 实体名称
     * @apiSuccess {String} fileToken 文件Token
     * @apiSuccess {Object} bean 事件对象
     * @apiSuccess (bean) {Long} id 事件ID
     * @apiSuccess (bean) {String} title 事件标题
     * @apiSuccess (bean) {String} content 事件内容
     * @apiSuccess (bean) {String} creator 创建人工号
     * @apiSuccess (bean) {String} creatorName 创建人名称
     * @apiSuccess (bean) {String} createAt 创建时间
     * @apiSuccess (bean) {String} project 项目编码
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "entity":"proEvent",
     *   "fileToken":"ba0e0047db774e8a92592df97c585c8b",
     *   "bean":{
     *   	"id":1,
     *   	"title":"重要事件",
     *   	"content":"重要事件内容",
     *   	"creator":"admin",
     *   	"creatorName":"管理员",
     *   	"createAt":"2018-07-18 18:00",
     *   	"project":"289"
     *   }
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	@Menu(value="概况.重要事件.编辑", type=MenuType.BUTTON)
	public void edit() {
		/*UserPrincipal user = getProjectUser();
		if (!user.isOwner()) {
			renderJson(Ret.fail("msg", "不允许编辑，请联系负责人"));
			return;
		}*/
		Event bean = srv.findById(getParaToLong());
		if (ObjKit.empty(bean)) {
			renderJson(Ret.fail("msg", "事件不存在，请检查"));
			return;
		}
		String fileToken = srv.getFolderName(bean.getId());
		if (StrKit.isBlank(fileToken)) {
			fileToken = StrKit.getRandomUUID();
		}
		renderJson(Ret.ok("bean", bean)
				.set("fileToken", fileToken)
				.set("entity", Folder.ENTITY_I_EVENT));
	}
	
	/**
     * @api {post} /project/event/update 5.修改项目事件
     * @apiGroup event
     * @apiVersion 1.0.0
     * @apiPermission 概况.重要事件.编辑
     *
     * @apiParam {Long} id <code>必须参数</code> 事件ID
     * @apiParam {String} title <code>必须参数</code> 事件标题
     * @apiParam {String} content <code>必须参数</code> 事件内容
     * @apiParam {String} fileToken <code>必须参数</code> 文件Token
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "msg": "事件更新成功"
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	@Before(EventValidator.class)
	@Menu(value="概况.重要事件.编辑", type=MenuType.BUTTON)
	public void update(){
		Event bean = getBean(Event.class, "");
		Ret ret = srv.update(bean, getPara("fileToken"));
		renderJson(ret);
	}
	
	/**
     * @api {get} /project/event/delete/{id} 6.删除项目事件
     * @apiGroup event
     * @apiVersion 1.0.0
     * @apiPermission 概况.重要事件.删除
     *
     * @apiParam {Long} id <code>必须参数</code> 事件ID
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "msg":"事件删除成功"
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	@Before(Tx.class)
	@Menu(value="概况.重要事件.删除", type=MenuType.BUTTON)
	public void delete() {
		UserPrincipal user = getProjectUser();
		Ret ret = srv.delete(getParaToLong(), user);
		renderJson(ret);
	}
	
	/**
     * @api {get} /project/event/{id} 7.获取事件详情
     * @apiGroup event
     * @apiVersion 1.0.0
     * @apiPermission 项目.概况.进入
     *
     * @apiParam {Long} id <code>必须参数</code> 事件ID
     * 
     * @apiSuccess {Long} id 事件ID
     * @apiSuccess {String} title 事件标题
     * @apiSuccess {String} content 事件内容
     * @apiSuccess {String} creator 创建人工号
     * @apiSuccess {String} creatorName 创建人名称
     * @apiSuccess {String} createAt 创建时间
     * @apiSuccess {String} project 项目编码
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "bean":{
     *   	"id":1,
     *   	"title":"重要事件",
     *   	"content":"重要事件内容",
     *   	"creator":"admin",
     *   	"creatorName":"管理员",
     *   	"createAt":"2018-07-18 18:00",
     *   	"project":"289"
     *   }
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
//	@Menu(value="项目.概况.进入", type=MenuType.BUTTON)
	public void detail() {
		Event bean = srv.findById(getParaToLong());
		if (ObjKit.empty(bean)) {
			renderJson(Ret.fail("msg", "事件不存在，请检查"));
			return;
		}
		renderJson(Ret.ok("bean", bean));
	}
	
	/**
     * @api {get} /project/event/authFile/{token}_{id} 8.获取上传授权
     * @apiGroup event
     * @apiVersion 1.0.0
     * @apiPermission 概况.重要事件.上传附件
     * 
     * @apiParam {String} token <code>必须参数</code> 文件Token
     * @apiParam {Long} id 事件ID
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
	@Menu(value="概况.重要事件.上传附件", type=MenuType.BUTTON)
	public void authFile() {
		String token = getPara(0);
		if (StrKit.isBlank(token)) {
			renderJson(Ret.fail("msg", "上传标识不存在，请检查"));
			return;
		}
		UserPrincipal user = getProjectUser();
		String pathId = FolderService.me
				.getFilePath(token, Folder.ENTITY_P_EVENT, user);
		if (StrKit.isBlank(pathId)) {
			renderJson(Ret.fail("msg", "上传目录不存在，请联系管理员"));
			return;
		}
		srv.updateFolder(getParaToLong(1), token);
		Ret ret = LfsService.me.getSIDUID();
		renderJson(ret.set("pathId", pathId));
	}
	
	/**
     * @api {get} /project/event/delFile/{token}_{fileIds} 9.删除上传文件
     * @apiGroup event
     * @apiVersion 1.0.0
     * @apiPermission 概况.重要事件.删除附件
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
	@Menu(value="概况.重要事件.删除附件", type=MenuType.BUTTON)
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
	
}
