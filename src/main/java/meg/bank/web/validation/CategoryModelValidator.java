package meg.bank.web.validation;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import meg.bank.bus.CategoryService;
import meg.bank.bus.dao.CategoryDao;
import meg.bank.web.model.CategoryModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class CategoryModelValidator implements Validator {

	@Autowired
	CategoryService categoryService;

	@Override
	public boolean supports(Class clazz) {
		return CategoryModel.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		CategoryModel model = (CategoryModel) target;
		CategoryDao acct = model.getCategory();

		// check standard fields
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		javax.validation.Validator validator = factory.getValidator();
		Set<ConstraintViolation<CategoryDao>> valerrors = validator
				.validate(acct);

		// put JSR-303 errors into standard errors
		for (ConstraintViolation<CategoryDao> cv : valerrors) {
			errors.rejectValue(cv.getPropertyPath().toString(),
					cv.getMessageTemplate());
		}
		
		// check circular references
		boolean hascircular = categoryService.hasCircularReference(model.getParentcatid(), model.getCategory());
		if (hascircular) {
			errors.rejectValue("parentcatid", "error_circularref","doesn't work");
		}
	}

}
