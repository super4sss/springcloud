package com.ysd.springcloud.common.dto;

import java.util.HashMap;
import java.util.Map;

/*
 * File MenusDTO.java
 * -----------------------
 * 菜单实体类
 */
public class MenusDTO {

	private String token;								//菜单标识
	private String customName;					//自定义名称
	
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	public String getCustomName() {
		return customName;
	}
	public void setCustomName(String customName) {
		this.customName = customName;
	}
	
	private Map<String, String> setMenusKV() {
		Map<String, String> menus = new HashMap<>();
		menus.put("info", "概况");
		menus.put("plan", "计划");
		menus.put("schedule", "进度");
		menus.put("task", "任务");
		menus.put("file", "图档");
		menus.put("model", "模型");
		menus.put("quantity", "工程量");
		menus.put("material", "物料追踪");
		menus.put("mate2", "构件跟踪");
		menus.put("vr", "VR展示");
		menus.put("groupRole", "组织角色");
		menus.put("operations", "运维");
		menus.put("labor", "智慧工地");
		return menus;
	}
	
	/**
	 * 获取菜单的名称
	 * @param tokenName
	 * @return
	 */
	public String getMenusName(String tokenName){
		Map<String, String> map = setMenusKV();
		return map.get(tokenName);
	}
	

	@Override
	public String toString() {
		return "MenusDTO [token=" + token + ", customName=" + customName + "]";
	}

	
	
	
}
