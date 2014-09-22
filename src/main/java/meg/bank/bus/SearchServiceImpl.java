package meg.bank.bus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import meg.bank.bus.dao.CategoryDao;
import meg.bank.bus.dao.ExpenseDao;
import meg.bank.bus.imp.ImportManager;
import meg.bank.bus.report.CategorySummaryDisp;
import meg.bank.bus.ExpenseCriteria;

@Service
public class SearchServiceImpl implements SearchService {

	
    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    private CategoryService catService;
    
	private List<ExpenseDao> getExpenseByCatType(ExpenseCriteria criteria,Long catType) {
		// calls searchService.getExpenseByCatType(ExpenseCriteria criteria,Long catType) 
		// base statement
		StringBuffer sql = new StringBuffer("from ExpenseDao as exp ");
		criteria.setCategorizedType(catType);
		sql.append(getWhereClauseForCriteria(criteria, true));
		sql.append(" order by transDate desc, transid");

		// get expenses
		Query query = entityManager.createNativeQuery(sql.toString(),"ExpenseResult");
		@SuppressWarnings("unchecked")
		List<ExpenseDao> expenses = query.getResultList();
		return expenses;
	}

	/* (non-Javadoc)
	 * @see meg.bank.bus.SearchService#getExpenses(meg.bank.bus.ExpenseCriteria)
	 */
	@Override
	public List<ExpenseDao> getExpenses(ExpenseCriteria criteria) {
		if (criteria==null) {
			criteria=new ExpenseCriteria();
		}
		
		List<ExpenseDao> expenses = new ArrayList<ExpenseDao>();
		// save orig cat type
		Long origcattype = criteria.getCategorizedType();
		
		// check if category list has been filled, if necessary
		if (criteria.getShowSubcats()!=null && criteria.getShowSubcats().booleanValue()) {
			if (criteria.getCategoryLevelList()==null) {
				// want to show subcategories, but they haven't been filled in - fix this
				CategoryLevel catlevel = catService.getAsCategoryLevel(criteria.getCategory());
				CategoryDao cat= catlevel.getCategory();
				List<CategoryLevel> subcats = catService.getAllSubcategories(cat);
				criteria.setCategoryLevelList(subcats);
			} 
		}
		
		if (origcattype==null || origcattype.longValue()==ExpenseCriteria.CategorizedType.ALL) {
			// all
			expenses = newGetExpenseByCatType(criteria,new Long(ExpenseCriteria.CategorizedType.NOCATS));
			List<ExpenseDao> expensescats = newGetExpenseByCatType(criteria,new Long(ExpenseCriteria.CategorizedType.ONLYCATS));
			expenses.addAll(expensescats);
			criteria.setCategorizedType(origcattype);
		} else {
			expenses = newGetExpenseByCatType(criteria,origcattype);
		} 
		
		

		return expenses;
	}
	
	/* (non-Javadoc)
	 * @see meg.bank.bus.SearchService#getExpenseTotalByMonth(meg.bank.bus.ExpenseCriteria)
	 */
	@Override
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
	
	/* (non-Javadoc)
	 * @see meg.bank.bus.SearchService#getExpenseTotalByYear(meg.bank.bus.ExpenseCriteria)
	 */
	@Override
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
	
	/* (non-Javadoc)
	 * @see meg.bank.bus.SearchService#getExpenseTotal(meg.bank.bus.ExpenseCriteria)
	 */
	@Override
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
	
	
	public List<ExpenseDao> getExpenseListByIds(List<String> idlist) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<ExpenseDao> c = cb.createQuery(ExpenseDao.class);
		Root<ExpenseDao> exp = c.from(ExpenseDao.class);
		c.select(exp);

		if (idlist != null) {
			// making space for parameters
			ParameterExpression<Collection> ids = cb.parameter(Collection.class);
			c.where(exp.get("id").in(ids));
			Collection<String> idsParameter = new ArrayList<String> ();
			for (String param:idlist) {
				idsParameter.add(param);
			}
					
			// creating the query
			TypedQuery<ExpenseDao> q = entityManager.createQuery(c);

			// setting the parameters
			q.setParameter(ids, idsParameter);

			
			return q.getResultList();

		}

		return null;
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
	
	private List executeAggregateQuery(final String sql) {
		 return entityManager.createNativeQuery(sql).getResultList();
	}
	
