package meg.bank.util.common.db;

import java.util.List;

import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.entity.RooJpaEntity;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaEntity(table = "columnkey")
public class ColumnKeyDao {

	@NotNull
	@Size(max = 100)
	private String lookup;

	@OneToMany(mappedBy = "columnkey", fetch=FetchType.EAGER)
	private List<ColumnValueDao> columnvalues;
}
