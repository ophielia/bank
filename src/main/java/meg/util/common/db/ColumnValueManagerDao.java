package meg.util.common.db;

import java.util.List;

public interface ColumnValueManagerDao {

	List getColumnValuesForKey(String lookup, boolean displayonly);

}
