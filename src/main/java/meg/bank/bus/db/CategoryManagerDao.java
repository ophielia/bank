package meg.bank.bus.db;

import java.util.List;

import meg.bank.bus.dao.CatRelationshipDao;
import meg.bank.bus.dao.CategoryDao;
import meg.bank.bus.dao.CategoryRuleDao;
import meg.bank.bus.dao.TargetDetailDao;
import meg.bank.bus.dao.TargetGroupDao;

public interface CategoryManagerDao {
	public void createOrSaveCategory(CategoryDao cat);
	
	public void updateCategory(CategoryDao cat);
	
	public CategoryDao getCategoryByName(String name);

	public List<CategoryDao> getCategories(boolean showall);

	public void createOrSaveCategoryRel(CatRelationshipDao rel);

	public CatRelationshipDao getCategoryRel(Long parentid, Long childid);

	public List<CategoryDao> getDirectSubcategories(Long parentid);

	public CategoryDao getCategory(Long id);

	public Long getParentIdForCat(Long id);

	public List<CategoryRuleDao> getCategoryRules();

	public void createOrSaveCategoryRule(CategoryRuleDao catrule);

	public CategoryRuleDao getLastCategoryRule();

	public void deleteCategoryRule(Long ruleid);

	public List<CategoryRuleDao> getCategoryRulesByOrder(long oldorder);

	public CategoryRuleDao getCategoryRule(Long ruleid);

	public List<TargetGroupDao> getTargetGroupList(Long targettype);

	public TargetGroupDao getDefaultTargetGroup(Long targettype);

	public void createOrSaveTargetGroup(TargetGroupDao tg);

	public List<TargetDetailDao> getTargetDetailForGroup(Long defaultid);

	public void createOrUpdateTargetDetail(TargetDetailDao newdetail);

	public void deleteTargetDetail(Long deleteid);

	public void deleteTargetGroup(Long editid);

	public TargetGroupDao getTargetGroup(Long editid);
	public TargetGroupDao getTargetGroupByMonthTag(String tag);
	public TargetGroupDao getTargetGroupByYearTag(String yeartag);
}