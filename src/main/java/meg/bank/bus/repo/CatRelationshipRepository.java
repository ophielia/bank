package meg.bank.bus.repo;
import meg.bank.bus.dao.CatRelationshipDao;
import meg.bank.bus.dao.CategoryDao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.roo.addon.layers.repository.jpa.RooJpaRepository;

@RooJpaRepository(domainType = CatRelationshipDao.class)
public interface CatRelationshipRepository {
	
	
	@Query("select r from CatRelationshipDao as r where r.parentId=:parentid and r.childId = :childid")
	CatRelationshipDao findByParentAndChild(@Param("parentid") Long parentid,@Param("childid") Long childid);
	
	@Query("select r from CatRelationshipDao as r where  r.childId = :childid")
	CatRelationshipDao findByChild(@Param("childid") Long childid);
	
}
