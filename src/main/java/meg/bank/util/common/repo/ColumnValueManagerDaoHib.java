package meg.bank.util.common.repo;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class ColumnValueManagerDaoHib extends HibernateDaoSupport implements
		ColumnValueManagerDao {

	public List getColumnValuesForKey(String lookup, boolean displayonly) {
		List result = getHibernateTemplate()
				.find(
						"from ColumnValueDao as cv where cv.isActive = ? and cv.keyid = (select id from ColumnKeyDao as ck where ck.lookup = ?) order by cv.disporder",
						new Object[] { new Boolean(displayonly), lookup });

		return result;
	}

}
