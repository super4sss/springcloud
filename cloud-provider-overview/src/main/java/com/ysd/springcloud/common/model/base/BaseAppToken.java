package com.ysd.springcloud.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseAppToken<M extends BaseAppToken<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Long id) {
		set("id", id);
	}
	
	public java.lang.Long getId() {
		return getLong("id");
	}

	public void setAuthToken(java.lang.String authToken) {
		set("authToken", authToken);
	}
	
	public java.lang.String getAuthToken() {
		return getStr("authToken");
	}

	public void setAppId(java.lang.Long appId) {
		set("appId", appId);
	}
	
	public java.lang.Long getAppId() {
		return getLong("appId");
	}

	public void setLoginAt(java.util.Date loginAt) {
		set("loginAt", loginAt);
	}
	
	public java.util.Date getLoginAt() {
		return get("loginAt");
	}

	public void setLoginIp(java.lang.String loginIp) {
		set("loginIp", loginIp);
	}
	
	public java.lang.String getLoginIp() {
		return getStr("loginIp");
	}

}
