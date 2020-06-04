package com.ysd.springcloud.admin.login;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

/**
 * ajax 登录参数验证
 */
public class LoginAdminValidator extends Validator {

	@Override
	protected void validate(Controller c) {
		setShortCircuit(true);

		validateRequired("userName", "userNameMsg", "账号不能为空");
		validateRequired("password", "passwordMsg", "密码不能为空");
		validateCaptcha("captcha", "captchaMsg", "验证码不正确");
	}

	@Override
	protected void handleError(Controller c) {
		c.renderJson();
	}

}
