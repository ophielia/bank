package meg.bank.bus.dao;

import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.entity.RooJpaEntity;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaEntity(table = "TARGETDETAIL")
public class TargetDetailDao {

	@NotNull
	private Long groupid;

	@NotNull
	private Long catid;

	@NotNull
	private Double amount;

}
