package meg.bank.web.validation;

import java.util.List;

import meg.bank.bus.TargetService;
import meg.bank.bus.dao.QuickGroupDetail;
import meg.bank.bus.repo.CategoryRepository;
import meg.bank.bus.repo.TargetDetailRepository;
import meg.bank.bus.repo.TargetGroupRepository;
import meg.bank.web.model.QuickGroupModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class QuickGroupModelValidator implements Validator {

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
		return QuickGroupModel.class.equals(clazz);
	}


	public void validate(Object target,Errors errors) {
		QuickGroupModel model = (QuickGroupModel) target;
		
		// check that name is entered
		if (model.getName()==null) {
			errors.rejectValue("name","field_required");
		}
		if (model.getName().length()==0) {
			errors.rejectValue("name","field_required");
		}
		// check that name is under 100 char
		if (model.getName().length()>100) {
			errors.rejectValue("",
					"name",new String[]{"Name","100 characters"}, "Name is too long");
		}

		
		// check that each detail has a category
		double detailtotal=0;
		List<QuickGroupDetail> details = model.getDetails();
		boolean hascaterror=false;
		for (QuickGroupDetail detail:details) {
			if (detail.getCatid()==null || detail.getCatid()==0) {
				if (!hascaterror) {
					errors.rejectValue("",
							"field_required_param",new String[]{"Category"}, "Category is required.");
					hascaterror=true;
				}
			}
			detailtotal+= detail.getPercentage().doubleValue();
		}
		// check that details add up to 100
		if (detailtotal!=100.0) {
			errors.rejectValue("",
					"error_details_dontaddup");
		}		
		
	}


}
