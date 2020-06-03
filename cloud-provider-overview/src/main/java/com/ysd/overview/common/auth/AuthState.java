package com.ysd.overview.common.auth;

public enum AuthState {
	UNKNOWN("未登录，请重新登录"),
	UNOPENED("无项目，请重新进入"),
	UNAUTHORIZED("未授权，请联系管理员"),
	FAIL("未授权，请联系管理员");

    private String msg;
    private AuthState(String msg) { 
    	this.msg = msg;
    }
    
    public String getCode() {
		return name().toLowerCase();
	}
	public String getMsg() {
		return msg;
	}
    
}
