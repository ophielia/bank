package meg.bank.bus.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
@Table(name  = "TARGETGROUP")
public class TargetGroupDao {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

	@Version
    @Column(name = "version")
    private Integer version;
	
	@NotNull
	private Long targettype;

	@NotNull
	@Size(max = 60)
	private String name;

	@Size(max = 200)
	private String description;

	private Boolean isdefault;

	private String monthtag;

	private String yeartag;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "targetgroup")
	private List<TargetDetailDao> targetdetails = new ArrayList<TargetDetailDao>();

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

	public Long getTargettype() {
		return targettype;
	}

	public void setTargettype(Long targettype) {
		this.targettype = targettype;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getIsdefault() {
		return isdefault;
	}

	public void setIsdefault(Boolean isdefault) {
		this.isdefault = isdefault;
	}

	public String getMonthtag() {
		return monthtag;
	}

	public void setMonthtag(String monthtag) {
		this.monthtag = monthtag;
	}

	public String getYeartag() {
		return yeartag;
	}

	public void setYeartag(String yeartag) {
		this.yeartag = yeartag;
	}

	public List<TargetDetailDao> getTargetdetails() {
		return targetdetails;
	}

	public void setTargetdetails(List<TargetDetailDao> targetdetails) {
		this.targetdetails = targetdetails;
	}
	
	
	
	public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
	
}
