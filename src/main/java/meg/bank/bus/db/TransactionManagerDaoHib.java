package meg.bank.bus.db;

import java.util.Date;
import java.util.List;

import meg.bank.bus.ExpenseCriteria;
import meg.bank.bus.dao.BankTADao;
import meg.bank.bus.dao.CategoryTADao;
import meg.bank.bus.dao.ExpenseDao;
import meg.bank.bus.repo.CategoryRepository;
import meg.bank.bus.report.CategorySummaryDisp;

import org.hibernate.Session;

public class TransactionManagerDaoHib  {

	


	// service and repo
	public boolean duplicateExists(BankTADao trans) {
		// call BankTransService.doesDuplicateExist(trans);
		
		return false;
		/*
		boolean exists = false;
		List result = null;
		// getHibernateTemplate().
		Object[] params = new Object[3];
		params[0] = trans.getAmount();
		params[1] = trans.getTransdate();
		params[2] = trans.getDesc();

		result = getHibernateTemplate()
				.find(
						"select id from BankTADao as trans where trans.amount = ? "
								+ "and trans.transdate = ? and trans.desc = ? ",
						params);

		if (result != null && result.size() > 0) {
			// this transaction seems to already exist
			exists = true;
		}
		return exists;*/
	}

	public Date getMostRecentTransDate() {
		// call banktransservice.getMostRecentTransDate();
/*
		List result = null;
		Date maxdate = null;

		result = getHibernateTemplate().find(
				"select max(trans.transdate) from BankTADao as trans ");

		if (result != null && result.size() > 0) {
			// pull the id off of the result list
			maxdate = (Date) result.get(0);

		}

		// check for null
		if (maxdate == null) {
			Calendar cal = Calendar.getInstance();
			cal.set(1970, 10, 1);
			maxdate = cal.getTime();
		}

		return maxdate;


 */
		return null;
	}

	public Date getFirstTransDate() {
		// call banktransservice.getFirstTransDate()
		/*
		List result = null;
		Date mindate = null;

		result = getHibernateTemplate().find(
				"select min(trans.transdate) from BankTADao as trans ");

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
		*/
		return null;
	}


	// !! repo only !! //
	private Session bankTADaoRepository;
	private CategoryRepository categoryRep;
	public List getAllBankTAs() {
		//BankTARepository.findAllUndeleted();
		return null;
		// include sort in caller
		/*
		 * 
		 * List transactions = getHibernateTemplate()
				
				.find(
						"from BankTADao as trans where amount < 0 and deleted = false order by transdate DESC");
		return transactions;
		 */
	}

	// for service
	public void deleteBankTA(Long todelete) {
		// BankTransactionService.deleteBankTA

	}

	public BankTADao getBankTA(Long transid) {
		// bankTARepository.findOne(long);
		return null;
	}

	public List getNoCategoryExpenses() {
		// call BankTARepository.findNoCategoryExpenses
		
		/*List transactions = getHibernateTemplate()
				.find(
						"from BankTADao as trans where amount < 0 and hascat = false and deleted = false order by transdate DESC");

		return transactions;*/
		return null;
	}

	public List retrieveTransactionsContaining(String containing) {
		// bankTARepository.findTransWithDetailLike(detail)
		// note - detail may need to be capitalized
		/*
		String sql = "from BankTADao as trans where  hascat = false and deleted = false and upper(detail) like '%"
				+ containing.toUpperCase() + "%'";
		List transactions = getHibernateTemplate().find(sql);
		return transactions;
		*/
		return null;
	}


	
	// different repo
	public CategoryTADao getCatTA(Long catid) {
		
	//	replace with categoryrepository.findOne(catid);
return null;
	/*
	 * 
	 * 		CategoryTADao cat = (CategoryTADao) getHibernateTemplate().get(
				CategoryTADao.class, catid);
		return cat;
*/
	}

	public void createOrSaveCatTrans(CategoryTADao catta) {
		// call categoryRepository.save(catta)
		
	}

	public void createOrSaveBankTrans(BankTADao trans) {
		// call BankTARepository.save(trans);
		
		
		//getHibernateTemplate().saveOrUpdate(trans);
	}

	
		public List getCategoryExpenses(Long transid) {
			// calls categoryRepository.findByBankTrans();
			
			/*List cattas = getHibernateTemplate().find(
				"from CategoryTADao as trans where banktaid = ?",
				new Object[] { transid });
*/
		return null;
	}

	public CategoryTADao getNewCategoryExpense(Long transactionId) {
		// calls banktransservice.getNewCategooryExpense(transactionId)
		return null;
		
		/*
		CategoryTADao newcat = new CategoryTADao();
		newcat.setBanktaid(transactionId);
		return newcat;*/
	}

	public void deleteCategoryExpense(Long deleteid) {
		// calls banktransservice.deleteCategoryExpense(Long deleteid);
		
		/*
		CategoryTADao cat = getCatTA(deleteid);
		getHibernateTemplate().delete(cat);
*/
	}



