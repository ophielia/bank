package meg.bank.bus;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import meg.bank.bus.dao.CategoryDao;
import meg.bank.bus.dao.TargetDetailDao;
import meg.bank.bus.dao.TargetGroupDao;
import meg.bank.bus.repo.TargetDetailRepository;
import meg.bank.bus.repo.TargetGroupRepository;
import meg.bank.util.common.ColumnManagerService;
import meg.bank.web.model.TargetModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TargetServiceImpl implements TargetService {


	@Autowired
	TargetGroupRepository targetGrpRep;

	@Autowired
	TargetDetailRepository targetDetRep;


	@Autowired
	ColumnManagerService cvManager;

	@Autowired
	CategoryService catManager;


	/* (non-Javadoc)
	 * @see meg.bank.bus.TargetService#getTargetGroupList(java.lang.Long)
	 */
	@Override
	public List<TargetGroupDao> getTargetGroupList() {
		// call to repository
		//return list sorted by targettype, yeartag and monthtag
		List<String> sortcolumns = new ArrayList<String>();
		sortcolumns.add("targettype");
		sortcolumns.add("yeartag");
		sortcolumns.add("monthtag");
		List<TargetGroupDao> grouplist = targetGrpRep.findAll(new Sort(Sort.Direction.ASC, sortcolumns));
		return grouplist;
	}


	public TargetGroupDao saveOrUpdateTargetGroup(TargetGroupDao targetgroup) {
		// check for null
		if (targetgroup!=null) {
			// save TargetGroupDao
			targetgroup = targetGrpRep.save(targetgroup);
			// return TargetGroupDao
			return targetgroup;
		}
		return null;
	}



	/* (non-Javadoc)
	 * @see meg.bank.bus.TargetService#copyTargetGroup(java.lang.Long)
	 */
	@Override
	public void copyTargetGroup(Long targettype) {
		TargetGroupDao tg = getDefaultTargetGroup(targettype);
		Long defaultid = tg.getId();

		TargetGroupDao newtg = new TargetGroupDao();
		newtg.setTargettype(targettype);
		newtg.setDescription("generated " + new Date());
		newtg.setName("new TargetGroup ");


		// copy details
		List<TargetDetailDao> details = tg.getTargetdetails();
		if (details != null) {
			for (TargetDetailDao detail : details) {
				TargetDetailDao newdetail = new TargetDetailDao();
				newdetail.setAmount(detail.getAmount());
				newdetail.setCatid(detail.getCatid());
				newdetail.setTargetgroup(newtg);

				//targetDetRep.save(newdetail);
			}
			newtg.setTargetdetails(details);
		}
		// save group and held details
		targetGrpRep.save(newtg);
	}

	/* (non-Javadoc)
	 * @see meg.bank.bus.TargetService#deleteTargetGroup(java.lang.Long)
	 */
	@Override
	public void deleteTargetGroup(Long editid) {
		// delete details
		TargetGroupDao targetgrp = targetGrpRep.findOne(editid);
		if (targetgrp != null) {
			List<TargetDetailDao> details = targetgrp.getTargetdetails();
			for (TargetDetailDao detail : details) {
				targetDetRep.delete(detail.getId());
			}
		}
		// delete Target Group
		targetGrpRep.delete(editid);
	}

	/* (non-Javadoc)
	 * @see meg.bank.bus.TargetService#getTargetGroup(java.lang.Long)
	 */
	@Override
	public TargetGroupDao getTargetGroup(Long editid) {
		if (editid!=null) {
			return targetGrpRep.findOne(editid);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see meg.bank.bus.TargetService#updateDefaultTargetGroup(java.lang.Long, java.lang.Long)
	 */
	@Override
	public void updateDefaultTargetGroup(Long editid) {
		// get group to be made default
		TargetGroupDao newdefault = getTargetGroup(editid);
		
		if (newdefault!=null) {
			// get previous default
			TargetGroupDao defaulttg = getDefaultTargetGroup(newdefault.getTargettype());

			// update previous default
			defaulttg.setIsdefault(new Boolean(false));
			targetGrpRep.save(defaulttg);


			// update new default
			newdefault.setIsdefault(new Boolean(true));
			targetGrpRep.save(newdefault);
		}
	}

	/* (non-Javadoc)
	 * @see meg.bank.bus.TargetService#loadTarget(java.lang.Long)
	 */
	@Override
	public TargetGroupDao loadTarget(Long loadid) {
		// get TargetGroup
		TargetGroupDao tg = targetGrpRep.findOne(loadid);

		// get Target Details
		List<TargetDetailDao> details = tg.getTargetdetails();

		return tg;
	}

	@Override
	public TargetModel loadTargetModel(Long id) {
		// loadTargetGroup
		TargetGroupDao group = loadTarget(id);

		// load TargetDetails
		List<TargetDetailDao> details = targetDetRep.findByTargetGroup(group);
		// fill in details display info
		if (details!=null) {
			// get category hash
			HashMap<Long, CategoryDao> allcats = catManager.getCategoriesAsMap(false);
			// go through all details and fill in disp
			for (TargetDetailDao detail:details) {
				Long catid = detail.getCatid();
				if (allcats.containsKey(catid)) {
					CategoryDao category = allcats.get(catid);
					detail.setCatdisplay(category.getName());
				}
			}
		}
		group.setTargetdetails(details);

		// add to model
		TargetModel model = new TargetModel(group);

		// fill in display info - load targettype display
		if (group!=null) {
			// add display info to model - month or year type
			String ttypedisp = cvManager.getDisplayForValue(TargetService.TargetTypeLkup, group.getTargettype().toString());
			model.setTargettypeDisplay(ttypedisp);
			// will also need to add display to details
		}

		// return model
		return model;
	}


	/* (non-Javadoc)
	 * @see meg.bank.bus.TargetService#loadTargetForMonth(java.lang.String)
	 */
	@Override
	public TargetGroupDao loadTargetForMonth(String month) {
		// look for target by month
		List<TargetGroupDao> list = targetGrpRep.findTargetsByTypeAndMonthTag(TargetService.TargetType.Month,month);
		if (list!=null&& list.size()>0) {
			TargetGroupDao tg = list.get(0);
			tg.getTargetdetails();
			return tg;
		}
		// if no target is available for the month, load the default
		TargetGroupDao tg = getDefaultTargetGroup(TargetService.TargetType.Month);
		tg.getTargetdetails();
		return tg;
	}

	/* (non-Javadoc)
	 * @see meg.bank.bus.TargetService#loadTargetForYear(java.lang.String)
	 */
	@Override
	public TargetGroupDao loadTargetForYear(String year) {
		// look for target by year
		List<TargetGroupDao> list = targetGrpRep.findTargetsByTypeAndMonthTag(TargetService.TargetType.Year,year);
		if (list!=null&& list.size()>0) {
			TargetGroupDao tg = list.get(0);
			tg.getTargetdetails();
			return tg;
		}
		// if no target is available for the month, load the default
		TargetGroupDao tg = getDefaultTargetGroup(TargetService.TargetType.Year);
		tg.getTargetdetails();
		return tg;

	}

	/* (non-Javadoc)
	 * @see meg.bank.bus.TargetService#deleteTargetDetails(java.util.List)
	 */
	@Override
	public void deleteTargetDetails(List<Long> deleted) {
		for (Long deleteid : deleted) {
			deleteTargetDetail(deleteid);
		}
	}

	@Override
	public void deleteTargetDetail(Long deleteid) {
			targetDetRep.delete(deleteid);
	}	
	
	/* (non-Javadoc)
	 * @see meg.bank.bus.TargetService#saveTarget(meg.bank.bus.dao.TargetGroupDao)
	 */
	@Override
	public void saveTarget(TargetGroupDao target) {
		// for month types
		if (target.getTargettype().longValue() == TargetService.TargetType.Month
				.longValue()) {
			// check if any other groups have the same month tag
			List<TargetGroupDao> results = targetGrpRep.findTargetsByTypeAndMonthTag(TargetService.TargetType.Month,target.getMonthtag());
			if (results!=null && results.size()>0) {
				TargetGroupDao previoustag = results.get(0);
				// if so, remove month tag from other group
					previoustag.setMonthtag(null);
					targetGrpRep.save(previoustag);

			}
		} else {
			// for year types
			// check if any other groups have the same year tag
						List<TargetGroupDao> results = targetGrpRep.findTargetsByTypeAndMonthTag(TargetService.TargetType.Year,target.getYeartag());
						if (results!=null && results.size()>0) {
							TargetGroupDao previoustag = results.get(0);
							// if so, remove month tag from other group
								previoustag.setYeartag(null);
								targetGrpRep.save(previoustag);

						}

		}
		targetGrpRep.save(target);

		// save target detail
		// place all details in hash with catid as key
		List<TargetDetailDao> details = target.getTargetdetails();
		Hashtable<Long, TargetDetailDao> alldetails = new Hashtable<Long, TargetDetailDao>();
		for (TargetDetailDao det : details) {
			if (alldetails.containsKey(det.getCatid())) {
				// the category has been entered twice. consolidate entries...
				TargetDetailDao existing = (TargetDetailDao) alldetails.get(det
						.getCatid());
				double combined = existing.getAmount().doubleValue()
						+ det.getAmount().doubleValue();
				existing.setAmount(new Double(combined));
				// delete duplicate, if it exists in db
				if (det.getId().longValue() > 0) {
					targetDetRep.delete(det.getId());
				}
			} else {
				alldetails.put(det.getCatid(), det);
			}

		}
		// now, cycle through all details, persisting changes
		Set<Long> keys = alldetails.keySet();
		List<TargetDetailDao> finaldetails = new ArrayList<TargetDetailDao>();
		for (Long key : keys) {
			TargetDetailDao det = (TargetDetailDao) alldetails.get(key);
			finaldetails.add(det);

		}
		target.setTargetdetails(finaldetails);
		targetGrpRep.save(target);
	}

	/* (non-Javadoc)
	 * @see meg.bank.bus.TargetService#getDefaultTargetGroup(java.lang.Long)
	 */
	@Override
	public TargetGroupDao getDefaultTargetGroup(Long targettype) {
		TargetGroupDao targetgroup = targetGrpRep.findDefaultGroupByType(targettype);

		return targetgroup;
	}


	public TargetDetailDao addTargetDetailToGroup(TargetDetailDao detail,
			TargetGroupDao group) {
		if (detail != null && group != null) {


			// if the detail hasn't been saved before, fill in the group
			if (detail.getId() == null) {

				detail.setTargetgroup(group);
			}
			targetDetRep.save(detail);
		}
		return detail;


	}


	@Override
	public void mergeTargetDetail(Long detailid, TargetDetailDao editdetail) {
		// get target detail
		TargetDetailDao fromdb = targetDetRep.findOne(detailid);
		// merge new info
		fromdb.setCatid(editdetail.getCatid());
		fromdb.setAmount(editdetail.getAmount());
		// save change
		targetDetRep.save(fromdb);
	}


}
