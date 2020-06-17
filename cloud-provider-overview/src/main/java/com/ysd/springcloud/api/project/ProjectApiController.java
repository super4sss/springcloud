package com.ysd.springcloud.api.project;

import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.ysd.springcloud.common.auth.UserPrincipal;
import com.ysd.springcloud.common.controller.BaseController;
import com.ysd.springcloud.common.dto.EventDTO;
import com.ysd.springcloud.common.dto.FileDTO;
import com.ysd.springcloud.common.kit.ObjKit;
import com.ysd.springcloud.common.model.Picture;

import java.util.List;
import java.util.Map;

/**
 * 项目数据接口
 */
public class ProjectApiController extends BaseController {

	ProjectApiService srv = ProjectApiService.me;
	
	/**
     * @api {get} /project/prop 1.获取项目属性
     * @apiGroup project
     * @apiVersion 1.0.0
     * @apiHeader {String} Auth-Token-Overview 授权码
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "data":{
     *   	"总包单位":"广东省国际工程咨询有限公司",
     *   	"项目类型":"建筑工程",
     *   	"工程地点":"广州天河区龙洞广东金融学院北校区"
     *   }
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	public void prop() {
		UserPrincipal user = getProjectUser();
		Map<String, String> propMap = srv.findPropMap(user);
		renderJson(Ret.ok("data", propMap));
	}
	
	/**
     * @api {get} /project/events 2.获取项目事件
     * @apiGroup project
     * @apiVersion 1.0.0
     * @apiHeader {String} Auth-Token-Overview 授权码
     * 
     * @apiParam {Integer} top 最新条数(默认:7)
     * 
     * @apiSuccess {String} id 事件ID
     * @apiSuccess {String} title 事件标题
     * @apiSuccess {String} createAt 创建时间
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "data":[
	 *	 	{
	 *			"id": "1",
	 *			"title": "重要事件",
     *      	"createAt": "2018-07-20"
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
	public void events() {
		int top = getParaToInt("top", 7);
		List<EventDTO> list = srv.findTopEvent(getBusProject(), top);
		renderJson(Ret.ok("data", list));
	}
	
	/**
     * @api {get} /project/event/{id} 3.获取事件详情
     * @apiGroup project
     * @apiVersion 1.0.0
     * @apiHeader {String} Auth-Token-Overview 授权码
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
     *   "data":{
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
	public void event() {
		Record record = srv.findEventById(getParaToLong());
		if (ObjKit.empty(record)) {
			renderJson(Ret.fail("msg", "事件不存在，请联系管理员"));
			return;
		}
		renderJson(Ret.ok("data", record));
	}
	
	/**
     * @api {get} /project/files?pid={pid} 4.获取技术文档
     * @apiGroup project
     * @apiVersion 1.0.0
     * @apiHeader {String} Auth-Token-Overview 授权码
     *
     * @apiParam {String} pid 目录ID
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
	public void files() {
		String pid = getPara("pid");
		List<FileDTO> list = srv.getFiles(pid, getBusProject());
		renderJson(Ret.ok("data", list));
	}
	
	/**
     * @api {get} /project/file/{id}_{extName} 5.获取文档路径
     * @apiGroup project
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
	public void file() {
		Ret ret = srv.downloadFile(getPara(0), getPara(1));
		renderJson(ret);
	}
	
	/**
     * @api {get} /project/pictures?type={type}&name={name} 6.获取照片分页
     * @apiGroup project
     * @apiVersion 1.0.0
     * @apiHeader {String} Auth-Token-Overview 授权码
     * 
     * @apiParam {Integer} type <code>必须参数</code> 图片类别(1:现场航拍,2:效果图,3:项目照片)
     * @apiParam {String} name 名称搜索
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
     *
     * @apiSuccessExample {json} Success-Response:
     * 
     * {
     *   "state": "ok",
     *   "page":{
     *   	"pageNumber": 1,
     *   	"pageSize": 20,
     *   	"totalRow": 1,
     *   	"totalPage": 1,
     *   	"firstPage": true,
     *   	"lastPage": true,
     *   	"list":[
	 *	 		{
	 *				"id": 1,
	 *				"name": "DJI_0124.jpg",
     *      		"path": "/prop/91adc1202ba74ab59235518dc73a02a1/"
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
	public void pictures() {
		Picture form = getQueryBean(Picture.class);
		form.setProject(getBusProject());
		Page<Record> page = srv.findPictures(getPaginator(), form);
		renderJson(Ret.ok("page", page));
	}
	
	/**
     * @api {get} /project/picture?type={type}&name={name} 7.获取照片列表
     * @apiGroup project
     * @apiVersion 1.0.0
     * @apiHeader {String} Auth-Token-Overview 授权码
     * 
     * @apiParam {Integer} type <code>必须参数</code> 图片类别(1:现场航拍,2:效果图,3:项目照片)
     * @apiParam {String} name 名称搜索
     * 
     * @apiSuccess {Long} id 图片ID
     * @apiSuccess {String} name 图片名称
     * @apiSuccess {String} path 图片路径
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "data":[
	 *	 	{
	 *			"id": 1,
	 *			"name": "DJI_0124.jpg",
     *      	"path": "/prop/91adc1202ba74ab59235518dc73a02a1/"
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
	public void picture() {
		Picture form = getQueryBean(Picture.class);
		form.setProject(getBusProject());
//		form.setType(getPara"));
		renderJson(Ret.ok("data", srv.getPictures(form)));
	}
	
	/**
     * @api {get} /project/vrshow?keyword={keyword} 8.获取VR展示
     * @apiGroup project
     * @apiVersion 1.0.0
     * @apiHeader {String} Auth-Token-Overview 授权码
     * 
     * @apiParam {String} keyword 地块关键词
     * 
     * @apiSuccess {String} name 模型名称
     * @apiSuccess {String} viewPath 全景地址
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "data":[
	 *	 	{
	 *			"name": "VR展示",
     *      	"viewPath": "http://www.baidu.com"
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
	public void vrshow() {
		String keyword = getPara("keyword");
		List<Record> list = srv.getVrShows(getBusProject(), keyword);
		renderJson(Ret.ok("data", list));
	}
	
	/**
     * @api {get} /project/getFileList?pid={pid}&root={root} 获取技术文档_v2
     * @apiGroup project
     * @apiVersion 2.0.0
     * @apiHeader {String} Auth-Token-Overview 授权码
     *
     * @apiParam {String} pid 目录ID
     * @apiParam {Boolean} root 是否是根目录
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
	/*获取文件列表*/
	public void getFileList(){
		String pid = getPara("pid");
		Boolean root =  getParaToBoolean("root", false);
		Ret ret = srv.getFileList(pid, root, getBusProject());
		renderJson(ret);
	}
	
}
