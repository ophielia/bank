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
import meg.bank.bus.repo.BankTARepository;
import meg.bank.bus.repo.CategoryRepository;
import meg.bank.bus.repo.CategoryRuleRepository;

public class BankTransactionService {

	
	@Autowired
	private CategoryRuleRepository catRuleRep;

	@Autowired
	private CategoryRepository catRep;
	
	@Autowired
	private BankTARepository bankTransRep;
	
	public void deleteBankTA(Long todelete) {
		BankTADao bankta = bankTransRep.findOne(todelete);
		bankta.setDeleted(new Boolean(true));

		bankTransRep.save(bankta);

	}
	
	public Date getFirstTransDate() {
		List result = null;
		Date mindate = null;

		Date resultdate = bankTransRep.getFirstTransDate();

		if (result != null && result.size() > 0) {
			// pull the id off of the result list
			mindate = (Date) result.get(0);

		}

		// check for null
		if (mindate == null) {
			Calendar cal = Calendar.getInstance();
			cal.set(1970, 10, 1);
			mindate = cal.getTime();
		}

		return mindate;
	}
	
	
	public List getAssignedCategoryList() {
		// get Category Rules
		List<CategoryRuleDao> rules = catRuleRep.findAll();
		List listofassigned = new ArrayList();
		Hashtable assigned = new Hashtable();
		
		if (rules != null) {

			// loop through Category Rules
			Hashtable<Long,Long> assignedtransactions = new Hashtable();
			for (Iterator iter = rules.iterator(); iter.hasNext();) {
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
		for (Iterator iter = assigned.values().iterator(); iter.hasNext();) {
			TransToCategory assign = (TransToCategory) iter.next();
			listofassigned.add(assign);
		}
		return listofassigned;
	}

}
