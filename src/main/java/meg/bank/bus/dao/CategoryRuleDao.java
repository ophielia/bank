package meg.bank.bus.dao;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "CATEGORYRULE")
public class CategoryRuleDao {
	

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

	@Version
    @Column(name = "version")
    private Integer version;
	
	private Long lineorder;

	@NotNull
	private String containing;
	
	@NotNull
	@Column(name = "category_id")
	private Long categoryId;
	

	@Transient
	private String catDisplay;


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


	public Long getLineorder() {
		return lineorder;
	}


	public void setLineorder(Long lineorder) {
		this.lineorder = lineorder;
	}


	public String getContaining() {
		return containing;
	}


	public void setContaining(String containing) {
		this.containing = containing;
	}


	public Long getCategoryId() {
		return categoryId;
	}


	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}


	public String getCatDisplay() {
		return catDisplay;
	}


	public void setCatDisplay(String catDisplay) {
		this.catDisplay = catDisplay;
	}
	
	
}
