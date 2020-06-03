package com.ysd.springcloud.test;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.ysd.springcloud.kit.ObjKit;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author daixin
 * @create 2020/4/21 16:22
 */
@Service
public class TestService {



  //  public List<Record> getVrShows(String project) {
//    String sql = "select name,viewPath from pro_vrshow where project=? order by id desc";
//    List<Record> list = Db.find(sql, project);
//    return  list;
//  }
public List<Record> getVrShows(String project, String keyword) {
  String sql = "select name,viewPath from pro_vrshow where project=? order by id desc";
  List<Record> list = Db.find(sql, project);
  if (ObjKit.empty(list)) {
    return Collections.emptyList();
  }
  return list.stream().filter(r -> {
    String name = r.getStr("name");
    String path = r.getStr("viewPath");
    return StrKit.notBlank(name, path)
      && (StrKit.isBlank(keyword) || name.startsWith(keyword));
  }).collect(toList());
}


  public String gettest() {
  return "success";
  }
}
