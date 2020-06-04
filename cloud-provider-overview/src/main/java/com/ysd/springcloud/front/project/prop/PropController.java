package com.ysd.springcloud.front.project.prop;

import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;
import com.jfinal.aop.Before;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.ysd.springcloud.common.auth.Menu;
import com.ysd.springcloud.common.auth.MenuType;
import com.ysd.springcloud.common.auth.UserPrincipal;
import com.ysd.springcloud.common.controller.BaseController;
import com.ysd.springcloud.common.dto.ProjectDTO;
import com.ysd.springcloud.common.kit.ObjKit;

/**
 * 项目属性控制器
 */
public class PropController extends BaseController {

	PropService srv = PropService.me;
	
	/**
     * @api {get} /project/prop 1.获取项目属性
     * @apiGroup prop
     * @apiVersion 1.0.0
     * @apiPermission 项目.概况.进入
     * 
     * @apiSuccess {String} name 项目名称
     * @apiSuccess {Map} propMap 属性键值对
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "data":{
     *   	 "name":"广东金融学院图书馆",
     *   	 "propMap":{
     *   		 "总包单位":"广东省国际工程咨询有限公司",
     *   		 "项目类型":"建筑工程",
     *   		 "工程地点":"广州天河区龙洞广东金融学院北校区"
     *   	 }
     *   }
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
//	@Menu("项目.概况.进入")
	public void index() {
		UserPrincipal user = getProjectUser();
		ProjectDTO project = srv.findProject(user);
		renderJson(Ret.ok("data", project));
	}
	
	/**
     * @api {post} /project/prop/save 2.保存项目属性
     * @apiGroup prop
     * @apiVersion 1.0.0
     * @apiPermission 概况.项目信息.编辑
     *
     * @apiParam {Map} propMap 属性键值对
     * 
     * @apiParamExample {html} Request-Example:
     * <form>
     *   <input name="propMap[项目名称]" value="广东金融学院图书馆"/>
     *   <input name="propMap[总包单位]" value="广东省国际工程咨询有限公司"/>
     *   <input name="propMap[项目类型]" value="建筑工程"/>
     *   <input name="propMap[工程地点]" value="广州天河区龙洞广东金融学院北校区"/>
     * </form>
     *
     * @apiSuccessExample {json} Success-Response:
     * {
     *   "state":"ok",
     *   "msg":"项目属性保存成功"
     * }
     * 
     * @apiErrorExample {json} Error-Response:
     * {
     *   "state":"fail",
     *   "msg":"操作处理失败，请联系管理员"
     * }
     */
	@Before(Tx.class)
	@Menu(value="概况.项目信息.编辑", type=MenuType.BUTTON)
	public void save() {
		/*UserPrincipal user = getProjectUser();
		if (!user.isOwner()) {
			renderJson(Ret.fail("msg", "不允许编辑，请联系负责人"));
			return;
		}*/
		Map<String, String> propMap = getParamMap(getRequest());
		Ret ret = srv.save(getBusProject(), propMap);
		renderJson(ret);
	}
	
	@SuppressWarnings("rawtypes")
	private Map<String, String> getParamMap(HttpServletRequest request) {
        Map<String, String> data = Maps.newHashMap();
        String prefix = "propMap";
        Enumeration en = request.getParameterNames();
        while (en.hasMoreElements()) {
            String name = (String) en.nextElement();
            if (!StringUtils.startsWith(name, prefix)) {
                continue;
            }
            String value = join(request.getParameterValues(name));
            if (StringUtils.isNotBlank(value)) {
                data.put(StringUtils.substringBetween(name, "[", "]"), value);
            }
        }
        return data;
    }
	
	private String join(String[] strs) {
		if (ObjKit.empty(strs)) {
            return null;
        }
		if (strs.length == 1) {
			return strs[0];
		}
		StringBuilder buf = new StringBuilder();
		for (String item : strs) {
			if (StringUtils.isBlank(item)) 
				continue;
			if (buf.length() > 0) buf.append(",");
			buf.append(item);
		}
		return buf.toString();
	}
	
}
