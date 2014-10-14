package meg.bank.bus.report;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
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
import java.util.Map;
import java.util.Set;

import meg.bank.bus.CategoryLevel;
import meg.bank.bus.CategoryService;
import meg.bank.bus.ExpenseCriteria;
import meg.bank.bus.SearchService;
import meg.bank.bus.TargetService;
import meg.bank.bus.dao.TargetDetailDao;
import meg.bank.bus.dao.TargetGroupDao;
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
import org.jfree.data.category.DefaultCategoryDataset;

public class YearlyTargetStatus extends AbstractReport{



	public YearlyTargetStatus(ReportCriteria reportCriteria,
			SearchService searchService, CategoryService categoryService,
			TargetService targetService) {
		super(reportCriteria, searchService, categoryService, targetService);
	}


	public Map crunchNumbers() {
		// fill criteria object
		ExpenseCriteria criteria = new ExpenseCriteria();
		criteria.setTransactionType(new Long(
				ExpenseCriteria.TransactionType.DEBITS));
		String year = getReportCriteria().getYear();
		// parse into date
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy");
		Date start;
		boolean iscurrentyear = false;
		String lastdatetag =null;
		Date calyearend = null;
		try {
			start = dateformat.parse(year);
			Calendar cal = Calendar.getInstance();
			Calendar comp = Calendar.getInstance();
			cal.setTime(start);
			iscurrentyear = cal.get(Calendar.YEAR) == comp.get(Calendar.YEAR);
			// get first of year (Calendar cal), first of next year (Calendar
			// comp)
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.MONTH, Calendar.JANUARY);
			Date startdate = cal.getTime();
			cal.roll(Calendar.YEAR, 1);
			calyearend = cal.getTime();
			criteria.setDateEnd(calyearend);
			lastdatetag = determineLastDateTag(criteria, iscurrentyear);
			Date end = daydateformat.parse(lastdatetag);
			// set in criteria
			criteria.setDateStart(startdate);
			criteria.setDateEnd(end);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		criteria.setExcludeNonExpense(true);

		// calculate progress in year (how many days into the year)
		// only makes sense, if we're looking at the current year
		double percentageofyear = 100.0;
		if (iscurrentyear) {
			ExpenseCriteria fullyearc = new ExpenseCriteria();
			fullyearc.setDateStart(criteria.getDateStart());
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
			percentageofyear = (Math.round((double) partofmonth
					/ (double) fullyear * 10000.0));
			percentageofyear = percentageofyear / 100;

		}

		// Storage lists
		List<TargetProgressDisp> displayspoint = new ArrayList<TargetProgressDisp>();
		List<TargetProgressDisp> displays = new ArrayList<TargetProgressDisp>();
		Hashtable<Long,TargetDetailDao> targethash = new Hashtable<Long,TargetDetailDao> ();

		// get Targets for year
		TargetGroupDao target = targetService.loadTargetForYear(year);
		// place targets in Hashtable by categoryid
		List<TargetDetailDao> details = target.getTargetdetails();
		for (TargetDetailDao det:details) {
			Long catid = det.getCatid();
			targethash.put(catid, det);
		}

		// process categories
		// get all level 1 categories
		List<CategoryLevel> categories = categoryService
				.getCategoriesUpToLevel(1);

		// prepare month hashtable (monthkey as key, and TargetProgressDisp as
		// value
		
		Hashtable<String, ArrayList<TargetProgressDisp>> runningTotals = prepareComparisonTable(
				criteria, lastdatetag);

		// loop through categories
		for (CategoryLevel catlvl:categories) {

			
			List<CategoryLevel> subcats = categoryService.getAllSubcategories(
					catlvl.getCategory());

			// set categories in criteria
			subcats.add(catlvl);
			criteria.setCategoryLevelList(subcats);
			criteria.setShowSubcats(true);

			// retrieve totals
			List<CategorySummaryDisp> results = getExpenseTotalByYear(criteria,
					catlvl.getCategory().getName());

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
				TargetDetailDao detail = (TargetDetailDao) targethash
						.get(catlvl.getCategory().getId());
				if (detail != null) {
					cat.setAmountTargeted(detail.getAmount().doubleValue());
				}
				displays.add(cat);

				// add at this point graph information
				TargetProgressDisp catpt = new TargetProgressDisp();
				catpt.setCatName(catlvl.getCategory().getName());
				catpt.setCatId(catlvl.getCategory().getId());
				catpt.setAmountSpent(cat.getAmountSpent());

				// add target info - calculated for the percentage of the year
				double targetedtotal = cat.getAmountTargeted();
				double targetednow = targetedtotal * percentageofyear / 100.0;
				if (targetednow > 0) {
					catpt.setAmountTargeted(targetednow);
				}
				displayspoint.add(catpt);

				// calculate values throughout the year
				addValuesOverTime(criteria, detail, catpt, lastdatetag,
						runningTotals);

			}
		}

		// calculate totals
		double totaltargeted = 0;
		double totalspent = 0;
		double pointtargeted = 0;
		for (TargetProgressDisp targ :displays) {
			totaltargeted += targ.getAmountTargeted();
			totalspent += targ.getAmountSpent();
		}
		for (TargetProgressDisp targ :displayspoint) {
			pointtargeted += targ.getAmountTargeted();
		}
		double statusamt = totalspent > totaltargeted ? totalspent
				- totaltargeted : totaltargeted - totalspent;
		String summary = totalspent > totaltargeted ? statusamt
				+ " over target" : statusamt + " under target";
		double statusamtpoint = totalspent > pointtargeted ? totalspent
				- pointtargeted : pointtargeted - totalspent;
		String summarypoint = totalspent > pointtargeted ? statusamtpoint
				+ " over target" : statusamtpoint + " under target";
		// get url for graph
		String graphurl = generateGraph("Target Status", displays, false);

