package meg.bank.bus.repo;
import meg.bank.bus.dao.MediaUploadDao;
import org.springframework.roo.addon.layers.repository.jpa.RooJpaRepository;

@RooJpaRepository(domainType = MediaUploadDao.class)
public interface MediaUploadRepository {
}
