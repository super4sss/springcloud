package com.ysd.overview.common.service;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.fluent.Request;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.ysd.overview.common.kit.FileKit;
import com.ysd.overview.common.kit.ObjKit;

public class LfsService {

	public static final LfsService me = new LfsService();
	private static final Log log = Log.getLog(LfsService.class);
	private String userId;
	private String sessionId;
	
	public Ret login() {
		StringBuilder buf = new StringBuilder();
		buf.append(getPath()).append("/apiLogin.action");
		buf.append("?encrypt=").append(PropKit.get("lfs.encrypt"));
		buf.append("&username=").append(PropKit.get("lfs.username"));
		buf.append("&password=").append(PropKit.get("lfs.password"));
		Ret ret = execute(buf.toString(), false, false);
		if (ret.isOk()) {
			Map<String, Object> map = ret.getAs("data");
			userId = String.valueOf(map.get("userId"));
			sessionId = String.valueOf(map.get("cookie"));
		}
		return ret;
	}
	
	public boolean checkOnline() {
		Ret ret = Ret.fail();
		if (StrKit.notBlank(sessionId)) {
			ret = execute("/checkOnline.action", true, true);
		}
		return ret.isFail() ? login().isOk() : true;
	}
	
	public Ret getFiles(String pid) {
		return execute("/commdisk/getFiles.action?pid=" + pid);
	}
	
	public Ret getDirectories(String pid) {
		return execute("/commdisk/getDirectories.action?pid=" + pid);
	}
	
	public Ret getFileInfo(String fileId) {
		return execute("/commdisk/getFileInfo.action?id=" + fileId);
	}
	
	public String getFilePath(String fileId) {
		Ret ret = getFileInfo(fileId);
		if (ret.isFail()) {
			return null;
		}
		Map<String, Object> map = ret.getAs("data");
		JSONObject obj = (JSONObject) map.get("rows");
		if (ObjKit.empty(obj)) {
			return null;
		}
		String name = obj.getString("name");
		if (StrKit.isBlank(name)) {
			return null;
		}
		String path = obj.getString("path");
		if (StrKit.isBlank(path)) {
			return "/" + name;
		}
		path = path + "/" + name;
		return path.substring(path.indexOf("/", 1));
	}
	
	public String createFolder(String pid, String name) {
		Ret ret = execute("/commdisk/createFolder.action?pid="+pid+"&name="+name);
		if (ret.isOk()) {
			Map<String, Object> map = ret.getAs("data");
			return (String) map.get("id");
		}
		return null;
	}
	
	public Ret deleteFile(String ids) {
		return execute("/commdisk/deleteFiles.action?id="+ids);
	}
	
	public boolean moveFile(String pid, String ids) {
		Ret ret = execute("/commdisk/moveFiles.action?pid="+pid+"&id="+ids);
		if (ret.isFail()) {
			String msg = ret.getStr("msg");
			return StringUtils.contains(msg, "目标文件夹为文件的父目录");
		}
		return true;
	}
	
	public Ret getSIDUID() {
		if (!checkOnline()) {
			return Ret.fail("msg", "登录LFS失败");
		}
		return Ret.ok("userId", userId).set("sessionId", sessionId);
	}
	
	public Ret execute(String url) {
		if (!checkOnline()) {
			return Ret.fail("msg", "登录LFS失败");
		}
		return execute(url, true, true);
	}
	
	public Ret execute(String url, boolean addCookie, boolean shortPath) {
		if (shortPath) {
			url = getPath() + url;
		}
		
		if (log.isDebugEnabled()) {
			log.debug("Request to " + url);
		}
		
		String resp = null;
		try {
			Request req = Request.Get(url);
			if (addCookie) req.addHeader("Cookie", genCookie());
			resp = req.execute().returnContent().asString();
		} catch (Exception e) {
			log.error("LFS请求异常", e);
			return Ret.fail("msg", "调用LFS接口失败");
		}
		
		Map<String, Object> map = json2Map(resp);
		if (ObjKit.empty(map)) {
			return Ret.fail("msg", "调用LFS接口结果数据转换失败");
		}
		
		Object obj = map.get("status");
		if (!"SUCCESS".equals(obj)) {
			return Ret.fail("msg", "对接LFS接口失败: " + map.get("msg"));
		}
		
		return Ret.ok("data", map);
	}
	
	private String genCookie() {
		return "lfs-localname=zh_CN;" + sessionId;
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Object> json2Map(String json) {
		try {
			return JsonKit.parse(json, Map.class);
		} catch (Exception e) {
			log.error("Json转换异常", e);
			return null;
		}
	}
	
	public String download(String id, String savePath, String fileName) throws IOException {
		String url = getDownPath() + id;
		String dir = PropKit.get("system.picPath");
		return Request.Get(url).execute().handleResponse(new ResponseHandler<String>() {
			@Override
			public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
				StatusLine statusLine = response.getStatusLine();
				if (statusLine.getStatusCode() >= 300) {
                    throw new HttpResponseException(
                    		statusLine.getStatusCode(), statusLine.getReasonPhrase());
                }
				HttpEntity entity = response.getEntity();
				if (entity == null) {
                    throw new ClientProtocolException("Response contains no content");
                }
				FileKit.writeTo(entity.getContent(), dir+savePath, fileName);
				return savePath + "/" + fileName;
			}
		});
	}
	
	public static String getPath() {
		return PropKit.get("lfs.path");
	}
	
	public static String getDownPath() {
		return getPath() + "/commdisk/download.action?acmod=downloadCommFile&apikey=9b11127a9701975c734b8aee81ee3526&id=";
	}
	
}
