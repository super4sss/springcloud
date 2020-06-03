package com.ysd.overview.api.schedule;

import java.util.Map;

import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.ysd.overview.common.controller.BaseController;

/**
 * 计划进度 action层
 * @author Administrator
 * @date 2019年7月8日
 *
 */
public class ScheduleController extends BaseController{

	ScheduleService srv = ScheduleService.me;
	
	/**
     * @api {get} /schedule/getSchedule 1.计划进度分月统计
     * @apiGroup schedule
     * @apiVersion 2.0.0
     *  @apiHeader {String} Auth-Token-Overview 授权码
     * 
     * @apiSuccess taskTotal 任务总数
     * @apiSuccess taskTodo 待处理
     * @apiSuccess taskDone 已完成
     * @apiSuccess {Integer} total 总数
     * @apiSuccess {String} month 年月
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "data": {
	 *       "taskTotal": [
	 *           {
	 *               "total": 19,
	 *               "month": "2018-11"
	 *           },
	 *       ],
	 *       "taskTodo": [
	 *           {
	 *               "total": 15,
	 *               "month": "2018-11"
	 *           },
	 *       ],
	 *       "taskDone": [
	 *           {
	 *               "total": 2,
	 *               "month": "2018-11"
	 *           },
	 *       ]
	 *   },
     *	"state": "ok"
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	public void getSchedule(){
		String project = getBusProject();
		if(StrKit.isBlank(project)){
			 renderJson(Ret.fail("msg", "参数有误"));
			 return;
		}
		Map<String,Object> data = srv.scheduleStatistics(project);
		renderJson(Ret.ok("data", data));
	}
	
}
