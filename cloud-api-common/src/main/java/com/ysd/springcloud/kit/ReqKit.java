package com.ysd.springcloud.kit;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * HTTP请求工具类
 */
public class ReqKit {

	public static String getIpAddress(HttpServletRequest request) {
		String ip = request.getHeader("X-Real-IP");
        if (StringUtils.isNotBlank(ip) 
        		&& !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotBlank(ip) 
        		&& !"unknown".equalsIgnoreCase(ip)) {
            int pos = ip.indexOf(',');
            return pos > 0 ? ip.substring(0, pos) : ip;
        }
        return request.getRemoteAddr();
	}
	
	public static String getDomain(HttpServletRequest request) {
		String scheme = request.getScheme();
		int serverPort = request.getServerPort();
		if (80 == serverPort && "http".equals(scheme) 
				|| 443 == serverPort && "https".equals(scheme)) {
			return scheme+"://"+request.getServerName();
		}
		return scheme+"://"+request.getServerName()+":"+serverPort;
    }
	
	public static boolean isAjax(HttpServletRequest request) {
		String accept = request.getHeader("accept");
		if (StringUtils.indexOf(accept, "application/json") != -1) 
			return true;
		String reqWith = request.getHeader("X-Requested-With");
		if (StringUtils.indexOf(reqWith, "XMLHttpRequest") != -1) 
			return true;
		return false;
	}
	
	public static String decode(String target) {
		try {
			return URLDecoder.decode(target, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return target;
		}
	}
	
}
