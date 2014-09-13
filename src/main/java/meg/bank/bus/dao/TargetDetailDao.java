package meg.bank.bus.dao;

import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.entity.RooJpaEntity;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaEntity(table = "TARGETDETAIL")
public class TargetDetailDao {



	@NotNull
	@Min(1)
	private Long catid;

	@NotNull
	@Min(1)
	private Double amount;
	
    @ManyToOne
    private TargetGroupDao targetgroup;
    
    @Transient
    private String catdisplay;

}
