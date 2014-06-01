package meg.bank.bus;

import java.util.List;

import meg.bank.bus.dao.TargetGroupDao;

public interface TargetService {

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
	public abstract List<TargetGroupDao> getTargetGroupList(Long targettype);

	public abstract void createNewTargetGroup(Long targettype);

	public abstract void deleteTargetGroup(Long editid);

	public abstract TargetGroupDao getTargetGroup(Long editid);

	public abstract void updateDefaultTargetGroup(Long editid, Long targettype);

	public abstract TargetGroupDao loadTarget(Long loadid);

	public abstract TargetGroupDao loadTargetForMonth(String month);

	public abstract TargetGroupDao loadTargetForYear(String year);

	public abstract void deleteTargetDetails(List<Long> deleted);

	public abstract void saveTarget(TargetGroupDao target);

	public abstract TargetGroupDao getDefaultTargetGroup(Long targettype);

}