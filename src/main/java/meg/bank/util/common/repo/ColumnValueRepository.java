package meg.bank.util.common.repo;
import meg.util.common.dao.ColumnValueDao;
import org.springframework.roo.addon.layers.repository.jpa.RooJpaRepository;

@RooJpaRepository(domainType = ColumnValueDao.class)
public interface ColumnValueRepository {
}
