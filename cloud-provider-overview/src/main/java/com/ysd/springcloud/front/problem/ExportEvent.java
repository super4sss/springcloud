package com.ysd.springcloud.front.problem;

import com.ysd.springcloud.common.model.Problem;

import net.dreamlu.event.core.ApplicationEvent;

/**
 * 导出事件
 */
public class ExportEvent extends ApplicationEvent<Problem> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ExportEvent(Problem source) {
		super(source);
	}

}
