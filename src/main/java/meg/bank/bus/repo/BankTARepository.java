package meg.bank.bus.repo;

import java.util.Date;
import java.util.List;
import meg.bank.bus.dao.BankTADao;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface BankTARepository extends JpaRepository<BankTADao, Long>, JpaSpecificationExecutor<BankTADao> {

	@Query("select b from BankTADao b where b.amount < 0 and b.deleted = false")
	List<BankTADao> findAllUndeleted(Sort sort);



	@Query("select b from BankTADao b where b.amount < 0 and b.hascat = false order by transdate DESC")
	List<BankTADao> findNoCategoryExpenses();


	@Query("select b from BankTADao b where b.hascat is false and b.deleted is false and upper(b.detail) like upper('%:detail%') order by transdate DESC")
	List<BankTADao> findTransWithDetailLike(@Param("detail") String detail);


    @Query(value = "SELECT min(at.transdate) FROM BankTADao at")
    public Date getFirstTransDate();

    @Query(value = "SELECT max(at.transdate) FROM BankTADao at")
    public Date getMostRecentTransDate();

	@Query("select trans from BankTADao as trans where trans.amount = :amount and trans.transdate = :transdate and trans.description = :description ")
	List<BankTADao> findTransDuplicates(@Param("amount") Double amount,@Param("transdate") Date transdate,@Param("description") String description);
}
