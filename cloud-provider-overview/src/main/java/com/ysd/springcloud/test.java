package com.ysd.springcloud;

import com.jfinal.kit.PropKit;

/**
 * @author daixin
 * @create 2020/6/9 20:02
 */
public class test {
  public static void main(String[] args) {
//    File file = new File("cloud-provider-overview\\src\\main\\resources\\config.properties");
//    PropKit.use(file);
    PropKit.use("config.properties");
    System.out.println(PropKit.get("type")+PropKit.getBoolean("app.devMode"));
//    System.out.println(System.getProperty("user.dir"));
  }
}
