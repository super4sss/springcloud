package com.ysd.springcloud.front.project.organizer;

import java.util.List;

import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.ysd.springcloud.common.auth.UserPrincipal;
import com.ysd.springcloud.common.kit.ObjKit;
import com.ysd.springcloud.common.model.Organizer;
import com.ysd.springcloud.common.page.Paginator;
import com.ysd.springcloud.common.page.QueryHelper;

/**
 * 承办单位服务
 */
public class OrganizerService {

	public static final OrganizerService me = new OrganizerService();
	private Organizer dao = new Organizer().dao();
	
	public Page<Organizer> paginate(Paginator page, Organizer form) {
		QueryHelper<Organizer> query = new QueryHelper<>();
		query.append("select id,parties,unitName,unitCode,");
		query.append("contacts,address,phone,telephone,fax,sort");
		query.append(" from pro_organizer");
		query.where("project = ?").param(form.getProject());
		query.where("(unitName like ? or address like ?)", form.getUnitName(), "%%%s%%", 2);
		query.order("sort asc");
		return query.paginate(dao, page.getPageNo(), page.getPageSize());
	}
	
	public List<Organizer> findByProject(String project) {
		String sql = "select parties,unitName from pro_organizer where project=? order by sort";
		return dao.find(sql, project);
	}
	
	public Organizer findById(Long orgId) {
		return ObjKit.notEmpty(orgId) ? dao.findById(orgId) : null;
	}
	
	public int getSeqNo(String project) {
		String sql = "select max(sort) from pro_organizer where project=?";
		Integer num = Db.queryInt(sql, project);
		return num != null ? num + 1 : 1;
	}
	
	/**
	 * 创建单位
	 */
	public Ret save(Organizer bean, UserPrincipal user) {
		bean.setParties(bean.getParties().trim());
		bean.setUnitName(bean.getUnitName().trim());
		if (ObjKit.empty(bean.getSort())) {
			bean.setSort(getSeqNo(user.getProject()));
		}
		bean.setProject(user.getProject());
		if (bean.save()) {
			return Ret.ok("msg", "单位创建成功");
		}
		return Ret.fail("msg", "单位创建失败");
	}
	
	/**
	 * 更新单位
	 */
	public Ret update(Organizer bean) {
		bean.setParties(bean.getParties().trim());
		bean.setUnitName(bean.getUnitName().trim());
		if (bean.update()) {
			return Ret.ok("msg", "单位更新成功");
		}
		return Ret.fail("msg", "单位更新失败");
	}
	
	/**
	 * 删除VR展示
	 */
	public Ret delete(Long orgId, UserPrincipal user) {
		Organizer bean = findById(orgId);
		if (ObjKit.empty(bean)) {
			return Ret.fail("msg", "单位实体不存在，请检查");
		}
		if (bean.delete()) {
			return Ret.ok("msg", "单位删除成功");
		}
		return Ret.fail("msg", "单位删除失败");
	}
	
}
