package meg.bank.bus.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
@Table(name = "TARGETDETAIL")
public class TargetDetailDao {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

	@Version
    @Column(name = "version")
    private Integer version;

	@NotNull
	@Min(1)
	private Long catid;

	@NotNull
	@Min(1)
	private Double amount;
	
    @ManyToOne
    @JoinColumn(name="targetgroup")
    private TargetGroupDao targetgroup;
    
    @Transient
    private String catdisplay;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Long getCatid() {
		return catid;
	}

	public void setCatid(Long catid) {
		this.catid = catid;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public TargetGroupDao getTargetgroup() {
		return targetgroup;
	}

	public void setTargetgroup(TargetGroupDao targetgroup) {
		this.targetgroup = targetgroup;
	}

	public String getCatdisplay() {
		return catdisplay;
	}

	public void setCatdisplay(String catdisplay) {
		this.catdisplay = catdisplay;
	}

	public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    
}
