package com.ysd.springcloud.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseProProp<M extends BaseProProp<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Long id) {
		set("id", id);
	}
	
	public java.lang.Long getId() {
		return getLong("id");
	}

	public void setName(java.lang.String name) {
		set("name", name);
	}
	
	public java.lang.String getName() {
		return getStr("name");
	}

	public void setText(java.lang.String text) {
		set("text", text);
	}
	
	public java.lang.String getText() {
		return getStr("text");
	}

	public void setPropSet(java.lang.String propSet) {
		set("propSet", propSet);
	}
	
	public java.lang.String getPropSet() {
		return getStr("propSet");
	}

	public void setProject(java.lang.String project) {
		set("project", project);
	}
	
	public java.lang.String getProject() {
		return getStr("project");
	}

}
