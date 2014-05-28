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
	List<TargetGroupDao> findTargetsByTypeAndTag(@Param("targettype") Long targettype,String monthtag);

	
	@Query("select tg from TargetGroupDao as tgdao where tgdao.isDefault = true and tgdao.targettype = :targettype")
	TargetGroupDao findDefaultGroupByType(@Param("targettype") Long targettype);
}
