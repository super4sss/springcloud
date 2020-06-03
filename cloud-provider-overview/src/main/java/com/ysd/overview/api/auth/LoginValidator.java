package com.ysd.overview.api.auth;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

/**
 * ajax 登录参数验证
 */
public class LoginValidator extends Validator {

	@Override
	protected void validate(Controller c) {
		setShortCircuit(true);

		validateRequiredString("userName", "msg", "账号不能为空");
		validateRequiredString("password", "msg", "密码不能为空");
		//validateCaptcha("captcha", "msg", "验证码不正确");
	}

	@Override
	protected void handleError(Controller c) {
		c.setAttr("state", "fail");
		c.removeAttr("ctx");
		c.renderJson();
	}

}
