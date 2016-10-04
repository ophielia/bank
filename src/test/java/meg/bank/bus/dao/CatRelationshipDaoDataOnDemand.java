package meg.bank.bus.dao;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import meg.bank.bus.repo.CatRelationshipRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

@Component
@Configurable
public class CatRelationshipDaoDataOnDemand {

	private Random rnd = new SecureRandom();

	private List<CatRelationshipDao> data;

	@Autowired
    CatRelationshipRepository catRelationshipRepository;

	public CatRelationshipDao getNewTransientCatRelationshipDao(int index) {
        CatRelationshipDao obj = new CatRelationshipDao();
        setChildId(obj, index);
        setParentId(obj, index);
        return obj;
    }

	public void setChildId(CatRelationshipDao obj, int index) {
        Long childId = new Integer(index).longValue();
        obj.setChildId(childId);
    }

	public void setParentId(CatRelationshipDao obj, int index) {
        Long parentId = new Integer(index).longValue();
        obj.setParentId(parentId);
    }

	public CatRelationshipDao getSpecificCatRelationshipDao(int index) {
        init();
        if (index < 0) {
            index = 0;
        }
        if (index > (data.size() - 1)) {
            index = data.size() - 1;
        }
        CatRelationshipDao obj = data.get(index);
        Long id = obj.getId();
        return catRelationshipRepository.findOne(id);
    }

	public CatRelationshipDao getRandomCatRelationshipDao() {
        init();
        CatRelationshipDao obj = data.get(rnd.nextInt(data.size()));
        Long id = obj.getId();
        return catRelationshipRepository.findOne(id);
    }

	public boolean modifyCatRelationshipDao(CatRelationshipDao obj) {
        return false;
    }

	public void init() {
        int from = 0;
        int to = 10;
        data = catRelationshipRepository.findAll(new org.springframework.data.domain.PageRequest(from / to, to)).getContent();
        if (data == null) {
            throw new IllegalStateException("Find entries implementation for 'CatRelationshipDao' illegally returned null");
        }
        if (!data.isEmpty()) {
            return;
        }
        
        data = new ArrayList<CatRelationshipDao>();
        for (int i = 0; i < 10; i++) {
            CatRelationshipDao obj = getNewTransientCatRelationshipDao(i);
            try {
                catRelationshipRepository.save(obj);
            } catch (final ConstraintViolationException e) {
                final StringBuilder msg = new StringBuilder();
                for (Iterator<ConstraintViolation<?>> iter = e.getConstraintViolations().iterator(); iter.hasNext();) {
                    final ConstraintViolation<?> cv = iter.next();
                    msg.append("[").append(cv.getRootBean().getClass().getName()).append(".").append(cv.getPropertyPath()).append(": ").append(cv.getMessage()).append(" (invalid value = ").append(cv.getInvalidValue()).append(")").append("]");
                }
                throw new IllegalStateException(msg.toString(), e);
            }
            catRelationshipRepository.flush();
            data.add(obj);
        }
    }
}
