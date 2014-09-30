package meg.bank.web.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import meg.bank.bus.dao.BankTADao;
import meg.bank.bus.dao.CategoryDao;
import meg.bank.bus.dao.CategoryTADao;

public class ExpenseEditModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private BankTADao bankta;
	private List<CategoryTADao> categoryexps;

	private List<Long> entrycats;
	private List<String> entrycatdisplays;
	private List<Double> entryamounts;
	
	private HashMap<Long,CategoryDao> catref;
	private Integer editIdx;
	private Long quickgroup;
	private Boolean createquickgroup;
	
	// constructor
	public ExpenseEditModel(BankTADao bankta, List<CategoryTADao> categoryexps,HashMap<Long,CategoryDao> catref) {
		super();
		this.bankta = bankta;
		this.catref=catref;
		setCategoryExpenses(categoryexps);
		this.editIdx=999;
	}
	

	// get and set main objects
	public BankTADao getBankTA() {
		return bankta;
	}

	public void setBankTA(BankTADao bankta) {
		this.bankta = bankta;
	}

	public List<CategoryTADao> getCategoryExpenses() {
		if (categoryexps!=null) {
			return categoryexps;
		} else {
			return new ArrayList<CategoryTADao>();
		}
		
	}

	public void setCategoryExpenses(List<CategoryTADao> categoryexps) {
		this.categoryexps = categoryexps;
		copyCategoriesIntoEntries();
	}

	// BankTADao getters
	public String getDescription() {
		return bankta.getDescription();
	}

	public Double getAmount() {
		return bankta.getAmount()*-1;
	}

	public Date getTransdate() {
		return bankta.getTransdate();
	}
	
	public Long getTransid() {
		return bankta.getId();
	}

	public String getDetail() {
		return bankta.getDetail();
	}

	public Boolean getHascat() {
		return bankta.getHascat();
	}

	public Boolean getDeleted() {
		return bankta.getDeleted();
	}

	public Date getImportdate() {
		return bankta.getImportdate();
	}

	public Integer getSource() {
		return bankta.getSource();
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


	public List<Double> getEntryamounts() {
		return entryamounts;
	}


	public void setEntryamounts(List<Double> entryamounts) {
		this.entryamounts = entryamounts;
	}	
	
	

	public Integer getEditIdx() {
		return editIdx;
	}


	public void setEditIdx(Integer editIdx) {
		this.editIdx = editIdx;
	}

	
	

	public Long getQuickgroup() {
		return quickgroup;
	}


	public void setQuickgroup(Long quickgroup) {
		this.quickgroup = quickgroup;
	}


	public Boolean getCreatequickgroup() {
		return createquickgroup;
	}


	public void setCreatequickgroup(Boolean createquickgroup) {
		this.createquickgroup = createquickgroup;
	}


	// Utility methods
	public void copyEntriesIntoCategories() {
		// loop through entrycatdisplays assigning entrycatdisplays,
		//entrycats, and entryamounts to categoryexps
		for (int i=0;i<entrycats.size();i++) {
			Long catid = entrycats.get(i);
			String catdisplay = "empty";
			if (catid!=null && catref.containsKey(catid)) {
				CategoryDao lookup = catref.get(catid);
				catdisplay = lookup.getName();
			}
			Double amount = entryamounts.get(i)*-1;
			// get categoryexp
			CategoryTADao cat = categoryexps.get(i);
			cat.setCatid(catid);
			cat.setCatdisplay(catdisplay);
			cat.setAmount(amount);
			
		}
	}
	
	public void copyCategoriesIntoEntries() {
		// clear entry lists
		entrycats=new ArrayList<Long>();
		entrycatdisplays=new ArrayList<String>();
		entryamounts=new ArrayList<Double>();
		if (categoryexps!=null ) {
			// loop through category list, assigning relevant values to entrycats, 
			//  entrycatdisplays, and entryamounts
			for (CategoryTADao catexp:categoryexps) {
				entrycats.add(catexp.getCatid());
				if (catexp.getCatid()!=null && catref.containsKey(catexp.getCatid())) {
					CategoryDao cat = catref.get(catexp.getCatid());
					entrycatdisplays.add(cat.getName());
				} else {
					entrycatdisplays.add("empty");
				}
				entryamounts.add(catexp.getAmount()==null?0:catexp.getAmount()*-1);
			}
		}
	}


	public void setAmountsInCategorized(Double[] distributed) {
    	for (int i=0;i<distributed.length;i++) {
    		Double newamount = distributed[i]*-1;
    		CategoryTADao cat = categoryexps.get(i);
    		cat.setAmount(newamount);
    	}
		
	}
	
	public Double getCategorizedTotal() {
		// make total variable
		Double totalamount = new Double(0);
		// go through Categorized Expenses, adding up amounts
		if (categoryexps!=null) {
			for (CategoryTADao exp:categoryexps) {
				totalamount += exp.getAmount()!=null?exp.getAmount():0;
			}
		}
		// return total
		return totalamount*-1;
	}
	
	public int getEmptyCount() {
		int count=0;
		if (categoryexps!=null) {
			for (CategoryTADao exp:categoryexps) {
				if (exp.getAmount()==null||exp.getAmount()==0) {
					count++;
				}
			}
		}	
		return count;
	}
	public void setAmountsInEmpties(Double[] distributed) {
		int count=0;
		if (categoryexps!=null) {
			for (CategoryTADao exp:categoryexps) {
				if (exp.getAmount()==null||exp.getAmount()==0) {
					exp.setAmount(distributed[count].doubleValue()*-1);
					count++;
				}
			}
		}
		setCategoryExpenses(categoryexps);
	}
	
	
	
	

}
