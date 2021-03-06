package meg.bank.bus.report;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import meg.bank.bus.CategoryLevel;
import meg.bank.bus.CategoryService;
import meg.bank.bus.ExpenseCriteria;
import meg.bank.bus.SearchService;
import meg.bank.bus.TargetService;
import meg.bank.bus.dao.ExpenseDao;
import meg.bank.bus.report.elements.ChartData;
import meg.bank.bus.report.elements.ChartRow;
import meg.bank.bus.report.elements.ReportElement;
import meg.bank.bus.report.elements.ReportLabel;




public class FullMonthReportData extends BankReportData {

	public FullMonthReportData(ReportCriteria reportCriteria,
			SearchService searchService, CategoryService categoryService,
			TargetService targetService) {
		super(reportCriteria, searchService, categoryService, targetService);
	}



	@Override
	public void crunchNumbers() {
		// fill up this ReportData with, uh, data....
		
		
		// fill criteria object
				ExpenseCriteria criteria = new ExpenseCriteria();
				criteria.setTransactionType(new Long(
						ExpenseCriteria.TransactionType.DEBITS));
				criteria.setCompareType(getReportCriteria().getComparetype());
				String month = getReportCriteria().getMonth();
				Date startdate = null;
				Date enddate = null;
				int daycount = 0;
				// parse into date
				SimpleDateFormat dateformat = new SimpleDateFormat("MM-yyyy");
				Date start;
				try {
					start = dateformat.parse(month);
					Calendar cal = Calendar.getInstance();
					// get first of month, first of next month
					cal.setTime(start);
					cal.set(Calendar.DAY_OF_MONTH, 1);
					startdate = cal.getTime();
					cal.add(Calendar.MONTH, 1);
					enddate = cal.getTime();
					// set in criteria
					criteria.setDateStart(startdate);
					criteria.setDateEnd(enddate);
					daycount = getDayCount(criteria);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				criteria.setExcludeNonExpense(getReportCriteria().getExcludeNonExpense());

				// run summary report
				getReportCriteria().setBreakoutLevel(new Long(1));
				ReportElement summary = crunchNumbersSummary(criteria, false);
				
				
				// run year to date report
				criteria.clearCategoryLevelList();
				criteria.clearCategoryLists();
				criteria.setCategory(null);
				ReportElement yeartodate = crunchNumbersMonthlyComp(criteria,true, "MM-yyyy");
				
				// pull all expenses
				List<ExpenseDao> allexpenses = searchService.getExpenses(criteria);
				sortAndCategorizeExpenses(allexpenses);
				ChartData data = new ChartData();
				ChartRow headers = new ChartRow();
				headers.addColumn(BankReportData.DispLabel.Date);
				headers.addColumn(BankReportData.DispLabel.Category);
				headers.addColumn(BankReportData.DispLabel.Subcategory);
				headers.addColumn(BankReportData.DispLabel.Detail);
				headers.addColumn(BankReportData.DispLabel.Amount);
				data.setHeaders(headers);

				for (ExpenseDao exp : allexpenses) {
					ChartRow row = new ChartRow();
					row.addColumn(daydateformat.format(exp.getTransdate()));
					row.addColumn(exp.getDispCat());
					row.addColumn(exp.getCatName());
					row.addColumn(exp.getDetail());
					if (exp.getHascat()) {
						row.addColumn(nf.format(exp.getCatamount()));
					} else {
						row.addColumn(nf.format(exp.getTranstotal()));
					}
					data.addRow(row);
				}
				
				// chartdata now created, make into report element, and add to reportdata
				ReportElement re = new ReportElement();
				re.setChart(data);
				re.setTag("allexpenses");
				addElement(re);
						
				// run target report
				criteria.clearCategoryLevelList();
				criteria.clearCategoryLists();
				criteria.setCategory(null);
				ReportElement targets = crunchNumbersTargets(criteria, month);
				targets.setTag("targets");
				addElement(targets);
				
				// run category breakouts for all main categories
				Hashtable<Long, String> catlkup = new Hashtable<Long, String>();
				criteria.setCategorizedType(new Long(
						ExpenseCriteria.CategorizedType.ALL));
				ReportElement allcategory = new ReportElement();
				List<CategoryLevel> categories = categoryService
						.getCategoriesUpToLevel(1);
				if (categories != null) {
					for (CategoryLevel catlvl : categories) {
						ReportElement catre = crunchNumbersCategory(criteria, catlvl, true);
						if (catre != null) {
							catre.setDisplay(catlvl.getCategory().getName());
							allcategory.addMember(catre);
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
				// add summary element
				allcategory.setTag("allcategories");
				addElement(allcategory);
				
				
				// add summary element
				summary.setTag("summary");
				addElement(summary);
				
				// add year to date element
				yeartodate.setTag("yeartodate");
				addElement(yeartodate);				
				
				// Add labels title, rundate
				ReportLabel title = new ReportLabel("lbltitle",BankReportData.DispLabel.FullMonthTitle + " - " + month);
				String rangestr  = daydateformat.format(startdate) + " - " +  daydateformat.format(enddate) + "("+daycount+" " + BankReportData.DispLabel.Days +")";
				ReportLabel range = new ReportLabel("lblrange",rangestr);
				ReportLabel allexp = new ReportLabel("lblallexpenses",BankReportData.DispLabel.AllExpenses);
				ReportLabel targetlbl = new ReportLabel("lbltarget",BankReportData.DispLabel.TargetStatus);
				ReportLabel summarylbl = new ReportLabel("lblsummary",BankReportData.DispLabel.SummaryByCategory);
				ReportLabel yeartodatelbl = new ReportLabel("lblyeartodate",BankReportData.DispLabel.YearToDate);
				
				
				addLabel(title);
				addLabel(range);
				addLabel(allexp);
				addLabel(targetlbl);
				addLabel(summarylbl);
				addLabel(yeartodatelbl);
				
				
	}

	@Override
	public String getXslTransformFilename() {
		return "fullmonth.xsl";
	}

	@Override
	public String getJspViewname() {
		return "fullmonthoutput";
	}




}