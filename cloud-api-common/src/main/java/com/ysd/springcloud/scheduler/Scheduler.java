package com.ysd.springcloud.scheduler;

import com.jfinal.kit.PropKit;
import com.ysd.springcloud.kit.NetKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

/**
 * @author daixin
 * @create 2020/6/2 17:58
 */
@Component
@Lazy(false)
public class Scheduler{
  private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
@Autowired
  NetKit netKit;
  //每隔2秒执行一次
  @Scheduled(fixedRate = 60000)
  public void testTasks() {
//    System.out.println(PropKit.getBoolean("app.devMode"));
    if (PropKit.getBoolean("app.devMode")) {
      try {
        netKit.closeSSH();
        netKit.SSh();
      } catch (Exception e) {
        e.printStackTrace();
      }
//      System.out.println("ssh连接：" + dateFormat.format(new Date()));
    }
  }
}
