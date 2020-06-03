package com.ysd.overview.common.dto;

public class UserDTO {
	
	private String code;
	private String name;
	private String email;
	private String phone;
	
	public UserDTO() {}
	
	public UserDTO(Builder builder) {
		this.code = builder.code;
		this.name = builder.name;
		this.email = builder.email;
		this.phone = builder.phone;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public static Builder getBuilder() {
		return new Builder();
	}
	
	public static class Builder {
		String code;
		String name;
		String email;
		String phone;
		
		public Builder withCode(String code) {
			this.code = code;
			return this;
		}
		public Builder withName(String name) {
			this.name = name;
			return this;
		}
		public Builder withEmail(String email) {
			this.email = email;
			return this;
		}
		public Builder withPhone(String phone) {
			this.phone = phone;
			return this;
		}
		
		public UserDTO build() {
			return new UserDTO(this);
		}
	}
	
}
