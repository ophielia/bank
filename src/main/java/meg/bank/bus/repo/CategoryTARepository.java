package meg.bank.bus.repo;
import java.util.List;

import meg.bank.bus.dao.BankTADao;
import meg.bank.bus.dao.CategoryTADao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.roo.addon.layers.repository.jpa.RooJpaRepository;

@RooJpaRepository(domainType = CategoryTADao.class)
public interface CategoryTARepository {
	
			@Query("select b from CategoryTADao b where b.banktrans=:transid")
			List<CategoryTADao> findByBankTrans(@Param("transid") Long transid);			
}
