package meg.bank.web;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
import org.springframework.web.servlet.ModelAndView;

@RequestMapping("/reports")
@Controller
public class ReportController {

	@Autowired
	ReportService reportService;

	@Autowired
	ColumnManagerService cvManager;

	@Autowired
	CategoryService categoryService;

	@RequestMapping(produces = "text/html")
	public String showAllReports(
			@ModelAttribute("reportCriteria") ReportCriteria reportCriteria,
			Model uiModel, HttpServletRequest request) {

		return "reports/reportlist";
	}

	@RequestMapping(params = "reporttype", produces = "text/html")
	public String showInput(
			@ModelAttribute("reportCriteria") ReportCriteria reportCriteria,
			@RequestParam("reporttype") Long reporttype, Model uiModel,
			HttpServletRequest request) {
		if (reporttype != null) {
			reportCriteria.setReportType(reporttype);
		}
		String view = "reports/monthlytargetinput";
		if (reporttype.longValue() == ReportService.ReportType.MonthlyTarget) {
			view = "reports/monthlytargetinput";
		} else if (reporttype.longValue() == ReportService.ReportType.YearlyTargetStatus) {
			view = "reports/yearlytargetinput";
		} else if (reporttype.longValue() == ReportService.ReportType.FullMonth) {
			view = "reports/fullmonthinput";
		}
		return view;
	}

	@RequestMapping(method = RequestMethod.POST, produces = "text/html")
	public String showOutput(
			@ModelAttribute("reportCriteria") ReportCriteria reportCriteria,
			Model uiModel, HttpServletRequest request) {
		Long reporttype = reportCriteria.getReportType();
		Map<String, Object> results = processReport(reportCriteria,request);

		// set results in model
		uiModel.addAttribute("results", results);

		// determine view
		String view = "reports/monthlytargetoutput";
		if (reporttype.longValue() == ReportService.ReportType.MonthlyTarget) {
			view = "reports/monthlytargetoutput";
		} else if (reporttype.longValue() == ReportService.ReportType.YearlyTargetStatus) {
			view = "reports/yearlytargetoutput";
		} else if (reporttype.longValue() == ReportService.ReportType.FullMonth) {
			view = "reports/fullmonthoutput";
		}
		return view;

	}

	private Map<String, Object> processReport(ReportCriteria reportCriteria,HttpServletRequest request) {
		Long reporttype = reportCriteria.getReportType();
		StringBuffer contextpath = request.getRequestURL();
		int i = contextpath.indexOf(request.getServletPath());
		contextpath.setLength(i);
		reportCriteria.setContextPath(contextpath.toString());
		// validate....
		// monthlystatus - month set
		// yearlystatus - year set
		// full month - month set

		// call report service
		Map<String, Object> results = reportService.runReport(reportCriteria);
		return results;

	}

	@RequestMapping(method = RequestMethod.POST, params = "pdfreport", produces = "text/html")
	public ModelAndView showPDFOutput(
			@ModelAttribute("reportCriteria") ReportCriteria reportCriteria,
			Model uiModel, HttpServletRequest request) {
		Long reporttype = reportCriteria.getReportType();
		Map<String, Object> results = processReport(reportCriteria,request);

		// set results in model
		uiModel.addAttribute("results", results);

		// determine view
		String view = "reports/monthlytargetoutput";
		if (reporttype.longValue() == ReportService.ReportType.FullMonth) {
			view = "pdffullmonthreport";
		}
		return new ModelAndView(view, "model", uiModel);

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
	protected List<CategoryDao> referenceCategoryData(
			HttpServletRequest request, Object command, Errors errors)
			throws Exception {
		List<CategoryDao> list = categoryService.getCategories(true);
		// return model
		return list;
	}

	@ModelAttribute("dateranges")
	protected List<ColumnValueDao> referenceDateRanges(
			HttpServletRequest request, Object command, Errors errors)
			throws Exception {
		List<ColumnValueDao> list = cvManager
				.getColumnValueList(ExpenseCriteria.DateRangeLkup);
		// return model
		return list;
	}

	@ModelAttribute("breakoutlevel")
	protected List<ColumnValueDao> referenceBreakoutLevel(
			HttpServletRequest request, Object command, Errors errors)
			throws Exception {
		List<ColumnValueDao> list = cvManager
				.getColumnValueList(ReportCriteria.BreakoutLookup);
		// return model
		return list;
	}

	@ModelAttribute("comptype")
	protected List<ColumnValueDao> referenceCompareType(
			HttpServletRequest request, Object command, Errors errors)
			throws Exception {
		List<ColumnValueDao> list = cvManager
				.getColumnValueList(ReportCriteria.CompareTypeLkup);
		// return model
		return list;
	}

	@ModelAttribute("monthselect")
	protected List<String> referenceMonthList(HttpServletRequest request,
			Object command, Errors errors) throws Exception {
		List<String> list = reportService.getAllMonthsAsList();
		// return model
		return list;
	}

	@ModelAttribute("yearselect")
	protected List<String> referenceYearList(HttpServletRequest request,
			Object command, Errors errors) throws Exception {
		List<String> list = reportService.getAllYearsAsList();
		// return model
		return list;
	}

}
