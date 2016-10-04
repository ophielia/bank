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
import meg.bank.bus.repo.BankTARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

@Configurable
@Component
public class BankTADaoDataOnDemand {

	private Random rnd = new SecureRandom();

	private List<BankTADao> data;

	@Autowired
    BankTARepository bankTARepository;

	public BankTADao getNewTransientBankTADao(int index) {
        BankTADao obj = new BankTADao();
        setAmount(obj, index);
        setDeleted(obj, index);
        setDescription(obj, index);
        setDetail(obj, index);
        setHascat(obj, index);
        setImportdate(obj, index);
        setSource(obj, index);
        setTransdate(obj, index);
        return obj;
    }

	public void setAmount(BankTADao obj, int index) {
        Double amount = new Integer(index).doubleValue();
        obj.setAmount(amount);
    }

	public void setDeleted(BankTADao obj, int index) {
        Boolean deleted = Boolean.TRUE;
        obj.setDeleted(deleted);
    }

	public void setDescription(BankTADao obj, int index) {
        String description = "description_" + index;
        if (description.length() > 1000) {
            description = description.substring(0, 1000);
        }
        obj.setDescription(description);
    }

	public void setDetail(BankTADao obj, int index) {
        String detail = "detail_" + index;
        if (detail.length() > 1000) {
            detail = detail.substring(0, 1000);
        }
        obj.setDetail(detail);
    }

	public void setHascat(BankTADao obj, int index) {
        Boolean hascat = Boolean.TRUE;
        obj.setHascat(hascat);
    }

	public void setImportdate(BankTADao obj, int index) {
        Date importdate = new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH), Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), Calendar.getInstance().get(Calendar.SECOND) + new Double(Math.random() * 1000).intValue()).getTime();
        obj.setImportdate(importdate);
    }

	public void setSource(BankTADao obj, int index) {
        Integer source = new Integer(index);
        obj.setSource(source);
    }

	public void setTransdate(BankTADao obj, int index) {
        Date transdate = new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH), Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), Calendar.getInstance().get(Calendar.SECOND) + new Double(Math.random() * 1000).intValue()).getTime();
        obj.setTransdate(transdate);
    }

	public BankTADao getSpecificBankTADao(int index) {
        init();
        if (index < 0) {
            index = 0;
        }
        if (index > (data.size() - 1)) {
            index = data.size() - 1;
        }
        BankTADao obj = data.get(index);
        Long id = obj.getId();
        return bankTARepository.findOne(id);
    }

	public BankTADao getRandomBankTADao() {
        init();
        BankTADao obj = data.get(rnd.nextInt(data.size()));
        Long id = obj.getId();
        return bankTARepository.findOne(id);
    }

	public boolean modifyBankTADao(BankTADao obj) {
        return false;
    }

	public void init() {
        int from = 0;
        int to = 10;
        data = bankTARepository.findAll(new org.springframework.data.domain.PageRequest(from / to, to)).getContent();
        if (data == null) {
            throw new IllegalStateException("Find entries implementation for 'BankTADao' illegally returned null");
        }
        if (!data.isEmpty()) {
            return;
        }

        data = new ArrayList<BankTADao>();
        for (int i = 0; i < 10; i++) {
            BankTADao obj = getNewTransientBankTADao(i);
            try {
                bankTARepository.save(obj);
            } catch (final ConstraintViolationException e) {
                final StringBuilder msg = new StringBuilder();
                for (Iterator<ConstraintViolation<?>> iter = e.getConstraintViolations().iterator(); iter.hasNext();) {
                    final ConstraintViolation<?> cv = iter.next();
                    msg.append("[").append(cv.getRootBean().getClass().getName()).append(".").append(cv.getPropertyPath()).append(": ").append(cv.getMessage()).append(" (invalid value = ").append(cv.getInvalidValue()).append(")").append("]");
                }
                throw new IllegalStateException(msg.toString(), e);
            }
            bankTARepository.flush();
            data.add(obj);
        }
    }
}
