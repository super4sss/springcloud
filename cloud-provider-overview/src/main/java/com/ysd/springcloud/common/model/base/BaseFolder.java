package com.ysd.springcloud.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseFolder<M extends BaseFolder<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Long id) {
		set("id", id);
	}
	
	public java.lang.Long getId() {
		return getLong("id");
	}

	public void setCode(java.lang.String code) {
		set("code", code);
	}
	
	public java.lang.String getCode() {
		return getStr("code");
	}

	public void setName(java.lang.String name) {
		set("name", name);
	}
	
	public java.lang.String getName() {
		return getStr("name");
	}

	public void setEntity(java.lang.String entity) {
		set("entity", entity);
	}
	
	public java.lang.String getEntity() {
		return getStr("entity");
	}

	public void setEntityId(java.lang.String entityId) {
		set("entityId", entityId);
	}
	
	public java.lang.String getEntityId() {
		return getStr("entityId");
	}

}
