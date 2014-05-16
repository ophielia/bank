package meg.bank.db;

import java.util.Date;
import java.util.List;

import meg.bank.bus.ExpenseCriteria;
import meg.bank.bus.dao.BankTADao;
import meg.bank.bus.dao.CategoryTADao;
import meg.bank.bus.report.CategorySummaryDisp;

public interface TransactionManagerDao {

	Date getMostRecentTransDate();

	Date getFirstTransDate();

	boolean duplicateExists(BankTADao trans);

	List getAllBankTAs();

	BankTADao getBankTA(Long transid);

	void createOrSaveCatTrans(CategoryTADao catta);
	void createOrSaveBankTrans(BankTADao bankta);



	List getNoCategoryExpenses();



	List getCategoryExpenses(Long transid);



	CategoryTADao getNewCategoryExpense(Long transactionId);



	void deleteCategoryExpense(Long deleteid);

	void deleteCategoryExpenseByTransaction(Long transid); 

	List retrieveTransactionsContaining(String containing);



	List getExpenses(ExpenseCriteria criteria);



	CategoryTADao getCatTA(Long catexpid);



	void deleteBankTA(Long todelete);


	List<CategorySummaryDisp> getExpenseTotalByYear(ExpenseCriteria criteria);
	
	List<CategorySummaryDisp> getExpenseTotalByMonth(ExpenseCriteria criteria);

	List<CategorySummaryDisp> getExpenseTotal(ExpenseCriteria criteria);










}
