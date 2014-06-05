package meg.bank.bus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import meg.bank.bus.dao.CatRelationshipDao;
import meg.bank.bus.dao.CategoryDao;
import meg.bank.bus.dao.CategoryRuleDao;
import meg.bank.bus.repo.CategoryRepository;
import meg.bank.bus.repo.CatRelationshipRepository;
import meg.bank.bus.repo.CategoryRuleRepository;

@Service
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	CategoryRepository catRepository;
	
	@Autowired
	CatRelationshipRepository catRelationRep;
	
	@Autowired
	CategoryRuleRepository catRuleRep;	
	
	/* (non-Javadoc)
	 * @see meg.bank.bus.CategoryService#getCategories(boolean)
	 */
	@Override
	public List<CategoryDao> getCategories(boolean showall) {
		if (showall) {
			return catRepository.findAll();
		} else {
			return catRepository.findByDisplayinlistTrue();
		}
	}
	
	/* (non-Javadoc)
	 * @see meg.bank.bus.CategoryService#addCategory(java.lang.String, java.lang.String, java.lang.Boolean)
	 */
	@Override
	public CategoryDao addCategory(String name, String description, Boolean nonexpcat) {
		// default description to name if empty
		if (description == null)
			description = name;

		CategoryDao cat = new CategoryDao();
		cat.setName(name);
		cat.setDescription(description);
		cat.setNonexpense(nonexpcat);
		catRepository.save(cat);

		// add category relationship
		CatRelationshipDao catrel = new CatRelationshipDao();
		catrel.setParentId(new Long(0));
		catrel.setChildId(cat.getId());
		catRelationRep.save(catrel);
		
		return cat;
	}
	
	/* (non-Javadoc)
	 * @see meg.bank.bus.CategoryService#getCategoriesAsMap()
	 */
	@Override
	public HashMap<Long, String> getCategoriesAsMap() {
		HashMap<Long, String> hash = new HashMap<Long, String>();
		List<CategoryDao> categories = getCategories(false);
		for (Iterator<CategoryDao> iter = categories.iterator(); iter.hasNext();) {
			CategoryDao cat = iter.next();
			hash.put(cat.getId(), cat.getName());
		}
		return hash;
	}
	
	/* (non-Javadoc)
	 * @see meg.bank.bus.CategoryService#getCategoryRel(java.lang.Long, java.lang.Long)
	 */
	@Override
	public CatRelationshipDao getCategoryRel(Long parentid, Long childid) {
		return catRelationRep.findByParentAndChild(parentid, childid);
	}
	
	/* (non-Javadoc)
	 * @see meg.bank.bus.CategoryService#getCategoriesUpToLevel(int)
	 */
	@Override
	public List<CategoryLevel> getCategoriesUpToLevel(int level) {
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
	
	/* (non-Javadoc)
	 * @see meg.bank.bus.CategoryService#getDirectSubcategories(java.lang.Long)
	 */
	@Override
	public List<CategoryDao> getDirectSubcategories(Long parentid) {
		return catRepository.findDirectSubcategories(parentid);
	}
	
	/* (non-Javadoc)
	 * @see meg.bank.bus.CategoryService#getAllSubcategories(meg.bank.bus.dao.CategoryDao)
	 */
	@Override
	public List<CategoryLevel> getAllSubcategories(CategoryDao cat) {
		return getSubcategoriesRecursive(cat.getId(), 1, 1, true);
	}
	
	/* (non-Javadoc)
	 * @see meg.bank.bus.CategoryService#getParentIdForCat(java.lang.Long)
	 */
	@Override
	public Long getParentIdForCat(Long id) {
		CatRelationshipDao result = catRelationRep.findByChild(id);
		
		if (result != null) {
			return result.getParentId();

		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see meg.bank.bus.CategoryService#getAsCategoryLevel(java.lang.Long)
	 */
	@Override
	public CategoryLevel getAsCategoryLevel(Long id) {
		// service of same name
		// get CategoryDao
		CategoryDao catd = catRepository.findOne(id);
		// get CategoryLevel number
		int catlvl = getCategoryLevel(catd);
		// create CategoryLevel Object and return
		CategoryLevel lvl = new CategoryLevel(catd,catlvl);
		return lvl;
	}

	
	private int getCategoryLevel(CategoryDao cat) {
		Long catid = cat.getId();
		int level = 1;

		// retrieve parent ids until toplevel category is found
		Long parentid = getParentIdForCat(catid);
		while (parentid.longValue() > 0) {
			level++;
			parentid = getParentIdForCat(parentid);
		}

		return level;
	}
	
	/* (non-Javadoc)
	 * @see meg.bank.bus.CategoryService#changeCatMembership(java.lang.Long, java.lang.Long, java.lang.Long)
	 */
	@Override
	public void changeCatMembership(Long catId, Long origParent, Long parentId) {
		// get original category relationship
		CatRelationshipDao catrel = getCategoryRel(origParent, catId);

		// update category relationship to new
		catrel.setParentId(parentId);

		// persist changes
		catRelationRep.save(catrel);

	}
	
	/* (non-Javadoc)
	 * @see meg.bank.bus.CategoryService#hasCircularReference(java.lang.Long, meg.bank.bus.dao.CategoryDao)
	 */
	@Override
	public boolean hasCircularReference(Long newParentId, CategoryDao category) {
		// service of same name
		boolean hasCircular = false;
		List<CategoryLevel> allsubcategories = getAllSubcategories(category);
		if (allsubcategories != null) {
			for (CategoryLevel catlvl : allsubcategories) {
				CategoryDao categ = catlvl.getCategory();
				if (categ.getId().longValue() == newParentId.longValue()) {
					hasCircular = true;
				}
			}
		}

		return hasCircular;
	}
	
	
	
	/* (non-Javadoc)
	 * @see meg.bank.bus.CategoryService#createCategoryRule(java.lang.String, java.lang.Long)
	 */
	@Override
	public void createCategoryRule(String contains, Long catid) {
		long neworder = 1;
		// get last order
		List<CategoryRuleDao> allcats = catRuleRep.findAll(new Sort(Sort.Direction.DESC, "lineorder"));
		if (allcats!=null) {
			CategoryRuleDao lastcat = allcats.get(0);
			if (lastcat != null) {
				neworder = lastcat.getLineorder().longValue() + 1;
			}
		}

		CategoryRuleDao newrule = new CategoryRuleDao();
		newrule.setContaining(contains);
		newrule.setCategoryId(catid);
		newrule.setLineorder(new Long(neworder));

		catRuleRep.save(newrule);
	}

	/* (non-Javadoc)
	 * @see meg.bank.bus.CategoryService#removeCategoryRule(meg.bank.bus.dao.CategoryRuleDao)
	 */
	@Override
	public void removeCategoryRule(CategoryRuleDao cat) {
		// first, save the order of the rule to be removed
		long oldorder = cat.getLineorder().longValue();
		// remove the rule
		catRuleRep.delete(cat.getId());
		// update the following rules, so there aren't any gaps in the order
		List<CategoryRuleDao> rules = catRuleRep.findCategoryRulesByOrder(oldorder);
		if (rules != null && rules.size() > 0) {
			for (CategoryRuleDao rule : rules) {
				rule
						.setLineorder(new Long(
								rule.getLineorder().longValue() - 1));
				catRuleRep.save(rule);
			}
		}
	}

	/* (non-Javadoc)
	 * @see meg.bank.bus.CategoryService#updateCategoryRule(java.lang.Long, java.lang.String, java.lang.Long)
	 */
	@Override
	public void updateCategoryRule(Long ruleid, String newcontains,
			Long newcatid) {
		// pull category rule
		CategoryRuleDao rule = catRuleRep.findOne(ruleid);

		// update category rule
		rule.setContaining(newcontains);
		rule.setCategoryId(newcatid);

		// persist change
		catRuleRep.save(rule);
	}

	/* (non-Javadoc)
	 * @see meg.bank.bus.CategoryService#swapOrder(java.lang.Long, java.lang.Long)
	 */
	@Override
	public void swapOrder(Long beforeid, Long afterid) {
		// pull rules
		CategoryRuleDao beforerule = catRuleRep.findOne(beforeid);
		CategoryRuleDao afterrule = catRuleRep.findOne(afterid);

		// update rules (swapping order)
		Long beforeorder = beforerule.getLineorder();
		beforerule.setLineorder(afterrule.getLineorder());
		afterrule.setLineorder(beforeorder);

		// persist change
		catRuleRep.save(beforerule);
		catRuleRep.save(afterrule);
	}
	
}
