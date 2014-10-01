package meg.bank.bus.dao;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.validation.constraints.Size;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.entity.RooJpaEntity;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaEntity
public class QuickGroup {
	
	@Size(max = 100)
	private String name;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "quickgroup", fetch=FetchType.LAZY)
	private List<QuickGroupDetail> groupdetails;
	
}
