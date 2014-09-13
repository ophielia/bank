package meg.bank.web;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import meg.bank.bus.CategoryService;
import meg.bank.bus.TargetService;
import meg.bank.bus.dao.CatRelationshipDao;
import meg.bank.bus.dao.CategoryDao;
import meg.bank.bus.dao.TargetDetailDao;
import meg.bank.bus.dao.TargetGroupDao;
import meg.bank.bus.imp.ImportManager;
import meg.bank.bus.repo.CategoryRepository;
import meg.bank.bus.repo.TargetGroupRepository;
import meg.bank.util.DateUtils;
import meg.bank.util.common.ColumnManagerService;
import meg.bank.util.common.db.ColumnValueDao;
import meg.bank.web.model.CategoryModel;
import meg.bank.web.model.TargetModel;
import meg.bank.web.validation.CategoryModelValidator;
import meg.bank.web.validation.TargetModelValidator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;


@RequestMapping("/target")
@SessionAttributes("targetModel")
@Controller
public class TargetController {

	@Autowired
	CategoryRepository catRepo;

	@Autowired
	CategoryService categoryService;

	

	@Autowired
	TargetService targetService;
	
	@Autowired
	TargetModelValidator targetValidator;
	
	
	@Autowired
	TargetGroupRepository targetGrpRep;
	
    @Autowired
    ColumnManagerService cvManager;	

	public final static class Action {
		public final static String AddDetail = "adddetail_action";
		public final static String EditDetail = "editdetail_action";
	}    
    
	private void populateCategoryList(Model uiModel) {
		uiModel.addAttribute("catList",catRepo.findAll());
	}
	
	@ModelAttribute("targettypelist")
	protected List<ColumnValueDao> referenceData(HttpServletRequest request, Object command,
			Errors errors) throws Exception {
		List<ColumnValueDao> reference = cvManager.getColumnValueList(TargetService.TargetTypeLkup);
		
		// return model
		return reference;
	}	
	
	@ModelAttribute("categorylist")
	protected List<CategoryDao> referenceCategoryData(HttpServletRequest request, Object command,
			Errors errors) throws Exception {
		List<CategoryDao> list = categoryService.getCategories(false);

		// return model
		return list;
	}		

	@ModelAttribute("yearlist")
	protected List<String> referenceYearlistData(HttpServletRequest request, Object command,
			Errors errors) throws Exception {
		Calendar oldcal = Calendar.getInstance();
		Calendar newcal = Calendar.getInstance();
		oldcal.add(Calendar.YEAR,-1);
		oldcal.set(Calendar.MONTH, Calendar.JANUARY);
		oldcal.set(Calendar.DAY_OF_MONTH, 1);
		newcal.set(newcal.get(Calendar.YEAR), Calendar.DECEMBER, 1);
		Date oldest = oldcal.getTime();
		Date newest = newcal.getTime();

		List<String> years = DateUtils.getYearsForSelect(oldest, newest);
		return years;

	}
	
	@ModelAttribute("monthlist")
	protected List<String> referenceMonthlistData(HttpServletRequest request, Object command,
			Errors errors) throws Exception {
		Calendar oldcal = Calendar.getInstance();
		Calendar newcal = Calendar.getInstance();
		oldcal.set(2007, Calendar.JANUARY, 1);
		newcal.set(newcal.get(Calendar.YEAR), Calendar.DECEMBER, 1);
		Date oldest = oldcal.getTime();
		Date newest = newcal.getTime();

		List<String> months = DateUtils.getMonthsForSelect(oldest, newest);
		return months;

	}	
	
	@RequestMapping(produces = "text/html")
    public String showList(Model uiModel) {
    	populateCategoryList(uiModel);
    	return "target/list";
    }

    @RequestMapping(value = "/create" ,method = RequestMethod.POST, produces = "text/html")
    public String create(TargetModel model,  Model uiModel,BindingResult bindingResult, HttpServletRequest httpServletRequest) {
    	targetValidator.validate(model, bindingResult);

		if (bindingResult.hasErrors()) {
			populateEditForm(uiModel, model);
			return "target/create";
		}
        uiModel.asMap().clear();

        // create target
        TargetGroupDao saved = targetService.saveOrUpdateTargetGroup(model.getTargetgroup());

        // repopulate model
        model.setTargetgroup(saved);
        populateEditForm(uiModel,model);
        return "redirect:/target/" + encodeUrlPathSegment(saved.getId().toString(), httpServletRequest);
    }

    @RequestMapping(params = "form",method = RequestMethod.GET, produces = "text/html")
    public String createForm(Model uiModel) {
    	HashMap<Long,CategoryDao> allcats = categoryService.getCategoriesAsMap();
    	TargetModel newmodel = new TargetModel(new TargetGroupDao());
        populateEditForm(uiModel, newmodel);
        return "target/create";
    }


