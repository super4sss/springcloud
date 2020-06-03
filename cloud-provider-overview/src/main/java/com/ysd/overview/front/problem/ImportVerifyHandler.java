package com.ysd.overview.front.problem;

import static java.util.stream.Collectors.joining;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.jfinal.kit.Ret;

/**
 * 问题文件导入校验
 */
public class ImportVerifyHandler {
	
	public static Ret verify(Map<String, Object> map, String project) {
		try {
			checkField(map);
		} catch (ImportVerifyException e) {
			return Ret.fail("msg", e.getMessage());
		}
		String code = map.get("code").toString();
		Long id = ProblemService.me.getByCode(code, project);
		return id != null ? toUpdateRet(id, map) : toSaveRet(project, map);
	}
	
	private static Ret toSaveRet(String project, Map<String, Object> map) {
		StringBuilder buf = new StringBuilder();
		buf.append("insert into pro_problem (");
		buf.append(map.keySet().stream().map(s -> String.format("`%s`", s)).collect(joining(",")));
		buf.append(",`project`").append(") values (");
		buf.append(map.values().stream().map(s -> String.format("'%s'", s)).collect(joining(",")));
		buf.append(",'").append(project).append("')");
		return Ret.ok("msg", "save").set("sql", buf.toString());
	}
	
	private static Ret toUpdateRet(Long id, Map<String, Object> map) {
		StringBuilder buf = new StringBuilder();
		buf.append("update pro_problem set");
		buf.append(" `type`=").append(objectToValue(map.get("type")));
		buf.append(",`category`=").append(objectToValue(map.get("category")));
		buf.append(",`specialty`=").append(objectToValue(map.get("specialty")));
		buf.append(",`description`=").append(objectToValue(map.get("description")));
		buf.append(",`area`=").append(objectToValue(map.get("area")));
		buf.append(",`build`=").append(objectToValue(map.get("build")));
		buf.append(",`floor`=").append(objectToValue(map.get("floor")));
		buf.append(",`position`=").append(objectToValue(map.get("position")));
		buf.append(",`beginTime`=").append(objectToValue(map.get("beginTime")));
		buf.append(",`endTime`=").append(objectToValue(map.get("endTime")));
		buf.append(",`contactMan`=").append(objectToValue(map.get("contactMan")));
		buf.append(",`contactNum`=").append(objectToValue(map.get("contactNum")));
		buf.append(",`belongsGroup`=").append(objectToValue(map.get("belongsGroup")));
		buf.append(",`status`=").append(objectToValue(map.get("status")));
		buf.append(" where id = ").append(id);
		return Ret.ok("msg", "update").set("sql", buf.toString());
	}
	
	private static void checkField(Map<String, Object> map) {
		checkString(map, "编号", "code", true, 32);
		checkString(map, "问题类型", "type", false, 32);
		checkString(map, "问题类别", "category", false, 32);
		checkString(map, "专业", "specialty", false, 32);
		checkString(map, "问题描述", "description", false, 255);
		checkString(map, "发生区域", "area", false, 20);
		checkString(map, "栋", "build", false, 20);
		checkString(map, "楼层", "floor", false, 20);
		checkString(map, "轴网位置", "position", false, 50);
		checkDate(map, "发生时间", "beginTime", false);
		checkDate(map, "解决问题时间", "endTime", false);
		checkString(map, "责任人", "contactMan", false, 32);
		checkString(map, "联系电话", "contactNum", false, 32);
		checkString(map, "所属团组", "belongsGroup", false, 50);
		checkString(map, "状态", "status", false, 20);
	}
	
	private static void checkString(Map<String, Object> map, String name, String field, boolean required, int maxLength) {
		String value = objectToString(map.remove(name));
		if (StringUtils.isBlank(value)) {
			if (required) throw new ImportVerifyException(name + "不能为空");
		} else if (value.length() > maxLength) {
			throw new ImportVerifyException(name + "不能超过" + maxLength + "个字符");
		} else {
			map.put(field, value);
		}
	}
	
	private static String objectToValue(Object source) {
		return source != null ? "'" + source + "'" : null;
    }
	
	private static String objectToString(Object source) {
		if (source instanceof Date) {
			Date date = (Date) source;
			return DateFormatUtils.format(date, "yyyy/MM/dd");
		} else if (source instanceof Double) {
			try {
				return new BigDecimal(source.toString()).toPlainString();
			} catch (Exception ignore) {}
		}
		return source != null ? source.toString() : null;
    }
	
	private static void checkDate(Map<String, Object> map, String name, String field, boolean required) {
		Object obj = map.remove(name);
		if (obj instanceof Date) {
			Date date = (Date) obj;
			map.put(field, DateFormatUtils.format(date, "yyyy/MM/dd"));
			return;
		}
		if (obj != null) {
			String dateString = obj.toString();
			try {
				DateUtils.parseDate(dateString, "yyyy/MM/dd");
			} catch (ParseException e) {
				throw new ImportVerifyException(name + "格式不符合");
			}
			map.put(field, dateString);
			return;
		}
		if (required) throw new ImportVerifyException(name + "不能为空");
	}
	
}
