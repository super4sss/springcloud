package com.ysd.springcloud.front.work;

import static java.util.stream.Collectors.toList;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.google.common.collect.Maps;
import com.jfinal.kit.Ret;

/**
 * 问题文件导入校验
 */
public class ImportVerifyHandler {
	
	private static final Pattern KEY_PATTERN = Pattern.compile("^(\\S+)\\((实|计)\\)$");
	
	public static Ret verify(Map<String, Object> map, String project) {
		try {
			String workday = checkDate(map.remove("日期"));
			Map<String, NumObj> params = Maps.newLinkedHashMap();
			params.put("delete", null);
			for (String key : map.keySet()) {
				Matcher matcher = KEY_PATTERN.matcher(key);
				if (matcher.matches()) {
					String name = matcher.group(1);
					if (!params.containsKey(name)) params.put(name, new NumObj());
					params.get(name).set(matcher.group(2), checkInteger(map.get(key), key));
				}
			}
			return toSaveRet(project, workday, params);
		} catch (ImportVerifyException e) {
			return Ret.fail("msg", e.getMessage());
		}
	}
	
	private static Ret toSaveRet(String project, String workday, Map<String, NumObj> map) {
		return Ret.ok("msg", "save").set("sqls", map.entrySet().stream().map(e -> {
			if ("delete".equals(e.getKey())) {
				return toDeleteSql(project, workday);
			}
			StringBuilder buf = new StringBuilder();
			buf.append("insert into labor_worker (");
			buf.append("`workday`,`realNum`,`planNum`,`carft`,`project`) values (");
			buf.append("'").append(workday).append("',");
			buf.append("'").append(e.getValue().realNum).append("',");
			buf.append("'").append(e.getValue().planNum).append("',");
			buf.append("'").append(e.getKey()).append("',");
			buf.append("'").append(project).append("')");
			return buf.toString();
		}).collect(toList()));
	}
	
	private static String toDeleteSql(String project, String workday) {
		return "delete from labor_worker where project='"+project+"' and workday='"+workday+"'";
	}
	
	private static Integer checkInteger(Object value, String name) {
		if (value instanceof Integer) {
			return (Integer) value;
		}
		if (null == value) {
			throw new ImportVerifyException(name + "不能为空");
		}
		try {
			return NumberUtils.createInteger(value.toString());
		} catch (Exception e) {
			throw new ImportVerifyException(name + "格式不符合");
		}
	}
	
	private static String checkDate(Object value) {
		Date date = null;
		if (value instanceof Date) {
			date = (Date) value;
		} else if (value instanceof Integer) {
			String dateString = value.toString();
			date = parseDate(dateString, "yyyyMMdd");
		} else if (value instanceof String) {
			String dateString = value.toString();
			date = parseDate(dateString, "yyyy/MM/dd");
		}
		if (null == date) throw new ImportVerifyException("日期不能为空");
		return DateFormatUtils.format(date, "yyyy-MM-dd");
	}
	
	private static Date parseDate(String dateString, String pattern) {
		try {
			return DateUtils.parseDate(dateString, pattern);
		} catch (ParseException e) {
			throw new ImportVerifyException("日期格式不符合");
		}
	}
	
	private static class NumObj {
		Integer realNum;
		Integer planNum;
		
		public void set(String key, Integer value) {
			if ("实".equals(key)) {
				realNum = value;
			} else if ("计".equals(key)) {
				planNum = value;
			}
		}
	}
	
}
