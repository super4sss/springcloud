package com.ysd.springcloud.common.dto;

public class CameraDTO implements Comparable<CameraDTO> {

	private int id;
	private String name;
	private String vcrPath;
	private String picPath;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVcrPath() {
		return vcrPath;
	}
	public void setVcrPath(String vcrPath) {
		this.vcrPath = vcrPath;
	}
	public String getPicPath() {
		return picPath;
	}
	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}
	
	@Override
	public int compareTo(CameraDTO o) {
		int num = getId() - o.getId();
        return num == 0 ? getName().compareTo(o.getName()) : num;
	}
	
}
