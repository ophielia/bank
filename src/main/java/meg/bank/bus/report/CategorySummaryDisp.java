package meg.bank.bus.report;

import java.util.Date;

public class CategorySummaryDisp {

	private String catName;

	private int averageDivisor;

	private double sum;

	private int expCount;

	private double average;

	private double averagePerDay;

	private Date summaryDate;

	private double percentageTotal;

	public CategorySummaryDisp(String catname, int daycount) {
		this.catName = catname;
		this.averageDivisor = daycount;
	}

	public CategorySummaryDisp() {

	}

	public void addExpenseAmt(Double amount) {
		sum += amount.doubleValue();
		expCount++;
		recalculate();
	}

	private void recalculate() {
		// recalculate
		average = sum / (double) expCount;
		averagePerDay = sum / (double) averageDivisor;
	}
	
	public double getAverage() {
		return average;
	}

	public void setAverage(double average) {
		this.average = average;
	}

	public double getAveragePerDivisor() {
		return averagePerDay;
	}

	public void setAveragePerDivisor(double averagePerDay) {
		this.averagePerDay = averagePerDay;
	}

	public double getSum() {
		return sum;
	}

	public void setSum(double sum) {
		this.sum = sum;
	}

	public String getCatName() {
		return catName;
	}

	public void setSummaryDate(Date date) {
		this.summaryDate = date;
	}

	public Date getSummaryDate() {
		return summaryDate;
	}

	public void setCatName(String dispname) {
		this.catName = dispname;
	}

	public void setAverageDivisor(int monthcount) {
		this.averageDivisor = monthcount;
		recalculate();
	}

	public void setPercentageTotal(double total) {
		this.percentageTotal = total;
	}

	public double getPercentageOfTotal() {
		return (this.sum/this.percentageTotal)*100.0;
	}
}
