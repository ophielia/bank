package meg.bank.bus;


import java.util.ArrayList;
import java.util.List;

import meg.bank.bus.dao.CategoryDao;
import meg.bank.bus.dao.ExpenseDao;
import meg.bank.bus.repo.CategoryRuleRepository;

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
CategoryRuleRepository catRuleRep;	

CategoryDao tCat;

@Before
public void setup() {
	tCat = catService.addCategory("tCat", "", false, true);
	
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
			if (updateids.size()>3) {
				break;
			}
		} 
	}
	
	// call service
	transService.assignCategoriesToExpenses(toassign.getId(), updateids);
	
	// get transactions
	List<ExpenseDao> testresults = searchService.getExpenseListByIds(updateids);

	// test that each has a category now
	Assert.assertNotNull(testresults);
	for (ExpenseDao exp: testresults) {
		Assert.assertTrue(exp.getHascat().booleanValue());
	}
	

}



/**
 * 
addTransaction(BankTADao)
	assignCategory(Long, Long)
	assignExpensesFromCategories(Long, List<ExpenseDao>)
assignFromCategories(List<TransToCategory>)
assignFromCategories(Long, List<BankTADao>)
clearCategoryAssignment(Long)
deleteBankTA(Long)
deleteCategoryExpense(Long)
deleteCategoryExpenseByTransaction(Long)
deleteCategoryExpenses(List<Long>)
distributeAmounts(Double, int)
doesDuplicateExist(BankTADao)
getAllBankTransactions()
getAssignedCategoryList()
getCategoryExpForTrans(Long)
getExpenseTotal(ExpenseCriteria, String)
getExpenseTotalByMonth(ExpenseCriteria, String)
getExpenseTotalByYear(ExpenseCriteria, String)
getExpenses(ExpenseCriteria)
getFirstTransDate()
getMostRecentTransDate()
getNewCategoryExpense(Long)
getNoCategoryExpenses()
getTransaction(Long)
saveTransaction(BankTADao, List<CategoryTADao>)
	updateCategoryExp
 */
}