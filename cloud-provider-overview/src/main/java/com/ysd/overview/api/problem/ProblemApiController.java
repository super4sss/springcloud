package com.ysd.overview.api.problem;

import com.jfinal.aop.Before;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.ysd.overview.common.controller.BaseController;
import com.ysd.overview.common.model.Problem;

/**
 * 项目问题数据接口
 */
public class ProblemApiController extends BaseController {

	ProblemApiService srv = ProblemApiService.me;
	
	/**
     * @api {get} /problem?type={type} 1.获取问题分页
     * @apiGroup problem
     * @apiVersion 1.0.0
     * @apiHeader {String} Auth-Token-Overview 授权码
     * 
     * @apiParam {String} type 问题类型
     * @apiParam {String} category 问题类别
     * @apiParam {String} specialty 专业
     * @apiParam {String} area 发生区域
     * @apiParam {String} status 状态
     * @apiParam {Integer} pageNo 第几页(默认:1)
     * @apiParam {Integer} pageSize 页记录数(默认:20)
     *
     * @apiSuccess {Integer} pageNumber 第几页
     * @apiSuccess {Integer} pageSize 页记录数
     * @apiSuccess {Integer} totalRow 总记录数
     * @apiSuccess {Integer} totalPage 总页数
     * @apiSuccess {Boolean} firstPage 是否首页
     * @apiSuccess {Boolean} lastPage 是否末页
     * @apiSuccess {List} list 问题列表
     * @apiSuccess (list) {Long} id 问题ID
     * @apiSuccess (list) {String} code 问题编号
     * @apiSuccess (list) {String} type 问题类型
     * @apiSuccess (list) {String} category 问题类别
     * @apiSuccess (list) {String} specialty 专业
     * @apiSuccess (list) {String} description 描述
     * @apiSuccess (list) {String} area 发生区域
     * @apiSuccess (list) {String} build 栋
     * @apiSuccess (list) {String} floor 楼层
     * @apiSuccess (list) {String} position 位置
     * @apiSuccess (list) {String} beginTime 发生时间
     * @apiSuccess (list) {String} endTime 解决时间
     * @apiSuccess (list) {String} contactMan 责任人
     * @apiSuccess (list) {String} contactNum 联系电话
     * @apiSuccess (list) {String} belongsGroup 所属团组
     * @apiSuccess (list) {String} status 状态
     * @apiSuccess (list) {String} project 项目编码
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
	 *				"code": "001",
     *      		"type": "质量",
     *      		"category": "材料",
     *      		"specialty": "土建",
     *      		"description": "混凝土不达标",
     *      		"area": "S1地块",
     *      		"build": "1栋",
     *      		"floor": "B1层",
     *      		"position": "轴",
     *      		"beginTime": "2018-09-08",
     *      		"endTime": null,
     *      		"contactMan": "张三",
     *      		"contactNum": "123456789",
     *      		"belongsGroup": "分包公司1",
     *      		"status": "整改中",
     *      		"project": "289"
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
		Problem form = getQueryBean(Problem.class);
		form.setProject(getBusProject());
		Page<Record> page = srv.paginate(getPaginator(), form);
		renderJson(Ret.ok("page", page));
	}
	
	/**
     * @api {get} /problem/month?type={type}&area={area} 2.按月份汇总
     * @apiGroup problem
     * @apiVersion 1.0.0
     * @apiHeader {String} Auth-Token-Overview 授权码
     * 
     * @apiParam {String} type <code>必须参数</code> 问题类型
     * @apiParam {String} area 发生区域
     * 
     * @apiSuccess {Integer} findNum 发现个数
     * @apiSuccess {Integer} solveNum 解决个数
     * @apiSuccess {String} solveRate 解决比率
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "data":{
     *   	"2018年01月":{
	 *			"findNum": 20,
     *			"solveNum": 10,
     *			"solveRate": 50
     *   	},
     *   	"2018年02月":{
	 *			"findNum": 20,
     *			"solveNum": 10,
     *			"solveRate": 50
     *   	}
	 *   }
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	public void month() {
		Problem form = getQueryBean(Problem.class);
		form.setProject(getBusProject());
		renderJson(Ret.ok("data", srv.statByMonth(form)));
	}
	
	/**
     * @api {get} /problem/specialty?type={type}&area={area} 3.按专业汇总
     * @apiGroup problem
     * @apiVersion 1.0.0
     * @apiHeader {String} Auth-Token-Overview 授权码
     * 
     * @apiParam {String} type <code>必须参数</code> 问题类型
     * @apiParam {String} area 发生区域
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "data":{
     *   	"智控弱电1":10,
     *   	"智控弱电2":20,
     *   	"智控弱电3":30
	 *   }
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	public void specialty() {
		Problem form = getQueryBean(Problem.class);
		form.setProject(getBusProject());
		renderJson(Ret.ok("data", srv.statBySpecialty(form)));
	}
	
	/**
     * @api {get} /problem/group?type={type}&area={area} 4.按分包公司汇总
     * @apiGroup problem
     * @apiVersion 1.0.0
     * @apiHeader {String} Auth-Token-Overview 授权码
     * 
     * @apiParam {String} type <code>必须参数</code> 问题类型
     * @apiParam {String} area 发生区域
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "data":{
     *   	"分包公司1":10,
     *   	"分包公司2":20,
     *   	"分包公司3":30
	 *   }
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	public void group() {
		Problem form = getQueryBean(Problem.class);
		form.setProject(getBusProject());
		renderJson(Ret.ok("data", srv.statByGroup(form)));
	}
	
	/*风投指挥页问题导入*/
	@Before(Tx.class)
	public void importProb(){
		Ret ret = srv.importProb(getProjectUser(), getFile("file"));
		renderJson(ret);
	}
	
	
}
