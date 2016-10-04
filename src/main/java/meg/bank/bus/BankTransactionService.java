package meg.bank.bus;

import java.util.List;

import meg.bank.bus.dao.BankTADao;
import meg.bank.bus.dao.CategoryTADao;
import meg.bank.bus.dao.ExpenseDao;
import meg.bank.bus.report.elements.CategorySummaryDisp;
import meg.bank.web.model.ExpenseEditModel;

public interface BankTransactionService {

	public abstract void deleteBankTA(Long todelete);
	
	public abstract List<RuleAssignment> getAssignedCategoryList();
	public abstract List<BankTADao> getAllBankTransactions();
	public abstract void clearCategoryAssignment(Long toclear);
	public abstract BankTADao getTransaction(Long transid);
	public abstract List<BankTADao> getNoCategoryExpenses();
	public abstract List<CategoryTADao> getCategoryExpForTrans(Long transid);
	public abstract Double[] distributeAmounts(Double amount, int cnt);
	public abstract void saveTransaction(BankTADao transaction,
			List<CategoryTADao> categories);
	public abstract void deleteCategoryExpenses(List<Long> deleted);
	public abstract void assignFromCategories(Long catid,
			List<BankTADao> transtoadd);
	public abstract List<ExpenseDao> getExpenses(ExpenseCriteria criteria);

	public abstract List<CategorySummaryDisp> getExpenseTotalByYear(
			ExpenseCriteria criteria, String dispname);

	

	
	
	
	
	
	
	public abstract void deleteCategoryExpense(Long deleteid);
	public abstract void deleteCategoryExpenseByTransaction(Long transid);
	public abstract boolean doesDuplicateExist(BankTADao trans);

	public abstract BankTADao addTransaction(BankTADao trans);
	public abstract void assignCategory(Long transid, Long catid);
	public abstract ExpenseEditModel loadExpenseEditModel(Long id);
	public abstract void saveFromExpenseEdit(ExpenseEditModel model);
	public abstract void assignQuickGroupToExpenses(Long quickgroupid, List<String> selectedids) ;
	public abstract List<RuleAssignment> getRuleAssignments();	
	public abstract void updateExpenseByRuleAssignments(List<RuleAssignment> toupdate);
	public abstract void assignCategoriesToExpenses(Long catid, List<String>  selectedids);


	

	

	

	

	

	


	



}