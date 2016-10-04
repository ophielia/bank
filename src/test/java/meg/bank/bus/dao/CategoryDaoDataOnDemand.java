package meg.bank.bus.dao;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import meg.bank.bus.repo.CategoryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

@Component
@Configurable
public class CategoryDaoDataOnDemand {

	private Random rnd = new SecureRandom();

	private List<CategoryDao> data;

	@Autowired
    CategoryRepository categoryRepository;

	public CategoryDao getNewTransientCategoryDao(int index) {
        CategoryDao obj = new CategoryDao();
        setDescription(obj, index);
        setDisplayinlist(obj, index);
        setName(obj, index);
        setNonexpense(obj, index);
        return obj;
    }

	public void setDescription(CategoryDao obj, int index) {
        String description = "description_" + index;
        if (description.length() > 300) {
            description = description.substring(0, 300);
        }
        obj.setDescription(description);
    }

	public void setDisplayinlist(CategoryDao obj, int index) {
        Boolean displayinlist = Boolean.TRUE;
        obj.setDisplayinlist(displayinlist);
    }

	public void setName(CategoryDao obj, int index) {
        String name = "name_" + index;
        if (name.length() > 100) {
            name = name.substring(0, 100);
        }
        obj.setName(name);
    }

	public void setNonexpense(CategoryDao obj, int index) {
        Boolean nonexpense = Boolean.TRUE;
        obj.setNonexpense(nonexpense);
    }

	public CategoryDao getSpecificCategoryDao(int index) {
        init();
        if (index < 0) {
            index = 0;
        }
        if (index > (data.size() - 1)) {
            index = data.size() - 1;
        }
        CategoryDao obj = data.get(index);
        Long id = obj.getId();
        return categoryRepository.findOne(id);
    }

	public CategoryDao getRandomCategoryDao() {
        init();
        CategoryDao obj = data.get(rnd.nextInt(data.size()));
        Long id = obj.getId();
        return categoryRepository.findOne(id);
    }

	public boolean modifyCategoryDao(CategoryDao obj) {
        return false;
    }

	public void init() {
        int from = 0;
        int to = 10;
        data = categoryRepository.findAll(new org.springframework.data.domain.PageRequest(from / to, to)).getContent();
        if (data == null) {
            throw new IllegalStateException("Find entries implementation for 'CategoryDao' illegally returned null");
        }
        if (!data.isEmpty()) {
            return;
        }
        
        data = new ArrayList<CategoryDao>();
        for (int i = 0; i < 10; i++) {
            CategoryDao obj = getNewTransientCategoryDao(i);
            try {
                categoryRepository.save(obj);
            } catch (final ConstraintViolationException e) {
                final StringBuilder msg = new StringBuilder();
                for (Iterator<ConstraintViolation<?>> iter = e.getConstraintViolations().iterator(); iter.hasNext();) {
                    final ConstraintViolation<?> cv = iter.next();
                    msg.append("[").append(cv.getRootBean().getClass().getName()).append(".").append(cv.getPropertyPath()).append(": ").append(cv.getMessage()).append(" (invalid value = ").append(cv.getInvalidValue()).append(")").append("]");
                }
                throw new IllegalStateException(msg.toString(), e);
            }
            categoryRepository.flush();
            data.add(obj);
        }
    }
}
