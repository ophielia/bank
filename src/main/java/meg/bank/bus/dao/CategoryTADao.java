package meg.bank.bus.dao;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.entity.RooJpaEntity;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaEntity(table = "CATTRANS")
public class CategoryTADao {

	@NotNull
	private Long catid;

	@NotNull
	private Double amount;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(style = "M-")
	private Date createdon;



	@Size(max = 300)
	private String comment;

	@ManyToOne
	@JoinColumn(name="banktaid")
	private BankTADao banktrans;

	@Transient
	private String catdisplay;



	public String getCatdisplay() {
		return catdisplay;
	}

	public void setCatdisplay(String catdisplay) {
		this.catdisplay = catdisplay;
	}


}
