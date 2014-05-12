package meg.bank.bus.repo;
import meg.bank.bus.dao.ExpenseDao;
import org.springframework.roo.addon.layers.repository.jpa.RooJpaRepository;

@RooJpaRepository(domainType = ExpenseDao.class)
public interface ExpenseRepository {
}
