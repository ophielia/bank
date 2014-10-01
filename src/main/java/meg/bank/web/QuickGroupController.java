package meg.bank.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import meg.bank.bus.BankTransactionService;
import meg.bank.bus.CategoryService;
import meg.bank.bus.QuickGroupService;
import meg.bank.bus.dao.CategoryDao;
import meg.bank.bus.dao.QuickGroup;
import meg.bank.bus.dao.QuickGroupDetail;
import meg.bank.util.common.ColumnManagerService;
import meg.bank.web.model.QuickGroupModel;
import meg.bank.web.validation.QuickGroupModelValidator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

@SessionAttributes("quickGroupModel")
@RequestMapping("/quickgroup")
@Controller
public class QuickGroupController {


	@Autowired
	QuickGroupService quickGroupService;

	@Autowired
	BankTransactionService transService;

    @Autowired
    ColumnManagerService cvManager;

    @Autowired
    CategoryService categoryService;

	@Autowired
	QuickGroupModelValidator modelValidator;

	@ModelAttribute("categorylist")
	protected List<CategoryDao> referenceCategoryData(HttpServletRequest request, Object command,
			Errors errors) throws Exception {
		List<CategoryDao> list = categoryService.getCategories(false);

		// return model
		return list;
	}
	
	@ModelAttribute("quickgrouplist")
	protected List<QuickGroup> referenceQuickGroups(HttpServletRequest request, Object command,
			Errors errors) throws Exception {
		List<QuickGroup> list = quickGroupService.getAllQuickGroups();

		// return model
		return list;
	}	

    @RequestMapping( method = RequestMethod.GET, produces = "text/html")
    public String showList(  Model uiModel,HttpServletRequest httpServletRequest) {
    	return "quickgroup/list";
    }    
    
    @RequestMapping(value="/create", method = RequestMethod.GET, produces = "text/html")
    public String showCreateForm(  Model uiModel,HttpServletRequest httpServletRequest) {
    	HashMap<Long, CategoryDao> categoryref = categoryService.getCategoriesAsMap();
    	// put new QuickGroupModel together
    	QuickGroupModel model = new QuickGroupModel(new QuickGroup(),new ArrayList<QuickGroupDetail>(),categoryref);
    	// set model in uiModel
    	uiModel.addAttribute("quickGroupModel", model);    	
    	return "quickgroup/edit";
    }        
   
    @RequestMapping(value="/create", method = RequestMethod.GET, params="transid", produces = "text/html")
    public String showCreateFromExpenseDetail(@RequestParam("transid") Long transid,  Model uiModel,HttpServletRequest httpServletRequest) {
    	// create model from bank transaction
    	QuickGroupModel model = quickGroupService.createQuickGroupFromExpense(transid);
    	model.setFromExpensePage(true);
    	// set model in uiModel
    	uiModel.addAttribute("quickGroupModel", model);    	
    	return "quickgroup/edit";
    }        
        
    @RequestMapping(value="/edit/{id}", method = RequestMethod.GET, produces = "text/html")
    public String showEditForm(@PathVariable("id") Long id,  Model uiModel,HttpServletRequest httpServletRequest) {
    	HashMap<Long, CategoryDao> categoryref = categoryService.getCategoriesAsMap();
    	if (id!=null) {
    		QuickGroupModel model = quickGroupService.loadQuickGroupModelForId(id);
    		uiModel.addAttribute("quickGroupModel", model); 
    	} else {
        	QuickGroupModel model = new QuickGroupModel(new QuickGroup(),new ArrayList<QuickGroupDetail>(),categoryref);
        	// set model in uiModel
        	uiModel.addAttribute("quickGroupModel", model);   	
    	}
    	 return "quickgroup/edit";
    }
    
    @RequestMapping(value="/edit", method = RequestMethod.PUT, params = "addcategory",produces = "text/html")
    public String addTargetDetail(QuickGroupModel model,Model uiModel,HttpServletRequest httpServletRequest) {
    	// write any changes to expenses
    	model.copyEntriesIntoCategories();
    	// add new CategoryTADao to list in model
    	List<QuickGroupDetail> details = model.getDetails();
    	Integer editidx = details.size();
    	details.add(new QuickGroupDetail());
    	model.setDetails(details);
    	// set editindex to size of list - 1
    	model.setEditIdx(editidx);
    	uiModel.addAttribute("quickGroupModel", model);
    	return "quickgroup/edit";
    }

