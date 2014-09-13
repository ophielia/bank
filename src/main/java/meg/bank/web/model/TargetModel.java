package meg.bank.web.model;

import java.util.List;

import meg.bank.bus.dao.TargetDetailDao;
import meg.bank.bus.dao.TargetGroupDao;

public class TargetModel {

	private TargetGroupDao targetgroup;

	private Long catid;
	private Double amount;
	
	private String action;
	private int actionid;
	private String targettypedisp;
	
	

	public TargetModel(TargetGroupDao targetgroup) {
		super();
		this.targetgroup = targetgroup;
	}

	public TargetModel() {
		this.targetgroup = new TargetGroupDao();
	}
	
	public boolean containsDetailEntry() {
		return (amount!=null || catid!=null);
	}

	// getters and setters for main members
	public TargetGroupDao getTargetgroup() {
		return targetgroup;
	}

	public void setTargetgroup(TargetGroupDao targetgroup) {
		this.targetgroup = targetgroup;
	}

	public List<TargetDetailDao> getTargetdetails() {
		return targetgroup.getTargetdetails();
	}

	public void setTargetdetails(List<TargetDetailDao> targetdetails) {
		this.targetgroup.setTargetdetails(targetdetails);
	}


	
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


	

	public int getActionid() {
		return actionid;
	}

	public void setActionid(int actionidx) {
		this.actionid = actionidx;
	}

	
	
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setTargettypeDisplay(String ttypedisp) {
		this.targettypedisp = ttypedisp;
		
	}
	
	public String getTargettypeDisplay() {
		return this.targettypedisp;
	}

}
