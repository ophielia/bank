package meg.bank.bus.repo;
import java.util.List;

import meg.bank.bus.dao.TargetDetailDao;
import meg.bank.bus.dao.TargetGroupDao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.roo.addon.layers.repository.jpa.RooJpaRepository;

@RooJpaRepository(domainType = TargetDetailDao.class)
public interface TargetDetailRepository {

	@Query("select tg from TargetDetailDao as tg where tg.targetgroup=:targetgroup ")
	List<TargetDetailDao> findByTargetGroup(@Param("targetgroup") TargetGroupDao targetgroup);

	@Query("select tg from TargetDetailDao as tg where tg.targetgroup=:targetgroup and tg.catid=:catid")
	List<TargetDetailDao> findByTargetGroupAndCategory(@Param("targetgroup") TargetGroupDao targetgroup,@Param("catid") Long catid);

}
