package com.ysd.overview.front.vrshow;

import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.ysd.overview.common.kit.ObjKit;
import com.ysd.overview.common.model.Vrshow;
import com.ysd.overview.front.common.FrontValidator;

public class VrshowValidator extends FrontValidator {

	@Override
	protected void validate(Controller c) {
		setShortCircuit(true);
		
		validateRequiredString("fileToken", "msg", "文件Token不能为空");
		validateRequiredString("name", "msg", "模型名称不能为空");
		validateString("name", 2, 32, "msg", "模型名称长度要求在2到32个字符");
		
		String viewPath = c.getPara("viewPath");
		if (StrKit.notBlank(viewPath)) {
			validateUrl("viewPath", "msg", "全景地址不能解析成合法URL");
		}
		
		String method = getActionMethod().getName();
		if ("save".equals(method)) {
			// 创建VR模型
			validateSaveVr(c);
		} else if ("update".equals(method) || "updateV2".equals(method)) {
			// 更新VR模型
			validateUpdateVr(c);
		}
	}
	
	private void validateSaveVr(Controller c) {
		validateRequiredString("stage", "msg", "阶段编码不能为空");
		/*UserPrincipal user = c.getAttr(BaseController.BUS_PROJECT_USER);
		if (!user.isOwner()) {
			boolean isOwner = ClientService.me.isOwner(c.getPara("stage"), user);
			if (!isOwner) addError("msg", "不允许新建，请联系负责人");
		}*/
	}
	
	private void validateUpdateVr(Controller c) {
		Vrshow bean = VrshowService.me.findById(c.getParaToLong("id"));
		if (ObjKit.empty(bean)) {
			addError("msg", "VR展示不存在，请检查");
		}
		/*UserPrincipal user = c.getAttr(BaseController.BUS_PROJECT_USER);
		if (!user.isOwner()) {
			boolean isOwner = ClientService.me.isOwner(bean.getStage(), user);
			if (!isOwner) addError("msg", "不允许更新，请联系负责人");
		}*/
	}
	
}
