package meg.bank.util.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import meg.bank.util.common.db.ColumnValueDao;
import meg.bank.util.common.repo.ColumnValueRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ColumnManagerServiceImpl implements ColumnManagerService {

	@Autowired
	private ColumnValueRepository cvmd;



	@Override
	public HashMap<Long, String> getColumnDisplayByNumberHash(String lookup,
			boolean displayonly) {
		// retrieve all displays for lookup
		if (displayonly) {
			List<ColumnValueDao> displays = cvmd.getActiveColumnValuesForKey(lookup,
					displayonly);

			return fillHashMap(displays);
		} else {
			List<ColumnValueDao> displays = cvmd.getColumnValuesForKey(lookup);
			return fillHashMap(displays);
		}
		
	}

	private HashMap<Long, String> fillHashMap(List<ColumnValueDao> displays ) {
		HashMap<Long, String> hash = new HashMap<Long, String>();
		if (displays != null) {
			for (Iterator<ColumnValueDao> iter = displays.iterator(); iter
					.hasNext();) {
				ColumnValueDao cv = (ColumnValueDao) iter.next();
				Long key = new Long(cv.getValue());
				hash.put(key, cv.getDisplay());
			}

		}
		return hash;
	}
	@Override
	public List<ColumnValueDao> getColumnValueList(String lookup) {
		return getColumnValueList(lookup, true);
	}

	@Override
	public List<ColumnValueDao> getColumnValueList(String lookup,
			boolean displayonly) {
		// retrieve all displays for lookup
		if ( displayonly ) {
			return cvmd.getActiveColumnValuesForKey(lookup, displayonly);
		}else {
			return cvmd.getColumnValuesForKey(lookup);
		}
		
	}

	@Override
	public String getDisplayForValue(String lookup, String value) {
		ColumnValueDao cv = cvmd.getColumnValuesForKeyAndValue(lookup, value);
		return cv.getDisplay();
	}

}
