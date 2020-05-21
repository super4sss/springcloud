//package com.ysd.springcloud.controller;
//
//import com.alibaba.fastjson.JSONObject;
//import com.jfinal.aop.Before;
//import com.jfinal.core.Controller;
//import com.jfinal.ext.interceptor.NotAction;
//import com.jfinal.kit.LogKit;
//import com.jfinal.kit.PropKit;
//import com.jfinal.kit.Ret;
//import com.jfinal.kit.StrKit;
//import com.ysd.springcloud.dto.UserPrincipal;
//import com.ysd.springcloud.kit.ObjKit;
//import com.ysd.springcloud.service.ClientService;
//import com.ysd.springcloud.service.UcenterService;
//import org.apache.commons.lang3.StringUtils;
//
//
///**
// * 基础控制器
// */
//public abstract class BaseController extends Controller {
//
//	public static final String QUERY_MODEL_NAME = "form";
//	public static final String BUS_PROJECT_USER = "busProjectUser";
//
//	@Before(NotAction.class)
//	public <T> T getQueryModel(Class<T> modelClass) {
//		return super.getModel(modelClass, QUERY_MODEL_NAME);
//	}
//
//	@Before(NotAction.class)
//	public <T> T getQueryBean(Class<T> modelClass) {
//		return super.getBean(modelClass, StringUtils.EMPTY);
//	}
//
//	@Before(NotAction.class)
//	public com.ysd.overview.common.page.Paginator getPaginator() {
//		com.ysd.overview.common.page.Paginator paginator = new com.ysd.overview.common.page.Paginator();
//		Integer pageNo = getParaToInt("pageNo");
//		if (pageNo != null && pageNo > 0) {
//			paginator.setPageNo(pageNo);
//		}
//		Integer pageSize = getParaToInt("pageSize");
//		if (pageSize != null && pageSize > 0) {
//			paginator.setPageSize(pageSize);
//		}
//		return paginator;
//	}
//	//获取用户对象
//	@Before(NotAction.class)
//	public UserPrincipal getProjectUser() {
//		UserPrincipal user;
//		if (ACTIVE_TOKEN){
//			String project = getPara("soluId");
////			String name = "bimsess";
////			String value = "MTU4NzU0NTI0MHxJbE5WVWxGUFJGSlNVMVpHVjAxV1VraFhha1pHVFRCV1dWTkZZM2ROUmtKS1ZrWmFUVlJGWkZwTmF6QWlDZz09fD3fxE0LjVQzJwZJbcACxVnfVfjHUlA44pDXO3G81ZEy";
////			setCookie(name, value, 3600);
//			user = getCookieUser();
//			if (ObjKit.empty(user)) {
//				throw new IllegalArgumentException("The project user is not logged in");
//			}
//			user.setProject(project);
//			if(StrKit.notBlank(project)){
//				return ClientService.me.getProjectInfo(user);
//			}
//		}
//		else{
//			user = getAttr(BUS_PROJECT_USER);
//			if (ObjKit.empty(user)) {
//				throw new IllegalArgumentException("The project user is not logged in");
//			}
//		}
//		return user;
//  }
//
//	@Before(NotAction.class)
//	public String getBusProject() {
//		String project = null;
//		if(ACTIVE_TOKEN){
//			project = getPara("soluId");
//		}else{
//			project = getProjectUser().getProject();
//		}
//		if (StrKit.isBlank(project)) {
//			throw new IllegalArgumentException("The project is not logged in");
//		}
//		return project;
//    }
//
//	private UserPrincipal getCookieUser() {
//		String cookie = getCookie(BUS_COOKIE_NAME);
//		if (StrKit.isBlank(cookie)) {
//			return null;
//		}
//		Ret ret = UcenterService.me.getLoginUser(cookie);
//		JSONObject obj = ret.isOk() ? ret.getAs("data") : null;
//		if (ObjKit.empty(obj)) {
//			LogKit.warn("获取登录用户为空，" + ret.getStr("msg"));
//			return null;
//		}
//		return UserPrincipal.on(obj.getJSONObject("member"));
//	}
//
//	private static final String BUS_COOKIE_NAME = "bimsess";
//	private static final boolean ACTIVE_TOKEN =  PropKit.getBoolean("system.activetoken");
//
//}
