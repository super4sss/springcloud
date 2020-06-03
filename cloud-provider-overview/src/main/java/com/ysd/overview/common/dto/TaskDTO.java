package com.ysd.overview.common.dto;

import java.util.Date;

import com.jfinal.plugin.activerecord.Record;

public class TaskDTO {

	private Long id;
	private String title;
	private String executor;
	private Date startTime;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getExecutor() {
		return executor;
	}
	public void setExecutor(String executor) {
		this.executor = executor;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	
	public static TaskDTO onRecord(Record record) {
		TaskDTO dto = new TaskDTO();
		dto.setId(record.getLong("id"));
		dto.setTitle(record.getStr("title"));
		dto.setExecutor(record.getStr("executorName"));
		dto.setStartTime(record.getDate("startAt"));
		return dto;
	}
	
}
