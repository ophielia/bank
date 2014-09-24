package meg.bank.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import meg.bank.bus.BankTransactionService;
import meg.bank.bus.CategoryService;
import meg.bank.bus.SearchService;
import meg.bank.bus.dao.CategoryDao;
import meg.bank.bus.dao.CategoryTADao;
import meg.bank.util.common.ColumnManagerService;
import meg.bank.web.model.ExpenseEditModel;
import meg.bank.web.validation.ExpenseListModelValidator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
	ExpenseListModelValidator modelValidator;    
    
	@ModelAttribute("categorylist")
	protected List<CategoryDao> referenceCategoryData(HttpServletRequest request, Object command,
			Errors errors) throws Exception {
		List<CategoryDao> list = categoryService.getCategories(false);
		
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
	
 }
