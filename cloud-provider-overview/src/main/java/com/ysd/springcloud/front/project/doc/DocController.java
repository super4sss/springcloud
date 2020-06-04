package com.ysd.springcloud.front.project.doc;

import com.jfinal.aop.Before;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.ysd.springcloud.common.controller.BaseController;
import com.ysd.springcloud.common.model.LogDocument;

/**
 * 图档操作控制器
 */
public class DocController extends BaseController {

	DocService srv = DocService.me;
	
	/**
     * @api {get} /project/doc 1.汇总更新日志
     * @apiGroup doc
     * @apiVersion 1.0.0
     * 
     * @apiParam {Integer} pageNo 第几页(默认:1)
     * @apiParam {Integer} pageSize 页记录数(默认:5)
     *
     * @apiSuccess {Integer} pageNumber 第几页
     * @apiSuccess {Integer} pageSize 页记录数
     * @apiSuccess {Integer} totalRow 总记录数
     * @apiSuccess {Integer} totalPage 总页数
     * @apiSuccess {Boolean} firstPage 是否首页
     * @apiSuccess {Boolean} lastPage 是否末页
     * @apiSuccess {List} list 记录列表
     * @apiSuccess (list) {String} start 开始日期
     * @apiSuccess (list) {String} end 结束日期
     * @apiSuccess (list) {Integer} docNum 图档更新数
     * @apiSuccess (list) {Integer} modNum 模型更新数
     * @apiSuccess (list) {String} creatorName 最后更新人
     *
     * @apiSuccessExample {json} Success-Response:
     * 
     * {
     *   "state": "ok",
	 *   "page":{
	 *   	"pageNumber": 1,
	 *   	"pageSize": 5,
	 *   	"totalRow": 2,
	 *   	"totalPage": 1,
	 *   	"firstPage": true,
	 *   	"lastPage": true,
	 *   	"list":[
	 *	 		{
	 *				"start": "2019-05-25",
	 *				"end": "2019-05-31",
     *      		"docNum": 5,
     *      		"modNum": 2,
     *      		"creatorName": "管理员"
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
	public void index() {
		int pageNo = getParaToInt("pageNo", 1);
		int pageSize = getParaToInt("pageSize", 5);
		Page<Record> page = srv.paginate(getBusProject(), pageNo, pageSize);
		renderJson(Ret.ok("page", page));
	}
	
	/**
     * @api {get} /project/doc/list?type={type}&start={start}&end={end} 2.获取更新记录
     * @apiGroup doc
     * @apiVersion 1.0.0
     * 
     * @apiParam {Integer} type <code>必须参数</code> 类型(1:图档,2:模型)
     * @apiParam {String} start 开始日期
     * @apiParam {String} end 结束日期
     * @apiParam {Integer} pageNo 第几页(默认:1)
     * @apiParam {Integer} pageSize 页记录数(默认:5)
     *
     * @apiSuccess {Integer} pageNumber 第几页
     * @apiSuccess {Integer} pageSize 页记录数
     * @apiSuccess {Integer} totalRow 总记录数
     * @apiSuccess {Integer} totalPage 总页数
     * @apiSuccess {Boolean} firstPage 是否首页
     * @apiSuccess {Boolean} lastPage 是否末页
     * @apiSuccess {List} list 记录列表
     * @apiSuccess (list) {String} code 文件编码
     * @apiSuccess (list) {String} name 文件名称
     * @apiSuccess (list) {String} path 访问路径
     * @apiSuccess (list) {String} creator 创建人工号
     * @apiSuccess (list) {String} creatorName 创建人名称
     * @apiSuccess (list) {String} createAt 创建时间
     *
     * @apiSuccessExample {json} Success-Response:
     * 
     * {
     *   "state": "ok",
	 *   "page":{
	 *   	"pageNumber": 1,
	 *   	"pageSize": 5,
     *   	"totalRow": 2,
     *   	"totalPage": 1,
     *   	"firstPage": true,
     *   	"lastPage": true,
	 *   	"list":[
	 *	 		{
	 *				"code": "20001",
	 *				"name": "上传图档v1.0.doc",
	 *				"path": "10001,10002,10003",
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
	public void list() {
		DocParam form = getQueryBean(DocParam.class);
		form.setProject(getBusProject());
		int pageNo = getParaToInt("pageNo", 1);
		int pageSize = getParaToInt("pageSize", 5);
		Page<Record> page = srv.paginate(form, pageNo, pageSize);
		renderJson(Ret.ok("page", page));
	}
	
	/**
     * @api {post} /project/doc/save 3.新增图档更新
     * @apiGroup doc
     * @apiVersion 1.0.0
     * 
     * @apiParam {String} code <code>必须参数</code> 文件编码
     * @apiParam {String} name <code>必须参数</code> 文件名称
     * @apiParam {String} path 访问路径
     * @apiParam {String} pathName 访问路径名称
     * @apiParam {Integer} type 类型(1:图档,2:模型)
     * @apiParam {String} stageId 阶段ID   （废弃）
     * @apiParam {String} stageName 阶段名称	（废弃）
     * 
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "msg":"记录保存成功"
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	@Before(DocValidator.class)
	public void save() {
		DocParam bean = getBean(DocParam.class, "");
		Ret ret = srv.save(bean, getProjectUser());
		renderJson(ret);
	}
	
	/**
     * @api {get} /project/doc/getDocLogSum 4.分页汇总图档更新日志
     * @apiGroup doc
     * @apiVersion 2.0.0
     * 
     * @apiParam {Integer} pageNo 第几页(默认:1)
     * @apiParam {Integer} pageSize 页记录数(默认:10)
     * 
     * @apiSuccess {String} dateStr 日期
     * @apiSuccess {Integer} uploadNum 上传数
     * @apiSuccess {Integer} peopNum 人数
     *
     * @apiSuccess {Integer} pageNumber 第几页
     * @apiSuccess {Integer} pageSize 页记录数
     * @apiSuccess {Integer} totalRow 总记录数
     * @apiSuccess {Integer} totalPage 总页数
     * @apiSuccess {Boolean} firstPage 是否首页
     * @apiSuccess {Boolean} lastPage 是否末页
     * 
     * @apiSuccessExample {json} Success-Response:
	 *{
	 *    "data": {
	 *        "totalRow": 33,
	 *        "pageNumber": 1,
	 *        "firstPage": true,
	 *        "lastPage": false,
	 *        "totalPage": 33,
	 *        "pageSize": 1,
	 *        "list": [
	 *            {
	 *                "peopNum": 1,
	 *                "dateStr": "2019-10-09",
	 *                "uploadNum": 1
	 *            }
	 *        ]
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
	public void getDocLogSum(){
		String project = getBusProject();
		String dateStr = getPara("dateStr");
		Page<LogDocument> pages = srv.getDocLogSum(project,getPaginator(),dateStr);
		renderJson(Ret.ok("data", pages));
	}
	
	
	/**
     * @api {get} /project/doc/getDocLogDesc 5.分页获取日志详情
     * @apiGroup doc
     * @apiVersion 2.0.0
     * 
     * @apiParam {String} dateStr  日期	（格式：YYYY-MM-dd）
     * @apiParam {Integer} pageNo 第几页(默认:1)
     * @apiParam {Integer} pageSize 页记录数(默认:10)
     * 
     * @apiSuccess {String} path 路径编号
     * @apiSuccess {String} pathName 路径名称
     * @apiSuccess {String} creator 创建人编号
     * @apiSuccess {String} creatorName 创建人
     * @apiSuccess {Integer} uploadNum 文件数量
     *
     * @apiSuccess {Integer} pageNumber 第几页
     * @apiSuccess {Integer} pageSize 页记录数
     * @apiSuccess {Integer} totalRow 总记录数
     * @apiSuccess {Integer} totalPage 总页数
     * @apiSuccess {Boolean} firstPage 是否首页
     * @apiSuccess {Boolean} lastPage 是否末页
     * 
     * @apiSuccessExample {json} Success-Response:
	 *{
	 *    "totalRow": 3,
	 *    "pageNumber": 1,
	 *    "lastPage": true,
	 *    "firstPage": true,
	 *    "totalPage": 1,
	 *    "pageSize": 10,
	 *    "list": [
	 *        {
	 *            "pathName": "2#",
	 *            "path": "95464,103017,103018,130590",
	 *            "creator": "365",
	 *            "uploadNum": 14,
	 *            "creatorName": "林晓瀛"
	 *        },
	 *    ]
	 *}
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	public void getDocLogDesc(){
		String project = getBusProject();
		String dateStr = getPara("dateStr");
		Page<LogDocument> pages =  srv.getDocLogDesc(project, dateStr,getPaginator());
		renderJson(pages);
	}
	
	/*批量设置图档路径名称，不对外开放*/
	@Before(Tx.class)
	public void setPathName(){
		String project = getPara("project");
		Ret ret = srv.setPathName(project);
		renderJson(ret);
	}
	
}