    @RequestMapping(value = "/{id}", produces = "text/html")
    public String show(@PathVariable("id") Long id, Model uiModel) {
        TargetGroupDao tgroup = targetGrpRep.findOne(id);
        TargetModel model = targetService.loadTargetModel(id);
    	uiModel.addAttribute("targetModel", model);
        uiModel.addAttribute("itemId", id);
        return "target/show";
    }
        
    
    @RequestMapping(value="/edit/{id}", method = RequestMethod.PUT, produces = "text/html")
    public String edit(@PathVariable("id") Long id,TargetModel model,  Model uiModel,BindingResult bindingResult, HttpServletRequest httpServletRequest) {
    	targetValidator.validate(model, bindingResult);

		if (bindingResult.hasErrors()) {
//			populateEditForm(uiModel, model);
			return "target/edit/" + encodeUrlPathSegment(model.getTargetgroup().getId().toString(), httpServletRequest);
		}
        uiModel.asMap().clear();
// update entries.....
        // MM TODO
            // now, update relationship
           // CatRelationshipDao rel = categoryService.changeCatMembership(cat.getId(), model.getParentcatid());

        return "redirect:/target";
        //return "redirect:/target/" + encodeUrlPathSegment(cat.getId().toString(), httpServletRequest);
    }

	@RequestMapping(value = "/edit/{id}", params = "adddetail", method = RequestMethod.PUT, produces = "text/html")
	public String addTargetDetail(@PathVariable("id") Long id,
			TargetModel model, Model uiModel, BindingResult bindingResult,
			HttpServletRequest httpServletRequest) {
		// if action available, send to directAction
		if (model.getAction() != null) {
			return directAction(model, uiModel, bindingResult,
					httpServletRequest);
		}
		// if no action available, send directly to to SaveAddTargetDetail
		return saveAddTargetDetail(model, uiModel, bindingResult,
				httpServletRequest);
	}


    @RequestMapping(params = "form",value = "/edit/{id}",method = RequestMethod.GET, produces = "text/html")
    public String createEditForm(@PathVariable("id") Long id,Model uiModel) {
    	// pull target group to be edited
    	TargetModel model = targetService.loadTargetModel(id);
    	
    	// place in model
    	populateEditForm(uiModel,model);
    	
    	// return edit view
    	return "target/edit";
    	
    	
    	
    	
    	/*
    	 * 
    	 * HashMap<Long,CategoryDao> allcats = categoryService.getCategoriesAsMap();
    	CategoryDao cat = catRepo.findOne(id);
    	if (cat!=null) {
        	TargetModel newmodel = new TargetModel(cat,allcats);
        	List<CategoryDao> list = categoryService.getDirectSubcategories(cat.getId());
        	newmodel.setSubcategories(list);

            populateEditForm(uiModel, newmodel);
            return "target/edit";

    	}

    	return "redirect:/target";
    	    	 */
    }

    void populateEditForm(Model uiModel, TargetModel model) {
        uiModel.addAttribute("targetModel", model);
    }

    String encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
        String enc = httpServletRequest.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        } catch (UnsupportedEncodingException uee) {}
        return pathSegment;
    }
    
    private String saveAddTargetDetail(TargetModel model,  Model uiModel,BindingResult bindingResult, HttpServletRequest httpServletRequest) {
    	// validate target detail entry
    	targetValidator.validate(model,bindingResult);
    	// if no errors, put detail into TargetDetailDao object
    	if (bindingResult.hasErrors()) {
			populateEditForm(uiModel, model);
			return "target/edit";
		}
    	// create new target detail
    	TargetDetailDao newdetail = new TargetDetailDao();
    	newdetail.setCatid(model.getCatid());
    	newdetail.setAmount(model.getAmount());
    	// service call to add target detail
    	targetService.addTargetDetailToGroup(newdetail,model.getTargetgroup());
    	// save any changes to group itself
    	targetService.saveOrUpdateTargetGroup(model.getTargetgroup());
    	// populate edit form
    	Long groupid = model.getTargetgroup().getId();
    	model = targetService.loadTargetModel(groupid);
    	// return edit jsp
    	populateEditForm(uiModel,model);
    	return "target/edit";
    	
    }
    
    
    private String directAction(TargetModel model,  Model uiModel,BindingResult bindingResult, HttpServletRequest httpServletRequest) {
    	if (model.getAction().equals(Action.AddDetail)) {
    		// save add detail
    		return saveAddTargetDetail(model,  uiModel, bindingResult,  httpServletRequest);
    	} else if (model.getAction().equals(Action.EditDetail)) {
    		// save edit detail
    	} 
    		// save global TargetModel edit
        	return "target/edit/" + encodeUrlPathSegment(model.getTargetgroup().getId().toString(), httpServletRequest);
    	
    }
}
