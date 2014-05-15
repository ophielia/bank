package meg.bank.util.common.repo;

import java.util.List;

public interface ColumnValueManagerDao {

	List getColumnValuesForKey(String lookup, boolean displayonly);

}
