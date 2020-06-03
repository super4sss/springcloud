package com.ysd.overview.common.dto;

import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

import com.jfinal.plugin.activerecord.Record;
import com.ysd.overview.common.kit.ObjKit;

public class ExportDTO {

	private Long id;
	private String fileName;
	private String remark;
	private String exportAt;
	private Integer status;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getExportAt() {
		return exportAt;
	}
	public void setExportAt(String exportAt) {
		this.exportAt = exportAt;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	public static ExportDTO onRecord(Record record) {
		ExportDTO dto = new ExportDTO();
		dto.setId(record.getLong("id"));
		dto.setFileName(record.getStr("fileName"));
		dto.setRemark(record.getStr("remark"));
		dto.setStatus(record.getInt("status"));
		
		Date date = record.getDate("createAt");
		if (ObjKit.notEmpty(date)) {
			dto.setExportAt(DateFormatUtils.format(date, "yyyy-MM-dd"));
		}
		return dto;
	}
	
}
