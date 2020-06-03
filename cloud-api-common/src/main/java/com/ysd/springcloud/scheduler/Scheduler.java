package com.ysd.springcloud.scheduler;

import com.ysd.springcloud.kit.NetKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author daixin
 * @create 2020/6/2 17:58
 */
@Component
public class Scheduler{
  private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
@Autowired
  NetKit netKit;
  //每隔2秒执行一次
  @Scheduled(fixedRate = 60000)
  public void testTasks() {
    try {
      netKit.closeSSH();
      netKit.SSh();
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("ssh连接：" + dateFormat.format(new Date()));
  }
}
