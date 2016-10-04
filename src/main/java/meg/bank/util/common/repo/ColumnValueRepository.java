package meg.bank.util.common.repo;

import java.util.List;

import meg.bank.util.common.db.ColumnValueDao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ColumnValueRepository extends
		JpaRepository<ColumnValueDao, Long>,
		JpaSpecificationExecutor<ColumnValueDao> {

	@Query("select cv from ColumnValueDao as cv where cv.columnkey = (select id from ColumnKeyDao as ck where ck.lookup = :lookup) order by cv.disporder")
	List<ColumnValueDao> getColumnValuesForKey(@Param("lookup") String lookup);

	@Query("select cv from ColumnValueDao as cv where cv.active = :isActive and cv.columnkey = (select id from ColumnKeyDao as ck where ck.lookup = :lookup) order by cv.disporder")
	List<ColumnValueDao> getActiveColumnValuesForKey(
			@Param("lookup") String lookup, @Param("isActive") Boolean isActive);

	@Query("select cv from ColumnValueDao as cv where cv.value= :value and cv.columnkey = (select id from ColumnKeyDao as ck where ck.lookup = :lookup) order by cv.disporder")
	ColumnValueDao getColumnValuesForKeyAndValue(
			@Param("lookup") String lookup, @Param("value") String value);
}
