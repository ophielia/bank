package meg.bank.bus;

import java.util.Date;
import java.util.List;

import meg.bank.bus.dao.BankTADao;
import meg.bank.bus.dao.CategoryTADao;
import meg.bank.bus.dao.ExpenseDao;
import meg.bank.bus.report.CategorySummaryDisp;

public interface BankTransactionService {

	public abstract void deleteBankTA(Long todelete);

	public abstract Date getFirstTransDate();

	public abstract List<TransToCategory> getAssignedCategoryList();

	public abstract CategoryTADao getNewCategoryExpense(Long transactionId);

	public abstract void deleteCategoryExpense(Long deleteid);

	public abstract void deleteCategoryExpenseByTransaction(Long transid);

	public abstract boolean doesDuplicateExist(BankTADao trans);

	public abstract Date getMostRecentTransDate();

	public abstract void addTransaction(BankTADao trans);

	public abstract List<BankTADao> getAllBankTransactions();

	public abstract void clearCategoryAssignment(Long toclear);

	public abstract BankTADao getTransaction(Long transid);

	public abstract void assignCategory(Long transid, Long catid);

	public abstract List<BankTADao> getNoCategoryExpenses();

	public abstract List<CategoryTADao> getCategoryExpForTrans(Long transid);

	public abstract Double[] distributeAmounts(Double amount, int cnt);

	public abstract void saveTransaction(BankTADao transaction,
			List<CategoryTADao> categories);

	public abstract void deleteCategoryExpenses(List<Long> deleted);

	public abstract void assignFromCategories(
			List<TransToCategory> assignedcategories);

	public abstract void assignFromCategories(Long catid,
			List<BankTADao> transtoadd);

	public abstract List<ExpenseDao> getExpenses(ExpenseCriteria criteria);

	public abstract void assignExpensesFromCategories(Long catid, List selected);

	public abstract List<CategorySummaryDisp> getExpenseTotalByMonth(
			ExpenseCriteria criteria, String dispname);

	public abstract List<CategorySummaryDisp> getExpenseTotalByYear(
			ExpenseCriteria criteria, String dispname);

	public abstract List<CategorySummaryDisp> getExpenseTotal(
			ExpenseCriteria criteria, String dispname);

}