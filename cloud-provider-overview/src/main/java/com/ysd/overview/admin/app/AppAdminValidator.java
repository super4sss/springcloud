package com.ysd.overview.admin.app;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

/**
 * 验证应用功能表单
 */
public class AppAdminValidator extends Validator {

	@Override
	protected void validate(Controller c) {
		setShortCircuit(true);

		validateRequiredString("app.channel", "msg", "渠道不能为空");
		validateRequiredString("app.appKey", "msg", "授权账号不能为空");
		validateRequiredString("app.appSecret", "msg", "授权密码不能为空");
	}

	@Override
	protected void handleError(Controller c) {
		c.setAttr("state", "fail");
		c.renderJson();
	}

}
