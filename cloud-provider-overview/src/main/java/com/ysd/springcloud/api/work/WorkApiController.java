package com.ysd.springcloud.api.work;

import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.ysd.springcloud.api.work.support.LaborService;
import com.ysd.springcloud.api.work.support.LaborVenService;
import com.ysd.springcloud.common.controller.BaseController;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 施工数据接口
 */
@Slf4j
public class WorkApiController extends BaseController {

	WorkApiService srv = WorkApiService.me;
	
	/**
     * @api {get} /work/realtime 1.实时总数据
     * @apiGroup work
     * @apiVersion 1.0.0
     * @apiHeader {String} Auth-Token-Overview 授权码
     * 
     * @apiSuccess {Object} worker 工人
     * @apiSuccess {Object} schedule 进度
     * @apiSuccess {Object} problem 问题
     * @apiSuccess (worker) {Integer} total 总人数
     * @apiSuccess (worker) {Integer} managementSum 管理人员
     * @apiSuccess (worker) {Integer} workerSum 工人
     * @apiSuccess (schedule) {String} startTime 开工时间
     * @apiSuccess (schedule) {String} endTime 竣工时间
     * @apiSuccess (schedule) {Long} productionDays 生产天数
     * @apiSuccess (schedule) {Long} remainingDays 剩余天数
     * @apiSuccess (schedule) {String} completionRates 完成率
     * @apiSuccess (problem) {Integer} qualityFind 质量问题发现个数
     * @apiSuccess (problem) {Integer} qualitySolve 质量问题解决个数
     * @apiSuccess (problem) {Integer} safetyFind 安全问题发现个数
     * @apiSuccess (problem) {Integer} safetySolve 安全问题解决个数
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "data":{
     *   	"worker":{
     *   		"total":200,
     *   		"managementSum":100,
     *   		"workerSum":100,
     *   	},
     *   	"schedule":{
     *   		"startTime":"2018-08-16",
     *   		"endTime":"2018-11-24",
     *   		"productionDays":20,
     *   		"remainingDays":80,
     *   		"completionRates":"20%"
     *   	},
     *   	"problem":{
     *   		"qualityFind":5,
     *   		"qualitySolve":2,
     *   		"safetyFind":5,
     *   		"safetySolve":2
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
	public void realtime() {
		Map<String, Object> data = srv.realtime(getBusProject());
		renderJson(Ret.ok("data", data));
	}
	
	/**
     * @api {get} /work/workerOnWeek 2.本周人数统计
     * @apiGroup work
     * @apiVersion 1.0.0
     * @apiHeader {String} Auth-Token-Overview 授权码
     * 
     * @apiSuccess {Integer} planNum 计划人数
     * @apiSuccess {Integer} realNum 实际人数
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "data":{
     *   	"8月29日":{
     *   		"planNum":30,
     *   		"realNum":20
     *   	},
     *   	"8月30日":{
     *   		"planNum":45,
     *   		"realNum":10
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
	public void workerOnWeek() {
		String project = getBusProject();
		renderJson(LaborService.me.workerOnWeek(project));
	}
	
	/**
     * @api {get} /work/workerOnDay 3.当天人数统计
     * @apiGroup work
     * @apiVersion 1.0.0
     * @apiHeader {String} Auth-Token-Overview 授权码
     * 
     * @apiSuccess {Object} worker 工人
     * @apiSuccess {Object} management 管理人员
     * @apiSuccess {Object} live 场内实时
     * @apiSuccess {String} noPrintCertificate 未打印离场凭证
     * @apiSuccess {String} noSafetyEducation 未接受安全教育
     * @apiSuccess {String} needNameReg 实名登记待完善
     * @apiSuccess {String} certificateIsAboutToExpire 证书资质即将到期
     * @apiSuccess (worker) {String} today 今日
     * @apiSuccess (worker) {String} yesterday 昨日
     * @apiSuccess (worker) {String} left 已离场
     * @apiSuccess (management) {String} constructionUnit 建设单位
     * @apiSuccess (management) {String} builderUnit 施工单位
     * @apiSuccess (management) {String} supervisorUnit 监理单位
     * @apiSuccess (live) {String} worker 工人
     * @apiSuccess (live) {String} construction 建设
     * @apiSuccess (live) {String} builder 施工
     * @apiSuccess (live) {String} supervisor 监理
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "data":{
     *   	"worker":{
     *   		"today":"450/1118",
     *   		"yesterday":"373/1118",
     *   		"left":"177"
     *   	},
     *   	"management":{
     *   		"constructionUnit":"0/0",
     *   		"builderUnit":"0/0",
     *   		"supervisorUnit":"0/0"
     *   	},
     *   	"live":{
     *   		"worker":"225",
     *   		"construction":"0",
     *   		"builder":"0",
     *   		"supervisor":"0"
     *   	},
     *   	"noPrintCertificate":"178",
     *   	"noSafetyEducation":"0",
     *   	"needNameReg":"0",
     *   	"certificateIsAboutToExpire":"0"
     *   }
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	public void workerOnDay() {
		String project = getBusProject();
		renderJson(LaborService.me.workerOnDay(project));
	}
	
	/**
     * @api {get} /work/workerOnType 4.实时工种统计
     * @apiGroup work
     * @apiVersion 1.0.0
     * @apiHeader {String} Auth-Token-Overview 授权码
     * 
     * @apiSuccess {String} cardHolding 持卡数
     * @apiSuccess {String} punchCardCountYoday 当日打卡数
     * @apiSuccess {String} punchCardCountYesterday 昨日打卡数
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "data":{
     *   	"钢筋工":{
     *   		"cardHolding":"403",
     *   		"punchCardCountYoday":"164",
     *   		"punchCardCountYesterday":"111"
     *   	},
     *   	"木工":{
     *   		"cardHolding":"347",
     *   		"punchCardCountYoday":"181",
     *   		"punchCardCountYesterday":"150"
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
	public void workerOnType() {
		String project = getBusProject();
		renderJson(LaborService.me.workerOnType(project));
	}
	
	/**
     * @api {get} /work/getCraftWorker?carft={carft} 5.获取工种人员
     * @apiGroup work
     * @apiVersion 1.0.0
     * @apiHeader {String} Auth-Token-Overview 授权码
     * 
     * @apiParam {String} carft <code>必须参数</code> 工种|时间(yyyy-MM-dd)
     * 
     * @apiSuccess {String} projWorkNum 工号
     * @apiSuccess {String} name 姓名
     * @apiSuccess {String} appPhotoPath 头像地址
     * @apiSuccess {String} age 年龄
     * @apiSuccess {String} officeName 所属单位
     * @apiSuccess {String} groupName 班组
     * @apiSuccess {String} carftName 工种
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "data":[
	 *	 	{
	 *			"projWorkNum": "303",
	 *			"name": "张三",
     *      	"appPhotoPath": "http://www.xxx.com/personImage/123",
     *      	"age": "23",
     *      	"officeName": "广州劳务有限公司",
     *      	"groupName": "手工木工板组",
     *      	"carftName": "木工"
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
	public void getCraftWorker() {
		String carft = getPara("carft");
		if (StrKit.isBlank(carft)) {
			renderJson(Ret.fail("msg", "工种标识不存在，请检查"));
			return;
		}
		String project = getBusProject();
		renderJson(LaborService.me.getCraftWorker(project, carft));
	}
	
	/**
     * @api {get} /work/getMonitoring 6.获取施工监控
     * @apiGroup work
     * @apiVersion 1.0.0
     * @apiHeader {String} Auth-Token-Overview 授权码
     * 
     * @apiSuccess {String} name 摄像头名称
     * @apiSuccess {String} vcrPath 摄像头路径
     * @apiSuccess {String} picPath 摄像头图片
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "data":[
	 *	 	{
	 *			"name": "塔吊1",
     *      	"vcrPath": "http://www.xxx.com/vcr/123",
     *      	"picPath": "http://www.xxx.com/pic/123"
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
	public void getMonitoring() {
		String project = getBusProject();
		renderJson(srv.getMonitoring(project));
	}
	
	/**
     * @api {get} /work/getPMData 7.获取噪声和扬尘数据
     * @apiGroup work
     * @apiVersion 1.0.0
     * @apiHeader {String} Auth-Token-Overview 授权码
     * 
     * @apiSuccess {String} B03Real 噪声实时值
     * @apiSuccess {String} B03Hour 噪声时均值
     * @apiSuccess {String} PM10Real PM10实时值
     * @apiSuccess {String} PM10Hour PM10时均值
     * @apiSuccess {String} PM25Real PM25实时值
     * @apiSuccess {String} PM25Hour PM25时均值
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "data":{
     *   	"B03Real":"64.30",
     *   	"B03Hour":"66.88",
     *   	"PM10Real":"81.50",
     *   	"PM10Hour":"95.11",
     *   	"PM25Real":"49.80",
     *   	"PM25Hour":"58.81"
     *   }
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	public void getPMData() {
		String project = getBusProject();
		Map<String, String> data = LaborService.me.getPMData(project);
		renderJson(Ret.ok("data", data));
	}
	
	/**
     * @api {get} /work/green 8.获取绿色施工
     * @apiGroup work
     * @apiVersion 1.0.0
     * @apiHeader {String} Auth-Token-Overview 授权码
     * 
     * @apiSuccess {String} noiseControl 噪声控制值
     * @apiSuccess {String} noiseCurrent 噪声当前值
     * @apiSuccess {String} noiseUnit 噪声单位
     * @apiSuccess {String} dustControl 扬尘控制值
     * @apiSuccess {String} dustCurrent 扬尘当前值
     * @apiSuccess {String} dustUnit 扬尘单位
     * @apiSuccess {String} waterControl 节水控制值
     * @apiSuccess {String} waterCurrent 节水当前值
     * @apiSuccess {String} waterUnit 节水单位
     * @apiSuccess {String} electricControl 节电控制值
     * @apiSuccess {String} electricCurrent 节电当前值
     * @apiSuccess {String} electricUnit 节电单位
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "data":{
     *   	"noiseControl":"70",
     *   	"noiseCurrent":"65",
     *   	"noiseUnit":"dB",
     *   	"dustControl":"1.5",
     *   	"dustCurrent":"1.2",
     *   	"dustUnit":"m",
     *   	"waterControl":"300",
     *   	"waterCurrent":"150",
     *   	"waterUnit":"t",
     *   	"electricControl":"1000",
     *   	"electricCurrent":"300",
     *   	"electricUnit":"KWh"
     *   }
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	public void green() {
		Map<String, Object> data = srv.green(getBusProject());
		renderJson(Ret.ok("data", data));
	}
	
	/**
     * @api {get} /work/labor 9.随机劳务数据
     * @apiGroup work
     * @apiVersion 1.0.0
     * @apiHeader {String} Auth-Token-Overview 授权码
     * 
     * @apiSuccess {Integer} builders 施工人员
     * @apiSuccess {Integer} managers 管理人员
     * @apiSuccess {Integer} workers 工人
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "data":{
     *   	"builders":120,
     *   	"managers":38,
     *   	"workers":82
     *   }
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	public void labor() {
		Map<String, Object> data = srv.labor(getBusProject());
		renderJson(Ret.ok("data", data));
	}
	
	/**
     * @api {get} /work/schedule 91.随机进度数据
     * @apiGroup work
     * @apiVersion 1.0.0
     * @apiHeader {String} Auth-Token-Overview 授权码
     * 
     * @apiSuccess {String} startTime 开工时间
     * @apiSuccess {String} endTime 竣工时间
     * @apiSuccess {Long} productionDays 生产天数
     * @apiSuccess {Long} remainingDays 剩余天数
     * @apiSuccess {String} completionRates 完成率
     * @apiSuccess {Integer} qualityFind 质量问题发现个数
     * @apiSuccess {Integer} qualitySolve 质量问题解决个数
     * @apiSuccess {Integer} safetyFind 安全问题发现个数
     * @apiSuccess {Integer} safetySolve 安全问题解决个数
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "data":{
     *   	"startTime":"2018-08-16",
     *   	"endTime":"2018-11-24",
     *   	"productionDays":20,
     *   	"remainingDays":80,
     *   	"completionRates":"20%",
     *   	"qualityFind":5,
     *   	"qualitySolve":2,
     *   	"safetyFind":5,
     *   	"safetySolve":2
     *   }
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	public void schedule() {
		Map<String, Object> data = srv.schedule(getBusProject());
		renderJson(Ret.ok("data", data));
	}
	
	/**
     * @api {get} /work/getSurveillanceVideo 获取施工监控_v2
     * @apiGroup work
     * @apiVersion 2.0.0
     * @apiHeader {String} Auth-Token-Overview 授权码
     * 
     * @apiSuccess {String} name 摄像头名称
     * @apiSuccess {String} vcrPath 摄像头路径
     * @apiSuccess {String} picPath 摄像头图片
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "data":[
	 *	 	{
	 *			"name": "塔吊1",
     *      	"vcrPath": "http://www.xxx.com/vcr/123",
     *      	"picPath": "http://www.xxx.com/pic/123"
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
	/*静态数据接口*/
  LaborVenService sr =new LaborVenService();
	public void getSurveillanceVideo(){
    System.out.println(getAttr("appid")+"***************************************");
    if (getAttr("appid").equals("6")){
      log.info(sr.getSurveillanceVideo2("392").toJson());
      renderJson(sr.getSurveillanceVideo2("392"));
      return;
    }

		Ret ret = srv.getSurveillanceVideo(getBusProject());
//    System.out.println("project"+getBusProject());
//    System.out.println("project2"+getProjectUser().getProject());

    log.info(ret.toJson());
		renderJson(ret);
	}
	
