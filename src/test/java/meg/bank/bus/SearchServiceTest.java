package meg.bank.bus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import meg.bank.bus.dao.BankTADao;
import meg.bank.bus.dao.BankTADaoDataOnDemand;
import meg.bank.bus.dao.CategoryDao;
import meg.bank.bus.dao.ExpenseDao;
import meg.bank.bus.imp.ImportManager;
import meg.bank.bus.report.elements.CategorySummaryDisp;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@ContextConfiguration(locations = { "classpath*:/spring/application-config*.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@Configurable
public class SearchServiceTest {

	@Autowired
	SearchService searchService;

	@Autowired
	CategoryService categoryService;
	
	@Autowired
	BankTransactionService transService;	

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
	
	@Test
	public void testGetExpenseByIds() {
		// create three new transactions (BankTADao)
		BankTADaoDataOnDemand bdod = new BankTADaoDataOnDemand();
		BankTADao t1 = bdod.getNewTransientBankTADao(1);
		BankTADao t2 = bdod.getNewTransientBankTADao(2);
		BankTADao t3 = bdod.getNewTransientBankTADao(3);
		t1=transService.addTransaction(t1);
		t2=transService.addTransaction(t2);
		t3=transService.addTransaction(t3);
		// add ids for transactions into list
		List<String> ids = new ArrayList<String>();
		ids.add("140");
		ids.add("141");
		ids.add("142");
		// get list through service call
		List<ExpenseDao> results = searchService.getExpenseListByIds(ids);
		// Assert that the list isn't null
		Assert.assertNotNull(results);

	}
	
	@Test
	public void testGetExpenseTotalByMonth() {
		// just test that it doesn't blow up
		ExpenseCriteria criteria = new ExpenseCriteria();
		List<CategorySummaryDisp> results = searchService.getExpenseTotalByMonthAndCategory(criteria);
		Assert.assertNotNull(results);
	}

}