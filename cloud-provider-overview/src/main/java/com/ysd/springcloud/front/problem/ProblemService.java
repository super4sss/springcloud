package com.ysd.springcloud.front.problem;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.result.ExcelImportResult;
import com.google.common.collect.ImmutableMap;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.ysd.springcloud.common.auth.UserPrincipal;
import com.ysd.springcloud.common.dto.ExportDTO;
import com.ysd.springcloud.common.kit.FileKit;
import com.ysd.springcloud.common.kit.ImgKit;
import com.ysd.springcloud.common.kit.ObjKit;
import com.ysd.springcloud.common.model.Problem;
import com.ysd.springcloud.common.page.Paginator;
import com.ysd.springcloud.common.page.QueryHelper;
import net.dreamlu.event.EventKit;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * 项目问题服务
 */
public class ProblemService {

	public static final ProblemService me = new ProblemService();
	private Problem dao = new Problem().dao();
	
	public Page<Problem> paginate(Paginator page, Problem form) {
		QueryHelper<Problem> query = new QueryHelper<>();
		query.append("from pro_problem");
		where(query, form).order("id asc");
		return query.paginate(dao, page.getPageNo(), page.getPageSize());
	}
	
	private QueryHelper<Problem> where(QueryHelper<Problem> query, Problem form) {
		query.where("type = ?", form.getType());
		query.where("category = ?", form.getCategory());
		query.where("specialty = ?", form.getSpecialty());
		query.where("status = ?", form.getStatus());
		query.where("belongsGroup like ?", form.getBelongsGroup(), "%%%s%%", 1);
		query.where("project = ?").param(form.getProject());
		return query;
	}
	
	public Long countByExport(Problem form) {
		QueryHelper<Problem> query = new QueryHelper<>();
		query.append("select count(*) from pro_problem");
		where(query, form);
		return Db.queryLong(query.getSql(), query.getParas());
	}
	
	public List<Problem> findByExport(Problem form) {
		QueryHelper<Problem> query = new QueryHelper<>();
		query.append("select * from pro_problem");
		return where(query, form).order("id asc").find(dao);
	}
	
	public Long getByCode(String code, String project) {
		String sql = "select id from pro_problem where code=? and project=?";
		return Db.queryLong(sql, code, project);
	}
	
	public List<ExportDTO> findExportRecord(UserPrincipal user) {
		QueryHelper<Problem> query = new QueryHelper<>();
		query.append("select id,fileName,remark,createAt,status from log_problem");
		query.where("project = ?").param(user.getProject());
		query.where("creator = ?").param(user.getUser());
		query.where("type = ?").param(Problem.ACTION_EXPORT);
		query.where("status = ?").param(Problem.ACTION_DONE);
		query.order("id desc");
		List<Record> list = Db.find(query.getSql(), query.getParas());
		return list.stream().map(ExportDTO::onRecord).collect(toList());
	}
	
	public ExportDTO getExportRecord(Long recordId) {
		String sql = "select id,fileName,remark,createAt,status from log_problem where id=?";
		Record record = Db.findFirst(sql, recordId);
		return ObjKit.notEmpty(record) ? ExportDTO.onRecord(record) : null;
	}
	
	public Ret importExcel(UploadFile uf, UserPrincipal user) {
		if (ObjKit.empty(uf) 
				|| ObjKit.empty(uf.getFile())) {
			return Ret.fail("msg", "文件对象不能为空，请检查");
		}
		if (!user.isOwner()) {
			uf.getFile().delete();
			return Ret.fail("msg", "文件不允许导入，请联系负责人");
		}
		
		ImportParams params = new ImportParams();
		//params.setTitleRows(1);
        //params.setHeadRows(1);
        
		ExcelImportResult<Map<String, Object>> result = 
				ExcelImportUtil.importExcelMore(uf.getFile(), Map.class, params);
		
		List<Map<String, Object>> list = result.getList();
		if (ObjKit.empty(list)) {
			return Ret.ok("okCount", 0).set("failCount", 0);
		}
		
		int okCount = 0, failCount = 0;
		Workbook workbook = result.getWorkbook();
		Map<String, CellStyle> styleMap = createCellStyle(workbook);
		
		for (int i = 0; i < list.size(); i++) {
			Ret ret = ImportVerifyHandler.verify(list.get(i), user.getProject());
      System.out.println(ret.toString());
			if (ret.isFail()) {
				failCount++;
				wirteImportMsg(workbook, i, ret.getStr("msg"), styleMap.get("failStyle"));
				continue;
			}
			try {
				Db.update(ret.getStr("sql"));
				okCount++;
				wirteImportMsg(workbook, i, ret.getStr("msg")+" success", styleMap.get("okStyle"));
			} catch (Exception e) {
				LogKit.error("保存导入记录失败", e);
				failCount++;
				wirteImportMsg(workbook, i, ret.getStr("msg")+" fail", styleMap.get("failStyle"));
			}
		}
		
		return Ret.ok("okCount", okCount)
				.set("failCount", failCount)
                .set("fileName", saveImportLog(workbook, uf, user));
	}
	
	private Map<String, CellStyle> createCellStyle(Workbook workbook) {
        Font okFont = workbook.createFont();
        okFont.setFontName("Calibri");
        okFont.setColor(HSSFColor.GREEN.index);
        CellStyle okStyle = workbook.createCellStyle();
        okStyle.setFont(okFont);
        
        Font failFont = workbook.createFont();
        failFont.setFontName("Calibri");
        failFont.setColor(HSSFColor.RED.index);
        CellStyle failStyle = workbook.createCellStyle();
        failStyle.setFont(failFont);
        
        return ImmutableMap.of("okStyle", okStyle, "failStyle", failStyle);
	}
	
