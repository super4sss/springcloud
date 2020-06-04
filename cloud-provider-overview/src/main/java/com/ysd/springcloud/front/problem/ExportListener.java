package com.ysd.springcloud.front.problem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.ss.usermodel.Workbook;

import com.jfinal.kit.Kv;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.ysd.springcloud.common.kit.ImgKit;
import com.ysd.springcloud.common.model.Problem;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import net.dreamlu.event.core.EventListener;

/**
 * 导出监听
 */
public class ExportListener {

	@SuppressWarnings("unchecked")
	@EventListener(order = 1, async = true)
	public void doExportExcel(ExportEvent event) {
		LogKit.warn("ExportExcel start...");
		
		Problem form = event.getSource();
		List<Problem> list = ProblemService.me.findByExport(form);
		
		TemplateExportParams params = new TemplateExportParams(
				getExcelTemplatePath(), true);
		Workbook book = ExcelExportUtil.exportExcel(params, Kv.by("list", list));
		
		try {
			String fileName = saveExportFile(book);
			String sql = "update log_problem set `fileName`=?,`status`=? where id=?";
			Db.update(sql, fileName, Problem.ACTION_DONE, form.getId());
		} catch (Exception e) {
			LogKit.error("保存导出文件失败", e);
		}
		
		LogKit.warn("ExportExcel finish!!!");
	}
	
	private String getExcelTemplatePath() {
		return PropKit.get("system.picPath") + "/problem/exportTemplate.xlsx";
	}
	
	private String saveExportFile(Workbook workbook) throws Exception {
		Date now = new Date();
		String savePath = "/problem/" + DateFormatUtils.format(now, "yyyyMMdd");
		String fileName = "export_" + DateFormatUtils.format(now, "HHmmss") + ImgKit.getRandomName(6) + ".xlsx";
		String absolutePath = PropKit.get("system.picPath") + savePath;
		
		OutputStream out = null;
		try {
			File dir = new File(absolutePath);
	        if (!dir.exists()) dir.mkdirs();
	        
        	out = new FileOutputStream(new File(absolutePath, fileName));
            workbook.write(out);
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
	
}
