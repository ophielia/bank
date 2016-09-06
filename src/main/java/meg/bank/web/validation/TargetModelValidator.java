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

	@Autowired
	TargetGroupRepository targetGrpRepo;
	
	@Autowired
	TargetService targetService;

	@Override
	public boolean supports(Class clazz) {
		return TargetModel.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		validateGroup(target,errors);
		validateDetails(target,errors);
	}

	public void validateGroup(Object target, Errors errors) {
		TargetModel model = (TargetModel) target;
		TargetGroupDao targetgroup = model.getTargetgroup();
		boolean isnewgroup = targetgroup.getId()==null;
		// check standard fields
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		javax.validation.Validator validator = factory.getValidator();
		Set<ConstraintViolation<TargetGroupDao>> valerrors = validator
				.validate(targetgroup);
		// put JSR-303 errors into standard errors
		for (ConstraintViolation<TargetGroupDao> cv : valerrors) {
			errors.rejectValue(cv.getPropertyPath().toString(),
					stripBraces(cv.getMessageTemplate()));
		}

		
		// check no duplicate of month tag or year tag
		/*if (targetgroup.getTargettype()==TargetService.TargetType.Month) {
			List<TargetGroupDao> duptags = targetGrpRepo.findTargetsByTypeAndMonthTag(TargetService.TargetType.Month, targetgroup.getMonthtag());
			
			if (duptags!=null&& duptags.size()>0) {
				if (isnewgroup) {
					errors.rejectValue("monthtag","field_duplicate",new Object[]{"Monthtag"},"This monthtag already exists");	
				} else {
					TargetGroupDao duptag = duptags.get(0);
					if (duptag.getId().longValue()!= targetgroup.getId().longValue()) {
						errors.rejectValue("monthtag","field_duplicate",new Object[]{"Monthtag"},"This monthtag already exists");	
					}
				}
			}
		} else {
			List<TargetGroupDao> duptags = targetGrpRepo.findTargetsByTypeAndYearTag(TargetService.TargetType.Year, targetgroup.getYeartag());
			if (duptags!=null&& duptags.size()>0) {
				if (isnewgroup) {
					errors.rejectValue("yeartag","field_duplicate",new Object[]{"Yeartag"},"This yeartag already exists");	
				} else {
					TargetGroupDao duptag = duptags.get(0);
					if (duptag.getId().longValue()!= targetgroup.getId().longValue()) {
						errors.rejectValue("yeartag","field_duplicate",new Object[]{"Yeartag"},"This yeartag already exists");	
					}
				}
			}
		}*/
		// check not the default
		if (targetgroup.getIsdefault().booleanValue()) {
			TargetGroupDao defaultgrp = targetService.getDefaultTargetGroup(targetgroup.getTargettype());
			if (defaultgrp!=null) {
				// check that it's not this one
				if (!isnewgroup && defaultgrp.getId().longValue()!=targetgroup.getId().longValue())
				errors.rejectValue("isdefault","field_baddefault");
			}
		}
		


	}

	public void validateDetails(Object target, Errors errors) {
		TargetModel model = (TargetModel) target;
		TargetGroupDao targetgroup = model.getTargetgroup();


		// check new or edit target detail
		if (model.containsDetailEntry()) {
			Double amount = model.getAmount();
			Long catid = model.getCatid();
			TargetDetailDao testdetail = new TargetDetailDao();
			testdetail.setAmount(amount);
			testdetail.setCatid(catid);
			testdetail.setId(new Long(model.getActionid()));
			boolean isnewdet = testdetail.getId()==0;
			
			// put JSR-303 errors into standard errors
			ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
			javax.validation.Validator validator = factory.getValidator();
			Set<ConstraintViolation<TargetDetailDao>> detailerrors = validator
					.validate(testdetail);
			for (ConstraintViolation<TargetDetailDao> cv : detailerrors) {
				errors.rejectValue(cv.getPropertyPath().toString(),
						stripBraces(cv.getMessageTemplate()));
			}

			// check that catid 1) is a real cat and 2) doesn't already exist in group
			CategoryDao cat = categoryRepo.findOne(catid);
			if (cat==null) {
				errors.rejectValue("catid","field_invalid",new String[]{"Category"},"Enter that category");
			}
			List<TargetDetailDao> detexists = targetDetRepo.findByTargetGroupAndCategory(targetgroup, catid);
			if (detexists!=null && detexists.size()>0) {
				TargetDetailDao existingdet = detexists.get(0);
				if (!isnewdet && existingdet.getId().longValue()!=testdetail.getId().longValue()) {
					errors.rejectValue("catid","field_duplicate",new String[]{"Category"},"Already there");	
				}
				
			}
		}
	}

	private String stripBraces(String tostrip) {
		tostrip=tostrip.substring(1);
		tostrip=tostrip.substring(0,tostrip.length()-1);
		return tostrip;
	}

	public void validateTargetDetail(TargetModel model,
			BindingResult bindingResult) {
// MM implement this

	}

}
