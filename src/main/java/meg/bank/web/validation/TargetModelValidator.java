package meg.bank.web.validation;

import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import meg.bank.bus.CategoryService;
import meg.bank.bus.dao.CategoryDao;
import meg.bank.bus.dao.TargetDetailDao;
import meg.bank.bus.dao.TargetGroupDao;
import meg.bank.bus.repo.CategoryRepository;
import meg.bank.bus.repo.TargetDetailRepository;
import meg.bank.web.model.TargetModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class TargetModelValidator implements Validator {

	@Autowired
	CategoryRepository categoryRepo;

	@Autowired
	TargetDetailRepository targetDetRepo;
	
	@Override
	public boolean supports(Class clazz) {
		return TargetModel.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		TargetModel model = (TargetModel) target;
		TargetGroupDao targetgroup = model.getTargetgroup();

		// check standard fields
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		javax.validation.Validator validator = factory.getValidator();
		Set<ConstraintViolation<TargetGroupDao>> valerrors = validator
				.validate(targetgroup);

		// put JSR-303 errors into standard errors
		for (ConstraintViolation<TargetGroupDao> cv : valerrors) {
			errors.rejectValue(cv.getPropertyPath().toString(),
					cv.getMessageTemplate());
		}
		
		// check new or edit target detail
		if (model.containsDetailEntry()) {
			Double amount = model.getAmount();
			Long catid = model.getCatid();
			TargetDetailDao testdetail = new TargetDetailDao();
			testdetail.setAmount(amount);
			testdetail.setCatid(catid);
			
			// put JSR-303 errors into standard errors
			Set<ConstraintViolation<TargetDetailDao>> detailerrors = validator
					.validate(testdetail);
			for (ConstraintViolation<TargetDetailDao> cv : detailerrors) {
				errors.rejectValue(cv.getPropertyPath().toString(),
						cv.getMessageTemplate());
			}
			
			// check that catid 1) is a real cat and 2) doesn't already exist in group
			CategoryDao cat = categoryRepo.findOne(catid);
			if (cat==null) {
				errors.rejectValue("catid","field_invalid",new String[]{"Category"},"Enter that category");
			}
			List<TargetDetailDao> detexists = targetDetRepo.findByTargetGroupAndCategory(targetgroup, catid);
			if (detexists!=null && detexists.size()>0) {
				errors.rejectValue("catid","field_duplicate",new String[]{"Category"},"Already there");
			}
		}
	}

	public void validateTargetDetail(TargetModel model,
			BindingResult bindingResult) {
// MM implement this
		
	}

}
