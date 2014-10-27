package meg.bank.bus.report;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import meg.bank.bus.CategoryLevel;
import meg.bank.bus.CategoryService;
import meg.bank.bus.ExpenseCriteria;
import meg.bank.bus.SearchService;
import meg.bank.bus.TargetService;
import meg.bank.bus.dao.ExpenseDao;
import meg.bank.bus.report.utils.ChartData;

public class FullMonthReport extends AbstractReport {


	public FullMonthReport(ReportCriteria reportCriteria,
			SearchService searchService, CategoryService categoryService,
			TargetService targetService) {
		super(reportCriteria, searchService, categoryService, targetService);
	}





	public String getReportname() {
		return "Full Month Report";
	}





	public Map<String, Object> crunchNumbers() {
		// fill criteria object
		ExpenseCriteria criteria = new ExpenseCriteria();
		criteria.setTransactionType(new Long(
				ExpenseCriteria.TransactionType.DEBITS));
		criteria.setCompareType(getReportCriteria().getComparetype());
		String month = getReportCriteria().getMonth();
		// parse into date
		SimpleDateFormat dateformat = new SimpleDateFormat("MM-yyyy");
		Date start;
		try {
			start = dateformat.parse(month);
			Calendar cal = Calendar.getInstance();
			// get first of month, first of next month
			cal.setTime(start);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			Date startdate = cal.getTime();
			cal.add(Calendar.MONTH, 1);
			Date enddate = cal.getTime();
			// set in criteria
			criteria.setDateStart(startdate);
			criteria.setDateEnd(enddate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		criteria.setExcludeNonExpense(getReportCriteria().getExcludeNonExpense());
		int daycount = getDayCount(criteria);
		Hashtable<Long, String> catlkup = new Hashtable<Long, String>();

		// run summary report
		getReportCriteria().setBreakoutLevel(new Long(1));
		ReportElements summary = crunchNumbersSummary(criteria, false);

		// run category breakouts for all main categories
		criteria.setCategorizedType(new Long(
				ExpenseCriteria.CategorizedType.ALL));
		List<ReportElements> allcategory = new ArrayList<ReportElements>();
		List<CategoryLevel> categories = categoryService
				.getCategoriesUpToLevel(1);
		if (categories != null) {
			for (CategoryLevel catlvl : categories) {
				ReportElements catre = crunchNumbersCategory(criteria, catlvl, true);
				if (catre != null) {
					catre.setName(catlvl.getCategory().getName());
					allcategory.add(catre);
				}
				// construct category lookup hash (for use later)
				List<CategoryLevel> subcats = categoryService
						.getAllSubcategories(catlvl.getCategory());
				String catname = catlvl.getCategory().getName();
				catlkup.put(catlvl.getCategory().getId(), catname);
				if (subcats != null) {
					for (CategoryLevel sublvl : subcats) {
						catlkup.put(sublvl.getCategory().getId(), catname);
					}
				}
			}
		}

		// run year to date report
		criteria.clearCategoryLevelList();
		criteria.clearCategoryLists();
		criteria.setCategory(null);
		ReportElements yeartodate = crunchNumbersMonthlyComp(criteria,true, "MM-yyyy");

		// pull all expenses
		List<ExpenseDao> allexpenses = searchService.getExpenses(criteria);
		ChartData expensedata = convertExpensesToChartData(allexpenses);
		
		// run target report
		criteria.clearCategoryLevelList();
		criteria.clearCategoryLists();
		criteria.setCategory(null);
		ReportElements targets = crunchNumbersTargets(criteria, month);

		// put together model
		HashMap<String, Object> model = new HashMap<String, Object>();
		// list of expenses
		model.put("title", "Full Month Report - " + month);
		model.put("allexpenses", expensedata);
		// list of category summary objects
		model.put("summary", summary.getChartData());
		model.put("summaryimg", summary.getUrl());
		// add all category breakout info
		model.put("categories", allcategory);
		// add year to date info
		model.put("yeartodate", yeartodate);
		// add target info
		model.put("targets", targets);
		// add meta info
		model.put("StartDate", criteria.getDateStartAsString());
		model.put("EndDate", criteria.getDateEndAsString());
		model.put("BreakoutLevel", getReportCriteria().getBreakoutLevel());
		model.put("DayCount", new Integer(daycount));

		return model;
	}
	




	public String getResultView(String outputtype) {
		if (outputtype!=null && outputtype.toLowerCase().equals("pdf")) {
			return "pdffullmonthreport";
		}
		return "fullmonthreport";
	}

	public Hashtable<Integer, Boolean> getRequiredParameters() {
		Hashtable<Integer, Boolean> req = new Hashtable<Integer, Boolean>();
/*
 * 
 * 		req.put(new Integer(Report.Parameters.excludenonexp), new Boolean(
						true));
		req.put(new Integer(Report.Parameters.daterange), new Boolean(false));
		req.put(new Integer(Report.Parameters.comparetype), new Boolean(true));
		req.put(new Integer(Report.Parameters.breakoutlvl), new Boolean(false));
		req.put(new Integer(Report.Parameters.category), new Boolean(false));
		req.put(new Integer(Report.Parameters.monthlist), new Boolean(true));
		req.put(new Integer(Report.Parameters.yearlist), new Boolean(false));
 */
		return req;
	}

}
