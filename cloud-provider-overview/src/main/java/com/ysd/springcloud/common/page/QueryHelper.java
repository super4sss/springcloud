package com.ysd.springcloud.common.page;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

/**
 * 条件查询助手
 */
public class QueryHelper<M extends Model<M>> {
	
	public static final String SELECT_SQL = "select *";
	public static final String KEYWORD_ORDER = " order by ";
    public static final String KEYWORD_GROUP = " group by ";
    public static final Pattern SQL_PATTERN = Pattern.compile("^(?i)(select .+ )?(from .+)$");

	boolean whereFlag = true;
    boolean orderFlag = true;
    boolean groupFlag = true;
    private StringBuilder sqlBuilder;
    private List<Object> list;
    
    public QueryHelper() {
        sqlBuilder = new StringBuilder();
    }
    
    public QueryHelper(String sql) {
        sqlBuilder = new StringBuilder();
        sqlBuilder.append(sql);
    }
    
    public QueryHelper<M> where(String condition) {
        if (whereFlag) {
            whereFlag = false;
            sqlBuilder.append(" where ");
        } else {
            sqlBuilder.append(" and ");
        }
        sqlBuilder.append(condition);
        return this;
    }
    
    public QueryHelper<M> where(String condition, Object value) {
    	return where(condition, value, null, 1);
    }
    
    public QueryHelper<M> where(String condition, Object value, int times) {
    	return where(condition, value, null, times);
    }
    
    public QueryHelper<M> where(String condition, Object value, String format) {
    	return where(condition, value, format, 1);
    }
    
    public QueryHelper<M> where(String condition, Object value, String format, int times) {
    	if (notNull(value)) {
    		if (StrKit.notBlank(format)) {
    			value = String.format(format, value);
    		}
    		where(condition).param(value, times);
    	}
    	return this;
    }
    
    public QueryHelper<M> order(String sqlString) {
        if (orderFlag) {
            orderFlag = false;
            sqlBuilder.append(KEYWORD_ORDER);
        } else {
            sqlBuilder.append(',');
        }
        sqlBuilder.append(sqlString);
        return this;
    }
    
    public QueryHelper<M> group(String sqlString) {
        if (groupFlag) {
            groupFlag = false;
            sqlBuilder.append(KEYWORD_GROUP);
        } else {
            sqlBuilder.append(',');
        }
        sqlBuilder.append(sqlString);
        return this;
    }
    
    public QueryHelper<M> append(String sqlString) {
        sqlBuilder.append(sqlString);
        return this;
    }
    
    public QueryHelper<M> param(Object value) {
        return param(value, 1);
    }
    
    public QueryHelper<M> param(Object value, int times) {
        if (null == list) {
        	list = new ArrayList<>();
        }
        for (int i = 0; i < times; i++) {
        	list.add(value);
		}
        return this;
    }
    
    public List<M> find(M model) {
    	return model.find(getSql(), getParas());
    }
    
    public Page<M> paginate(M model, int pageNumber, int pageSize) {
    	String sql = getSql();
    	Matcher matcher = SQL_PATTERN.matcher(sql);
    	if (matcher.matches()) {
    		String select = getSelect(matcher.group(1));
    		return model.paginate(pageNumber, pageSize, select, matcher.group(2), getParas());
    	}
    	return new Page<M>();
    }
    
    public String getSql() {
        return sqlBuilder.toString();
    }
    
    private String getSelect(String custom) {
    	return StrKit.notBlank(custom) ? custom : SELECT_SQL;
    }
    
    public Object[] getParas() {
    	return list != null ? list.toArray() : ArrayUtils.EMPTY_OBJECT_ARRAY;
    }
    
    private boolean notNull(Object value) {
    	if (value instanceof String) {
    		return StrKit.notBlank((String) value);
    	} else if (value instanceof Number) {
    		return ((Number) value).doubleValue() > 0;
    	}
    	return value != null;
    }
	
}
