package com.ysd.springcloud.test;

import com.alibaba.fastjson.JSON;
import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import com.jfinal.upload.UploadFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * @author daixin
 * @create 2020/4/21 16:22
 */

@RestController
public class TestController extends Controller {
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
@PostMapping(value="/exportByTemplate")
  public void exportByTemplate(){
//    UploadFile templateFile = getFile("templateFile");
    UploadFile templateFile = getFile();
  System.out.println(templateFile.getFileName());
    System.out.println("path:"+templateFile.getUploadPath());
    System.out.println(1111111);
    String s = getPara("map");
  System.out.println("s:"+s);
  HashMap<String, Object> map=JSON.parseObject(s, HashMap.class);
  System.out.println(map.toString());

  Ret ret = sr.exportByTemplate(templateFile,map);
//    renderFile(PropKit.get("system.picPath") + File.separator + "temp.docx");
//    renderFile( "G:\\\\xml解析文件\\\\\"+\"temp.docx");
    renderFile("G:\\xml解析文件\\temp.docx");
    renderJson(ret);

  }



}
