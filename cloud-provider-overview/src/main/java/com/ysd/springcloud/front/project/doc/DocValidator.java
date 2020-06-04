package com.ysd.springcloud.front.project.doc;

import com.jfinal.core.Controller;
import com.ysd.springcloud.front.common.FrontValidator;

public class DocValidator extends FrontValidator {

	@Override
	protected void validate(Controller c) {
		setShortCircuit(true);
		
		validateString("code", 1, 36, "msg", "文件编码要求1至36个字符");
		validateString("name", 2, 100, "msg", "文件名称要求2至100个字符");
	}

}
