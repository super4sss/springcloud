package com.ysd.overview.front.project.newspaper;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.ysd.overview.common.auth.UserPrincipal;
import com.ysd.overview.common.kit.ObjKit;
import com.ysd.overview.common.model.Folder;
import com.ysd.overview.common.model.WeeklyNewspaper;
import com.ysd.overview.common.page.Paginator;
import com.ysd.overview.common.page.QueryHelper;
import com.ysd.overview.front.index.FolderService;

/*
 * File WeeklyNewspaperService.java
 * --------------------------------------
 * 周报管理业务层
 * 负责处理action传递的数据处理，返回结果
 */
public class WeeklyNewspaperService {
	
	public static final WeeklyNewspaperService paperService = new WeeklyNewspaperService();
	
	private WeeklyNewspaper dao = new WeeklyNewspaper().dao();

	/**
	 * 通过项目编码获取周报列表
	 * @param project
	 * @return
	 */
	public Ret getPaperByProject(String project, Paginator paginator) {
		QueryHelper<WeeklyNewspaper> query = new QueryHelper<>();
		query.append("select id,title,content,DATE_FORMAT(createAt,'%Y-%m-%d') createAt,creator,creatorNum from pro_weekly_newspaper");
		query.where("project=?").param(project);
		query.order("id desc");
		Page<WeeklyNewspaper> list = query.paginate(dao, paginator.getPageNo(), paginator.getPageSize());
		return Ret.ok("data", list);
	}
	
	
	/**
	 * 新建
	 * @param bean				周报实体
	 * @param fileToken		文件主目录名称
	 * @param user				用户实体
	 * @return
	 */
	public Ret save(WeeklyNewspaper bean, String fileToken, UserPrincipal user) {
		bean.setCreateAt(new Date());
		bean.setCreator(user.getUsername());
		bean.setCreatorNum(user.getUser());
		bean.setProject(user.getProject());
		Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				bean.save();
				updateFolder(bean.getId(),fileToken);
				return true;
			}
		});
		return Ret.ok("msg", "周报新增成功");
	}

	public void updateFolder(Long id, String fileToken) {
		if (ObjKit.empty(id)) return;
		FolderService.me.updateByName(fileToken, Folder.ENTITY_I_PAPER, id.toString());
	}

	
	public WeeklyNewspaper findById(Long paperId) {
	
		return dao.findById(paperId);
	}

	/**
	 * 获取文件名
	 * @param id	周报实体id
	 * @return
	 */
	public String getFolderName(Long id) {
		return FolderService.me.getFileName(Folder.ENTITY_I_PAPER, id.toString());
	}

	/**
	 * 修改
	 * @param bean		周报实体
	 * @param para		文件token
	 * @return
	 */
	public Ret update(WeeklyNewspaper bean, String fileToken) {
		Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				bean.update();
				updateFolder(bean.getId(),fileToken);
				return true;
			}
		});
		return Ret.ok("msg", "周报更新成功");
	}

	/**
	 * 删除
	 * @param id
	 * @param user
	 * @return
	 */
	public Ret delete(Long id, UserPrincipal user) {
		WeeklyNewspaper bean = dao.findById(id);
		if(ObjKit.empty(bean)){
			return Ret.fail("msg", "项目实体不存在，请检查");
		}
		/*if(!user.isOwner()){
			return Ret.fail("msg", "权限不足，不允许删除");
		}*/
		if(bean.delete()){
			FolderService.me.deleteByEntity(Folder.ENTITY_I_PAPER, id.toString());
		}
		
		return Ret.ok("msg", "删除成功");
	}

	/**
	 * 通过周报id获取周报详情
	 * @param paperId
	 * @return
	 */
	public Ret getNewspaperDetailsById(Long paperId) {
		WeeklyNewspaper bean = dao.findById(paperId);
		if(ObjKit.empty(bean)){
			return Ret.fail("msg", "周报不存在，请检查");
		}
		return Ret.ok("bean", bean);
	}


	public Ret delFiles(String token, String fileIds, UserPrincipal user) {
		
		return FolderService.me.delFiles(token, fileIds, paperId -> {
			WeeklyNewspaper bean = findById(paperId);
			if (ObjKit.empty(bean)) {
				return Ret.fail("msg", "函件不存在，请检查");
			}
			/*if (!user.isOwner()) {
				return Ret.fail("msg", "文件不允许删除，请联系负责人");
			}*/
			return Ret.ok();
		});
	}

	
	
}
