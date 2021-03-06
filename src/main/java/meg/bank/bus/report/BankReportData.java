package meg.bank.bus.report;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
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
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import meg.bank.bus.CategoryLevel;
import meg.bank.bus.CategoryService;
import meg.bank.bus.ExpenseCriteria;
import meg.bank.bus.ExpenseCriteria.CategorizedType;
import meg.bank.bus.SearchService;
import meg.bank.bus.TargetService;
import meg.bank.bus.dao.CategoryDao;
import meg.bank.bus.dao.ExpenseDao;
import meg.bank.bus.dao.TargetDetailDao;
import meg.bank.bus.dao.TargetGroupDao;
import meg.bank.bus.report.elements.CategorySummaryDisp;
import meg.bank.bus.report.elements.ChartData;
import meg.bank.bus.report.elements.ChartRow;
import meg.bank.bus.report.elements.ReportData;
import meg.bank.bus.report.elements.ReportElement;
import meg.bank.bus.report.elements.ReportLabel;
import meg.bank.bus.report.elements.TargetProgressDisp;
import meg.bank.bus.report.tools.CategorySummaryComparator;
import meg.bank.bus.report.tools.ChartRowComparator;
import meg.bank.bus.report.tools.ExpenseComparator;
import meg.bank.bus.report.tools.UtilityComparator;
import meg.bank.util.DateUtils;

import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

@XmlRootElement(name = "reportdata")
public abstract class BankReportData implements ReportData {

	protected List<ReportElement> elements;

	protected List<ReportLabel> reportlabels;

	protected SearchService searchService;

	protected CategoryService categoryService;

	protected TargetService targetService;

	protected ReportCriteria reportCriteria;

	protected static SimpleDateFormat daydateformat = new SimpleDateFormat(
			"MM-dd-yyyy");
	protected static NumberFormat nf = new DecimalFormat("######.00",
			new DecimalFormatSymbols(Locale.US));
	SimpleDateFormat mthyearformat = new SimpleDateFormat("MM-yyyy", Locale.US);

	public final class DispLabel {
		public static final String Date = "Date";
		public static final String Category = "Category";
		public static final String Subcategory = "Subcategory";
		public static final String Detail = "Detail";
		public static final String Amount = "Amount";
		public static final String Days = "Days";
		public static final String Spent = "Spent";
		public static final String Targeted = "Targeted";
		public static final String Status = "Status";
		public static final String Summary = "Summary";

		public static final String FullMonthTitle = "Full Month Report";
		public static final String MonthlyTargetTitle = "Monthly Targets Report";
		public static final String YearlyTargetTitle = "Yearly Target Status Report";
		public static final String YearlySummaryTitle = "Yearly Summary Report";

		public static final String AllExpenses = "All Expenses";
		public static final String TargetStatus = "Target Status";
		public static final String SummaryByCategory = "Summary By Category";
		public static final String YearToDate = "Year To Date";
		public static final String PercentageThrYear = "Percentage Through Year";
	}

	public BankReportData(ReportCriteria reportCriteria,
			SearchService searchService, CategoryService categoryService,
			TargetService targetService) {
		this.reportCriteria = reportCriteria;
		this.searchService = searchService;
		this.categoryService = categoryService;
		this.targetService = targetService;
	}

	public BankReportData() {
		super();
	}

	@XmlElement(name = "labels")
	public List<ReportLabel> getReportLabels() {
		return reportlabels;
	}

	@XmlElement(name = "element")
	public List<ReportElement> getElements() {
		return elements;
	}

	public HashMap<String, Object> resultsAsHashMap() {
		// create hashmap
		HashMap<String, Object> results = new HashMap<String, Object>();

		// put labels in hashmap
		if (getReportLabels() != null) {
			for (ReportLabel lbl : getReportLabels()) {
				results.put(lbl.getTag(), lbl.getText());
			}
		}
		// put elements in hashmap
		if (getElements() != null) {
			for (ReportElement e : getElements()) {
				results.put(e.getTag(), e);
			}
		}
		// return hashmap
		return results;
	}

	public String resultsAsXml() throws JAXBException {

		StringWriter sw = new StringWriter();
		JAXBContext context = JAXBContext.newInstance(BankReportData.class);
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

		m.marshal(this, sw);

		return sw.toString();

	}

	@XmlTransient
	public ReportCriteria getReportCriteria() {
		return reportCriteria;
	}

	public void setReportCriteria(ReportCriteria reportCriteria) {
		this.reportCriteria = reportCriteria;
	}

	public void addElement(ReportElement element) {
		if (element != null) {
			if (this.elements == null) {
				this.elements = new ArrayList<ReportElement>();
			}
			elements.add(element);
		}
	}

	public void setElements(List<ReportElement> elements) {
		this.elements = elements;
	}

	public void addLabel(ReportLabel label) {
		if (label != null) {
			if (this.reportlabels == null) {
				this.reportlabels = new ArrayList<ReportLabel>();
			}
			reportlabels.add(label);
		}
	}

	public void setLabels(List<ReportLabel> labels) {
		this.reportlabels = labels;
	}

	/** Output Methods **/

