package meg.bank.web.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import meg.bank.bus.ExpenseCriteria;
import meg.bank.bus.dao.ExpenseDao;
import meg.bank.bus.imp.ImportManager;



public class ExpenseListModel implements Serializable {

	private static final long serialVersionUID = 1L;
 	

	
	private List<ExpenseDao> expenses;
	private List<Boolean> checked;
	private List<String> idref;
	private Long batchUpdate;

	
	
	private ExpenseCriteria expensecriteria;
	
	
	
	public ExpenseListModel(ExpenseCriteria expensecriteria) {
		super();
		this.expensecriteria = expensecriteria;
	}
	
	public List<ExpenseDao> getExpenses() {
		return expenses;
	}
	public void setExpenses(List<ExpenseDao> expenses) {
		this.expenses = expenses;
		if (expenses!=null && expenses.size()>0) {
			// initialize checked list
			createCheckedAndIdSlots(expenses.size());
		}
	}
	public List<Boolean> getChecked() {
		return checked;
	}
	public void setChecked(List<Boolean> checked) {
		this.checked = checked;
	}
	public List<String> getIdref() {
		return idref;
	}

	public void setIdref(List<String> idref) {
		this.idref = idref;
	}

	public ExpenseCriteria getCriteria() {
		// TODO Auto-generated method stub
		return expensecriteria;
	}
	
	// getters and setters for criteria fields
	public Long getDateRangeByType() {
		return expensecriteria.getDateRangeByType();
	}
	public void setDateRangeByType(Long daterangetype) {
		expensecriteria.setDateRangeByType(daterangetype);
	}

	public Long getCategorizedType() {
		return expensecriteria.getCategorizedType();
	}
	public void setCategorizedType(Long daterangetype) {
		expensecriteria.setCategorizedType(daterangetype);
	}	
	
	public Long getCategory() {
		return expensecriteria.getCategory();
	}
	public void setCategory(Long catid) {
		expensecriteria.setCategory(catid);
	}		
	
	public Long getSource() {
		return expensecriteria.getSource();
	}
	public void setSource(Long daterangetype) {
		expensecriteria.setSource(daterangetype);
	}	
	
	public Long getTransactionType() {
		return expensecriteria.getTransactionType();
	}
	public void setTransactionType(Long daterangetype) {
		expensecriteria.setTransactionType(daterangetype);
	}

	public Long getBatchUpdate() {
		return batchUpdate;
	}

	public void setBatchUpdate(Long batchUpdate) {
		this.batchUpdate = batchUpdate;
	}

	public List<String> getCheckedExpenseIds() {
		// make new empty list (for ExpenseDao)
		List<String> checkedexp = new ArrayList<String>();
		if (idref!=null) {
			// go through checked list
			for (int i=0;i<checked.size();i++) {
				// if checked is true, add expenseDao at same slot to checkedlist
				Boolean test = checked.get(i);
				if (test!=null && test) {
					checkedexp.add(idref.get(i));
				}
			}
		}
		// return checked list
		return checkedexp;
	}

	private void createCheckedAndIdSlots(int size) {
		checked = new ArrayList<Boolean>();
		//idref = new ArrayList<Long>();
		for (int i=0;i<size;i++) {
			checked.add(false);
			//idref.add(expenses.get(i).getId());
		}
		
	}
	
	
	

	
	


}
