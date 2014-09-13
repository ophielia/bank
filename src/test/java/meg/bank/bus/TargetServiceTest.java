package meg.bank.bus;


import java.util.List;

import meg.bank.bus.dao.TargetDetailDao;
import meg.bank.bus.dao.TargetGroupDao;
import meg.bank.bus.repo.CategoryRuleRepository;
import meg.bank.bus.repo.TargetGroupRepository;
import meg.bank.web.model.TargetModel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext*.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class TargetServiceTest {

	
@Autowired
TargetService targetService;

@Autowired
TargetGroupRepository targetGrpRep;


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