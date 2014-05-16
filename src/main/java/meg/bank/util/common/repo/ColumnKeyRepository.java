package meg.bank.util.common.repo;
import meg.bank.util.common.db.ColumnKeyDao;

import org.springframework.roo.addon.layers.repository.jpa.RooJpaRepository;

@RooJpaRepository(domainType = ColumnKeyDao.class)
public interface ColumnKeyRepository {
}
