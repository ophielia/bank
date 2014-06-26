package meg.bank.bus.dao;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.entity.RooJpaEntity;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaEntity(table = "CATEGORYRULE")
public class CategoryRuleDao {
	
	private Long lineorder;

	@NotNull
	private String containing;
	
	@NotNull
	private Long categoryId;
	

	@Transient
	private String catDisplay;
}
