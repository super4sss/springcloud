package com.ysd.springcloud.common.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.TimeUnit;

/**
 * 支持缓存服务基类
 */
public abstract class CacheService {

	private LoadingCache<String, Object> cache;
	
	protected CacheService() {
		this(30L, TimeUnit.MINUTES);
	}
	
	protected CacheService(Long duration, TimeUnit unit) {
		cache = CacheBuilder.newBuilder().expireAfterWrite(duration, unit).build(createCacheLoader());
	}
	
	protected abstract CacheLoader<String, Object> createCacheLoader();
	
	protected Object get(String key) {
		try {
			return cache.getUnchecked(key);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected void remove(String key) {
		try {
			cache.invalidate(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