	private void wirteImportMsg(Workbook workbook, int index, String msg, CellStyle style) {
		Sheet sheet = workbook.getSheetAt(0);
		Row row = sheet.getRow(index + 1);
		short lastCellNum = row.getLastCellNum();
		Cell cell = row.createCell(lastCellNum);
		cell.setCellStyle(style);
        cell.setCellValue(msg);
	}
	
	private String saveImportFile(Workbook workbook) {
		Date now = new Date();
		String savePath = "/problem/" + DateFormatUtils.format(now, "yyyyMMdd");
		String fileName = "import_" + DateFormatUtils.format(now, "HHmmss") + ImgKit.getRandomName(6) + ".xlsx";
		String absolutePath = PropKit.get("system.picPath") + savePath;
		
		OutputStream out = null;
		try {
			File dir = new File(absolutePath);
	        if (!dir.exists()) dir.mkdirs();
	        
        	out = new FileOutputStream(new File(absolutePath, fileName));
            workbook.write(out);
        } catch (Exception e) {
        	LogKit.error("保存导入文件失败", e);
        } finally {
        	if (out != null) {
        		try {
                	out.flush();
                	out.close();
                } catch (IOException ignore) {}
        	}
        }
		
		return savePath + "/" + fileName;
	}
	
	public String saveImportLog(Workbook workbook, UploadFile uf, UserPrincipal user) {
		String fileName = saveImportFile(workbook);
		
		Record record = new Record()
				.set("type", Problem.ACTION_IMPORT)
				.set("fileName", fileName)
				.set("creator", user.getUser())
				.set("creatorName", user.getUsername())
				.set("createAt", new Date())
				.set("project", user.getProject())
				.set("status", Problem.ACTION_DONE);
		
		try {
			Db.save("log_problem", record);
		} catch (Exception e) {
			LogKit.error("保存导入日志失败", e);
		} finally {
			try {
				uf.getFile().delete();
			} catch (Exception ignore) {}
		}
		return fileName;
	}
	
	public Ret exportExcel(Problem form, UserPrincipal user) {
		if (StrKit.isBlank(form.getDescription())) {
			return Ret.fail("msg", "导出备注不能为空，请检查");
		}
		Long count = countByExport(form);
		if (!ObjKit.isDigits(count)) {
			return Ret.fail("msg", "获取记录为空不做导出，请检查");
		}
		
		Record record = new Record()
				.set("type", Problem.ACTION_EXPORT)
				.set("creator", user.getUser())
				.set("creatorName", user.getUsername())
				.set("createAt", new Date())
				.set("project", user.getProject())
				.set("status", Problem.ACTION_START)
				.set("remark", form.getDescription());
		
		Db.save("log_problem", record);
		EventKit.post(new ExportEvent(form.set("id", record.getLong("id"))));
		
		return Ret.ok("data", ExportDTO.onRecord(record));
	}
	
	/**
	 * 删除导入问题
	 */
	public Ret deleteImport(Long problemId, UserPrincipal user) {
		Problem bean = dao.findById(problemId);
		if (ObjKit.empty(bean)) {
			return Ret.fail("msg", "问题对象不存在，请检查");
		}
		if (!user.isOwner()) {
			return Ret.fail("msg", "问题不允许删除，请联系负责人");
		}
		if (!bean.delete()) {
			return Ret.fail("msg", "问题删除失败，请联系管理员");
		}
		return Ret.ok("msg", "导入问题删除成功");
	}
	
	/**
	 * 删除导出文件
	 */
	public Ret deleteExport(Long recordId, UserPrincipal user) {
		String sql = "select * from log_problem where id=?";
		Record record = Db.findFirst(sql, recordId);
		if (ObjKit.empty(record)) {
			return Ret.fail("msg", "导出记录不存在，请检查");
		}
		if (ObjKit.notEquals(record.getStr("creator"), user.getUser())) {
			return Ret.fail("msg", "文件不允许删除，请联系导出者");
		}
		if (Db.delete("log_problem", record)) {
			String filePath = PropKit.get("system.picPath") + record.getStr("fileName");
			FileKit.deleteQuietly(filePath);
		}
		return Ret.ok("msg", "导出文件删除成功");
	}
	
	/**
	 * 下载问题文件
	 */
	public Ret downloadExcel(Long recordId) {
		if (!ObjKit.isDigits(recordId)) {
			return Ret.ok("path", getExcelTemplatePath()).set("name", getExcelTemplateName());
		}
		String sql = "select * from log_problem where id=?";
		Record record = Db.findFirst(sql, recordId);
		if (ObjKit.notEmpty(record)) {
			String name = record.getStr("fileName");
			String path = PropKit.get("system.picPath") + name;
			return Ret.ok("path", path).set("name", StringUtils.substringAfterLast(name, "/"));
		} else {
			return Ret.fail("msg", "导出文件未找到");
		}
	}
	
	public String getExcelTempDir() {
		return "/problem/" + StrKit.getRandomUUID();
	}
	
	public String getExcelTemplatePath() {
		return PropKit.get("system.picPath") + "/problem/importTemplate.xlsx";
	}
	
	public String getExcelTemplateName() {
		return "质量安全导入模板_" + System.currentTimeMillis() + ".xlsx";
	}
	
}
