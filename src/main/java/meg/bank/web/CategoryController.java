package meg.bank.web;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import meg.bank.bus.CategoryLevel;
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
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
	
	private String categorydisptype="categorydisptype";
	
	public final class DispType {
		public final static int GRID=0;
		public final static int LIST=1;
	}
	
	private void populateCategoryList(Model uiModel) {
		uiModel.addAttribute("catList",categoryService.getCategories(true));
	}
	
	@ModelAttribute("categorylist")
	protected List<CategoryDao> referenceCategoryData(HttpServletRequest request, Object command,
			Errors errors) throws Exception {
		List<CategoryDao> list = categoryService.getCategories(true);
		
		
		// return model
		return list;
	}	
	
	@ModelAttribute("categorylvllist")
	protected List<CategoryLevel> referenceCategoryLevelData(HttpServletRequest request, Object command,
			Errors errors) throws Exception {
		List<CategoryLevel> list = categoryService.getCategoriesUpToLevel(999);
		// return model
		return list;
	}		
	
	@RequestMapping(produces = "text/html")
    public String showCategories(Model uiModel, HttpServletRequest httpServletRequest) {
		HttpSession session = httpServletRequest.getSession();
		if (session.getAttribute(categorydisptype)!=null) {
			int display = (int) session.getAttribute(categorydisptype);
			if (display==DispType.GRID) {
				return "categories/grid";
			} else {
				return "categories/list";
			}
		}
		session.setAttribute(categorydisptype, DispType.LIST);
		return "categories/list";
	}
	

    
    @RequestMapping(params="display", method = RequestMethod.GET, produces = "text/html")
    public String setDisplayType(@RequestParam("display") int display, Model uiModel,HttpServletRequest httpServletRequest) {
		HttpSession session = httpServletRequest.getSession();
		session.setAttribute(categorydisptype,display);
		if (display==DispType.GRID) {
			return "categories/grid";
		} else {
			return "categories/list";
		}
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
    	CategoryModel newmodel = categoryService.loadCategoryModel(id);
    	if (newmodel.getCategory()!=null) {
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
