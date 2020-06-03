package com.ysd.springcloud.controller;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import com.ysd.springcloud.interceptor.CrossDomainInterceptor;

/**
 * @author daixin
 * @create 2020/5/14 15:38
 */
//@Before(CrossDomainInterceptor.class)
public abstract class BaseController1 extends Controller {

  public HttpServletRequest request = this.getRequest();


  public HttpServletResponse response = this.getResponse();

  public String getPara(String name) {
    return request.getParameter(name);
  }

  //获取项目
  public String getProject(){
//    String token=getPara("Auth-Token-Overview");
//    System.out.println(getPara("Auth-Token-Overview"));
//    String token=request.getHeader("Auth-Token-Overview");
    System.out.println(getHeader("Auth-Token-Overview"));
    String token="c4e04ff32c0c4b7e82d873db7ae40d1a";

    String project = Db.find("select channel from sys_app where id = (select appId from sys_app_token where authToken = ?)",token).get(0).get("channel");
    System.out.println(project);
    return project;


  }
}
