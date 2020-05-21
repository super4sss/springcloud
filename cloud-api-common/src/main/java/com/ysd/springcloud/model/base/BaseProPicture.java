package com.ysd.springcloud.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseProPicture<M extends BaseProPicture<M>> extends Model<M> implements IBean {

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

	public void setPath(java.lang.String path) {
		set("path", path);
	}
	
	public java.lang.String getPath() {
		return getStr("path");
	}

	public void setType(java.lang.Integer type) {
		set("type", type);
	}
	
	public java.lang.Integer getType() {
		return getInt("type");
	}

	public void setProject(java.lang.String project) {
		set("project", project);
	}
	
	public java.lang.String getProject() {
		return getStr("project");
	}

	public void setCreateTime(java.util.Date createTime) {
		set("createTime", createTime);
	}
	
	public java.util.Date getCreateTime() {
		return get("createTime");
	}

	public void setCustomName(java.lang.String customName) {
		set("customName", customName);
	}
	
	public java.lang.String getCustomName() {
		return getStr("customName");
	}

	public void setUploadTime(java.util.Date uploadTime) {
		set("uploadTime", uploadTime);
	}
	
	public java.util.Date getUploadTime() {
		return get("uploadTime");
	}

	public void setSite(java.lang.String site) {
		set("site", site);
	}
	
	public java.lang.String getSite() {
		return getStr("site");
	}

}
