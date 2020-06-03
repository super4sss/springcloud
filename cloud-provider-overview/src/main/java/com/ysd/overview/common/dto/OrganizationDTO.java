package com.ysd.overview.common.dto;

/**
 * 组织角色DTO
 * @author Administrator
 * @date 2019年7月8日
 *
 */
public class OrganizationDTO {
	
	private String code;			//组织编码
	private String name;			//组织名称
//	private String userCode;	//用户编码
//	private String userName;	//用户名称
//	private List<OrganizationDTO> children;
	
	public OrganizationDTO(){}
	
	public OrganizationDTO(String code, String name) {
		this.code = code;
		this.name = name;
	}

/*	public OrganizationDTO(String code, String name,String userCode, String userName) {
		this.code = code;
		this.name = name;
		this.userCode = userCode;
		this.userName = userName;
	}*/

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
/*
	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}*/

	@Override
	public String toString() {
		return "OrganizationDTO [code=" + code + ", name=" + name + "]";
	}

	
	

/*	public List<UserDTO> getChildren() {
		if (children == null) {
			children = Lists.newArrayList();
		}
		return children;
	}

	public void setChildren(List<UserDTO> children) {
		this.children = children;
	}
	
	public OrganizationDTO add(UserDTO child) {
		if (null == children) {
			children = Lists.newArrayList();
		}
		children.add(child);
		return this;
	}*/
	
}
