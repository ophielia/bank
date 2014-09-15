package meg.bank.bus.repo;
import java.util.List;

import meg.bank.bus.dao.CategoryDao;
import meg.bank.bus.dao.TargetGroupDao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.roo.addon.layers.repository.jpa.RooJpaRepository;

@RooJpaRepository(domainType = TargetGroupDao.class)
public interface TargetGroupRepository {
	

	@Query("select tg from TargetGroupDao as tg where tg.targettype=:targettype and  tg.monthtag = :monthtag")
	List<TargetGroupDao> findTargetsByTypeAndMonthTag(@Param("targettype") Long targettype,@Param("monthtag")String monthtag);

	@Query("select tg from TargetGroupDao as tg where tg.targettype=:targettype and  tg.yeartag = :yeartag")
	List<TargetGroupDao> findTargetsByTypeAndYearTag(@Param("targettype") Long targettype,@Param("yeartag")String yeartag);
	
	
	@Query("select tgdao from TargetGroupDao as tgdao where tgdao.isdefault = true and tgdao.targettype = :targettype")
	TargetGroupDao findDefaultGroupByType(@Param("targettype") Long targettype);
}
