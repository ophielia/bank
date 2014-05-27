package meg.bank.bus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import meg.bank.bus.dao.CatRelationshipDao;
import meg.bank.bus.dao.CategoryDao;
import meg.bank.bus.repo.CategoryRepository;
import meg.bank.bus.repo.CatRelationshipRepository;

@Service
public class CategoryService {

	@Autowired
	CategoryRepository catRepository;
	
	@Autowired
	CatRelationshipRepository catRelationRep;
	
	public List<CategoryDao> getCategories(boolean showall) {
		if (showall) {
			return catRepository.findAll();
		} else {
			return catRepository.findByDisplayinlistTrue();
		}
	}
	
	public void addCategory(String name, String description, Boolean nonexpcat) {
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
	}
	
	public HashMap<Long, String> getCategoriesAsMap() {
		HashMap<Long, String> hash = new HashMap<Long, String>();
		List<CategoryDao> categories = getCategories(false);
		for (Iterator<CategoryDao> iter = categories.iterator(); iter.hasNext();) {
			CategoryDao cat = iter.next();
			hash.put(cat.getId(), cat.getName());
		}
		return hash;
	}
	
	public CatRelationshipDao getCategoryRel(Long parentid, Long childid) {
		return catRelationRep.findByParentAndChild(parentid, childid);
	}
	
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
	
	public List<CategoryDao> getDirectSubcategories(Long parentid) {
		return catRepository.findDirectSubcategories(parentid);
	}
	
	public List<CategoryLevel> getAllSubcategories(CategoryDao cat) {
		return getSubcategoriesRecursive(cat.getId(), 1, 1, true);
	}
	
}
