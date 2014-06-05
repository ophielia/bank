package meg.bank.web;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import meg.bank.bus.CategoryService;
import meg.bank.bus.dao.CategoryDao;
import meg.bank.bus.repo.CategoryRepository;
import meg.bank.web.model.CategoryModel;
import meg.bank.web.validation.CategoryModelValidator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;


@RequestMapping("/categories")
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
        return "redirect:/categories";
        //return "redirect:/categories/" + encodeUrlPathSegment(cat.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(params = "form",method = RequestMethod.GET, produces = "text/html")
    public String createForm(Model uiModel) {
    	HashMap<Long,String> allcats = categoryService.getCategoriesAsMap();
    	CategoryModel newmodel = new CategoryModel(new CategoryDao(),allcats);
        populateEditForm(uiModel, newmodel);
        return "categories/create";
    }
    
    void populateEditForm(Model uiModel, CategoryModel model) {
        uiModel.addAttribute("category", model);
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