		// get url for at this point graph
		String graphpointurl = generateGraph("Target Status (as of today)",
				displayspoint, true);

		// get url for progress graph
		String graphprogressurl = generateProgressGraph(runningTotals);

		// run category breakouts for all main categories
		criteria.setCategorizedType(new Long(
				ExpenseCriteria.CategorizedType.ALL));
		criteria.clearCategoryLists();
		List<ReportElements> allcategory = new ArrayList<ReportElements>();

		
		if (categories != null) {
			for (CategoryLevel catlvl : categories) {
				ReportElements catre = newCrunchNumbersCategory(criteria,
						catlvl, false);
				if (catre != null) {
					catre.setName(catlvl.getCategory().getName());
					allcategory.add(catre);
				}
			}
		}

		// put together return objects
		HashMap<String,Object> model = new HashMap<String,Object>();
		// summary info and graph
		model.put("pointlist", displayspoint);
		model.put("totallist", displays);
		model.put("year", year);
		model.put("percentageofyear", new Double(percentageofyear));
		model.put("graphpointpath", graphpointurl);
		model.put("graphprogressurl", graphprogressurl);
		model.put("graphpath", graphurl);
		// add all category breakout info
		model.put("categories", allcategory);
		model.put("reportname", "Yearly Targets");
		model.put("pointtargeted", new Double(pointtargeted));
		model.put("totaltargeted", new Double(totaltargeted));
		model.put("totalspent", new Double(totalspent));
		model.put("summary", summary);
		model.put("summarypoint", summarypoint);

		return model;
	}

	public String getResultView(String outputtype) {
		return "yearlytargetstatusreport";
	}

	public Hashtable getRequiredParameters() {
		Hashtable req = new Hashtable();
		req.put(new Integer(ReportCriteria.Parameters.excludenonexp),
				new Boolean(false));
		req.put(new Integer(ReportCriteria.Parameters.daterange), new Boolean(false));
		req.put(new Integer(ReportCriteria.Parameters.breakoutlvl), new Boolean(false));
		req.put(new Integer(ReportCriteria.Parameters.category), new Boolean(false));
		req.put(new Integer(ReportCriteria.Parameters.monthlist), new Boolean(false));
		req.put(new Integer(ReportCriteria.Parameters.yearlist), new Boolean(true));
		req.put(new Integer(ReportCriteria.Parameters.comparetype), new Boolean(false));
		return req;
	}

	protected String generateGraph(String graphname, List<TargetProgressDisp> results,
			boolean ishoriz) {
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
		for (TargetProgressDisp target:results) {
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
		File imagefile = new File(reportCriteria.getImageDir() + filename);
		try {
			ChartUtilities.saveChartAsPNG(imagefile, chart, 550, 550);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return reportCriteria.getImageLink() + filename;

	}

	private String generateProgressGraph(
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

			if (datekey.compareTo(lastdatetag) == 0) {
				// or, if this is the last date...
				// save info in TargetProgressDisp
				TargetProgressDisp targetrun = new TargetProgressDisp();
				targetrun.setAmountSpent(catpt.getAmountSpent());
				targetrun.setAmountTargeted(catpt.getAmountTargeted());
				targetrun.setCatId(catpt.getCatId());
				targetrun.setCatName(catpt.getCatName());

				// add TargetProgressDisp object to list in Hashtable
				ArrayList<TargetProgressDisp> list = runningTotals.get(datekey);
				list.add(targetrun);
				runningTotals.put(datekey, list);
			} else {
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
				List<CategorySummaryDisp> results = getExpenseTotalByYear(timecriteria, catname);
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
		}
		// end loop through keys

	}

	/**
	 * Determines whether the last date in the running totals is today's date
	 * (as in the case of the current year) or December 31st, as in the case of
	 * a previous year
	 * 
	 * @param criteria
	 * @param iscurrentyear
	 * @return
	 */
	private String determineLastDateTag(ExpenseCriteria criteria,
			boolean iscurrentyear) {
		if (iscurrentyear) {
			// return current date
			return daydateformat.format(new Date());
		} else {
			// return the last day of the year
			return daydateformat.format(criteria.getDateEnd());
		}
	}

	/**
	 * prepares a Hashtable with keys starting from the beginning of each
	 * month.#
	 * 
	 * @param criteria
	 * @return
	 */
	private Hashtable<String, ArrayList<TargetProgressDisp>> prepareComparisonTable(
			ExpenseCriteria criteria, String lastdatetag) {
		// prepare criteria for taglist call
		Date origbegin = criteria.getDateStart();
		Date origend = criteria.getDateEnd();
		// parse into date
		Calendar cal = Calendar.getInstance();
		Calendar comp = Calendar.getInstance();
		cal.setTime(origbegin);
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		criteria.setDateStart(cal.getTime());

		// initialize the Hashtable
		Hashtable<String, ArrayList<TargetProgressDisp>> runningTotals = new Hashtable<String, ArrayList<TargetProgressDisp>>();
		// get month tag list
		List monthtags = getMonthTagList(criteria, "MM-dd-yyyy");
		// remove January - first month of year doesn't make sense
		monthtags.remove(0);
		// add last date tag
		monthtags.add(lastdatetag);
		// put month tags into hashtable
		for (Iterator iter = monthtags.iterator(); iter.hasNext();) {
			String monthtag = (String) iter.next();
			runningTotals.put(monthtag, new ArrayList<TargetProgressDisp>());
		}

		// reset criteria
		criteria.setDateStart(origbegin);
		criteria.setDateEnd(origend);
		// return hash
		return runningTotals;
	}


}