	private List<ExpenseDao> getExpenseByCatType(ExpenseCriteria criteria,Long catType) {
		// calls searchService.getExpenseByCatType(ExpenseCriteria criteria,Long catType) 
		
		return null;
	}
	
	
	//different service
	public void deleteCategoryExpenseByTransaction(Long transid) {
		//calls bankService.deleteCategoryExpenseByTransaction(transid);
		/*
		List<CategoryTADao> catexplst = getCategoryExpenses(transid);
		for (Iterator<CategoryTADao> iterator=catexplst.iterator();iterator.hasNext();) {
			CategoryTADao cat = iterator.next();
			deleteCategoryExpense(cat.getId());
		}
		*/
	}
	

// expense criteria
	public List<ExpenseDao> getExpenses(ExpenseCriteria criteria) {
// calls searchService.getExpenses(criteria)
		
		return null;
		/*if (criteria==null) {
			criteria=new ExpenseCriteria();
		}
		
		List<ExpenseDao> expenses = new ArrayList<ExpenseDao>();
		// save orig cat type
		Long origcattype = criteria.getCategorizedType();
		
		
		if (origcattype==null || origcattype.longValue()==ExpenseCriteria.CategorizedType.ALL) {
			// all
			expenses = getExpenseByCatType(criteria,new Long(ExpenseCriteria.CategorizedType.NOCATS));
			List<ExpenseDao> expensescats = getExpenseByCatType(criteria,new Long(ExpenseCriteria.CategorizedType.ONLYCATS));
			expenses.addAll(expensescats);
			criteria.setCategorizedType(origcattype);
		} else {
			expenses = getExpenseByCatType(criteria,origcattype);
		} 
		
		

		return expenses;*/
	}
	
	public List<CategorySummaryDisp> getExpenseTotalByMonth(ExpenseCriteria criteria) {
// call SearchService.getExpenseTotalByMonth(ExpenseCriteria criteria)
		
		return null;
		/*List<CategorySummaryDisp> displays = new ArrayList<CategorySummaryDisp>();
		// create sql
		String summedcol = "catamount";
		StringBuffer sql = new StringBuffer("select month,year, sum(").append(
				summedcol).append(") from expense ");
		sql.append(getWhereClauseForCriteria(criteria, false));
		sql.append("group by month,year");

		// execute sql, and retrieve results
		List results = executeAggregateQuery(sql.toString());

		// populate CategorySummaryDisp objects from results
		if (results != null) {
			for (Iterator iter = results.iterator(); iter.hasNext();) {
				Object[] row = (Object[]) iter.next();
				CategorySummaryDisp catsum = new CategorySummaryDisp();
				Integer month = (Integer) row[0];
				Integer year = (Integer) row[1];
				Double total = (Double) row[2];
				Calendar cal = Calendar.getInstance();
				cal.set(year.intValue(), month.intValue() - 1, 1);
				catsum.setSummaryDate(cal.getTime());
				catsum.setSum(total.doubleValue());
				displays.add(catsum);
			}
		}

		// return list of CategorySummaryDisp objects
		return displays;*/
	}

	public List<CategorySummaryDisp> getExpenseTotalByYear(ExpenseCriteria criteria) {
		// call SearchService.getExpenseTotalByYear(criteria)
		/*
		List<CategorySummaryDisp> displays = new ArrayList<CategorySummaryDisp>();
		// create sql
		String summedcol = "catamount";
		StringBuffer sql = new StringBuffer("select year, sum(").append(
				summedcol).append(") from expense ");
		sql.append(getWhereClauseForCriteria(criteria, false));
		sql.append("group by year");

		// execute sql, and retrieve results
		List results = executeAggregateQuery(sql.toString());

		// populate CategorySummaryDisp objects from results
		if (results != null) {
			for (Iterator iter = results.iterator(); iter.hasNext();) {
				Object[] row = (Object[]) iter.next();
				CategorySummaryDisp catsum = new CategorySummaryDisp();
				Integer year = (Integer) row[0];
				Double total = (Double) row[1];
				Calendar cal = Calendar.getInstance();
				cal.set(year.intValue(), Calendar.JANUARY, 1);
				catsum.setSummaryDate(cal.getTime());
				catsum.setSum(total.doubleValue());
				displays.add(catsum);
			}
		}

		// return list of CategorySummaryDisp objects
		return displays;
		*/
		return null;
	}	
	
	public List<CategorySummaryDisp> getExpenseTotal(ExpenseCriteria criteria) {
		// call SearchService.getExpenseTotal(criteria)
		
		/*
		List<CategorySummaryDisp> displays = new ArrayList<CategorySummaryDisp>();
		// create sql
		String summedcol = "catamount";
		StringBuffer sql = new StringBuffer("select year, sum(").append(
				summedcol).append(") from expense ");
		sql.append(getWhereClauseForCriteria(criteria, false));
		sql.append("group by year");

		// execute sql, and retrieve results
		List results = executeAggregateQuery(sql.toString());

		// populate CategorySummaryDisp objects from results
		if (results != null) {
			for (Iterator iter = results.iterator(); iter.hasNext();) {
				Object[] row = (Object[]) iter.next();
				CategorySummaryDisp catsum = new CategorySummaryDisp();
				Integer year = (Integer) row[0];
				Double total = (Double) row[1];
				Calendar cal = Calendar.getInstance();
				cal.set(year.intValue(), Calendar.JANUARY, 1);
				catsum.setSummaryDate(cal.getTime());
				catsum.setSum(total.doubleValue());
				displays.add(catsum);
			}
		}

		// return list of CategorySummaryDisp objects
		return displays;
		*/
		return null;
	}
	
	/*private List executeAggregateQuery(final String sql) {
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				SQLQuery query = session.createSQLQuery(sql);
				return query.list();
			}
		});

	}*/


	

	
}
