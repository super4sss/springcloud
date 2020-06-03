package com.ysd.overview.front.common;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;
import com.ysd.overview.common.controller.BaseController;

public abstract class FrontValidator extends Validator {

	@Override
	protected void handleError(Controller c) {
		c.setAttr("state", "fail");
		c.removeAttr("ctx");
		c.removeAttr(BaseController.BUS_PROJECT_USER);
		c.renderJson();
	}

}
