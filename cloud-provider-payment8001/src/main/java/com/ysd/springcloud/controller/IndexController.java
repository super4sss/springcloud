/**
 * $Id: IndexController.java,v 1.0 2019-08-09 09:56 chenmin Exp $
 */
package com.ysd.springcloud.controller;

import com.jfinal.core.Controller;
import com.jfinal.core.paragetter.Para;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author 陈敏
 * @version $Id: IndexController.java,v 1.1 2019-08-09 09:56 chenmin Exp $
 * Created on 2019-08-09 09:56
 * My blog： https://www.chenmin.info
 */
//@RouterPath("/index")
  @RestController
  @RequestMapping("/index")
public class IndexController extends Controller {

    public void index(@Para(value = "title", defaultValue = "") String title) {
//        List<Record> records = Db.find(Db.getSqlPara("theme.queryTheme", Kv.by("title", title)));
//        renderJson(records);
      renderJson("hello");
    }
}
