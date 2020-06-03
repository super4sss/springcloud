package com.ysd.overview.common.dto;

import java.util.Map;

public class DictDTO {

	private String code;
	private String name;
	
	public DictDTO() {}
	
	public DictDTO(String code, String name) {
		this.code = code;
		this.name = name;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public static DictDTO on(Map.Entry<String, String> me) {
		return new DictDTO(me.getKey(), me.getValue());
	}

	@Override
	public String toString() {
		return "DictDTO [code=" + code + ", name=" + name + "]";
	}
	
	
	
}
