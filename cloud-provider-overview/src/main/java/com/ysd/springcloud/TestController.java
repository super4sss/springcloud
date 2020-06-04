package com.ysd.springcloud;

import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author daixin
 * @create 2020/6/3 9:37
 */
@RestController
public class TestController extends Controller {
@GetMapping(value = "/hello")
  public  void hello(){
renderJson(Ret.ok("data",Db.find("select * from sys_app").get(0).getColumns().toString()));

  }

}