	private List<ExpenseDao> newGetExpenseByCatType(ExpenseCriteria criteria,
			Long catType) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<ExpenseDao> c = cb.createQuery(ExpenseDao.class);
		Root<ExpenseDao> exp = c.from(ExpenseDao.class);
		c.select(exp);
		setOrderBy(criteria,cb,c,exp);
		
		
		if (criteria != null) {
			// put together where clause
			List<Predicate> whereclause = new ArrayList<Predicate>();
			// set the cattype in the criteria
			criteria.setCategorizedType(catType);

			// making space for parameters
			// date start
			if (criteria.getDateStart() != null) {
				ParameterExpression<Date> param = cb.parameter(Date.class,
						"transdate");
				whereclause.add(cb.greaterThanOrEqualTo(
						exp.<Date> get("transdate"), param));
				
			}
			// categorized type
			if (criteria.getCategorizedType() != null &&  criteria.getCategorizedType()!=ExpenseCriteria.CategorizedType.ALL) {
				// categorized type exists - add to sql
				ParameterExpression<Boolean> param = cb.parameter(Boolean.class,
						"hascat");
				whereclause.add(cb.equal(exp.get("hascat"), param));
			}			
			// set category or categories
			if (criteria.showSingleCategory()) {
				ParameterExpression<Long> param = cb.parameter(Long.class,
						"catid");
				whereclause.add(cb.equal(exp.get("catid"), param));
			} else if (criteria.showListOfCategories()) {
				List<Long> catids = new ArrayList<Long>();
				for (Iterator iter = criteria.getCategoryLevelList().iterator(); iter
						.hasNext();) {
					CategoryLevel catlvl = (CategoryLevel) iter.next();
					CategoryDao cat = catlvl.getCategory();
					catids.add(cat.getId());
				}

				Expression<Long> param = exp.<Long>get("catid");
				whereclause.add(exp.get("catid").in(catids));
			}	
			// non-expense handling
			if (criteria.getExcludeNonExpense() != null) {
				if (criteria.getExcludeNonExpense().booleanValue()) {
					ParameterExpression<Boolean> param = cb.parameter(Boolean.class,
							"nonexpense");
					whereclause.add(cb.equal(exp.get("nonexpense"), param));
				}
			}	
			// source
			if (criteria.showBySource()) {
				ParameterExpression<Integer> param = cb.parameter(Integer.class,
						"source");
				whereclause.add(cb.equal(exp.get("source"), param));
			} 		
			// transactiontype 			
			if (criteria.getTransactionType()!=null) {
				if (criteria.getTransactionType().longValue()==
					ExpenseCriteria.TransactionType.CREDITS) {
					ParameterExpression<Double> param = cb.parameter(Double.class,
							"transtotal");
					whereclause.add(cb.gt(exp.<Double>get("transtotal"), param));
				} else if (criteria.getTransactionType().longValue()==
					ExpenseCriteria.TransactionType.DEBITS) {
					ParameterExpression<Double> param = cb.parameter(Double.class,
							"transtotal");
					whereclause.add(cb.lt(exp.<Double>get("transtotal"), param));
				}
			}
			
			
			// creating the query
			c.where(cb.and(whereclause.toArray(new Predicate[whereclause.size()])));
			TypedQuery<ExpenseDao> q = entityManager.createQuery(c);

			// setting the parameters
			// date start
			if (criteria.getDateStart() != null) {
				q.setParameter("transdate", criteria.getDateStart());
			}
			// categorized type
			if (criteria.getCategorizedType() != null && criteria.getCategorizedType()!=ExpenseCriteria.CategorizedType.ALL) {
				if (criteria.getCategorizedType().longValue() == ExpenseCriteria.CategorizedType.NOCATS) {
					q.setParameter("hascat", false);
				} else if (criteria.getCategorizedType().longValue() == ExpenseCriteria.CategorizedType.ONLYCATS) {
					q.setParameter("hascat", true);
				}
			}
			// set category or categories
			if (criteria.showSingleCategory()) {
				q.setParameter("catid", criteria.getCategory());
			}			
			// non-expense handling
			if (criteria.getExcludeNonExpense() != null) {
				if (criteria.getExcludeNonExpense().booleanValue()) {
					q.setParameter("nonexpense", false);
				}
			}			
			// source
			if (criteria.showBySource()) {
				Integer source=new Integer(criteria.getSource().intValue());
				q.setParameter("source", source);
			} 	
			// transactiontype 			
			if (criteria.getTransactionType()!=null) {
				if (criteria.getTransactionType().longValue()==
					ExpenseCriteria.TransactionType.CREDITS) {
					q.setParameter("transtotal", new Double(0));
				} else if (criteria.getTransactionType().longValue()==ExpenseCriteria.TransactionType.DEBITS) {
					q.setParameter("transtotal", new Double(0));
				}
			}			
			
			return q.getResultList();

		}

		return null;
	}

	private void setOrderBy(ExpenseCriteria criteria, CriteriaBuilder cb,
			CriteriaQuery<ExpenseDao> query, Root<ExpenseDao> root) {
		
		List<String> sortstrings=new ArrayList<String>();
		// set sort string
		if (criteria.getSorttype() != null) {
			if (criteria.getSorttype().equals(ExpenseCriteria.SortType.Amount)) {
				sortstrings.add("displayamount");
			} else if (criteria.getSorttype().equals(
					ExpenseCriteria.SortType.Category)) {
				sortstrings.add("catName");
			} else if (criteria.getSorttype().equals(
					ExpenseCriteria.SortType.Date)) {
				sortstrings.add("transdate");
				sortstrings.add("transid");
			} else if (criteria.getSorttype().equals(
					ExpenseCriteria.SortType.Detail)) {
				sortstrings.add("detail");
			}
		} else {
			sortstrings.add("transdate");
			sortstrings.add("transid");
		}
		
		// now, put into list of order objects with direction
		List<Order> orderstatements = new ArrayList<Order>();
		for (String sortparam:sortstrings) {
			if (criteria.getSortdir()!=null && criteria.getSortdir().longValue()==ExpenseCriteria.SortDirection.Asc) {
				 Order neworder = cb.asc(root.get(sortparam));
				 orderstatements.add(neworder);
			} else {
				Order neworder = cb.desc(root.get(sortparam));
				 orderstatements.add(neworder);
			}
		}
		query.orderBy(orderstatements);
		
	}

	@Override
	public List<ExpenseDao> getAllExpenses() {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<ExpenseDao> c = cb.createQuery(ExpenseDao.class);
		Root<ExpenseDao> exp = c.from(ExpenseDao.class);
		c.select(exp);
		c.orderBy(cb.desc(exp.get("transdate")), cb.asc(exp.get("transid")));

		// creating the query
		TypedQuery<ExpenseDao> q = entityManager.createQuery(c);

		return q.getResultList();

	}

}	

