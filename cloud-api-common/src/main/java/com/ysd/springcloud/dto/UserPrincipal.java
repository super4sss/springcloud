package com.ysd.springcloud.dto;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.AesKit;
import com.jfinal.kit.Base64Kit;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.PropKit;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;
import java.util.function.Function;

public class UserPrincipal {

	private String account;
	private String user;
	private String username;
	private long accessedTime;
	private String project;
	private String projectName;
	private String document;
	private boolean isOwner;
	private boolean isAdmin;
	
	public UserPrincipal(Builder builder) {
		this.account = builder.account;
		this.user = builder.user;
		this.username = builder.username;
		this.accessedTime = builder.accessedTime;
		this.project = builder.project;
		this.projectName = builder.projectName;
		this.document = builder.document;
		this.isOwner = builder.isOwner;
		this.isAdmin = builder.isAdmin;
	}
	
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public long getAccessedTime() {
		return accessedTime;
	}
	public void setAccessedTime(long accessedTime) {
		this.accessedTime = accessedTime;
	}
	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = document;
	}
	public boolean isOwner() {
		return isOwner;
	}
	public void setOwner(boolean isOwner) {
		this.isOwner = isOwner;
	}
	public boolean isAdmin() {
		return isAdmin;
	}
	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public static UserPrincipal on(String token) {
		String[] items = StringUtils.split(token, ";");
		if (items == null 
				|| items.length < 8) {
			return null;
		}
		return UserPrincipal.getBuilder()
				.withAccount(items[0])
				.withUser(items[1])
				.withUsername(items[2])
				.withProject(items[3])
				.withProjectName(items[4])
				.withDocument(items[5])
				.withOwner(BooleanUtils.toBoolean(items[6]))
				.withAdmin(BooleanUtils.toBoolean(items[7]))
				.withAccessedTime(System.currentTimeMillis()).build();
	}
	
	public static UserPrincipal on(JSONObject obj) {
		if (com.ysd.springcloud.kit.ObjKit.empty(obj)) {
			return null;
		}
		String account = obj.getString("mobile");
		String user = obj.getString("user_id");
		String username = obj.getString("user_name");
		Integer adm = obj.getInteger("typ");
		if (StringUtils.isBlank(account)
				|| StringUtils.isBlank(user)
				|| StringUtils.isBlank(username)) {
			return null;
		}
		return UserPrincipal.getBuilder()
				.withAccount(account).withUser(user)
				.withUsername(com.ysd.springcloud.kit.ReqKit.decode(username))
				.withAdmin(adm == 0 ? true : false)
				.withAccessedTime(System.currentTimeMillis()).build();
	}
	
	private static String[] parse(String token) {
		try {
			String aesKey = PropKit.get("aes.key");
			String text = AesKit.decryptToStr(Base64Kit.decode(token), aesKey);
			return StringUtils.split(text, ";");
		} catch (Exception e) {
			LogKit.error(token + " decode error", e);
			return null;
		}
	}
	
	public String genToken() {
		StringBuilder buf = new StringBuilder(getProject());
		buf.append(";").append(getAccount());
		buf.append(";").append(getUsername());
		buf.append(";").append(getAccessedTime());
		
		String aesKey = PropKit.get("aes.key");
		return Base64Kit.encode(AesKit.encrypt(buf.toString(), aesKey));
	}
	
	public boolean expiredTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(getAccessedTime());
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 60);
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            return true;
        }
        return false;
    }
	
	public boolean openProject(Function<String,String> function) {
		String token = function.apply(getUser());
		if (StringUtils.isBlank(token)) {
			return false;
		}
		String[] items = parse(token);
		if (items == null 
				|| items.length < 5) {
			return false;
		}
		setProject(items[0]);
		setProjectName(items[1]);
		setDocument(items[2]);
		setOwner(BooleanUtils.toBoolean(items[3]));
		setAdmin(BooleanUtils.toBoolean(items[4]));
		return StringUtils.isNotBlank(getProject());
	}
	
	public static Builder getBuilder() {
		return new Builder();
	}
	
	public static class Builder {
		String account;
		String user;
		String username;
		long accessedTime;
		String project;
		String projectName;
		String document;
		boolean isOwner;
		boolean isAdmin;
		
		public Builder withAccount(String account) {
			this.account = account;
			return this;
		}
		public Builder withUser(String user) {
			this.user = user;
			return this;
		}
		public Builder withUsername(String username) {
			this.username = username;
			return this;
		}
		public Builder withAccessedTime(long accessedTime) {
			this.accessedTime = accessedTime;
			return this;
		}
		public Builder withProject(String project) {
			this.project = project;
			return this;
		}
		public Builder withProjectName(String projectName) {
			this.projectName = projectName;
			return this;
		}
		public Builder withDocument(String document) {
			this.document = document;
			return this;
		}
		public Builder withOwner(boolean isOwner) {
			this.isOwner = isOwner;
			return this;
		}
		
		public Builder withAdmin(boolean isAdmin) {
			this.isAdmin = isAdmin;
			return this;
		}
		
		public UserPrincipal build() {
			return new UserPrincipal(this);
		}
	}
	
}
