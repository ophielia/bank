package meg.bank.bus.repo;
import meg.bank.bus.dao.CategoryRuleDao;
import org.springframework.roo.addon.layers.repository.jpa.RooJpaRepository;

@RooJpaRepository(domainType = CategoryRuleDao.class)
public interface CategoryRuleRepository {
}
