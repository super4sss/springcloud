package com.ysd.overview.front.project.organizer;

import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.ysd.overview.common.auth.UserPrincipal;
import com.ysd.overview.common.controller.BaseController;
import com.ysd.overview.common.kit.ObjKit;
import com.ysd.overview.common.model.Organizer;

/**
 * 承办单位控制器
 */
public class OrganizerController extends BaseController {

	OrganizerService srv = OrganizerService.me;
	
	/**
     * @api {get} /project/organizer?unitName={unitName} 1.获取单位记录
     * @apiGroup organizer
     * @apiVersion 1.0.0
     * 
     * @apiParam {String} unitName 名称关键字
     * @apiParam {Integer} pageNo 第几页(默认:1)
     * @apiParam {Integer} pageSize 页记录数(默认:20)
     *
     * @apiSuccess {Integer} pageNumber 第几页
     * @apiSuccess {Integer} pageSize 页记录数
     * @apiSuccess {Integer} totalRow 总记录数
     * @apiSuccess {Integer} totalPage 总页数
     * @apiSuccess {Boolean} firstPage 是否首页
     * @apiSuccess {Boolean} lastPage 是否末页
     * @apiSuccess {List} list 记录列表
     * @apiSuccess (list) {Long} id 单位ID
     * @apiSuccess (list) {String} parties 参与方
     * @apiSuccess (list) {String} unitName 单位名称
     * @apiSuccess (list) {String} unitCode 单位编码
     * @apiSuccess (list) {String} contacts 联系人
     * @apiSuccess (list) {String} address 地址
     * @apiSuccess (list) {String} phone 手机号码
     * @apiSuccess (list) {String} telephone 电话
     * @apiSuccess (list) {String} fax 传真
     * @apiSuccess (list) {Integer} sort 排序号
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
	 *				"id": 1,
	 *				"parties": "设计单位",
	 *				"unitName": "广东设计院",
	 *				"unitCode": "GZSJY",
     *      		"contacts": "张三",
     *      		"address": "广东省广州市",
     *      		"phone": "13600000001",
     *      		"telephone": "020-0000001",
     *      		"fax": "020-0000002",
     *      		"sort": 1
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
		Organizer form = getQueryBean(Organizer.class);
		form.setProject(getBusProject());
		Page<Organizer> page = srv.paginate(getPaginator(), form);
		renderJson(Ret.ok("page", page));
	}
	
	/**
     * @api {post} /project/organizer/save 2.新增单位
     * @apiGroup organizer
     * @apiVersion 1.0.0
     * 
     * @apiParam {String} parties <code>必须参数</code> 参与方
     * @apiParam {String} unitName <code>必须参数</code> 单位名称
     * @apiParam {String} unitCode 单位编码
     * @apiParam {String} contacts 联系人
     * @apiParam {String} address 地址
     * @apiParam {String} phone 手机号码
     * @apiParam {String} telephone 电话
     * @apiParam {String} fax 传真
     * @apiParam {String} remark 备注
     * @apiParam {Integer} sort 排序号
     * 
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "msg":"单位创建成功"
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	@Before(OrganizerValidator.class)
	public void save() {
		Organizer bean = getBean(Organizer.class, "");
		Ret ret = srv.save(bean, getProjectUser());
		renderJson(ret);
	}
	
	/**
     * @api {post} /project/organizer/update 3.修改单位
     * @apiGroup organizer
     * @apiVersion 1.0.0
     *
     * @apiParam {Long} id <code>必须参数</code> 单位ID
     * @apiParam {String} parties <code>必须参数</code> 参与方
     * @apiParam {String} unitName <code>必须参数</code> 单位名称
     * @apiParam {String} unitCode 单位编码
     * @apiParam {String} contacts 联系人
     * @apiParam {String} address 地址
     * @apiParam {String} phone 手机号码
     * @apiParam {String} telephone 电话
     * @apiParam {String} fax 传真
     * @apiParam {String} remark 备注
     * @apiParam {Integer} sort 排序号
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "msg": "单位更新成功"
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	@Before(OrganizerValidator.class)
	public void update() {
		Organizer bean = getBean(Organizer.class, "");
		Ret ret = srv.update(bean);
		renderJson(ret);
	}
	
	/**
     * @api {get} /project/organizer/delete/{id} 4.删除单位
     * @apiGroup organizer
     * @apiVersion 1.0.0
     *
     * @apiParam {Long} id <code>必须参数</code> 单位ID
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "msg":"单位删除成功"
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	public void delete() {
		UserPrincipal user = getProjectUser();
		Ret ret = srv.delete(getParaToLong(), user);
		renderJson(ret);
	}
	
	/**
     * @api {get} /project/organizer/{id} 5.获取单位详情
     * @apiGroup organizer
     * @apiVersion 1.0.0
     *
     * @apiParam {Long} id <code>必须参数</code> 单位ID
     * 
     * @apiSuccess {Long} id 单位ID
     * @apiSuccess {String} parties 参与方
     * @apiSuccess {String} unitName 单位名称
     * @apiSuccess {String} unitCode 单位编码
     * @apiSuccess {String} contacts 联系人
     * @apiSuccess {String} address 地址
     * @apiSuccess {String} phone 手机号码
     * @apiSuccess {String} telephone 电话
     * @apiSuccess {String} fax 传真
     * @apiSuccess {String} remark 备注
     * @apiSuccess {Integer} sort 排序号
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "bean":{
     *   	"id": 1,
	 *		"parties": "设计单位",
	 *		"unitName": "广东设计院",
	 *		"unitCode": "GZSJY",
     *      "contacts": "张三",
     *      "address": "广东省广州市",
     *      "phone": "13600000001",
     *      "telephone": "020-0000001",
     *      "fax": "020-0000002",
     *      "remark": "设计单位备注",
     *      "sort": 1
     *   }
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	public void detail() {
		Organizer bean = srv.findById(getParaToLong());
		if (ObjKit.empty(bean)) {
			renderJson(Ret.fail("msg", "单位实体不存在，请检查"));
			return;
		}
		renderJson(Ret.ok("bean", bean));
	}
	
	/**
     * @api {get} /project/organizer/list 6.获取项目单位
     * @apiGroup organizer
     * @apiVersion 1.0.0
     *
     * @apiSuccess {String} parties 参与方
     * @apiSuccess {String} unitName 单位名称
     *
     * @apiSuccessExample {json} Success-Response:
     * 
     * {
     *   "state": "ok",
     *   "data":[
	 *	 	{
	 *			"parties": "设计单位",
     *      	"unitName": "广东设计院"
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
	public void list() {
		List<Organizer> list = srv.findByProject(getBusProject());
		renderJson(Ret.ok("data", list));
	}
	
}
