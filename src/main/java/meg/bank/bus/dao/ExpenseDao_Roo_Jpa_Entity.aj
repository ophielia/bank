// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package meg.bank.bus.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import meg.bank.bus.dao.ExpenseDao;

privileged aspect ExpenseDao_Roo_Jpa_Entity {
    
    declare @type: ExpenseDao: @Entity;
    
    declare @type: ExpenseDao: @Table(name = "EXPENSE");
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long ExpenseDao.id;
    
    @Version
    @Column(name = "version")
    private Integer ExpenseDao.version;
    
    public Long ExpenseDao.getId() {
        return this.id;
    }
    
    public void ExpenseDao.setId(Long id) {
        this.id = id;
    }
    
    public Integer ExpenseDao.getVersion() {
        return this.version;
    }
    
    public void ExpenseDao.setVersion(Integer version) {
        this.version = version;
    }
    
}
