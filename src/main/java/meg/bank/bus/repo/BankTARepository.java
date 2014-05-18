package meg.bank.bus.repo;
import java.util.Date;
import java.util.List;

import meg.bank.bus.dao.BankTADao;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.roo.addon.layers.repository.jpa.RooJpaRepository;


@RooJpaRepository(domainType = BankTADao.class)
public interface BankTARepository {
	
	@Query("select b from BankTADao b where b.amount < 0 and b.deleted = false")
	List<BankTADao> findAllUndeleted(Sort sort);
	
	
	
	@Query("select b from BankTADao b where b.amount < 0 and b.hascat = false order by transdate DESC")
	List<BankTADao> findNoCategoryExpenses();
	

	@Query("select b from BankTADao b where b.hascat is false and b.deleted is false and upper(b.detail) like upper('%:detail%') order by transdate DESC")
	List<BankTADao> findTransWithDetailLike(@Param("detail") String detail);
	
	
    @Query(value = "SELECT min(at.transdate) FROM BankTADao at")
    public Date getFirstTransDate();	
}
