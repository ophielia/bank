package meg.bank.bus;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import meg.bank.bus.imp.ImportManager;

public class ExpenseCriteria implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public final class DateRange {
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


	
	public final class SortType {
		public static final String Date = "Date";
		public static final String Category = "Cat";
		public static final String Detail = "Detail";
		public static final String Amount ="Amount";
	}
	
	public static final class SortDirection {
		public static final Long Asc = 1L;
		public static final Long Desc= 2L;
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
	
	private Long daterangetype;

	private List<CategoryLevel> categoryLevelList;

	private Boolean excludeNonExpense;
	
	private Long transactionType;
	private Long source;

	private Long compareType;

	private Boolean showsubcategories;
	
	private String sorttype;
	
	private Long sortdir;

	public void setDateRangeByType(Long dateRangeType) {
		this.daterangetype = dateRangeType;
		setDateRangeFromType(dateRangeType);

	}
	
	public Long getDateRangeByType() {
		return daterangetype;
	}
	
	public void setCategorizedType(Long categorizedType) {
		if (categorizedType!=null &&categorizedType.longValue()>0) {
			categorizedtype = categorizedType;
		}

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
		this.showsubcategories=false;

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
		if (source>0) {
			this.source = source;	
		}
		
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
	
	
	
	public String getSorttype() {
		return sorttype;
	}

	public void setSorttype(String sorttype) {
		if (this.sorttype != null && this.sorttype.equals(sorttype)) {
			// same sort submitted - change the direction
			changeSortDirection();
		}
		
		this.sorttype = sorttype;
	}


	public Long getSortdir() {
		return sortdir;
	}

	public void setSortdir(Long sortdir) {
		this.sortdir = sortdir;
	}

	public boolean showSingleCategory() {
		// single category set??
		if (getCategory()!=null) {
			if (getShowSubcats()!=null) {
				return !getShowSubcats();
			} else {
				return true;
			}
		}
		return false;
	}
	
	public boolean showListOfCategories() {
		// single category set, and show subcategories not set
		if (getShowSubcats()!=null && getShowSubcats().booleanValue()) {
			boolean showcategorylist =(getCategoryLevelList() != null
					&& getCategoryLevelList().size() > 0);
			return showcategorylist;
		}
		return false;
	}
	
	public boolean showBySource() {
		return getSource()!=null && getSource().longValue()!=ImportManager.ImportClient.All;
	}
	

	private void changeSortDirection() {
		if (sortdir!=null) {
			if (sortdir==SortDirection.Asc) {
				this.sortdir = SortDirection.Desc;
			} else {
				this.sortdir=SortDirection.Asc;
			}
		} else {
			sortdir=SortDirection.Desc;
		} 
		
	}
	
	private void setDateRangeFromType(Long dateRangeType) {
		this.daterangetype = dateRangeType;
		// return if "DATERANGE"
		if (dateRangeType.longValue() == DateRange.DATERANGE) {
			return;
		}
		// return if "ALL"
		if (dateRangeType.longValue() == DateRange.ALL) {
			// clear start date and end date
			setDateStart(null);
			setDateEnd(null);
			return;
		}		
		// set start and end dates
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		if (dateRangeType.longValue() == DateRange.CURRENT) {
			end.add(Calendar.MONTH, 1);
		} else if (dateRangeType.longValue() == DateRange.LAST) {
			start.add(Calendar.MONTH, -1);
		}  else if (dateRangeType.longValue() == DateRange.BEFORELAST) {
			start.add(Calendar.MONTH, -2);
			end.add(Calendar.MONTH, -1);
		} else if (dateRangeType.longValue() == DateRange.THISWEEK) {
			// get current day
			int currentday = start.get(Calendar.DAY_OF_WEEK);
	
			// get last Saturday
			Long toroll = (Long) weekhelper.get(new Integer(currentday));
			start.add(Calendar.DAY_OF_MONTH, (int) toroll.longValue());
			// get next Saturday
			end = (Calendar) start.clone();
			end.add(Calendar.DAY_OF_MONTH, 7);
	
		} else if (dateRangeType.longValue() == DateRange.LASTWEEK) {
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
	
		if (dateRangeType.longValue() != DateRange.THISWEEK
				&& dateRangeType.longValue() != DateRange.LASTWEEK) {
			// set dates to first of month
			start.set(Calendar.DAY_OF_MONTH, 1);
			end.set(Calendar.DAY_OF_MONTH, 1);
		}
		
		if (dateRangeType.longValue()== DateRange.THISYEAR) {
			// set date to january 1st
			start.set(Calendar.DAY_OF_MONTH, 1);
			start.set(Calendar.MONTH, Calendar.JANUARY);
			end.set(Calendar.DAY_OF_MONTH, 1);
			end.add(Calendar.MONTH, 1);
		}
		
		if (dateRangeType.longValue()== DateRange.LASTYEAR) {
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
