package meg.bank.web.model;

import meg.bank.bus.dao.TargetDetailDao;
import meg.bank.bus.dao.TargetGroupDao;

public class TargetModel {

	private TargetGroupDao targetgroup;
	private TargetDetailDao targetdetail;

	private int actionidx;
	
	

	// getters and setters for group portion
	public Long getTargettype() {
		return targetgroup.getTargettype();
	}

	public void setTargettype(Long targettype) {
		targetgroup.setTargettype(targettype);
	}

	public String getName() {
		return targetgroup.getName();
	}

	public void setName(String name) {
		targetgroup.setName(name);
	}

	public String getDescription() {
		return targetgroup.getDescription();
	}

	public void setDescription(String description) {
		targetgroup.setDescription(description);
	}

	public Boolean getIsdefault() {
		return targetgroup.getIsdefault();
	}

	public void setIsdefault(Boolean isdefault) {
		targetgroup.setIsdefault(isdefault);
	}

	public String getMonthtag() {
		return targetgroup.getMonthtag();
	}

	public void setMonthtag(String monthtag) {
		targetgroup.setMonthtag(monthtag);
	}

	public String getYeartag() {
		return targetgroup.getYeartag();
	}

	public void setYeartag(String yeartag) {
		targetgroup.setYeartag(yeartag);
	}

	// getters and setters for detail portion
	public Long getCatid() {
		return targetdetail.getCatid();
	}

	public void setCatid(Long catid) {
		targetdetail.setCatid(catid);
	}

	public Double getAmount() {
		return targetdetail.getAmount();
	}

	public void setAmount(Double amount) {
		targetdetail.setAmount(amount);
	}

	public int getActionidx() {
		return actionidx;
	}

	public void setActionidx(int actionidx) {
		this.actionidx = actionidx;
	}

}
