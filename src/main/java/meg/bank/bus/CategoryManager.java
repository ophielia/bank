package meg.bank.bus;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import meg.bank.bus.dao.CatRelationshipDao;
import meg.bank.bus.dao.CategoryDao;
import meg.bank.bus.dao.CategoryRuleDao;
import meg.bank.bus.dao.TargetDetailDao;
import meg.bank.bus.dao.TargetGroupDao;
import meg.bank.bus.db.CategoryManagerDao;

public class CategoryManager {

	private CategoryManagerDao cmd;

	public final static class TargetType {
		public final static Long Month = new Long(1);
		public final static Long Year = new Long(2);
	}

	public CategoryManagerDao getCategoryManagerDao() {
		return cmd;
	}

	public void setCategoryManagerDao(CategoryManagerDao catmanager) {
		this.cmd = catmanager;
	}

	/**
	 * Category Methods
	 * 
	 * @param showall
	 *            TODO
	 */

	public List<CategoryDao> getCategories(boolean showall) {
		// replace with same in categoryService
		
		return null;
	}

	public CategoryDao getCategory(Long id) {
		// return CategoryRepository.findOne(Long id)
		//return cmd.getCategory(id);
		return null;
	}

	public void createOrSaveCategory(CategoryDao category) {
		// return CategoryRepository.save(CategoryDao category)

	}


	public CategoryDao getCategoryByName(String name) {
		// // return CategoryRepository.findByName(CategoryDao category)
		return cmd.getCategoryByName(name);
	}

	public HashMap<Long, String> getCategoriesAsMap() {
		// return categoryService.getCategoriesAsMap
		return null;
	}

	/**
	 * Category Relationship Methods
	 * 
	 */

	public void createOrSaveCategoryRel(CatRelationshipDao rel) {
		// return catRelRep.save(CatRelationshipDao)
	}

	public CatRelationshipDao getCategoryRel(Long parentid, Long childid) {
		// return service.getCategoryRel(Long parentid, Long childid) 
		return cmd.getCategoryRel(parentid, childid);
	}

	public List<CategoryLevel> getCategoriesUpToLevel(int level) {
		// categoryService.getCategoriesUpToLevel(int level)
		return getSubcategoriesRecursive(new Long(0), level, 1, false);
	}

	private List<CategoryLevel> getSubcategoriesRecursive(Long parentid,
			int maxlvl, int currentlvl, boolean ignorelvl) {
		// done
		
		List<CategoryLevel> returncats = new ArrayList<CategoryLevel>();

		// get categories belonging to parentid
		List<CategoryLevel> subcategories = getDirectSubcategoryLvls(parentid,
				currentlvl);
		// if max level = current level then stop condition is met
		// -- return the subcategories for parentid
		if (!ignorelvl && (maxlvl == currentlvl)) {
			return subcategories;
		}
		// otherwise, cycle through all subcategories, retrieving
		// their subcategories.
		if (subcategories != null) {
			for (Iterator<CategoryLevel> iter = subcategories.iterator(); iter
					.hasNext();) {
				CategoryLevel catlvl = iter.next();
				returncats.add(catlvl);
				// -- increment level
				// -- Add all subcategories to a list
				returncats.addAll(getSubcategoriesRecursive(catlvl
						.getCategory().getId(), maxlvl, currentlvl + 1,
						ignorelvl));
			}
		}
		// return collection of subcategories
		return returncats;
	}

	private List<CategoryLevel> getDirectSubcategoryLvls(Long parentid,
			int level) {
		// moved
		List<CategoryDao> direct = getDirectSubcategories(parentid);
		List<CategoryLevel> sublevels = new ArrayList<CategoryLevel>();
		if (direct != null) {
			for (Iterator<CategoryDao> iter = direct.iterator(); iter.hasNext();) {
				CategoryDao cat = iter.next();
				CategoryLevel catlvl = new CategoryLevel(cat, level);
				sublevels.add(catlvl);
			}

		}
		return sublevels;
	}

	public List<CategoryDao> getDirectSubcategories(Long parentid) {
		// service of same name
		return cmd.getDirectSubcategories(parentid);
	}


	public List<CategoryLevel> getAllSubcategories(CategoryDao cat) {
		// service of same name
		return getSubcategoriesRecursive(cat.getId(), 1, 1, true);
	}

	

