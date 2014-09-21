package meg.bank.web.validation;

import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import meg.bank.bus.TargetService;
import meg.bank.bus.dao.CategoryDao;
import meg.bank.bus.dao.TargetDetailDao;
import meg.bank.bus.dao.TargetGroupDao;
import meg.bank.bus.repo.CategoryRepository;
import meg.bank.bus.repo.TargetDetailRepository;
import meg.bank.bus.repo.TargetGroupRepository;
import meg.bank.web.model.ExpenseListModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ExpenseListModelValidator implements Validator {

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
		return ExpenseListModel.class.equals(clazz);
	}


	public void validate(Object target,Errors errors) {
		// empty method
	}

	public void validateUpdate(Object target, Errors errors) {
		ExpenseListModel model = (ExpenseListModel) target;
		
		// check that the category has been set
		if (model.getBatchUpdate()==null || model.getBatchUpdate().longValue()==0) {
			errors.rejectValue("batchUpdate",
					"field_required");
		}

	}



}
