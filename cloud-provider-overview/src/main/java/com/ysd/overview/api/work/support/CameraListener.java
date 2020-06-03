package com.ysd.overview.api.work.support;

import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Stopwatch;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.Ret;
import com.ysd.overview.common.kit.ObjKit;

import net.dreamlu.event.core.EventListener;

/**
 * 监控视频抓取图片监听
 */
public class CameraListener {
	
	@EventListener(order = 1, async = true)
	public void doFetchFrame(CameraEvent event) {
		LogKit.warn("FetchFrame start...");
		
		String project = event.getSource();
		Ret ret = LaborService.me.getMonitoring(project);
		if (ret.isOk()) {
			JSONObject map = ret.getAs("data");
			if (ObjKit.notEmpty(map)) {
				for (String key : map.keySet()) {
					JSONObject obj = map.getJSONObject(key);
					saveFramePicture(project, key, obj.getString("path"));
				}
			}
		}
		LogKit.warn("FetchFrame finish!!!");
	}
	
	private void saveFramePicture(String project, String cameraName, String cameraPath) {
		try {
			Stopwatch watch = Stopwatch.createStarted();
			LogKit.warn("Save Frame Picture with "+cameraName+" start...");
			
			FrameGrabber grabber = new FrameGrabber();
			BufferedImage img = grabber.grabBufferImage(cameraPath);
			if (img != null) {
				ImageConverter.saveImage(img, 
						134, 90, getSavePath(project, cameraName));
			}
			
			watch.stop();
			long millis = watch.elapsed(TimeUnit.MILLISECONDS);
			LogKit.warn("Save Frame Picture with "+cameraName+" succeed, [elapsed: "+millis+"ms]");
		} catch (Exception e) {
			LogKit.error("Save Frame Picture with "+cameraName+" failed", e);
		}
	}
	
	private String getSavePath(String project, String picture) {
		return PropKit.get("system.picPath") + "/camera/" + project + "/" + picture + ".jpg";
	}
	
}
