package com.ysd.springcloud.api.model;

import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.ysd.springcloud.common.kit.ObjKit;

import java.util.Collections;
import java.util.List;

/*
 * File ModelService.java
 * -------------------------
 * 重难点模拟业务处理层
 */
public class ModelService {

	public static final ModelService me = new ModelService();
	private static final String TAB_NAME = "重难点";
	

	public Ret getModelList(String project) {
		if(StrKit.isBlank(project)){
			return Ret.fail("msg", "参数错误");
		}
		List<Record> list = findModelList(project);
		if(ObjKit.empty(list)){
			return Ret.ok("data", Collections.emptyList());
		}
		return Ret.ok("data", list);
	}

	private List<Record> findModelList(String project) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT bvm.id,bvm.code,bvm.name,bvm.remark,bvm.picPath,bmt.name as tabName ");
		sql.append("FROM ysd_bvshow.bus_bv_model bvm LEFT JOIN ysd_bvshow.bus_model_tab bmt ON bvm.tabId = bmt.id ");
		sql.append("WHERE bvm.project = ? ");
		sql.append("AND bmt.name = ? ");
		sql.append("ORDER BY bvm.id ");
		return Db.find(sql.toString(), project,TAB_NAME);
	}
	
}
