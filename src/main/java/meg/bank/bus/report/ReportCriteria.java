package meg.bank.bus.report;

import java.io.Serializable;
import java.util.Date;

public class ReportCriteria implements Serializable {

	public static final class Parameters {
		public static final int daterange=1;
		public static final int breakoutlvl=2;
		public static final int excludenonexp=3;
		public static final int category=4;
		public static final int monthlist=5;
		public static final int comparetype=6;
		public static final int yearlist=7;
	}
	
	public final class CompareType {
		public static final int LASTMONTHS = 1;
		public static final int CALYEAR = 2;
		public static final int ALL = 3;
	}	
	
	public final static String BreakoutLookup = "breakoutlevel";
	public static final String CompareTypeLkup= "comparetype";
	
	private Long breakoutLevel;

	private Long daterangetype;

	private Date endDate;

	private Date startDate;

	private String imageDir;

	private Boolean excludeNonExpense;

	public Long categoryId;

	private String month;

	private Long comparetype;
	
	private String year;
	
	private Long reporttype;
	private String imageweblink;
	private String contextpath;
	private String fullimageweblink;
	
	private boolean usefulllink=false;

	public Long getBreakoutLevel() {
		return breakoutLevel;
	}

	public void setBreakoutLevel(Long breakoutLevel) {
		this.breakoutLevel = breakoutLevel;
	}

	public Long getDaterangetype() {
		return daterangetype;
	}

	public void setDaterangetype(Long daterangetype) {
		this.daterangetype = daterangetype;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getImageDir() {
		return imageDir;
	}

	public void setImageDir(String imageDir) {
		this.imageDir = imageDir;
	}

	public Boolean getExcludeNonExpense() {
		return excludeNonExpense;
	}

	public void setExcludeNonExpense(Boolean excludeNonExpense) {
		this.excludeNonExpense = excludeNonExpense;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public Long getComparetype() {
		return comparetype;
	}

	public void setComparetype(Long comparetype) {
		this.comparetype = comparetype;
	}

	public String getYear() {
		return year; 
	}

	public void setYear(String year) {
		this.year = year;
	}

	public void setReportType(Long reporttype) {
		this.reporttype = reporttype;
		
	}
	
	public Long getReportType() {
		return this.reporttype;
	}

	public void setUseFullImageLink(boolean usefulllink) {
		this.usefulllink=usefulllink;
	}
	
	public void setImageLink(String imageweblink) {
		this.imageweblink = imageweblink;
	}

	public String getImageLink() {
		if (!this.usefulllink)
		return this.imageweblink;
		return this.fullimageweblink;
	}

	public void setFullImageLink(String fullimageweblink) {
		this.fullimageweblink=fullimageweblink;
	}
	
	public String getFullImageLink() {
		return this.fullimageweblink;
	}
	public void setContextPath(String contextpath) {
		this.contextpath = contextpath;
		
	}
	
	public String getContextPath() {
		return this.contextpath;
	}


	

	
}
