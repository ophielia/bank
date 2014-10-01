package meg.bank.bus;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import meg.bank.bus.dao.BankTADao;
import meg.bank.bus.dao.BankTADaoDataOnDemand;
import meg.bank.bus.dao.CategoryDao;
import meg.bank.bus.dao.CategoryTADao;
import meg.bank.bus.dao.QuickGroup;
import meg.bank.bus.dao.QuickGroupDetail;
import meg.bank.bus.repo.BankTARepository;
import meg.bank.bus.repo.CategoryTARepository;
import meg.bank.bus.repo.QuickGroupDetailRepository;
import meg.bank.bus.repo.QuickGroupRepository;
import meg.bank.web.model.QuickGroupModel;

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
public class QuickGroupServiceTest {

	@Autowired
	QuickGroupService quickGroupService;

	@Autowired
	CategoryService catService;

	@Autowired
	SearchService searchService;

	@Autowired
	BankTARepository bankRepo;

	@Autowired
	CategoryTARepository catExpRepo;

	@Autowired
	private QuickGroupRepository qcRepo;

	@Autowired
	private QuickGroupDetailRepository qcDetRepo;
	
	CategoryDao tCat;
	BankTADao withcategorized;
	BankTADao withoutcategorized;

	List<CategoryDao> randomcats;
	HashMap<Long,CategoryDao> categoryref;
	
	@Before
	public void setup() {
		tCat = catService.addCategory("tCat", "", false, true);

		// trans with category
		// make BankTrans
		BankTADaoDataOnDemand bDod = new BankTADaoDataOnDemand();
		withcategorized = bDod.getNewTransientBankTADao(12);
		withcategorized.setAmount(new Double(200.0*-1));
		withcategorized=bankRepo.saveAndFlush(withcategorized);

		// make CategoryDao
		CategoryTADao cat = new CategoryTADao();
		cat.setCatid(tCat.getId());
		cat.setAmount(-100D);
		cat.setCreatedon(new Date());
		cat.setBanktrans(withcategorized);
		cat.setBanktrans(withcategorized);
		cat = catExpRepo.saveAndFlush(cat);
		cat = new CategoryTADao();
		cat.setCatid(tCat.getId());
		cat.setAmount(-50D);
		cat.setCreatedon(new Date());
		cat.setBanktrans(withcategorized);
		cat.setBanktrans(withcategorized);
		cat = catExpRepo.saveAndFlush(cat);
		cat = new CategoryTADao();
		cat.setCatid(tCat.getId());
		cat.setAmount(-50D);
		cat.setCreatedon(new Date());
		cat.setBanktrans(withcategorized);
		cat.setBanktrans(withcategorized);
		cat = catExpRepo.saveAndFlush(cat);		


		// trans without category
		bDod = new BankTADaoDataOnDemand();
		withoutcategorized = bDod.getNewTransientBankTADao(12);
		withoutcategorized = bankRepo.saveAndFlush(withoutcategorized);
		
		randomcats=catService.getCategories(true);
		categoryref=catService.getCategoriesAsMap();
		
	}

	@Test
	public void testCreateQuickGroupFromExpense() {
		// get id for withcategorized
		Long transid = withcategorized.getId();
		// make service call
		QuickGroupModel testmodel = quickGroupService.createQuickGroupFromExpense(transid);
		// model not null
		Assert.assertNotNull(testmodel);
		// check that model has 3 detailrows
		List<QuickGroupDetail> details = testmodel.getDetails();
		Assert.assertEquals(3, details.size());
		// check that detailrow amounts add up to 100
		double total=0;
		for (QuickGroupDetail detail:details) {
			total+=detail.getPercentage().doubleValue();
		}
		Assert.assertTrue(100==total);
		
	}
	
	@Test
	public void testLoadQuickGroupModel() {
		// create QuickGroup
		QuickGroup quickgroup = new QuickGroup();
		quickgroup.setName("beepbop");
		quickgroup = qcRepo.saveAndFlush(quickgroup);
		// create two quickgroupdetails
		QuickGroupDetail detail = new QuickGroupDetail();
		CategoryDao cat = randomcats.get(2);
		detail.setCatid(cat.getId());
		detail.setPercentage(75D);
		detail.setQuickgroup(quickgroup);
		qcDetRepo.save(detail);
		detail = new QuickGroupDetail();
		cat = randomcats.get(4);
		detail.setCatid(cat.getId());
		detail.setPercentage(25D);
		detail.setQuickgroup(quickgroup);
		qcDetRepo.save(detail);

		// service call
		QuickGroupModel testmodel = quickGroupService.loadQuickGroupModelForId(quickgroup.getId());
		
		// Assert not null
		Assert.assertNotNull(testmodel);
		// assert name matches
		Assert.assertEquals("beepbop", testmodel.getName());
		// assert has 2 details
		Assert.assertNotNull(testmodel.getDetails());
		Assert.assertEquals(2, testmodel.getDetails().size());
	}	
	
