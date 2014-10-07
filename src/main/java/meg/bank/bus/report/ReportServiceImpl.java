package meg.bank.bus.report;

import java.util.Date;
import java.util.List;
import java.util.Map;

import meg.bank.bus.SearchService;
import meg.bank.util.DateUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportServiceImpl implements ReportService {


	@Autowired
	private SearchService searchService;
	

	@Override
	public List<String> getAllMonthsAsList() {
		Date oldest = searchService.getFirstTransDate();
		Date newest = searchService.getMostRecentTransDate();

		return DateUtils.getMonthsForSelect(oldest, newest);
	}
	
	@Override
	public List<String> getAllYearsAsList() {
		Date oldest = searchService.getFirstTransDate();
		Date newest = searchService.getMostRecentTransDate();

		return DateUtils.getYearsForSelect(oldest, newest);
	}

	@Override
	public Map<String, Object> runReport(Long reporttype,
			ReportCriteria reportCriteria) {
		// TODO Auto-generated method stub
		return null;
	}	
	

}
