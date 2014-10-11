package meg.bank.bus.report;

import java.util.Date;
import java.util.List;
import java.util.Map;

import meg.bank.bus.CategoryService;
import meg.bank.bus.SearchService;
import meg.bank.bus.TargetService;
import meg.bank.util.DateUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ReportServiceImpl implements ReportService {

    @Value("${document.tmp.path}")
    private String tmpdir;

	@Autowired
	private SearchService searchService;
	
	@Autowired
	protected CategoryService categoryService;
	
	@Autowired
	protected TargetService targetService;	
	
	

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
	public  Map<String, Object> runReport(ReportCriteria reportCriteria) {
		// Retrieve Report for reporttype
		Report report = retrieveReport(reportCriteria);

		if (report!=null) {
			// run crunch numbers
			Map<String,Object> results = report.crunchNumbers();
			
			// return result
			return results;
		}
		return null;
	}

	private Report retrieveReport(ReportCriteria reportCriteria) {
		AbstractReport report = null;
		// get reporttype
		Long reporttype = reportCriteria.getReportType();
		// set image directory (to tmpdir)
		reportCriteria.setImageDir(tmpdir);
		// get appropriate report
		if (reporttype!=null) {
			// get appropriate report for reporttype
			if (reporttype.longValue()==ReportService.ReportType.MonthlyTarget.longValue()) {
				report = new MonthlyTargetsReport(reportCriteria,searchService,categoryService,targetService);
			}
			
			return report;
		}

		return null;
	}	
	

}
