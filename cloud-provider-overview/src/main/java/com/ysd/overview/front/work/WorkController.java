package com.ysd.overview.front.work;

import com.jfinal.kit.LogKit;
import com.jfinal.kit.Ret;
import com.jfinal.upload.UploadFile;
import com.ysd.overview.common.auth.UserPrincipal;
import com.ysd.overview.common.controller.BaseController;

/**
 * 劳务工人控制器
 */
public class WorkController extends BaseController {

	WorkService srv = WorkService.me;
	
	public void index() {
		render("index.html");
	}
	
	public void importExcel() {
		UserPrincipal user = getProjectUser();
		UploadFile uf = null;
		try {
			uf = getFile("file", srv.getExcelTempDir());
			Ret ret = srv.importExcel(uf, user);
			renderJson(ret);
		} catch (Exception e) {
			LogKit.error("导入问题文件出现异常", e);
			if (uf != null) {
				uf.getFile().delete();
			}
			renderJson(Ret.fail("msg", "导入失败，请联系管理员"));
		}
	}
	
}
