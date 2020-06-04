package com.ysd.springcloud.api.doc;

/*
 * File DocType.java
 * -------------------
 * 用于存储技术文档参数类型类
 */
public final class  DocType {

	protected static final String OFFICE_TYPE = "['.doc','.docx','.xlsx','.pdf']";		//图档格式
	protected static final String OFFICE = "office";
	
	protected static final String PICTURE_TYPE = "['.jpg','.jpeg','.png','.bmp','.dwg','.svg','.gif']";		//图片格式
	protected static final String PICTURE = "picture";
	
	protected static final String BIMVIZ_MODEL_TYPE = "['.bzip','.bzip2']";		//BIMVIZ 模型格式
	protected static final String BIMVIZ_MODEL = "bmodel";
	
	protected static final String VIDEO_TYPE = "['.flv','.m3u8','.mp4','.mpeg','.avi','.mtv','.wmv']";		//视频格式
	protected static final String VIDEO = "video";
	
//	protected static final String NWD_TYPE = "['.NWD','.NWF','.NWC']";			//Navisworks的文件格式
	protected static final String NWD_TYPE = "['.nwd','.nwf','.nwc']";			//Navisworks的文件格式
	protected static final String NWD = "nwd";
	
	/*局部阶段名称*/
	protected static final String STAGE_QQZL = "前期资料";	
	protected static final String STAGE_FASJ = "方案设计";
	protected static final String STAGE_CBSJ = "初步设计";
	protected static final String STAGE_SGTSJ = "施工图设计";
	
}
