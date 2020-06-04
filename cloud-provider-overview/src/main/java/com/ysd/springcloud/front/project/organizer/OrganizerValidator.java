package com.ysd.springcloud.front.project.organizer;

import com.jfinal.core.Controller;
import com.ysd.springcloud.front.common.FrontValidator;

public class OrganizerValidator extends FrontValidator {

	@Override
	protected void validate(Controller c) {
		setShortCircuit(true);
		
		validateRequiredString("parties", "msg", "参与方不能为空");
		validateRequiredString("unitName", "msg", "单位名称不能为空");
	}
	
}
