package com.ysd.overview.common.dto;

import java.util.Map;

public class ProjectDTO {

	private String name;
	private Map<String, String> propMap;
	
	public ProjectDTO() {}
	
	public ProjectDTO(Builder builder) {
		this.name = builder.name;
		this.propMap = builder.propMap;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Map<String, String> getPropMap() {
		return propMap;
	}
	public void setPropMap(Map<String, String> propMap) {
		this.propMap = propMap;
	}
	
	public static Builder on(String name) {
		return new Builder(name);
	}
	
	public static class Builder {
		final String name;
		Map<String, String> propMap;

		Builder(String name) {
			this.name = name;
		}

		public Builder withPropMap(Map<String, String> propMap) {
			this.propMap = propMap;
			return this;
		}
		
		public ProjectDTO build() {
			return new ProjectDTO(this);
		}
	}
	
}
