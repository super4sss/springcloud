package com.ysd.springcloud.interceptor;

import com.ysd.springcloud.config.MyInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * @author daixin
 * @create 2020/5/18 17:44
 */
@Configuration
public class WebMvcConfigurer extends WebMvcConfigurationSupport {
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    //'/*'匹配一个请求
    registry.addInterceptor(new MyInterceptor()).addPathPatterns("/**");

    WebMvcConfigurer.super.addInterceptors(registry);
  }
}
