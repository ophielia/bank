package meg.bank.bus.repo;
import meg.bank.bus.dao.TargetDetailDao;
import org.springframework.roo.addon.layers.repository.jpa.RooJpaRepository;

@RooJpaRepository(domainType = TargetDetailDao.class)
public interface TargetDetailRepository {
}
