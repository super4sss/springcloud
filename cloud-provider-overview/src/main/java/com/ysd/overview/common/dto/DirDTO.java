package com.ysd.overview.common.dto;

import java.util.List;

import com.google.common.collect.Lists;

public class DirDTO {

	private String id;
	private String text;
	private List<DirDTO> children;
	
	public DirDTO() {}
	
	public DirDTO(String id, String text) {
		this.id = id;
		this.text = text;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public List<DirDTO> getChildren() {
		if (children == null) {
			children = Lists.newArrayList();
		}
		return children;
	}
	public void setChildren(List<DirDTO> children) {
		this.children = children;
	}
	
	public DirDTO add(DirDTO child) {
		if (null == children) {
			children = Lists.newArrayList();
		}
		children.add(child);
		return this;
	}
	
}
