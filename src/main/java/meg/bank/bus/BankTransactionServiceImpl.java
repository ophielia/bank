package meg.bank.bus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import meg.bank.bus.dao.BankTADao;
import meg.bank.bus.dao.CategoryDao;
import meg.bank.bus.dao.CategoryRuleDao;
import meg.bank.bus.dao.CategoryTADao;
import meg.bank.bus.dao.ExpenseDao;
import meg.bank.bus.repo.BankTARepository;
import meg.bank.bus.repo.CategoryRepository;
import meg.bank.bus.repo.CategoryRuleRepository;
import meg.bank.bus.repo.CategoryTARepository;
import meg.bank.bus.report.CategorySummaryDisp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BankTransactionServiceImpl implements BankTransactionService {

	
	@Autowired
	private CategoryRuleRepository catRuleRep;

	@Autowired
	private CategoryRepository catRep;
	
	@Autowired
	private CategoryTARepository catTransRep;	
	
	@Autowired
	private BankTARepository bankTransRep;
	
	@Autowired
	private CategoryManager cms;
	
	@Autowired
	private SearchService searchService;
	
	/* (non-Javadoc)
	 * @see meg.bank.bus.BankTransactionService#deleteBankTA(java.lang.Long)
	 */
	@Override
	public void deleteBankTA(Long todelete) {
		BankTADao bankta = bankTransRep.findOne(todelete);
		bankta.setDeleted(new Boolean(true));

		bankTransRep.save(bankta);

	}
	
	/* (non-Javadoc)
	 * @see meg.bank.bus.BankTransactionService#getFirstTransDate()
	 */
	@Override
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
	
	
	/* (non-Javadoc)
	 * @see meg.bank.bus.BankTransactionService#getAssignedCategoryList()
	 */
	@Override
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
	
	/* (non-Javadoc)
	 * @see meg.bank.bus.BankTransactionService#getNewCategoryExpense(java.lang.Long)
	 */
	@Override
	public CategoryTADao getNewCategoryExpense(Long transactionId) {
		CategoryTADao newcat = new CategoryTADao();
		newcat.setBanktaid(transactionId);
		return newcat;
	}
	
	/* (non-Javadoc)
	 * @see meg.bank.bus.BankTransactionService#deleteCategoryExpense(java.lang.Long)
	 */
	@Override
	public void deleteCategoryExpense(Long deleteid) {
		CategoryTADao cat = catTransRep.findOne(deleteid);
		if (cat!=null) {
			catTransRep.delete(cat);	
		}
	}
	
	/* (non-Javadoc)
	 * @see meg.bank.bus.BankTransactionService#deleteCategoryExpenseByTransaction(java.lang.Long)
	 */
	@Override
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
	
	/* (non-Javadoc)
	 * @see meg.bank.bus.BankTransactionService#doesDuplicateExist(meg.bank.bus.dao.BankTADao)
	 */
	@Override
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
	
	/* (non-Javadoc)
	 * @see meg.bank.bus.BankTransactionService#getMostRecentTransDate()
	 */
	@Override
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

	/* (non-Javadoc)
	 * @see meg.bank.bus.BankTransactionService#addTransaction(meg.bank.bus.dao.BankTADao)
	 */
	@Override
	public void addTransaction(BankTADao trans) {
	// set deleted to false
			trans.setDeleted(new Boolean(false));
			// call Dao interface here
			bankTransRep.save(trans);
	
		}


	/* (non-Javadoc)
	 * @see meg.bank.bus.BankTransactionService#getAllBankTransactions()
	 */
	@Override
	public List<BankTADao> getAllBankTransactions() {
		return bankTransRep.findAll();
	}


	/* (non-Javadoc)
	 * @see meg.bank.bus.BankTransactionService#clearCategoryAssignment(java.lang.Long)
	 */
	@Override
	public void clearCategoryAssignment(Long toclear) {
		BankTADao bankta = bankTransRep.findOne(toclear);
		if (bankta.getHascat().booleanValue()) {
			// delete category expenses belonging to this transaction
			deleteCategoryExpenseByTransaction(toclear);
			
			// reset category flag in transaction
			bankta.setHascat(new Boolean(false));
			
			// save transaction
			bankTransRep.save(bankta);
		}
		
	}

	/* (non-Javadoc)
	 * @see meg.bank.bus.BankTransactionService#getTransaction(java.lang.Long)
	 */
	@Override
	public BankTADao getTransaction(Long transid) {
		BankTADao bankta = bankTransRep.findOne(transid);
	
		return bankta;
	}

	/* (non-Javadoc)
	 * @see meg.bank.bus.BankTransactionService#assignCategory(java.lang.Long, java.lang.Long)
	 */
	@Override
	public void assignCategory(Long transid, Long catid) {
		// lookup BankTransaction
		BankTADao bankta = bankTransRep.findOne(transid);
	
		// begin creating CategoryTADao based upon BankTransaction
		CategoryTADao catta = new CategoryTADao();
		catta.setAmount(bankta.getAmount());
		catta.setCatid(catid);
		catta.setCreatedon(new Date());
		catta.setBanktaid(bankta.getId());
	
		// add CategoryTADao to DB
		catTransRep.save(catta);
	
		// update Transaction
		bankta.setHascat(new Boolean(true));
		bankTransRep.save(bankta);
	}

	/* (non-Javadoc)
	 * @see meg.bank.bus.BankTransactionService#getNoCategoryExpenses()
	 */
	@Override
	public List<BankTADao> getNoCategoryExpenses() {
		return bankTransRep.findNoCategoryExpenses();
	}

	/* (non-Javadoc)
	 * @see meg.bank.bus.BankTransactionService#getCategoryExpForTrans(java.lang.Long)
	 */
	@Override
	public List<CategoryTADao> getCategoryExpForTrans(Long transid) {
	
		return catTransRep.findByBankTrans(transid);
	}


	/* (non-Javadoc)
	 * @see meg.bank.bus.BankTransactionService#distributeAmounts(java.lang.Double, int)
	 */
	@Override
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

	/* (non-Javadoc)
	 * @see meg.bank.bus.BankTransactionService#saveTransaction(meg.bank.bus.dao.BankTADao, java.util.List)
	 */
	@Override
	public void saveTransaction(BankTADao transaction, List<CategoryTADao> categories) {
		// check for categories
		if (categories != null && categories.size() > 0) {
			transaction.setHascat(new Boolean(true));
		}
		// save transaction
		bankTransRep.save(transaction);
	
		// clean up categories - compress duplicates into 1 category
		Hashtable<Long,CategoryTADao> compressed = new Hashtable<Long,CategoryTADao>();
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
					deleteCategoryExpense(cat.getId());
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
				catTransRep.save(cat);
			}
	
		}
	
	}

	/* (non-Javadoc)
	 * @see meg.bank.bus.BankTransactionService#deleteCategoryExpenses(java.util.List)
	 */
	@Override
	public void deleteCategoryExpenses(List<Long> deleted) {
		for (Iterator<Long> iter = deleted.iterator(); iter.hasNext();) {
			Long deleteid = (Long) iter.next();
			deleteCategoryExpense(deleteid);
		}
	
	}



	/* (non-Javadoc)
	 * @see meg.bank.bus.BankTransactionService#assignFromCategories(java.util.List)
	 */
	@Override
	public void assignFromCategories(List<TransToCategory> assignedcategories) {
		if (assignedcategories != null) {
			// loop through categories
			for (Iterator iter = assignedcategories.iterator(); iter.hasNext();) {
				TransToCategory rule = (TransToCategory) iter.next();
				// for each category group, pull transactions to be assigned
				List<BankTADao> transactions = rule.getTransactions();
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

	/* (non-Javadoc)
	 * @see meg.bank.bus.BankTransactionService#assignFromCategories(java.lang.Long, java.util.List)
	 */
	@Override
	public void assignFromCategories(Long catid, List<BankTADao> transtoadd) {
		// create TransToCategory
		CategoryDao cat = cms.getCategory(catid);
		TransToCategory ttc = new TransToCategory(cat, transtoadd);
	
		// add to "list"
		List newlist = new ArrayList();
		newlist.add(ttc);
	
		// assign transactions
		assignFromCategories(newlist);
	}

	/* (non-Javadoc)
	 * @see meg.bank.bus.BankTransactionService#getExpenses(meg.bank.bus.ExpenseCriteria)
	 */
	@Override
	public List<ExpenseDao> getExpenses(ExpenseCriteria criteria) {
	
		return searchService.getExpenses(criteria);
	}

	/* (non-Javadoc)
	 * @see meg.bank.bus.BankTransactionService#assignExpensesFromCategories(java.lang.Long, java.util.List)
	 */
	@Override
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
		CategoryTADao catexp = catTransRep.findOne(catexpid);
		// update with new catid
		catexp.setCatid(newcatid);
		// persist change
		catTransRep.save(catexp);
	}

	/* (non-Javadoc)
	 * @see meg.bank.bus.BankTransactionService#getExpenseTotalByMonth(meg.bank.bus.ExpenseCriteria, java.lang.String)
	 */
	@Override
	public List<CategorySummaryDisp> getExpenseTotalByMonth(ExpenseCriteria criteria,String dispname) {
// MM move to search Service
		List displays = searchService.getExpenseTotalByMonth(criteria);
		for (Iterator iter = displays.iterator(); iter.hasNext();) {
			CategorySummaryDisp catsum = (CategorySummaryDisp) iter.next();
			catsum.setCatName(dispname);
		}
		return displays;
	}

	/* (non-Javadoc)
	 * @see meg.bank.bus.BankTransactionService#getExpenseTotalByYear(meg.bank.bus.ExpenseCriteria, java.lang.String)
	 */
	@Override
	public List<CategorySummaryDisp> getExpenseTotalByYear(ExpenseCriteria criteria,String dispname) {
		// MM move to search Service
		List displays = searchService.getExpenseTotalByYear(criteria);
		for (Iterator iter = displays.iterator(); iter.hasNext();) {
			CategorySummaryDisp catsum = (CategorySummaryDisp) iter.next();
			catsum.setCatName(dispname);
		}
		return displays;
	}

	/* (non-Javadoc)
	 * @see meg.bank.bus.BankTransactionService#getExpenseTotal(meg.bank.bus.ExpenseCriteria, java.lang.String)
	 */
	@Override
	public List<CategorySummaryDisp> getExpenseTotal(ExpenseCriteria criteria,
			String dispname) {
		List<CategorySummaryDisp> displays = searchService.getExpenseTotal(criteria);
		for (CategorySummaryDisp catsum:displays) {
			catsum.setCatName(dispname);
		}
		return displays;
	}
}