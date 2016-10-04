package meg.bank.bus;


import java.util.ArrayList;
import java.util.List;

import meg.bank.bus.dao.TargetDetailDao;
import meg.bank.bus.dao.TargetDetailDaoDataOnDemand;
import meg.bank.bus.dao.TargetGroupDao;
import meg.bank.bus.repo.TargetDetailRepository;
import meg.bank.bus.repo.TargetGroupRepository;
import meg.bank.web.model.TargetModel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@ContextConfiguration(locations = { "classpath*:/spring/application-config*.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@Configurable
public class TargetServiceTest {

	
@Autowired
TargetService targetService;

@Autowired
TargetGroupRepository targetGrpRep;

@Autowired
TargetDetailRepository targetDetailRep;


@Test
public void testCreateTargetGroup() {
	// create TargetGroup
	TargetGroupDao group = new TargetGroupDao();
	group.setTargettype(TargetService.TargetType.Month);
	group.setName("tTest");
	group.setDescription("tDescription");
	group.setIsdefault(new Boolean(true));
	group.setMonthtag("Jan");
	
	// service call
	group = targetService.saveOrUpdateTargetGroup(group);
	Long id = group.getId();
	// assert id not null
	Assert.assertNotNull(id);
	// retrieve TargetGroup with same id
	TargetGroupDao test = targetGrpRep.findOne(id);
	Assert.assertNotNull(test);
	Assert.assertEquals("tTest", test.getName());
}

@Test
public void testAddTargetDetailToGroup() {
	// create group
	TargetGroupDao group = new TargetGroupDao();
	group.setTargettype(TargetService.TargetType.Month);
	group.setName("tTest");
	group.setDescription("tDescription");
	group.setIsdefault(new Boolean(true));
	group.setMonthtag("Jan");
	group = targetService.saveOrUpdateTargetGroup(group);
	// create detail
	TargetDetailDao detail = new TargetDetailDao();
	detail.setCatid(new Long(11));
	detail.setAmount(new Double(11));
	// service call
	detail = targetService.addTargetDetailToGroup(detail, group);
	
	// load group
	TargetModel model = targetService.loadTargetModel(group.getId());
	
	// verify one detail
	List<TargetDetailDao> details = model.getTargetdetails();
	Assert.assertNotNull(details);
	Assert.assertEquals(1, details.size());
	// verify detail matches
	TargetDetailDao first = details.get(0);
	Assert.assertEquals(new Long(11),first.getCatid());
	Assert.assertEquals(new Double(11),first.getAmount());
}

@Test
public void testDeleteTargetDetail() {
	// create group
	TargetGroupDao group = new TargetGroupDao();
	group.setTargettype(TargetService.TargetType.Month);
	group.setName("tTest");
	group.setDescription("tDescription");
	group.setIsdefault(new Boolean(true));
	group.setMonthtag("Jan");
	group = targetService.saveOrUpdateTargetGroup(group);
	// create detail
	TargetDetailDao detail = new TargetDetailDao();
	detail.setCatid(new Long(11));
	detail.setAmount(new Double(11));
	// service call
	detail = targetService.addTargetDetailToGroup(detail, group);	
	// add more details
	TargetDetailDaoDataOnDemand tddod = new TargetDetailDaoDataOnDemand();
	TargetDetailDao det2 = tddod.getNewTransientTargetDetailDao(12);
	TargetDetailDao det3 = tddod.getNewTransientTargetDetailDao(13);
	TargetDetailDao det4 = tddod.getNewTransientTargetDetailDao(14);
	// make list 
	List<Long> detailids = new ArrayList<Long>();
	det2=targetService.addTargetDetailToGroup(det2,group);
	det3=targetService.addTargetDetailToGroup(det3,group);
	det4=targetService.addTargetDetailToGroup(det4,group);
	detailids.add(det2.getId());
	detailids.add(det3.getId());
	detailids.add(det4.getId());
	// get TargetDetails by group, and assert 4
	List<TargetDetailDao> testlist = targetDetailRep.findByTargetGroup(group);
	Assert.assertEquals(4, testlist.size());
	
	// first delete details in list
	targetService.deleteTargetDetails(detailids);
	
	// get TargetDetails by group, and assert only 1
	testlist = targetDetailRep.findByTargetGroup(group);
	Assert.assertEquals(1, testlist.size());

	// now delete details singly
	targetService.deleteTargetDetail(detail.getId());
	
	// get TargetDetails by group, and assert 0
	testlist = targetDetailRep.findByTargetGroup(group);
	Assert.assertEquals(0, testlist.size());	
}

/*
 * public List<TargetGroupDao> getTargetGroupList(Long targettype) {
public TargetGroupDao getDefaultTargetGroup(Long targettype) {
public TargetGroupDao getTargetGroup(Long editid) {
public TargetGroupDao loadTarget(Long loadid) {
public TargetGroupDao loadTargetForMonth(String month) {
public TargetGroupDao loadTargetForYear(String year) {
public class TargetServiceImpl implements TargetService {
public void copyTargetGroup(Long targettype) {
public void deleteTargetDetails(List<Long> deleted) {
public void deleteTargetGroup(Long editid) {
public void saveTarget(TargetGroupDao target) {
public void updateDefaultTargetGroup(Long editid, Long targettype)

*/



}