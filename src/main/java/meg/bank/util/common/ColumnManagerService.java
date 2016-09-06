package meg.bank.util.common;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import meg.bank.util.common.db.ColumnValueDao;

public interface ColumnManagerService {

	HashMap<Long, String> getColumnDisplayByNumberHash(String lookup,
			boolean displayonly);



	public abstract List<ColumnValueDao> getColumnValueList(String lookup);

	public abstract List<ColumnValueDao> getColumnValueList(String lookup, boolean displayonly);
	
	public abstract String getDisplayForValue(String lookup, String value);





}