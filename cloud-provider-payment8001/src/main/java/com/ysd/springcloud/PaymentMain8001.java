package com.ysd.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


/**
 * @author daixin
 * @create 2020/4/21 9:18
 */
@SpringBootApplication
@EnableDiscoveryClient
public class PaymentMain8001 {
  public static void main(String[] args) {
    SpringApplication.run(PaymentMain8001.class, args);
  }
}
