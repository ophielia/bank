package meg.bank.bus.report;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import meg.bank.bus.CategoryLevel;
import meg.bank.bus.CategoryService;
import meg.bank.bus.ExpenseCriteria;
import meg.bank.bus.SearchService;
import meg.bank.bus.TargetService;
import meg.bank.bus.dao.ExpenseDao;
import meg.bank.bus.dao.TargetDetailDao;
import meg.bank.bus.dao.TargetGroupDao;
import meg.bank.bus.report.utils.TargetProgressDisp;

import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

public class MonthlyTargetsReport extends AbstractReport{



	public MonthlyTargetsReport(ReportCriteria reportCriteria,
			SearchService searchService, CategoryService categoryService,
			TargetService targetService) {
		super(reportCriteria, searchService, categoryService, targetService);
	}



	public Map<String,Object> crunchNumbers() {
		// fill criteria object
		ExpenseCriteria criteria = new ExpenseCriteria();
		criteria.setTransactionType(new Long(ExpenseCriteria.TransactionType.DEBITS));
		String month = getReportCriteria().getMonth();
		// parse into date
		SimpleDateFormat dateformat = new SimpleDateFormat("MM-yyyy");
		Date start;
		boolean iscurrentmonth=false;
		try {
			start = dateformat.parse(month);
			Calendar cal = Calendar.getInstance();
			Calendar comp = Calendar.getInstance();
			cal.setTime(start);
			iscurrentmonth = cal.get(Calendar.MONTH)==comp.get(Calendar.MONTH);
			// get first of month, first of next month
			cal.set(Calendar.DAY_OF_MONTH, 1);
			Date startdate = cal.getTime();
			cal.roll(Calendar.MONTH, 1);
			Date enddate = cal.getTime();
			// set in criteria
			criteria.setDateStart(startdate);
			criteria.setDateEnd(enddate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		criteria.setExcludeNonExpense(getReportCriteria().getExcludeNonExpense());

		// calculate progress in month (how many days into month?)
		// only makes sense, if we're looking at the current month
		double percentageofmonth = 100.0;
		if (iscurrentmonth) {
			// current criteria contains full month - so use current criteria
			// to get full month day count
			int fullmonth = getDayCount(criteria);
			// now, how many days are we into the month?
			ExpenseCriteria intomonth = new ExpenseCriteria();
			intomonth.setDateStart(criteria.getDateStart());
			intomonth.setDateEnd(new Date());
			int partofmonth = getDayCount(intomonth);
			
			// calculate percentage
				percentageofmonth = (Math.round((double)partofmonth/(double)fullmonth * 10000.0));
				percentageofmonth = percentageofmonth/100;
				
			
		}
		
		// Storage lists
		List<TargetProgressDisp> displays = new ArrayList<TargetProgressDisp>();
		Hashtable<Long,TargetDetailDao> targethash = new Hashtable<Long,TargetDetailDao>();

		// get Targets for month
		TargetGroupDao target = targetService.loadTargetForMonth(month);
		// place targets in Hashtable by categoryid
		List<TargetDetailDao> details = target.getTargetdetails();
		for (Iterator iter = details.iterator(); iter.hasNext();) {
			TargetDetailDao det = (TargetDetailDao) iter.next();
			Long catid = det.getCatid();
			targethash.put(catid, det);
		}

		// process categories
		// get all level 1 categories
		List<CategoryLevel> categories = categoryService.getCategoriesUpToLevel(1);

		// loop through categories
		criteria.setShowSubcats(true);
		for (CategoryLevel catlvl:categories) {
			List<CategoryLevel> subcats = categoryService.getAllSubcategories(
					catlvl.getCategory());
			// set categories in criteria
			subcats.add(catlvl);
			criteria.setCategoryLevelList(subcats);
			

			// retrieve totals
			List<CategorySummaryDisp> results = getExpenseTotalByMonth(
					criteria, catlvl.getCategory().getName());

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
			}
		}

		// calculate totals
		double totaltargeted = 0;
		double totalspent = 0;
		for (TargetProgressDisp targ :displays) {
			totaltargeted+=targ.getAmountTargeted();
			totalspent+=targ.getAmountSpent();
		}
		double statusamt = totalspent>totaltargeted?totalspent-totaltargeted:totaltargeted-totalspent;
		String summary = totalspent>totaltargeted?statusamt + " over target": statusamt + " under target";
		// get url for graph
		String graphurl = generateGraph(displays);

		
		// get all expenses
		criteria.clearCategoryLists();
		criteria.setShowSubcats(false);
		List<ExpenseDao> expenses = searchService.getExpenses(criteria);
		// sort and categorize expenses
		sortAndCategorizeExpenses(expenses);
		
		// put together return objects
		HashMap<String,Object> model = new HashMap<String,Object>();
		// summary info and graph
		model.put("totallist", displays);
		model.put("month", month);
		model.put("expenses", expenses);
		model.put("percentageofmonth", new Double(percentageofmonth));
		model.put("graphpath", graphurl);
		model.put("reportname", "Monthly Targets");
		model.put("totaltargeted", new Double(totaltargeted));
		model.put("totalspent", new Double(totalspent));
		model.put("summary", summary);

		return model;
	}


	private String generateGraph(List results) {
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
		int i=0;
		for (Iterator iter = results.iterator(); iter.hasNext();) {
			TargetProgressDisp target = (TargetProgressDisp) iter.next();
			String catname = target.getCatName();
			categories.add(i,catname);
		
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
				spent =target.getAmountSpent();
				targeted = target.getAmountTargeted()- target.getAmountSpent();
			}

			// add to dataset
			exc.add(i,new Double(exceeded));
			targ.add(i,new Double(targeted));
			spe.add(i,new Double(spent));
			targ.add(i,new Double(targeted));
			notarget.add(i,new Double(notarg));
			i++;
		}

		// fill in dataset
		for (int j=0;j<categories.size();j++) {
			String catname = (String) categories.get(j);
			// add spent
			Double spent = (Double) spe.get(j);
			double val = spent!=null?spent.doubleValue():0;
			dataset.addValue(val, spentlabel, catname);
			// add targeted
			Double targeted = (Double) targ.get(j);
			val = targeted!=null?targeted.doubleValue():0;
			dataset.addValue(val, tarlabel, catname);
			// add exceeded
			Double excval = (Double) exc.get(j);
			val = excval!=null?excval.doubleValue():0;
			dataset.addValue(val, exclabel, catname);			
			// add no target
			Double notargval = (Double) notarget.get(j);
			val = notargval!=null?notargval.doubleValue():0;
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
		StackedBarRenderer renderer = (StackedBarRenderer ) plot.getRenderer();
		renderer.setSeriesPaint(0, ChartColor.BLUE);
        renderer.setSeriesPaint(1, ChartColor.YELLOW);
        renderer.setSeriesPaint(2, ChartColor.RED);
        renderer.setSeriesPaint(3, ChartColor.LIGHT_CYAN);

		
		CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions
				.createUpRotationLabelPositions(Math.PI / 6.0));

		// save graph and return url
		String filename = "monthlytargets_" + (new Date()).getTime() + ".png";
		File imagefile = new File(getReportCriteria().getImageDir() + filename);
		try {
			ChartUtilities.saveChartAsPNG(imagefile, chart, 550, 550);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return reportCriteria.getImageLink() + filename;

	}



	
	public String getResultView(String outputtype) {
		return "targetstatusreport";
	}




}
