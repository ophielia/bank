package meg.bank.util.common.repo;

import meg.bank.util.common.db.ColumnKeyDao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ColumnKeyRepository extends JpaRepository<ColumnKeyDao, Long>,
		JpaSpecificationExecutor<ColumnKeyDao> {
}
