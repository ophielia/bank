package meg.bank.bus.repo;
import java.util.List;

import meg.bank.bus.dao.BankTADao;
import meg.bank.bus.dao.CategoryRuleDao;
import meg.bank.bus.dao.CategoryTADao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryTARepository  extends JpaRepository<CategoryTADao, Long>, JpaSpecificationExecutor<CategoryTADao> {
	
			@Query("select b from CategoryTADao b where b.banktrans=:transid")
			List<CategoryTADao> findByBankTrans(@Param("transid") BankTADao banktrans);			
}
