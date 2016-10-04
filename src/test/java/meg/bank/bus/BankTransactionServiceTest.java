package meg.bank.bus;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import meg.bank.bus.dao.BankTADao;
import meg.bank.bus.dao.BankTADaoDataOnDemand;
import meg.bank.bus.dao.CategoryDao;
import meg.bank.bus.dao.CategoryRuleDao;
import meg.bank.bus.dao.CategoryTADao;
import meg.bank.bus.dao.ExpenseDao;
import meg.bank.bus.dao.QuickGroup;
import meg.bank.bus.dao.QuickGroupDetail;
import meg.bank.bus.repo.BankTARepository;
import meg.bank.bus.repo.CategoryRuleRepository;
import meg.bank.bus.repo.CategoryTARepository;
import meg.bank.web.model.AssignmentListModel;
import meg.bank.web.model.ExpenseEditModel;

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
public class BankTransactionServiceTest {

	@Autowired
	BankTransactionService transService;

	@Autowired
	CategoryService catService;
	
	@Autowired
	QuickGroupService quickGroupService;	

	@Autowired
	SearchService searchService;

	@Autowired
	BankTARepository bankRepo;

	@Autowired
	CategoryTARepository catExpRepo;

	@Autowired
	CategoryRuleRepository catRuleRepo;

	
	CategoryDao tCat;
	CategoryDao tCat2;
	BankTADao withcategorized;
	BankTADao withoutcategorized;

	List<CategoryDao> randomcats;
	CategoryRuleDao tCatRule;
	List<Long> banktas;
	
