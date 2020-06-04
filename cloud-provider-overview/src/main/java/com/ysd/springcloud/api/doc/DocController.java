package com.ysd.springcloud.api.doc;

import com.jfinal.kit.Ret;
import com.ysd.springcloud.common.auth.UserPrincipal;
import com.ysd.springcloud.common.controller.BaseController;

/**
 * 技术图档访问控制层
 * @author Administrator
 * @date 2019年7月8日
 *
 */
public class DocController extends BaseController{
	
	DocService srv = DocService.me;
	

	/**
     * @api {get} /doc/getDocCircle 1.设计文档与施工文档统计
     * @apiGroup doc
     * @apiVersion 2.0.0
     * @apiHeader {String} Auth-Token-Overview 授权码
     * 
     * @apiSuccess designDoc 设计文档
     * @apiSuccess constructionDoc 施工文档
     * @apiSuccess {Integer} nowDesignTotal 设计文档当天上传统计
     * @apiSuccess {Integer} nowConsTotal 施工文档当天上传统计
     * @apiSuccess {Integer} docNum 累计上传的图档数量
     * @apiSuccess {Integer} modNum 累计上传的模型数量
     *
     * @apiSuccessExample {json} Success-Response:
	 *	{
	 *	    "data": {
	 *	        "designDoc": {
	 *	            "docNum": 40,
	 *	            "nowDesignTotal": 0,
	 *	            "modNum": 10
	 *	        },
	 *	        "constructionDoc": {
	 * 	            "nowConsTotal": 0,
	 *	            "docNum": 41,
	 *	            "modNum": 15
	 *	        }
	 *	    },
	 *	    "state": "ok"
	 *	}
     *
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	public void getDocCircle(){
		Ret ret = srv.getDocList(getProjectUser());
		renderJson(ret);
	}
	
	/**
     * @api {get} /doc/getAppointFile?typeVal={typeVal} 2.获取指定类型文件
     * @apiGroup doc
     * @apiVersion 2.0.0
     * @apiHeader {String} Auth-Token-Overview 授权码
     * 
     * @apiParam {String} typeVal <code>必须参数</code> 类型 [图档：office ,模型：bmodel ,图片: picture ,视频：video,工程文件：nwd]
     * 
     * @apiSuccess {Integer} total 总数
     * @apiSuccess {Array} docList 数组
     * @apiSuccess {string} id 文件ID
     * @apiSuccess {string} name 文件名
     * @apiSuccess {string} pathIds 文件路径ID
     * @apiSuccess {string} path 文件路径名
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   	"data": {
	 *	        "total": 55,
	 *	        "docList": [
	 *	            {
	 *	                "pathIds": "49348,63810,71495,71496,71497",
	 *	                "path": "/IT专用测试项目（勿删）1531908410635/proJob/201901302112140HbPza/jobData-154",
	 *	                "name": "404.jpg",
	 *	                "id": "71497"
	 *	            },
	 *	          ]
	 *	   	},
     *		"state": "ok"
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	public void getAppointFile(){
		String project = getBusProject();
		String type = getPara("typeVal");
//		System.out.println(type);
		Ret ret = srv.getAppointFile(project,type);
		renderJson(ret);
	}
	
	
	/**
     * @api {get} /doc/getDocHistogram 3.获取设计文档柱状图
     * @apiGroup doc
     * @apiVersion 2.0.0
     * @apiHeader {String} Auth-Token-Overview 授权码
     * 
     * @apiSuccess {Number} docNum 文档数量
     * @apiSuccess {Number} modNum 模型数量
     * @apiSuccess {string} name 组织名称
     *
     * @apiSuccessExample {json} Success-Response:
     *
     *	{
	 *	    "data": [
	 *	        {
	 *	            "docNum": 0,
	 *	            "modNum": 0,
	 *	            "name": "未分组人员"
	 *	        },
	 *	        {
	 *	            "docNum": 0,
	 *	            "modNum": 0,
	 *	            "name": "班组"
	 *	        },
	 *	    ],
	 *	    "state": "ok"
	 *	}
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	public void getDocHistogram(){
		UserPrincipal user = getProjectUser();
		Ret ret = srv.getDocHistogram(user);
		renderJson(ret);
	}
	
	/**
     * @api {get} /doc/getConstructionHistogram 4.获取施工文档柱状图
     * @apiGroup doc
     * @apiVersion 2.0.0
     * @apiHeader {String} Auth-Token-Overview 授权码
     * 
     * @apiSuccess {Number} docNum 文档数量
     * @apiSuccess {Number} modNum 模型数量
     * @apiSuccess {string} name 组织名称
     *
     * @apiSuccessExample {json} Success-Response:
     *
     *	{
	 *	    "data": [
	 *	        {
	 *	            "docNum": 0,
	 *	            "modNum": 0,
	 *	            "name": "未分组人员"
	 *	        },
	 *	        {
	 *	            "docNum": 0,
	 *	            "modNum": 0,
	 *	            "name": "班组"
	 *	        },
	 *	    ],
	 *	    "state": "ok"
	 *	}
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
//	获取施工文档柱状图
	public void getConstructionHistogram(){
		UserPrincipal user = getProjectUser();
		Ret ret = srv.getConstructionHistogram(user);
		renderJson(ret);
	}
	
	
}
