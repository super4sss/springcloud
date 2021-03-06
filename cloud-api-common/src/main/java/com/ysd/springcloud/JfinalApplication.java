///**
// * $Id: JfinalApplication.java,v 1.0 2019-08-08 17:14 chenmin Exp $
// */
//package com.ysd.springcloud;
//
//import com.jfinal.aop.Interceptor;
//import com.jfinal.config.Routes;
//import com.jfinal.core.Controller;
//import com.jfinal.handler.Handler;
//import com.jfinal.render.Render;
//import com.ysd.springcloud.annotation.JfinalScan;
//import com.ysd.springcloud.annotation.RouterPath;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//
///**
// * @author 陈敏
// * @version $Id: JfinalApplication.java,v 1.1 2019-08-08 17:14 chenmin Exp $
// * Created on 2019-08-08 17:14
// * My blog： https://www.chenmin.info
// */
//@JfinalScan(
//        basePackages = "com.github.springcloud",
//        markerInterfaces = {
//                Controller.class,
//                Interceptor.class,
//                Routes.class,
//                Handler.class,
//                Render.class
//        },
//        annotationClass = RouterPath.class)
//@SpringBootApplication
//public class JfinalApplication {
//
//    public static void main(String[] args) {
//        SpringApplication.run(JfinalApplication.class, args);
//    }
//
//}
