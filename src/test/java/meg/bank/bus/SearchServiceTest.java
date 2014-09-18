package meg.bank.bus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import meg.bank.bus.dao.CategoryDao;
import meg.bank.bus.dao.ExpenseDao;
import meg.bank.bus.dao.TargetDetailDao;
import meg.bank.bus.dao.TargetDetailDaoDataOnDemand;
import meg.bank.bus.dao.TargetGroupDao;
import meg.bank.bus.imp.ImportManager;
import meg.bank.bus.repo.TargetDetailRepository;
import meg.bank.bus.repo.TargetGroupRepository;
import meg.bank.web.model.TargetModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext*.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class SearchServiceTest {

	@Autowired
	SearchService searchService;

	@Autowired
	CategoryService categoryService;

	List<CategoryDao> cats;

	@Before
	public void setup() {
		// get all categories into list
		cats = categoryService.getCategories(true);
	}

	@Test
	public void testGetExpenses() {
		// just test that it doesn't blow up
		ExpenseCriteria criteria = new ExpenseCriteria();
		List<ExpenseDao> results = searchService.getExpenses(criteria);
		Assert.assertNotNull(results);

		// now test with criteria
		criteria.setDateStart(new Date());
		criteria.setCategorizedType(new Long(
				ExpenseCriteria.CategorizedType.ONLYCATS));
		criteria.setCategory(new Long(1));
		criteria.setExcludeNonExpense(true);
		criteria.setSource(new Long(ImportManager.ImportClient.All));
		criteria.setTransactionType(new Long(ExpenseCriteria.TransactionType.DEBITS));
		results = searchService.getExpenses(criteria);
		Assert.assertNotNull(results);

		// test with multiple categories
		List<CategoryLevel> catids = new ArrayList<CategoryLevel>();
		for (CategoryDao cat:cats) {
			catids.add(new CategoryLevel(cat,0));
		}
		criteria.setCategoryLevelList(catids);
		criteria.setExcludeNonExpense(false);
		criteria.setSource(new Long(1));
		criteria.setTransactionType(new Long(ExpenseCriteria.TransactionType.CREDITS));
		results = searchService.getExpenses(criteria);
		Assert.assertNotNull(results);

		
		
		catids=new ArrayList<CategoryLevel>();
		
	}

}