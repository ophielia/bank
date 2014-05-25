package meg.bank.bus.db;

import java.util.List;

import meg.bank.bus.CategoryManager;
import meg.bank.bus.dao.CatRelationshipDao;
import meg.bank.bus.dao.CategoryDao;
import meg.bank.bus.dao.CategoryRuleDao;
import meg.bank.bus.dao.TargetDetailDao;
import meg.bank.bus.dao.TargetGroupDao;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class CategoryManagerDaoHib extends HibernateDaoSupport implements
		CategoryManagerDao {

	public void createOrSaveCategory(CategoryDao cat) {
		if (cat.getDisplayinlist() == null) {
			cat.setDisplayinlist(new Boolean(true));
		}
		// create the object in the DB
		getHibernateTemplate().saveOrUpdate(cat);
	}

	public void updateCategory(CategoryDao cat) {
		getHibernateTemplate().saveOrUpdate(cat);
	}

	public CategoryDao getCategoryByName(String name) {
		List result = null;
		Long id = null;
		CategoryDao test = null;
		result = getHibernateTemplate().find(
				"select id from CategoryDao as trans where trans.name = ?",
				name);

		if (result != null && result.size() > 0) {
			// pull the id off of the result list
			id = (Long) result.get(0);

			// now pull up that id
			test = (CategoryDao) getHibernateTemplate().get(CategoryDao.class,
					id);

		} else {
			test = null;
		}

		return test;

	}

	public List<CategoryDao> getCategories(boolean showall) {
		String sql = showall ? "from CategoryDao as cat order by name"
				: "from CategoryDao as cat where cat.displayinlist = 1 order by name";
		// create the object in the DB
		List categories = getHibernateTemplate().find(sql);

		return categories;

	}

	public void createOrSaveCategoryRel(CatRelationshipDao rel) {
		// create the object in the DB
		getHibernateTemplate().saveOrUpdate(rel);

	}

	public CatRelationshipDao getCategoryRel(Long parentid, Long childid) {
		List result = null;
		CatRelationshipDao catrel = null;
		result = getHibernateTemplate()
				.find(
						"from CatRelationshipDao as catrel where catrel.parentId = ? and catrel.childId = ? ",
						new Object[] { parentid, childid });

		if (result != null && result.size() > 0) {
			catrel = (CatRelationshipDao) result.get(0);

		}

		return catrel;
	}

	public List<CategoryDao> getDirectSubcategories(Long parentid) {
		List result = getHibernateTemplate()
				.find(
						"	from CategoryDao as cat where cat.id in  ( select rel.childId from CatRelationshipDao as rel where rel.parentId = ?)",
						new Object[] { parentid });

		return result;
	}

	public CategoryDao getCategory(Long id) {
		CategoryDao cat = (CategoryDao) getHibernateTemplate().get(
				CategoryDao.class, id);
		return cat;
	}

	public Long getParentIdForCat(Long id) {
		List result = null;
		CatRelationshipDao catrel = null;
		result = getHibernateTemplate().find(
				"from CatRelationshipDao as catrel where catrel.childId = ? ",
				new Object[] { id });

		if (result != null && result.size() > 0) {
			catrel = (CatRelationshipDao) result.get(0);

		}

		return catrel.getParentId();
	}

	public void createOrSaveCategoryRule(CategoryRuleDao catrule) {
		// create the object in the DB
		getHibernateTemplate().saveOrUpdate(catrule);
	}

	public List<CategoryRuleDao> getCategoryRules() {
		List rules = getHibernateTemplate().find(
				"from CategoryRuleDao as cat  order by cat.lineorder ");

		return rules;

	}

	public CategoryRuleDao getLastCategoryRule() {
		List rules = getHibernateTemplate().find(
				"from CategoryRuleDao as cat order by cat.lineorder desc");

		if (rules != null && rules.size() > 0) {
			return (CategoryRuleDao) rules.get(0);
		}

		return null;
	}

	public void deleteCategoryRule(Long ruleid) {
		CategoryRuleDao rule = (CategoryRuleDao) getHibernateTemplate().get(
				CategoryRuleDao.class, ruleid);
		getHibernateTemplate().delete(rule);
	}

	public List<CategoryRuleDao> getCategoryRulesByOrder(long oldorder) {
		List rules = getHibernateTemplate().find(
				"from CategoryRuleDao as cat  where cat.lineorder>?",
				new Object[] { new Long(oldorder) });

		return rules;
	}

	public CategoryRuleDao getCategoryRule(Long ruleid) {
		CategoryRuleDao rule = (CategoryRuleDao) getHibernateTemplate().get(
				CategoryRuleDao.class, ruleid);
		return rule;
	}

	public void createOrSaveTargetGroup(TargetGroupDao tg) {
		if (tg.getId() == null) {
			tg.setIsDefault(new Boolean(false));
		}
		getHibernateTemplate().saveOrUpdate(tg);
	}

	public void createOrUpdateTargetDetail(TargetDetailDao newdetail) {
		getHibernateTemplate().saveOrUpdate(newdetail);

	}

	public TargetGroupDao getDefaultTargetGroup(Long targettype) {
		List result = null;
		TargetGroupDao targetgroup = null;
		result = getHibernateTemplate()
				.find(
						"from TargetGroupDao as tgdao where tgdao.isDefault = ? and tgdao.targettype = ?",
						new Object[] { new Boolean(true), targettype });

		if (result != null && result.size() > 0) {
			targetgroup = (TargetGroupDao) result.get(0);

		}

		return targetgroup;
	}

	public List<TargetDetailDao> getTargetDetailForGroup(Long defaultid) {
		List<TargetDetailDao> result = getHibernateTemplate().find(
				"	from TargetDetailDao as det where det.groupid = ?",
				new Object[] { defaultid });

		return result;
	}

	public TargetGroupDao getTargetGroupByMonthTag(String tag) {
		TargetGroupDao targetgroup = null;
		List result = getHibernateTemplate()
				.find(
						"	from TargetGroupDao as tg where tg.targettype=? and  tg.monthtag = ?",
						new Object[] { CategoryManager.TargetType.Month, tag });

		if (result != null && result.size() > 0) {
			targetgroup = (TargetGroupDao) result.get(0);

		}

		return targetgroup;
	}

	public TargetGroupDao getTargetGroupByYearTag(String yeartag) {
		TargetGroupDao targetgroup = null;
		List result = getHibernateTemplate()
				.find(
						"	from TargetGroupDao as tg where tg.targettype=? and  tg.yeartag = ?",
						new Object[] { CategoryManager.TargetType.Year, yeartag });

		if (result != null && result.size() > 0) {
			targetgroup = (TargetGroupDao) result.get(0);

		}

		return targetgroup;
	}

	public TargetGroupDao getTargetGroup(Long id) {
		TargetGroupDao tgd = (TargetGroupDao) getHibernateTemplate().get(
				TargetGroupDao.class, id);
		return tgd;
	}

	public List<TargetGroupDao> getTargetGroupList(Long targettype) {
		List targetgroups = getHibernateTemplate().find(
				"from TargetGroupDao as cat where cat.targettype = ?",
				new Object[] { targettype });

		return targetgroups;
	}

	public void deleteTargetDetail(Long deleteid) {
		TargetDetailDao tgd = (TargetDetailDao) getHibernateTemplate().get(
				TargetDetailDao.class, deleteid);
		getHibernateTemplate().delete(tgd);
	}

	public void deleteTargetGroup(Long deleteid) {
		TargetGroupDao tgd = (TargetGroupDao) getHibernateTemplate().get(
				TargetGroupDao.class, deleteid);
		getHibernateTemplate().delete(tgd);
	}

}
