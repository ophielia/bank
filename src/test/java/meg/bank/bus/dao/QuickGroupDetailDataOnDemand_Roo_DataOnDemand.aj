// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package meg.bank.bus.dao;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import meg.bank.bus.dao.QuickGroupDataOnDemand;
import meg.bank.bus.dao.QuickGroupDetail;
import meg.bank.bus.dao.QuickGroupDetailDataOnDemand;
import meg.bank.bus.repo.QuickGroupDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

privileged aspect QuickGroupDetailDataOnDemand_Roo_DataOnDemand {
    
    declare @type: QuickGroupDetailDataOnDemand: @Component;
    
    private Random QuickGroupDetailDataOnDemand.rnd = new SecureRandom();
    
    private List<QuickGroupDetail> QuickGroupDetailDataOnDemand.data;
    
    @Autowired
    QuickGroupDataOnDemand QuickGroupDetailDataOnDemand.quickGroupDataOnDemand;
    
    @Autowired
    QuickGroupDetailRepository QuickGroupDetailDataOnDemand.quickGroupDetailRepository;
    
    public QuickGroupDetail QuickGroupDetailDataOnDemand.getNewTransientQuickGroupDetail(int index) {
        QuickGroupDetail obj = new QuickGroupDetail();
        setCatid(obj, index);
        setPercentage(obj, index);
        return obj;
    }
    
    public void QuickGroupDetailDataOnDemand.setCatid(QuickGroupDetail obj, int index) {
        Long catid = new Integer(index).longValue();
        obj.setCatid(catid);
    }
    
    public void QuickGroupDetailDataOnDemand.setPercentage(QuickGroupDetail obj, int index) {
        Double percentage = new Integer(index).doubleValue();
        obj.setPercentage(percentage);
    }
    
    public QuickGroupDetail QuickGroupDetailDataOnDemand.getSpecificQuickGroupDetail(int index) {
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
    
    public QuickGroupDetail QuickGroupDetailDataOnDemand.getRandomQuickGroupDetail() {
        init();
        QuickGroupDetail obj = data.get(rnd.nextInt(data.size()));
        Long id = obj.getId();
        return quickGroupDetailRepository.findOne(id);
    }
    
    public boolean QuickGroupDetailDataOnDemand.modifyQuickGroupDetail(QuickGroupDetail obj) {
        return false;
    }
    
    public void QuickGroupDetailDataOnDemand.init() {
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
