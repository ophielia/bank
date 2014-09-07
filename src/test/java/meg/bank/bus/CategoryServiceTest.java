package meg.bank.bus;


import java.util.List;

import meg.bank.bus.dao.CatRelationshipDao;
import meg.bank.bus.dao.CategoryDao;
import meg.bank.bus.dao.CategoryDaoDataOnDemand;
import meg.bank.bus.dao.CategoryRuleDao;
import meg.bank.bus.dao.CategoryRuleDaoDataOnDemand;
import meg.bank.bus.repo.CatRelationshipRepository;
import meg.bank.bus.repo.CategoryRuleRepository;

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

@Autowired
CategoryRuleRepository catRuleRep;	

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

    @Test
    public void testCreateOrUpdCategoryRule() throws Exception {
    	// Add category 'tCreateRule'
    	CategoryDaoDataOnDemand cdod = new CategoryDaoDataOnDemand();
    	CategoryDao cat = cdod.getNewTransientCategoryDao(99);
    	String catname = "tCreateRule";
    	cat.setName(catname);
    	cat = catService.addCategory(catname, catname, false, true);

    	// make stub CategoryRule
    	CategoryRuleDaoDataOnDemand rdod = new CategoryRuleDaoDataOnDemand();
    	CategoryRuleDao rule = rdod.getNewTransientCategoryRuleDao(99);
    	// insert text 'tCreateRule'
    	rule.setContaining("tCreateRule");
    	rule.setCategoryId(cat.getId());
    	// call createOrUpdCategoryRule
    	rule = catService.createOrUpdCategoryRule(rule);
    	// test that CategoryRule is found
    	List<CategoryRuleDao> rules = catRuleRep.findCategoryRulesByContaining("tCreateRule");
    	Assert.assertNotNull(rules);
    	Assert.assertTrue(rules.size()>0);
    	CategoryRuleDao test = rules.get(0);
    	Assert.assertEquals(test.getId(),rule.getId());
    	
    	// change rule
    	Long origid = rule.getId();
    	rule.setContaining("tChange");
    	// call createOrUpdCategoryRule
    	rule = catService.createOrUpdCategoryRule(rule);
    	rules = catRuleRep.findCategoryRulesByContaining("tChange");
    	Assert.assertNotNull(rules);
    	Assert.assertTrue(rules.size()>0);
    	test = rules.get(0);
    	Assert.assertEquals(test.getId(),rule.getId());
    	Assert.assertEquals("tChange", test.getContaining());
    }
    
    
    @Test
    public void testMoveUp() throws Exception {
    	//add category 'tForFirstRule'
    	CategoryDaoDataOnDemand cdod = new CategoryDaoDataOnDemand();
    	CategoryDao firstcat = cdod.getNewTransientCategoryDao(99);
    	String catname = "tForFirstRule";
    	firstcat.setName(catname);
    	firstcat = catService.addCategory(catname, catname, false, true);
    	//add category 'tForSecondRule'
    	CategoryDao secondcat = cdod.getNewTransientCategoryDao(999);
    	catname = "tForFirstRule";
    	secondcat.setName(catname);
    	secondcat = catService.addCategory(catname, catname, false, true);

    	// create rule - first category with containing 'forfirstrule'
    	CategoryRuleDao firstrule = new CategoryRuleDao();
    	firstrule.setContaining("forfirstrule");
    	firstrule.setCategoryId(firstcat.getId());
    	// create rule - second category with containing 'forsecondrule'
    	CategoryRuleDao secondrule = new CategoryRuleDao();
    	secondrule.setContaining("forsecondrule");
    	secondrule.setCategoryId(secondcat.getId());
    	// add first rule
    	firstrule = catService.createOrUpdCategoryRule(firstrule);
    	
    	// add second rule
    	secondrule = catService.createOrUpdCategoryRule(secondrule);

    	// save line number of first rule
    	Long origlinenumber = firstrule.getLineorder();

    	// move up second rule
    	catService.moveRuleUp(secondrule.getId());

    	// retrieve first and second rule
    	List<CategoryRuleDao> rules = catRuleRep.findCategoryRulesByContaining("forfirstrule");
    	CategoryRuleDao testfirst = rules.get(0);
    	rules = catRuleRep.findCategoryRulesByContaining("forsecondrule");
    	CategoryRuleDao testsecond = rules.get(0);

    	// verify that first rule has linenumber minus one
    	Assert.assertEquals(origlinenumber+1,testfirst.getLineorder().longValue());
    	// verify that second rule has linenumber itself
    	Assert.assertEquals(origlinenumber.longValue(),testsecond.getLineorder().longValue());
    }
    
    @Test
    public void testRemoveRule() {
    	String catname="tDelete";
    	CategoryDao cat = catService.addCategory(catname, catname, false, true);

    	// make stub CategoryRule
    	CategoryRuleDaoDataOnDemand rdod = new CategoryRuleDaoDataOnDemand();
    	CategoryRuleDao rule = rdod.getNewTransientCategoryRuleDao(99);
    	// insert text 'tCreateRule'
    	rule.setContaining("tDelete");
    	rule.setCategoryId(cat.getId());
    	// call createOrUpdCategoryRule
    	rule = catService.createOrUpdCategoryRule(rule);
    	// test that CategoryRule is found
    	List<CategoryRuleDao> rules = catRuleRep.findCategoryRulesByContaining("tDelete");
    	Assert.assertNotNull(rules);
    	Assert.assertTrue(rules.size()>0);
    	CategoryRuleDao test = rules.get(0);
    	Assert.assertEquals(test.getId(),rule.getId());
    	
    	// now, call the delete
    	catService.removeCategoryRule(rule.getId());
    	
    	// test that the rule is NOT found
    	rules = catRuleRep.findCategoryRulesByContaining("tDelete");
    	Assert.assertTrue(rules==null || rules.size()==0);
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
    
    updateCategoryRule(Long, String, Long)
    
    updateCategory(CategoryDao)
    
    */
}