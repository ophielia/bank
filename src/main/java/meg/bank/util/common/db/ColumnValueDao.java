package meg.bank.util.common.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
@Table(name = "columnvalue")
public class ColumnValueDao {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

	@Version
    @Column(name = "version")
    private Integer version;
	
	@NotNull
	@Size(max = 100)
	private String value;

	@NotNull
	@Size(max = 100)
	private String display;

	private Boolean active;

	@NotNull
	private Long disporder;

	@ManyToOne
	@JoinColumn(name = "keyid")
	private ColumnKeyDao columnkey;

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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Long getDisporder() {
		return disporder;
	}

	public void setDisporder(Long disporder) {
		this.disporder = disporder;
	}

	public ColumnKeyDao getColumnkey() {
		return columnkey;
	}

	public void setColumnkey(ColumnKeyDao columnkey) {
		this.columnkey = columnkey;
	}
	
	

	public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
