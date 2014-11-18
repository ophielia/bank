package meg.bank.bus.report;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import meg.bank.bus.CategoryLevel;
import meg.bank.bus.CategoryService;
import meg.bank.bus.ExpenseCriteria;
import meg.bank.bus.SearchService;
import meg.bank.bus.TargetService;
import meg.bank.bus.dao.CategoryDao;
import meg.bank.bus.dao.TargetDetailDao;
import meg.bank.bus.dao.TargetGroupDao;
import meg.bank.bus.report.utils.ChartData;
import meg.bank.bus.report.utils.ChartRow;
import meg.bank.bus.report.utils.TargetProgressDisp;
import meg.bank.bus.report.utils.UtilityComparator;

import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleEdge;

public class YearlyReport extends AbstractReport {
	private static NumberFormat nf = new DecimalFormat("######.00",
			new DecimalFormatSymbols(Locale.US));

	private static SimpleDateFormat daydateformat = new SimpleDateFormat(
			"MM-dd-yyyy");

	SimpleDateFormat yearformat = new SimpleDateFormat("yyyy", Locale.US);

	SimpleDateFormat mthyearformat = new SimpleDateFormat("MM-yyyy", Locale.US);

	public YearlyReport(ReportCriteria reportCriteria,
			SearchService searchService, CategoryService categoryService,
			TargetService targetService) {
		super(reportCriteria, searchService, categoryService, targetService);
	}
	
	
	public String getReportname() {
		return "New Yearly Report";
	}

