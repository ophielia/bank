package meg.bank.bus.dao;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import meg.bank.bus.repo.QuickGroupRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

@Configurable
@Component
public class QuickGroupDataOnDemand {

	private Random rnd = new SecureRandom();

	private List<QuickGroup> data;

	@Autowired
    QuickGroupRepository quickGroupRepository;

	public QuickGroup getNewTransientQuickGroup(int index) {
        QuickGroup obj = new QuickGroup();
        setName(obj, index);
        return obj;
    }

	public void setName(QuickGroup obj, int index) {
        String name = "name_" + index;
        if (name.length() > 100) {
            name = name.substring(0, 100);
        }
        obj.setName(name);
    }

	public QuickGroup getSpecificQuickGroup(int index) {
        init();
        if (index < 0) {
            index = 0;
        }
        if (index > (data.size() - 1)) {
            index = data.size() - 1;
        }
        QuickGroup obj = data.get(index);
        Long id = obj.getId();
        return quickGroupRepository.findOne(id);
    }

	public QuickGroup getRandomQuickGroup() {
        init();
        QuickGroup obj = data.get(rnd.nextInt(data.size()));
        Long id = obj.getId();
        return quickGroupRepository.findOne(id);
    }

	public boolean modifyQuickGroup(QuickGroup obj) {
        return false;
    }

	public void init() {
        int from = 0;
        int to = 10;
        data = quickGroupRepository.findAll(new org.springframework.data.domain.PageRequest(from / to, to)).getContent();
        if (data == null) {
            throw new IllegalStateException("Find entries implementation for 'QuickGroup' illegally returned null");
        }
        if (!data.isEmpty()) {
            return;
        }
        
        data = new ArrayList<QuickGroup>();
        for (int i = 0; i < 10; i++) {
            QuickGroup obj = getNewTransientQuickGroup(i);
            try {
                quickGroupRepository.save(obj);
            } catch (final ConstraintViolationException e) {
                final StringBuilder msg = new StringBuilder();
                for (Iterator<ConstraintViolation<?>> iter = e.getConstraintViolations().iterator(); iter.hasNext();) {
                    final ConstraintViolation<?> cv = iter.next();
                    msg.append("[").append(cv.getRootBean().getClass().getName()).append(".").append(cv.getPropertyPath()).append(": ").append(cv.getMessage()).append(" (invalid value = ").append(cv.getInvalidValue()).append(")").append("]");
                }
                throw new IllegalStateException(msg.toString(), e);
            }
            quickGroupRepository.flush();
            data.add(obj);
        }
    }
}
