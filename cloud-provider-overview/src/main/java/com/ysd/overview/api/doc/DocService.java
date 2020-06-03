package com.ysd.overview.api.doc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.ysd.overview.common.auth.UserPrincipal;
import com.ysd.overview.common.dto.DictDTO;
import com.ysd.overview.common.dto.DocDTO;
import com.ysd.overview.common.dto.OrganizationDTO;
import com.ysd.overview.common.kit.ObjKit;
import com.ysd.overview.front.index.ClientService;

/**
 * 技术图档业务处理层
 * @author Administrator
 * @date 2019年7月8日
 *
 */
public class DocService{

	public static final DocService me = new DocService();
	private static final Integer DOC_TYPE = 1;		//图档
	private static final Integer MOD_TYPE = 2;	//模型

	/**
	 * 根据传入的参数获取指定的文件列表
	 * @param project
	 * @param typeArr
	 * @return
	 */
	public Ret getAppointFile(String project, String typeArr) {
		if(StrKit.isBlank(project)){
			return Ret.fail("msg", "参数错误");
		}
		if(StrKit.isBlank(typeArr)){
			return Ret.fail("msg", "参数不能为空");
		}
		List<DocDTO> list = DocClientService.me.getDocList(project,typeArr);
		Map<String,Object> map = new HashMap<>();
		map.put("total", list.size());
		map.put("docList", list);
		return Ret.ok("data", map);
	}
	
	/*获取技术图档的总入口*/
	public Ret getDocList(UserPrincipal user) {
		
		String code = getStage(user);
		if (StrKit.isBlank(code)) {
			return Ret.fail("msg", "阶段获取失败");
		}
		
		Map<String, Object> map = getData(code,user.getProject());
		
		return Ret.ok("data", map);
	}
	
	/*获取施工柱状图数据*/
	public Ret getConstructionHistogram(UserPrincipal user) {
		String stageCode = getStage(user);
		if (StrKit.isBlank(stageCode)) {
			return Ret.fail("msg", "阶段获取失败");
		}
		List<OrganizationDTO> list = DocClientService.me.getOrganization(user.getProject());
		if(ObjKit.empty(list)){
			return Ret.fail("msg", "获取组织失败");
		}
		List<Map<String, Object>> constructionList = assembleData(getDefaultVal(list),getNowHistogramOfConstruction(stageCode,user.getProject()));
		return Ret.ok("data", constructionList);
	}

	private List<Map<String, Object>> assembleData(List<Map<String, Object>> defaultList, List<Record> nowHistogram) {
		if(ObjKit.empty(nowHistogram)){
			return defaultList;
		}else{
			for (Record record : nowHistogram) {
				System.out.println(record);
				for (Map<String, Object> map : defaultList) {
					System.out.println(map);
					if(map.get("name").equals(record.getStr("organization")) && DOC_TYPE == record.getInt("type")){
						map.put("docNum", record.get("countNum"));
					}else if(map.get("name").equals(record.getStr("organization")) && MOD_TYPE == record.getInt("type")){
						map.put("modNum", record.get("countNum"));
					}
				}
			}
		}
		return defaultList;
		
	}

	/*获取设计柱状图数据*/
	public Ret getDocHistogram(UserPrincipal user) {
		String stageCode = getStage(user);
		if (StrKit.isBlank(stageCode)) {
			return Ret.fail("msg", "阶段获取失败");
		}
		List<OrganizationDTO> list = DocClientService.me.getOrganization(user.getProject());
		if(ObjKit.empty(list)){
			return Ret.fail("msg", "获取组织失败");
		}
		List<Map<String, Object>> design = assembleData(getDefaultVal(list),getNowHistogramOfDesign(stageCode,user.getProject()));
		
		return Ret.ok("data", design);
	}
	
	
	private List<Map<String,Object>> getDefaultVal(List<OrganizationDTO> list) {
		List<Map<String,Object>> dataList = new ArrayList<>();
			for (int i = 0; i < list.size(); i++) {
				OrganizationDTO obj = list.get(i);
				Map<String,Object> map = new TreeMap<>();
//				System.out.println(obj);
				map.put("name", obj.getName());
				map.put("docNum", 0);
				map.put("modNum", 0);
				dataList.add(map);
			}
			return dataList;
	}

	/*组装成map数据*/
	private Map<String,Object> getData(String code, String project) {
		Map<String,Object> map = new HashMap<>();
		Record record = getNowDesignDocUploadTotal(code,project);
		List<Record> designDoc = groupByCountDesignDocTotal(code,project);
		Record record2 = getNowCountconstructionDocUploadTotal(code,project);
		List<Record> constructionDoc = groupByCountconstructionDocTotal(code,project);
		map.put("designDoc", encapsulate(record,designDoc));
		map.put("constructionDoc", encapsulate(record2,constructionDoc));
		return map;
}

