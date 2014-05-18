package meg.bank.bus.repo;
import meg.bank.bus.dao.CategoryTADao;
import org.springframework.roo.addon.layers.repository.jpa.RooJpaRepository;

@RooJpaRepository(domainType = CategoryTADao.class)
public interface CategoryTARepository {
}
