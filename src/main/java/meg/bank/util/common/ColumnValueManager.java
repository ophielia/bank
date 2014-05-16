package meg.bank.util.common;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import meg.bank.util.common.db.ColumnKeyDao;
import meg.bank.util.common.db.ColumnValueDao;
import meg.bank.util.common.repo.ColumnValueManagerDao;

public class ColumnValueManager {

	private ColumnValueManagerDao cvmd;

	public Hashtable getColumnHash(String lookup) {
		return getColumnHash(lookup, true);
	}

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

	public List getColumnValueList(String lookup) {
		return getColumnValueList(lookup, true);
	}

	public List getColumnValueList(String lookup, boolean displayonly) {
		// retrieve all displays for lookup
		return cvmd.getColumnValuesForKey(lookup, displayonly);
	}
	
	public void setColumnValueManagerDao(ColumnValueManagerDao cvmd) {
		this.cvmd = cvmd;
	}
}
