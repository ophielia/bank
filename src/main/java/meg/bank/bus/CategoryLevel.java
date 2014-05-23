package meg.bank.bus;

import meg.bank.bus.dao.CategoryDao;

public class CategoryLevel {
private CategoryDao category;
private int level;
public CategoryLevel(CategoryDao cat,int level) {
	this.category = cat;
	this.level = level;
}
public int getLevel() {
	return level;
}
public void setLevel(int level) {
	this.level = level;
}
public CategoryDao getCategory() {
	return category;
}
public void setCategory(CategoryDao category) {
	this.category = category;
}


}
