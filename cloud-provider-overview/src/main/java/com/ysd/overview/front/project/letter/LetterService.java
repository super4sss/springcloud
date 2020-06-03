package com.ysd.overview.front.project.letter;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.ysd.overview.common.auth.UserPrincipal;
import com.ysd.overview.common.kit.ObjKit;
import com.ysd.overview.common.model.Folder;
import com.ysd.overview.common.model.Letter;
import com.ysd.overview.common.page.Paginator;
import com.ysd.overview.common.page.QueryHelper;
import com.ysd.overview.front.index.FolderService;

/**
 * 项目函件业务处理层
 * @author Administrator
 * @date 2019年3月11日
 *
 */
public class LetterService {

	public static final LetterService me = new LetterService();
	private Letter dao = new Letter().dao();

	/**
	 * 函件保存
	 * @param letter			函件实体
	 * @param fileToken		文件token
	 * @param projectUser	用户实体
	 * @return
	 */
	public Ret save(Letter letter, String fileToken, UserPrincipal projectUser) {
		letter.setProject(projectUser.getProject());
		letter.setCreateAt(new Date());
		letter.setCreatorNum(projectUser.getUser());
		letter.setCreator(projectUser.getUsername());
		System.out.println(letter);
		Db.tx(new IAtom() {	// Execute transaction with default transaction level.
			
			@Override
			public boolean run() throws SQLException {
				letter.save();
				updateFolder(letter.getId(),fileToken);
				return true;
			}
		});
		
		return Ret.ok("msg","函件新增成功");
	}

	public void updateFolder(Long id, String fileToken) {
		if (ObjKit.empty(id)) return;
		FolderService.me.updateByName(fileToken, Folder.ENTITY_I_LETTER, id.toString());
	}

	/**
	 * 通过项目标识查询函件列表
	 * @param project	项目编码
	 * @return	当前角色的函件列表
	 */
	public Page<Letter> getLettersByProject(String project, Paginator paginator) {
		QueryHelper<Letter> query = new QueryHelper<>();
		query.append("select id,title,");
		query.append("creator,creatorNum,DATE_FORMAT(createAt,'%Y-%m-%d') createAt from pro_letter");
		query.where("project=?").param(project);
		query.order("id desc");
		return query.paginate(dao, paginator.getPageNo(), paginator.getPageSize());
	}

	/**
	 * 通过id查询实体是否存在
	 * @param paraToLong		函件实体id
	 * @return		函件实体信息
	 */
	public Letter findById(Long letterId) {
		return dao.findById(letterId);
	}

	/**
	 * 通過實體id查詢文件名
	 * @param letterId	實體id
	 * @return	文件名稱
	 */
	public String getFolderName(Long letterId) {
		return FolderService.me.getFileName(Folder.ENTITY_I_LETTER, letterId.toString());
	}

	/**
	 * 更新
	 * @param bean		函件實體
	 * @param para		文件token
	 * @return
	 */
	public Ret update(Letter bean, String fileToken) {
		Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				bean.update();
				updateFolder(bean.getId(),fileToken);
				return true;
			}
		});
		return Ret.ok("msg", "函件更新成功");
	}

	/**
	 * 删除
	 * @param entityId	函件实体
	 * @param principal	用户实体
	 * @return
	 */
	public Ret delete(Long entityId, UserPrincipal principal) {
		Letter bean = findById(entityId);
		if(ObjKit.empty(bean)){
			return Ret.fail("msg", "项目实体不存在，请检查");
		}
		/*if(!principal.isOwner()){
			return Ret.fail("msg", "权限不足，不允许删除");
		}*/
		if(bean.delete()){
			FolderService.me.deleteByEntity(Folder.ENTITY_I_LETTER, entityId.toString());
		}
		
		return Ret.ok("msg", "删除成功");
	}
	
	/**
	 * 删除上传文件
	 */
	public Ret delFiles(String token, String fileIds, UserPrincipal user) {
		return FolderService.me.delFiles(token, fileIds, letterId -> {
			Letter bean = findById(letterId);
			if (ObjKit.empty(bean)) {
				return Ret.fail("msg", "函件不存在，请检查");
			}
			/*if (!user.isOwner()) {
				return Ret.fail("msg", "文件不允许删除，请联系负责人");
			}*/
			return Ret.ok();
		});
	}

	/**
	 * 获取函件详情
	 * @param letterId
	 * @return
	 */
	public Ret getLetterDetailById(Long letterId) {
		Letter letter = dao.findById(letterId);
		if(ObjKit.empty(letter)){
			return Ret.fail("msg", "函件不存在，请检查");
		}
		return Ret.ok("bean", letter);
	}
	
}