	/**
     * @api {get} /work/getCheckingInCount?size={size} 获取考勤统计 
     * @apiGroup work
     * @apiVersion 2.0.0
     * @apiHeader {String} Auth-Token-Overview 授权码
     * 
     * @apiParam {Integer} size 显示数
     * 
     * @apiSuccess {Integer} id 	实体ID
     * @apiSuccess {String} gmt_date 日期
     * @apiSuccess {Integer} check_in_num 考勤总人数
     * @apiSuccess {Integer} scene_num 在场人数
     *
     * @apiSuccessExample {json} Success-Response:
     * {
	 *	    "data": [
	 *	        {
	 *	            "check_in_num": 212,
	 *	            "gmt_date": "2019-12-01",
	 *	            "id": 1,
	 *	            "scene_num": 298
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
	public void getCheckingInCount(){
		Ret ret = srv.getCheckingInCount(getBusProject(), getParaToInt("size", 10));
		renderJson(ret);
	}
	
	/**
     * @api {get} /work/getMonthSceneNumAvg 获取月平均在场人数
     * @apiGroup work
     * @apiVersion 2.0.0
     * @apiHeader {String} Auth-Token-Overview 授权码
     * 
     * @apiSuccess {date_str} gmt_date 年月
     * @apiSuccess {avg_val} avg_val 平均值
     * @apiSuccess {sum_val} sum_val 总值
     *
     * @apiSuccessExample {json} Success-Response:
	 *{
	 *    "data": {
	 *        "2019年01月": {
	 *            "gmt_date": "2019年01月",
	 *            "avg_val": 0,
	 *            "sum_val": 0
	 *        }
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
	public void getMonthSceneNumAvg(){
		Ret ret = srv.getMonthSceneNumAvg(getBusProject());
		renderJson(ret);
	}
	
	
}
