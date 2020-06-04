package com.ysd.springcloud.front.project.newspaper;

import com.jfinal.core.Controller;
import com.ysd.springcloud.common.kit.ObjKit;
import com.ysd.springcloud.common.model.WeeklyNewspaper;
import com.ysd.springcloud.front.common.FrontValidator;

/*
 * File WeeklyNewspaperValidator.java
 * ----------------------------------------
 * 周报管理验证器
 */
public class WeeklyNewspaperValidator extends FrontValidator{

	@Override
	protected void validate(Controller c) {
		setShortCircuit(true);
		validateRequiredString("fileToken", "msg", "文件token不能为空");
		validateRequiredString("title", "msg", "周报标题不能为空");
		validateString("title", 2, 100, "msg", "周报标题长度要求在2到100个字符");
		validateRequiredString("content", "msg", "周报内容不能为空");
		
		String methodName = getActionMethodName();
		if("create".equals(methodName)){
			validatorSave(c);
		}else if("update".equals(methodName)){
			validatorUpdate(c);
		}
		
	}

	private void validatorUpdate(Controller c) {
		WeeklyNewspaper bean = WeeklyNewspaperService.paperService.findById(c.getParaToLong("id"));
		if(ObjKit.empty(bean)){
			addError("msg", "周报实体不存在，请检查");
		}
		/*UserPrincipal user = c.getAttr(BaseController.BUS_PROJECT_USER);
		if(!user.isOwner()){
			addError("msg", "周报不允许更新，请联系负责人");
		}*/
		
	}

	private void validatorSave(Controller c) {
		/*UserPrincipal user = c.getAttr(BaseController.BUS_PROJECT_USER);
		if(!user.isOwner()){
			addError("msg", "不允许新建，请联系负责人");
		}*/
	}

}
