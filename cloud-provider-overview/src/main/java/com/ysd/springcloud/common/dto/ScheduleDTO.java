package com.ysd.springcloud.common.dto;

import java.util.Date;

/**
 * 计划进度实体类
 * @author Administrator
 * @date 2019年7月8日
 *
 */
public class ScheduleDTO {

	public static final int STATUS_COMPLETED = 1; 	//已完成
	public static final int STATUS_OPEN = 0;	//待完成
	
	private Long id;
	private Date createAt;
	private int status;	 //状态(0:待完成,1:已完成,2:已归档)
	private Date doneAt;
	private Long jobId;
	
	public ScheduleDTO(){
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getDoneAt() {
		return doneAt;
	}

	public void setDoneAt(Date doneAt) {
		this.doneAt = doneAt;
	}

	public Long getJobId() {
		return jobId;
	}

	public void setJobId(Long jobId) {
		this.jobId = jobId;
	}
	
	
	
	
}
