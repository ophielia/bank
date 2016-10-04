package meg.bank.bus.repo;

import meg.bank.bus.dao.MediaUploadDao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaUploadRepository extends JpaRepository<MediaUploadDao, Long>,
		JpaSpecificationExecutor<MediaUploadDao> {
	
}