	@Test
	public void testSaveQuickGroupFromModel() {
		CategoryDao cat1=randomcats.get(3);
		CategoryDao cat2=randomcats.get(6);
		CategoryDao cat3=randomcats.get(5);
		
		// create QuickGroup
		QuickGroup group = new QuickGroup();
		group.setName("testname");
		// create three details
		List<QuickGroupDetail> details = new ArrayList<QuickGroupDetail>();
		QuickGroupDetail detail = new QuickGroupDetail();
		detail.setCatid(cat1.getId());
		detail.setPercentage(50D);
		details.add(detail);
		detail = new QuickGroupDetail();
		detail.setCatid(cat2.getId());
		detail.setPercentage(35D);
		details.add(detail);
		detail = new QuickGroupDetail();
		detail.setCatid(cat3.getId());
		detail.setPercentage(15D);
		details.add(detail);		
		// create model from group and details
		QuickGroupModel model = new QuickGroupModel(group,details,categoryref);

		// service call
		model = quickGroupService.saveFromQuickGroupModel(model);
		// Assert not null
		Assert.assertNotNull(model);

		// load model from id
		Long id = model.getGroupId();
		model = quickGroupService.loadQuickGroupModelForId(id);

		// Assert name is correct
		Assert.assertEquals("testname",model.getName());
		// Assert three details
		Assert.assertNotNull(model.getDetails());
		Assert.assertEquals(3,model.getDetails().size());
		
		// now test three details - two with same category, in same model
		// (tests delete and category squishing)
		// make three new details - two with same category
		details = new ArrayList<QuickGroupDetail>();
		detail = new QuickGroupDetail();
		detail.setCatid(cat1.getId());
		detail.setPercentage(50D);
		details.add(detail);
		detail = new QuickGroupDetail();
		detail.setCatid(cat1.getId());
		detail.setPercentage(35D);
		details.add(detail);
		detail = new QuickGroupDetail();
		detail.setCatid(cat3.getId());
		detail.setPercentage(15D);
		details.add(detail);	
		// replace in model
		model.setDetails(details);

		// service call
		model = quickGroupService.saveFromQuickGroupModel(model);
		// Assert not null
		Assert.assertNotNull(model);
		// load model from id
		id = model.getGroupId();
		model = quickGroupService.loadQuickGroupModelForId(id);
				
		// Assert name is correct
		Assert.assertEquals("testname",model.getName());
				
		// Assert two details
		Assert.assertNotNull(model.getDetails());
		Assert.assertEquals(2,model.getDetails().size());		
	}
	
	@Test
	public void testGetExpenseDetailFromQuickGroup() {
		// create categories
		CategoryDao cat1=randomcats.get(3);
		CategoryDao cat2=randomcats.get(6);
		CategoryDao cat3=randomcats.get(5);
		
		// create QuickGroup
		QuickGroup group = new QuickGroup();
		group.setName("testname");
		// create three details
		List<QuickGroupDetail> details = new ArrayList<QuickGroupDetail>();
		QuickGroupDetail detail = new QuickGroupDetail();
		detail.setCatid(cat1.getId());
		detail.setPercentage(50D);
		details.add(detail);
		detail = new QuickGroupDetail();
		detail.setCatid(cat2.getId());
		detail.setPercentage(25D);
		details.add(detail);
		detail = new QuickGroupDetail();
		detail.setCatid(cat3.getId());
		detail.setPercentage(25D);
		details.add(detail);		
		// create model from group and details
		QuickGroupModel model = new QuickGroupModel(group,details,categoryref);
		model = quickGroupService.saveFromQuickGroupModel(model);
		Long groupid=model.getGroupId();
		
		// service call with 100
		List<CategoryTADao> expdetails = quickGroupService.getExpDetailsForQuickGroup(-123.00,groupid);
		// Assert not null,size 3
		Assert.assertNotNull(expdetails);
		Assert.assertEquals(3,expdetails.size());
		// Assert total of 100
		double total=0;
		for (CategoryTADao expdetail:expdetails) {
			total+=(expdetail.getAmount().doubleValue());
		}
		Assert.assertTrue(-123.0==total);
		
		HashMap<Long,CategoryTADao> lookupdetails = putExpenseDetailsIntoHash(expdetails);
		// Assert 1st amount of 50
		CategoryTADao test = lookupdetails.get(cat1.getId());
		Assert.assertTrue(61.50==test.getAmount().doubleValue()*-1);
		// Assert 2nd amount of 25
		test = lookupdetails.get(cat2.getId());
		Assert.assertTrue(30.75==test.getAmount().doubleValue()*-1);
		// Assert 3rd amount of 25
		test = lookupdetails.get(cat3.getId());
		Assert.assertTrue(30.75==test.getAmount().doubleValue()*-1);
		

		
		// service call with 200
		expdetails = quickGroupService.getExpDetailsForQuickGroup(-200.0,groupid);
		// Assert not null,size 3
		Assert.assertNotNull(expdetails);
		Assert.assertEquals(3,expdetails.size());
		// Assert total of 200
		total=0;
		for (CategoryTADao expdetail:expdetails) {
			total+=(expdetail.getAmount().doubleValue())*-1.0;
		}
		Assert.assertTrue(200.0==total);		
		lookupdetails = putExpenseDetailsIntoHash(expdetails);
		// Assert 1st amount of 100
		test = lookupdetails.get(cat1.getId());
		Assert.assertTrue(100.0==test.getAmount().doubleValue()*-1);
		// Assert 2nd amount of 50
		test = lookupdetails.get(cat2.getId());
		Assert.assertTrue(50.0==test.getAmount().doubleValue()*-1);
		// Assert 3rd amount of 50
		test = lookupdetails.get(cat3.getId());
		Assert.assertTrue(50.0==test.getAmount().doubleValue()*-1);
		
		
		// service call with bad groupid
		expdetails = quickGroupService.getExpDetailsForQuickGroup(200.0,new Long(0));
		// Assert not null, size=0
		Assert.assertNotNull(expdetails);
		Assert.assertEquals(0,expdetails.size());		
		
		
	}

	private HashMap<Long, CategoryTADao> putExpenseDetailsIntoHash(
			List<CategoryTADao> expdetails) {
		HashMap<Long,CategoryTADao> lookupdetails = new HashMap<Long,CategoryTADao>();
		for (CategoryTADao detail:expdetails) {
			lookupdetails.put(detail.getCatid(), detail);
		}
		return lookupdetails;
	}
}