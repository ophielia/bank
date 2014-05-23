package meg.bank.bus;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

public class ExpenseCriteria {

	public final class Month {
		public static final int CURRENT = 1;

		public static final int LAST = 2;

		public static final int BEFORELAST = 3;

		public static final int DATERANGE = 4;

		public static final int ALL = 0;

		public static final int THISWEEK = 5;

		public static final int LASTWEEK = 6;

		public static final int THISYEAR = 7;

		public static final int LASTYEAR = 8;
	}

	public final class CategorizedType {
		public static final int NOCATS = 1;

		public static final int ONLYCATS = 2;

		public static final int ALL = 3;
	}
	
	public final class TransactionType {
		public static final int DEBITS = 1;
		public static final int CREDITS = 2;
		public static final int ALL = 3;
		
	}

	public final class CompareType {
		public static final int LASTMONTHS = 1;
		public static final int CALYEAR = 2;
		public static final int ALL = 3;
	}
	public static final String DateRangeLkup = "daterange";

	public static final String CatTypeLkup = "cattype";

	public static final String TransTypeLkup= "transtype";
	public static final String ClientKeyLkup= "source";
	public static final String CompareTypeLkup= "comparetype";
	
	private static Hashtable<Integer, Long> weekhelper;

	static {
		weekhelper = new Hashtable<Integer, Long>();
		weekhelper.put(new Integer(Calendar.SUNDAY), new Long(-8));
		weekhelper.put(new Integer(Calendar.MONDAY), new Long(-2));
		weekhelper.put(new Integer(Calendar.TUESDAY), new Long(-3));
		weekhelper.put(new Integer(Calendar.WEDNESDAY), new Long(-4));
		weekhelper.put(new Integer(Calendar.THURSDAY), new Long(-5));
		weekhelper.put(new Integer(Calendar.FRIDAY), new Long(-6));
		weekhelper.put(new Integer(Calendar.SATURDAY), new Long(-7));
	}

	private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");

	private Date startdate;

	private Date enddate;

	private Long categorizedtype;

	private Long categoryid;

	private int dayCount;

	private List<CategoryLevel> categoryLevelList;

	private Boolean excludeNonExpense;
	
	private Long transactionType;
	private Long source;

	private Long compareType;

	private Boolean showsubcategories;

	public void setDateRangeByType(Long dateRangeType) {
		// return if "DATERANGE"
		if (dateRangeType.longValue() == Month.DATERANGE) {
			return;
		}
		// set start and end dates
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		if (dateRangeType.longValue() == Month.CURRENT) {
			end.add(Calendar.MONTH, 1);
		} else if (dateRangeType.longValue() == Month.LAST) {
			start.add(Calendar.MONTH, -1);
		} else if (dateRangeType.longValue() == Month.BEFORELAST) {
			start.add(Calendar.MONTH, -2);
			end.add(Calendar.MONTH, -1);
		} else if (dateRangeType.longValue() == Month.THISWEEK) {
			// get current day
			int currentday = start.get(Calendar.DAY_OF_WEEK);

			// get last Saturday
			Long toroll = (Long) weekhelper.get(new Integer(currentday));
			start.add(Calendar.DAY_OF_MONTH, (int) toroll.longValue());
			// get next Saturday
			end = (Calendar) start.clone();
			end.add(Calendar.DAY_OF_MONTH, 7);

		} else if (dateRangeType.longValue() == Month.LASTWEEK) {
			// get current day
			int currentday = start.get(Calendar.DAY_OF_WEEK);

			// get last Saturday
			Long toroll = (Long) weekhelper.get(new Integer(currentday));
			start.add(Calendar.DAY_OF_MONTH, (int) toroll.longValue());
			start.add(Calendar.DAY_OF_MONTH, -7);
			// get next Saturday
			end = (Calendar) start.clone();
			end.add(Calendar.DAY_OF_MONTH, 7);

		}

		if (dateRangeType.longValue() != Month.THISWEEK
				&& dateRangeType.longValue() != Month.LASTWEEK) {
			// set dates to first of month
			start.set(Calendar.DAY_OF_MONTH, 1);
			end.set(Calendar.DAY_OF_MONTH, 1);
		}
		
		if (dateRangeType.longValue()== Month.THISYEAR) {
			// set date to january 1st
			start.set(Calendar.DAY_OF_MONTH, 1);
			start.set(Calendar.MONTH, Calendar.JANUARY);
			end.set(Calendar.DAY_OF_MONTH, 1);
			end.add(Calendar.MONTH, 1);
		}
		
		if (dateRangeType.longValue()== Month.LASTYEAR) {
			// set date to january 1st
			start.add(Calendar.YEAR, -1);
			start.set(Calendar.DAY_OF_MONTH, 1);
			start.set(Calendar.MONTH, Calendar.JANUARY);
			end.set(Calendar.DAY_OF_MONTH, 31);
			end.set(Calendar.MONTH, Calendar.DECEMBER);
			end.set(Calendar.YEAR, start.get(Calendar.YEAR));
		}		

		// set dates
		setDateStart(start.getTime());
		setDateEnd(end.getTime());

	}

