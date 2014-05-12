package meg.bank.bus.repo;
import meg.bank.bus.dao.CategoryDao;
import org.springframework.roo.addon.layers.repository.jpa.RooJpaRepository;

@RooJpaRepository(domainType = CategoryDao.class)
public interface CategoryRepository {
}
