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
public class PaymentMain8002 {
  public static void main(String[] args) {
    SpringApplication.run(PaymentMain8002.class, args);
  }
}
