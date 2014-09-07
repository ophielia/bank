package meg.bank.bus.repo;
import java.util.List;

import meg.bank.bus.dao.CategoryRuleDao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.roo.addon.layers.repository.jpa.RooJpaRepository;

@RooJpaRepository(domainType = CategoryRuleDao.class)
public interface CategoryRuleRepository {

	@Query("select cat from CategoryRuleDao as cat  where cat.lineorder>:lineorder")
	List<CategoryRuleDao> findCategoryRulesGreaterThanOrder(@Param("lineorder") long lineorder);
	
	@Query("select cat from CategoryRuleDao as cat  where cat.lineorder=:lineorder")
	List<CategoryRuleDao> findCategoryRulesByOrder(@Param("lineorder") long lineorder);
	
	@Query("select cat from CategoryRuleDao as cat  where cat.containing=:text")
	List<CategoryRuleDao> findCategoryRulesByContaining(@Param("text") String text);
		
}
