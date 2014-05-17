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

}
