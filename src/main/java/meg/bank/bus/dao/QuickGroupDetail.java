package meg.bank.bus.dao;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.entity.RooJpaEntity;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaEntity
public class QuickGroupDetail {
	
	private Long catid;
	private Double percentage;
	@ManyToOne
	private QuickGroup quickgroup;
	@Transient
	private String catdisplay;
	
}
