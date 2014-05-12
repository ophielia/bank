package meg.util.common.dao;

public class ColumnValueDao {

	private Long id;
	private String value;
	private String display;
	private Long keyid;
	private Boolean active;
	private Long disporder;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Boolean getIsActive() {
		return active;
	}
	public void setIsActive(Boolean active) {
		this.active = active;
	}
	public String getDisplay() {
		return display;
	}
	public void setDisplay(String display) {
		this.display = display;
	}
	public Long getKeyid() {
		return keyid;
	}
	public void setKeyid(Long keyid) {
		this.keyid = keyid;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Long getDisporder() {
		return disporder;
	}
	public void setDisporder(Long disporder) {
		this.disporder = disporder;
	}
	
	
	
}
