package com.ysd.overview.api.model;

import com.jfinal.kit.Ret;
import com.ysd.overview.common.controller.BaseController;

/*
 * File ModelController.java
 * ----------------------------
 * 重难点模拟 
 */
public class ModelController extends BaseController{
	ModelService srv = ModelService.me;
	
	/**
     * @api {get} /model/getModelList 1.获取模型列表
     * @apiGroup model
     * @apiVersion 2.0.0
     *  @apiHeader {String} Auth-Token-Overview 授权码
     *
     * @apiSuccess {Long} id 模型ID
     * @apiSuccess {String} code 模型编码(关联BimViz)
     * @apiSuccess {String} name 模型名称
     * @apiSuccess {String} picPath 图片地址
     * @apiSuccess {String} remark 模型描述
     *
     * @apiSuccessExample {json} Success-Response:
     * 
     * {
     *   "state": "ok",
	 *   "data":[
	 *	 	{
	 *			"id": 1,
	 *			"tabId": 8,
	 *			"code": "04d3a8f8-1018-41df-9fca-0d77a4fab00d",
	 *			"name": "施工图过程版模型(新)",
	 *			"verTime": "2019-01-09",
     *       	"verName": "1.1",
     *      	"picPath": "/model/732a521917ee4aa9996824c3d0596f36/",
     *      	"remark": "施工图过程版模型(新)",
     *      	"type": 0
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
	public void getModelList(){
		String project = getBusProject();
		System.out.println(project);
		Ret ret = srv.getModelList(project);
		renderJson(ret);
	}
}
