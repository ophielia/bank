package meg.bank.bus;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import meg.bank.bus.dao.TargetDetailDao;
import meg.bank.bus.dao.TargetGroupDao;
import meg.bank.bus.repo.TargetDetailRepository;
import meg.bank.bus.repo.TargetGroupRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TargetService {

	
	@Autowired
	TargetGroupRepository targetGrpRep;
	
	@Autowired
	TargetDetailRepository targetDetRep;
	
	
	public final static class TargetType {
		public final static Long Month = new Long(1);
		public final static Long Year = new Long(2);
	}

	/**
	 * TargetGroup and Target Methods
	 * 
	 * @param targettype
	 * 
	 */
	public List<TargetGroupDao> getTargetGroupList(Long targettype) {
		// call to repository
		// MM remove....CategoryManagerDao catmandao = getCategoryManagerDao();
		//return catmandao.getTargetGroupList(targettype);
		return null;
	}

	public void createNewTargetGroup(Long targettype) {
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
				newdetail.setGroupid(newtg.getId());

				//targetDetRep.save(newdetail);
			}
			newtg.setTargetdetails(details);
		}
		// save group and held details
		targetGrpRep.save(newtg);
	}

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

	public TargetGroupDao getTargetGroup(Long editid) {
		// replace with repository findOne
		// remove
		/*CategoryManagerDao catmandao = getCategoryManagerDao();
		return catmandao.getTargetGroup(editid);*/
		return null;
	}

	public void updateDefaultTargetGroup(Long editid, Long targettype) {
		// get previous default
		TargetGroupDao defaulttg = getDefaultTargetGroup(targettype);

		// update previous default
		defaulttg.setIsdefault(new Boolean(false));
		targetGrpRep.save(defaulttg);

		// get new default
		TargetGroupDao newdefault = targetGrpRep.findOne(editid);

		// update new default
		newdefault.setIsdefault(new Boolean(true));
		targetGrpRep.save(newdefault);
	}

	public TargetGroupDao loadTarget(Long loadid) {
		// get TargetGroup
		TargetGroupDao tg = targetGrpRep.findOne(loadid);

		// get Target Details
		List<TargetDetailDao> details = tg.getTargetdetails();

		return tg;
	}

	public TargetGroupDao loadTargetForMonth(String month) {
		// look for target by month
		List<TargetGroupDao> list = targetGrpRep.findTargetsByTypeAndTag(TargetService.TargetType.Month,month);
		if (list!=null&& list.size()>0) {
			TargetGroupDao tg = list.get(0);
			tg.getTargetdetails();
			return tg;
		}
		// if no target is available for the month, load the default
		TargetGroupDao tg = getDefaultTargetGroup(CategoryManager.TargetType.Month);
		tg.getTargetdetails();
		return tg;
	}
	
	public TargetGroupDao loadTargetForYear(String year) {
		// look for target by year
		List<TargetGroupDao> list = targetGrpRep.findTargetsByTypeAndTag(TargetService.TargetType.Year,year);
		if (list!=null&& list.size()>0) {
			TargetGroupDao tg = list.get(0);
			tg.getTargetdetails();
			return tg;
		}
		// if no target is available for the month, load the default
		TargetGroupDao tg = getDefaultTargetGroup(CategoryManager.TargetType.Year);
		tg.getTargetdetails();
		return tg;

	}	

	public void deleteTargetDetails(List<Long> deleted) {
		for (Long deleteid : deleted) {
			targetDetRep.delete(deleteid);
		}
	}

	public void saveTarget(TargetGroupDao target) {
		// for month types
		if (target.getTargettype().longValue() == CategoryManager.TargetType.Month
				.longValue()) {
			// check if any other groups have the same month tag
			List<TargetGroupDao> results = targetGrpRep.findTargetsByTypeAndTag(TargetService.TargetType.Month,target.getMonthtag());
			if (results!=null && results.size()>0) {
				TargetGroupDao previoustag = results.get(0);
				// if so, remove month tag from other group
					previoustag.setMonthtag(null);
					targetGrpRep.save(previoustag);
				
			}
		} else {  
			// for year types
			// check if any other groups have the same year tag
						List<TargetGroupDao> results = targetGrpRep.findTargetsByTypeAndTag(TargetService.TargetType.Year,target.getYeartag());
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
	
	public TargetGroupDao getDefaultTargetGroup(Long targettype) {
		TargetGroupDao targetgroup = targetGrpRep.findDefaultGroupByType(targettype);

		return targetgroup;
	}
	

}
