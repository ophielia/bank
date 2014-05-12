package meg.bank.bus.dao;

import java.util.Date;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.entity.RooJpaEntity;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaEntity(table = "EXPENSE")
public class ExpenseDao {

	private Long transid;
	private Date transdate;
	private Boolean hascat;
	private String description;
	private String detail;
	private Double transtotal;
	private Long cattransid;
	private Double catamount;
	private Long catid;
	private String dispCat;
	private String catName;
	private Boolean nonexpense;
	private Boolean deleted;
	private String comment;
	private String month;
	private String year;
	private Integer source;

}
