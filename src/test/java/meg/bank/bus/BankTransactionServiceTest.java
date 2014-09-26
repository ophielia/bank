package meg.bank.bus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import meg.bank.bus.dao.BankTADao;
import meg.bank.bus.dao.BankTADaoDataOnDemand;
import meg.bank.bus.dao.CategoryDao;
import meg.bank.bus.dao.CategoryDaoDataOnDemand;
import meg.bank.bus.dao.CategoryTADao;
import meg.bank.bus.dao.ExpenseDao;
import meg.bank.bus.repo.BankTARepository;
import meg.bank.bus.repo.CategoryRuleRepository;
import meg.bank.bus.repo.CategoryTARepository;
import meg.bank.web.model.ExpenseEditModel;

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
public class BankTransactionServiceTest {

	@Autowired
	BankTransactionService transService;

	@Autowired
	CategoryService catService;

	@Autowired
	SearchService searchService;

	@Autowired
	BankTARepository bankRepo;

	@Autowired
	CategoryTARepository catExpRepo;

	CategoryDao tCat;
	BankTADao withcategorized;
	BankTADao withoutcategorized;

	List<CategoryDao> randomcats;
	
	@Before
	public void setup() {
		tCat = catService.addCategory("tCat", "", false, true);

		// trans with category
		// make BankTrans
		BankTADaoDataOnDemand bDod = new BankTADaoDataOnDemand();
		withcategorized = bDod.getNewTransientBankTADao(12);


		// make CategoryDao
		CategoryTADao cat = new CategoryTADao();
		cat.setCatid(tCat.getId());
		cat.setAmount(100D);
		cat.setCreatedon(new Date());
		//withcategorized.setCategorizedExp(new ArrayList<CategoryTADao>());
		//withcategorized.getCategorizedExp().add(cat);
		//MM fix test
		bankRepo.saveAndFlush(withcategorized);
		cat.setBanktrans(withcategorized);
		cat = catExpRepo.saveAndFlush(cat);

		// trans without category
		bDod = new BankTADaoDataOnDemand();
		withoutcategorized = bDod.getNewTransientBankTADao(12);
		bankRepo.saveAndFlush(withoutcategorized);
		
		randomcats=catService.getCategories(true);
		
	}

	@Test
	public void testAssignCategoriesToExpense() {
		// get category for test
		List<CategoryDao> allcats = catService.getCategories(true);
		CategoryDao toassign = allcats.get(0);

		// get all transactions
		List<ExpenseDao> allexpenses = searchService.getAllExpenses();

		// make list of three transactions to update - first, with no categories
		List<String> updateids = new ArrayList<String>();
		for (ExpenseDao exp : allexpenses) {
			if (!exp.getHascat().booleanValue()) {
				updateids.add(exp.getId());
				if (updateids.size() > 3) {
					break;
				}
			}
		}

		// call service
		transService.assignCategoriesToExpenses(toassign.getId(), updateids);

		// get transactions
		List<ExpenseDao> testresults = searchService
				.getExpenseListByIds(updateids);

		// test that each has a category now
		Assert.assertNotNull(testresults);
		for (ExpenseDao exp : testresults) {
			Assert.assertTrue(exp.getHascat().booleanValue());
		}

	}

	@Test
	public void testLoadExpenseEditModel() {
		// test loading bank trans with category
		Long testid = withcategorized.getId();
		// service call
		ExpenseEditModel testmodel = transService.loadExpenseEditModel(testid);
		// ExpenseEditModel not null
		Assert.assertNotNull(testmodel);
		// get EEM categories
		List<CategoryTADao> transcats = testmodel.getCategoryExpenses();
		// Not null, size of 1
		Assert.assertNotNull(transcats);
		Assert.assertEquals(1, transcats.size());
		Assert.assertEquals(1, testmodel.getEntryamounts().size());

		// test loading without category
		testid = withoutcategorized.getId();
		// service call
		testmodel = transService.loadExpenseEditModel(testid);
		// ExpenseEditModel not null
		Assert.assertNotNull(testmodel);
		// get EEM categories
		transcats = testmodel.getCategoryExpenses();
		// Not null, size of 1
		Assert.assertNotNull(transcats);
		Assert.assertEquals(0, transcats.size());
		Assert.assertEquals(0, testmodel.getEntryamounts().size());
	}

	@Test
	public void testSaveExpenseEditModel() {
		// get trans with category as model
		Long id = withcategorized.getId();
		ExpenseEditModel model = transService.loadExpenseEditModel(id);
		// add three more categories
		List<CategoryTADao> exps = model.getCategoryExpenses();
		
		for (int i = 0; i < 3; i++) {
			CategoryTADao newcat = new CategoryTADao();
			newcat.setCatid(randomcats.get(i).getId());
			newcat.setAmount(-10D);
			exps.add(newcat);
		}
		model.setCategoryExpenses(exps);
		// save from model
		transService.saveFromExpenseEdit(model);

		// load from model
		ExpenseEditModel test = transService.loadExpenseEditModel(id);

		// ensure that model contains 4 expenses
		Assert.assertNotNull(test);
		Assert.assertEquals(4, test.getCategoryExpenses().size());
		
		
		
		// now try with delete
		model = transService.loadExpenseEditModel(id);
		List<CategoryTADao> cats = model.getCategoryExpenses();
		cats.remove(0);
		model.setCategoryExpenses(cats);
		// save from model
		transService.saveFromExpenseEdit(model);

		// load from model
		test = transService.loadExpenseEditModel(id);

		// ensure that model contains 3 expenses
		Assert.assertNotNull(test);
		Assert.assertEquals(3, test.getCategoryExpenses().size());		
		
		// now try with lots of same categories
		id = withoutcategorized.getId();
		model = transService.loadExpenseEditModel(id);
		exps = new ArrayList<CategoryTADao>();
		
		Long catid = randomcats.get(3).getId();
		for (int i = 0; i < 3; i++) {
			CategoryTADao newcat = new CategoryTADao();
			newcat.setCatid(catid);
			newcat.setAmount(-10D);
			exps.add(newcat);
		}
		
		model.setCategoryExpenses(exps);
		// save from model
		transService.saveFromExpenseEdit(model);

		// load from model
		test = transService.loadExpenseEditModel(id);

		// ensure that model contains 1 expense
		Assert.assertNotNull(test);
		Assert.assertEquals(1, test.getCategoryExpenses().size());			
		
	}
	
	/**
	 * 
	 addTransaction(BankTADao) assignCategory(Long, Long)
	 * assignExpensesFromCategories(Long, List<ExpenseDao>)
	 * assignFromCategories(List<TransToCategory>) assignFromCategories(Long,
	 * List<BankTADao>) clearCategoryAssignment(Long) deleteBankTA(Long)
	 * deleteCategoryExpense(Long) deleteCategoryExpenseByTransaction(Long)
	 * deleteCategoryExpenses(List<Long>) distributeAmounts(Double, int)
	 * doesDuplicateExist(BankTADao) getAllBankTransactions()
	 * getAssignedCategoryList() getCategoryExpForTrans(Long)
	 * getExpenseTotal(ExpenseCriteria, String)
	 * getExpenseTotalByMonth(ExpenseCriteria, String)
	 * getExpenseTotalByYear(ExpenseCriteria, String)
	 * getExpenses(ExpenseCriteria) getFirstTransDate() getMostRecentTransDate()
	 * getNewCategoryExpense(Long) getNoCategoryExpenses() getTransaction(Long)
	 * saveTransaction(BankTADao, List<CategoryTADao>) updateCategoryExp
	 */
}