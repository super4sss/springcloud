package com.ysd.springcloud.front.project.doc;

import java.util.Date;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;

public class DocParam {

	private String code;
	private String name;
	private String path;
	private String pathName;			
	private String stageId;			//阶段ID
	private String stageName;	//阶段名称
	
	private String project;
	private Integer type = NumberUtils.INTEGER_ONE;
	private String start;
	private String end;
	
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
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getStageId() {
		return stageId;
	}
	public void setStageId(String stageId) {
		this.stageId = stageId;
	}
	public String getStageName() {
		return stageName;
	}
	public void setStageName(String stageName) {
		this.stageName = stageName;
	}
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}
	
	public String getPathName() {
		return pathName;
	}
	public void setPathName(String pathName) {
		this.pathName = pathName;
	}
	public Date toStartDate() {
		try {
			return DateUtils.parseDate(start, "yyyy-MM-dd");
		} catch (Exception ignore) {}
		return null;
	}
	
	public Date toEndDate() {
		try {
			return DateUtils.parseDate(end, "yyyy-MM-dd");
		} catch (Exception ignore) {}
		return null;
	}
	
}
