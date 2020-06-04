package com.ysd.springcloud.front.problem;

import com.jfinal.aop.Before;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.upload.UploadFile;
import com.ysd.springcloud.common.auth.UserPrincipal;
import com.ysd.springcloud.common.controller.BaseController;
import com.ysd.springcloud.common.dto.ExportDTO;
import com.ysd.springcloud.common.model.Problem;

import java.io.File;
import java.util.List;

/**
 * 项目问题控制器
 */

public class ProblemController extends BaseController {

	ProblemService srv = ProblemService.me;
	
	public void index() {
		redirect(PropKit.get("page.problem"), true);
	}
	
	/**
     * @api {get} /problem/list 1.获取问题列表
     * @apiGroup problem
     * @apiVersion 1.0.0
     *
     * @apiSuccess {Long} id 问题ID
     * @apiSuccess {String} code 问题编号
     * @apiSuccess {String} type 问题类型
     * @apiSuccess {String} category 问题类别
     * @apiSuccess {String} specialty 专业
     * @apiSuccess {String} description 描述
     * @apiSuccess {String} area 发生区域
     * @apiSuccess {String} build 栋
     * @apiSuccess {String} floor 楼层
     * @apiSuccess {String} position 位置
     * @apiSuccess {String} beginTime 发生时间
     * @apiSuccess {String} endTime 解决时间
     * @apiSuccess {String} contactMan 责任人
     * @apiSuccess {String} contactNum 联系电话
     * @apiSuccess {String} belongsGroup 所属团组
     * @apiSuccess {String} status 状态
     * @apiSuccess {String} project 项目编码
     *
     * @apiSuccessExample {json} Success-Response:
     * 
     * {
     *   "state": "ok",
     *   "page":{
     *   	"totalRow": 2,
     *   	"pageNumber": 1,
     *   	"firstPage": true,
     *   	"lastPage": true,
     *   	"totalPage": 1,
     *   	"pageSize": 20,
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
	public void list() {
		Problem form = getQueryBean(Problem.class);
		form.setProject(getBusProject());
		Page<Problem> page = srv.paginate(getPaginator(), form);
		renderJson(Ret.ok("page", page));
	}
	
	/**
     * @api {post} /problem/importExcel 2.上传导入文件
     * @apiGroup problem
     * @apiVersion 1.0.0
     *
     * @apiParam {Multipart} file <code>必须参数</code> 文件
     * 
     * @apiSuccess {Integer} okCount 成功记录数
     * @apiSuccess {Integer} failCount 失败记录数
     * @apiSuccess {String} fileName 导入文件保存路径
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "okCount":2,
     *   "failCount":0,
     *   "fileName":"/problem/20180913/import_180329rYJrwr.xlsx"
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */

	public void importExcel() {
		UserPrincipal user = getProjectUser();
		UploadFile uf = null;
		try {
			uf = getFile("file", srv.getExcelTempDir());
			Ret ret = srv.importExcel(uf, user);
			renderJson(ret);
		} catch (Exception e) {
			LogKit.error("导入问题文件出现异常", e);
			if (uf != null) {
				uf.getFile().delete();
			}
			renderJson(Ret.fail("msg", "导入失败，请联系管理员"));
		}
	}
	
