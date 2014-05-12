package meg.bank.bus.repo;
import meg.bank.bus.dao.CatRelationshipDao;
import org.springframework.roo.addon.layers.repository.jpa.RooJpaRepository;

@RooJpaRepository(domainType = CatRelationshipDao.class)
public interface CatRelationshipRepository {
}
