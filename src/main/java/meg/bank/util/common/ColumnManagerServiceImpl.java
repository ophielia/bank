package meg.bank.util.common;

import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import meg.bank.bus.BankTransactionService;
import meg.bank.util.DateUtils;
import meg.bank.util.common.db.ColumnValueDao;
import meg.bank.util.common.repo.ColumnValueRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ColumnManagerServiceImpl implements ColumnManagerService {

	@Autowired
	private ColumnValueRepository cvmd;

	@Autowired
	private BankTransactionService transMan;
	
	@Override
	public Hashtable getColumnHash(String lookup) {
		return getColumnHash(lookup, true);
	}

	@Override
	public Hashtable getColumnHash(String lookup, boolean displayonly) {
		Hashtable hash = new Hashtable();
		// retrieve all displays for lookup
		List displays = cvmd.getColumnValuesForKey(lookup, displayonly);
		if (displays != null) {
			for (Iterator iter = displays.iterator(); iter.hasNext();) {
				ColumnValueDao cv = (ColumnValueDao) iter.next();
				hash.put(cv.getValue(), cv.getDisplay());
			}

		}
		return hash;
	}

	@Override
	public List<ColumnValueDao> getColumnValueList(String lookup) {
		return getColumnValueList(lookup, true);
	}

	@Override
	public List<ColumnValueDao> getColumnValueList(String lookup, boolean displayonly) {
		// retrieve all displays for lookup
		return cvmd.getColumnValuesForKey( lookup,displayonly);
	}

	@Override
	public String getDisplayForValue(String lookup, String value) {
		ColumnValueDao cv = cvmd.getColumnValuesForKeyAndValue(lookup, value);
		return cv.getDisplay();
	}


	

}
