package meg.bank.bus.repo;
import java.util.List;

import meg.bank.bus.dao.BankTADao;
import meg.bank.bus.dao.CategoryRuleDao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRuleRepository extends JpaRepository<CategoryRuleDao, Long>, JpaSpecificationExecutor<CategoryRuleDao> {

	@Query("select cat from CategoryRuleDao as cat  where cat.lineorder>:lineorder")
	List<CategoryRuleDao> findCategoryRulesGreaterThanOrder(@Param("lineorder") long lineorder);
	
	@Query("select cat from CategoryRuleDao as cat  where cat.lineorder=:lineorder")
	List<CategoryRuleDao> findCategoryRulesByOrder(@Param("lineorder") long lineorder);
	
	@Query("select cat from CategoryRuleDao as cat  where cat.containing=:text")
	List<CategoryRuleDao> findCategoryRulesByContaining(@Param("text") String text);
		
}
