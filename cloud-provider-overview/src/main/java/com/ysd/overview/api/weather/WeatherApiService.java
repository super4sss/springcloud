package com.ysd.overview.api.weather;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.client.fluent.Request;

import com.jfinal.kit.JsonKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.ysd.overview.api.common.CacheManager;
import com.ysd.overview.common.kit.ObjKit;

/*
 * File WeatherApiService.java
 * -------------------------------
 * 天气服务业务处理
 */
public class WeatherApiService{

	public static final WeatherApiService me = new WeatherApiService();
	private static final Log log = Log.getLog(WeatherApiService.class);
	private static final String CACHE_KEY = "weather__cache_key";

	public Ret getWeather(String project, String city) {
		if(StrKit.isBlank(project)){
			return Ret.fail("msg", "未授权，请联系管理员");
		}
		if(StrKit.isBlank(city)){
			return Ret.fail("msg", "参数错误，请检查");
		}
		Object cache = CacheManager.get(CACHE_KEY);
		if(cache == null){
			Ret ret = execute(city);
			if(ret.isOk()) CacheManager.put(CACHE_KEY, ret);
			return ret;
		}
		return (Ret) cache;
	}

	private Ret execute(String city) {
		String url = getRequestPath(city);
		if(log.isDebugEnabled()){
			log.debug("Request to " + url);
		}
		String response = null;
		try{
			response = Request.Get(url).execute().returnContent().asString();
		}catch(Exception e){
			log.error("接口请求异常", e);
			return Ret.fail("msg", "调用应用接口失败");
		}
		Map<String,Object> map = json2Map(response);
		if(ObjKit.empty(map)){
			return Ret.fail("msg", "调用应用接口结果数据转换失败");
		}
		Object obj = map.get("error_code");
		if(ObjKit.empty(obj)){
			return Ret.fail("msg", "对接应用接口失败");
		}
		int code = NumberUtils.toInt(obj.toString());
		if(code != 0){
			return Ret.fail("msg", map.get("reason"));
		}
		return Ret.ok("data", map.get("result"));
	}

	/*将json字符串转为map*/
	@SuppressWarnings("unchecked")
	private Map<String, Object> json2Map(String response) {
		try{
			return JsonKit.parse(response, Map.class);
		}catch(Exception e){
			log.error("Json转换失败", e);
			return null;
		}
	}

	/*获取请求全路径*/
	private String getRequestPath(String city) {
		StringBuilder url = new StringBuilder();
		url.append(getWeatherApi());
		url.append("?city=").append(city);
		url.append("&key=").append(getWeatherKey());
		return url.toString();
	}

	/*获取天气接口的key*/
	private String getWeatherKey() {
		return PropKit.get("weather.key");
	}

	/*获取天气接口基础路径*/
	private String getWeatherApi() {
		return PropKit.get("weather.api");
	}

	public void session(HttpServletRequest request){
		request.setAttribute("weather", "天气接口对象");
	}


	
}
