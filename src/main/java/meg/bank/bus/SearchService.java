package meg.bank.bus;

import java.util.List;

import meg.bank.bus.dao.ExpenseDao;
import meg.bank.bus.report.CategorySummaryDisp;

public interface SearchService {

	public abstract List<ExpenseDao> getExpenses(ExpenseCriteria criteria);

	public abstract List<CategorySummaryDisp> getExpenseTotalByMonth(
			ExpenseCriteria criteria);

	public abstract List<CategorySummaryDisp> getExpenseTotalByYear(
			ExpenseCriteria criteria);

	public abstract List<CategorySummaryDisp> getExpenseTotal(
			ExpenseCriteria criteria);

}