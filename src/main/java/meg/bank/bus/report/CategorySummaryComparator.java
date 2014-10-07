package meg.bank.bus.report;

import java.util.Comparator;

public class CategorySummaryComparator implements Comparator {

	public final static class Sort {
		public final static int ByDate=1;
		public final static int ByAmount=2;
	}
	
	private int sortby = Sort.ByDate;
	
	public int compare(Object arg0, Object arg1) {
		CategorySummaryDisp disp1=(CategorySummaryDisp)arg0;
		CategorySummaryDisp disp2=(CategorySummaryDisp)arg1;
		// TODO add Date Utils sort here!!
		if (sortby==Sort.ByDate) {
			return sortbydate(disp1,disp2);
		} else if (sortby==Sort.ByAmount) {
			return sortbyamount(disp1,disp2);
		}
		
		
		
		// default
		return 0;
	}
	
	public void setSortType(int sortkey) {
		this.sortby=sortkey;
	}

	private int sortbydate(CategorySummaryDisp disp1,CategorySummaryDisp disp2) {
		if (disp1.getSummaryDate().before(disp2.getSummaryDate())) {
			return -1;
		} else if (disp1.getSummaryDate().after(disp2.getSummaryDate())) {
		return 1;
		}
		return 0;
	}
	
	private int sortbyamount(CategorySummaryDisp disp1,CategorySummaryDisp disp2) {
		if (disp1.getSum() < disp2.getSum()) {
			return -1;
		} else if (disp1.getSum() > disp2.getSum()) {
		return 1;
		}
		return 0;
	}	
}
