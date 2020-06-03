package com.ysd.springcloud.test;

import com.jfinal.kit.Ret;
import com.ysd.springcloud.controller.BaseController1;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author daixin
 * @create 2020/5/22 17:34
 */
@RestController
public class TestController1  extends BaseController1 {
  @Resource
  private TestService sr ;


  @RequestMapping("/test2")
  public void vrShow(){
    renderJson(Ret.ok("data",sr.getVrShows("392", null).get(0).getColumns()));
  }
}

