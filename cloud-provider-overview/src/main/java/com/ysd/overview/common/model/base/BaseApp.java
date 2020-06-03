package com.ysd.overview.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseApp<M extends BaseApp<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Long id) {
		set("id", id);
	}
	
	public java.lang.Long getId() {
		return getLong("id");
	}

	public void setChannel(java.lang.String channel) {
		set("channel", channel);
	}
	
	public java.lang.String getChannel() {
		return getStr("channel");
	}

	public void setAppKey(java.lang.String appKey) {
		set("appKey", appKey);
	}
	
	public java.lang.String getAppKey() {
		return getStr("appKey");
	}

	public void setAppSecret(java.lang.String appSecret) {
		set("appSecret", appSecret);
	}
	
	public java.lang.String getAppSecret() {
		return getStr("appSecret");
	}

	public void setRemark(java.lang.String remark) {
		set("remark", remark);
	}
	
	public java.lang.String getRemark() {
		return getStr("remark");
	}
	
	public void setStatus(java.lang.Integer status) {
		set("status", status);
	}
	
	public java.lang.Integer getStatus() {
		return getInt("status");
	}

}
