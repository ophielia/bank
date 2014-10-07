package meg.bank.bus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import meg.bank.bus.dao.BankTADao;
import meg.bank.bus.dao.CategoryDao;
import meg.bank.bus.dao.CategoryTADao;
import meg.bank.bus.dao.QuickGroup;
import meg.bank.bus.dao.QuickGroupDetail;
import meg.bank.bus.repo.BankTARepository;
import meg.bank.bus.repo.CategoryTARepository;
import meg.bank.bus.repo.QuickGroupDetailRepository;
import meg.bank.bus.repo.QuickGroupRepository;
import meg.bank.web.model.QuickGroupModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class QuickGroupServiceImpl implements QuickGroupService {

	@Autowired
	private CategoryTARepository catTransRep;

	@Autowired
	private BankTARepository bankTransRep;

	@Autowired
	private CategoryService cms;

	@Autowired
	private QuickGroupRepository qcRepo;

	@Autowired
	private QuickGroupDetailRepository qcDetRepo;

	@Override
	public QuickGroupModel createQuickGroupFromExpense(Long transid) {
		// get hash of categories
		HashMap<Long,CategoryDao> categoryref = cms.getCategoriesAsMap();

		if (transid!=null) {
			// get BankTransaction
			BankTADao banktrans = bankTransRep.findOne(transid);
			// get ExpenseDetails
			List<CategoryTADao> expdetails = catTransRep.findByBankTrans(banktrans);
			if (banktrans!=null&&expdetails!=null) {
				double transtotal = banktrans.getAmount().doubleValue()*-1;
				double totalpct = 0;
				List<QuickGroupDetail> qcdetails = new ArrayList<QuickGroupDetail>();
				// create QuickGroupDetails from ExpenseDetails
				for (int i=0;i<expdetails.size();i++) {
					QuickGroupDetail detail = new QuickGroupDetail();
					CategoryTADao exp=expdetails.get(i);
					// copy cat
					detail.setCatid(exp.getCatid());
					// copy amount (100-totalpct if last)
					if (i==expdetails.size()-1) {
						double pct = 100.0-totalpct;
						detail.setPercentage(pct);
					}else {
						double unrounded = exp.getAmount().doubleValue()* -1.0 *10000.0/ transtotal   ;
						double pct = Math.round(unrounded)/100.0;
						detail.setPercentage(pct);
						totalpct+=pct;
					}
					qcdetails.add(detail);
				}
				// set created QuickGroupDetails and new QuickGroup in model
				QuickGroupModel model = new QuickGroupModel(new QuickGroup(),qcdetails,categoryref);
				// return model
				return model;

			}

		}

		// return model with new objects
		QuickGroupModel model = new QuickGroupModel(new QuickGroup(),new ArrayList<QuickGroupDetail>(),categoryref);
		
		// set banktransid in model
		model.setTransId(transid);
		return model;
	}

	@Override
	public QuickGroupModel loadQuickGroupModelForId(Long id) {
		// load category ref
		HashMap<Long,CategoryDao> categoryref = cms.getCategoriesAsMap();
		if (id!=null) {

			// load QuickGroup
			QuickGroup group = qcRepo.findOne(id);
			if (group!=null) {
				// load QuickGroupDetails
				List<QuickGroupDetail> details= qcDetRepo.findByQuickGroup(group);
				// construct model
				QuickGroupModel model = new QuickGroupModel(group,details,categoryref);
				// return model
				return model;
			}
		}

		// return model with new objects
		QuickGroupModel model = new QuickGroupModel(new QuickGroup(),new ArrayList<QuickGroupDetail>(),categoryref);
		return model;
	}

	@Override  // empty model
	public QuickGroupModel saveFromQuickGroupModel(QuickGroupModel model) {
		// get QuickGroup from db
				QuickGroup quickgroup = null;
				if (model.getGroupId()!=null && model.getGroupId()>0) {
					quickgroup = qcRepo.findOne(model.getGroupId());
					quickgroup.setName(model.getName());
				} else {
					quickgroup = model.getQuickGroup();
				}
				qcRepo.saveAndFlush(quickgroup);
				
				
				// first squish categories together
				List<QuickGroupDetail> modeldetails = model.getDetails();
				HashMap<Long,QuickGroupDetail> squished = new HashMap<Long,QuickGroupDetail>();
				for (QuickGroupDetail detail:modeldetails) {
					// get categoryid
					Long catid = detail.getCatid();
					// if exists in squished, check and add to existing
					if (squished.containsKey(catid)) {
						QuickGroupDetail hashcat = squished.get(catid);
						double newamount = detail.getPercentage().doubleValue() + hashcat.getPercentage().doubleValue();
						if (hashcat.getId()==null && detail!=null) {
							// copy hashcat into / add to catexp
							detail.setPercentage(newamount);
							// set catexp in hash
							squished.put(catid, detail);
						} else {
							// copy catexp into / add to hashcat
							hashcat.setPercentage(newamount);
							// set hashcat in hash
							squished.put(catid, hashcat);
						}
					} else {
						// otherwise add to squished
						squished.put(catid, detail);
					}

				}
				List<QuickGroupDetail> finaldetails =new ArrayList<QuickGroupDetail>();
				for (Long key:squished.keySet()) {
					QuickGroupDetail catexp = squished.get(key);
					if (catexp.getPercentage()!=null && catexp.getPercentage()!=0) {
						finaldetails.add(catexp);	
					}
					
				}
				
				// secondly, make a list of ids in category expenses
				List<Long> detailids = new ArrayList<Long>();
				for (QuickGroupDetail detail:finaldetails) {
					// set bankta in catexp
					detail.setQuickgroup(quickgroup);
					// save catexp
					detail = qcDetRepo.saveAndFlush(detail);
					// put id in list
					detailids.add(detail.getId());
				}
				
				// get db category expenses
				List<QuickGroupDetail> dbdetails =qcDetRepo.findByQuickGroup(quickgroup);
				if (dbdetails!=null) {
					// go through all category expenses, deleting those that don't exist in model
					List<QuickGroupDetail> todelete =new ArrayList<QuickGroupDetail>();
					for (QuickGroupDetail detail:dbdetails) {
						Long catexpid = detail.getId();
						if (catexpid!=null && catexpid>0) {
							if (!detailids.contains(catexpid)) {
								// delete this
								todelete.add(detail);
							}
						}
					}
					for (QuickGroupDetail detail:todelete) {
						qcDetRepo.delete(detail);
					}
				}

				
				// put model back together
				model = loadQuickGroupModelForId(model.getGroupId());
				// return

				return model;
	}

	@Override
	public List<CategoryTADao> getExpDetailsForQuickGroup(double todistribute, Long groupid) {
		// create list to be returned
		List<CategoryTADao> expensedetails = new ArrayList<CategoryTADao>();
		if (groupid !=null) {
			// load quickgroupd
			QuickGroup group = qcRepo.findOne(groupid);
			if (group!=null) {
				// load quickgroupdetails
				List<QuickGroupDetail> details = qcDetRepo.findByQuickGroup(group);
				// start total
				double total=0;
				// cycle through details, creating an expensedetail for each
				for (QuickGroupDetail detail:details) {
					// create new ExpenseDetail
					CategoryTADao expdet = new CategoryTADao();
					// calculate amount
					double amount = todistribute * detail.getPercentage().doubleValue() / 100.0;
					amount = Math.round(amount*100.0) / 100.0;
					// fill in ExpenseDetail amount and catid
					expdet.setAmount(amount);
					expdet.setCatid(detail.getCatid());
					expensedetails.add(expdet);
					total+=amount;
				}
				// check for penny off
				if (total!=todistribute) {
					double correction = todistribute - total;
					CategoryTADao corrdet = expensedetails.get(expensedetails.size()-1);
					double amount = corrdet.getAmount().doubleValue();
					amount+=correction;
					amount = Math.round(amount*100.0)/100.0;
					corrdet.setAmount(new Double(amount));
				}
				// return list
				return expensedetails;
				
			}
		}
		return expensedetails;
	}
	
	@Override
	public List<QuickGroup> getAllQuickGroups() {
		return qcRepo.findAll();
	}

	@Override
	public List<QuickGroupDetail> getDetailsForQuickGroup(QuickGroup quickgroup) {
		return qcDetRepo.findByQuickGroup(quickgroup);
	}

	@Override
	public QuickGroup getQuickGroup(Long quickgroupid) {
		return qcRepo.findOne(quickgroupid);
	}

}
