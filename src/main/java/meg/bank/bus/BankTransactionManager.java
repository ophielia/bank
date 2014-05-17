package meg.bank.bus;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Service;

import meg.bank.bus.dao.BankTADao;
import meg.bank.bus.dao.CategoryDao;
import meg.bank.bus.dao.CategoryRuleDao;
import meg.bank.bus.dao.CategoryTADao;
import meg.bank.bus.dao.ExpenseDao;
import meg.bank.bus.report.CategorySummaryDisp;
import meg.bank.db.TransactionManagerDao;

/**
 * Handles BankTransactions - the transactions as received from the banking
 * institution.
 * 
 * @author maggie
 * 
 */
@Service
public class BankTransactionManager {

	private TransactionManagerDao transmandao;

	private CategoryManager cms;

	public void setCategoryManager(CategoryManager cms) {
		this.cms = cms;
	}

	public CategoryManager getCategoryManager() {
		return this.cms;
	}

	public Date getMostRecentTransDate() {
		return transmandao.getMostRecentTransDate();
	}
	
	public Date getFirstTransDate() {
		return transmandao.getFirstTransDate();
	}

	public TransactionManagerDao getTransactionManagerDao() {
		return transmandao;
	}

	public void setTransactionManagerDao(TransactionManagerDao transmandao) {
		this.transmandao = transmandao;
	}

	public void addTransaction(BankTADao trans) {
// set deleted to false
		trans.setDeleted(new Boolean(false));
		// call Dao interface here
		transmandao.createOrSaveBankTrans(trans);

	}

	public boolean doesDuplicateExist(BankTADao trans) {
		// currently, duplicates check date, amount and description
		// call Dao interface here
		return transmandao.duplicateExists(trans);
	}

	public List getAllBankTransactions() {
		return transmandao.getAllBankTAs();
	}

	public void deleteBankTA(Long todelete) {
		transmandao.deleteBankTA(todelete);
		
	}

	public void clearCategoryAssignment(Long toclear) {
		BankTADao bankta = transmandao.getBankTA(toclear);
		if (bankta.getHascat().booleanValue()) {
			// delete category expenses belonging to this transaction
			transmandao.deleteCategoryExpenseByTransaction(toclear);
			
			// reset category flag in transaction
			bankta.setHascat(new Boolean(false));
			
			// save transaction
			transmandao.createOrSaveBankTrans(bankta);
		}
		
	}
	
	public BankTADao getTransaction(Long transid) {
		BankTADao bankta = transmandao.getBankTA(transid);

		return bankta;
	}

	public void assignCategory(Long transid, Long catid) {
		// lookup BankTransaction
		BankTADao bankta = transmandao.getBankTA(transid);

		// begin creating CategoryTADao based upon BankTransaction
		CategoryTADao catta = new CategoryTADao();
		catta.setAmount(bankta.getAmount());
		catta.setCatid(catid);
		catta.setCreatedon(new Date());
		catta.setBanktaid(bankta.getId());

		// add CategoryTADao to DB
		transmandao.createOrSaveCatTrans(catta);

		// update Transaction
		bankta.setHascat(new Boolean(true));
		transmandao.createOrSaveBankTrans(bankta);
	}

	public List getNoCategoryExpenses() {
		return transmandao.getNoCategoryExpenses();
	}

	public List getCategoryExpForTrans(Long transid) {

		return transmandao.getCategoryExpenses(transid);
	}

	public CategoryTADao getNewCategoryExpense(Long transactionId) {
		return transmandao.getNewCategoryExpense(transactionId);
	}

	public Double[] distributeAmounts(Double amount, int cnt) {
		if (cnt < 1)
			return new Double[0];
		Double[] amounts = new Double[cnt];
		// now, round to smallest penny
		double baseamountraw = 0;
		if (amount < 0) {
			baseamountraw = Math.ceil((amount / (double) cnt) * 100);
		} else {
			baseamountraw = Math.floor((amount / (double) cnt) * 100);
		}

		double baseamount = baseamountraw / 100;
		double total = 0;
		for (int i = 0; i < amounts.length - 1; i++) {
			amounts[i] = new Double(baseamount);
			total += baseamount;
		}
		amounts[cnt - 1] = new Double(
				Math.round((amount - total) * 100) / 100D);
		return amounts;

	}

	public void saveTransaction(BankTADao transaction, List categories) {
		// check for categories
		if (categories != null && categories.size() > 0) {
			transaction.setHascat(new Boolean(true));
		}
		// save transaction
		transmandao.createOrSaveBankTrans(transaction);

		// clean up categories - compress duplicates into 1 category
		Hashtable<Long,CategoryTADao> compressed = new Hashtable();
		for (Iterator<CategoryTADao> iter = categories.iterator();iter.hasNext();) {
			CategoryTADao cat = (CategoryTADao)iter.next();
			Long catid = cat.getCatid();
			if (compressed.containsKey(catid)) {
				// compress and delete
				CategoryTADao compressinto = compressed.get(catid);
				double amt=compressinto.getAmount().doubleValue();
				amt+=cat.getAmount().doubleValue();
				compressinto.setAmount(new Double(amt));
				compressed.put(catid,compressinto);
				if (cat.getId()!=null && cat.getId().longValue()>0) {
					transmandao.deleteCategoryExpense(cat.getId());
				}
			} else {
				// add to hashtable
				compressed.put(catid,cat);
			}
		}
		
		
		// update/ save all categories
		Enumeration<CategoryTADao> tosave = compressed.elements();
		if (tosave != null ) {
			while (tosave.hasMoreElements()) {
				CategoryTADao cat = (CategoryTADao) tosave.nextElement();
				cat.setBanktaid(transaction.getId());
				cat.setCreatedon(new Date());
				transmandao.createOrSaveCatTrans(cat);
			}

		}

	}

