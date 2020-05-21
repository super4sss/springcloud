//package com.ysd.springcloud.service;
//
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.jfinal.kit.JsonKit;
//import com.jfinal.kit.PropKit;
//import com.jfinal.kit.Ret;
//import com.jfinal.kit.StrKit;
//import com.ysd.springcloud.dto.UserPrincipal;
//import org.apache.commons.lang3.math.NumberUtils;
//import org.apache.http.client.fluent.Request;
//
//import java.io.IOException;
//import java.util.Map;
///**
// * @author daixin
// * @create 2020/5/13 12:02
// */
//public class ClientService {
//
//  public static final ClientService me = new ClientService();
//
//  /**
//   * 执行客户端请求
//   * @param url		客户端请求路径
//   * @return
//   */
//  private Ret execute(String url) {
////    if(log.isDebugEnabled()){
////      log.debug("Request to " + url);
////    }
//    String response = null;
//    try {
//      response = Request.Get(url).execute().returnContent().asString();
//    } catch (IOException e) {
////      log.error("Http请求异常",e);
//      return Ret.fail("msg", "调用应用接口失败");
//    }
//    Map<String, Object> map = json2Map(response);
//    if (com.ysd.springcloud.kit.ObjKit.empty(map)) {
//      return Ret.fail("msg", "调用应用接口结果数据转换失败");
//    }
//    Object obj = map.get("code");
//    if(com.ysd.springcloud.kit.ObjKit.empty(obj)){
//      return Ret.fail("msg", "对接应用接口失败");
//    }
//    int code = NumberUtils.toInt(obj.toString());
//    if(code != 200){
//      return Ret.fail("msg", map.get("msg"));
//    }
//    return Ret.ok("data", map.get("data"));
//  }
//
//
//  public UserPrincipal getProjectInfo(UserPrincipal user) {
//    String paras = getApiPath() + "?action=GetSolutionInfo&solutionID=" + user.getProject();
//    Ret ret = execute(paras);
//    if (ret.isOk()) {
//      JSONObject obj = ret.getAs("data");
//      JSONArray ownArr = obj.getJSONArray("OwnerIDs");
//      user.setProject(obj.getString("ID"));
//      user.setProjectName(obj.getString("Name"));
//      user.setOwner(ownArr.contains(Integer.valueOf(user.getUser())));
//    }
//    return user;
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
//  private String getApiUrl(String queryParas) {
//    if (StrKit.isBlank(queryParas)) {
//      return getApiPath();
//    }
//    return getApiPath() + "?" + queryParas;
//  }
//
//  public static String getApiPath() {
//    return PropKit.get("api.path");
//  }
//
//}
