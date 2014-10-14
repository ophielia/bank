package meg.bank.bus.report;


import java.io.File;
import java.util.List;
import java.util.Map;

import meg.bank.bus.dao.CatRelationshipDao;
import meg.bank.bus.dao.CategoryDao;
import meg.bank.bus.dao.CategoryDaoDataOnDemand;
import meg.bank.bus.dao.CategoryRuleDao;
import meg.bank.bus.dao.CategoryRuleDaoDataOnDemand;
import meg.bank.bus.repo.CatRelationshipRepository;
import meg.bank.bus.repo.CategoryRuleRepository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@ContextConfiguration("classpath*:META-INF/spring/*.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@Transactional
public class ReportServiceTest {



@Autowired
CatRelationshipRepository catRelRepo;

@Autowired
ReportService reportService;


@Autowired
CategoryRuleRepository catRuleRep;	

File tmpdir=null;



    @Test
    public void testRunReports() throws Exception {
    	ReportCriteria criteria = new ReportCriteria();
    	// set criteria - for MonthlyTargetsReport
    	criteria.setReportType(ReportService.ReportType.MonthlyTarget);
    	criteria.setMonth("09-2014");
    	criteria.setExcludeNonExpense(true);
    	Map<String,Object> results = reportService.runReport(criteria);
    	Assert.assertNotNull(results);
    }
    
    @Test
    public void testRunYearlyTargetStatus() throws Exception {
    	ReportCriteria criteria = new ReportCriteria();
    	// set criteria - for MonthlyTargetsReport
    	criteria.setReportType(ReportService.ReportType.YearlyTargetStatus);
    	criteria.setYear("2014");
    	Map<String,Object> results = reportService.runReport(criteria);
    	Assert.assertNotNull(results);
    }    

    
}