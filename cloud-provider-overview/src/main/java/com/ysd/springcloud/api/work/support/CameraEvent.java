package com.ysd.springcloud.api.work.support;

import net.dreamlu.event.core.ApplicationEvent;

/**
 * 监控视频抓取图片事件
 */
public class CameraEvent extends ApplicationEvent<String> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public CameraEvent(String source) {
		super(source);
	}

}
