package meg.bank.util.common.repo;
import meg.bank.util.common.db.ColumnValueDao;

import org.springframework.roo.addon.layers.repository.jpa.RooJpaRepository;

@RooJpaRepository(domainType = ColumnValueDao.class)
public interface ColumnValueRepository {
}
