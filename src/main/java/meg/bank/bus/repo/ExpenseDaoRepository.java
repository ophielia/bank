package meg.bank.bus.repo;
import java.util.Date;
import java.util.List;

import meg.bank.bus.dao.BankTADao;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.roo.addon.layers.repository.jpa.RooJpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ExpenseDaoRepository extends JpaRepository<BankTADao, String> {
	
	//@Query("select b from BankTADao b where b.amount < 0 and b.deleted = false")
	//List<BankTADao> findAllUndeleted(Sort sort);
   
}
