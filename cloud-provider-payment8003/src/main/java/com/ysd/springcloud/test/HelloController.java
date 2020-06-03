package com.ysd.springcloud.test;

import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author daixin
 * @create 2020/5/21 10:16
 */
@RestController
@Clear
public class HelloController extends Controller { //不继承Controller,就是原生的Spring Boot,Jfinal的Aop也就不能用

//  @Autowired
//  private TestService sr;

//  @Before(Tx.class) //事务的用法
  @RequestMapping(value="/list/jfinal")
  public void index() {
    System.out.println("ok");
    renderJson("hello");
  }

}
