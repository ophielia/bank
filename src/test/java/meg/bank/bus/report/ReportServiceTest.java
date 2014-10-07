package meg.bank.bus.report;


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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext*.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class ReportServiceTest {

	


@Autowired
CatRelationshipRepository catRelRepo;

@Autowired
ReportService reportService;


@Autowired
CategoryRuleRepository catRuleRep;	



@Before
public void setup() {
	
	
}

    @Test
    public void testRunReports() throws Exception {
    	ReportCriteria criteria = new ReportCriteria();
    	Map<String,Object> results = reportService.runReport(ReportService.ReportType.MonthlyTarget,criteria);
    	Assert.assertNotNull(results);
    }

    
}