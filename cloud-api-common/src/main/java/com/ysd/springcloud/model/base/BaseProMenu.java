package com.ysd.springcloud.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseProMenu<M extends BaseProMenu<M>> extends Model<M> implements IBean {

	public void setId(java.lang.String id) {
		set("id", id);
	}
	
	public java.lang.String getId() {
		return getStr("id");
	}

	public void setCode(java.lang.String code) {
		set("code", code);
	}
	
	public java.lang.String getCode() {
		return getStr("code");
	}

	public void setTitle(java.lang.String title) {
		set("title", title);
	}
	
	public java.lang.String getTitle() {
		return getStr("title");
	}

	public void setUrl(java.lang.String url) {
		set("url", url);
	}
	
	public java.lang.String getUrl() {
		return getStr("url");
	}

	public void setSort(java.lang.Integer sort) {
		set("sort", sort);
	}
	
	public java.lang.Integer getSort() {
		return getInt("sort");
	}

	public void setStatus(java.lang.Integer status) {
		set("status", status);
	}
	
	public java.lang.Integer getStatus() {
		return getInt("status");
	}

	public void setType(java.lang.Integer type) {
		set("type", type);
	}
	
	public java.lang.Integer getType() {
		return getInt("type");
	}

	public void setLink(java.lang.Integer link) {
		set("link", link);
	}
	
	public java.lang.Integer getLink() {
		return getInt("link");
	}

	public void setTarget(java.lang.String target) {
		set("target", target);
	}
	
	public java.lang.String getTarget() {
		return getStr("target");
	}

}
