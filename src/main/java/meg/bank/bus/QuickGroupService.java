package meg.bank.bus;

import java.util.List;

import meg.bank.bus.dao.CategoryTADao;
import meg.bank.bus.dao.QuickGroup;
import meg.bank.web.model.QuickGroupModel;

public interface QuickGroupService {

	QuickGroupModel createQuickGroupFromExpense(Long transid);

	QuickGroupModel loadQuickGroupModelForId(Long id);

	QuickGroupModel saveFromQuickGroupModel(QuickGroupModel model);

	List<CategoryTADao> getExpDetailsForQuickGroup(double amount, Long groupid);
	
	List<QuickGroup> getAllQuickGroups();

	

}