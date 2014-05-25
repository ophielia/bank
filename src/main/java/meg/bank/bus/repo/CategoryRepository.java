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
	
	@Query("select r from CatRelationshipDao as r where r.parentId=:parentid and r.childId = :childid")
	CategoryDao findByParentAndChild(@Param("parentid") Long parentid,@Param("childid") Long childid);
	
}
