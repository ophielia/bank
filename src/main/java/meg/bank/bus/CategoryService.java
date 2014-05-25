package meg.bank.bus;

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
	

	
	
}
