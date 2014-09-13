package meg.bank.util.common.repo;
import java.util.List;

import meg.bank.util.common.db.ColumnValueDao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.roo.addon.layers.repository.jpa.RooJpaRepository;

@RooJpaRepository(domainType = ColumnValueDao.class)
public interface ColumnValueRepository {
	
	@Query("select cv from ColumnValueDao as cv where cv.active = :isActive and cv.columnkey = (select id from ColumnKeyDao as ck where ck.lookup = :lookup) order by cv.disporder")
	List<ColumnValueDao> getColumnValuesForKey(@Param("lookup") String lookup,@Param("isActive") Boolean isActive);

	@Query("select cv from ColumnValueDao as cv where cv.value= :value and cv.columnkey = (select id from ColumnKeyDao as ck where ck.lookup = :lookup) order by cv.disporder")
	ColumnValueDao getColumnValuesForKeyAndValue(@Param("lookup") String lookup,@Param("value") String value);	
}