	/** Utility Methods used by all bank reports **/
	public void sortAndCategorizeExpenses(List<ExpenseDao> allexpenses) {
		if (allexpenses == null)
			return;
		// build category lookup list
		Hashtable<Long, String> catlkup = new Hashtable<Long, String>();
		List<CategoryLevel> categories = categoryService
				.getCategoriesUpToLevel(1);
		if (categories != null) {
			for (CategoryLevel catlvl : categories) {

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

		// add main category to display expenses
		// fill in display catagory of all expenses
		for (ExpenseDao exp : allexpenses) {
			String dispcat = (String) catlkup.get(exp.getCatid());
			exp.setDispCat(dispcat);
		}

		// sort expenses by display category
		ExpenseComparator comp = new ExpenseComparator();
		comp.setSortType(ExpenseComparator.Sort.DispCategory);
		Collections.sort(allexpenses, comp);
	}

	public List<String> getMonthTagList(ExpenseCriteria criteria,
			String dateformat) {
		// initialize formatter
		SimpleDateFormat thisformat = new SimpleDateFormat(dateformat,
				Locale.US);
		// initialize the List
		List<String> tags = new ArrayList<String>();

		// initialize the Calendars
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		start.setTime(criteria.getDateStart());
		end.setTime(criteria.getDateEnd());
		/*
		 * start.set(Calendar.DAY_OF_MONTH, 1); start.set(Calendar.MONTH,
		 * Calendar.JANUARY);
		 */

		// iterate from first date to last date filling in keys along the way
		// cycle from end to start, adding as values to list
		while (start.before(end)) {
			String value = thisformat.format(start.getTime());
			tags.add(value);
			start.add(Calendar.MONTH, 1);
			start.set(Calendar.DAY_OF_MONTH, 1);
		}

		return tags;
	}

	protected ReportElement crunchNumbersTargets(ExpenseCriteria origcriteria,
			String month) {
		// fill criteria object
		ExpenseCriteria criteria = new ExpenseCriteria();
		criteria.setTransactionType(new Long(
				ExpenseCriteria.TransactionType.DEBITS));
		criteria.setExcludeNonExpense(origcriteria.getExcludeNonExpense());
		criteria.setDateEnd(origcriteria.getDateEnd());
		criteria.setDateStart(origcriteria.getDateStart());
		// Storage lists
		List<TargetProgressDisp> displays = new ArrayList<TargetProgressDisp>();
		Hashtable<Long, TargetDetailDao> targethash = new Hashtable<Long, TargetDetailDao>();

		// get Targets for month
		TargetGroupDao target = targetService.loadTargetForMonth(month);
		// place targets in Hashtable by categoryid
		List<TargetDetailDao> details = target.getTargetdetails();
		for (TargetDetailDao det : details) {
			Long catid = det.getCatid();
			targethash.put(catid, det);
		}

		// process categories
		// get all level 1 categories
		List<CategoryLevel> categories = categoryService
				.getCategoriesUpToLevel(1);

		// prepare chart data
		ChartData chart = new ChartData();
		ChartRow headers = new ChartRow();
		headers.addColumn("Category");
		headers.addColumn("Spent");
		headers.addColumn("Targeted");
		headers.addColumn("Status");
		chart.setHeaders(headers);

		// loop through categories
		double totalamount = 0;
		double totaltargeted = 0;
		for (CategoryLevel catlvl : categories) {
			List<CategoryLevel> subcats = categoryService.getAllSubcategories(catlvl
					.getCategory());
			// set categories in criteria
			subcats.add(catlvl);
			criteria.setCategoryLevelList(subcats);
			criteria.setShowSubcats(true);
			// retrieve totals
			List<CategorySummaryDisp> results = getExpenseTotalByMonth(criteria, catlvl
					.getCategory().getName());

			// loop through results
			if (results != null && results.size() > 0) {
				// if results are available, insert totals in Hashtable, add to
				// category total
				TargetProgressDisp cat = new TargetProgressDisp();
				cat.setCatName(catlvl.getCategory().getName());
				cat.setCatId(catlvl.getCategory().getId());
				// get info from results
				CategorySummaryDisp spentsum = (CategorySummaryDisp) results
						.get(0);
				cat.setAmountSpent(spentsum.getSum());
				// add target info
				TargetDetailDao detail = targethash.get(catlvl.getCategory()
						.getId());
				if (detail != null) {
					cat.setAmountTargeted(detail.getAmount().doubleValue());
				}
				displays.add(cat);

				double amtspent = cat.getAmountSpent();
				double amttargeted = cat.getAmountTargeted();
				totalamount += amtspent;
				totaltargeted += amttargeted;
				// add chart row
				ChartRow row = new ChartRow();
				row.addColumn(catlvl.getCategory().getName());
				row.addColumn(nf.format(amtspent));
				row.addColumn(nf.format(amttargeted));
				row.addColumn(cat.getStatusMessage());
				chart.addRow(row);
			}
		}
		// add total row to chart data
		String status = null;
		if (totaltargeted == 0) {
			status = "No Target";
		} else if (totaltargeted == totalamount) {
			status = "Target Met";
		} else if (totaltargeted > totalamount) {
			double percentage = totalamount / totaltargeted;
			percentage = Math.round(percentage * 100);
			status = percentage + "% of Target spent";
		} else {
			double percentage = totalamount / totaltargeted;
			percentage = Math.round(Math.abs(1 - percentage) * 100);
			status = "Target exceeded by "
					+ nf.format(totalamount - totaltargeted) + " Euros ("
					+ percentage + "%)";
		}
		ChartRow row = new ChartRow();
		row.addColumn("TOTAL");
		row.addColumn(nf.format(totalamount));
		row.addColumn(nf.format(totaltargeted));
		row.addColumn(status);
		chart.addRow(row);

		// get url for graph
		String graphurl = generateTargetGraph(displays);

		// put together return objects
		ReportElement re = new ReportElement();
		re.setChart(chart);
		re.addUrl(graphurl);

		return re;
	}

	protected double getPercentageThrYear(ExpenseCriteria criteria) {
		double percentageofyear = 100.0;
		ExpenseCriteria fullyearc = new ExpenseCriteria();
		fullyearc.setDateStart(criteria.getDateStart());
		Date calyearend = DateUtils.getEndOfCalendarYear(criteria
				.getDateStart());
		fullyearc.setDateEnd(calyearend);
		// current criteria contains full year - so use current criteria
		// to get full year day count (will be 365 or 366)
		int fullyear = getDayCount(fullyearc);
		// now, how many days are we into the year?
		ExpenseCriteria intomonth = new ExpenseCriteria();
		intomonth.setDateStart(criteria.getDateStart());
		intomonth.setDateEnd(new Date());
		int partofmonth = getDayCount(intomonth);

		// calculate percentage
		percentageofyear = (Math.round((double) partofmonth / (double) fullyear
				* 10000.0));
		percentageofyear = percentageofyear / 100;
		return percentageofyear;
	}

	public ReportElement crunchNumbersYearlyTargetProgress(
			ExpenseCriteria criteria, String lastdatetag,
			boolean iscurrentyear, Hashtable<Long, TargetDetailDao> targethash,
			double percentageofyear) {
		// prepare month hashtable (monthkey as key, and TargetProgressDisp as
		// value
		Hashtable<String, ArrayList<TargetProgressDisp>> runningTotals = prepareComparisonTable(
				criteria, lastdatetag);
		// get categories
		List<CategoryLevel> categories = categoryService
				.getCategoriesUpToLevel(1);
		// prepare displays point
		List<TargetProgressDisp> displayspoint = new ArrayList<TargetProgressDisp>();
		// loop through categories
		for (CategoryLevel catlvl : categories) {

			List<CategoryLevel> subcats = categoryService
					.getAllSubcategories(catlvl.getCategory());

			// set categories in criteria
			subcats.add(catlvl);
			criteria.setCategoryLevelList(subcats);
			criteria.setShowSubcats(true);

			TargetDetailDao cattarget = targethash.get(catlvl.getCategory()
					.getId());

			// only gather data if there's a target available for the category
			if (cattarget != null) {
				// retrieve totals
				List<CategorySummaryDisp> results = getExpenseTotalByYear(
						criteria, catlvl.getCategory().getName());

				// loop through results
				if (results != null && results.size() > 0) {
					// if results are available, insert totals in Hashtable, add
					// to
					// category total
					// MM TODO remove unused
					/*TargetProgressDisp cat = new TargetProgressDisp();
					cat.setCatName(catlvl.getCategory().getName());
					cat.setCatId(catlvl.getCategory().getId());*/
					// get info from results
					CategorySummaryDisp spentsum = results.get(0);
				/*	cat.setAmountSpent(spentsum.getSum());
					// add targeted amount to cat
					cat.setAmountTargeted(cattarget.getAmount().doubleValue());*/

					// add at this point graph information
					TargetProgressDisp catpt = new TargetProgressDisp();
					catpt.setCatName(catlvl.getCategory().getName());
					catpt.setCatId(catlvl.getCategory().getId());
					catpt.setAmountSpent(spentsum.getSum());
					catpt.setAmountTargeted(cattarget.getAmount().doubleValue());

					// add target info - calculated for the percentage of the
					// year
					double targetedtotal = catpt.getAmountTargeted();
					double targetednow = targetedtotal * percentageofyear
							/ 100.0;
					if (targetednow > 0) {
						catpt.setAmountTargeted(targetednow);
					}
					displayspoint.add(catpt);

					// calculate values throughout the year
					addValuesOverTime(criteria, cattarget, catpt, lastdatetag,
							runningTotals);

				}
			}
		}

/*
 * 		// calculate totals
		double totaltargeted = 0;
		double totalspent = 0;
		double pointtargeted = 0;

		for (TargetProgressDisp targ : displayspoint) {
			pointtargeted += targ.getAmountTargeted();
		}
		double statusamt = totalspent > totaltargeted ? totalspent
				- totaltargeted : totaltargeted - totalspent;
		String summary = totalspent > totaltargeted ? statusamt
				+ " over target" : statusamt + " under target";
		double statusamtpoint = totalspent > pointtargeted ? totalspent
				- pointtargeted : pointtargeted - totalspent;
		String fmtstatusamt = nf.format(statusamtpoint);
		String summarypoint = totalspent > pointtargeted ? fmtstatusamt
				+ " over target" : fmtstatusamt + " under target";

		ChartRow totalrow = new ChartRow();
		totalrow.addColumn(BankReportData.DispLabel.Summary);
		totalrow.addColumn(nf.format(totalspent));
		totalrow.addColumn(nf.format(totaltargeted));
		totalrow.addColumn(summary);

		ChartRow totalrowpoint = new ChartRow();
		totalrowpoint.addColumn(BankReportData.DispLabel.Summary);
		totalrowpoint.addColumn(nf.format(totalspent));
		totalrowpoint.addColumn(nf.format(pointtargeted));
		totalrowpoint.addColumn(summarypoint);

 */
		// get url for progress graph
		String graphprogressurl = generateProgressGraph(runningTotals);

		// put url in ReportElement
		ReportElement re = new ReportElement();
		re.addUrl(graphprogressurl);
return re;
	}
	
	protected String generateProgressGraph(
			Hashtable<String, ArrayList<TargetProgressDisp>> runningTotals) {
		// row keys... are categories
		// column keys... months - sort to prepare
		Set<String> months = runningTotals.keySet();
		List<String> monthlist = new ArrayList<String>();
		monthlist.addAll(months);

		UtilityComparator comp = new UtilityComparator();
		comp.setSortType(UtilityComparator.Sort.ByMonthDayYearStr);
		Collections.sort(monthlist, comp);

		// fill in dataset
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (Iterator<String> iter = monthlist.iterator(); iter.hasNext();) {
			String monthkey = iter.next();
			List<TargetProgressDisp> targetdisps = runningTotals.get(monthkey);
			// iterate through displays
			if (targetdisps != null) {
				for (Iterator<TargetProgressDisp> iterator = targetdisps.iterator(); iterator
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
		File imagefile = new File(reportCriteria.getImageDir() + filename);
		try {
			ChartUtilities.saveChartAsPNG(imagefile, chart, 550, 550);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return reportCriteria.getImageLink() + filename;
	}

	public ReportElement crunchNumbersSummary(ExpenseCriteria criteria,
			boolean avgbymonth) {
		ReportElement re = new ReportElement();

		// initialize ChartData
		ChartData chart = new ChartData();
		ChartRow headers = new ChartRow();
		headers.addColumn("Catagory");
		headers.addColumn("Spent");
		headers.addColumn("Percentage");
		chart.setHeaders(headers);

		// retrieve categories for level
		long breakoutlvl = reportCriteria.getBreakoutLevel().longValue();
		List<CategoryLevel> categories = categoryService
				.getCategoriesUpToLevel((int) breakoutlvl);

		Double charttotal = 0D;
		if (categories != null) {
			// loop through categories
			for (Iterator<CategoryLevel> iter = categories.iterator(); iter.hasNext();) {
				CategoryLevel catlvl = (CategoryLevel) iter.next();
				CategoryDao category = catlvl.getCategory();

				// create list to be retrieved (depends upon categorylevel
				// and relationship to breakout level)
				List<CategoryLevel> subcategories = null;
				if (catlvl.getLevel() < breakoutlvl) {
					// retrieve expenses belonging directly to this category
					// only
					subcategories = new ArrayList<CategoryLevel>();
					subcategories.add(catlvl);
				} else {
					// retrieve expenses belonging to this and all subcategories
					// get all subcategories for category
					subcategories = categoryService
							.getAllSubcategories(category);
					subcategories.add(catlvl); // add the category itself
				}
				// process categories
				ChartRow categoryRow = processCategories(criteria,
						category.getName(), subcategories);
				if (categoryRow.getColumnCount() > 0) {
					chart.addRow(categoryRow);
					// add to total
					String amountstr = categoryRow.getColumn(1);
					Number amount;
					try {
						amount = nf.parse(amountstr);
						charttotal += amount.doubleValue();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
			// end category loop
		}

		// pull all expenses without category
		Long origcattype = criteria.getCategorizedType();
		criteria.clearCategoryLists();
		criteria.setCategorizedType(new Long(
				ExpenseCriteria.CategorizedType.NOCATS));
		List<ExpenseDao> expenses = searchService.getExpenses(criteria);
		// loop through expenses, adding to no cat category summary
		if (expenses != null && expenses.size() > 0) {
			ChartRow row = new ChartRow();
			double total = 0;
			for (Iterator<ExpenseDao> iter = expenses.iterator(); iter.hasNext();) {
				ExpenseDao expense = (ExpenseDao) iter.next();
				Double amount = expense.getTranstotal();
				total += amount;
			}
			row.addColumn("No Category");
			row.addColumn(nf.format(total * -1));
			chart.addRow(row);
			// add to total
			charttotal += (total * -1);
		}
		// reset cattype
		origcattype = origcattype == null ? CategorizedType.ALL : origcattype;
		criteria.setCategorizedType(origcattype);

		// add percentage info for chart
		List<ChartRow> rows = chart.getRows();
		for (ChartRow row : rows) {
			String amountstr = row.getColumn(1);
			Number amount;
			try {
				amount = nf.parse(amountstr);
				double percentage = amount.doubleValue() / charttotal * 100.0;
				row.addColumn(nf.format(percentage) + "%");
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		String url = generateSummaryGraph(rows);

		// add total to list
		ChartRow totalrow = new ChartRow();
		totalrow.addColumn("TOTAL");
		totalrow.addColumn(nf.format(charttotal));
		totalrow.addColumn("100%");
		chart.addRow(totalrow);

		re.setChart(chart);
		re.addUrl(url);

		return re;
	}

	protected String generateCategoryGraph(
			List<CategorySummaryDisp> catsumdisps, String catname, double sum) {

		DefaultPieDataset dataset = new DefaultPieDataset();
		// sort catsumdisps
		CategorySummaryComparator comp = new CategorySummaryComparator();
		comp.setSortType(CategorySummaryComparator.Sort.ByAmount);
		Collections.sort(catsumdisps, comp);

		// make graph name
		if (sum > 0) {
			catname.concat("(" + nf.format(sum) + ")");
		}
		// populate dataset
		for (CategorySummaryDisp catsum : catsumdisps) {
			dataset.setValue(catsum.getCatName(),
					new Double(Math.abs(catsum.getSum())));
		}

		JFreeChart chart = ChartFactory.createPieChart(catname + "(" + sum
				+ ")", // chart
				// title
				dataset, // data
				false, // include legend
				true, false);

		PiePlot plot = (PiePlot) chart.getPlot();
		plot.setSectionOutlinesVisible(false);
		plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
		// plot.setNoDataMessage("No data available");
		plot.setCircular(false);
		plot.setLabelGap(0.02);

		String filecatname = catname.indexOf(" ") >= 0 ? catname.substring(0,
				catname.indexOf(" ") - 1) : catname;
		String filename = "fullmonth_" + filecatname + "_"
				+ (new Date()).getTime() + ".png";
		File imagefile = new File(reportCriteria.getImageDir() + filename);
		try {
			ChartUtilities.saveChartAsPNG(imagefile, chart, 250, 250);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return reportCriteria.getImageLink() + filename;

	}

	protected String generateSummaryGraph(List<ChartRow> datarows) {

		DefaultPieDataset dataset = new DefaultPieDataset();
		// sort catsumdisps
		ChartRowComparator comp = new ChartRowComparator(1, nf,
				ChartRowComparator.SortOrder.DESC);
		Collections.sort(datarows, comp);

		// populate dataset
		for (ChartRow row : datarows) {
			String valuestr = row.getColumn(1);
			Number value;
			try {
				value = nf.parse(valuestr);
				dataset.setValue(row.getColumn(0), value.doubleValue());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		JFreeChart chart = ChartFactory.createPieChart("Summary", // chart
				// title
				dataset, // data
				false, // include legend
				true, false);

		PiePlot plot = (PiePlot) chart.getPlot();
		plot.setSectionOutlinesVisible(false);
		plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
		// plot.setNoDataMessage("No data available");
		plot.setCircular(false);
		plot.setLabelGap(0.02);

		String filename = "fullmonthsummary_" + (new Date()).getTime() + ".png";
		File imagefile = new File(reportCriteria.getImageDir() + filename);
		try {
			ChartUtilities.saveChartAsPNG(imagefile, chart, 500, 500);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return reportCriteria.getImageLink() + filename;

	}

	/**
	 * prepares a Hashtable with keys starting from the beginning of each
	 * month.#
	 * 
	 * @param criteria
	 * @return
	 */
	protected Hashtable<String, ArrayList<TargetProgressDisp>> prepareComparisonTable(
			ExpenseCriteria criteria, String lastdatetag) {
		// prepare criteria for taglist call
		Date origbegin = criteria.getDateStart();
		Date origend = criteria.getDateEnd();
		// parse into date
		Calendar cal = Calendar.getInstance();
		cal.setTime(origbegin);
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		criteria.setDateStart(cal.getTime());

		// initialize the Hashtable
		Hashtable<String, ArrayList<TargetProgressDisp>> runningTotals = new Hashtable<String, ArrayList<TargetProgressDisp>>();
		// get month tag list
		List<String> monthtags = getMonthTagList(criteria, "MM-dd-yyyy");
		// remove January - first month of year doesn't make sense
		monthtags.remove(0);
		// add last date tag
		monthtags.add(lastdatetag);
		// put month tags into hashtable
		for (Iterator<String> iter = monthtags.iterator(); iter.hasNext();) {
			String monthtag = iter.next();
			runningTotals.put(monthtag, new ArrayList<TargetProgressDisp>());
		}

		// reset criteria
		criteria.setDateStart(origbegin);
		criteria.setDateEnd(origend);
		// return hash
		return runningTotals;
	}

	protected ChartRow processCategories(ExpenseCriteria criteria,
			String catname, List<CategoryLevel> subcategories) {
		// create ChartRow for category
		ChartRow row = new ChartRow();

		// pull expenses for categories
		criteria.setCategoryLevelList(subcategories);
		criteria.setShowSubcats(true);
		List<CategorySummaryDisp> expenses = searchService
				.getExpenseTotal(criteria);
		if (expenses != null && expenses.size() > 0) {
			double total = 0;
			// loop through category expenses
			for (CategorySummaryDisp catsum : expenses) {
				// add expense to category and total
				double amount = catsum.getSum();
				total += amount;
			}
			row.addColumn(catname);
			row.addColumn(nf.format(total * -1));
			// end expense loop
		}

		return row;
	}

	public ReportElement crunchNumbersCategory(ExpenseCriteria criteria,
			CategoryLevel cat, boolean numbymonth) {
		ExpenseCriteria catcriteria = criteria.clone();
		int daycount = getDayCount(criteria);

		// create headers
		ChartRow headers = new ChartRow();
		headers.addColumn(BankReportData.DispLabel.Subcategory);
		headers.addColumn(BankReportData.DispLabel.Amount);

		// get subcategories
		List<CategoryLevel> catlevels = categoryService.getAllSubcategories(cat
				.getCategory());
		CategorySummaryDisp totalsum = new CategorySummaryDisp("TOTAL",
				daycount);

		// do one query to grab all subcategories
		catlevels.add(cat);
		catcriteria.setCategoryLevelList(catlevels);
		catcriteria.setShowSubcats(true);

		List<CategorySummaryDisp> displays = new ArrayList<CategorySummaryDisp>();
		if (numbymonth) {
			displays = searchService
					.getExpenseTotalByMonthAndCategory(catcriteria);
		} else {
			displays = searchService
					.getExpenseTotalByYearAndCategory(catcriteria);
		}

		// go through displays, adding daycount, and summing total
		List<CategorySummaryDisp> results = new ArrayList<CategorySummaryDisp>();
		for (CategorySummaryDisp catsum : displays) {
			catsum.setAverageDivisor(daycount);
			totalsum.addExpenseAmt(new Double(catsum.getSum()));
			results.add(catsum);
		}

		// generate graph
		if (totalsum.getSum() == 0) {
			// no use going on - this category doesn't have anything
			return null;
		}
		double catsum = Math.round(totalsum.getSum() * 100.0) / 100.0;
		String graphurl = generateCategoryGraph(results, cat.getCategory()
				.getName(), catsum);

		// add total to results
		results.add(totalsum);

		// populate ReportElements
		ReportElement re = new ReportElement();
		ChartData chart = catSummaryToChart(results, headers);
		re.setChart(chart);
		re.addUrl(graphurl);

		return re;
	}

	protected List<CategorySummaryDisp> getExpenseTotalByMonth(
			ExpenseCriteria criteria, String dispcatname) {
		List<CategorySummaryDisp> displays = searchService
				.getExpenseTotalByMonth(criteria);
		for (CategorySummaryDisp catsum : displays) {
			catsum.setCatName(dispcatname);
		}
		return displays;
	}

	protected ReportElement crunchNumbersMonthlyComp(
			ExpenseCriteria origcriteria, boolean detailedchart,
			String dateformatstr) {
		{
			// initialize dateformatter
			SimpleDateFormat thisformat = new SimpleDateFormat(dateformatstr,
					Locale.US);
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
			Hashtable<String, List<CategorySummaryDisp>> results = new Hashtable<String, List<CategorySummaryDisp>>();
			ChartData chartdata = new ChartData();
			ChartRow headers = new ChartRow();
			headers.addColumn("Category");

			// prepare month tag lookup for detailed report
			Hashtable<String, Integer> taglkup = new Hashtable<String, Integer>();
			if (detailedchart) {
				List<String> monthtags = getMonthTagList(criteria,
						dateformatstr);
				int position = headers.getColumns().size();
				for (String tag : monthtags) {
					taglkup.put(tag, new Integer(position));
					position++;
					// add headers at the same time
					headers.addColumn(tag);
				}
			}
			int totalscolumn = headers.getColumnCount();
			if (detailedchart) {
				headers.addColumn("Total");
			} else {
				headers.addColumn("Amount");
			}
			headers.addColumn("Avg per Month");

			// loop through categories
			// get all level 1 categories
			List<CategoryLevel> categories = categoryService
					.getCategoriesUpToLevel(1);
			criteria.setShowSubcats(true);
			int monthcount = 0;
			for (CategoryLevel catlvl : categories) {
				List<CategoryLevel> subcats = categoryService
						.getAllSubcategories(catlvl.getCategory());
				// set categories in criteria
				subcats.add(catlvl);
				criteria.setCategoryLevelList(subcats);
				criteria.setShowSubcats(true);
				// retrieve totals
				List<CategorySummaryDisp> totals = getExpenseTotalByMonth(
						criteria, catlvl.getCategory().getName());

				// loop through results
				double rowtotal = 0;
				if (totals != null && totals.size() > 0) {
					// create ChartRow to hold data
					ChartRow row = new ChartRow();
					row.addColumn(catlvl.getCategory().getName());

					for (CategorySummaryDisp catsum : totals) {
						rowtotal += catsum.getSum();

						// month key
						// get date formatter
						String monthkey = thisformat.format(catsum
								.getSummaryDate());
						// if detailed report, add to correct column in row
						if (detailedchart) {
							Integer position = taglkup.get(monthkey);
							String amount = nf.format(catsum.getSum() * -1);
							row.addColumn(amount, position.intValue());
						}

						// insert in result hash
						if (!results.containsKey(monthkey)) {
							monthcount++;
						}
						List<CategorySummaryDisp> listfordate = results.get(monthkey);
						if (listfordate == null) {
							listfordate = new ArrayList<CategorySummaryDisp>();
						}
						listfordate.add(catsum);
						results.put(monthkey, listfordate);
					}

					// add category total to list
					String amount = nf.format(rowtotal * -1);
					row.addColumn(amount, totalscolumn);
					chartdata.addRow(row);
				}
			}

			// get url for graph
			int sorttype = UtilityComparator.Sort.ByMonth;
			if (dateformatstr.equals("MM-yyyy")) {
				sorttype = UtilityComparator.Sort.ByMonthYearStr;
			}
			String graphurl = generateYearToDateGraph(null, results, sorttype);

			// add average per month info, and totals row
			ChartRow total = new ChartRow();
			total.addColumn("TOTAL");
			int startcol = 1;

			Hashtable<Integer, Double> columntotals = new Hashtable<Integer, Double>();
			for (Iterator<ChartRow> iter = chartdata.getRows().iterator(); iter.hasNext();) {
				ChartRow row = (ChartRow) iter.next();

				Double rowsum = new Double(0);
				if (startcol != totalscolumn) {
					for (int i = startcol; i < totalscolumn; i++) {
						String val = row.getColumn(i);
						if (!val.equals("")) {
							Integer colkey = new Integer(i);
							Double colamt = 0D;
							try {
								Number amount = nf.parse(val);
								colamt = amount.doubleValue();
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							Double coltotal = columntotals.get(colkey);
							if (coltotal == null) {
								coltotal = 0D;
							}
							coltotal += colamt;
							columntotals.put(colkey, coltotal);
							rowsum += colamt;
						}
					}
				} else {
					String val = row.getColumn(totalscolumn);
					Number amount;
					try {
						amount = nf.parse(val);
						rowsum = amount.doubleValue();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				double avgpermonth = rowsum / monthcount;
				row.addColumn(nf.format(avgpermonth));
			}

			// now add the actual total row - with one "total" column, or many
			// (for the different months)
			double sumtotal = 0;
			if (startcol != totalscolumn) {
				for (int i = startcol; i < totalscolumn; i++) {
					Integer colkey = new Integer(i);
					Double coltotal = columntotals.get(colkey);
					if (coltotal == null) {
						coltotal = 0D;
					}
					total.addColumn(nf.format(coltotal), i);
					sumtotal += coltotal;
				}
			} else {
				for (Iterator<ChartRow> iter = chartdata.getRows().iterator(); iter
						.hasNext();) {
					ChartRow row = (ChartRow) iter.next();
					String val = row.getColumn(totalscolumn);
					Number amount;
					try {
						amount = nf.parse(val);
						sumtotal += amount.doubleValue();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}

			// and finally, the average per month for the totals row
			double avgpermonth = sumtotal / monthcount;
			total.addColumn(nf.format(sumtotal));
			total.addColumn(nf.format(avgpermonth));
			chartdata.addRow(total);

			// put together return objects
			ReportElement re = new ReportElement();
			chartdata.setHeaders(headers);
			re.setChart(chartdata);
			re.addUrl(graphurl);

			return re;
		}
	}

	protected String generateYearToDateGraph(String title,
			Hashtable<String, List<CategorySummaryDisp>> results, int sorttype) {
		// row keys... are categories
		// column keys... months - sort to prepare
		Set<String> months = results.keySet();
		List<String> monthlist = new ArrayList<String>();
		monthlist.addAll(months);
		UtilityComparator comp = new UtilityComparator();
		comp.setSortType(sorttype);
		Collections.sort(monthlist, comp);

		// fill in dataset
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (Iterator<String> iter = monthlist.iterator(); iter.hasNext();) {
			String monthkey = (String) iter.next();
			List<CategorySummaryDisp> catsumdisps = results.get(monthkey);
			// iterate through displays
			if (catsumdisps != null) {
				for (Iterator<CategorySummaryDisp> iterator = catsumdisps.iterator(); iterator
						.hasNext();) {
					CategorySummaryDisp catsum = (CategorySummaryDisp) iterator
							.next();
					dataset.addValue(Math.abs(catsum.getSum()),
							catsum.getCatName(), monthkey);
				}
			}
		}

		// create the chart...
		title = title != null ? title
				: sorttype == UtilityComparator.Sort.ByYearStr ? "Year "
						: "Month ";
		JFreeChart chart = ChartFactory.createStackedBarChart(title
				+ "Comparison", // chart title
				"Month", // domain axis label
				"Expense Sum", // range axis label
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
		File imagefile = new File(reportCriteria.getImageDir() + filename);
		try {
			ChartUtilities.saveChartAsPNG(imagefile, chart, 550, 550);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return reportCriteria.getImageLink() + filename;
	}

	protected List<CategorySummaryDisp> getExpenseTotalByYear(
			ExpenseCriteria criteria, String dispname) {
		List<CategorySummaryDisp> displays = searchService
				.getExpenseTotalByYear(criteria);
		for (CategorySummaryDisp catsum : displays) {
			catsum.setCatName(dispname);
		}
		return displays;
	}

	protected int getDayCount(ExpenseCriteria criteria) {
		int daycount = 0;
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		start.setTime(criteria.getDateStart());
		end.setTime(criteria.getDateEnd());
		while (start.before(end)) {
			start.add(Calendar.DAY_OF_MONTH, 1);
			daycount++;
		}
		return daycount;
	}

	protected ReportElement crunchNumbersYearlyComp(
			ExpenseCriteria origcriteria, boolean detailedchart) {
		{
			// initialize dateformatter
			SimpleDateFormat thisformat = new SimpleDateFormat("yyyy",
					Locale.US);
			ExpenseCriteria criteria = new ExpenseCriteria();
			criteria.setExcludeNonExpense(origcriteria.getExcludeNonExpense());
			criteria.setTransactionType(new Long(
					ExpenseCriteria.TransactionType.DEBITS));

			// set start and end dates
			// end date is first of next month
			Date enddate = origcriteria.getDateEnd();
			// start date is earliest possible, or 5 years ago
			Calendar cal = Calendar.getInstance();
			Date firstdate = searchService.getFirstTransDate();
			cal.setTime(firstdate);
			int firstpossyear = cal.get(Calendar.YEAR);
			SimpleDateFormat dateformat = new SimpleDateFormat("yyyy");
			Date endyeardate;
			try {
				endyeardate = dateformat.parse(reportCriteria.getYear());
				cal.setTime(endyeardate);
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			int desiredyear = cal.get(Calendar.YEAR);
			int beginyear = desiredyear - 5 > firstpossyear ? desiredyear - 5
					: firstpossyear;
			cal.set(Calendar.YEAR, beginyear);
			cal.set(Calendar.MONTH, Calendar.JANUARY);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			int yearcount = desiredyear - beginyear + 1;

			Date startdate = cal.getTime();
			criteria.setDateEnd(enddate);
			criteria.setDateStart(startdate);

			// Storage lists
			Hashtable<String, List<CategorySummaryDisp>> results = new Hashtable<String, List<CategorySummaryDisp>>();
			ChartData chartdata = new ChartData();
			ChartRow headers = new ChartRow();
			headers.addColumn("Category");

			// prepare month tag lookup for detailed report
			Hashtable<String, Integer> taglkup = new Hashtable<String, Integer>();
			if (detailedchart) {
				List<String> yeartags = new ArrayList<String>();
				for (int i = beginyear; i <= desiredyear; i++) {
					yeartags.add(i + "");
				}
				int position = headers.getColumns().size();
				for (String tag : yeartags) {
					taglkup.put(tag, new Integer(position));
					position++;
					// add headers at the same time
					headers.addColumn(tag);
				}
			}
			int totalscolumn = headers.getColumnCount();
			if (detailedchart) {
				headers.addColumn("Total");
			} else {
				headers.addColumn("Amount");
			}
			headers.addColumn("Avg per Month");

			// loop through categories
			// get all level 1 categories
			List<CategoryLevel> categories = categoryService
					.getCategoriesUpToLevel(1);
			for (CategoryLevel catlvl : categories) {
				List<CategoryLevel> subcats = categoryService
						.getAllSubcategories(catlvl.getCategory());
				// set categories in criteria
				subcats.add(catlvl);
				criteria.setCategoryLevelList(subcats);
				criteria.setShowSubcats(true);

				// retrieve totals
				List<CategorySummaryDisp> totals = getExpenseTotalByYear(
						criteria, catlvl.getCategory().getName());

				// loop through results
				double rowtotal = 0;
				if (totals != null && totals.size() > 0) {
					// create ChartRow to hold data
					ChartRow row = new ChartRow();
					row.addColumn(catlvl.getCategory().getName());

					for (CategorySummaryDisp catsum : totals) {
						rowtotal += catsum.getSum();

						// year key
						// get date formatter
						String yearkey = thisformat.format(catsum
								.getSummaryDate());
						// if detailed report, add to correct column in row
						if (detailedchart) {
							Integer position = taglkup.get(yearkey);
							String amount = nf.format(catsum.getSum() * -1);
							row.addColumn(amount, position.intValue());
						}

						// insert in result hash
						List<CategorySummaryDisp> listfordate = results
								.get(yearkey);
						if (listfordate == null) {
							listfordate = new ArrayList<CategorySummaryDisp>();
						}
						listfordate.add(catsum);
						results.put(yearkey, listfordate);
					}

					// add category total to list
					String amount = nf.format(rowtotal * -1);
					row.addColumn(amount, totalscolumn);
					chartdata.addRow(row);
				}
			}

			// get url for graph
			String graphurl = generateYearToDateGraph(null, results,
					UtilityComparator.Sort.ByYearStr);

			// totals row and total column
			ChartRow total = new ChartRow();
			total.addColumn("TOTAL");
			int startcol = 1;

			Hashtable<Integer, Double> columntotals = new Hashtable<Integer, Double>();
			for (Iterator<ChartRow> iter = chartdata.getRows().iterator(); iter.hasNext();) {
				ChartRow row = (ChartRow) iter.next();

				Double rowsum = new Double(0);
				// MM add avg per monthinfo
				if (startcol != totalscolumn) {
					for (int i = startcol; i < totalscolumn; i++) {
						String val = row.getColumn(i);
						Integer colkey = new Integer(i);
						Double colamt = 0D;
						try {
							Number amount = nf.parse(val);
							colamt = amount.doubleValue();
							double colavg = colamt / 12;
							// MM make avg smart enough for current year
							row.setColumn(val + " / " + nf.format(colavg), i);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Double coltotal = columntotals.get(colkey);
						if (coltotal == null) {
							coltotal = 0D;
						}
						coltotal += colamt;
						columntotals.put(colkey, coltotal);
						rowsum += colamt;
					}
				} else {
					String val = row.getColumn(totalscolumn);
					Number amount;
					try {
						amount = nf.parse(val);
						rowsum = amount.doubleValue();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				// MM make avg per month sharper&smarter
				double avgperyear = rowsum / yearcount / 12;
				row.addColumn(nf.format(avgperyear));
			}

			// now add the actual total row - with one "total" column, or many
			// (for the different months)
			double sumtotal = 0;
			if (startcol != totalscolumn) {
				for (int i = startcol; i < totalscolumn; i++) {
					Integer colkey = new Integer(i);
					Double coltotal = columntotals.get(colkey);
					if (coltotal == null) {
						coltotal = 0D;
					}
					double colavg = coltotal / 12;
					total.addColumn(
							nf.format(coltotal) + " / " + nf.format(colavg), i);
					sumtotal += coltotal;
				}
			} else {
				for (Iterator<ChartRow> iter = chartdata.getRows().iterator(); iter
						.hasNext();) {
					ChartRow row = (ChartRow) iter.next();
					String val = row.getColumn(totalscolumn);
					Number amount;
					try {
						amount = nf.parse(val);
						sumtotal += amount.doubleValue();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}

			// and finally, the average per month for the totals row
			double avgperyear = sumtotal / yearcount / 12;
			total.addColumn(nf.format(sumtotal));
			total.addColumn(nf.format(avgperyear));
			chartdata.addRow(total);

			// put together return objects
			ReportElement re = new ReportElement();
			chartdata.setHeaders(headers);
			re.setChart(chartdata);
			re.addUrl(graphurl);

			return re;
		}
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
	protected void addValuesOverTime(ExpenseCriteria criteria,
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
				percentageofyear = (Math.round((double) partofyear
						/ (double) 365 * 10000.0));
				percentageofyear = percentageofyear / 100;
				// get target for this date
				double wholeyeartarget = target.getAmount();
				double partialtarget = (wholeyeartarget * percentageofyear) / 100.0;

				// run totals for date
				List<CategorySummaryDisp> results = getExpenseTotalByYear(
						timecriteria, catname);
				if (results != null && results.size() > 0) {
					CategorySummaryDisp spentsum = results
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
					ArrayList<TargetProgressDisp> list = runningTotals
							.get(datekey);
					list.add(targetrun);
					runningTotals.put(datekey, list);
				} else {
					TargetProgressDisp targetrun = new TargetProgressDisp();
					targetrun.setAmountSpent(0.0);
					targetrun.setAmountTargeted(partialtarget);
					targetrun.setCatId(catpt.getCatId());
					targetrun.setCatName(catpt.getCatName());

					// add TargetProgressDisp object to list in Hashtable
					ArrayList<TargetProgressDisp> list = runningTotals
							.get(datekey);
					list.add(targetrun);
					runningTotals.put(datekey, list);
				}
		
		}
		// end loop through keys

	}

	private String generateTargetGraph(List<TargetProgressDisp> results) {
		final String exclabel = "Exceeded Target";
		final String tarlabel = "Target";
		final String spentlabel = "Spent";
		// row keys... are target, spent and exceeded
		// column keys... categories

		// fill in dataset
		List<Double> exc = new ArrayList<Double>();
		List<Double> targ = new ArrayList<Double>();
		List<Double> spe = new ArrayList<Double>();
		List<Double> notarget = new ArrayList<Double>();
		List<String> categories = new ArrayList<String>();

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		int i = 0;
		for (TargetProgressDisp target : results) {
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
			String catname = categories.get(j);
			// add spent
			Double spent = spe.get(j);
			double val = spent != null ? spent.doubleValue() : 0;
			dataset.addValue(val, spentlabel, catname);
			// add targeted
			Double targeted = targ.get(j);
			val = targeted != null ? targeted.doubleValue() : 0;
			dataset.addValue(val, tarlabel, catname);
			// add exceeded
			Double excval = exc.get(j);
			val = excval != null ? excval.doubleValue() : 0;
			dataset.addValue(val, exclabel, catname);
			// add no target
			Double notargval = notarget.get(j);
			val = notargval != null ? notargval.doubleValue() : 0;
			dataset.addValue(val, "No Target", catname);
		}

		// create the chart...
		JFreeChart chart = ChartFactory.createStackedBarChart("Target Status", // chart
				// title
				"Category", // domain axis label
				"Status", // range axis label
				dataset, // data
				PlotOrientation.HORIZONTAL, // orientation
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
		renderer.setSeriesPaint(0, Color.BLUE);
		renderer.setSeriesPaint(1, Color.YELLOW);
		renderer.setSeriesPaint(2, Color.RED);
		renderer.setSeriesPaint(3, ChartColor.LIGHT_CYAN);

		CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions
				.createUpRotationLabelPositions(Math.PI / 6.0));

		// save graph and return url
		String filename = "monthlytargets_" + (new Date()).getTime() + ".png";
		File imagefile = new File(reportCriteria.getImageDir() + filename);
		try {
			ChartUtilities.saveChartAsPNG(imagefile, chart, 550, 550);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return reportCriteria.getImageLink() + filename;

	}

	protected ChartData catSummaryToChart(List<CategorySummaryDisp> results,
			ChartRow headers) {
		ChartData cd = new ChartData();
		cd.setHeaders(headers);
		if (results != null) {
			for (CategorySummaryDisp sum : results) {
				ChartRow row = new ChartRow();
				row.addColumn(sum.getCatName());
				row.addColumn(nf.format(sum.getSum()));
				cd.addRow(row);
			}

		}
		return cd;
	}

	protected ChartData targetProgressToChart(List<TargetProgressDisp> results,
			ChartRow headers) {
		ChartData cd = new ChartData();
		cd.setHeaders(headers);
		if (results != null) {
			for (TargetProgressDisp sum : results) {
				ChartRow row = new ChartRow();
				row.addColumn(sum.getCatName());
				row.addColumn(nf.format(sum.getAmountSpent()));
				row.addColumn(nf.format(sum.getAmountTargeted()));
				row.addColumn(sum.getStatusMessage());
				cd.addRow(row);
			}

		}
		return cd;
	}
}