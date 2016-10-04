package meg.bank.bus.dao;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import meg.bank.bus.repo.TargetGroupRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

@Component
@Configurable
public class TargetGroupDaoDataOnDemand {

	private Random rnd = new SecureRandom();

	private List<TargetGroupDao> data;

	@Autowired
    TargetGroupRepository targetGroupRepository;

	public TargetGroupDao getNewTransientTargetGroupDao(int index) {
        TargetGroupDao obj = new TargetGroupDao();
        setDescription(obj, index);
        setIsdefault(obj, index);
        setMonthtag(obj, index);
        setName(obj, index);
        setTargettype(obj, index);
        setYeartag(obj, index);
        return obj;
    }

	public void setDescription(TargetGroupDao obj, int index) {
        String description = "description_" + index;
        if (description.length() > 200) {
            description = description.substring(0, 200);
        }
        obj.setDescription(description);
    }

	public void setIsdefault(TargetGroupDao obj, int index) {
        Boolean isdefault = Boolean.TRUE;
        obj.setIsdefault(isdefault);
    }

	public void setMonthtag(TargetGroupDao obj, int index) {
        String monthtag = "monthtag_" + index;
        obj.setMonthtag(monthtag);
    }

	public void setName(TargetGroupDao obj, int index) {
        String name = "name_" + index;
        if (name.length() > 60) {
            name = name.substring(0, 60);
        }
        obj.setName(name);
    }

	public void setTargettype(TargetGroupDao obj, int index) {
        Long targettype = new Integer(index).longValue();
        obj.setTargettype(targettype);
    }

	public void setYeartag(TargetGroupDao obj, int index) {
        String yeartag = "yeartag_" + index;
        obj.setYeartag(yeartag);
    }

	public TargetGroupDao getSpecificTargetGroupDao(int index) {
        init();
        if (index < 0) {
            index = 0;
        }
        if (index > (data.size() - 1)) {
            index = data.size() - 1;
        }
        TargetGroupDao obj = data.get(index);
        Long id = obj.getId();
        return targetGroupRepository.findOne(id);
    }

	public TargetGroupDao getRandomTargetGroupDao() {
        init();
        TargetGroupDao obj = data.get(rnd.nextInt(data.size()));
        Long id = obj.getId();
        return targetGroupRepository.findOne(id);
    }

	public boolean modifyTargetGroupDao(TargetGroupDao obj) {
        return false;
    }

	public void init() {
        int from = 0;
        int to = 10;
        data = targetGroupRepository.findAll(new org.springframework.data.domain.PageRequest(from / to, to)).getContent();
        if (data == null) {
            throw new IllegalStateException("Find entries implementation for 'TargetGroupDao' illegally returned null");
        }
        if (!data.isEmpty()) {
            return;
        }
        
        data = new ArrayList<TargetGroupDao>();
        for (int i = 0; i < 10; i++) {
            TargetGroupDao obj = getNewTransientTargetGroupDao(i);
            try {
                targetGroupRepository.save(obj);
            } catch (final ConstraintViolationException e) {
                final StringBuilder msg = new StringBuilder();
                for (Iterator<ConstraintViolation<?>> iter = e.getConstraintViolations().iterator(); iter.hasNext();) {
                    final ConstraintViolation<?> cv = iter.next();
                    msg.append("[").append(cv.getRootBean().getClass().getName()).append(".").append(cv.getPropertyPath()).append(": ").append(cv.getMessage()).append(" (invalid value = ").append(cv.getInvalidValue()).append(")").append("]");
                }
                throw new IllegalStateException(msg.toString(), e);
            }
            targetGroupRepository.flush();
            data.add(obj);
        }
    }
}
