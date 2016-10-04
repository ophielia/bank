package meg.bank.bus.repo;

import java.util.List;

import meg.bank.bus.dao.TargetDetailDao;
import meg.bank.bus.dao.TargetGroupDao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TargetDetailRepository extends
		JpaRepository<TargetDetailDao, Long>,
		JpaSpecificationExecutor<TargetDetailDao> {

	@Query("select tg from TargetDetailDao as tg where tg.targetgroup=:targetgroup ")
	List<TargetDetailDao> findByTargetGroup(
			@Param("targetgroup") TargetGroupDao targetgroup);

	@Query("select tg from TargetDetailDao as tg where tg.targetgroup=:targetgroup and tg.catid=:catid")
	List<TargetDetailDao> findByTargetGroupAndCategory(
			@Param("targetgroup") TargetGroupDao targetgroup,
			@Param("catid") Long catid);

}
