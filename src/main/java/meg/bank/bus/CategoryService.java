package meg.bank.bus;

import java.util.HashMap;
import java.util.List;

import meg.bank.bus.dao.CatRelationshipDao;
import meg.bank.bus.dao.CategoryDao;
import meg.bank.bus.dao.CategoryRuleDao;

public interface CategoryService {

	
	public abstract List<CategoryDao> getCategories(boolean showall);

	public abstract CategoryDao addCategory(String name, String description,
			Boolean nonexpcat);

	public abstract HashMap<Long, String> getCategoriesAsMap();

	public abstract CatRelationshipDao getCategoryRel(Long parentid,
			Long childid);

	public abstract List<CategoryLevel> getCategoriesUpToLevel(int level);

	public abstract List<CategoryDao> getDirectSubcategories(Long parentid);

	public abstract List<CategoryLevel> getAllSubcategories(CategoryDao cat);

	public abstract Long getParentIdForCat(Long id);

	public abstract CategoryLevel getAsCategoryLevel(Long id);

	public abstract void changeCatMembership(Long catId, Long origParent,
			Long parentId);

	public abstract boolean hasCircularReference(Long newParentId,
			CategoryDao category);

	public abstract void createCategoryRule(String contains, Long catid);

	public abstract void removeCategoryRule(CategoryRuleDao cat);

	public abstract void updateCategoryRule(Long ruleid, String newcontains,
			Long newcatid);

	public abstract void swapOrder(Long beforeid, Long afterid);

}