	public Long getParentIdForCat(Long id) {
		// service getParentIdForCat
		return cmd.getParentIdForCat(id);
	}

	public CategoryLevel getAsCategoryLevel(Long id) {
		// service of same name
		// get CategoryDao
		CategoryDao catd = getCategory(id);
		// get CategoryLevel number
		int catlvl = getCategoryLevel(catd);
		// create CategoryLevel Object and return
		CategoryLevel lvl = new CategoryLevel(catd,catlvl);
		return lvl;
	}
	
	public void changeCatMembership(Long catId, Long origParent, Long parentId) {
		// service of same name
		// get original category relationship
		CatRelationshipDao catrel = getCategoryRel(origParent, catId);

		// update category relationship to new
		catrel.setParentId(parentId);

		// persist changes
		createOrSaveCategoryRel(catrel);

	}

	public boolean hasCircularReference(Long newParentId, CategoryDao category) {
		// service of same name
/*
 * 		boolean hasCircular = false;
		List<CategoryLevel> allsubcategories = getAllSubcategories(category);
		if (allsubcategories != null) {
			for (CategoryLevel catlvl : allsubcategories) {
				CategoryDao categ = catlvl.getCategory();
				if (categ.getId().longValue() == newParentId.longValue()) {
					hasCircular = true;
				}
			}
		}
 */

		return false;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	

	/**
	 * Category Rule Methods
	 * 
	 */
	public List<CategoryRuleDao> getCategoryRules() {
		// repository, findAll
		CategoryManagerDao catmandao = getCategoryManagerDao();
		return catmandao.getCategoryRules();
	}

	public void createCategoryRule(String contains, Long catid) {
		// service of same name
	/*	CategoryManagerDao catmandao = getCategoryManagerDao();
		// get last order
		CategoryRuleDao lastcat = catmandao.getLastCategoryRule();
		long neworder = 1;
		if (lastcat != null) {
			neworder = lastcat.getLineorder().longValue() + 1;
		}

		CategoryRuleDao newrule = new CategoryRuleDao();
		newrule.setContaining(contains);
		newrule.setCategoryId(catid);
		newrule.setLineorder(new Long(neworder));

		catmandao.createOrSaveCategoryRule(newrule);*/
	}

	public void removeCategoryRule(CategoryRuleDao cat) {
		// service of same name
	/*	CategoryManagerDao catmandao = getCategoryManagerDao();
		// first, save the order of the rule to be removed
		long oldorder = cat.getLineorder().longValue();
		// remove the rule
		catmandao.deleteCategoryRule(cat.getId());
		// update the following rules, so there aren't any gaps in the order
		List<CategoryRuleDao> rules = catmandao
				.getCategoryRulesByOrder(oldorder);
		if (rules != null && rules.size() > 0) {
			for (CategoryRuleDao rule : rules) {
				rule
						.setLineorder(new Long(
								rule.getLineorder().longValue() - 1));
				catmandao.createOrSaveCategoryRule(rule);
			}
		}*/
	}

	public void updateCategoryRule(Long ruleid, String newcontains,
			Long newcatid) {
		// service of same name
		/*CategoryManagerDao catmandao = getCategoryManagerDao();
		// pull category rule
		CategoryRuleDao rule = catmandao.getCategoryRule(ruleid);

		// update category rule
		rule.setContaining(newcontains);
		rule.setCategoryId(newcatid);

		// persist change
		catmandao.createOrSaveCategoryRule(rule);*/
	}

	public void swapOrder(Long beforeid, Long afterid) {
		// service of same name
		/*CategoryManagerDao catmandao = getCategoryManagerDao();

		// pull rules
		CategoryRuleDao beforerule = catmandao.getCategoryRule(beforeid);
		CategoryRuleDao afterrule = catmandao.getCategoryRule(afterid);

		// update rules (swapping order)
		Long beforeorder = beforerule.getLineorder();
		beforerule.setLineorder(afterrule.getLineorder());
		afterrule.setLineorder(beforeorder);

		// persist change
		catmandao.createOrSaveCategoryRule(beforerule);
		catmandao.createOrSaveCategoryRule(afterrule);*/
	}

	
	
	
/*
 * 
all in TargetService....
	public List<TargetGroupDao> getTargetGroupList(Long targettype) {
		CategoryManagerDao catmandao = getCategoryManagerDao();
		return catmandao.getTargetGroupList(targettype);
	}

	public void createNewTargetGroup(Long targettype) {
		CategoryManagerDao catmandao = getCategoryManagerDao();

		TargetGroupDao tg = catmandao.getDefaultTargetGroup(targettype);
		Long defaultid = tg.getId();

		TargetGroupDao newtg = new TargetGroupDao();
		newtg.setTargettype(targettype);
		newtg.setDescription("generated " + new Date());
		newtg.setName("new TargetGroup ");
		catmandao.createOrSaveTargetGroup(newtg);

		// copy details
		List<TargetDetailDao> details = catmandao
				.getTargetDetailForGroup(defaultid);
		if (details != null) {
			for (TargetDetailDao detail : details) {
				TargetDetailDao newdetail = new TargetDetailDao();
				newdetail.setAmount(detail.getAmount());
				newdetail.setCatid(detail.getCatid());
				newdetail.setGroupid(newtg.getId());

				catmandao.createOrUpdateTargetDetail(newdetail);
			}
		}
	}

	public void deleteTargetGroup(Long editid) {
		CategoryManagerDao catmandao = getCategoryManagerDao();
		// delete details
		List<TargetDetailDao> details = catmandao
				.getTargetDetailForGroup(editid);
		if (details != null) {
			for (TargetDetailDao detail : details) {
				catmandao.deleteTargetDetail(detail.getId());
			}
		}
		// delete Target Group
		catmandao.deleteTargetGroup(editid);
	}

	public TargetGroupDao getTargetGroup(Long editid) {
		CategoryManagerDao catmandao = getCategoryManagerDao();
		return catmandao.getTargetGroup(editid);
	}

	public void updateDefaultTargetGroup(Long editid, Long targettype) {
		CategoryManagerDao catmandao = getCategoryManagerDao();

		// get previous default
		TargetGroupDao defaulttg = catmandao.getDefaultTargetGroup(targettype);

		// update previous default
		defaulttg.setIsDefault(new Boolean(false));
		catmandao.createOrSaveTargetGroup(defaulttg);

		// get new default
		TargetGroupDao newdefault = catmandao.getTargetGroup(editid);

		// update new default
		newdefault.setIsDefault(new Boolean(true));
		catmandao.createOrSaveTargetGroup(newdefault);
	}

	public Target loadTarget(Long loadid) {
		CategoryManagerDao catmandao = getCategoryManagerDao();
		Target loading = new Target();
		// get TargetGroup
		TargetGroupDao tg = catmandao.getTargetGroup(loadid);

		// get Target Details
		List<TargetDetailDao> details = catmandao
				.getTargetDetailForGroup(loadid);

		// load in target
		loading.setTargetgroup(tg);
		loading.setTargetDetails(details);
		return loading;
	}

	public Target loadTargetForMonth(String month) {
		CategoryManagerDao catmandao = getCategoryManagerDao();
		// look for target by month
		TargetGroupDao tg = catmandao.getTargetGroupByMonthTag(month);
		if (tg != null) {
			return loadTarget(tg.getId());
		}

		// if no target is available for the month, load the default
		tg = catmandao.getDefaultTargetGroup(CategoryManager.TargetType.Month);
		return loadTarget(tg.getId());

	}
	
	public Target loadTargetForYear(String year) {
		CategoryManagerDao catmandao = getCategoryManagerDao();
		// look for target by month
		TargetGroupDao tg = catmandao.getTargetGroupByYearTag(year);
		if (tg != null) {
			return loadTarget(tg.getId());
		}

		// if no target is available for the year, load the default
		tg = catmandao.getDefaultTargetGroup(CategoryManager.TargetType.Year);
		return loadTarget(tg.getId());

	}	

	public void deleteTargetDetails(List<Long> deleted) {
		CategoryManagerDao catmandao = getCategoryManagerDao();
		for (Long deleteid : deleted) {
			catmandao.deleteTargetDetail(deleteid);
		}
	}

	public void saveTarget(Target target) {
		CategoryManagerDao catmandao = getCategoryManagerDao();
		// save target group
		TargetGroupDao tg = target.getTargetgroup();

		// for month types
		if (tg.getTargettype().longValue() == CategoryManager.TargetType.Month
				.longValue()) {
			// check if any other groups have the same month tag
			TargetGroupDao previoustag = catmandao.getTargetGroupByMonthTag(tg
					.getMonthtag());
			// if so, remove month tag from other group
			if (previoustag != null && previoustag.getId().longValue() > 0) {
				previoustag.setMonthtag(null);
				catmandao.createOrSaveTargetGroup(previoustag);
			}
		} else {  
			// for year types
			// check if any other groups have the same year tag
			TargetGroupDao previoustag = catmandao.getTargetGroupByYearTag(tg
					.getYeartag());
			// if so, remove year tag from other group
			if (previoustag != null && previoustag.getId().longValue() > 0) {
				previoustag.setYeartag(null);
				catmandao.createOrSaveTargetGroup(previoustag);
			}
		}
		catmandao.createOrSaveTargetGroup(tg);

		// save target detail
		// place all details in hash with catid as key
		List<TargetDetailDao> details = target.getTargetDetails();
		Hashtable<Long, TargetDetailDao> alldetails = new Hashtable<Long, TargetDetailDao>();
		for (TargetDetailDao det : details) {
			if (alldetails.containsKey(det.getCatid())) {
				// the category has been entered twice. consolidate entries...
				TargetDetailDao existing = (TargetDetailDao) alldetails.get(det
						.getCatid());
				double combined = existing.getAmount().doubleValue()
						+ det.getAmount().doubleValue();
				existing.setAmount(new Double(combined));
				// delete duplicate, if it exists in db
				if (det.getId().longValue() > 0) {
					catmandao.deleteTargetDetail(det.getId());
				}
			} else {
				alldetails.put(det.getCatid(), det);
			}

		}
		// now, cycle through all details, persisting changes
		Set<Long> keys = alldetails.keySet();
		for (Long key : keys) {
			TargetDetailDao det = (TargetDetailDao) alldetails.get(key);
			det.setGroupid(tg.getId());
			catmandao.createOrUpdateTargetDetail(det);

		}
	}
	
	*/
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	////////////  START HERE
	public List<CategoryLevel> getSubcategoriesToLevel(Long catid,
			int breakoutLevel) {
		CategoryManagerDao catmandao = getCategoryManagerDao();
		// get level of category
		CategoryDao cat = catmandao.getCategory(catid);
		int currentlvl = getCategoryLevel(cat);
		// if category is deeper than breakout level, return null
		if (currentlvl >= breakoutLevel) {
			return null;
		}
		// get subcategories
		return getSubcategoriesRecursive(cat.getId(), breakoutLevel,
				currentlvl + 1, false);
	}

	private int getCategoryLevel(CategoryDao cat) {
		// in service now
		CategoryManagerDao catmandao = getCategoryManagerDao();
		Long catid = cat.getId();
		int level = 1;

		// retrieve parent ids until toplevel category is found
		Long parentid = catmandao.getParentIdForCat(catid);
		while (parentid.longValue() > 0) {
			level++;
			parentid = catmandao.getParentIdForCat(parentid);
		}

		return level;
	}


	/**
	 * Toggles the Category Display to the opposite of it's current value.
	 * 
	 * @param categoryid
	 */
	public void toggleCategoryDisplay(Long categoryid) {
		CategoryManagerDao catmandao = getCategoryManagerDao();

		// get category
		CategoryDao cat = catmandao.getCategory(categoryid);

		// set category display in list to opposite of current value
		Boolean currentdisp = cat.getDisplayinlist();
		cat.setDisplayinlist(new Boolean(!currentdisp.booleanValue()));

		// save category
		catmandao.createOrSaveCategory(cat);

	}

	/**
	 * Toggles the ExpenseType to the opposite of it's current value.
	 * 
	 * @param categoryid
	 */
	public void toggleExpenseType(Long categoryid) {
		CategoryManagerDao catmandao = getCategoryManagerDao();

		// get category
		CategoryDao cat = catmandao.getCategory(categoryid);

		// set category display in list to opposite of current value
		Boolean currentexp = cat.getNonexpense();
		cat.setNonexpense(new Boolean(!currentexp.booleanValue()));

		// save category
		catmandao.createOrSaveCategory(cat);
	}

}
