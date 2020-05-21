package com.ysd.springcloud.test;

import com.jfinal.kit.Ret;
import com.jfinal.render.Render;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
/**
 * @author daixin
 * @create 2020/4/21 16:22
 */

@RestController
public class TestController {
  @Autowired
  private TestService sr ;

  @Value("${server.port}")
  private String serverPort;
  @GetMapping("/payment/get/{id}")
//  @RequestMapping(value = "/1",method = RequestMethod.GET)
  public Ret test(@PathVariable("id") Long id){

    Ret ret =sr.test(id);
    ret.set("port", serverPort);
    return ret;
//    return "nacos register, serverport=" + serverPort + "\t id:" + id;

  }



}
