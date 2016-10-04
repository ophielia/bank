package meg.bank.bus.dao;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import meg.bank.bus.repo.CategoryTARepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

@Component
@Configurable
public class CategoryTADaoDataOnDemand {

	private Random rnd = new SecureRandom();

	private List<CategoryTADao> data;

	@Autowired
    BankTADaoDataOnDemand bankTADaoDataOnDemand;

	@Autowired
    CategoryTARepository catTARepository;

	public CategoryTADao getNewTransientCategoryTADao(int index) {
        CategoryTADao obj = new CategoryTADao();
        setAmount(obj, index);
        setCatid(obj, index);
        setComment(obj, index);
        setCreatedon(obj, index);
        return obj;
    }

	public void setAmount(CategoryTADao obj, int index) {
        Double amount = new Integer(index).doubleValue();
        obj.setAmount(amount);
    }

	public void setCatid(CategoryTADao obj, int index) {
        Long catid = new Integer(index).longValue();
        obj.setCatid(catid);
    }

	public void setComment(CategoryTADao obj, int index) {
        String comment = "comment_" + index;
        if (comment.length() > 300) {
            comment = comment.substring(0, 300);
        }
        obj.setComment(comment);
    }

	public void setCreatedon(CategoryTADao obj, int index) {
        Date createdon = new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH), Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), Calendar.getInstance().get(Calendar.SECOND) + new Double(Math.random() * 1000).intValue()).getTime();
        obj.setCreatedon(createdon);
    }

	public CategoryTADao getSpecificCategoryTADao(int index) {
        init();
        if (index < 0) {
            index = 0;
        }
        if (index > (data.size() - 1)) {
            index = data.size() - 1;
        }
        CategoryTADao obj = data.get(index);
        Long id = obj.getId();
        return catTARepository.findOne(id);
    }

	public CategoryTADao getRandomCategoryTADao() {
        init();
        CategoryTADao obj = data.get(rnd.nextInt(data.size()));
        Long id = obj.getId();
        return catTARepository.findOne(id);
    }

	public boolean modifyCategoryTADao(CategoryTADao obj) {
        return false;
    }

	public void init() {
        int from = 0;
        int to = 10;
        data = catTARepository.findAll(new org.springframework.data.domain.PageRequest(from / to, to)).getContent();
        if (data == null) {
            throw new IllegalStateException("Find entries implementation for 'CategoryTADao' illegally returned null");
        }
        if (!data.isEmpty()) {
            return;
        }
        
        data = new ArrayList<CategoryTADao>();
        for (int i = 0; i < 10; i++) {
            CategoryTADao obj = getNewTransientCategoryTADao(i);
            try {
                catTARepository.save(obj);
            } catch (final ConstraintViolationException e) {
                final StringBuilder msg = new StringBuilder();
                for (Iterator<ConstraintViolation<?>> iter = e.getConstraintViolations().iterator(); iter.hasNext();) {
                    final ConstraintViolation<?> cv = iter.next();
                    msg.append("[").append(cv.getRootBean().getClass().getName()).append(".").append(cv.getPropertyPath()).append(": ").append(cv.getMessage()).append(" (invalid value = ").append(cv.getInvalidValue()).append(")").append("]");
                }
                throw new IllegalStateException(msg.toString(), e);
            }
            catTARepository.flush();
            data.add(obj);
        }
    }
}
