package meg.bank.bus.dao;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.entity.RooJpaEntity;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaEntity(table = "CATEGORY")
public class CategoryDao {

	@NotNull
	@Size(max = 100)
	private String name;

	@Size(max = 300)
	private String description;
	
	private Boolean nonexpense;
	
	private Boolean displayinlist;

}
