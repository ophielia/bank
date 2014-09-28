package meg.bank.bus;

import java.util.Date;
import java.util.List;

import meg.bank.bus.dao.BankTADao;
import meg.bank.bus.dao.CategoryTADao;
import meg.bank.bus.dao.ExpenseDao;
import meg.bank.bus.report.CategorySummaryDisp;
import meg.bank.web.model.ExpenseEditModel;
import meg.bank.web.model.QuickGroupModel;

public interface QuickGroupService {

	QuickGroupModel createQuickGroupFromExpense(Long transid);

	QuickGroupModel loadQuickGroupModelForId(Long id);

	QuickGroupModel saveFromQuickGroupModel(QuickGroupModel model);

	List<CategoryTADao> getExpDetailsForQuickGroup(double amount, Long groupid);


}