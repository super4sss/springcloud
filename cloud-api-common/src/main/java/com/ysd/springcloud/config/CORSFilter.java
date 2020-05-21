//package com.ysd.springcloud.config;
//
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.annotation.WebFilter;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
///**
// * @author daixin
// * @create 2020/5/19 16:05
// */
//@WebFilter(urlPatterns = "/*", filterName = "CORSFilter")
//public class CORSFilter extends OncePerRequestFilter {
//  @Override
//  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
////    response.addHeader("Access-Control-Allow-Origin", "*");
////    response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
////    response.addHeader("Access-Control-Allow-Headers", "Content-Type");
////    response.addHeader("Access-Control-Max-Age", "1800");//30 min
////    filterChain.doFilter(request, response);
//
//
//
//
//    response.addHeader("Access-Control-Allow-Origin", "*");
//    response.addHeader("Access-Control-Allow-Methods", "POST,GET,OPTIONS,DELETE");
//    response.addHeader("Access-Control-Allow-Headers", "Auth-Token-Overview");
//    if ("OPTIONS".equals(request.getMethod())) {
//      response.setStatus(HttpServletResponse.SC_ACCEPTED);
//
//
//
//    }
//    filterChain.doFilter(request, response);
//  }
//}
