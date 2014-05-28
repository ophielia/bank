package meg.bank.bus.repo;
import java.util.List;

import meg.bank.bus.dao.BankTADao;
import meg.bank.bus.dao.CategoryDao;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.roo.addon.layers.repository.jpa.RooJpaRepository;

@RooJpaRepository(domainType = CategoryDao.class)
public interface CategoryRepository {
	
	List<CategoryDao> findByName(String name);
	
	List<CategoryDao> findByDisplayinlistTrue();
	

	@Query("select r from CategoryDao as r where cat.id in  ( select rel.childId from CatRelationshipDao as rel where rel.parentId = :parentid)")
	List<CategoryDao> findDirectSubcategories(@Param("parentid") Long parentid);

	

	
	
}
