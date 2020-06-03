//package com.ysd.springcloud.listener;
//
//import com.ysd.springcloud.kit.NetKit;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import javax.servlet.ServletContextEvent;
//import javax.servlet.ServletContextListener;
//import javax.servlet.annotation.WebListener;
//import java.util.Timer;
//import java.util.TimerTask;
//
///**
// * @author daixin
// * @create 2020/6/2 11:44
// */
//
//
//  @WebListener
//  public class SSHListener implements ServletContextListener {
//
//  @Autowired
//  NetKit netKit;
//
//    public SSHListener() {
//      super();
//    }
//
//    /**
//     * @see ServletContextListener#contextInitialized(ServletContextEvent)
//     */
//    public void contextInitialized(ServletContextEvent arg0) {
//      System.out.println("Context initialized ... !");
////      try {
////        netKit = new NetKit();
////        netKit.SSh();// 监听到了 就装配文件
////      } catch (Throwable e) {
////        e.printStackTrace(); // error connecting SSH server
////      }
//      Timer timer = new Timer();
//
//      timer.schedule(new TimerTask() {
//
//        public void run() {
//          try {
//            netKit.SSh();
//          } catch (Exception e) {
//            e.printStackTrace();
//          }
//          System.out.println("ssh");
//
//
//        }
//
//      }, 1000, 6000);
//    }
//
//    /**
//     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
//     */
//    public void contextDestroyed(ServletContextEvent arg0) {
//      System.out.println("Context destroyed ... !");
//      netKit.closeSSH(); // disconnect
//    }
//
//  }
