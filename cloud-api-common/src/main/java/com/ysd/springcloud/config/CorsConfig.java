//package com.ysd.springcloud.config;
//
///**
// * @author daixin
// * @create 2020/5/19 11:08
// */
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.filter.CorsFilter;
///**
// * 实现基本的跨域请求
// * @author linhongcun
// *
// */
//
//@Configuration
//public class CorsConfig {
//
//  private CorsConfiguration buildConfig() {
//    CorsConfiguration corsConfiguration = new CorsConfiguration();
//    corsConfiguration.addAllowedOrigin("*");
//    corsConfiguration.addAllowedHeader("*");
//    corsConfiguration.addAllowedMethod("*");
//    corsConfiguration.setAllowCredentials(true);
//    return corsConfiguration;
//  }
//
//  @Bean
//  public CorsFilter corsFilter() {
//    System.out.println("------------------------------");
//    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//    source.registerCorsConfiguration("/**", buildConfig());
//    return new CorsFilter(source);
//  }
//
//}
