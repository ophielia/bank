package meg.bank.bus;


import java.util.List;

import meg.bank.bus.dao.CatRelationshipDao;
import meg.bank.bus.dao.CategoryDao;
import meg.bank.bus.dao.CategoryDaoDataOnDemand;
import meg.bank.bus.repo.CatRelationshipRepository;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext*.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class CategoryServiceTest {

	
@Autowired
CategoryService catService;

@Autowired
CatRelationshipRepository catRelRepo;

    @Test
    public void testAddCategory() throws Exception {
    	// make Category
    	CategoryDaoDataOnDemand cdod = new CategoryDaoDataOnDemand();
    	CategoryDao cat = cdod.getNewTransientCategoryDao(99);
    	String catname = "tCategory";
    	cat.setName(catname);
    	
    	// service call  -add category
    	catService.addCategory(catname, catname, false, true);
    	
    	// get all categories
    	List<CategoryDao> categories = catService.getCategories(true);
    	
    	// verify not null
    	Assert.assertNotNull(categories);
    	
    	// verify category is found
    	CategoryDao testcat = null;
    	for (CategoryDao comp:categories) {
    		if (comp.getName().equals(catname)) {
    			testcat = comp;
    			break;
    		} 
    	}
    	Assert.assertNotNull(testcat);
    	
    	// verify CategoryRelationship was created
    	CatRelationshipDao rel = catRelRepo.findByChild(testcat.getId());
    	Assert.assertNotNull(rel);
    }

    
    
    
    /*
    getCategories(boolean)
    addCategory(String, String, Boolean, Boolean)
    getCategoriesAsMap()
    getCategoriesAsMap(boolean)
    getCategoryRel(Long, Long)
    getCategoriesUpToLevel(int)
    getDirectSubcategories(Long)
    getAllSubcategories(CategoryDao)
    getParentIdForCat(Long)
    getAsCategoryLevel(Long)
    changeCatMembership(Long, Long)
    hasCircularReference(Long, CategoryDao)
    createOrUpdCategoryRule(CategoryRuleDao)
    removeCategoryRule(CategoryRuleDao)
    updateCategoryRule(Long, String, Long)
    swapOrder(Long, Long)
    updateCategory(CategoryDao)
    
    */
}