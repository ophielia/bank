package meg.bank.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import meg.bank.bus.BankTransactionService;
import meg.bank.bus.CategoryService;
import meg.bank.bus.ExpenseCriteria;
import meg.bank.bus.QuickGroupService;
import meg.bank.bus.SearchService;
import meg.bank.bus.RuleAssignment;
import meg.bank.bus.dao.CategoryDao;
import meg.bank.bus.dao.ExpenseDao;
import meg.bank.bus.dao.QuickGroup;
import meg.bank.bus.imp.ImportManager;
import meg.bank.util.common.ColumnManagerService;
import meg.bank.util.common.db.ColumnValueDao;
import meg.bank.web.model.AssignmentListModel;
import meg.bank.web.model.ExpenseListModel;
import meg.bank.web.validation.ExpenseListModelValidator;
import meg.bank.web.validation.TargetModelValidator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@RequestMapping("/ruleassignment")
@Controller
public class AssignRulesController {

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
	
	@Autowired
	QuickGroupService quickGroupService;
    
	
	@ModelAttribute("assignmentListModel")
	public AssignmentListModel populateAssignmentListModel(HttpServletRequest request) {
		// service call which assigns uncategorized expenses by rules
		List<RuleAssignment> assignmentlist=transService.getRuleAssignments();
		AssignmentListModel model = new AssignmentListModel(assignmentlist);
		return model;
	}	
	
	
	@RequestMapping(produces = "text/html")
    public String showList(@ModelAttribute("assignmentListModel") AssignmentListModel model,Model uiModel,HttpServletRequest request) {
		return "rule/assignbyrule";
    }
	
	@RequestMapping(method = RequestMethod.PUT, produces = "text/html")
	public String makeAssignments(@ModelAttribute("assignmentListModel") AssignmentListModel model,Model uiModel,HttpServletRequest request) {
		List<RuleAssignment> toupdate = model.getCheckedRuleAssignments();
		// update by rule assignment
		transService.updateExpenseByRuleAssignments(toupdate);
		return "redirect:expense/list";
	}

}
