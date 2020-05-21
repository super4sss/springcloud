package com.ysd.springcloud.config;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author daixin
 * @create 2020/5/18 17:41
 */
public class MyInterceptor implements HandlerInterceptor {

  //方法执行前
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//    System.out.println("zhixing preHandle");
    return true;
  }

  //方法执行后
  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
//    System.out.println("zhixing postHandle");
  }

  //页面渲染前
  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
//    System.out.println("zhixing afterCompletion");
  }
}
