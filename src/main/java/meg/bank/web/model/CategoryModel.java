package meg.bank.web.model;

import java.util.HashMap;
import java.util.List;

import meg.bank.bus.dao.CategoryDao;

public class CategoryModel {




	private Long parentcatid;

	private List<CategoryDao> subcategories;

	private CategoryDao category;
	
	private HashMap<Long, CategoryDao> allcategories;

	public CategoryModel() {
		this.category = new CategoryDao();
		this.parentcatid=0L;
		
	}
	
	public CategoryModel(CategoryDao categoryDao, HashMap<Long, CategoryDao> allcats) {
		this.category = categoryDao;
		this.allcategories = allcats;
	}

	
	
	public Long getId() {
		return getCategory().getId();
	}

	public void setId(Long id) {
		getCategory().setId(id);
	}

	public String getName() {
		return this.category.getName();
	}

	public void setName(String name) {
		this.category.setName(name);
	}

	public String getDescription() {
		return this.category.getDescription();
	}

	public void setDescription(String description) {
		this.category.setDescription(description);
	}

	public Boolean getNonexpense() {
		return this.category.getNonexpense();
	}

	public void setNonexpense(Boolean nonexpense) {
		this.category.setNonexpense(nonexpense);
	}

	public Boolean getDisplayinlist() {
		return this.category.getDisplayinlist();
	}

	public void setDisplayinlist(Boolean displayinlist) {
		this.category.setDisplayinlist(displayinlist);
	}

	public Long getParentcatid() {
		return parentcatid;
	}

	public void setParentcatid(Long parentcatid) {
		this.parentcatid = parentcatid;
	}

	public List<CategoryDao> getSubcategories() {
		return subcategories;
	}

	public void setSubcategories(List<CategoryDao> subcategories) {
		this.subcategories = subcategories;
	}

	public CategoryDao getCategory() {
		return category;
	}

	public void setCategory(CategoryDao category) {
		this.category = category;
	}

	public HashMap<Long, CategoryDao> getAllcategories() {
		return allcategories;
	}

	public void setAllcategories(HashMap<Long, CategoryDao> allcategories) {
		this.allcategories = allcategories;
	}
	
	
	public boolean hasParent() {
		return this.parentcatid!=null && this.parentcatid > 0;
	}
	
}


