//package com.ysd.springcloud.service;
//
//import com.google.common.collect.ImmutableMap;
//import com.jfinal.kit.JsonKit;
//import com.jfinal.kit.PropKit;
//import com.jfinal.kit.Ret;
//import org.apache.http.client.fluent.Request;
//
//import java.util.Map;
//
///**
// * @author daixin
// * @create 2020/5/13 12:13
// */
//public class UcenterService {
//
//  public static final UcenterService me = new UcenterService();
//
//  public Ret getLoginUser(String cookies) {
//    return execute("user/get_logined_status", ImmutableMap.of("cookies", cookies));
//  }
//
//
//  public Ret execute(String method, Map<String, String> params) {
//    StringBuilder url = new StringBuilder();
//    url.append(getPath()).append("/").append(method).append("/");
//    int index = 0;
//    for (String key : params.keySet()) {
//      url.append((index++ == 0 ? "?" : "&"));
//      url.append(key).append("=").append(params.get(key));
//    }
//
////    if (log.isDebugEnabled()) {
////      log.debug("Request to " + url.toString());
////    }
//
//    String response = null;
//    try {
//      response = Request.Get(url.toString())
//        .execute().returnContent().asString();
//    } catch (Exception e) {
////      log.error("Http请求异常", e);
//      return Ret.fail("msg", "调用用户中心接口失败");
//    }
//
//    Map<String, Object> map = json2Map(response);
//    if (com.ysd.springcloud.kit.ObjKit.empty(map)) {
//      return Ret.fail("msg", "调用用户中心接口结果数据转换失败");
//    }
//
//    String code = map.get("result") + "";
//    if (!"0".equals(code)) {
//      return Ret.fail("msg", "对接用户中心接口失败: " + map.get("message"));
//    }
//
//    return Ret.ok("data", map.get("data"));
//  }
//
//  private Map<String, Object> json2Map(String json) {
//    try {
//      return JsonKit.parse(json, Map.class);
//    } catch (Exception e) {
////      log.error("Json转换异常", e);
//      return null;
//    }
//  }
//
//  private String getPath() {
//    System.out.println("test:"+PropKit.get("ucenter.path"));
//    return PropKit.get("ucenter.path");
//
//  }
//}
