package com.ysd.overview.api.common;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CacheManager {

	 private static final Logger log = LoggerFactory.getLogger(CacheManager.class);
	 
	    /*使用google guava缓存处理*/
	    private static Cache<String,Object> cache;
	    static {
	        cache = CacheBuilder.newBuilder().maximumSize(10000)
	                .expireAfterWrite(1, TimeUnit.HOURS)
	                .initialCapacity(10)
	                .removalListener(new RemovalListener<String, Object>() {
	                    @Override
	                    public void onRemoval(RemovalNotification<String, Object> rn) {
	                        if(log.isInfoEnabled()){
	                            log.info("被移除缓存{}:{}",rn.getKey(),rn.getValue());
	                        }
	                    }
	                }).build();
	    }
	 
	    /*获取缓存*/
	    public  static Object get(String key){
	        return StringUtils.isNotEmpty(key)?cache.getIfPresent(key):null;
	    }

	    /*放入缓存*/
	    public static void put(String key,Object value){
	        if(StringUtils.isNotEmpty(key) && value !=null){
	            cache.put(key,value);
	        }
	    }

	    /*移除缓存*/
	    public static void remove(String key){
	        if(StringUtils.isNotEmpty(key)){
	            cache.invalidate(key);
	        }
	    }
	    
	    /*批量删除缓存*/
	    public static void remove(List<String> keys){
	        if(keys !=null && keys.size() >0){
	            cache.invalidateAll(keys);
	        }
	    }

}
