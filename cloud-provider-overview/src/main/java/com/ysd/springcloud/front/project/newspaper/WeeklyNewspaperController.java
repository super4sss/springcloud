package com.ysd.springcloud.front.project.newspaper;

import com.jfinal.aop.Before;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.ysd.springcloud.common.auth.Menu;
import com.ysd.springcloud.common.auth.MenuType;
import com.ysd.springcloud.common.auth.UserPrincipal;
import com.ysd.springcloud.common.controller.BaseController;
import com.ysd.springcloud.common.kit.ObjKit;
import com.ysd.springcloud.common.model.Folder;
import com.ysd.springcloud.common.model.WeeklyNewspaper;
import com.ysd.springcloud.common.page.Paginator;
import com.ysd.springcloud.common.service.LfsService;
import com.ysd.springcloud.front.index.FolderService;

/*
 * File WeeklyNewspaperController.java
 * ------------------------------------------
 * 周报管理action层
 * 接收并响应请求。
 */
public class WeeklyNewspaperController extends BaseController{
	WeeklyNewspaperService service = WeeklyNewspaperService.paperService;
	
	/**
     * @api {get} /project/newspaper?pageNo={pageNo}&pageSize={pageSize} 1.获取周报列表
     * @apiGroup newspaper
     * @apiVersion 1.0.0
     * @apiPermission 项目.概况.进入
     *
     * @apiParam {Long} pageNo 页码
     * @apiParam {Long} pageSize 页显示数
     *
     * @apiSuccess {Long} id 周报ID
     * @apiSuccess {String} title 周报标题
     * @apiSuccess {String} creatorNum 创建人工号
     * @apiSuccess {String} creator 创建人名称
     * @apiSuccess {String} createAt 创建时间
     * 
     * @apiSuccess {Integer} pageNumber 页码
     * @apiSuccess {Integer} pageSize 页显示数
     * @apiSuccess {Integer} totalPage 总页
     * @apiSuccess {Integer} totalRow 总记录
     * @apiSuccess {Boolean} firstPage 首页
     * @apiSuccess {Boolean} lastPage 尾页
     *
     * @apiSuccessExample {json} Success-Response:
     * 
	 *{
	 *	"data": {
	 *			"totalRow": 7,
	 *			"pageNumber": 1,
	 *			"firstPage": true,
	 *			"lastPage": false,
	 *			"totalPage": 7,
	 *			"pageSize": 1,
	 *			"list": [
	 *				{
	 *					"creator": "郑希彬",
	 *					"id": 8,
	 *					"title": "新的数据",
	 *					"creatorNum": "678",
	 *					"content": "<p>新的数据</p>",
	 *					"createAt": "2019-05-15"
	 *				}
	 *			]
	 *		},
	 *	"state": "ok"
	 *}
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
//	@Menu("项目.概况.进入")
	public void index(){
		String project = getBusProject();
		Paginator paginator = getPaginator();
		Ret ret = service.getPaperByProject(project,paginator);
		renderJson(ret);
	}
	
	/**
     * @api {get} /project/newspaper/add 2.获取新增信息
     * @apiGroup newspaper
     * @apiVersion 1.0.0
     * @apiPermission 概况.周报.新增
     * 
     * @apiSuccess {String} entity 实体名称
     * @apiSuccess {String} fileToken 文件Token
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "entity":"proPaper",
     *   "fileToken":"7501a1cd4dd840628d6264664e35f362"
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	@Menu(value="概况.周报.新增", type=MenuType.BUTTON)
	public void add(){
		/*UserPrincipal bean = getProjectUser();
		if(!bean.isOwner()){
			renderJson(Ret.fail("msg", "操作处理失败，请联系管理员"));
			return ;
		}*/
		String token = StrKit.getRandomUUID();
		renderJson(Ret.ok("fileToken", token)
				.set("entity", Folder.ENTITY_I_PAPER));
	}
	
	/**
     * @api {post} /project/newspaper/create 3.新增项目周报
     * @apiGroup newspaper
     * @apiVersion 1.0.0
     * @apiPermission 概况.周报.新增
     * 
     * @apiParam {String} title <code>必须参数</code> 周报标题
     * @apiParam {String} content <code>必须参数</code> 周报内容
     * @apiParam {String} fileToken <code>必须参数</code> 文件Token
     * 
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "msg":"周报新增成功"
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	@Before(WeeklyNewspaperValidator.class)
	@Menu(value="概况.周报.新增", type=MenuType.BUTTON)
	public void create(){
		WeeklyNewspaper bean = getBean(WeeklyNewspaper.class,"");
		String fileToken = getPara("fileToken");
		Ret ret = service.save(bean,fileToken,getProjectUser());
		renderJson(ret);
	}
	
	/**
     * @api {get} /project/newspaper/editInfo/{id} 4.获取编辑信息
     * @apiGroup newspaper
     * @apiVersion 1.0.0
     * @apiPermission 概况.周报.编辑
     *
     * @apiParam {Long} id <code>必须参数</code> 周报ID
     * 
     * @apiSuccess {String} entity 实体名称
     * @apiSuccess {String} fileToken 文件Token
     * @apiSuccess {Object} bean 周报对象
     * @apiSuccess (bean) {Long} id 周报ID
     * @apiSuccess (bean) {String} title 周报标题
     * @apiSuccess (bean) {String} content 周报内容
     * @apiSuccess (bean) {String} creatorNum 创建人工号
     * @apiSuccess (bean) {String} creator 创建人名称
     * @apiSuccess (bean) {String} createAt 创建时间
     * @apiSuccess (bean) {String} project 项目编码
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "entity":"proLetter",
     *   "fileToken":"8d6adf9684cc4690a6f1f2858b6ab36d",
     *   "bean":{
     *   	"id":1,
     *   	"title":"關於xxx的一則報告",
     *   	"content":"以下乃報告的内容：。。。",
     *   	"creator":"罗远峰",
     *   	"creatorNum":"411",
     *   	"createAt":"2018-07-18 18:00",
     *   	"project":"2"
     *   }
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	@Menu(value="概况.周报.编辑", type=MenuType.BUTTON)
	public void editInfo(){
		Long paperId = getParaToLong();
		/*UserPrincipal user = getProjectUser();
		if(!user.isOwner()){
			renderJson(Ret.fail("msg", "当前用户不具有操作权限"));
			return ;
		}*/
		WeeklyNewspaper bean = service.findById(paperId);
		if(ObjKit.empty(bean)){
			renderJson(Ret.fail("msg", "周报实体不存在，请检查"));
			return ;
		}
		String fileToken = service.getFolderName(bean.getId());
		if(StrKit.isBlank(fileToken)){
			fileToken = StrKit.getRandomUUID();
		}
		renderJson(Ret.ok("bean", bean)
				.set("fileToken", fileToken)
				.set("entity", Folder.ENTITY_I_PAPER));	
	}
	
	/**
     * @api {post} /project/newspaper/update 5.修改项目周报
     * @apiGroup newspaper
     * @apiVersion 1.0.0
     * @apiPermission 概况.周报.编辑
     *
     * @apiParam {Long} id <code>必须参数</code> 周报ID
     * @apiParam {String} title <code>必须参数</code> 周报标题
     * @apiParam {String} content <code>必须参数</code> 周报内容
     * @apiParam {String} fileToken <code>必须参数</code> 文件Token
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "msg": "周报更新成功"
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	@Before(WeeklyNewspaperValidator.class)
	@Menu(value="概况.周报.编辑", type=MenuType.BUTTON)
	public void update(){
		WeeklyNewspaper bean = getBean(WeeklyNewspaper.class,"");
		Ret ret = service.update(bean,getPara("fileToken"));
		renderJson(ret);
	}
	
	/**
     * @api {get} /project/newspaper/delete/{id} 6.删除项目周报
     * @apiGroup newspaper
     * @apiVersion 1.0.0
     * @apiPermission 概况.周报.删除
     *
     * @apiParam {Long} id <code>必须参数</code> 周报ID
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "msg":"删除成功"
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	@Before(Tx.class)
	@Menu(value="概况.周报.删除", type=MenuType.BUTTON)
	public void delete(){
		Long id = getParaToLong();
		UserPrincipal user = getProjectUser();
		Ret ret = service.delete(id,user);
		renderJson(ret);
	}
	
	/**
     * @api {get} /project/newspaper/details/{id} 7.获取周报详情
     * @apiGroup newspaper
     * @apiVersion 1.0.0
     * @apiPermission 项目.概况.进入
     *
     * @apiParam {Long} id <code>必须参数</code> 周报ID
     * 
     * @apiSuccess {Long} id 周报ID
     * @apiSuccess {String} title 周报标题
     * @apiSuccess {String} content 周报内容
     * @apiSuccess {String} creator 创建人工号
     * @apiSuccess {String} creatorNum 创建人名称
     * @apiSuccess {String} createAt 创建时间
     * @apiSuccess {String} project 项目编码
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "bean":{
     *   	"id":1,
     *   	"title":"關於xxx的一則報告",
     *   	"content":"以下乃報告的内容：。。。",
     *   	"creator":"罗远峰",
     *   	"creatorNum":"411",
     *   	"createAt":"2019-03-12 14:12:15",
     *   	"project":"2"
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
	public void details(){
		Long paperId = getParaToLong();
		Ret ret = service.getNewspaperDetailsById(paperId);
		renderJson(ret);
	}
	
	/**
     * @api {get} /project/newspaper/authFile/{token}_{id} 8.获取上传授权
     * @apiGroup newspaper
     * @apiVersion 1.0.0
     * @apiPermission 概况.周报.上传附件
     * 
     * @apiParam {String} token <code>必须参数</code> 文件Token
     * @apiParam {Long} id 周报ID
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
	@Menu(value="概况.周报.上传附件", type=MenuType.BUTTON)
	public void authFile() {
		String token = getPara(0);
		if (StrKit.isBlank(token)) {
			renderJson(Ret.fail("msg", "上传标识不存在，请检查"));
			return;
		}
		UserPrincipal user = getProjectUser();
		String pathId = FolderService.me
				.getFilePath(token, Folder.ENTITY_P_PAPER, user);
		if (StrKit.isBlank(pathId)) {
			renderJson(Ret.fail("msg", "上传目录不存在，请联系管理员"));
			return;
		}
		service.updateFolder(getParaToLong(1), token);
		Ret ret = LfsService.me.getSIDUID();
		renderJson(ret.set("pathId", pathId));
	}
	
	/**
     * @api {get} /project/newspaper/delFile/{token}_{fileIds} 9.删除上传文件
     * @apiGroup newspaper
     * @apiVersion 1.0.0
     * @apiPermission 概况.周报.删除附件
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
	@Menu(value="概况.周报.删除附件", type=MenuType.BUTTON)
	public void delFile() {
		String token = getPara(0);
		String fileIds = getPara(1);
		if (StrKit.isBlank(token) 
				|| StrKit.isBlank(fileIds)) {
			renderJson(Ret.fail("msg", "文件标识不存在，请检查"));
			return;
		}
		UserPrincipal user = getProjectUser();
		Ret ret = service.delFiles(token, fileIds, user);
		renderJson(ret);
	}
	
}
