package com.ysd.overview.front.project.event;

import com.jfinal.core.Controller;
import com.ysd.overview.common.kit.ObjKit;
import com.ysd.overview.common.model.Event;
import com.ysd.overview.front.common.FrontValidator;

public class EventValidator extends FrontValidator {

	@Override
	protected void validate(Controller c) {
		setShortCircuit(true);
		
		validateRequiredString("fileToken", "msg", "文件Token不能为空");
		validateRequiredString("title", "msg", "事件标题不能为空");
		validateString("title", 2, 100, "msg", "事件标题长度要求在2到100个字符");
		validateRequiredString("content", "msg", "事件内容不能为空");
		
		String method = getActionMethod().getName();
		if ("save".equals(method)) {
			// 创建项目事件
			validateSaveEvent(c);
		} else if ("update".equals(method)) {
			// 更新项目事件
			validateUpdateEvent(c);
		}
	}
	
	private void validateSaveEvent(Controller c) {
		/*UserPrincipal user = c.getAttr(BaseController.BUS_PROJECT_USER);
		if (!user.isOwner()) {
			addError("msg", "不允许新建，请联系负责人");
		}*/
	}
	
	private void validateUpdateEvent(Controller c) {
		Event bean = EventService.me.findById(c.getParaToLong("id"));
		if (ObjKit.empty(bean)) {
			addError("msg", "事件不存在，请检查");
		}
		/*UserPrincipal user = c.getAttr(BaseController.BUS_PROJECT_USER);
		if (!user.isOwner()) {
			addError("msg", "事件不允许更新，请联系负责人");
		}*/
	}

}
