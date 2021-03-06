package meg.bank.bus.dao;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import meg.bank.bus.repo.TargetDetailRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

@Component
@Configurable
public class TargetDetailDaoDataOnDemand {

	private Random rnd = new SecureRandom();

	private List<TargetDetailDao> data;

	@Autowired
    TargetGroupDaoDataOnDemand targetGroupDaoDataOnDemand;

	@Autowired
    TargetDetailRepository targetDetailRepository;

	public TargetDetailDao getNewTransientTargetDetailDao(int index) {
        TargetDetailDao obj = new TargetDetailDao();
        setAmount(obj, index);
        setCatid(obj, index);
        return obj;
    }

	public void setAmount(TargetDetailDao obj, int index) {
        Double amount = new Integer(index).doubleValue();
        obj.setAmount(amount);
    }

	public void setCatid(TargetDetailDao obj, int index) {
        Long catid = new Integer(index).longValue();
        if (catid < 1L) {
            catid = 1L;
        }
        obj.setCatid(catid);
    }

	public TargetDetailDao getSpecificTargetDetailDao(int index) {
        init();
        if (index < 0) {
            index = 0;
        }
        if (index > (data.size() - 1)) {
            index = data.size() - 1;
        }
        TargetDetailDao obj = data.get(index);
        Long id = obj.getId();
        return targetDetailRepository.findOne(id);
    }

	public TargetDetailDao getRandomTargetDetailDao() {
        init();
        TargetDetailDao obj = data.get(rnd.nextInt(data.size()));
        Long id = obj.getId();
        return targetDetailRepository.findOne(id);
    }

	public boolean modifyTargetDetailDao(TargetDetailDao obj) {
        return false;
    }

	public void init() {
        int from = 0;
        int to = 10;
        data = targetDetailRepository.findAll(new org.springframework.data.domain.PageRequest(from / to, to)).getContent();
        if (data == null) {
            throw new IllegalStateException("Find entries implementation for 'TargetDetailDao' illegally returned null");
        }
        if (!data.isEmpty()) {
            return;
        }
        
        data = new ArrayList<TargetDetailDao>();
        for (int i = 0; i < 10; i++) {
            TargetDetailDao obj = getNewTransientTargetDetailDao(i);
            try {
                targetDetailRepository.save(obj);
            } catch (final ConstraintViolationException e) {
                final StringBuilder msg = new StringBuilder();
                for (Iterator<ConstraintViolation<?>> iter = e.getConstraintViolations().iterator(); iter.hasNext();) {
                    final ConstraintViolation<?> cv = iter.next();
                    msg.append("[").append(cv.getRootBean().getClass().getName()).append(".").append(cv.getPropertyPath()).append(": ").append(cv.getMessage()).append(" (invalid value = ").append(cv.getInvalidValue()).append(")").append("]");
                }
                throw new IllegalStateException(msg.toString(), e);
            }
            targetDetailRepository.flush();
            data.add(obj);
        }
    }
}
