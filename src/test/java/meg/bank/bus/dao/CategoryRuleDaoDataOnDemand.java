package meg.bank.bus.dao;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import meg.bank.bus.repo.CategoryRuleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

@Component
@Configurable
public class CategoryRuleDaoDataOnDemand {

	private Random rnd = new SecureRandom();

	private List<CategoryRuleDao> data;

	@Autowired
    CategoryRuleRepository categoryRuleRepository;

	public CategoryRuleDao getNewTransientCategoryRuleDao(int index) {
        CategoryRuleDao obj = new CategoryRuleDao();
        setCategoryId(obj, index);
        setContaining(obj, index);
        setLineorder(obj, index);
        return obj;
    }

	public void setCategoryId(CategoryRuleDao obj, int index) {
        Long categoryId = new Integer(index).longValue();
        obj.setCategoryId(categoryId);
    }

	public void setContaining(CategoryRuleDao obj, int index) {
        String containing = "containing_" + index;
        obj.setContaining(containing);
    }

	public void setLineorder(CategoryRuleDao obj, int index) {
        Long lineorder = new Integer(index).longValue();
        obj.setLineorder(lineorder);
    }

	public CategoryRuleDao getSpecificCategoryRuleDao(int index) {
        init();
        if (index < 0) {
            index = 0;
        }
        if (index > (data.size() - 1)) {
            index = data.size() - 1;
        }
        CategoryRuleDao obj = data.get(index);
        Long id = obj.getId();
        return categoryRuleRepository.findOne(id);
    }

	public CategoryRuleDao getRandomCategoryRuleDao() {
        init();
        CategoryRuleDao obj = data.get(rnd.nextInt(data.size()));
        Long id = obj.getId();
        return categoryRuleRepository.findOne(id);
    }

	public boolean modifyCategoryRuleDao(CategoryRuleDao obj) {
        return false;
    }

	public void init() {
        int from = 0;
        int to = 10;
        data = categoryRuleRepository.findAll(new org.springframework.data.domain.PageRequest(from / to, to)).getContent();
        if (data == null) {
            throw new IllegalStateException("Find entries implementation for 'CategoryRuleDao' illegally returned null");
        }
        if (!data.isEmpty()) {
            return;
        }
        
        data = new ArrayList<CategoryRuleDao>();
        for (int i = 0; i < 10; i++) {
            CategoryRuleDao obj = getNewTransientCategoryRuleDao(i);
            try {
                categoryRuleRepository.save(obj);
            } catch (final ConstraintViolationException e) {
                final StringBuilder msg = new StringBuilder();
                for (Iterator<ConstraintViolation<?>> iter = e.getConstraintViolations().iterator(); iter.hasNext();) {
                    final ConstraintViolation<?> cv = iter.next();
                    msg.append("[").append(cv.getRootBean().getClass().getName()).append(".").append(cv.getPropertyPath()).append(": ").append(cv.getMessage()).append(" (invalid value = ").append(cv.getInvalidValue()).append(")").append("]");
                }
                throw new IllegalStateException(msg.toString(), e);
            }
            categoryRuleRepository.flush();
            data.add(obj);
        }
    }
}
