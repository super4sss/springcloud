package com.ysd.overview.admin.index;

import java.util.Map;
import java.util.Properties;

import com.google.common.collect.ImmutableMap;

/**
 * 后台首页业务
 */
public class IndexAdminService {

	public static final IndexAdminService me = new IndexAdminService();
	
	public Map<String, String> getSysInfo() {
		Properties props = System.getProperties();
		
		return ImmutableMap.<String, String>builder()
                .put("os.name", props.getProperty("os.name"))
                .put("os.arch", props.getProperty("os.arch"))
                .put("os.version", props.getProperty("os.version"))
                .put("java.runtime.name", props.getProperty("java.runtime.name"))
                .put("java.runtime.version", props.getProperty("java.runtime.version"))
                .put("java.vm.info", props.getProperty("java.vm.info"))
                .put("java.vm.name", props.getProperty("java.vm.name"))
                .put("java.vm.version", props.getProperty("java.vm.version")).build();
	}
	
}
