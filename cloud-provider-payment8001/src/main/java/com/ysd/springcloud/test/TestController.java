package com.ysd.springcloud.test;

import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author daixin
 * @create 2020/4/21 16:22
 */

@RestController
@RequestMapping("/test1")
public class TestController {
  @Autowired
  private TestService sr ;

  @Value("${server.port}")
  private String serverPort;
  @GetMapping("/payment/get/{id}")
//  @RequestMapping(value = "/1",method = RequestMethod.GET)
  public Ret test(@PathVariable("id") Long id){
    Ret ret = Ret.ok("data", Db.find("select* from pro_vrshow"));
    return ret ;

  }
  @GetMapping("/test2")
//  @RequestMapping(value = "/1",method = RequestMethod.GET)
  public Ret test(){
    Ret ret = Ret.ok("data", Db.find("select* from pro_vrshow"));
    return ret ;

  }



}
