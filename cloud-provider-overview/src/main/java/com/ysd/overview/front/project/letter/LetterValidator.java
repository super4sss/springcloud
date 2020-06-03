package com.ysd.overview.front.project.letter;

import com.jfinal.core.Controller;
import com.ysd.overview.common.kit.ObjKit;
import com.ysd.overview.common.model.Letter;
import com.ysd.overview.front.common.FrontValidator;

public class LetterValidator extends FrontValidator{

	@Override
	protected void validate(Controller c) {
		setShortCircuit(true);
		
		validateRequiredString("fileToken", "msg", "文件Token不能为空");
		validateRequiredString("title", "msg", "函件标题不能为空");
		validateString("title", 2, 100, "msg", "函件标题长度要求在2到100个字符");
		validateRequiredString("content", "msg", "函件内容不能为空");
		
		String actionName = getActionMethod().getName();
		if ("save".equals(actionName)) {
			// 创建项目函件
			validateSaveLetter(c);
		} else if ("update".equals(actionName)) {
			// 更新项目函件
			validateUpdateLetter(c);
		}
		
	}

	private void validateUpdateLetter(Controller c) {
		Letter bean = LetterService.me.findById(c.getParaToLong("id"));
		if (ObjKit.empty(bean)) {
			addError("msg", "函件实体不存在，请检查");
		}
		/*UserPrincipal user = c.getAttr(BaseController.BUS_PROJECT_USER);
		if (!user.isOwner()) {
			addError("msg", "函件不允许更新，请联系负责人");
		}*/
		
	}

	private void validateSaveLetter(Controller c) {
		/*UserPrincipal user = c.getAttr(BaseController.BUS_PROJECT_USER);
		if (!user.isOwner()) {
			addError("msg", "不允许新建，请联系负责人");
		}*/
		
	}

}
