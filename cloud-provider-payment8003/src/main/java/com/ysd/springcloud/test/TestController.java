package com.ysd.springcloud.test;

import com.jfinal.kit.Ret;
import com.ysd.springcloud.controller.BaseController1;
import com.ysd.springcloud.kit.ObjKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author daixin
 * @create 2020/4/21 16:22
 */

@RestController
public class TestController extends BaseController1 {
  @Autowired
  private TestService sr ;

  @Value("${server.port}")
  private String serverPort;
  @GetMapping("/payment/get/{id}")
//  @RequestMapping(value = "/1",method = RequestMethod.GET)
  public Ret test(@PathVariable("id") Long id){

//    Ret ret =sr.getVrShows();
//    ret.set("port", serverPort);
//    return ret;
    Ret ret = new Ret();
    return ret.set("id", 6);
//    return "nacos register, serverport=" + serverPort + "\t id:" + id;

  }
  @RequestMapping("/test")
  public  void test1(){

    renderJson(Ret.ok("data", 1111));
  }
  @RequestMapping("/1")
  public  Ret test3(){

    return  Ret.ok("ss","ssss");
  }


  //未完善，自定义异常，缓存数据
  @RequestMapping("/commandPage/test1")
  public  Ret test2(){
//    String keyword = getPara("keyword");
//    List<Record> list = sr.getVrShows(getProject(),keyword);
//    return Ret.ok("data", list);
    List list =sr.getVrShows(getProject(),null);
    if (ObjKit.empty(list)){
      return Ret.fail("message","查询结果为空");
    }else {
      renderJson();
      return Ret.ok("data", sr.getVrShows(getProject(), null).get(0).getColumns());
    }
  }



}
