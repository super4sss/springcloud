package com.ysd.overview.front.work;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.google.common.collect.ImmutableMap;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.upload.UploadFile;
import com.ysd.overview.common.auth.UserPrincipal;
import com.ysd.overview.common.kit.ImgKit;
import com.ysd.overview.common.kit.ObjKit;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.result.ExcelImportResult;

/**
 * 劳务工人服务
 */
public class WorkService {

	public static final WorkService me = new WorkService();
	
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
			if (ret.isFail()) {
				failCount++;
				wirteImportMsg(workbook, i, ret.getStr("msg"), styleMap.get("failStyle"));
				continue;
			}
			try {
				List<String> sqls = ret.getAs("sqls");
				Db.batch(sqls, sqls.size());
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
                .set("fileName", saveImportFile(workbook, uf));
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
	
	private String saveImportFile(Workbook workbook, UploadFile uf) {
		Date now = new Date();
		String savePath = "/labor/" + DateFormatUtils.format(now, "yyyyMMdd");
		String fileName = "importWorker_" + DateFormatUtils.format(now, "HHmmss") + ImgKit.getRandomName(6) + ".xlsx";
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
        	try {
    			uf.getFile().delete();
    		} catch (Exception ignore) {}
        }
		
		return savePath + "/" + fileName;
	}
	
	public String getExcelTempDir() {
		return "/labor/" + StrKit.getRandomUUID();
	}
	
}
