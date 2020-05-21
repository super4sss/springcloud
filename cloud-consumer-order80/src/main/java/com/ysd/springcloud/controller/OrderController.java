package com.ysd.springcloud.controller;

import com.jfinal.kit.Ret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;
import javax.annotation.Resource;

/**
 * @author daixin
 * @create 2020/4/22 14:41
 */
@RestController
@Slf4j
public class OrderController {
  public static final String PAYMENT_URL="http://localhost:8002";


  @Resource
  private RestTemplate restTemplate;
  @Value("${service-url.nacos-user-service}")
  private String serverUrl;

  @GetMapping("/consumer/payment/get/{id}")
  public Ret getPayment(@PathVariable("id") Long id){

    log.info(serverUrl + "/payment/nacos/" + id);
    return restTemplate.getForObject(serverUrl+"/payment/get/"+id,Ret.class);

  }
  @GetMapping("/test")
  public int test(){
    return 2;
  }

}
