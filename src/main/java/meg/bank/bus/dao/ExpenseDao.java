package meg.bank.bus.dao;

import java.util.Date;

import javax.persistence.SqlResultSetMapping;
import javax.persistence.ColumnResult;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FetchType;
import javax.persistence.FieldResult;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.entity.RooJpaEntity;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaEntity(table = "EXPENSE")
@SqlResultSetMapping(name="ExpenseResult", 
entities={ 
    @EntityResult(entityClass=meg.bank.bus.dao.ExpenseDao.class, fields={
        @FieldResult(name="id", column="id"),
        @FieldResult(name="transid", column="id"),
        @FieldResult(name="transdate", column="id"),
        @FieldResult(name="hascat", column="id"),
        @FieldResult(name="description", column="id"),
        @FieldResult(name="detail", column="id"),
        @FieldResult(name="transtotal", column="id"),
        @FieldResult(name="cattransid", column="id"),
        @FieldResult(name="catamount", column="id"),
        @FieldResult(name="catid", column="id"),
        @FieldResult(name="dispCat", column="id"),
        @FieldResult(name="catName", column="id"),
        @FieldResult(name="nonexpense", column="id"),
        @FieldResult(name="deleted", column="id"),
        @FieldResult(name="comment", column="id"),
        @FieldResult(name="month", column="id"),
        @FieldResult(name="year", column="id"),
        @FieldResult(name="source", column="id")})},
columns={}
)
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
