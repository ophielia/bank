package meg.bank.bus.report;

import java.util.List;
import java.util.Map;

public interface ReportService {

	public final class ReportType {
		public static final Long MonthlyTarget = 1L;
		public static final Long YearlyTargetStatus = 2L;
	}	
	
	

	public abstract List<String> getAllMonthsAsList();

	public abstract List<String> getAllYearsAsList();


	public abstract Map<String, Object> runReport(
			ReportCriteria reportCriteria);


}