	@Before
	public void setup() {
		tCat = catService.addCategory("tCat", "", false, true);
		tCat2 = catService.addCategory("tCat2", "", false, true);

		// trans with category
		// make BankTrans
		BankTADaoDataOnDemand bDod = new BankTADaoDataOnDemand();
		withcategorized = bDod.getNewTransientBankTADao(12);
		withcategorized = bankRepo.saveAndFlush(withcategorized);


		// make CategoryDao
		CategoryTADao cat = new CategoryTADao();
		cat.setCatid(tCat.getId());
		cat.setAmount(100D);
		cat.setCreatedon(new Date());
		cat.setBanktrans(withcategorized);
		cat = catExpRepo.saveAndFlush(cat);

		// trans without category
		bDod = new BankTADaoDataOnDemand();
		withoutcategorized = bDod.getNewTransientBankTADao(12);
		withoutcategorized.setDetail("x x x tTest Text x x x");
		withoutcategorized.setHascat(false);
		withoutcategorized.setAmount(-100.0D);
		bankRepo.saveAndFlush(withoutcategorized);
		
		randomcats=catService.getCategories(true);
		
		// make list of 5 bank transaction ids
		banktas = new ArrayList<Long>();
		for (int i=0;i<5;i++) {
			BankTADao test1 = bDod.getNewTransientBankTADao(10+i);
			test1.setDetail("x x x tBeep Bop"+i+" x x x");
			test1.setHascat(false);
			test1.setAmount(-100.0D);
			test1 = bankRepo.saveAndFlush(test1);
			banktas.add(test1.getId());
		}
		
		
		// CategoryRule
		tCatRule = new CategoryRuleDao();
		tCatRule.setCategoryId(tCat.getId());
		tCatRule.setContaining("tTest Text");
		tCatRule.setLineorder(199L);
		tCatRule = catRuleRepo.saveAndFlush(tCatRule);
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
	public void testAssignQuickGroupToExpenses() {
		// get category for test
		List<QuickGroup> allqcs = quickGroupService.getAllQuickGroups();
		QuickGroup toassign = allqcs.get(1);
		List<QuickGroupDetail> details = quickGroupService.getDetailsForQuickGroup(toassign);
		int testsize=details.size();

		// get all transactions
		List<ExpenseDao> allexpenses = searchService.getAllExpenses();

		// make list of three transactions to update - first, with no categories
		List<String> updateids = new ArrayList<String>();
		List<Long> updatetestids = new ArrayList<Long>();
		for (ExpenseDao exp : allexpenses) {
			if (!exp.getHascat().booleanValue()) {
				updateids.add(exp.getId());
				updatetestids.add(exp.getTransid());
				if (updateids.size() > 3) {
					break;
				}
			}
		}

		// call service
		transService.assignQuickGroupToExpenses(toassign.getId(), updateids);

		// get transactions
		List<ExpenseDao> testresults = searchService
				.getExpenseListByIds(updateids);
		List<BankTADao> testtrans = bankRepo.findAll(updatetestids);

		// test that each has a category now
		Assert.assertNotNull(testresults);
		for (BankTADao exp : testtrans) {
			Assert.assertTrue(exp.getHascat().booleanValue());
			// test that each has requisite number of categories
			List<CategoryTADao> expdetails = catExpRepo.findByBankTrans(exp);
			Assert.assertEquals(testsize,expdetails.size());
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
		Long id = withoutcategorized.getId();
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
		Assert.assertEquals(3, test.getCategoryExpenses().size());
		
		
		
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
		Assert.assertEquals(2, test.getCategoryExpenses().size());		
		
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
	
	@Test
	public void testGetRuleAssignments() {
		// get ids to look for, transid (withoutcategorized) and tCatId
		Long transid = withoutcategorized.getId();
		Long catid = tCat.getId();
		
		// service call
		List<RuleAssignment> assignments = transService.getRuleAssignments();
		
		// something returned
		Assert.assertNotNull(assignments);
		
		RuleAssignment test = null;
		for (RuleAssignment assignm:assignments) {
			if (assignm.getCategoryId().longValue()==catid.longValue()) {
				test = assignm;
				break;
			}
		}
		
		// Assert not null
		Assert.assertNotNull(test);
		// Assert contains one trans
		Assert.assertNotNull(test.getTransactions());
		Assert.assertEquals(1,test.getTransactions().size());
		// Assert Trans has transid
		BankTADao testbt = test.getTransactions().get(0);
		Assert.assertEquals(transid,testbt.getId());
		 // Assert test has Category
		Assert.assertNotNull(test.getCategory());
	}
	
	@Test
	public void testUpdateExpenseByRuleAssignments() {
		Long catid1 = tCat.getId();
		Long catid2 = tCat2.getId();
		
		// set up RuleAssignmentList
		List<RuleAssignment> assignments = new ArrayList<RuleAssignment>();
		RuleAssignment rass = new RuleAssignment(tCat);
		List<Long> transids = banktas.subList(0, 3);
		List<BankTADao> trans = bankRepo.findAll(transids);
		for (BankTADao tr : trans) {
			rass.addTransaction(tr);
		}
		assignments.add(rass);
		rass = new RuleAssignment(tCat2);
		transids = banktas.subList(3, 5);
		trans = bankRepo.findAll(transids);
		for (BankTADao tr : trans) {
			rass.addTransaction(tr);
		}
		assignments.add(rass);		

		// service call
		transService.updateExpenseByRuleAssignments(assignments);
		
		// loop through list of ids (banktas), testing each
		for (int i=0;i<5;i++) {
			// get banktadao - assert hascat is true
			BankTADao bankta = bankRepo.findOne(banktas.get(i));
			Assert.assertTrue(bankta.getHascat());
			// get expensedetails for banktadao
			List<CategoryTADao> expdetails = transService.getCategoryExpForTrans(banktas.get(i));
			// assert list has 1
			Assert.assertNotNull(expdetails);
			Assert.assertEquals(1, expdetails.size());
			// assert category is correct
			CategoryTADao detail = expdetails.get(0);
			Long testcatid = i<3?catid1:catid2;
			Assert.assertEquals(testcatid, detail.getCatid());
		}
	}
	
	
	@Test
	public void testGetCheckedRuleAssignments() {
		// actually testing the model, but this is crucial
		// to the BTS rule assignment methods

		Long catid1 = tCat.getId();
		Long catid2 = tCat2.getId();

		// make a list of checked
		List<Boolean> checked = new ArrayList<Boolean>();
		checked.add(true);
		checked.add(false);
		checked.add(true);
		checked.add(false);
		checked.add(true);
		// make List of RuleAssignments
		List<RuleAssignment> assignments = new ArrayList<RuleAssignment>();
		RuleAssignment rass = new RuleAssignment(tCat);
		List<Long> transids = banktas.subList(0, 3);
		List<BankTADao> trans = bankRepo.findAll(transids);
		for (BankTADao tr : trans) {
			rass.addTransaction(tr);
		}
		assignments.add(rass);
		rass = new RuleAssignment(tCat2);
		transids = banktas.subList(3, 5);
		trans = bankRepo.findAll(transids);
		for (BankTADao tr : trans) {
			rass.addTransaction(tr);
		}
		assignments.add(rass);

		// create model, and set checked
		AssignmentListModel model = new AssignmentListModel(assignments);
		model.setChecked(checked);

		// model call
		List<RuleAssignment> testresults = model.getCheckedRuleAssignments();

		// test that correct transactions are there
		Assert.assertNotNull(testresults);
		// rule assignment for cat1
		RuleAssignment test = null;
		for (RuleAssignment assignm : testresults) {
			if (assignm.getCategoryId().longValue() == catid1.longValue()) {
				test = assignm;
				break;
			}
		}
		// should have 2 transactions
		Assert.assertEquals(2, test.getTransactionCount());
		// put transactions in Hash
		HashMap<Long, BankTADao> hash = new HashMap<Long, BankTADao>();
		for (BankTADao ta : test.getTransactions()) {
			hash.put(ta.getId(), ta);
		}
		// check for existence of banktas idx 0
		Assert.assertTrue(hash.containsKey(banktas.get(0)));
		// check for existence of banktas idx 2
		Assert.assertTrue(hash.containsKey(banktas.get(2)));

		// rule assignment for cat2
		test = null;
		for (RuleAssignment assignm : testresults) {
			if (assignm.getCategoryId().longValue() == catid2.longValue()) {
				test = assignm;
				break;
			}
		}
		Assert.assertNotNull(test);
		// should have 2 transactions
		Assert.assertEquals(1, test.getTransactionCount());
		// put transactions in Hash
		hash = new HashMap<Long, BankTADao>();
		for (BankTADao ta : test.getTransactions()) {
			hash.put(ta.getId(), ta);
		}
		// check for existence of banktas idx 4
		Assert.assertTrue(hash.containsKey(banktas.get(4)));
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