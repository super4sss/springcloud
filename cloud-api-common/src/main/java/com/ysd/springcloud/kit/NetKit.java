package com.ysd.springcloud.kit;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

/**
 * @author daixin
 * @create 2020/5/12 17:34
 */
public class NetKit {

  public static Session session;
  public static Channel channel;
  public static void SSh() throws Exception {
    try {
      String Sshuser = "mojianghui";
      String SShPassword = "ysd2018";
      String SSHip = "119.23.208.205";
      int SShPort = 22;
      JSch jsch = new JSch();
      session = jsch.getSession(Sshuser, SSHip, SShPort);
      session.setPassword(SShPassword);
      session.setConfig("StrictHostKeyChecking", "no");
      session.connect();//

      //建立通道
      channel = session.openChannel("session");
      channel.connect();
      //通过ssh连接到mysql机器
      int assinged_port = session.setPortForwardingL(9253, "172.18.148.80", 10010);

//      session.setPortForwardingL(17364, "119.23.208.205", 3306);//端口映射 转发
//      session.setPortForwardingL("119.23.208.205", 9253,"172.18.148.80",10010);//端口映射 转发

//      System.out.println("localhost:" + assinged_port);
    }catch (Exception e) {
      e.printStackTrace();
      System.out.println("ssh连接失败");
    }
  }

  public static void close() {
    if (session != null && session.isConnected() ) {
      session.disconnect();
    }

    if (channel != null && session.isConnected() ) {
      channel.disconnect();
    }
  }

}
