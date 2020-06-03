package com.ysd.overview.front.project.event;

import java.sql.SQLException;
import java.util.Date;

import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.ysd.overview.common.auth.UserPrincipal;
import com.ysd.overview.common.kit.ObjKit;
import com.ysd.overview.common.model.Event;
import com.ysd.overview.common.model.Folder;
import com.ysd.overview.common.page.Paginator;
import com.ysd.overview.common.page.QueryHelper;
import com.ysd.overview.front.index.FolderService;

/**
 * 项目事件服务
 */
public class EventService {

	public static final EventService me = new EventService();
	private Event dao = new Event().dao();
	
	public Page<Event> paginate(Paginator page, UserPrincipal user) {
		QueryHelper<Event> query = new QueryHelper<>();
		query.append("select id,title,creator,");
		query.append("creatorName,createAt from pro_event");
		query.where("project = ?").param(user.getProject());
		query.order("id desc");
		return query.paginate(dao, page.getPageNo(), page.getPageSize());
	}
	
	public Event findById(Long eventId) {
		return dao.findById(eventId);
	}
	
	public String getFolderName(Long eventId) {
		return FolderService.me.getFileName(Folder.ENTITY_I_EVENT, eventId.toString());
	}
	
	/**
	 * 创建项目事件
	 */
	public Ret save(Event bean, String fileToken, UserPrincipal user) {
		bean.setProject(user.getProject());
		bean.setCreator(user.getUser());
		bean.setCreatorName(user.getUsername());
		bean.setCreateAt(new Date());
		Db.tx(new IAtom() {
			public boolean run() throws SQLException {
				bean.save();
				updateFolder(bean.getId(), fileToken);
				return true;
			}
		});
		return Ret.ok("msg", "事件创建成功");
	}
	
	/**
	 * 更新项目事件
	 */
	public Ret update(Event bean, String fileToken) {
		Db.tx(new IAtom() {
			public boolean run() throws SQLException {
				bean.update();
				updateFolder(bean.getId(), fileToken);
				return true;
			}
		});
		return Ret.ok("msg", "事件更新成功");
	}
	
	public void updateFolder(Long eventId, String fileToken) {
		if (ObjKit.empty(eventId)) return;
		FolderService.me.updateByName(fileToken, Folder.ENTITY_I_EVENT, eventId.toString());
	}
	
	/**
	 * 删除项目事件
	 */
	public Ret delete(Long eventId, UserPrincipal user) {
		Event bean = findById(eventId);
		if (ObjKit.empty(bean)) {
			return Ret.fail("msg", "事件不存在，请检查");
		}
		/*if (!user.isOwner()) {
			return Ret.fail("msg", "事件不允许删除，请联系负责人");
		}*/
		if (bean.delete()) {
			FolderService.me.deleteByEntity(Folder.ENTITY_I_EVENT, eventId.toString());
		}
		return Ret.ok("msg", "事件删除成功");
	}
	
	/**
	 * 删除上传文件
	 */
	public Ret delFiles(String token, String fileIds, UserPrincipal user) {
		return FolderService.me.delFiles(token, fileIds, eventId -> {
			Event bean = findById(eventId);
			if (ObjKit.empty(bean)) {
				return Ret.fail("msg", "事件不存在，请检查");
			}
			/*if (!user.isOwner()) {
				return Ret.fail("msg", "文件不允许删除，请联系负责人");
			}*/
			return Ret.ok();
		});
	}
	
}
