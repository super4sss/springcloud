package com.ysd.springcloud.controller;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import com.ysd.springcloud.interceptor.CrossDomainInterceptor;

/**
 * @author daixin
 * @create 2020/5/14 15:38
 */
//@Before(CrossDomainInterceptor.class)
public abstract class BaseController1 extends Controller {
  @Autowired
  public HttpServletRequest request;

  @Autowired
  public HttpServletResponse response;

  public String getPara(String name) {
    return request.getParameter(name);
  }

  //获取项目
  public String getProject(){
//    String token=getPara("Auth-Token-Overview");
//    System.out.println(getPara("Auth-Token-Overview"));
    String token=request.getHeader("Auth-Token-Overview");
    System.out.println(request.getHeader("Auth-Token-Overview"));
//    String token="66a0c072cc544a1683b105a58da7f729";

    String project = Db.find("select channel from sys_app where id = (select appId from sys_app_token where authToken = ?)",token).get(0).get("channel");
    System.out.println(project);
    return project;


  }
}
