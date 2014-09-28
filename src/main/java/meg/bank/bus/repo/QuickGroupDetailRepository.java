package meg.bank.bus.repo;
import java.util.List;

import meg.bank.bus.dao.BankTADao;
import meg.bank.bus.dao.CategoryTADao;
import meg.bank.bus.dao.QuickGroup;
import meg.bank.bus.dao.QuickGroupDetail;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.roo.addon.layers.repository.jpa.RooJpaRepository;

@RooJpaRepository(domainType = QuickGroupDetail.class)
public interface QuickGroupDetailRepository {
	
	@Query("select b from QuickGroupDetail b where b.quickgroup=:quickgroup")
	List<QuickGroupDetail> findByQuickGroup(@Param("quickgroup") QuickGroup quickgroup);			
	
}
