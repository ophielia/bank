package meg.bank.bus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import meg.bank.bus.dao.BankTADao;
import meg.bank.bus.dao.CategoryDao;
import meg.bank.bus.dao.CategoryRuleDao;
import meg.bank.bus.dao.CategoryTADao;
import meg.bank.bus.repo.BankTARepository;
import meg.bank.bus.repo.CategoryRepository;
import meg.bank.bus.repo.CategoryRuleRepository;
import meg.bank.bus.repo.CategoryTARepository;

public class BankTransactionService {

	
	@Autowired
	private CategoryRuleRepository catRuleRep;

	@Autowired
	private CategoryRepository catRep;
	
	@Autowired
	private CategoryTARepository catTransRep;	
	
	@Autowired
	private BankTARepository bankTransRep;
	
	public void deleteBankTA(Long todelete) {
		BankTADao bankta = bankTransRep.findOne(todelete);
		bankta.setDeleted(new Boolean(true));

		bankTransRep.save(bankta);

	}
	
	public Date getFirstTransDate() {

		Date resultdate = bankTransRep.getFirstTransDate();

		// check for null
		if (resultdate == null) {
			Calendar cal = Calendar.getInstance();
			cal.set(1970, 10, 1);
			resultdate = cal.getTime();
		}

		return resultdate;
	}
	
	
	public List<TransToCategory> getAssignedCategoryList() {
		// get Category Rules
		List<CategoryRuleDao> rules = catRuleRep.findAll();
		List<TransToCategory> listofassigned = new ArrayList<TransToCategory>();
		Hashtable<Long,TransToCategory> assigned = new Hashtable<Long,TransToCategory>();
		
		if (rules != null) {
			// loop through Category Rules
			Hashtable<Long,Long> assignedtransactions = new Hashtable<Long,Long>();
			for (Iterator<CategoryRuleDao> iter = rules.iterator(); iter.hasNext();) {
				CategoryRuleDao rule = (CategoryRuleDao) iter.next();
				// pull category
				CategoryDao category = catRep.findOne(rule.getCategoryId());

				if (rule.getContaining()!=null) {
					// pull transactions for Category Rule
					List<BankTADao> transactions = bankTransRep.findTransWithDetailLike(rule.getContaining().toUpperCase());

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
		}

		// return list
		for (Iterator<TransToCategory> iter = assigned.values().iterator(); iter.hasNext();) {
			TransToCategory assign = (TransToCategory) iter.next();
			listofassigned.add(assign);
		}
		return listofassigned;
	}
	
	public CategoryTADao getNewCategoryExpense(Long transactionId) {
		CategoryTADao newcat = new CategoryTADao();
		newcat.setBanktaid(transactionId);
		return newcat;
	}
	
	public void deleteCategoryExpense(Long deleteid) {
		CategoryTADao cat = catTransRep.findOne(deleteid);
		if (cat!=null) {
			catTransRep.delete(cat);	
		}
	}
	
	public void  deleteCategoryExpenseByTransaction(Long transid) {
		BankTADao trans = bankTransRep.findOne(transid);

		if (trans!=null) {
			List<CategoryTADao> catexplst = catTransRep.findByBankTrans(trans.getId());
			
			for (Iterator<CategoryTADao> iterator=catexplst.iterator();iterator.hasNext();) {
				CategoryTADao cat = iterator.next();
				deleteCategoryExpense(cat.getId());
			}
			
		}
	}
	
	public boolean doesDuplicateExist(BankTADao trans) {
		boolean exists = false;
		if (trans != null) {
			List<BankTADao> result = bankTransRep.findTransDuplicates(
					trans.getAmount(), trans.getTransdate(),
					trans.getDescription());

			if (result != null && result.size() > 0) {
				// this transaction seems to already exist
				exists = true;
			}
		}
		return exists;
	}
	
	public Date getMostRecentTransDate() {
		// call banktransservice.getMostRecentTransDate();
		Date resultdate = bankTransRep.getMostRecentTransDate();

		// check for null
		if (resultdate == null) {
			Calendar cal = Calendar.getInstance();
			cal.set(1970, 10, 1);
			resultdate = cal.getTime();
		}

		return resultdate;
	}
}
