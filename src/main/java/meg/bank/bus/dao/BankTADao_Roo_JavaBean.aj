// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package meg.bank.bus.dao;

import java.util.Date;
import java.util.List;
import meg.bank.bus.dao.BankTADao;
import meg.bank.bus.dao.CategoryTADao;

privileged aspect BankTADao_Roo_JavaBean {
    
    public String BankTADao.getDescription() {
        return this.description;
    }
    
    public void BankTADao.setDescription(String description) {
        this.description = description;
    }
    
    public Double BankTADao.getAmount() {
        return this.amount;
    }
    
    public void BankTADao.setAmount(Double amount) {
        this.amount = amount;
    }
    
    public Date BankTADao.getTransdate() {
        return this.transdate;
    }
    
    public void BankTADao.setTransdate(Date transdate) {
        this.transdate = transdate;
    }
    
    public String BankTADao.getDetail() {
        return this.detail;
    }
    
    public void BankTADao.setDetail(String detail) {
        this.detail = detail;
    }
    
    public Boolean BankTADao.getHascat() {
        return this.hascat;
    }
    
    public void BankTADao.setHascat(Boolean hascat) {
        this.hascat = hascat;
    }
    
    public Boolean BankTADao.getDeleted() {
        return this.deleted;
    }
    
    public void BankTADao.setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
    
    public Date BankTADao.getImportdate() {
        return this.importdate;
    }
    
    public void BankTADao.setImportdate(Date importdate) {
        this.importdate = importdate;
    }
    
    public Integer BankTADao.getSource() {
        return this.source;
    }
    
    public void BankTADao.setSource(Integer source) {
        this.source = source;
    }
    
    public List<CategoryTADao> BankTADao.getCategorizedExp() {
        return this.categorizedExp;
    }
    
    public void BankTADao.setCategorizedExp(List<CategoryTADao> categorizedExp) {
        this.categorizedExp = categorizedExp;
    }
    
}
