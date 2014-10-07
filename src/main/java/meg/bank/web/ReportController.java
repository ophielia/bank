package meg.bank.web;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import meg.bank.bus.CategoryService;
import meg.bank.bus.ExpenseCriteria;
import meg.bank.bus.dao.CategoryDao;
import meg.bank.bus.report.ReportCriteria;
import meg.bank.bus.report.ReportService;
import meg.bank.util.common.ColumnManagerService;
import meg.bank.util.common.db.ColumnValueDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@RequestMapping("/expense/list")
@Controller
public class ReportController {


	@Autowired
	ReportService reportService;	
	
	
    @Autowired
    ColumnManagerService cvManager;

    @Autowired
    CategoryService categoryService;
    
  
	@RequestMapping(params="reporttype" ,produces = "text/html")
    public String showInput(@ModelAttribute("reportCriteria") ReportCriteria reportCriteria,@RequestParam("reporttype") Long reporttype,Model uiModel,HttpServletRequest request) {
		if (reporttype!=null) {
			reportCriteria.setReportType(reporttype);
		}
		String view = "reports/monthlytargetinput";
		if (reporttype.longValue()==ReportService.ReportType.MonthlyTarget) {
			view = "reports/monthlytargetinput";	
		}
		return view;
    }
	
	@RequestMapping(method = RequestMethod.POST, produces = "text/html")
	public String showOutput(@ModelAttribute("reportCriteria") ReportCriteria reportCriteria,Model uiModel,HttpServletRequest request) {
		Long reporttype = reportCriteria.getReportType();
		// validate....
		
		// call report service
		Map<String,Object> results = reportService.runReport(reporttype,reportCriteria);
		
		// set results in model
		uiModel.addAttribute("results",results);
		
		// determine view
		String view = "reports/monthlytargetoutput";
		if (reporttype.longValue()==ReportService.ReportType.MonthlyTarget) {
			view = "reports/monthlytargetoutput";	
		}
		return view;		
		
	}
	

	

	private ReportCriteria getDefaultCriteria() {
		ReportCriteria criteria = new ReportCriteria();
		criteria.setDaterangetype(new Long(ExpenseCriteria.DateRange.CURRENT));
		criteria.setBreakoutLevel(new Long(2));
		criteria.setExcludeNonExpense(true);
		
		return criteria;
	}

	@ModelAttribute("reportCriteria")
	public ReportCriteria getReportCriteria(HttpServletRequest request) {
		return getDefaultCriteria();
	}
	
	
	@ModelAttribute("categories")
	protected List<CategoryDao> referenceCategoryData(HttpServletRequest request, Object command,
			Errors errors) throws Exception {
		List<CategoryDao> list = categoryService.getCategories(true);
		// return model
		return list;
	}	
	
	@ModelAttribute("dateranges")
	protected List<ColumnValueDao> referenceDateRanges(HttpServletRequest request, Object command,
			Errors errors) throws Exception {
		List<ColumnValueDao> list = cvManager.getColumnValueList(ExpenseCriteria.DateRangeLkup);
		// return model
		return list;
	}	
	
	@ModelAttribute("breakoutlevel")
	protected List<ColumnValueDao> referenceBreakoutLevel(HttpServletRequest request, Object command,
			Errors errors) throws Exception {
		List<ColumnValueDao> list = cvManager.getColumnValueList(ReportCriteria.BreakoutLookup);
		// return model
		return list;
	}
	
	@ModelAttribute("comptype")
	protected List<ColumnValueDao> referenceCompareType(HttpServletRequest request, Object command,
			Errors errors) throws Exception {
		List<ColumnValueDao> list = cvManager.getColumnValueList(ReportCriteria.CompareTypeLkup);
		// return model
		return list;
	}
	
	@ModelAttribute("monthselect")
	protected List<String> referenceMonthList(HttpServletRequest request, Object command,
			Errors errors) throws Exception {
		List<String> list = reportService.getAllMonthsAsList();
		// return model
		return list;
	}		
	
	@ModelAttribute("yearselect")
	protected List<String> referenceYearList(HttpServletRequest request, Object command,
			Errors errors) throws Exception {
		List<String> list = reportService.getAllYearsAsList();
		// return model
		return list;
	}		


 }
