package com.ysd.springcloud.common.dto;

/*
 * File DocDTO.java
 * -------------------
 * 技术图档文件实体类,用于设置技术图档文件的字段
 * 
 */
public class DocDTO {

	private String id;			//技术图档文件ID
	private String name;		//技术图档文件名称
	private String pathIds;	//技术图档文件的目录路径ID 如："49348,60682,60685,60686",
	private String path;		//技术图档文件目录路径 如："/IT专用测试项目（勿删）1531908410635/proVrshow/e431be93cd7e4bfa83c44d11d6f3a03a"
	
	public DocDTO(){}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPathIds() {
		return pathIds;
	}

	public void setPathIds(String pathIds) {
		this.pathIds = pathIds;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	
	
}
