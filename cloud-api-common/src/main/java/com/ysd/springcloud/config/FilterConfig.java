package com.ysd.springcloud.config;

import com.ysd.springcloud.filter.CORSFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author daixin
 * @create 2020/5/18 17:19
 */
@Configuration
public class FilterConfig {

  @Bean
  public FilterRegistrationBean registrationBean() {
    FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new CORSFilter());
    filterRegistrationBean.addUrlPatterns("/*");
    return filterRegistrationBean;
  }


}