    @RequestMapping(value="/edit", method = RequestMethod.PUT, params = "removecategory",produces = "text/html")
    public String removeCategoryExp(QuickGroupModel model,Model uiModel,HttpServletRequest httpServletRequest) {
    	// write any changes to expenses
    	model.copyEntriesIntoCategories();
    	// get editIndex
    	int editidx = model.getEditIdx();
    	List<QuickGroupDetail> details = model.getDetails();
    	details.remove(editidx);
    	model.setDetails(details);
    	model.setEditIdx(999);
    	uiModel.addAttribute("quickGroupModel", model);
    	return "quickgroup/edit";
    }    
    
    @RequestMapping(value="/edit", method = RequestMethod.PUT, params = "resetchanges",produces = "text/html")
    public String resetAllChanges(QuickGroupModel model,Model uiModel,HttpServletRequest httpServletRequest) {
        if (model.getFromExpensePage()) {
        	Long transid = model.getTransId();
        	model = quickGroupService.createQuickGroupFromExpense(transid);
        	model.setFromExpensePage(true);
        } else {
        	Long groupid = model.getGroupId();
        	model = quickGroupService.loadQuickGroupModelForId(groupid);
        }
    	uiModel.addAttribute("quickGroupModel", model);
    	return "quickgroup/edit";
    }


    @RequestMapping(value="/edit", method = RequestMethod.PUT, params = "cancel",produces = "text/html")
    public String cancelEdit(QuickGroupModel model,Model uiModel,HttpServletRequest httpServletRequest) {
		String redirect = "redirect:/quickgroup";
		if (model.getFromExpensePage()) {
			redirect = "redirect:/expense/list";
		}
		return redirect;
    }    
   
    @RequestMapping(value="/edit", method = RequestMethod.PUT, params = "save",produces = "text/html")
    public String saveChanges(QuickGroupModel model,Model uiModel,BindingResult bindingResult,HttpServletRequest httpServletRequest) {
    	// write any changes to expenses
    	model.copyEntriesIntoCategories();

    	modelValidator.validate(model, bindingResult);

		if (bindingResult.hasErrors()) {
			model.setEditIdx(999);
			uiModel.addAttribute("quickGroupModel", model);
			return "quickgroup/edit";
		}

    	// call to service
		quickGroupService.saveFromQuickGroupModel(model);

		String redirect = "redirect:/quickgroup";
		if (model.getFromExpensePage()) {
			redirect = "redirect:/expense/list";
		}
		return redirect;
    }    
   
    @RequestMapping(value="/preview/{id}", method=RequestMethod.GET)
    public @ResponseBody ModelAndView previewQuickGroup( @PathVariable("id") Long id ) {    
    	// load all categories
    	HashMap<Long,CategoryDao> catref = categoryService.getCategoriesAsMap();
    	// load all quickgroup details
    	QuickGroup quickgroup = quickGroupService.getQuickGroup(id);
    	List<QuickGroupDetail> details=quickGroupService.getDetailsForQuickGroup(quickgroup);
    	// fill in categories
    	for (QuickGroupDetail detail:details) {
    		if (detail.getCatid()!=null && catref.containsKey(detail.getCatid())) {
    			CategoryDao cat = catref.get(detail.getCatid());
    			detail.setCatdisplay(cat.getName());
    		}
    	}
    	// return with model of list, and view quickgroup/preview
    	ModelAndView mv = new ModelAndView("quickgroup/preview","quickgroupdetails",details);
    	// return model and view
    	return mv;
    }    
    
/*
   

    @RequestMapping(value="preview/{id}", method=RequestMethod.GET)
    public @ResponseBody ModelAndView previewRecipe( @PathVariable("id") Long id ) {    
    	// load recipe model
    	RecipeModel model = recipeService.loadFullRecipeModel(id);
    	ModelAndView mv = new ModelAndView("recipes/preview","recipeModel",model);
    	// return model and view
    	return mv;
    }




    @RequestMapping(value="/{id}", method = RequestMethod.PUT, params = "save",produces = "text/html")
    public String saveChanges(ExpenseEditModel model,Model uiModel,BindingResult bindingResult,HttpServletRequest httpServletRequest) {
    	// write any changes to expenses
    	model.copyEntriesIntoCategories();

    	modelValidator.validate(model, bindingResult);

		if (bindingResult.hasErrors()) {
			uiModel.addAttribute("expenseEditModel", model);
	    	return "expense/edit";
		}

    	// call to service
		transService.saveFromExpenseEdit(model);

		// return to list
		return "redirect:/expense/list";
    }
*/
 }
