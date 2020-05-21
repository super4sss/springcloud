package com.ysd.springcloud.kit;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author daixin
 * @create 2020/4/27 18:41
 */
public class Kit {

  public boolean isValid(String str) {
    boolean flag = false;
    List<String> list = new ArrayList();
    List<String> list1 = new ArrayList();
    Map map = new HashMap();
    list.add("(");
    list.add("[");
    list.add("{");
    map.put("(", ")");
    map.put("[", "]");
    map.put("{", "}");
    char[] charArr = str.toCharArray();
    if (charArr.length==0){
      return true;
    }
    if (charArr.length==1){
      return false;
    }
    for (char b : charArr) {
      //匹配开括号就置入开括号堆
      if (list.contains(String.valueOf(b))) {
        list1.add(String.valueOf(b));
      }
      else{
        //匹配闭括号时开括号堆为空，直接返回假
        if (list1.size()==0){
          return  false;
        }
        //闭括号和最近一个开括号相等，置真，移除匹配开括号
        if(map.get(list1.get(list1.size()-1)).equals(String.valueOf(b))){
          list1.remove(list1.size()-1);
          flag=true;
          //闭括号和最近一个开括号不相等，直接返回假
        }else{
          flag=false;
          return flag;
        }
      }
    }
    //全匹配完后开括号堆不为空返回假
    if (list1.size()!=0){
      return false;
    }
    return flag;
  }








}