	/*封装数据*/
	private Record encapsulate(Record record, List<Record> designDoc) {
		if(designDoc.size() > 0){
			for (int i = 0; i < designDoc.size(); i++) {
				Record bean = designDoc.get(i);
				if(1 == bean.getInt("type")){
					record.set("docNum", bean.get("countNum"));
				}else{
					record.set("modNum", bean.get("countNum"));
				}
			}
		}else{
			record.set("docNum", 0);
			record.set("modNum", 0);
		}
		return record;
	}

	/**
	 * 获取设计文档的阶段编码
	 * 设计文档 = 前期资料 + 方案设计 + 初步设计 + 施工图设计
	 * 施工文档 = 其余文档
	 * @return (936,940,941,942)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private String getStage(UserPrincipal user) {
		/*获取项目阶段*/
		Ret ret = ClientService.me.getStages(user);
		if(ret.isOk()){
			ArrayList<DictDTO> list = (ArrayList) ret.get("data");
			StringBuilder code = new StringBuilder("(");
			for (DictDTO obj : list) {
				if(DocType.STAGE_QQZL.equals(obj.getName())
						|| DocType.STAGE_FASJ.equals(obj.getName())
						|| DocType.STAGE_CBSJ.equals(obj.getName())
						|| DocType.STAGE_SGTSJ.equals(obj.getName())
					){
						code.append(obj.getCode()).append(",");	//将设计文档的Id存入
				}
			}
			if (code.length() > 0) {
				code.replace(code.length() - 1, code.length(), ")");	//将最后的“，”替换为")"
				return code.toString();
			}
		}
		return null;
	}

	/*获取今天上传的设计文档总数*/
	private Record getNowDesignDocUploadTotal(String code, String project){
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(id) nowDesignTotal FROM log_document ");
		sql.append(" WHERE project=? AND  date_format(createAt,'%Y-%m-%d')=date_format(NOW(),'%Y-%m-%d') ");
		sql.append(" AND stageId in ").append(code);
		return Db.findFirst(sql.toString(),project);
	}
	
	/*按类型分组统计设计文档的文件上传总数*/
	private List<Record> groupByCountDesignDocTotal(String code, String project){
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(id) countNum,type FROM log_document ");
		sql.append(" WHERE project=? AND stageId in ").append(code);
		sql.append(" GROUP BY type");
		return Db.find(sql.toString(),project);
	}
	
	/*获取今天上传的施工文档总数*/
	private Record getNowCountconstructionDocUploadTotal(String code, String project){
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(id) nowConsTotal FROM log_document ");
		sql.append(" WHERE project=? AND  date_format(createAt,'%Y-%m-%d')=date_format(NOW(),'%Y-%m-%d') ");
		sql.append(" AND stageId not in").append(code);
		return Db.findFirst(sql.toString(),project);
	}
	
	
	/*按类型分组统计施工文档的文件上传总数*/
	private List<Record> groupByCountconstructionDocTotal(String code, String project){
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(id) countNum,type FROM log_document ");
		sql.append(" WHERE project=? AND stageId not in ").append(code);
		sql.append(" GROUP BY type");
		return Db.find(sql.toString(),project);
	}

	/**
	 * 获取当天设计文档柱形图数据
	 * @param stageCode
	 * @param project
	 */
	private List<Record> getNowHistogramOfDesign(String stageCode, String project) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT count(id) countNum,organization,type FROM log_document ");
		sql.append(" WHERE project=? AND date_format(createAt,'%Y-%m-%d')=date_format(NOW(),'%Y-%m-%d')  ");
//		sql.append(" WHERE project=? AND date_format(createAt,'%Y-%m-%d')='2019-07-11'  ");
		sql.append(" AND stageId in ").append(stageCode);
		sql.append(" GROUP BY organization,type ");
		return Db.find(sql.toString(), project);
	}
	
	/**
	 * 获取当天施工文档柱形图数据
	 * @param stageCode
	 * @param project
	 */
	private List<Record> getNowHistogramOfConstruction(String stageCode, String project) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT count(id) countNum,organization,type FROM log_document ");
		sql.append(" WHERE project=? AND date_format(createAt,'%Y-%m-%d')=date_format(NOW(),'%Y-%m-%d')  ");
		sql.append(" AND stageId not in ").append(stageCode);
		sql.append(" GROUP BY organization,type ");
		return Db.find(sql.toString(), project);
	}

	
	

	
	
	
	
	
}
