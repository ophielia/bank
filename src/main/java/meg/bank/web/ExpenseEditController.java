package meg.bank.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import meg.bank.bus.BankTransactionService;
import meg.bank.bus.CategoryService;
import meg.bank.bus.QuickGroupService;
import meg.bank.bus.SearchService;
import meg.bank.bus.dao.CategoryDao;
import meg.bank.bus.dao.CategoryTADao;
import meg.bank.bus.dao.QuickGroup;
import meg.bank.util.common.ColumnManagerService;
import meg.bank.web.model.ExpenseEditModel;
import meg.bank.web.validation.ExpenseEditModelValidator;

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

@SessionAttributes("expenseEditModel")
@RequestMapping("/expense/edit")
@Controller
public class ExpenseEditController {

	
	@Autowired
	SearchService searchService;
	
	@Autowired
	BankTransactionService transService;
		
    @Autowired
    ColumnManagerService cvManager;

    @Autowired
    CategoryService categoryService;
    
    @Autowired
    QuickGroupService quickGroupService;    
    
	@Autowired
	ExpenseEditModelValidator modelValidator;    
    
	@ModelAttribute("categorylist")
	protected List<CategoryDao> referenceCategoryData(HttpServletRequest request, Object command,
			Errors errors) throws Exception {
		List<CategoryDao> list = categoryService.getCategories(false);
		
		// return model
		return list;
	}	
	
	@ModelAttribute("quickgrouplist")
	protected List<QuickGroup> referenceQuickGroupData(HttpServletRequest request, Object command,
			Errors errors) throws Exception {
		List<QuickGroup> list = quickGroupService.getAllQuickGroups();
		
		// return model
		return list;
	}		

    @RequestMapping(value="/{id}", method = RequestMethod.GET, produces = "text/html")
    public String showEditForm(@PathVariable("id") Long id,  Model uiModel,HttpServletRequest httpServletRequest) {
        ExpenseEditModel model = transService.loadExpenseEditModel(id);
    	uiModel.addAttribute("expenseEditModel", model);
    	
    	return "expense/edit";
    }
    
    @RequestMapping(value="/{id}", method = RequestMethod.PUT, params = "addcategory",produces = "text/html")
    public String addCategoryExp(ExpenseEditModel model,Model uiModel,HttpServletRequest httpServletRequest) {
    	// write any changes to expenses
    	model.copyEntriesIntoCategories();
    	// add new CategoryTADao to list in model
    	List<CategoryTADao> catexps = model.getCategoryExpenses();
    	Integer editidx = catexps.size();
    	catexps.add(new CategoryTADao());
    	model.setCategoryExpenses(catexps);
    	// set editindex to size of list - 1
    	model.setEditIdx(editidx);
    	uiModel.addAttribute("expenseEditModel", model);
    	return "expense/edit";
    }    
    
    @RequestMapping(value="/{id}", method = RequestMethod.PUT, params = "removecategory",produces = "text/html")
    public String removeCategoryExp(ExpenseEditModel model,Model uiModel,HttpServletRequest httpServletRequest) {
    	// write any changes to expenses
    	model.copyEntriesIntoCategories();
    	// get editIndex
    	int editidx = model.getEditIdx();
    	List<CategoryTADao> catexps = model.getCategoryExpenses();
    	catexps.remove(editidx);
    	model.setCategoryExpenses(catexps);
    	model.setEditIdx(999);
    	uiModel.addAttribute("expenseEditModel", model);
    	return "expense/edit";
    }      
    
    @RequestMapping(value="/{id}", method = RequestMethod.PUT, params = "resetchanges",produces = "text/html")
    public String resetAllChanges(ExpenseEditModel model,Model uiModel,HttpServletRequest httpServletRequest) {
        Long transid = model.getTransid();
    	model = transService.loadExpenseEditModel(transid);
    	uiModel.addAttribute("expenseEditModel", model);
    	
    	return "expense/edit";
    }    
    
    
    @RequestMapping(value="/{id}", method = RequestMethod.PUT, params = "cancel",produces = "text/html")
    public String cancelEdit(ExpenseEditModel model,Model uiModel,HttpServletRequest httpServletRequest) {
        
    	return "redirect:/expense/list";
    }  
    
    @RequestMapping(value="/{id}", method = RequestMethod.PUT, params = "distributeall",produces = "text/html")
    public String distributeAll(ExpenseEditModel model,Model uiModel,HttpServletRequest httpServletRequest) {
    	// write any changes to expenses
    	model.copyEntriesIntoCategories();
    	// get Transtotal (total to be distributed)
    	Double transtotal = model.getAmount();
    	// get categoryexpenses from model
    	List<CategoryTADao> categoryexpenses = model.getCategoryExpenses();
    	// get count of current categories in model
    	int count = categoryexpenses!=null?categoryexpenses.size():0;
    	// get new amount array
    	Double[] distributed = transService.distributeAmounts(transtotal, count);
    	// set new amounts in categoryexpenses
    	model.setAmountsInCategorized(distributed);
    	// set categoryexpenses in model
    	model.setCategoryExpenses(categoryexpenses);
    	
    	// unset edit
    	model.setEditIdx(999);
    	
    	// set model in uimodel
    	uiModel.addAttribute("expenseEditModel", model);
    	
    	return "expense/edit";
    }      
   
    @RequestMapping(value="/{id}", method = RequestMethod.PUT, params = "distributeremainder",produces = "text/html")
    public String distributeRemainder(ExpenseEditModel model,Model uiModel,HttpServletRequest httpServletRequest) {
    	// write any changes to expenses
    	model.copyEntriesIntoCategories();
    	// get Remainder (amount to be distributed)
    	Double categorizedamt = model.getCategorizedTotal();
    	Double transtotal = model.getAmount();
    	Double todistribute = transtotal.doubleValue() - categorizedamt.doubleValue();
    	// get count of current categories in model
    	int count = model.getEmptyCount();
    	// get new amount array
    	Double[] distributed = transService.distributeAmounts(todistribute, count);
    	// set new amounts in categoryexpenses
    	model.setAmountsInEmpties(distributed);
    	
    	// unset edit
    	model.setEditIdx(999);
    	
    	// set model in uimodel
    	uiModel.addAttribute("expenseEditModel", model);
    	
    	return "expense/edit";
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
		
		// check whether this should be assigned as a quickgroup
		if (model.getCreatequickgroup()) {
			// get bankid 
			Long bankid = model.getTransid();
			// construct redirect path
			String redirect="redirect:/quickgroup/create?transid=" + bankid;
			// redirect
			return redirect;
		}
		// return to list
		return "redirect:/expense/list";
    }      
	
 }
