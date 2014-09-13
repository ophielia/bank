package meg.bank.bus;

import java.util.List;

import meg.bank.bus.dao.TargetDetailDao;
import meg.bank.bus.dao.TargetGroupDao;
import meg.bank.web.model.TargetModel;

public interface TargetService {

	public final static class TargetType {
		public final static Long Month = new Long(1);
		public final static Long Year = new Long(2);
	}

	public static final String TargetTypeLkup = "targettype";
	/**
	 * TargetGroup and Target Methods
	 *
	 * @param targettype
	 *
	 */
	public abstract List<TargetGroupDao> getTargetGroupList(Long targettype);

	public abstract void copyTargetGroup(Long targettype);

	public abstract TargetGroupDao saveOrUpdateTargetGroup(TargetGroupDao targetgroup);

	public abstract TargetModel loadTargetModel(Long id);

	public abstract void deleteTargetGroup(Long editid);

	public abstract TargetGroupDao getTargetGroup(Long editid);

	public abstract void updateDefaultTargetGroup(Long editid, Long targettype);

	public abstract TargetGroupDao loadTarget(Long loadid);

	public abstract TargetGroupDao loadTargetForMonth(String month);

	public abstract TargetGroupDao loadTargetForYear(String year);

	public abstract void deleteTargetDetails(List<Long> deleted);

	public abstract void saveTarget(TargetGroupDao target);

	public abstract TargetGroupDao getDefaultTargetGroup(Long targettype);

	public abstract TargetDetailDao addTargetDetailToGroup(TargetDetailDao newdetail,
			TargetGroupDao targetgroup);

}