package meg.bank.web.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import meg.bank.bus.dao.QuickGroup;
import meg.bank.bus.dao.CategoryDao;
import meg.bank.bus.dao.QuickGroupDetail;

public class QuickGroupModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private QuickGroup quickgroup;
	private List<QuickGroupDetail> quickdetails;

	private List<Long> entrycats;
	private List<String> entrycatdisplays;
	private List<Double> entrypercents;

	private HashMap<Long,CategoryDao> catref;
	private Integer editIdx;
	private Boolean fromExpensePage=false;
	private Long transId;



	// constructor
	public QuickGroupModel(QuickGroup quickgroup, List<QuickGroupDetail> categoryexps,HashMap<Long,CategoryDao> catref) {
		this.quickgroup = quickgroup;
		this.catref=catref;
		setDetails(categoryexps);
		this.editIdx=999;
	}


	// get and set main objects
	public QuickGroup getQuickGroup() {
		return quickgroup;
	}

	public void setQuickGroup(QuickGroup quickgroup) {
		this.quickgroup = quickgroup;
	}

	public List<QuickGroupDetail> getDetails() {
		if (quickdetails!=null) {
			return quickdetails;
		} else {
			return new ArrayList<QuickGroupDetail>();
		}

	}

	public void setDetails(List<QuickGroupDetail> categoryexps) {
		this.quickdetails = categoryexps;
		copyCategoriesIntoEntries();
	}

	// QuickGroup getters
	public String getName() {
		return quickgroup.getName();
	}

	public void setName(String name) {
		this.quickgroup.setName(name);
	}
	

	public Long getGroupId() {
		// TODO Auto-generated method stub
		return quickgroup.getId();
	}
	// entry getters and setters
	public List<Long> getEntrycats() {
		return entrycats;
	}


	public void setEntrycats(List<Long> entrycats) {
		this.entrycats = entrycats;
	}


	public List<String> getEntrycatdisplays() {
		return entrycatdisplays;
	}


	public void setEntrycatdisplays(List<String> entrycatdisplays) {
		this.entrycatdisplays = entrycatdisplays;
	}


	public List<Double> getEntrypercents() {
		return entrypercents;
	}


	public void setEntryamounts(List<Double> entrypercents) {
		this.entrypercents = entrypercents;
	}



	public Integer getEditIdx() {
		return editIdx;
	}


	public void setEditIdx(Integer editIdx) {
		this.editIdx = editIdx;
	}

	

	public Boolean getFromExpensePage() {
		return fromExpensePage;
	}


	public void setFromExpensePage(Boolean fromExpensePage) {
		this.fromExpensePage = fromExpensePage;
	}

	public void setTransId(Long transid) {
		this.transId=transid;
	}


	public Long getTransId() {
		return transId;
	}

	

	// Utility methods
	public void copyEntriesIntoCategories() {
		// loop through entrycatdisplays assigning entrycatdisplays,
		//entrycats, and entrypercents to quickgroupdetails
		for (int i=0;i<entrycats.size();i++) {
			Long catid = entrycats.get(i);
			String catdisplay = "empty";
			if (catid!=null && catref.containsKey(catid)) {
				CategoryDao lookup = catref.get(catid);
				catdisplay = lookup.getName();
			}
			Double percent = entrypercents.get(i);
			// get categoryexp
			QuickGroupDetail cat = quickdetails.get(i);
			cat.setCatid(catid);
			cat.setCatdisplay(catdisplay);
			cat.setPercentage(percent);

		}
	}

	public void copyCategoriesIntoEntries() {
		// clear entry lists
		entrycats=new ArrayList<Long>();
		entrycatdisplays=new ArrayList<String>();
		entrypercents=new ArrayList<Double>();
		if (quickdetails!=null ) {
			// loop through category list, assigning relevant values to entrycats,
			//  entrycatdisplays, and entryamounts
			for (QuickGroupDetail detail:quickdetails) {
				entrycats.add(detail.getCatid());
				if (detail.getCatid()!=null && catref.containsKey(detail.getCatid())) {
					CategoryDao cat = catref.get(detail.getCatid());
					entrycatdisplays.add(cat.getName());
				} else {
					entrycatdisplays.add("empty");
				}
				entrypercents.add(detail.getPercentage()==null?0:detail.getPercentage());
			}
		}
	}



	




}