	public void setCategorizedType(Long categorizedType) {
		categorizedtype = categorizedType;

	}

	public Long getCategorizedType() {
		return categorizedtype;
	}

	public void setCategory(Long category) {
		categoryid = category;

	}

	public Long getCategory() {
		return categoryid;
	}

	public void setDateEnd(Date end) {
		enddate = end;

	}

	public Date getDateEnd() {
		return enddate;
	}

	public void setDateStart(Date start) {
		startdate = start;

	}

	public Date getDateStart() {
		return startdate;
	}

	public String getDateStartAsString() {
		return dateformat.format(startdate);
	}

	public String getDateEndAsString() {
		return dateformat.format(enddate);
	}



	public int getDayCount() {
		return dayCount;
	}

	public void setDayCount(int dayCount) {
		this.dayCount = dayCount;
	}

	public void clearCategoryLists() {
		this.categoryLevelList=null;

	}

	public List<CategoryLevel> getCategoryLevelList() {
		return categoryLevelList;
	}

	public void setCategoryLevelList(List<CategoryLevel> subcategories) {
		this.categoryLevelList = subcategories;
	}

	public void clearCategoryLevelList() {
		this.categoryLevelList = null;
	}

	public void setExcludeNonExpense(Boolean excludeNonExpense) {
this.excludeNonExpense=excludeNonExpense;
		
	}

	public Boolean getExcludeNonExpense() {
		return excludeNonExpense;
	}

	public Long getSource() {
		return source;
	}

	public void setSource(Long source) {
		this.source = source;
	}

	public Long getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(Long transactionType) {
		this.transactionType = transactionType;
	}

	public Long getCompareType() {
		return compareType;
	}

	public void setCompareType(Long compareType) {
		this.compareType = compareType;
	}

	public void setShowSubcats(Boolean showSubcats) {
		this.showsubcategories = showSubcats;
	}

	public Boolean getShowSubcats() {
		return showsubcategories;
	}	
	
	public ExpenseCriteria clone()  {
		ExpenseCriteria newobj = new ExpenseCriteria();
		newobj.startdate= this.startdate;
		newobj.enddate = this.enddate;
		newobj.categorizedtype = this.categorizedtype;
		newobj.categoryid = this.categoryid;
		newobj.dayCount = this.dayCount;
		newobj.categoryLevelList = this.categoryLevelList;
		newobj.excludeNonExpense = this.excludeNonExpense;
		newobj.transactionType = this.transactionType;
		newobj.source = this.source;
		newobj.compareType = this.compareType;
		newobj.showsubcategories = this.showsubcategories;
		return newobj;
	}
}
