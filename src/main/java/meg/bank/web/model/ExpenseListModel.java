package meg.bank.web.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import meg.bank.bus.ExpenseCriteria;
import meg.bank.bus.dao.ExpenseDao;
import meg.bank.bus.imp.ImportManager;



public class ExpenseListModel implements Serializable {

	private static final long serialVersionUID = 1L;
 	

	
	private List<ExpenseDao> expenses;
	private List<Boolean> checked;


	
	
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
	}
	public List<Boolean> getChecked() {
		return checked;
	}
	public void setChecked(List<Boolean> checked) {
		this.checked = checked;
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
	

	
	


}
