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
import meg.bank.bus.dao.CategoryRuleDao;

privileged aspect CategoryRuleDao_Roo_Jpa_Entity {
    
    declare @type: CategoryRuleDao: @Entity;
    
    declare @type: CategoryRuleDao: @Table(name = "CATEGORYRULE");
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long CategoryRuleDao.id;
    
    @Version
    @Column(name = "version")
    private Integer CategoryRuleDao.version;
    
    public Long CategoryRuleDao.getId() {
        return this.id;
    }
    
    public void CategoryRuleDao.setId(Long id) {
        this.id = id;
    }
    
    public Integer CategoryRuleDao.getVersion() {
        return this.version;
    }
    
    public void CategoryRuleDao.setVersion(Integer version) {
        this.version = version;
    }
    
}
