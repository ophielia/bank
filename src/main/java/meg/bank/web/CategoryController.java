package meg.bank.web;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import meg.bank.bus.CategoryService;
import meg.bank.bus.dao.CatRelationshipDao;
import meg.bank.bus.dao.CategoryDao;
import meg.bank.bus.repo.CategoryRepository;
import meg.bank.web.model.CategoryModel;
import meg.bank.web.validation.CategoryModelValidator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;


@RequestMapping("/categories")
@SessionAttributes("categoryModel")
@Controller
public class CategoryController {

	@Autowired
	CategoryRepository catRepo;
	
	@Autowired
	CategoryService categoryService;
	
	@Autowired
	CategoryModelValidator catValidator;
	
	private void populateCategoryList(Model uiModel) {
		uiModel.addAttribute("catList",catRepo.findAll());
	}
	
	@RequestMapping(produces = "text/html")
    public String showList(Model uiModel) {
    	populateCategoryList(uiModel);
    	return "categories/list";
    }
	
    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(CategoryModel model,  Model uiModel,BindingResult bindingResult, HttpServletRequest httpServletRequest) {
    	catValidator.validate(model, bindingResult);

		if (bindingResult.hasErrors()) {
			populateEditForm(uiModel, model);
			return "categories/create";
		}
        uiModel.asMap().clear();
        CategoryDao cat=categoryService.addCategory( model.getName(),  model.getDescription(), model.getNonexpense(), model.getDisplayinlist());

        if (!model.getParentcatid().equals("0")) {
            // now, update relationship
            CatRelationshipDao rel = categoryService.changeCatMembership(cat.getId(),  model.getParentcatid());
        }
        
        return "redirect:/categories";
        //return "redirect:/categories/" + encodeUrlPathSegment(cat.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(params = "form",method = RequestMethod.GET, produces = "text/html")
    public String createForm(Model uiModel) {
    	HashMap<Long,CategoryDao> allcats = categoryService.getCategoriesAsMap();
    	CategoryModel newmodel = new CategoryModel(new CategoryDao(),allcats);
        populateEditForm(uiModel, newmodel);
        return "categories/create";
    }
    

    @RequestMapping(value="/edit/{id}", method = RequestMethod.PUT, produces = "text/html")
    public String edit(@PathVariable("id") Long id,CategoryModel model,  Model uiModel,BindingResult bindingResult, HttpServletRequest httpServletRequest) {
    	catValidator.validate(model, bindingResult);

		if (bindingResult.hasErrors()) {
//			populateEditForm(uiModel, model);
			return "categories/edit";
		}
        uiModel.asMap().clear();
        CategoryDao cat=categoryService.updateCategory(model.getCategory());

            // now, update relationship
            CatRelationshipDao rel = categoryService.changeCatMembership(cat.getId(), model.getParentcatid());
        
        return "redirect:/categories";
        //return "redirect:/categories/" + encodeUrlPathSegment(cat.getId().toString(), httpServletRequest);
    }
    

    
    
    @RequestMapping(params = "form",value = "/edit/{id}",method = RequestMethod.GET, produces = "text/html")
    public String createEditForm(@PathVariable("id") Long id,Model uiModel) {
    	HashMap<Long,CategoryDao> allcats = categoryService.getCategoriesAsMap();
    	CategoryDao cat = catRepo.findOne(id);
    	if (cat!=null) {
        	CategoryModel newmodel = new CategoryModel(cat,allcats);
        	List<CategoryDao> list = categoryService.getDirectSubcategories(cat.getId());
        	newmodel.setSubcategories(list);

            populateEditForm(uiModel, newmodel);
            return "categories/edit";
    		
    	}
    	return "redirect:/categories";
    }
    
    void populateEditForm(Model uiModel, CategoryModel model) {
        uiModel.addAttribute("categoryModel", model);
    }    
    
    String encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
        String enc = httpServletRequest.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        } catch (UnsupportedEncodingException uee) {}
        return pathSegment;
    }    
}
