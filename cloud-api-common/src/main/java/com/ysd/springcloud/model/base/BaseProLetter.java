package com.ysd.springcloud.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseProLetter<M extends BaseProLetter<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Long id) {
		set("id", id);
	}
	
	public java.lang.Long getId() {
		return getLong("id");
	}

	public void setTitle(java.lang.String title) {
		set("title", title);
	}
	
	public java.lang.String getTitle() {
		return getStr("title");
	}

	public void setContent(java.lang.String content) {
		set("content", content);
	}
	
	public java.lang.String getContent() {
		return getStr("content");
	}

	public void setCreator(java.lang.String creator) {
		set("creator", creator);
	}
	
	public java.lang.String getCreator() {
		return getStr("creator");
	}

	public void setCreatorNum(java.lang.String creatorNum) {
		set("creatorNum", creatorNum);
	}
	
	public java.lang.String getCreatorNum() {
		return getStr("creatorNum");
	}

	public void setCreateAt(java.util.Date createAt) {
		set("createAt", createAt);
	}
	
	public java.util.Date getCreateAt() {
		return get("createAt");
	}

	public void setProject(java.lang.String project) {
		set("project", project);
	}
	
	public java.lang.String getProject() {
		return getStr("project");
	}

}
