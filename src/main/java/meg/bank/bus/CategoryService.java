package meg.bank.bus;

import java.util.HashMap;
import java.util.List;

import meg.bank.bus.dao.CatRelationshipDao;
import meg.bank.bus.dao.CategoryDao;
import meg.bank.bus.dao.CategoryRuleDao;
import meg.bank.web.model.CategoryModel;

public interface CategoryService {

	
	public abstract List<CategoryDao> getCategories(boolean showall);

	public abstract CategoryDao addCategory(String name, String description,
			Boolean nonexpcat, 	Boolean display);

	public abstract HashMap<Long, CategoryDao> getCategoriesAsMap();

	public abstract HashMap<Long, CategoryDao> getCategoriesAsMap(boolean exclNonDisp);
	
	public abstract CatRelationshipDao getCategoryRel(Long parentid,
			Long childid);
	
	public abstract List<CategoryLevel> getCategoriesUpToLevel(int level);

	public abstract List<CategoryDao> getDirectSubcategories(Long parentid);

	public abstract List<CategoryLevel> getAllSubcategories(CategoryDao cat);

	public abstract Long getParentIdForCat(Long id);

	public abstract CategoryLevel getAsCategoryLevel(Long id);

	public abstract CatRelationshipDao changeCatMembership(Long catId, 
			Long parentId);

	public abstract boolean hasCircularReference(Long newParentId,
			CategoryDao category);

	public abstract CategoryRuleDao createOrUpdCategoryRule(CategoryRuleDao catRule);

	public abstract void removeCategoryRule(Long categoryruleid);

	public abstract void updateCategoryRule(Long ruleid, String newcontains,
			Long newcatid);

	public abstract void moveRuleUp(Long moveupid);

	public abstract CategoryDao updateCategory(CategoryDao category);

	public abstract CategoryModel loadCategoryModel(Long id);

}