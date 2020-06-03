package com.ysd.overview.front.vrshow;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.ysd.overview.common.auth.UserPrincipal;
import com.ysd.overview.common.kit.ObjKit;
import com.ysd.overview.common.model.Folder;
import com.ysd.overview.common.model.Vrshow;
import com.ysd.overview.common.page.QueryHelper;
import com.ysd.overview.front.index.FolderService;

/**
 * VR展示服务
 */
public class VrshowService {

	public static final VrshowService me = new VrshowService();
	private Vrshow dao = new Vrshow().dao();
	
	public List<Vrshow> findByStage(Vrshow bean) {
		if (StrKit.isBlank(bean.getProject()) 
				|| StrKit.isBlank(bean.getStage())) {
			return Collections.emptyList();
		}
		QueryHelper<Vrshow> query = new QueryHelper<>();
		query.append("select id,name,viewPath,picPath,");
		query.append("creator,creatorName,createAt from pro_vrshow");
		query.where("project = ?").param(bean.getProject());
		query.where("stage = ?").param(bean.getStage());
		query.order("id desc");
		return dao.find(query.getSql(), query.getParas());
	}
	
	public Vrshow findById(Long vrShowId) {
		return dao.findById(vrShowId);
	}
	
	/**
	 * 创建VR展示
	 */
	public Ret save(Vrshow bean, String fileToken, UserPrincipal user) {
		bean.setName(bean.getName().trim());
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
		return Ret.ok("msg", "VR展示创建成功");
	}
	
	/**
	 * 更新VR展示
	 */
	public Ret update(Vrshow bean, String fileToken) {
		bean.setName(bean.getName().trim());
		Db.tx(new IAtom() {
			public boolean run() throws SQLException {
				bean.update();
				updateFolder(bean.getId(), fileToken);
				return true;
			}
		});
		return Ret.ok("msg", "VR展示更新成功");
	}
	
	public void updateFolder(Long vrShowId, String fileToken) {
		if (ObjKit.empty(vrShowId)) return;
		FolderService.me.updateByName(fileToken, Folder.ENTITY_I_VRSHOW, vrShowId.toString());
	}
	
	/**
	 * 删除VR展示
	 */
	public Ret delete(Long vrShowId, UserPrincipal user) {
		Vrshow bean = findById(vrShowId);
		if (ObjKit.empty(bean)) {
			return Ret.fail("msg", "VR展示不存在，请检查");
		}
		/*if (!user.isOwner()) {
			boolean isOwner = ClientService.me.isOwner(bean.getStage(), user);
			if (!isOwner) return Ret.fail("msg", "VR展示不允许删除，请联系负责人");
		}*/
		if (bean.delete()) {
			FolderService.me.deleteByEntity(Folder.ENTITY_I_VRSHOW, vrShowId.toString());
		}
		return Ret.ok("msg", "VR展示删除成功");
	}
	
	/**
	 * 删除上传文件
	 */
	public Ret delFiles(String token, String fileIds, UserPrincipal user) {
		return FolderService.me.delFiles(token, fileIds, entityId -> {
			Vrshow bean = findById(entityId);
			if (ObjKit.empty(bean)) {
				return Ret.fail("msg", "VR展示不存在，请检查");
			}
			/*if (!user.isOwner()) {
				boolean isOwner = ClientService.me.isOwner(bean.getStage(), user);
				if (!isOwner) return Ret.fail("msg", "文件不允许删除，请联系负责人");
			}*/
			return Ret.ok();
		});
	}

	


	/**
	 * 查詢數據
	 * @param bean
	 * @return
	 * @version 2.0.0
	 */
	public List<Vrshow> findByV2(Vrshow bean) {
		if (StrKit.isBlank(bean.getProject()) ) {
			return Collections.emptyList();
		}
		QueryHelper<Vrshow> query = new QueryHelper<>();
		query.append("select id,name,viewPath,picPath,");
		query.append("creator,creatorName,createAt from pro_vrshow");
		query.where("project = ?").param(bean.getProject());
		query.order("id desc");
		return dao.find(query.getSql(), query.getParas());
	}

	}
