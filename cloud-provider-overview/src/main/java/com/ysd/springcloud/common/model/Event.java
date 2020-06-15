package com.ysd.springcloud.common.model;

import com.ysd.springcloud.common.kit.JsoupKit;
import com.ysd.springcloud.common.model.base.BaseEvent;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class Event extends BaseEvent<Event> {
	
	/**
	 * 过滤 title content 字段的 html 标记，防止 XSS 攻击
	 */
	protected void filter(int filterBy) {
		JsoupKit.filterTitleAndContent(this);
	}
	
}