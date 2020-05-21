package com.ysd.springcloud.kit;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import java.util.function.BiFunction;

public class ObjKit {

	public static boolean notEmpty(Object var) {
        return null != var;
    }
	
	public static boolean empty(Object var) {
        return null == var;
    }
	
	public static boolean notEmpty(Collection<?> var) {
        return null != var && !var.isEmpty();
    }
	
	public static boolean empty(Collection<?> var) {
        return null == var || var.isEmpty();
    }
	
	public static boolean notEmpty(Map<?, ?> var) {
        return null != var && !var.isEmpty();
    }
	
	public static boolean empty(Map<?, ?> var) {
        return null == var || var.isEmpty();
    }
	
	public static boolean notEmpty(Object[] var) {
        return null != var && 0 < var.length;
    }
	
	public static boolean empty(Object[] var) {
        return null == var || 0 == var.length;
    }
	
	public static boolean notEquals(Object a, Object b) {
        return !equals(a, b);
    }
	
	public static boolean equals(Object a, Object b) {
		if (a == null) {
            return b == null;
        }
        return a.equals(b);
    }
	
	public static boolean isDigits(Object var) {
		if (var instanceof Number) {
    		return ((Number) var).doubleValue() > 0;
    	}
		return false;
	}
	
	public static Date getDate() {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
	}
	
	public static String getDateStr() {
		Date now = new Date();
		return DateFormatUtils.format(now, "yyyy-MM-dd");
	}
	
	public static Date getMonday() {
		Calendar cal = Calendar.getInstance();
		int day = cal.get(Calendar.DAY_OF_WEEK)-1;
		if (day == 0) day = 7;
		cal.add(Calendar.DATE, -day+1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
	
	public static Date getLastDayOfMonth() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH)+1);
		cal.set(Calendar.DAY_OF_MONTH, 0);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
		return cal.getTime();
	}
	
	public static Date getLastDayOfMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH)+1);
		cal.set(Calendar.DAY_OF_MONTH, 0);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
		return cal.getTime();
	}
	
	public static Date getBeforeMonth(Date date, int times) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH)-times);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
	
	public static long getDays(String startTime, String endTime) {
		long days = 0;
		try {
			Date d1 = DateUtils.parseDate(startTime, "yyyy-MM-dd");
			Date d2 = DateUtils.parseDate(endTime, "yyyy-MM-dd");
			days = (d2.getTime() - d1.getTime()) / (24*60*60*1000);
		} catch (ParseException ignore) {}
		
        return days > 0 ? days : 0;
	}
	
	public static String calcRate(Number num1, Number num2) {
        double d1 = num1 != null ? num1.doubleValue() : 0;
        double d2 = num2 != null ? num2.doubleValue() : 0;

        if (d1 <= 0 || d2 <= 0) {
            return "0";
        }

        double rate = d1 / d2 * 100;
        
        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(2);
        return format.format(rate);
    }
	
	public static final String VAL_SEPARATOR = ",";
	public static final String KEY_SEPARATOR = ":";
	
	public static <T> List<T> collect(String str, BiFunction<String,String,T> function) {
		String[] array = StringUtils.split(str, VAL_SEPARATOR);
		if (empty(array)) {
			return Collections.emptyList();
		}
		List<T> list = new ArrayList<T>();
		for (String item : array) {
			String[] vals = StringUtils.split(item, KEY_SEPARATOR);
			if (vals == null 
					|| vals.length < 2) {
				continue;
			}
			T t = function.apply(vals[0], vals[1]);
			if (notEmpty(t)) list.add(t);
		}
		return list;
	}
	
	public static Map<String, String> arrayToMap(String[] array) {
		if (empty(array)) {
			return Collections.emptyMap();
		}
		Map<String, String> map = Maps.newHashMap();
		for (String item : array) {
			String[] vals = StringUtils.split(item, KEY_SEPARATOR);
			if (vals == null 
					|| vals.length < 2) {
				continue;
			}
			map.put(vals[0], vals[1]);
		}
		return map;
	}
	
}
