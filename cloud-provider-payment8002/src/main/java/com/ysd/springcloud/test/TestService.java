package com.ysd.springcloud.test;

import com.jfinal.kit.PropKit;
import com.jfinal.kit.Ret;
import com.jfinal.upload.UploadFile;
import com.ysd.springcloud.kit.ObjKit;
import com.ysd.springcloud.kit.WordRead;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author daixin
 * @create 2020/4/21 16:22
 */
@Service
public class TestService {

  public Ret test(Long id){


    Ret ret = new Ret();


    return ret;
  }
  public Ret exportByTemplate(UploadFile templateFile, HashMap map) {
  Iterator iterator = map.entrySet().iterator();
  Map newMap = new HashMap();
    while(iterator.hasNext()) {
    Map.Entry entry = (Map.Entry) iterator.next();
    Object key = entry.getKey();
    key = "{{"+key.toString()+"}}";
    //
    Object value = entry.getValue();
    value = validateValue(value);
    newMap.put(key,value);
  }
//    System.out.println(newMap.toString());

  //    Map EconomicMgConstPayMap = dfdf(constDO,amountMap);
  FileInputStream in = null;

//      System.out.println(templateFile.getFile().equals(null));


      try {
    in = new FileInputStream(templateFile.getFile());
  } catch (
  FileNotFoundException e) {
    e.printStackTrace();
    return Ret.fail("msg", "读取失败");
  }

  XWPFDocument hdt = null;
    try {
    hdt = new XWPFDocument(in);
  } catch (IOException e) {
    e.printStackTrace();
    return Ret.fail("msg", "读取失败");
  }
  //    File file = new File(PropKit.get("system.picPath") + File.separator + "temp.docx");
  //测试路径
//    System.out.println(templateFile.getFileName());
//    String fileName = "G:\\xml解析文件\\"+"temp.docx";
//    File file =new File(fileName);
  //时间转路径
  String time = String.valueOf((int)System.currentTimeMillis());
    //设置路径
//  String filepath = "G:\\xml解析文件\\uploadFile\\"+time+".docx";
//  String filepath = "D:\\ysdcloud\\xml解析文件\\temp.docx";
//  String filepath = "G:\\xml解析文件\\temp.docx";
    String filepath = PropKit.get("tempPath");
//    new Filepath().set("filePath", filepath).set("fileName", templateFile.getFileName()).save();
  File file =new File(filepath);
    try {
    file.createNewFile();
  } catch (
  IOException e) {
    e.printStackTrace();
  }
    try {
    new WordRead().replaceInWord(newMap,hdt,file);
  } catch (IOException e) {
    e.printStackTrace();
    return Ret.fail("msg", "导出失败");
  }

    return Ret.ok();

}

  //写个方法判断值是不是空，是不是字符串
  private Object validateValue(Object obj){
    obj = ObjKit.notEmpty(obj)?obj:"";
    if(obj instanceof java.sql.Timestamp){
      obj = getSimpleFormatDate((Date) obj, "yyyy-MM-dd");
      return obj;
    }
    else {obj =obj instanceof String ?obj:obj.toString();}
    return obj;
  }

  private static String getSimpleFormatDate(Date now, String pattern){
    SimpleDateFormat sdf = new SimpleDateFormat(pattern);
    return sdf.format(now);
  }


}
