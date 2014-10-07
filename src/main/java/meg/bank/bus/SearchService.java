package meg.bank.bus;

import java.util.Date;
import java.util.List;

import meg.bank.bus.dao.ExpenseDao;
import meg.bank.bus.report.CategorySummaryDisp;

public interface SearchService {

	public abstract List<ExpenseDao> getExpenses(ExpenseCriteria criteria);
	
	public abstract List<ExpenseDao> getAllExpenses();
	
	public abstract List<CategorySummaryDisp> getExpenseTotalByMonth(
			ExpenseCriteria criteria);

	public abstract List<CategorySummaryDisp> getExpenseTotalByYear(
			ExpenseCriteria criteria);

	
	public List<ExpenseDao> getExpenseListByIds(List<String> idlist) ;	

	public abstract Date getFirstTransDate();
	
	public abstract Date getMostRecentTransDate();

}