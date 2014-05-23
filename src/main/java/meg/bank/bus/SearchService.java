package meg.bank.bus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import meg.bank.bus.dao.CategoryDao;
import meg.bank.bus.dao.ExpenseDao;
import meg.bank.bus.repo.ExpenseRepository;
import meg.bank.bus.report.CategorySummaryDisp;
import meg.bank.bus.ExpenseCriteria;

public class SearchService {

	@Autowired
	ExpenseRepository expenseRepository;
	
	private List<ExpenseDao> getExpenseByCatType(ExpenseCriteria criteria,Long catType) {
		// calls searchService.getExpenseByCatType(ExpenseCriteria criteria,Long catType) 
		// base statement
		StringBuffer sql = new StringBuffer("from ExpenseDao as exp ");
		criteria.setCategorizedType(catType);
		sql.append(getWhereClauseForCriteria(criteria, true));
		sql.append(" order by transDate desc, transid");

		// get expenses
		List<ExpenseDao> expenses = getHibernateTemplate().find(sql.toString());
		return expenses;
	}

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
	return (List) getHibernateTemplate().execute(new HibernateCallback() {
		public Object doInHibernate(Session session)
				throws HibernateException, SQLException {
			SQLQuery query = session.createSQLQuery(sql);
			return query.list();
		}
	});

}	
}
