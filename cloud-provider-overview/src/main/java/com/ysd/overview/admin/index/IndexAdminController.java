package com.ysd.overview.admin.index;

import com.jfinal.core.Controller;

/**
 * 后台管理首页
 */
public class IndexAdminController extends Controller {

	IndexAdminService srv = IndexAdminService.me;

	public void index() {
		setAttr("sysInfo", srv.getSysInfo());
		render("index.html");
	}
	
}
