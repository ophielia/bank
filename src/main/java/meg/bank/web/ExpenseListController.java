package meg.bank.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import meg.bank.bus.CategoryService;
import meg.bank.bus.ExpenseCriteria;
import meg.bank.bus.SearchService;
import meg.bank.bus.dao.CategoryDao;
import meg.bank.bus.dao.ExpenseDao;
import meg.bank.bus.imp.ImportManager;
import meg.bank.util.common.ColumnManagerService;
import meg.bank.util.common.db.ColumnValueDao;
import meg.bank.web.model.ExpenseListModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@RequestMapping("/expense")
@Controller
public class ExpenseListController {

	private final String sessioncriteria="sessioncriteria";
	
	@Autowired
	SearchService searchService;
	
    @Autowired
    ColumnManagerService cvManager;

    @Autowired
    CategoryService categoryService;
    
	@RequestMapping(produces = "text/html")
    public String showList(@ModelAttribute("expenseListModel") ExpenseListModel model,Model uiModel,HttpServletRequest request) {
		ExpenseCriteria criteria = model.getCriteria();
		HttpSession session = request.getSession();
		session.setAttribute(sessioncriteria,criteria);
		List<ExpenseDao> list = searchService.getExpenses(criteria);
		model.setExpenses(list);
		return "expense/list";
    }
	
	@RequestMapping(method = RequestMethod.PUT, produces = "text/html")
	public String searchExpenses(@ModelAttribute("expenseListModel") ExpenseListModel model,Model uiModel,HttpServletRequest request) {
		ExpenseCriteria criteria = model.getCriteria();
		HttpSession session = request.getSession();
		session.setAttribute(sessioncriteria,criteria);
		List<ExpenseDao> list = searchService.getExpenses(criteria);
		model.setExpenses(list);
		return "expense/list";
	}
	
	public String updateMultiCategories() {
		
		return null;
	}
	
	
	private ExpenseCriteria getDefaultCriteria() {
		ExpenseCriteria criteria = new ExpenseCriteria();
		criteria.setDateRangeByType(new Long(ExpenseCriteria.DateRange.CURRENT));
		criteria.setCategorizedType(new Long(ExpenseCriteria.CategorizedType.ALL));
		criteria.setSource(new Long(ImportManager.ImportClient.All));
		criteria.setTransactionType(new Long(ExpenseCriteria.TransactionType.DEBITS));
		return criteria;
	}

	@ModelAttribute("expenseListModel")
	public ExpenseListModel populateExpenseList(HttpServletRequest request) {
		HttpSession session = request.getSession();
		ExpenseCriteria criteria = (ExpenseCriteria) session.getAttribute(sessioncriteria);
		if (criteria==null) {
			criteria = getDefaultCriteria();
			session.setAttribute(sessioncriteria,criteria);
		}
		ExpenseListModel model = new ExpenseListModel(criteria);
		
		return model;
	}
	
	
	@ModelAttribute("categorylist")
	protected List<CategoryDao> referenceCategoryData(HttpServletRequest request, Object command,
			Errors errors) throws Exception {
		List<CategoryDao> list = categoryService.getCategories(false);
		
		
		// return model
		return list;
	}	
	
	@ModelAttribute("daterangelist")
	protected List<ColumnValueDao> referenceDataDateRange(HttpServletRequest request, Object command,
			Errors errors) throws Exception {
		List<ColumnValueDao> reference = cvManager.getColumnValueList(ExpenseCriteria.DateRangeLkup);

		// return model
		return reference;
	}	
	
	@ModelAttribute("sourcelist")
	protected List<ColumnValueDao> referenceDataSource(HttpServletRequest request, Object command,
			Errors errors) throws Exception {
		List<ColumnValueDao> reference = cvManager.getColumnValueList(ExpenseCriteria.ClientKeyLkup	);

		// return model
		return reference;
	}	

	@ModelAttribute("cattypelist")
	protected List<ColumnValueDao> referenceDataCatType(HttpServletRequest request, Object command,
			Errors errors) throws Exception {
		List<ColumnValueDao> reference = cvManager.getColumnValueList(ExpenseCriteria.CatTypeLkup	);

		// return model
		return reference;
	}	
	
	@ModelAttribute("transtypelist")
	protected List<ColumnValueDao> referenceDataTransType(HttpServletRequest request, Object command,
			Errors errors) throws Exception {
		List<ColumnValueDao> reference = cvManager.getColumnValueList(ExpenseCriteria.TransTypeLkup	);

		// return model
		return reference;
	}	
 }