	public void deleteCategoryExpenses(List deleted) {
		for (Iterator iter = deleted.iterator(); iter.hasNext();) {
			Long deleteid = (Long) iter.next();
			transmandao.deleteCategoryExpense(deleteid);
		}

	}

	public List getAssignedCategoryList() {
		// call BankTransactionService.getAssignedCategoryList()
return null;
/*
// get Category Rules
List rules = cms.getCategoryRules();
List listofassigned = new ArrayList();
Hashtable assigned = new Hashtable();

if (rules != null) {

	// loop through Category Rules
	Hashtable<Long,Long> assignedtransactions = new Hashtable();
	for (Iterator iter = rules.iterator(); iter.hasNext();) {
		CategoryRuleDao rule = (CategoryRuleDao) iter.next();
		// pull category
		CategoryDao category = cms.getCategory(rule.getCategoryId());

		// pull transactions for Category Rule
		List transactions = transmandao
				.retrieveTransactionsContaining(rule.getContaining());

		// assemble AssignRuleCategory, and add to list
		if (transactions!=null && transactions.size()>0 ) {
			TransToCategory assign = (TransToCategory) assigned.get(category.getId());
			if (assign==null) {
				assign=new TransToCategory(category);
			}
			assign.addTransactions(transactions, assignedtransactions);
			assigned.put(category.getId(),assign);
		}
	}
}

// return list
for (Iterator iter = assigned.values().iterator(); iter.hasNext();) {
	TransToCategory assign = (TransToCategory) iter.next();
	listofassigned.add(assign);
}
return listofassigned;
*/
	
	
	}

	public void assignFromCategories(List assignedcategories) {
		if (assignedcategories != null) {
			// loop through categories
			for (Iterator iter = assignedcategories.iterator(); iter.hasNext();) {
				TransToCategory rule = (TransToCategory) iter.next();
				// for each category group, pull transactions to be assigned
				List transactions = rule.getTransactions();
				Long currentcatid = rule.getCategoryId();
				if (transactions != null) {
					// loop through transactions
					for (Iterator iterator = transactions.iterator(); iterator
							.hasNext();) {
						BankTADao trans = (BankTADao) iterator.next();
						// assign each transaction to the group
						assignCategory(trans.getId(), currentcatid);
					}
				}
			}
		}
	}

	public void assignFromCategories(Long catid, List transtoadd) {
		// create TransToCategory
		CategoryDao cat = cms.getCategory(catid);
		TransToCategory ttc = new TransToCategory(cat, transtoadd);

		// add to "list"
		List newlist = new ArrayList();
		newlist.add(ttc);

		// assign transactions
		assignFromCategories(newlist);
	}

	public List<ExpenseDao> getExpenses(ExpenseCriteria criteria) {

		return transmandao.getExpenses(criteria);
	}

	public void assignExpensesFromCategories(Long catid, List selected) {
		// loop through selected list
		for (Iterator iter = selected.iterator(); iter.hasNext();) {
			ExpenseDao expense = (ExpenseDao) iter.next();
			if (expense.getHascat().booleanValue()) {
				// if expense has a category already, existing category must be
				// updated
				updateCategoryExp(expense.getCattransid(), catid);
			} else {
				// otherwise, assign category to BankTA
				assignCategory(expense.getTransid(), catid);
			}
		}
	}

	private void updateCategoryExp(Long catexpid, Long newcatid) {
		// retrieve CategoryExpense
		CategoryTADao catexp = transmandao.getCatTA(catexpid);
		// update with new catid
		catexp.setCatid(newcatid);
		// persist change
		transmandao.createOrSaveCatTrans(catexp);
	}

	public List<CategorySummaryDisp> getExpenseTotalByMonth(ExpenseCriteria criteria,String dispname) {
		List displays = transmandao.getExpenseTotalByMonth(criteria);
		for (Iterator iter = displays.iterator(); iter.hasNext();) {
			CategorySummaryDisp catsum = (CategorySummaryDisp) iter.next();
			catsum.setCatName(dispname);
		}
		return displays;
	}
	
	public List<CategorySummaryDisp> getExpenseTotalByYear(ExpenseCriteria criteria,String dispname) {
		List displays = transmandao.getExpenseTotalByYear(criteria);
		for (Iterator iter = displays.iterator(); iter.hasNext();) {
			CategorySummaryDisp catsum = (CategorySummaryDisp) iter.next();
			catsum.setCatName(dispname);
		}
		return displays;
	}	

	public List<CategorySummaryDisp> getExpenseTotal(ExpenseCriteria criteria,
			String dispname) {
		List<CategorySummaryDisp> displays = transmandao.getExpenseTotal(criteria);
		for (CategorySummaryDisp catsum:displays) {
			catsum.setCatName(dispname);
		}
		return displays;
	}


}
