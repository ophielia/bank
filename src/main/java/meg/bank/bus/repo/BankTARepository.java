package meg.bank.bus.repo;
import meg.bank.bus.dao.BankTADao;
import org.springframework.roo.addon.layers.repository.jpa.RooJpaRepository;

@RooJpaRepository(domainType = BankTADao.class)
public interface BankTARepository {
}
