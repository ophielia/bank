package meg.util.imp;

import java.util.List;

public interface MapConfig {

	String getDestinationClassName();

	List<FieldMapping> getMappings();

	String getHelperClassName();

}
