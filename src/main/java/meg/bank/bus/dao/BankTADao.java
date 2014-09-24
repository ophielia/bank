package meg.bank.bus.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.entity.RooJpaEntity;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaEntity(table = "BANKTRANS")
public class BankTADao {

	@Size(max = 1000)
	private String description;

	@NotNull
	private Double amount;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(style = "M-")
	private Date transdate;

	@Size(max = 1000)
	private String detail;

	private Boolean hascat;

	private Boolean deleted;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(style = "M-")
	private Date importdate;

	private Integer source;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "banktrans")
	private List<CategoryTADao> categorizedExp;

}
