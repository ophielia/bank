package meg.bank.bus.repo;

import meg.bank.bus.dao.QuickGroup;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface QuickGroupRepository extends JpaRepository<QuickGroup, Long>,
		JpaSpecificationExecutor<QuickGroup> {

}