	public Map<String, Object> crunchNumbers() {
		// fill criteria object
		ExpenseCriteria criteria = new ExpenseCriteria();
		criteria.setTransactionType(new Long(
				ExpenseCriteria.TransactionType.DEBITS));
		String year = getReportCriteria().getYear();
		String title = "Yearly Report - " + year;
		getReportCriteria().setBreakoutLevel(1L);
		// parse into date
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy");
		Date start;
		try {
			start = dateformat.parse(year);
			Calendar cal = Calendar.getInstance();
			Date currentdate = cal.getTime();

			// get first of month, first of next month
			cal.setTime(start);
			cal.set(Calendar.MONTH, Calendar.JANUARY);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			Date startdate = cal.getTime();
			cal.add(Calendar.YEAR, 1);
			cal.add(Calendar.DAY_OF_MONTH, -1);
			Date enddate = cal.getTime();

			if (currentdate.before(enddate)) {
				// get last complete month
				cal.setTime(currentdate);
				cal.set(Calendar.DAY_OF_MONTH, 1);
				cal.add(Calendar.DAY_OF_MONTH, -1);
				enddate = cal.getTime();
			}
			// set in criteria
			criteria.setDateStart(startdate);
			criteria.setDateEnd(enddate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		criteria.setExcludeNonExpense(getReportCriteria().getExcludeNonExpense());

		// run summary report
		ReportElements summary = crunchNumbersSummary(criteria, true);

		// run Target Summary report
		ReportElements targetsum = crunchTargetSummary(criteria);

		// run Target Detail report
		ReportElements targetdet = crunchTargetDetail(criteria);

		// run month comparison
		criteria.clearCategoryLevelList();
		criteria.clearCategoryLists();
		criteria.setCategory(null);
		criteria.setCompareType(new Long(ReportCriteria.CompareType.CALYEAR));
		ReportElements monthcompare = crunchNumbersMonthlyComp(criteria, true,
				"MMM");

		// run year comparison
		criteria.clearCategoryLevelList();
		criteria.clearCategoryLists();
		criteria.setCategory(null);
		criteria.setCompareType(new Long(ReportCriteria.CompareType.CALYEAR));
		ReportElements yearcompare = crunchNumbersYearlyComp(criteria, true);

		// run all category info - all years
		List<ReportElements> categoryallyears = crunchNumbersYearlyCategories(criteria);

		// run all category info
		List<ReportElements> categoryyear = crunchNumbersAllCategories(criteria);

		// run detailed information for other
		ReportElements detailedother = crunchNumbersOtherDetail(criteria);

		HashMap<String, Object> model = new HashMap<String, Object>();

		// add title
		model.put("title", title);
		// list of category summary objects
		model.put("summary", summary.getChartData());
		model.put("summaryimg", summary.getUrl());
		// target summary report
		model.put("targetsumm", targetsum.getChartData());
		model.put("targetsummaryimg", targetsum.getUrl());
		// target detail report
		model.put("targetdetsummaryimg", targetdet.getUrl());
		// month comparison
		model.put("monthcomparison", monthcompare.getChartData());
		model.put("monthcompareimg", monthcompare.getUrl());
		// year comparison
		model.put("yearcomparison", yearcompare.getChartData());
		model.put("yearcompareimg", yearcompare.getUrl());
		// this year category detail
		model.put("categoryyear", categoryyear);
		// this year category detail
		model.put("categoryallyears", categoryallyears);
		// this year other detail
		model.put("detailedother", detailedother);
		
		return model;
	}

	private ReportElements crunchNumbersOtherDetail(ExpenseCriteria criteria) {
		// initialize List of ReportElements
		ReportElements report = null;

		// make copy of criteria
		ExpenseCriteria cpycriteria = criteria.clone();

		// initialize MonthTag list and lkup
		List<String> monthtags = getMonthTagList(cpycriteria, "MM-yyyy");
		Hashtable<String, Integer> monthtaglkup = new Hashtable<String, Integer>();

		// create headers row, find values for monthcount, and set totalscolumn
		// and avgpermonth column
		ChartRow headers = new ChartRow();
		headers.addColumn("Sub-Category");
		for (String month : monthtags) {
			Integer key = new Integer(headers.getColumnCount());
			monthtaglkup.put(month, key);
			headers.addColumn(month);
		}
		int monthcount = monthtags.size();
		int totalscolumn = headers.getColumnCount();
		int avgpermonthcol = totalscolumn + 1;
		headers.addColumn("Total");
		headers.addColumn("Avg Per Month");

		// run numbers for Other category
		CategoryDao othercat = categoryService.getCategoryByName(CategoryService.othercategoryname);
		CategoryLevel catlvl = categoryService.getAsCategoryLevel(
				othercat.getId());

		report = processDetailedMonthlySubCats(criteria, headers, monthtags,
				monthtaglkup, catlvl, totalscolumn, monthcount, avgpermonthcol);

		return report;
	}

	private List<ReportElements> crunchNumbersAllCategories(
			ExpenseCriteria criteria) {
		// initialize List of ReportElements
		List<ReportElements> reports = new ArrayList<ReportElements>();

		// make copy of criteria
		ExpenseCriteria cpycriteria = criteria.clone();

		// initialize MonthTag list and lkup
		List<String> monthtags = getMonthTagList(cpycriteria, "MM-yyyy");
		Hashtable<String, Integer> monthtaglkup = new Hashtable<String, Integer>();

		// create headers row, find values for monthcount, and set totalscolumn
		// and avgpermonth column
		ChartRow headers = new ChartRow();
		headers.addColumn("Sub-Category");
		for (String month : monthtags) {
			Integer key = new Integer(headers.getColumnCount());
			monthtaglkup.put(month, key);
			headers.addColumn(month);
		}
		int monthcount = monthtags.size();
		int totalscolumn = headers.getColumnCount();
		int avgpermonthcol = totalscolumn + 1;
		headers.addColumn("Total");
		headers.addColumn("Avg Per Month");

		// get all top-level categories for year
		List<CategoryLevel> categories = categoryService.getCategoriesUpToLevel(1);

		// loop through categories, creating ReportElement for each category,
		// and adding to list
		if (categories != null) {
			for (Iterator iter = categories.iterator(); iter.hasNext();) {
				CategoryLevel catlvl = (CategoryLevel) iter.next();
				CategoryDao category = catlvl.getCategory();
				if (category.getNonexpense() != null
						&& category.getNonexpense().booleanValue()) {
					continue;
				}

				ReportElements re = null;
				if (category.getId().longValue() == 57) {
					re = processMonthlySubCats(cpycriteria, headers, monthtags,
							monthtaglkup, catlvl, totalscolumn, monthcount,
							avgpermonthcol);
				} else {
					re = processDetailedMonthlySubCats(cpycriteria, headers,
							monthtags, monthtaglkup, catlvl, totalscolumn,
							monthcount, avgpermonthcol);
				}

				if (re != null) {
					// add ReportElements object to List
					reports.add(re);
				}

			}
		}
		// end category loop

		return reports;
	}

	private ReportElements processDetailedMonthlySubCats(
			ExpenseCriteria cpycriteria, ChartRow headers,
			List<String> monthtags, Hashtable<String, Integer> monthtaglkup,
			CategoryLevel catlvl, int totalscolumn, int monthcount,
			int avgpermonthcol) {
		CategoryDao category = catlvl.getCategory();
		// initialize working calendar
		Calendar cal = Calendar.getInstance();

		if (category.getNonexpense() != null
				&& category.getNonexpense().booleanValue()) {
			return null;
		}
		// initialize ChartData - with headers
		ChartData chartdata = new ChartData();
		chartdata.setHeaders(headers);

		// initialize bargraph results
		Hashtable<String, List<CategorySummaryDisp>> bargraphres = new Hashtable<String, List<CategorySummaryDisp>>();

		// initialize piegraph results
		List<CategorySummaryDisp> piegraphres = new ArrayList<CategorySummaryDisp>();

		// get all subcategories for category, adding the category
		// itself
		List<CategoryLevel> subcats = null;
		subcats = categoryService.getAllSubcategories(category);
		subcats.add(catlvl);
		/*
		 * if (category.getId()==57) { subcats =
		 * categoryService.getSubcategoriesToLevel (category.getId(), 2);
		 * subcats.add(catlvl); } else { //MM just for testing subcats = new
		 * ArrayList<CategoryLevel>(); }
		 */

		// loop through subcategories, retrieving monthly info for each,
		// and
		// summing monthly info into CategoryDisp object for summary
		// graph
		for (CategoryLevel subcat : subcats) {

			// initialize summary CategoryDisp (bargraph)
			CategorySummaryDisp cattotal = new CategorySummaryDisp();

			// set category in criteria
			cpycriteria.clearCategoryLevelList();
			cpycriteria.clearCategoryLists();
			cpycriteria.setCategory(subcat.getCategory().getId());

			// initialize chartrow
			ChartRow row = new ChartRow();
			String catname = subcat.getCategory().getName();
			row.addColumn(catname);

			// loop through all months, retrieving expense results for
			// each
			for (String month : monthtags) {
				// set dates in criteria
				Date rundate = null;
				Date startdate = null;
				Date enddate = null;
				try {
					rundate = mthyearformat.parse(month);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				cal.setTime(rundate);
				cal.set(Calendar.DAY_OF_MONTH, 1);
				startdate = cal.getTime();
				cal.add(Calendar.MONTH, 1);
				cal.set(Calendar.DAY_OF_MONTH, 1);
				enddate = cal.getTime();

				// set dates in criteria
				cpycriteria.setDateStart(startdate);
				cpycriteria.setDateEnd(enddate);
				// retrieve monthly expense total
				/*MM orig - List<CategorySummaryDisp> rawresults = searchService
				.getExpenseTotal(cpycriteria,
						catlvl.getCategory().getName());*/
				List<CategorySummaryDisp> rawresults = searchService
						.getExpenseTotal(cpycriteria);

				if (rawresults != null && rawresults.size() > 0) {
					CategorySummaryDisp raw = rawresults.get(0);
					raw.setCatName(catname);
					// add CategoryDisp to bargraph chart
					List<CategorySummaryDisp> fromhash = bargraphres.get(month);
					if (fromhash == null) {
						fromhash = new ArrayList<CategorySummaryDisp>();
					}
					fromhash.add(raw);
					bargraphres.put(month, fromhash);
					// add value to month column
					Integer key = monthtaglkup.get(month);
					String amount = nf.format(raw.getSum() * -1);
					row.addColumn(amount, key.intValue());
					// add to summary CategoryDisp (pie chart)
					cattotal.addExpenseAmt(raw.getSum() * -1);
				}
			}
			// end month loop

			// add totals column to end of row
			double rowsum = cattotal.getSum();
			row.addColumn(nf.format(rowsum), totalscolumn);

			// add avg column to end of row
			double rowavg = rowsum / 12; // MM make current year smart
			row.addColumn(nf.format(rowavg), avgpermonthcol);

			// add chart row to chartdata
			if (rowsum > 0) {
				chartdata.addRow(row);
			}

			// add CategoryDisp total to piegraph results
			if (cattotal.getSum() > 0) {
				cattotal.setCatName(catname);
				piegraphres.add(cattotal);
			}
		}
		// end subcategory loop

		// make totals row for the chart
		// sum up columns
		// add to row
		Hashtable<Integer, Double> columntotals = new Hashtable<Integer, Double>();
		List<ChartRow> rows = chartdata.getRows();
		for (ChartRow row : rows) {
			// loop through all columns, summing values
			for (int i = 1; i < totalscolumn; i++) {
				String val = row.getColumn(i);
				if (!val.equals("")) {
					Integer colkey = new Integer(i);
					// convert to double
					Double colamt = 0D;
					try {
						Number amount = nf.parse(val);
						colamt = amount.doubleValue();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// add to columntotal
					Double coltotal = columntotals.get(colkey);
					if (coltotal == null) {
						coltotal = 0D;
					}
					coltotal += colamt;
					columntotals.put(colkey, coltotal);
				}
			}
		}
		ChartRow totalsrow = new ChartRow();
		totalsrow.addColumn("TOTAL");
		double rowtotal = 0;
		for (int i = 1; i < totalscolumn; i++) {
			Integer colkey = new Integer(i);
			Double value = columntotals.get(colkey);
			if (value != null) {
				String valuestr = nf.format(value);
				totalsrow.addColumn(valuestr);
				rowtotal += value.doubleValue();
			} else {
				totalsrow.addColumn("");
			}
		}
		double avgpermonth = rowtotal / monthcount;
		totalsrow.addColumn(nf.format(rowtotal));
		totalsrow.addColumn(nf.format(avgpermonth));
		chartdata.addRow(totalsrow);

		// make piegraph
		String graphurl = generateCategoryGraph(piegraphres,
				category.getName(), 0);

		// make bargraph
		String bargraphurl = generateYearToDateGraph(null, bargraphres,
				UtilityComparator.Sort.ByMonthYearStr);

		// assemble ReportElements object
		ReportElements report = new ReportElements();
		report.setChartData(chartdata);
		List<String> urls = new ArrayList<String>();
		urls.add(graphurl);
		urls.add(bargraphurl);
		report.addUrls(urls);

		return report;
	}

	private ReportElements processMonthlySubCats(ExpenseCriteria cpycriteria,
			ChartRow headers, List<String> monthtags,
			Hashtable<String, Integer> monthtaglkup, CategoryLevel catlvl,
			int totalscolumn, int monthcount, int avgpermonthcol) {
		CategoryDao category = catlvl.getCategory();
		// initialize working calendar
		Calendar cal = Calendar.getInstance();

		if (category.getNonexpense() != null
				&& category.getNonexpense().booleanValue()) {
			return null;
		}
		// initialize ChartData - with headers
		ChartData chartdata = new ChartData();
		chartdata.setHeaders(headers);

		// initialize bargraph results
		Hashtable<String, List<CategorySummaryDisp>> bargraphres = new Hashtable<String, List<CategorySummaryDisp>>();

		// initialize piegraph results
		List<CategorySummaryDisp> piegraphres = new ArrayList<CategorySummaryDisp>();

		// get direct subcategories for category
		List<CategoryDao> subcats = null;
		subcats = categoryService.getDirectSubcategories(category.getId());
		subcats.add(category);

		// loop through subcategories, retrieving monthly info for each,
		// and
		// summing monthly info into CategoryDisp object for summary
		// graph
		for (CategoryDao subcat : subcats) {
			// get all subcategories for this subcategory
			List<CategoryLevel> detailcats = null;
			if (subcat.getId() != category.getId()) {
				detailcats = categoryService.getAllSubcategories(subcat);
			} else {
				detailcats = new ArrayList<CategoryLevel>();
			}
			CategoryLevel lvl = categoryService.getAsCategoryLevel(
					subcat.getId());
			detailcats.add(lvl);

			// initialize summary CategoryDisp (bargraph)
			CategorySummaryDisp cattotal = new CategorySummaryDisp();

			// set category in criteria
			cpycriteria.clearCategoryLevelList();
			cpycriteria.clearCategoryLists();
			cpycriteria.setCategory(null);
			cpycriteria.setCategoryLevelList(detailcats);

			// initialize chartrow
			ChartRow row = new ChartRow();
			String catname = subcat.getName();
			row.addColumn(catname);

			// loop through all months, retrieving expense results for
			// each
			for (String month : monthtags) {
				// set dates in criteria
				Date rundate = null;
				Date startdate = null;
				Date enddate = null;
				try {
					rundate = mthyearformat.parse(month);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				cal.setTime(rundate);
				cal.set(Calendar.DAY_OF_MONTH, 1);
				startdate = cal.getTime();
				cal.add(Calendar.MONTH, 1);
				cal.set(Calendar.DAY_OF_MONTH, 1);
				enddate = cal.getTime();

				// set dates in criteria
				cpycriteria.setDateStart(startdate);
				cpycriteria.setDateEnd(enddate);
				// retrieve monthly expense total
				/* orig List<CategorySummaryDisp> rawresults = searchService
						.getExpenseTotal(cpycriteria,
								catlvl.getCategory().getName());*/
				List<CategorySummaryDisp> rawresults = searchService
						.getExpenseTotal(cpycriteria);				

				if (rawresults != null && rawresults.size() > 0) {
					CategorySummaryDisp raw = rawresults.get(0);
					raw.setCatName(catname);
					// add CategoryDisp to bargraph chart
					List<CategorySummaryDisp> fromhash = bargraphres.get(month);
					if (fromhash == null) {
						fromhash = new ArrayList<CategorySummaryDisp>();
					}
					fromhash.add(raw);
					bargraphres.put(month, fromhash);
					// add value to month column
					Integer key = monthtaglkup.get(month);
					String amount = nf.format(raw.getSum() * -1);
					row.addColumn(amount, key.intValue());
					// add to summary CategoryDisp (pie chart)
					cattotal.addExpenseAmt(raw.getSum() * -1);
				}
			}
			// end month loop

			// add totals column to end of row
			double rowsum = cattotal.getSum();
			row.addColumn(nf.format(rowsum), totalscolumn);

			// add avg column to end of row
			double rowavg = rowsum / 12; // MM make current year smart
			row.addColumn(nf.format(rowavg), avgpermonthcol);

			// add chart row to chartdata
			if (rowsum > 0) {
				chartdata.addRow(row);
			}

			// add CategoryDisp total to piegraph results
			if (cattotal.getSum() > 0) {
				cattotal.setCatName(catname);
				piegraphres.add(cattotal);
			}
		}
		// end subcategory loop

		// make totals row for the chart
		// sum up columns
		// add to row
		Hashtable<Integer, Double> columntotals = new Hashtable<Integer, Double>();
		List<ChartRow> rows = chartdata.getRows();
		for (ChartRow row : rows) {
			// loop through all columns, summing values
			for (int i = 1; i < totalscolumn; i++) {
				String val = row.getColumn(i);
				if (!val.equals("")) {
					Integer colkey = new Integer(i);
					// convert to double
					Double colamt = 0D;
					try {
						Number amount = nf.parse(val);
						colamt = amount.doubleValue();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// add to columntotal
					Double coltotal = columntotals.get(colkey);
					if (coltotal == null) {
						coltotal = 0D;
					}
					coltotal += colamt;
					columntotals.put(colkey, coltotal);
				}
			}
		}
		ChartRow totalsrow = new ChartRow();
		totalsrow.addColumn("TOTAL");
		double rowtotal = 0;
		for (int i = 1; i < totalscolumn; i++) {
			Integer colkey = new Integer(i);
			Double value = columntotals.get(colkey);
			if (value != null) {
				String valuestr = nf.format(value);
				totalsrow.addColumn(valuestr);
				rowtotal += value.doubleValue();
			} else {
				totalsrow.addColumn("");
			}
		}
		double avgpermonth = rowtotal / monthcount;
		totalsrow.addColumn(nf.format(rowtotal));
		totalsrow.addColumn(nf.format(avgpermonth));
		chartdata.addRow(totalsrow);

		// make piegraph
		String graphurl = generateCategoryGraph(piegraphres,
				category.getName(), 0);

		// make bargraph
		String bargraphurl = generateYearToDateGraph(null, bargraphres,
				UtilityComparator.Sort.ByMonthYearStr);

		// assemble ReportElements object
		ReportElements report = new ReportElements();
		report.setChartData(chartdata);
		List<String> urls = new ArrayList<String>();
		urls.add(graphurl);
		urls.add(bargraphurl);
		report.addUrls(urls);

		return report;
	}

	private List<ReportElements> crunchNumbersYearlyCategories(
			ExpenseCriteria criteria) {
		// initialize List of ReportElements
		List<ReportElements> reports = new ArrayList<ReportElements>();

		// initialize working calendar
		Calendar cal = Calendar.getInstance();
		// set report run date
		Integer reportrun = new Integer(getReportCriteria().getYear());
		int desireddate = reportrun.intValue();
		// start date is earliest possible, or 5 years ago
		Date firstdate = searchService.getFirstTransDate();
		cal.setTime(firstdate);
		int firstpossyear = cal.get(Calendar.YEAR);
		int beginyear = desireddate - 5 > firstpossyear ? desireddate - 5
				: firstpossyear;
		cal.set(Calendar.YEAR, beginyear);
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		Date startdate = cal.getTime();
		cal.set(Calendar.YEAR, desireddate + 1);
		Date enddate = cal.getTime();
		int yearcount = desireddate - beginyear + 1;

		// make copy of criteria
		ExpenseCriteria cpycriteria = criteria.clone();
		cpycriteria.setDateEnd(enddate);
		cpycriteria.setDateStart(startdate);

		// initialize Yeartag list and lkup
		List<String> yeartags = new ArrayList<String>();
		for (int i = 0; i < yearcount; i++) {
			yeartags.add(beginyear + i + "");
		}
		Hashtable<String, Integer> yeartaglkup = new Hashtable<String, Integer>();

		// get start and end dates

		// create headers row, find values for monthcount, and set totalscolumn
		// and avgpermonth column
		ChartRow headers = new ChartRow();
		headers.addColumn("Sub-Category");
		for (String year : yeartags) {
			Integer key = new Integer(headers.getColumnCount());
			yeartaglkup.put(year, key);
			headers.addColumn(year);
		}
		int totalscolumn = headers.getColumnCount();
		headers.addColumn("Total");
		headers.addColumn("Avg Per Month");

		// get all top-level categories for year
		List<CategoryLevel> categories = categoryService.getCategoriesUpToLevel(1);

		// loop through categories, creating ReportElement for each category,
		// and adding to list
		if (categories != null) {
			for (Iterator iter = categories.iterator(); iter.hasNext();) {
				CategoryLevel catlvl = (CategoryLevel) iter.next();

				ReportElements report = processYearlySubCategories(catlvl,
						cpycriteria, headers, yeartaglkup, yeartags, yearcount,
						totalscolumn);

				// add ReportElements object to List
				if (report != null) {
					reports.add(report);
				}

			}
			// end category loop

		}

		return reports;

	}

	private ReportElements crunchTargetDetail(ExpenseCriteria criteria) {
		ReportElements targetdet = new ReportElements();
		ExpenseCriteria detailcriteria = (ExpenseCriteria) criteria.clone();

		// parse into date
		String year = getReportCriteria().getYear();
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy");
		Date start;
		boolean iscurrentyear = false;
		try {
			start = dateformat.parse(year);
			Calendar cal = Calendar.getInstance();
			Calendar comp = Calendar.getInstance();
			cal.setTime(start);
			iscurrentyear = cal.get(Calendar.YEAR) == comp.get(Calendar.YEAR);
			cal.set(Calendar.MONTH, Calendar.JANUARY);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			detailcriteria.setDateStart(cal.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Hashtable targethash = new Hashtable();

		// get Targets for year
		TargetGroupDao target = targetService.loadTargetForYear(year);
		// place targets in Hashtable by categoryid
		List<TargetDetailDao> details = target.getTargetdetails();
		for (Iterator iter = details.iterator(); iter.hasNext();) {
			TargetDetailDao det = (TargetDetailDao) iter.next();
			Long catid = det.getCatid();
			targethash.put(catid, det);
		}

		// process categories
		// get all level 1 categories
		List<CategoryLevel> categories = categoryService
				.getCategoriesUpToLevel(1);

		// prepare month hashtable (monthkey as key, and TargetProgressDisp as
		// value
		String lastdatetag = new String();
		if (iscurrentyear) {
			// return current date
			lastdatetag = daydateformat.format(new Date());
		} else {
			// return the last day of the year
			lastdatetag = daydateformat.format(detailcriteria.getDateEnd());
		}

		// initialize the Hashtable
		Hashtable<String, ArrayList<TargetProgressDisp>> runningTotals = new Hashtable<String, ArrayList<TargetProgressDisp>>();
		// get month tag list
		List monthtags = getMonthTagList(detailcriteria, "MM-dd-yyyy");
		// remove January - first month of year doesn't make sense
		monthtags.remove(0);
		// add last date tag
		monthtags.add(lastdatetag);
		// put month tags into hashtable
		for (Iterator iter = monthtags.iterator(); iter.hasNext();) {
			String monthtag = (String) iter.next();
			runningTotals.put(monthtag, new ArrayList<TargetProgressDisp>());
		}

		// loop through categories
		for (Iterator iter = categories.iterator(); iter.hasNext();) {

			CategoryLevel catlvl = (CategoryLevel) iter.next();
			if (catlvl != null && catlvl.getCategory() != null
					&& !catlvl.getCategory().getNonexpense().booleanValue()) {
				List subcats = categoryService.getAllSubcategories(
						catlvl.getCategory());

				// set categories in criteria
				subcats.add(catlvl);
				detailcriteria.setCategoryLevelList(subcats);

				// get target info
				TargetDetailDao detail = (TargetDetailDao) targethash
						.get(catlvl.getCategory().getId());

				// add category information
				TargetProgressDisp catpt = new TargetProgressDisp();
				catpt.setCatName(catlvl.getCategory().getName());
				catpt.setCatId(catlvl.getCategory().getId());

				// calculate values throughout the year
				addValuesOverTime(detailcriteria, detail, catpt, lastdatetag,
						runningTotals);
			}
		}

		// get url for progress graph
		String graphprogressurl = generateTargetDetailGraph(runningTotals);

		// put together report elements to return them...
		targetdet.setName("Target Detail - " + year);
		targetdet.setUrl(graphprogressurl);

		return targetdet;

	}

	private ReportElements processYearlySubCategoriesDetail(
			CategoryLevel catlvl, ExpenseCriteria cpycriteria,
			ChartRow headers, Hashtable<String, Integer> yeartaglkup,
			List<String> yeartags, int yearcount, int totalscolumn) {
		CategoryDao category = catlvl.getCategory();
		if (category.getNonexpense() != null
				&& category.getNonexpense().booleanValue()) {
			return null;
		}

		// initialize ChartData - with headers
		ChartData chartdata = new ChartData();
		chartdata.setHeaders(headers);

		// initialize Calender
		Calendar cal = Calendar.getInstance();

		// initialize bargraph results
		Hashtable<String, List<CategorySummaryDisp>> bargraphres = new Hashtable<String, List<CategorySummaryDisp>>();

		// get all subcategories for category, adding the category
		// itself
		List<CategoryLevel> subcats = null;
		subcats = categoryService.getAllSubcategories(category);
		subcats.add(catlvl);
		/*
		 * if (category.getId()==57) { subcats =
		 * categoryService.getSubcategoriesToLevel (category.getId(), 2);
		 * subcats.add(catlvl); } else { //MM just for testing subcats = new
		 * ArrayList<CategoryLevel>(); }
		 */

		// loop through subcategories, retrieving monthly info for each,
		// and
		// summing monthly info into CategoryDisp object for summary
		// graph
		for (CategoryLevel subcat : subcats) {

			// set category in criteria
			cpycriteria.clearCategoryLevelList();
			cpycriteria.clearCategoryLists();
			cpycriteria.setCategory(null);
			cpycriteria.setCategory(subcat.getCategory().getId());

			// initialize chartrow, rowsum
			ChartRow row = new ChartRow();
			String catname = subcat.getCategory().getName();
			row.addColumn(catname);
			double rowsum = 0;

			// loop through all years, retrieving expense results for
			// each
			for (String year : yeartags) {
				// set dates in criteria
				Date rundate = null;
				Date startdate = null;
				Date enddate = null;
				try {
					rundate = yearformat.parse(year);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				cal.setTime(rundate);
				cal.set(Calendar.DAY_OF_MONTH, 1);
				cal.set(Calendar.MONTH, Calendar.JANUARY);
				startdate = cal.getTime();
				cal.add(Calendar.YEAR, 1);
				enddate = cal.getTime();

				// set dates in criteria
				cpycriteria.setDateStart(startdate);
				cpycriteria.setDateEnd(enddate);

				// retrieve yearly expense total
				//MM orig 
				/*List<CategorySummaryDisp> rawresults = searchService
						.getExpenseTotal(cpycriteria,
								catlvl.getCategory().getName());*/
				List<CategorySummaryDisp> rawresults = searchService
						.getExpenseTotal(cpycriteria);

				if (rawresults != null && rawresults.size() > 0) {
					CategorySummaryDisp raw = rawresults.get(0);
					raw.setCatName(catname);
					// add CategoryDisp to bargraph chart
					List<CategorySummaryDisp> fromhash = bargraphres.get(year);
					if (fromhash == null) {
						fromhash = new ArrayList<CategorySummaryDisp>();
					}
					fromhash.add(raw);
					bargraphres.put(year, fromhash);
					// add value to year column
					Integer key = yeartaglkup.get(year);
					String amount = nf.format(raw.getSum() * -1);
					row.addColumn(amount, key.intValue());
					rowsum += raw.getSum() * -1;
				}
			}
			// end year loop

			// add totals column to end of row
			row.addColumn(nf.format(rowsum), totalscolumn);

			// add avg column to end of row
			double rowavg = rowsum / yearcount / 12; // MM make current
														// year smart
			row.addColumn(nf.format(rowavg), totalscolumn + 1);

			// add chart row to chartdata
			if (rowsum > 0) {
				chartdata.addRow(row);
			}
		}
		// end subcategory loop

		// make totals row for the chart
		// sum up columns
		// add to row
		Hashtable<Integer, Double> columntotals = new Hashtable<Integer, Double>();
		List<ChartRow> rows = chartdata.getRows();
		for (ChartRow row : rows) {
			// loop through all columns, summing values
			for (int i = 1; i < totalscolumn; i++) {
				String val = row.getColumn(i);
				if (!val.equals("")) {
					Integer colkey = new Integer(i);
					// convert to double
					Double colamt = 0D;
					try {
						Number amount = nf.parse(val);
						colamt = amount.doubleValue();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// add to columntotal
					Double coltotal = columntotals.get(colkey);
					if (coltotal == null) {
						coltotal = 0D;
					}
					coltotal += colamt;
					columntotals.put(colkey, coltotal);
				}
			}
		}
		ChartRow totalsrow = new ChartRow();
		totalsrow.addColumn("TOTAL");
		double rowtotal = 0;
		for (int i = 1; i < totalscolumn; i++) {
			Integer colkey = new Integer(i);
			Double value = columntotals.get(colkey);
			if (value != null) {
				String valuestr = nf.format(value);
				totalsrow.addColumn(valuestr);
				rowtotal += value.doubleValue();
			} else {
				totalsrow.addColumn("");
			}
		}
		double avgperyear = rowtotal / yearcount / 12;
		totalsrow.addColumn(nf.format(rowtotal));
		totalsrow.addColumn(nf.format(avgperyear));
		chartdata.addRow(totalsrow);

		// make bargraph
		String title = category.getName() + " - Yearly Comparison";
		String bargraphurl = generateYearToDateGraph(title, bargraphres,
				UtilityComparator.Sort.ByYearStr);

		// assemble ReportElements object
		ReportElements report = new ReportElements();
		report.setChartData(chartdata);
		List<String> urls = new ArrayList<String>();
		urls.add(bargraphurl);
		report.addUrls(urls);

		return report;

	}

	private ReportElements processYearlySubCategories(CategoryLevel catlvl,
			ExpenseCriteria cpycriteria, ChartRow headers,
			Hashtable<String, Integer> yeartaglkup, List<String> yeartags,
			int yearcount, int totalscolumn) {
		CategoryDao category = catlvl.getCategory();
		if (category.getNonexpense() != null
				&& category.getNonexpense().booleanValue()) {
			return null;
		}

		// initialize ChartData - with headers
		ChartData chartdata = new ChartData();
		chartdata.setHeaders(headers);

		// initialize Calender
		Calendar cal = Calendar.getInstance();

		// initialize bargraph results
		Hashtable<String, List<CategorySummaryDisp>> bargraphres = new Hashtable<String, List<CategorySummaryDisp>>();

		// get direct subcategories for category
		List<CategoryDao> subcats = null;
		subcats = categoryService.getDirectSubcategories(category.getId());
		subcats.add(category);

		// loop through subcategories, retrieving monthly info for each,
		// and
		// summing monthly info into CategoryDisp object for summary
		// graph
		for (CategoryDao subcat : subcats) {
			// get all subcategories for this subcategory
			List<CategoryLevel> detailcats = null;
			if (subcat.getId() != category.getId()) {
				detailcats = categoryService.getAllSubcategories(subcat);
			} else {
				detailcats = new ArrayList<CategoryLevel>();
			}
			CategoryLevel lvl = categoryService.getAsCategoryLevel(
					subcat.getId());
			detailcats.add(lvl);

			// set category in criteria
			cpycriteria.clearCategoryLevelList();
			cpycriteria.clearCategoryLists();
			cpycriteria.setCategoryLevelList(detailcats);
			cpycriteria.setShowSubcats(true);
			
			// initialize chartrow, rowsum
			ChartRow row = new ChartRow();
			String catname = subcat.getName();
			row.addColumn(catname);
			double rowsum = 0;

			// get results by year
			List<CategorySummaryDisp> rawresults = searchService
					.getExpenseTotalByYear(cpycriteria);
			
			// loop through all years, retrieving expense results for
			// each
			for (String year : yeartags) {
				// get matching year from results
				long matchyear = new Long(year).longValue();
				CategorySummaryDisp thisyear = null;
				for (CategorySummaryDisp test:rawresults) {
					if (test.getYear().longValue()== matchyear) {
						thisyear=test;
						break;
					}
				}

				if (thisyear!=null) {
					thisyear.setCatName(catname);
					// add CategoryDisp to bargraph chart
					List<CategorySummaryDisp> fromhash = bargraphres.get(year);
					if (fromhash == null) {
						fromhash = new ArrayList<CategorySummaryDisp>();
					}
					fromhash.add(thisyear);
					bargraphres.put(year, fromhash);
					// add value to year column
					Integer key = yeartaglkup.get(year);
					String amount = nf.format(thisyear.getSum() * -1);
					row.addColumn(amount, key.intValue());
					rowsum += thisyear.getSum() * -1;
				}
			}
			// end year loop

			// add totals column to end of row
			row.addColumn(nf.format(rowsum), totalscolumn);

			// add avg column to end of row
			double rowavg = rowsum / yearcount / 12; // MM make current
														// year smart
			row.addColumn(nf.format(rowavg), totalscolumn + 1);

			// add chart row to chartdata
			if (rowsum > 0) {
				chartdata.addRow(row);
			}
		}
		// end subcategory loop

		// make totals row for the chart
		// sum up columns
		// add to row
		Hashtable<Integer, Double> columntotals = new Hashtable<Integer, Double>();
		List<ChartRow> rows = chartdata.getRows();
		for (ChartRow row : rows) {
			// loop through all columns, summing values
			for (int i = 1; i < totalscolumn; i++) {
				String val = row.getColumn(i);
				if (!val.equals("")) {
					Integer colkey = new Integer(i);
					// convert to double
					Double colamt = 0D;
					try {
						Number amount = nf.parse(val);
						colamt = amount.doubleValue();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// add to columntotal
					Double coltotal = columntotals.get(colkey);
					if (coltotal == null) {
						coltotal = 0D;
					}
					coltotal += colamt;
					columntotals.put(colkey, coltotal);
				}
			}
		}
		ChartRow totalsrow = new ChartRow();
		totalsrow.addColumn("TOTAL");
		double rowtotal = 0;
		for (int i = 1; i < totalscolumn; i++) {
			Integer colkey = new Integer(i);
			Double value = columntotals.get(colkey);
			if (value != null) {
				String valuestr = nf.format(value);
				totalsrow.addColumn(valuestr);
				rowtotal += value.doubleValue();
			} else {
				totalsrow.addColumn("");
			}
		}
		double avgperyear = rowtotal / yearcount / 12;
		totalsrow.addColumn(nf.format(rowtotal));
		totalsrow.addColumn(nf.format(avgperyear));
		chartdata.addRow(totalsrow);

		// make bargraph
		String title = category.getName() + " - Yearly Comparison";
		String bargraphurl = generateYearToDateGraph(title, bargraphres,
				UtilityComparator.Sort.ByYearStr);

		// assemble ReportElements object
		ReportElements report = new ReportElements();
		report.setChartData(chartdata);
		List<String> urls = new ArrayList<String>();
		urls.add(bargraphurl);
		report.addUrls(urls);

		return report;

	}

	private ReportElements crunchTargetSummary(ExpenseCriteria criteria) {
		ReportElements targetsumm = new ReportElements();

		String year = getReportCriteria().getYear();

		// Storage lists
		List graphdata = new ArrayList();
		Hashtable targethash = new Hashtable();

		// get Targets for year
		TargetGroupDao target =targetService.loadTargetForYear(year);
		// place targets in Hashtable by categoryid
		List<TargetDetailDao> details = target.getTargetdetails();
		for (Iterator iter = details.iterator(); iter.hasNext();) {
			TargetDetailDao det = (TargetDetailDao) iter.next();
			Long catid = det.getCatid();
			targethash.put(catid, det);
		}

		// process categories
		// get all level 1 categories
		List<CategoryLevel> categories = categoryService
				.getCategoriesUpToLevel(1);

		// initialize chartdata
		ChartData chartdata = new ChartData();
		ChartRow headers = new ChartRow();
		headers.addColumn("Category");
		headers.addColumn("Spent");
		headers.addColumn("Targeted");
		headers.addColumn("Status");
		chartdata.setHeaders(headers);

		// loop through categories
		double totalspent = 0;
		double totaltargeted = 0;

		for (Iterator iter = categories.iterator(); iter.hasNext();) {

			CategoryLevel catlvl = (CategoryLevel) iter.next();
			List subcats = categoryService.getAllSubcategories(
					catlvl.getCategory());

			// set categories in criteria
			subcats.add(catlvl);
			criteria.setCategoryLevelList(subcats);
			criteria.setShowSubcats(true);
			// retrieve totals
			/* MM orig - List results = searchService.getExpenseTotal(criteria,
					catlvl.getCategory().getName());*/
			List results = searchService.getExpenseTotal(criteria);

			// loop through results
			if (results != null && results.size() > 0) {
				// if results are available, insert totals in Hashtable, add to
				// category total
				TargetProgressDisp cat = new TargetProgressDisp();
				cat.setCatName(catlvl.getCategory().getName());
				// get info from results
				CategorySummaryDisp spentsum = (CategorySummaryDisp) results
						.get(0);
				cat.setAmountSpent(spentsum.getSum());
				// add target info
				TargetDetailDao detail = (TargetDetailDao) targethash
						.get(catlvl.getCategory().getId());
				if (detail != null) {
					cat.setAmountTargeted(detail.getAmount().doubleValue());
				}
				graphdata.add(cat);

				// gather info and make row
				// get amtspent
				double amtspent = cat.getAmountSpent();
				// get amttargeted
				double amttargeted = cat.getAmountTargeted();
				// get status
				String status = cat.getStatusMessage();

				// add row
				ChartRow row = new ChartRow();
				row.addColumn(catlvl.getCategory().getName());
				row.addColumn(nf.format(amtspent));
				row.addColumn(nf.format(amttargeted));
				row.addColumn(status);
				chartdata.addRow(row);

				// add sums
				totalspent += amtspent;
				totaltargeted += amttargeted;
			}
		}

		// get url for graph
		String graphurl = generateTargetSummGraph("Target Status", graphdata,
				false);

		// now totals to list
		String status = null;
		if (totaltargeted == 0) {
			status = "No Target";
		} else if (totaltargeted == totalspent) {
			status = "Target Met";
		} else if (totaltargeted > totalspent) {
			double percentage = totalspent / totaltargeted;
			percentage = Math.round(percentage * 100);
			status = percentage + "% of Target spent";
		} else {
			double percentage = totalspent / totaltargeted;
			percentage = Math.round(Math.abs(1 - percentage) * 100);

			status = "Target exceeded by "
					+ nf.format(totalspent - totaltargeted) + " Euros ("
					+ percentage + "%)";
		}
		ChartRow row = new ChartRow();
		row.addColumn("TOTAL");
		row.addColumn(nf.format(totalspent));
		row.addColumn(nf.format(totaltargeted));
		row.addColumn(status);
		chartdata.addRow(row);

		// put together report elements to return them...
		targetsumm.setName("Target Status - " + year);
		targetsumm.setChartData(chartdata);
		targetsumm.setUrl(graphurl);

		return targetsumm;

	}

	private String generateMMYearToDateGraph(
			Hashtable<String, List<CategorySummaryDisp>> results) {
		// row keys... are categories
		// column keys... months - sort to prepare
		Set months = results.keySet();
		List monthlist = new ArrayList();
		monthlist.addAll(months);

		UtilityComparator comp = new UtilityComparator();
		comp.setSortType(UtilityComparator.Sort.ByYearStr);

		Collections.sort(monthlist, comp);

		// fill in dataset
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (Iterator iter = monthlist.iterator(); iter.hasNext();) {
			String monthkey = (String) iter.next();
			List catsumdisps = results.get(monthkey);
			// iterate through displays
			if (catsumdisps != null) {
				for (Iterator iterator = catsumdisps.iterator(); iterator
						.hasNext();) {
					CategorySummaryDisp catsum = (CategorySummaryDisp) iterator
							.next();
					dataset.addValue(Math.abs(catsum.getSum()),
							catsum.getCatName(), monthkey);
				}
			}
		}

		// create the chart...
		JFreeChart chart = ChartFactory.createStackedBarChart(
				"Year Comparison", // chart title
				"Year", // domain axis label
				"Expenditure", // range axis label
				dataset, // data
				PlotOrientation.VERTICAL, // orientation
				true, // include legend
				false, // tooltips?
				false // URLs?
				);

		// set the background color for the chart...
		chart.setBackgroundPaint(Color.white);

		// get a reference to the plot for further customisation...
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setDomainGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.white);
		plot.setForegroundAlpha(.5f);

		// set the range axis to display integers only...
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions
				.createUpRotationLabelPositions(Math.PI / 6.0));

		// save graph and return url
		String filename = "fullreport_comparison_" + (new Date()).getTime()
				+ ".png";
		File imagefile = new File(getReportCriteria().getImageDir() + filename);
		try {
			ChartUtilities.saveChartAsPNG(imagefile, chart, 550, 550);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return getReportCriteria().getImageLink() + filename;
	}

	public String getResultView(String outputtype) {
		if (outputtype != null && outputtype.toLowerCase().equals("pdf")) {
			return "pdfyearlyreport";
		}
		return "newyearlyreport";
	}

	public Hashtable<Integer, Boolean> getRequiredParameters() {
		Hashtable<Integer, Boolean> req = new Hashtable<Integer, Boolean>();
	//	req.put(new Integer(Report.Parameters.excludenonexp), new Boolean(true));
	//	req.put(new Integer(Report.Parameters.yearlist), new Boolean(true));
		
		/*req.put(new Integer(Report.Parameters.daterange), new Boolean(false));
		req.put(new Integer(Report.Parameters.comparetype), new Boolean(false));
		req.put(new Integer(Report.Parameters.breakoutlvl), new Boolean(false));
		req.put(new Integer(Report.Parameters.category), new Boolean(false));
		req.put(new Integer(Report.Parameters.monthlist), new Boolean(false));
		*/
		return req;
	}

	protected ReportElements crunctthNumbersYearComparison(
			ExpenseCriteria origcriteria) {
		ExpenseCriteria criteria = new ExpenseCriteria();
		criteria.setExcludeNonExpense(origcriteria.getExcludeNonExpense());
		criteria.setTransactionType(new Long(
				ExpenseCriteria.TransactionType.DEBITS));

		// set start and end dates
		// end date is first of next month
		// start date depends upon the comparetype, and is
		// either 12 months earlier, the last calendar year, or all
		Date enddate = origcriteria.getDateEnd();
		Calendar cal = Calendar.getInstance();
		cal.setTime(enddate);

		if ((origcriteria.getCompareType() != null)
				&& origcriteria.getCompareType() == ReportCriteria.CompareType.LASTMONTHS) {
			cal.add(Calendar.MONTH, -13);
			cal.set(Calendar.DAY_OF_MONTH, 1);
		} else if ((origcriteria.getCompareType() != null)
				&& origcriteria.getCompareType() == ReportCriteria.CompareType.CALYEAR) {
			cal.set(Calendar.MONTH, Calendar.JANUARY);
			cal.set(Calendar.DAY_OF_MONTH, 1);
		} else {
			Date firstdate = searchService.getFirstTransDate();
			Calendar first = Calendar.getInstance();
			first.setTime(firstdate);
			cal.set(Calendar.YEAR, first.get(Calendar.YEAR));
			cal.set(Calendar.MONTH, first.get(Calendar.MONTH));
			cal.set(Calendar.DAY_OF_MONTH, 1);
		}
		Date startdate = cal.getTime();
		criteria.setDateEnd(enddate);
		criteria.setDateStart(startdate);

		// Storage lists
		Hashtable<String, List<CategorySummaryDisp>> results = new Hashtable();
		List<CategorySummaryDisp> disptotals = new ArrayList<CategorySummaryDisp>();
		int yearcount = 0;
		// get all level 1 categories
		List<CategoryLevel> categories = categoryService
				.getCategoriesUpToLevel(1);

		// loop through categories
		for (CategoryLevel catlvl : categories) {
			List<CategoryLevel> subcats = categoryService
					.getAllSubcategories(catlvl.getCategory());
			// set categories in criteria
			subcats.add(catlvl);
			criteria.setCategoryLevelList(subcats);

			// retrieve totals
			/* MM orig - List<CategorySummaryDisp> totals = searchService
					.getExpenseTotalByYear(criteria,
							catlvl.getCategory().getName());*/
			List<CategorySummaryDisp> totals = searchService
					.getExpenseTotalByYear(criteria);

			// loop through results
			if (totals != null && totals.size() > 0) {
				// if results are available, insert totals in Hashtable, add to
				// category total
				CategorySummaryDisp cattotal = new CategorySummaryDisp();
				cattotal.setCatName(catlvl.getCategory().getName());
				for (CategorySummaryDisp catsum : totals) {
					cattotal.addExpenseAmt(new Double(catsum.getSum()));
					// month key
					// get date formatter
					SimpleDateFormat dateformat = new SimpleDateFormat("yyyy");
					String yearkey = dateformat.format(catsum.getSummaryDate());
					// insert in result hash
					if (!results.containsKey(yearkey)) {
						yearcount++;
					}
					List listfordate = results.get(yearkey);
					if (listfordate == null) {
						listfordate = new ArrayList();
					}
					listfordate.add(catsum);
					results.put(yearkey, listfordate);

				}
				// add category total to list
				disptotals.add(cattotal);
			}
		}

		// get url for graph
		String graphurl = generateMMYearToDateGraph(results);

		// count months and add to display totals (for showing average per
		// month)
		CategorySummaryDisp totsum = new CategorySummaryDisp();
		totsum.setCatName("TOTAL");
		totsum.setAveragePerDivisor(yearcount);
		for (Iterator iter = disptotals.iterator(); iter.hasNext();) {
			CategorySummaryDisp catsum = (CategorySummaryDisp) iter.next();
			double avgpermonth = catsum.getSum() / yearcount;
			catsum.setAveragePerDivisor(avgpermonth);
			totsum.addExpenseAmt(new Double(catsum.getSum()));
		}
		double avgpermonth = totsum.getSum() / yearcount;
		totsum.setAveragePerDivisor(avgpermonth);
		disptotals.add(totsum);

		// put together return objects
		ReportElements re = new ReportElements();
		re.setSummaries(disptotals);
		re.setUrl(graphurl);

		return re;
	}

	private String generateTargetDetailGraph(
			Hashtable<String, ArrayList<TargetProgressDisp>> runningTotals) {
		// row keys... are categories
		// column keys... months - sort to prepare
		Set months = runningTotals.keySet();
		List monthlist = new ArrayList();
		monthlist.addAll(months);

		UtilityComparator comp = new UtilityComparator();
		comp.setSortType(UtilityComparator.Sort.ByMonthDayYearStr);
		Collections.sort(monthlist, comp);

		// fill in dataset
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (Iterator iter = monthlist.iterator(); iter.hasNext();) {
			String monthkey = (String) iter.next();
			List targetdisps = runningTotals.get(monthkey);
			// iterate through displays
			if (targetdisps != null) {
				for (Iterator iterator = targetdisps.iterator(); iterator
						.hasNext();) {
					TargetProgressDisp targetprog = (TargetProgressDisp) iterator
							.next();
					double graphvalue = targetprog.getAmountSpent()
							/ targetprog.getAmountTargeted();
					if (targetprog.spendingExceedsTarget()) {
						graphvalue = graphvalue - 1.0;
					} else {
						graphvalue = 1.0 - graphvalue;
						graphvalue = graphvalue * -1.0;
					}
					graphvalue = graphvalue * 100.0;
					String catname = targetprog.getCatName() == null ? "null"
							: targetprog.getCatName();
					dataset.addValue(graphvalue, catname, monthkey);

					/*
					 * System.out.println(monthkey + ";" + catname + ";" +
					 * targetprog.getAmountSpent() + ";" +
					 * targetprog.getAmountTargeted()+ ";" + graphvalue +
					 * ";newline");
					 */

				}
			}
		}

		// create the chart...
		JFreeChart chart = ChartFactory.createLineChart("Target Variances", // chart
																			// title
				"month", // x axis label
				"over/under", // y axis label
				dataset, // data
				PlotOrientation.VERTICAL, // orientation
				true, // include legend
				false, // tooltips
				false); // URLs

		LegendTitle legend = chart.getLegend();
		legend.setPosition(RectangleEdge.RIGHT);

		// set the background color for the chart...
		chart.setBackgroundPaint(Color.white);

		// get a reference to the plot for further customisation...
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setDomainGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.white);
		plot.setForegroundAlpha(.9f);

		CategoryItemRenderer cir = plot.getRenderer();
		try {
			int seriescnt = dataset.getRowCount();
			for (int i = 0; i < seriescnt; i++) {
				cir.setSeriesStroke(i, new BasicStroke(3.5f)); // series line
																// style
			}

		} catch (Exception e) {
		}

		// set the range axis to display integers only...
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions
				.createUpRotationLabelPositions(Math.PI / 6.0));

		// save graph and return url
		String filename = "fullreport_targetcomparison_"
				+ (new Date()).getTime() + ".png";
		File imagefile = new File(getReportCriteria().getImageDir() + filename);
		try {
			ChartUtilities.saveChartAsPNG(imagefile, chart, 550, 550);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return getReportCriteria().getImageLink() + filename;
	}

	protected String generateTargetSummGraph(String graphname, List results,
			boolean ishoriz) {
		final String exclabel = "Exceeded Target";
		final String tarlabel = "Target";
		final String spentlabel = "Spent";
		// row keys... are target, spent and exceeded
		// column keys... categories

		// fill in dataset
		List exc = new ArrayList();
		List targ = new ArrayList();
		List spe = new ArrayList();
		List notarget = new ArrayList();
		List categories = new ArrayList();

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		int i = 0;
		for (Iterator iter = results.iterator(); iter.hasNext();) {
			TargetProgressDisp target = (TargetProgressDisp) iter.next();
			String catname = target.getCatName();
			categories.add(i, catname);

			double exceeded = 0;
			double targeted = 0;
			double spent = 0;
			double notarg = 0;
			// breakout according to relationship of target to spent
			if (target.spendingExceedsTarget()) {
				// we'll need two values - exceeded and Target
				exceeded = target.getExceededAmount();
				targeted = target.getAmountTargeted();
			} else if (target.targetDoesntExist()) {
				// we'll only need one value - spent
				notarg = target.getAmountSpent();
			} else if (target.spendingEqualsTarget()) {
				// we'll only need one value - spent
				spent = target.getAmountSpent();
			} else {
				// we'll need two values - spent and Target
				spent = target.getAmountSpent();
				targeted = target.getAmountTargeted() - target.getAmountSpent();
			}

			// add to dataset
			exc.add(i, new Double(exceeded));
			targ.add(i, new Double(targeted));
			spe.add(i, new Double(spent));
			targ.add(i, new Double(targeted));
			notarget.add(i, new Double(notarg));
			i++;
		}

		// fill in dataset
		for (int j = 0; j < categories.size(); j++) {
			String catname = (String) categories.get(j);
			// add spent
			Double spent = (Double) spe.get(j);
			double val = spent != null ? spent.doubleValue() : 0;
			dataset.addValue(val, spentlabel, catname);
			// add targeted
			Double targeted = (Double) targ.get(j);
			val = targeted != null ? targeted.doubleValue() : 0;
			dataset.addValue(val, tarlabel, catname);
			// add exceeded
			Double excval = (Double) exc.get(j);
			val = excval != null ? excval.doubleValue() : 0;
			dataset.addValue(val, exclabel, catname);
			// add no target
			Double notargval = (Double) notarget.get(j);
			val = notargval != null ? notargval.doubleValue() : 0;
			dataset.addValue(val, "No Target", catname);
		}

		// create the chart...
		PlotOrientation orient = PlotOrientation.HORIZONTAL;
		if (!ishoriz) {
			orient = PlotOrientation.VERTICAL;
		}
		JFreeChart chart = ChartFactory.createStackedBarChart(graphname, // chart
				// title
				"Category", // domain axis label
				"Status", // range axis label
				dataset, // data
				orient, // orientation
				true, // include legend
				false, // tooltips?
				false // URLs?
				);

		// set the background color for the chart...
		chart.setBackgroundPaint(Color.white);

		// get a reference to the plot for further customisation...
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setDomainGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.white);

		// set the range axis to display integers only...
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		// set colors
		StackedBarRenderer renderer = (StackedBarRenderer) plot.getRenderer();
		renderer.setSeriesPaint(0, ChartColor.BLUE);
		renderer.setSeriesPaint(1, ChartColor.YELLOW);
		renderer.setSeriesPaint(2, ChartColor.RED);
		renderer.setSeriesPaint(3, ChartColor.LIGHT_CYAN);

		CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions
				.createUpRotationLabelPositions(Math.PI / 6.0));

		// save graph and return url
		String filename = "monthlytargets_" + ishoriz + "_"
				+ (new Date()).getTime() + ".png";
		File imagefile = new File(getReportCriteria().getImageDir() + filename);
		try {
			ChartUtilities.saveChartAsPNG(imagefile, chart, 550, 550);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return getReportCriteria().getImageLink() + filename;

	}

	/**
	 * run target status for each month of the year (specifically, each month
	 * listed as a key in the runningTotals.
	 * 
	 * @param criteria
	 * @param detail
	 * @param catpt
	 * @param lastdatetag
	 * @param runningTotals
	 */
	private void addValuesOverTime(ExpenseCriteria criteria,
			TargetDetailDao target, TargetProgressDisp catpt,
			String lastdatetag,
			Hashtable<String, ArrayList<TargetProgressDisp>> runningTotals) {
		// get info
		String catname = catpt.getCatName();

		// prepare clone of criteria
		ExpenseCriteria timecriteria = (ExpenseCriteria) criteria.clone();

		// get keys of hashtable
		Enumeration<String> datekeys = runningTotals.keys();

		// loop through keys
		while (datekeys.hasMoreElements()) {
			String datekey = datekeys.nextElement();

			// convert key to date
			Date rundate = null;
			try {
				rundate = daydateformat.parse(datekey);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			timecriteria.setDateEnd(rundate);

			// process targets for time range
			// get percentage through year
			double percentageofyear = 100.0;
			int partofyear = getDayCount(timecriteria);
			percentageofyear = (Math.round((double) partofyear / (double) 365
					* 10000.0));
			percentageofyear = percentageofyear / 100;
			// get target for this date
			double wholeyeartarget = target != null ? target.getAmount() : 0;
			double partialtarget = (wholeyeartarget * percentageofyear) / 100.0;

			// run totals for date
			/* MM orig - List<CategorySummaryDisp> results = searchService
					.getExpenseTotal(timecriteria, catname);*/
			List<CategorySummaryDisp> results = searchService
					.getExpenseTotal(timecriteria);
			if (results != null && results.size() > 0) {
				CategorySummaryDisp spentsum = (CategorySummaryDisp) results
						.get(0);
				double spent = spentsum.getSum();

				// process information -
				// put target information, category information into
				// TargetProgressDisp object
				TargetProgressDisp targetrun = new TargetProgressDisp();
				targetrun.setAmountSpent(spent);
				targetrun.setAmountTargeted(partialtarget);
				targetrun.setCatName(catname);
				// add TargetProgressDisp object to list in Hashtable
				ArrayList<TargetProgressDisp> list = runningTotals.get(datekey);
				list.add(targetrun);
				runningTotals.put(datekey, list);
			} else {
				TargetProgressDisp targetrun = new TargetProgressDisp();
				targetrun.setAmountSpent(0.0);
				targetrun.setAmountTargeted(partialtarget);
				targetrun.setCatId(catpt.getCatId());
				targetrun.setCatName(catpt.getCatName());

				// add TargetProgressDisp object to list in Hashtable
				ArrayList<TargetProgressDisp> list = runningTotals.get(datekey);
				list.add(targetrun);
				runningTotals.put(datekey, list);
			}

		}
		// end loop through keys

	}

}
