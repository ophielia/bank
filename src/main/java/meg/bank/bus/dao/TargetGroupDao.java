package meg.bank.bus.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.entity.RooJpaEntity;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaEntity(table = "TARGETGROUP")
public class TargetGroupDao {

	@NotNull
	private Long targettype;

	@NotNull
	@Size(max = 60)
	private String name;

	@Size(max = 200)
	private String description;

	private Boolean isdefault;

	private String monthtag;

	private String yeartag;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "targetgroup")
	private List<TargetDetailDao> targetdetails = new ArrayList<TargetDetailDao>();
}
