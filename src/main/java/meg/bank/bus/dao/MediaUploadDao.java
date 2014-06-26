package meg.bank.bus.dao;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.entity.RooJpaEntity;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaEntity(table = "MediaUpload")
public class MediaUploadDao {
	
	private String filepath;
	
	@NotNull
	private String contentType;
	
	@NotNull
	private Integer importClient;
	
	
	@Transient
	private byte[] content;
	
}
