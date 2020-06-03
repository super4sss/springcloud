package com.ysd.overview.front.project.letter;

import java.util.List;

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
import com.ysd.overview.common.model.Folder;
import com.ysd.overview.common.model.Letter;
import com.ysd.overview.common.page.Paginator;
import com.ysd.overview.common.service.LfsService;
import com.ysd.overview.front.index.FolderService;

/**
 * 项目函件控制器
 * @author Administrator
 * @date 2019年3月11日
 *
 */
public class LetterController extends BaseController{

	LetterService srv = LetterService.me;
	
	/**
     * @api {get} /project/letter/getLettersByProject?pageNo={pageNo}&pageSize={pageSize} 1.获取函件列表
     * @apiGroup letter
     * @apiVersion 1.0.0
     * @apiPermission 项目.概况.进入
     *
     * @apiParam {Long} pageNo 页码
     * @apiParam {Long} pageSize 页显示数
     *
     * @apiSuccess {Long} id 函件ID
     * @apiSuccess {String} title 函件标题
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
	 *		"totalRow": 10,
	 *		"pageNumber": 1,
	 *		"firstPage": true,
	 *		"lastPage": false,
	 *		"totalPage": 10,
	 *		"pageSize": 1,
	 *		"list": [
	 *			{
	 *				"creator": "郑希彬",
	 *				"id": 11,
	 *				"title": "",
	 *				"creatorNum": "678",
	 *				"createAt": "2019-05-17"
	 *			}
	 *		]
	 *	},
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
	public void getLettersByProject(){
		String project = getBusProject();
		Paginator paginator = getPaginator();
		Page<Letter> list = srv.getLettersByProject(project,paginator);
		renderJson(Ret.ok("data", list));
	}

	/**
     * @api {get} /project/letter/add 2.获取新增信息
     * @apiGroup letter
     * @apiVersion 1.0.0
     * @apiPermission 概况.函件管理.新增
     * 
     * @apiSuccess {String} entity 实体名称
     * @apiSuccess {String} fileToken 文件Token
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "entity":"proLetter",
     *   "fileToken":"7501a1cd4dd840628d6264664e35f362"
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	@Menu(value="概况.函件管理.新增", type=MenuType.BUTTON)
	public void add(){
		// 1.获取用户实体信息
		/*UserPrincipal  principal = getProjectUser();
		// 2.判断是否是管理员
		if(!principal.isOwner()){	// 不是管理员
			renderJson(Ret.fail("msg", "权限不足，不允许新建。"));
			return ;
		}*/
		// 3.随机的图片文件名
		String filePath = StrKit.getRandomUUID();
		renderJson(Ret.ok("fileToken", filePath)
				.set("entity", Folder.ENTITY_I_LETTER));
		
	}
	
	/**
     * @api {post} /project/letter/save 3.新增项目函件
     * @apiGroup letter
     * @apiVersion 1.0.0
     * @apiPermission 概况.函件管理.新增
     * 
     * @apiParam {String} title <code>必须参数</code> 函件标题
     * @apiParam {String} content <code>必须参数</code> 函件内容
     * @apiParam {String} fileToken <code>必须参数</code> 文件Token
     * 
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "msg":"函件新增成功"
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	@Before(LetterValidator.class)
	@Menu(value="概况.函件管理.新增", type=MenuType.BUTTON)
	public void save(){
		Letter letter = getBean(Letter.class,"");		// getBean 需要一個默認值
		String fileToken =  getPara("fileToken");
		Ret ret = srv.save(letter,fileToken,getProjectUser());
		renderJson(ret);
	}
	
	/**
     * @api {get} /project/letter/getEditInfo/{id} 4.获取编辑信息
     * @apiGroup letter
     * @apiVersion 1.0.0
     * @apiPermission 概况.函件管理.编辑
     *
     * @apiParam {Long} id <code>必须参数</code> 函件ID
     * 
     * @apiSuccess {String} entity 实体名称
     * @apiSuccess {String} fileToken 文件Token
     * @apiSuccess {Object} bean 函件对象
     * @apiSuccess (bean) {Long} id 函件ID
     * @apiSuccess (bean) {String} title 函件标题
     * @apiSuccess (bean) {String} content 函件内容
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
	@Menu(value="概况.函件管理.编辑", type=MenuType.BUTTON)
	public void getEditInfo(){
		// 接收页面传递参数id
		Long letterId = getParaToLong();
		// 是否是管理员
		/*UserPrincipal principal = getProjectUser();
		if(!principal.isOwner()){
			renderJson(Ret.fail("msg", "当前用户不具有操作权限"));
			return ;
		}*/
		// 查询id
		Letter bean = srv.findById(letterId);
		// 是否为空
		if(ObjKit.empty(bean)){
			renderJson(Ret.fail("msg", "函件实体不存在，请检查"));
			return ;
		}
		String fileToken = srv.getFolderName(bean.getId());
		if(StrKit.isBlank(fileToken)){
			fileToken = StrKit.getRandomUUID();
		}
		renderJson(Ret.ok("bean", bean)
				.set("fileToken", fileToken)
				.set("entity", Folder.ENTITY_I_LETTER));
	}
	
	/**
     * @api {post} /project/letter/update 5.修改项目函件
     * @apiGroup letter
     * @apiVersion 1.0.0
     * @apiPermission 概况.函件管理.编辑
     *
     * @apiParam {Long} id <code>必须参数</code> 函件ID
     * @apiParam {String} title <code>必须参数</code> 函件标题
     * @apiParam {String} content <code>必须参数</code> 函件内容
     * @apiParam {String} fileToken <code>必须参数</code> 文件Token
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "msg": "函件更新成功"
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	@Before(LetterValidator.class)
	@Menu(value="概况.函件管理.编辑", type=MenuType.BUTTON)
	public void update(){
		Letter bean = getBean(Letter.class,"");		//需有一默認值（可能引發：空指針、表列沒默認值）
		Ret ret = srv.update(bean, getPara("fileToken"));
		renderJson(ret);
	}
	
	/**
     * @api {get} /project/letter/delete/{id} 6.删除项目函件
     * @apiGroup letter
     * @apiVersion 1.0.0
     * @apiPermission 概况.函件管理.删除
     *
     * @apiParam {Long} id <code>必须参数</code> 函件ID
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
	@Menu(value="概况.函件管理.删除", type=MenuType.BUTTON)
	public void delete(){
		Long letterId = getParaToLong();
		UserPrincipal principal = getProjectUser();
		Ret ret = srv.delete(letterId,principal);
		renderJson(ret);
	}
	
	/**
     * @api {get} /project/letter/getLetterDetail/{id} 7.获取函件详情
     * @apiGroup letter
     * @apiVersion 1.0.0
     * @apiPermission 项目.概况.进入
     *
     * @apiParam {Long} id <code>必须参数</code> 函件ID
     * 
     * @apiSuccess {Long} id 函件ID
     * @apiSuccess {String} title 函件标题
     * @apiSuccess {String} content 函件内容
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
	public void getLetterDetail(){
		Long letterId = getParaToLong();
		Ret ret = srv.getLetterDetailById(letterId);
		renderJson(ret);
	}
	
	/**
     * @api {get} /project/letter/authFile/{token}_{id} 8.获取上传授权
     * @apiGroup letter
     * @apiVersion 1.0.0
     * @apiPermission 概况.函件管理.上传附件
     * 
     * @apiParam {String} token <code>必须参数</code> 文件Token
     * @apiParam {Long} id 函件ID
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
	@Menu(value="概况.函件管理.上传附件", type=MenuType.BUTTON)
	public void authFile() {
		String token = getPara(0);
		if (StrKit.isBlank(token)) {
			renderJson(Ret.fail("msg", "上传标识不存在，请检查"));
			return;
		}
		UserPrincipal user = getProjectUser();
		String pathId = FolderService.me
				.getFilePath(token, Folder.ENTITY_P_LETTER, user);
		if (StrKit.isBlank(pathId)) {
			renderJson(Ret.fail("msg", "上传目录不存在，请联系管理员"));
			return;
		}
		srv.updateFolder(getParaToLong(1), token);
		Ret ret = LfsService.me.getSIDUID();
		renderJson(ret.set("pathId", pathId));
	}
	
	/**
     * @api {get} /project/letter/delFile/{token}_{fileIds} 9.删除上传文件
     * @apiGroup letter
     * @apiVersion 1.0.0
     * @apiPermission 概况.函件管理.删除附件
     *
     * @apiParam {String} token <code>必须参数</code> 文件Token
     * @apiParam {String} fileIds <code>必须参数</code> 文件ID(多个用逗号分隔)
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   data:{
     *   	date: "2019-05-13 17:30:47"
	 *		msg: "文件404.jpg删除成功!"
	 *		status: "SUCCESS"
     *   }
     *   
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	@Menu(value="概况.函件管理.删除附件", type=MenuType.BUTTON)
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
