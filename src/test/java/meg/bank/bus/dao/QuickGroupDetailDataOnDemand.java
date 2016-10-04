package meg.bank.bus.dao;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import meg.bank.bus.repo.QuickGroupDetailRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

@Component
@Configurable
public class QuickGroupDetailDataOnDemand {

	private Random rnd = new SecureRandom();

	private List<QuickGroupDetail> data;

	@Autowired
    QuickGroupDataOnDemand quickGroupDataOnDemand;

	@Autowired
    QuickGroupDetailRepository quickGroupDetailRepository;

	public QuickGroupDetail getNewTransientQuickGroupDetail(int index) {
        QuickGroupDetail obj = new QuickGroupDetail();
        setCatid(obj, index);
        setPercentage(obj, index);
        return obj;
    }

	public void setCatid(QuickGroupDetail obj, int index) {
        Long catid = new Integer(index).longValue();
        obj.setCatid(catid);
    }

	public void setPercentage(QuickGroupDetail obj, int index) {
        Double percentage = new Integer(index).doubleValue();
        obj.setPercentage(percentage);
    }

	public QuickGroupDetail getSpecificQuickGroupDetail(int index) {
        init();
        if (index < 0) {
            index = 0;
        }
        if (index > (data.size() - 1)) {
            index = data.size() - 1;
        }
        QuickGroupDetail obj = data.get(index);
        Long id = obj.getId();
        return quickGroupDetailRepository.findOne(id);
    }

	public QuickGroupDetail getRandomQuickGroupDetail() {
        init();
        QuickGroupDetail obj = data.get(rnd.nextInt(data.size()));
        Long id = obj.getId();
        return quickGroupDetailRepository.findOne(id);
    }

	public boolean modifyQuickGroupDetail(QuickGroupDetail obj) {
        return false;
    }

	public void init() {
        int from = 0;
        int to = 10;
        data = quickGroupDetailRepository.findAll(new org.springframework.data.domain.PageRequest(from / to, to)).getContent();
        if (data == null) {
            throw new IllegalStateException("Find entries implementation for 'QuickGroupDetail' illegally returned null");
        }
        if (!data.isEmpty()) {
            return;
        }
        
        data = new ArrayList<QuickGroupDetail>();
        for (int i = 0; i < 10; i++) {
            QuickGroupDetail obj = getNewTransientQuickGroupDetail(i);
            try {
                quickGroupDetailRepository.save(obj);
            } catch (final ConstraintViolationException e) {
                final StringBuilder msg = new StringBuilder();
                for (Iterator<ConstraintViolation<?>> iter = e.getConstraintViolations().iterator(); iter.hasNext();) {
                    final ConstraintViolation<?> cv = iter.next();
                    msg.append("[").append(cv.getRootBean().getClass().getName()).append(".").append(cv.getPropertyPath()).append(": ").append(cv.getMessage()).append(" (invalid value = ").append(cv.getInvalidValue()).append(")").append("]");
                }
                throw new IllegalStateException(msg.toString(), e);
            }
            quickGroupDetailRepository.flush();
            data.add(obj);
        }
    }
}
