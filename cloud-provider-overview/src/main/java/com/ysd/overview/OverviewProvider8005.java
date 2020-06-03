package com.ysd.overview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * @author daixin
 * @create 2020/4/21 9:18
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
public class OverviewProvider8005 {
  public static void main(String[] args) {
    SpringApplication.run(OverviewProvider8005.class, args);
  }
}
