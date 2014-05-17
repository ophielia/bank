package meg.bank.bus.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import meg.bank.bus.CategoryLevel;
import meg.bank.bus.ExpenseCriteria;
import meg.bank.bus.ImportManager;
import meg.bank.bus.dao.BankTADao;
import meg.bank.bus.dao.CategoryDao;
import meg.bank.bus.dao.CategoryTADao;
import meg.bank.bus.dao.ExpenseDao;
import meg.bank.bus.repo.CategoryRepository;
import meg.bank.bus.report.CategorySummaryDisp;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class TransactionManagerDaoHib  {

	


	// service and repo
	public boolean duplicateExists(BankTADao trans) {
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
		return exists;
	}

	public Date getMostRecentTransDate() {
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

	}

	public Date getFirstTransDate() {
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
	}


	// !! repo only !! //
	private Session bankTADaoRepository;
	private CategoryRepository categoryRep;
	public List getAllBankTAs() {
		//BankTARepository.findAllUndeleted();
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
		return cattas;
	}

	public CategoryTADao getNewCategoryExpense(Long transactionId) {
		CategoryTADao newcat = new CategoryTADao();
		newcat.setBanktaid(transactionId);
		return newcat;
	}

	public void deleteCategoryExpense(Long deleteid) {
		CategoryTADao cat = getCatTA(deleteid);
		getHibernateTemplate().delete(cat);

	}



	private List<ExpenseDao> getExpenseByCatType(ExpenseCriteria criteria,Long catType) {
		// base statement
		StringBuffer sql = new StringBuffer("from ExpenseDao as exp ");
		criteria.setCategorizedType(catType);
		sql.append(getWhereClauseForCriteria(criteria, true));
		sql.append(" order by transDate desc, transid");

		// get expenses
		List<ExpenseDao> expenses = getHibernateTemplate().find(sql.toString());
		return expenses;
	}
	
	
	//different service
	public void deleteCategoryExpenseByTransaction(Long transid) {
		List<CategoryTADao> catexplst = getCategoryExpenses(transid);
		for (Iterator<CategoryTADao> iterator=catexplst.iterator();iterator.hasNext();) {
			CategoryTADao cat = iterator.next();
			deleteCategoryExpense(cat.getId());
		}
	}
	

// expense criteria
	public List<ExpenseDao> getExpenses(ExpenseCriteria criteria) {
		if (criteria==null) {
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
		
		

		return expenses;
	}
	
	public List<CategorySummaryDisp> getExpenseTotalByMonth(ExpenseCriteria criteria) {
		List<CategorySummaryDisp> displays = new ArrayList<CategorySummaryDisp>();
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
		return displays;
	}

	public List<CategorySummaryDisp> getExpenseTotalByYear(ExpenseCriteria criteria) {
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
	}	
	
	public List<CategorySummaryDisp> getExpenseTotal(ExpenseCriteria criteria) {
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
	}
	
	private List executeAggregateQuery(final String sql) {
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				SQLQuery query = session.createSQLQuery(sql);
				return query.list();
			}
		});

	}

	private StringBuffer getWhereClauseForCriteria(ExpenseCriteria criteria,
			boolean hibernatereplace) {
		// base statement
		StringBuffer sql = new StringBuffer("where bdeleted = false ");

		// insert criteria info
		if (criteria != null) {
			if (criteria.getDateStart() != null) {
				String datestr = hibernatereplace ? "transDate" : "ddate";
				// dates exist - add to sql
				sql.append(" and " + datestr + " >='").append(
						criteria.getDateStartAsString()).append("' ");
				sql.append(" and " + datestr + " <'").append(
						criteria.getDateEndAsString()).append("' ");
			}
			if (criteria.getCategorizedType() != null) {
				String colstr = hibernatereplace ? "hascat" : "bhascat";
				// categorized type exists - add to sql
				if (criteria.getCategorizedType().longValue() == ExpenseCriteria.CategorizedType.NOCATS) {
					sql.append(" and " + colstr + " = false ");
				} else if (criteria.getCategorizedType().longValue() == ExpenseCriteria.CategorizedType.ONLYCATS) {
					sql.append(" and " + colstr + " = true ");
				}
			}
			if (criteria.getCategory() != null && (criteria.getShowSubcats()==null || (criteria.getShowSubcats()!=null && !criteria.getShowSubcats().booleanValue()))) {
				Long catid = criteria.getCategory();
				sql.append(" and catid=").append(catid);
			} else if (criteria.getCategoryLevelList() != null
					&& criteria.getCategoryLevelList().size() > 0) {
				sql.append(" and catid in (");
				for (Iterator iter = criteria.getCategoryLevelList().iterator(); iter
						.hasNext();) {
					CategoryLevel catlvl = (CategoryLevel) iter.next();
					CategoryDao cat = catlvl.getCategory();
					sql.append(cat.getId()).append(",");
				}
				sql.setLength(sql.length() - 1);
				sql.append(")");
			}
			if (criteria.getExcludeNonExpense() != null) {
				if (criteria.getExcludeNonExpense().booleanValue()) {
					sql.append(" and nonexpense = false ");
				}
			}
			if (criteria.getSource()!=null && criteria.getSource().longValue()!=ImportManager.ImportClient.All) {
				sql.append(" and source =  ").append(criteria.getSource());
			} 
			if (criteria.getTransactionType()!=null) {
				if (criteria.getTransactionType().longValue()==
					ExpenseCriteria.TransactionType.CREDITS) {
					sql.append(" and transtotal>0 ");
				} else if (criteria.getTransactionType().longValue()==
					ExpenseCriteria.TransactionType.DEBITS) {
					sql.append(" and transtotal<0 ");
				}
			}
		}

		return sql;
	}
	

	
}
