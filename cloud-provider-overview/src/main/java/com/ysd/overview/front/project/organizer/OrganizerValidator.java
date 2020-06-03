package com.ysd.overview.front.project.organizer;

import com.jfinal.core.Controller;
import com.ysd.overview.front.common.FrontValidator;

public class OrganizerValidator extends FrontValidator {

	@Override
	protected void validate(Controller c) {
		setShortCircuit(true);
		
		validateRequiredString("parties", "msg", "参与方不能为空");
		validateRequiredString("unitName", "msg", "单位名称不能为空");
	}
	
}
