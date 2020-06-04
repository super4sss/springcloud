package com.ysd.springcloud.api.weather;

import com.jfinal.kit.Ret;
import com.ysd.springcloud.common.controller.BaseController;

/*
 * File WeatherApiController.java
 * ----------------------------------
 * 天气访问接口
 */
public class WeatherApiController extends BaseController{

	WeatherApiService srv = WeatherApiService.me;
	
	/**
     * @api {get} /weather/getWeather?city={city} 1.通过城市名称查询天气预报情况
     * @apiGroup weather
     * @apiVersion 2.0.0
     * @apiHeader {String} Auth-Token-Overview 授权码
     * 
     * @apiParam {String} city <code>必须参数</code> 城市
     * 
     * @apiSuccess {Object} realtime  当前天气详情情况
     * @apiSuccess {string} info 天气情况，如：晴、多云
     * @apiSuccess {string} wid 天气标识id，可参考小接口2
     * @apiSuccess {string} temperature 温度，可能为空
     * @apiSuccess {string} humidity 湿度，可能为空
     * @apiSuccess {string} direct 风向，可能为空
     * @apiSuccess {string} power 风力，可能为空
     * @apiSuccess {string} aqi 空气质量指数，可能为空
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "data": {
	 *       "realtime": {
	 *           "wid": "02",
	 *           "temperature": "26",
	 *           "direct": "东南风",
	 *           "aqi": "22",
	 *           "humidity": "95",
	 *           "power": "2级",
	 *           "info": "阴"
	 *       },
	 *       "city": "广州",
	 *       "future": [
	 *           {
	 *               "date": "2019-07-04",
	 *               "wid": {
	 *                   "night": "04",
	 *                   "day": "04"
	 *               },
	 *               "temperature": "26/33℃",
	 *               "weather": "雷阵雨",
	 *               "direct": "持续无风向"
	 *           },
	 *      ]
	 *   },
	 *  "state": "ok"
     *   
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	public void getWeather(){
		String project = getBusProject();
		String city = getPara("city");
		Ret ret = srv.getWeather(project,city);
		renderJson(ret);
	}
}
