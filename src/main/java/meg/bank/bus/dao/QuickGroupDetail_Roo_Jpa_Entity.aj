// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package meg.bank.bus.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;
import meg.bank.bus.dao.QuickGroupDetail;

privileged aspect QuickGroupDetail_Roo_Jpa_Entity {
    
    declare @type: QuickGroupDetail: @Entity;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long QuickGroupDetail.id;
    
    @Version
    @Column(name = "version")
    private Integer QuickGroupDetail.version;
    
    public Long QuickGroupDetail.getId() {
        return this.id;
    }
    
    public void QuickGroupDetail.setId(Long id) {
        this.id = id;
    }
    
    public Integer QuickGroupDetail.getVersion() {
        return this.version;
    }
    
    public void QuickGroupDetail.setVersion(Integer version) {
        this.version = version;
    }
    
}