package com.ysd.springcloud.filter;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.ysd.springcloud.kit.NetKit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author daixin
 * @create 2020/5/18 17:21
 */

@WebFilter(urlPatterns = "/*", filterName = "SsHFilter")
@Slf4j
public class CORSFilter implements Filter {
  @Autowired
  NetKit netKit;
  /**
   * 容器加载的时候调用
   */
  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    System.out.println("daixinPreFilter");
  }
  /**
   * 请求被拦截的时候进行调用
   */
  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {


    // do something 处理request 或response
//    if (!NetKit.session.isConnected()) {
//      try {
//        NetKit.SSh();
//        System.out.println("ssh重新连接");
//      } catch (Exception e) {
//        e.printStackTrace();
//      }
//    } else {
//      System.out.println("ssh已连接");
//    }


//跨域设置
    HttpServletRequest request = (HttpServletRequest) servletRequest;
    HttpServletResponse response = (HttpServletResponse) servletResponse;

//    filterChain.doFilter(request, response);
    response.addHeader("Access-Control-Allow-Origin", "*");
    response.addHeader("Access-Control-Allow-Methods", "POST,GET,OPTIONS,DELETE");
    response.addHeader("Access-Control-Allow-Headers", "Auth-Token-Overview");
    String method = request.getMethod();
    System.out.println(method);
    System.out.println(request.getRequestURI());
    if ("OPTIONS".equals(request.getMethod())) {
//      response.setStatus(HttpServletResponse.SC_ACCEPTED);
      response.setStatus(HttpServletResponse.SC_OK);
      log.info(request.getRequestURI());
      return;
    }
    String token =request.getHeader("Auth-Token-Overview");
    if(!StrKit.isBlank(token)) {


      if (!StrKit.isBlank(Db.find("select channel from sys_app where id = (select appId from sys_app_token where authToken = ?)", token).get(0).get("channel"))) {
        System.out.println("ok");
//        filterChain.doFilter(request, response);

//        Timer timer = new Timer();
//
//        timer.schedule(new TimerTask() {
//
//          public void run() {
//            try {
//              netKit.SSh();
//            } catch (Exception e) {
//              e.printStackTrace();
//            }
//            System.out.println("ssh");
//
//
//          }
//
//        }, 1000, 6000);
        filterChain.doFilter(request, response);
      }
    }
  }


//  @Override
//  public void doFilter(ServletRequest request, ServletResponse response,
//                       FilterChain chain) throws IOException, ServletException {
//    HttpServletRequest req = (HttpServletRequest) request;
//    HttpServletResponse rep = (HttpServletResponse) response;
//
//    //设置允许跨域的配置
//    // 这里填写你允许进行跨域的主机ip（正式上线时可以动态配置具体允许的域名和IP）
//    rep.setHeader("Access-Control-Allow-Origin", "*");
//    // 允许的访问方法
//    rep.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE, PATCH");
//    // Access-Control-Max-Age 用于 CORS 相关配置的缓存
//    rep.setHeader("Access-Control-Max-Age", "3600");
//    rep.setHeader("Access-Control-Allow-Headers", "token,Origin, X-Requested-With, Content-Type, Accept");
//
//
//    response.setCharacterEncoding("UTF-8");
//    response.setContentType("application/json; charset=utf-8");
//    String token = req.getHeader("token");//header方式
////    ResultInfo resultInfo = new ResultInfo();
//    boolean isFilter = false;
//
//
//    String method = ((HttpServletRequest) request).getMethod();
//    if (method.equals("OPTIONS")) {
//      rep.setStatus(HttpServletResponse.SC_OK);
//    } else {
//
//
//      if (null == token || token.isEmpty()) {
////        resultInfo.setCode(Constant.UN_AUTHORIZED);
////        resultInfo.setMsg("用户授权认证没有通过!客户端请求参数中无token信息");
//      } else {
//        if (TokenUtil.volidateToken(token)) {
//          resultInfo.setCode(Constant.SUCCESS);
//          resultInfo.setMsg("用户授权认证通过!");
//          isFilter = true;
//        } else {
//          resultInfo.setCode(Constant.UN_AUTHORIZED);
//          resultInfo.setMsg("用户授权认证没有通过!客户端请求参数token信息无效");
//        }
//      }
//      if (resultInfo.getCode() == Constant.UN_AUTHORIZED) {// 验证失败
//        PrintWriter writer = null;
//        OutputStreamWriter osw = null;
//        try {
//          osw = new OutputStreamWriter(response.getOutputStream(),
//            "UTF-8");
//          writer = new PrintWriter(osw, true);
//          String jsonStr = JSON.toJSONString(resultInfo);
//          writer.write(jsonStr);
//          writer.flush();
//          writer.close();
//          osw.close();
//        } catch (UnsupportedEncodingException e) {
////          logger.error("过滤器返回信息失败:" + e.getMessage(), e);
//        } catch (IOException e) {
////          logger.error("过滤器返回信息失败:" + e.getMessage(), e);
//        } finally {
//          if (null != writer) {
//            writer.close();
//          }
//          if (null != osw) {
//            osw.close();
//          }
//        }
//        return;
//      }
//
//      if (isFilter) {
////        logger.info("token filter过滤ok!");
//        chain.doFilter(request, response);
//      }
//    }
//  }



  /**
   * 容器被销毁的时候被调用
   */
  @Override
  public void destroy() {

  }
}

