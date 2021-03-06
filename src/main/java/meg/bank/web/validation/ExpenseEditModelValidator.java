package meg.bank.web.validation;

import java.util.List;

import meg.bank.bus.TargetService;
import meg.bank.bus.dao.CategoryTADao;
import meg.bank.bus.repo.CategoryRepository;
import meg.bank.bus.repo.TargetDetailRepository;
import meg.bank.bus.repo.TargetGroupRepository;
import meg.bank.web.model.ExpenseEditModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ExpenseEditModelValidator implements Validator {

	@Autowired
	CategoryRepository categoryRepo;

	@Autowired
	TargetDetailRepository targetDetRepo;

	@Autowired
	TargetGroupRepository targetGrpRepo;
	
	@Autowired
	TargetService targetService;

	@Override
	public boolean supports(Class clazz) {
		return ExpenseEditModel.class.equals(clazz);
	}


	public void validate(Object target,Errors errors) {
		ExpenseEditModel model = (ExpenseEditModel) target;
		
		// need to check that categories add up
		double transtotal = model.getAmount().doubleValue();
		double catexptotal = model.getCategorizedTotal().doubleValue();
		if (transtotal!=catexptotal) {
			errors.rejectValue("",
					"field_doesntaddup");			
		}
		
		// go through all expenses, ensuring that categories are set
		List<CategoryTADao> expenses = model.getCategoryExpenses();
		for (CategoryTADao catexp:expenses) {
			if (catexp.getCatid()==null || catexp.getCatid()==0) {
				errors.rejectValue("",
						"field_required_param",new String[]{"Category"}, "Category is required.");	
				break;
			}
		}
	}

	
}
