package com.ysd.springcloud.test;

import com.jfinal.core.Controller;

/**
 * @author daixin
 * @create 2020/5/21 10:16
 */
public class JController extends Controller {
  public  void  index(){
    renderJson("hello");
  }
}
