package meg.bank.bus.repo;
import meg.bank.bus.dao.CatRelationshipDao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CatRelationshipRepository extends JpaRepository<CatRelationshipDao, Long>, JpaSpecificationExecutor<CatRelationshipDao>  {
	
	
	@Query("select r from CatRelationshipDao as r where r.parentId=:parentid and r.childId = :childid")
	CatRelationshipDao findByParentAndChild(@Param("parentid") Long parentid,@Param("childid") Long childid);
	
	@Query("select r from CatRelationshipDao as r where  r.childId = :childid")
	CatRelationshipDao findByChild(@Param("childid") Long childid);
	
}
