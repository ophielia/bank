package meg.bank.bus;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import meg.bank.bus.dao.BankTADao;
import meg.bank.bus.dao.CategoryDao;

public class RuleAssignment {

	private CategoryDao cat;
	private List<BankTADao> transactions;
	
	public RuleAssignment(CategoryDao cat, List<BankTADao> transactions) {
		this.cat = cat;
		this.transactions = transactions;
	}


	public RuleAssignment(CategoryDao category) {
		this.cat=category;
	}


	public CategoryDao getCategory() {
		return this.cat;
	}
	
	public void setCategory(CategoryDao cat) {
		this.cat = cat;
	}
	
	public List<BankTADao> getTransactions() {
		return this.transactions;
	}
	
	public void setTransactions(List<BankTADao> transactions) {
		this.transactions = transactions;
	}

	public Long getCategoryId() {
		return this.cat.getId();
	}
	
	public void addTransactions(List<BankTADao> newtrans, Hashtable<Long,Long> assigned) {
		if (this.transactions==null) {
			this.transactions = new ArrayList<BankTADao>();
		}
		for (Iterator<BankTADao> iter=newtrans.iterator();iter.hasNext();) {
			BankTADao tr = iter.next();
			if (!assigned.containsKey(tr.getId())) {
				this.transactions.add(tr);
				assigned.put(tr.getId(), new Long(1));	
			}
		}
	}


	public void addTransaction(BankTADao exp) {
		if (transactions == null) {
			transactions = new ArrayList<BankTADao>();
		}
		transactions.add(exp);
	}


	public int getTransactionCount() {
		return transactions!=null?transactions.size():0;
	}
	
}
