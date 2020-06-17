package com.ysd.springcloud.filter;

import com.jfinal.plugin.activerecord.Db;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * @author daixin
 * @create 2020/6/16 16:26
 */
@WebFilter( filterName = "DpFilter")
public class DpFilter implements Filter {
  @Override
  public void init(FilterConfig filterConfig) throws ServletException {

  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    Db.find("select 1");
  }

  @Override
  public void destroy() {

  }
}
