package meg.bank.util.common;

import java.util.Hashtable;
import java.util.List;

import meg.bank.util.common.db.ColumnValueDao;

public interface ColumnManagerService {

	public abstract Hashtable getColumnHash(String lookup);

	public abstract Hashtable getColumnHash(String lookup, boolean displayonly);

	public abstract List<ColumnValueDao> getColumnValueList(String lookup);

	public abstract List<ColumnValueDao> getColumnValueList(String lookup, boolean displayonly);


}