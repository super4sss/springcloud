package com.ysd.springcloud.test;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jfinal.kit.StrKit;
import com.ysd.springcloud.kit.Kit;
import com.ysd.springcloud.kit.Kit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author daixin
 * @create 2020/4/27 17:24
 */
public class test1 {
  public void SShMysqltest() throws Exception {
//    try {
//      String Sshuser = "mojianghui";
//      String SShPassword = "ysd2018";
//      String SSHip = "119.23.208.205";
//      int SShPort = 22;
//      JSch jsch = new JSch();
//      Session session = jsch.getSession(Sshuser, SSHip, SShPort);
//      session.setPassword(SShPassword);
//      session.setConfig("StrictHostKeyChecking", "no");
//      session.connect();//连接
//      int assinged_port = session.setPortForwardingL(8856, "172.18.148.80", 9964);//端口映射 转发
//
//      System.out.println("localhost:" + assinged_port);
//    }catch (Exception e) {
//      e.printStackTrace();
//    }
  }




  public static void main(String[] args) {
//    Kit kit = new Kit();
//    System.out.println(kit.isValid("([]"));
//    String s ="{{}}}";
//    s = s.replaceAll("\\{\\}","");
//    System.out.println(s);

//    List list = new ArrayList();
//    List list1 = new ArrayList();
//    Map map = new HashMap();
//    list.add("(");
//    list.add("[");
//    list.add("{");
//    System.out.println(list.get(list.size()-1));

    try {
      new test1().SShMysqltest();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

