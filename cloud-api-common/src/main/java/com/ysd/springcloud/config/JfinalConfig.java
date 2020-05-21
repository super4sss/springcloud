package com.ysd.springcloud.config;

import com.jfinal.json.IJsonFactory;
import com.jfinal.json.MixedJsonFactory;
import com.jfinal.render.IRenderFactory;
import com.jfinal.render.RenderFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author daixin
 * @create 2020/5/21 10:31
 */
@Configuration
public class JfinalConfig {
  @Bean
  @ConditionalOnMissingBean(IJsonFactory.class)
  public IJsonFactory jsonFactory(){
//    return new JacksonFactory();
    return new MixedJsonFactory();
  }

  @Bean
  @ConditionalOnMissingBean(IRenderFactory.class)
  public IRenderFactory renderFactory() {
    return new RenderFactory();
  }
}
