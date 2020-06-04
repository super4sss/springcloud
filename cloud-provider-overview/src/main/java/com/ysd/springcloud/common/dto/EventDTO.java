package com.ysd.springcloud.common.dto;

import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

import com.jfinal.plugin.activerecord.Record;
import com.ysd.springcloud.common.kit.ObjKit;

public class EventDTO {

	private String id;
	private String title;
	private String createAt;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCreateAt() {
		return createAt;
	}
	public void setCreateAt(String createAt) {
		this.createAt = createAt;
	}
	
	public static EventDTO onRecord(Record record) {
		EventDTO dto = new EventDTO();
		dto.setId(record.getStr("id"));
		dto.setTitle(record.getStr("title"));
		
		Date date = record.getDate("createAt");
		if (ObjKit.notEmpty(date)) {
			dto.setCreateAt(DateFormatUtils.format(date, "yyyy-MM-dd"));
		}
		return dto;
	}
	
}