	/**
     * @api {post} /problem/exportExcel 3.生成导出文件
     * @apiGroup problem
     * @apiVersion 1.0.0
     * 
     * @apiParam {String} description <code>必须参数</code> 导出备注
     * @apiParam {String} type 问题类型
     * @apiParam {String} category 问题类别
     * @apiParam {String} specialty 专业
     * @apiParam {String} status 状态
     * @apiParam {String} belongsGroup 所属团组
     * 
     * @apiSuccess {Long} id 记录ID
     * @apiSuccess {String} fileName 文件名称
     * @apiSuccess {String} remark 导出备注
     * @apiSuccess {String} exportAt 导出时间
     * @apiSuccess {Integer} status 状态(0:已发起,1:已完成)
     * 
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "data":{
     *   	"id": 1,
	 *		"fileName": null,
     *      "remark": "项目名称质量安全问题统计表",
     *      "exportAt": "2018-07-20",
     *      "status": 0
     *   }
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	public void exportExcel() {
		Problem form = getQueryBean(Problem.class);
		form.setProject(getBusProject());
		Ret ret = srv.exportExcel(form, getProjectUser());
		renderJson(ret);
	}
	
	/**
     * @api {get} /problem/exportRecords 4.获取导出记录
     * @apiGroup problem
     * @apiVersion 1.0.0
     *
     * @apiSuccess {Long} id 导出记录ID
     * @apiSuccess {String} fileName 文件名称
     * @apiSuccess {String} remark 导出备注
     * @apiSuccess {String} exportAt 导出时间
     * @apiSuccess {Integer} status 状态(0:已发起,1:已完成)
     *
     * @apiSuccessExample {json} Success-Response:
     * 
     * {
     *   "state": "ok",
	 *   "data":[
	 *	 	{
	 *			"id": 1,
	 *			"fileName": "/problem/20180913/export_180329rYJrwr.xlsx",
     *      	"remark": "项目名称质量安全问题统计表",
     *      	"exportAt": "2018-07-20",
     *      	"status": 1
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
	public void exportRecords() {
		UserPrincipal user = getProjectUser();
		List<ExportDTO> list = srv.findExportRecord(user);
		renderJson(Ret.ok("data", list));
	}
	
	/**
     * @api {get} /problem/exportRecord/{id} 5.获取导出状态
     * @apiGroup problem
     * @apiVersion 1.0.0
     * 
     * @apiParam {Long} id <code>必须参数</code> 导出记录ID
     *
     * @apiSuccess {Long} id 导出记录ID
     * @apiSuccess {String} fileName 文件名称
     * @apiSuccess {String} remark 导出备注
     * @apiSuccess {String} exportAt 导出时间
     * @apiSuccess {Integer} status 状态(0:已发起,1:已完成)
     *
     * @apiSuccessExample {json} Success-Response:
     * 
     * {
     *   "state": "ok",
	 *   "data":{
	 *		"id": 1,
	 *		"fileName": "/problem/20180913/export_180329rYJrwr.xlsx",
     *      "remark": "项目名称质量安全问题统计表",
     *      "exportAt": "2018-07-20",
     *      "status": 1
     *   }
	 * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	public void exportRecord() {
		ExportDTO record = srv.getExportRecord(getParaToLong());
		renderJson(Ret.ok("data", record));
	}
	
	/**
     * @api {get} /problem/downloadExcel/{id} 6.下载导出文件
     * @apiGroup problem
     * @apiVersion 1.0.0
     * 
     * @apiParam {Long} id <code>必须参数</code> 导出文件ID
     *
     * @apiErrorExample {html} Error-Response:
     * <html>
     *   404 Not Found
     * </html>
     */
	public void downloadExcel() {
		Ret ret = srv.downloadExcel(getParaToLong());
		if (ret.isOk()) {
			File file = new File(ret.getStr("path"));
			renderFile(file, ret.getStr("name"));
		} else {
			renderError(404);
		}
	}
	
	/**
     * @api {get} /problem/deleteImport/{id} 7.删除导入问题
     * @apiGroup problem
     * @apiVersion 1.0.0
     *
     * @apiParam {Long} id <code>必须参数</code> 导入问题ID
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "msg":"导入问题删除成功"
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	@Before(Tx.class)
	public void deleteImport() {
		UserPrincipal user = getProjectUser();
		Ret ret = srv.deleteImport(getParaToLong(), user);
		renderJson(ret);
	}
	
	/**
     * @api {get} /problem/deleteExport/{id} 8.删除导出文件
     * @apiGroup problem
     * @apiVersion 1.0.0
     *
     * @apiParam {Long} id <code>必须参数</code> 导出文件ID
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "msg":"导出文件删除成功"
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	@Before(Tx.class)
	public void deleteExport() {
		UserPrincipal user = getProjectUser();
		Ret ret = srv.deleteExport(getParaToLong(), user);
		renderJson(ret);
	}
	
}
