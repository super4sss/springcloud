package com.ysd.overview.front.index;

import java.util.List;
import java.util.Map;

import com.jfinal.kit.PropKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.ysd.overview.common.auth.Menu;
import com.ysd.overview.common.auth.UserPrincipal;
import com.ysd.overview.common.controller.BaseController;
import com.ysd.overview.common.dto.FileDTO;
import com.ysd.overview.common.dto.TaskDTO;

/**
 * 首页控制器
 */
public class IndexController extends BaseController {

	IndexService srv = IndexService.me;
	
//	@Menu("项目.概况.进入")
	public void index() {
		redirect(PropKit.get("page.overview"), true);
	}
	
	/**
     * @api {get} /projectUser 1.获取项目用户
     * @apiGroup common
     * @apiVersion 1.0.0
     * 
     * @apiSuccess {String} account 用户账号
     * @apiSuccess {String} user 用户ID
     * @apiSuccess {String} username 用户名称
     * @apiSuccess {String} project 项目标识
     * @apiSuccess {String} projectName 项目名称
     * @apiSuccess {String} isOwner 是否项目负责人
     * @apiSuccess {List} menus 菜单列表
     * @apiSuccess {List} perms 权限列表
     * @apiSuccess (menus) {String} id 菜单ID
     * @apiSuccess (menus) {String} title 菜单标题
     * @apiSuccess (menus) {String} url 菜单地址
     * @apiSuccess (menus) {String} target 重定向操作
     * @apiSuccess (menus) {Integer} link 链接类型(0:站内链接,1:站外链接)
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "data":{
     *   	"account":"zhangsan",
     *   	"user":"1",
     *   	"username":"张三",
     *   	"project":"289",
     *   	"projectName":"IT专用测试项目",
     *   	"isOwner":false,
     *   	"menus":[
     *   		{
     *   			"id":"gooverview",
     *   			"title":"概况",
     *   			"url":"/ysdOverview",
     *   			"target":"_self",
     *   			"link":0
     *   		}
     *   	],
     *   	"perms":[
     *   		"概况.重要事件.新增",
     *   	    "概况.重要事件.编辑"
     *   	]
     *   }
     * }
     * 
     * @apiErrorExample {json} 未登录-Response:
     * {
     *   "state":"unknown",
     *   "msg":"未登录，请重新登录"
     * }
     * @apiErrorExample {json} 无项目-Response:
     * {
     *   "state":"unopened",
     *   "msg":"无项目，请重新进入"
     * }
     * @apiErrorExample {json} 未授权-Response:
     * {
     *   "state":"unauthorized",
     *   "msg":"未授权，请联系管理员"
     * }
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	public void projectUser() {
		UserPrincipal user = getProjectUser();
		renderJson(Ret.ok("data", srv.getBusInfo(user)));
	}
	
	/**
     * @api {get} /projectPerms 2.获取项目权限
     * @apiGroup common
     * @apiVersion 1.0.0
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "data":[
     *   	"概况.重要事件.新增",
     *   	"概况.重要事件.编辑"
     *   ]
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	public void projectPerms() {
		UserPrincipal user = getProjectUser();
		renderJson(Ret.ok("data", srv.getUserPerms(user)));
	}
	
	/**
     * @api {get} /projectInfo 3.获取项目信息
     * @apiGroup common
     * @apiVersion 1.0.0
     * 
     * @apiSuccess {String} name 项目名称
     * @apiSuccess {String} startTime 开始时间
     * @apiSuccess {String} endTime 结束时间
     * @apiSuccess {String} timeLimit 工期
     * @apiSuccess {String} schedule 进度
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "data":{
     *   	"name":"研发部专用项目",
     *   	"startTime":"2018/10/29",
     *   	"endTime":"2019/10/28",
     *   	"timeLimit":"365",
     *   	"schedule":"0"
     *   }
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	public void projectInfo() {
		UserPrincipal user = getProjectUser();
		Ret ret = srv.getProjectInfo(user);
		renderJson(ret);
	}
	
	/**
     * @api {get} /projectStage 4.获取项目阶段
     * @apiGroup common
     * @apiVersion 1.0.0
     * 
     * @apiSuccess {String} code 阶段编码
     * @apiSuccess {String} name 阶段名称
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "data":[
     *   	 {
     *   		 "code":"795",
     *   		 "name":"前期资料"
     *   	 }
     *   ]
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	public void projectStage() {
		renderJson(ClientService.me.getStages(getProjectUser()));
	}
	
	/**
     * @api {get} /stageInfo/{stage} 5.获取阶段详情
     * @apiGroup common
     * @apiVersion 1.0.0
     * 
     * @apiParam {String} stage <code>必须参数</code> 阶段编码
     * 
     * @apiSuccess {String} name 阶段名称
     * @apiSuccess {String} startTime 开始时间
     * @apiSuccess {String} endTime 结束时间
     * @apiSuccess {String} timeLimit 工期
     * @apiSuccess {String} schedule 进度
     * @apiSuccess {String} isOwner 是否阶段负责人
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "data":{
     *   	"name":"前期资料",
     *   	"startTime":"2018/07/18",
     *   	"endTime":"2018/08/31",
     *   	"timeLimit":"45",
     *   	"schedule":"0",
     *   	"isOwner":false
     *   }
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	public void stageInfo() {
		UserPrincipal user = getProjectUser();
		Ret ret = srv.getStageInfo(getPara(), user);
		renderJson(ret);
	}
	
	/**
     * @api {get} /entityFile/{entity}_{entityId} 6.获取上传文件
     * @apiGroup common
     * @apiVersion 1.0.0
     *
     * @apiParam {String} entity <code>必须参数</code> 实体名称
     * @apiParam {String} entityId <code>必须参数</code> 实体标识
     * 
     * @apiSuccess {String} id 文件ID
     * @apiSuccess {String} name 文件名称
     * @apiSuccess {String} size 文件大小
     * @apiSuccess {String} lastModifyTime 最后修改时间
     * @apiSuccess {Boolean} directory 是否文件夹
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "data":[
     *   	 {
     *   		 "id":1,
     *   		 "name":"场地准备施工.doc",
     *   		 "size":"16M",
     *   		 "lastModifyTime":"2018-07-20 12:00:00",
     *   		 "directory":false
     *   	 }
     *   ]
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	public void entityFile() {
		String entity = getPara(0);
		String entityId = getPara(1);
		if (StrKit.isBlank(entity) 
				|| StrKit.isBlank(entityId)) {
			renderJson(Ret.fail("msg", "实体标识不存在，请检查"));
			return;
		}
		List<FileDTO> list = FolderService.me.getFiles(entity, entityId);
		renderJson(Ret.ok("data", list));
	}
	
	/**
     * @api {get} /onlinePreview/{id}_{extName} 7.在线预览
     * @apiGroup common
     * @apiVersion 1.0.0
     * @apiHeader {String} Auth-Token-Overview 授权码
     *
     * @apiParam {String} id <code>必须参数</code> 文件ID
     * @apiParam {String} extName <code>必须参数</code> 文件扩展名
     * 
     * @apiSuccess {String} filePath 文件路径
     * @apiSuccess {String} fileType 文件类型(office:办公文档,picture:图片,text:文本,other:其他)
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "filePath":"/preview/20180824/115000abcdef.jpg",
     *   "fileType":"picture"
     *   
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	public void onlinePreview(){
		String documentId = getPara(0);
		String suffix = getPara(1);
		Ret ret = srv.downloadFile(documentId,suffix);
		renderJson(ret);
	}
	
	
	/**
     * @api {get} /startTask 1.汇总发起任务
     * @apiGroup task
     * @apiVersion 1.0.0
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "data":{
     *   	"2018年04月":"50",
     *   	"2018年05月":"40",
     *      "2018年06月":"30",
     *   	"2018年07月":"20",
     *   	"2018年08月":"10"
     *   }
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	public void startTask() {
		Map<String, String> map = srv.statStartTask(getBusProject());
		renderJson(Ret.ok("data", map));
	}
	
	/**
     * @api {get} /weekTask 2.获取本周任务
     * @apiGroup task
     * @apiVersion 1.0.0
     * 
     * @apiSuccess {Long} id 任务ID
     * @apiSuccess {String} title 任务标题
     * @apiSuccess {String} executor 执行人
     * @apiSuccess {String} startTime 发起时间
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "data":[
     *   	 {
     *   		 "id":1,
     *           "title":"场地准备施工",
     *   		 "executor":"张三",
     *   		 "startTime":"2018-08-10 15:00:00"
     *   	 }
     *   ]
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	public void weekTask() {
		List<TaskDTO> list = srv.findWeekTask(getBusProject());
		renderJson(Ret.ok("data", list));
	}
	
}
