package meg.bank.web;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import meg.bank.bus.CategoryService;
import meg.bank.bus.dao.CategoryDao;
import meg.bank.bus.dao.CategoryRuleDao;
import meg.bank.bus.repo.CategoryRepository;
import meg.bank.bus.repo.CategoryRuleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

@RequestMapping("/categoryrule")
@Controller
@RooWebScaffold(path = "categoryrule", formBackingObject = CategoryRuleDao.class)
public class CategoryRuleController {
	
	 @Autowired
	 CategoryRepository catRepo;
	
	 @Autowired
	 CategoryRuleRepository categoryRuleRepository;
		
	 @Autowired
	 CategoryService catService;	    
	 
		@ModelAttribute("categoryList")
		public List<CategoryDao> getAllCategories() {
			return catRepo.findAll(new Sort(Sort.Direction.ASC,"name"));
		}
		
	 private void populateRuleList(Model uiModel) {
		 // get all category rules
		 List<CategoryRuleDao> rules = categoryRuleRepository.findAll(new Sort(Sort.Direction.ASC, "lineorder"));
		 // get all categories
		 HashMap<Long, CategoryDao> cats = catService.getCategoriesAsMap(false);
		 // insert displays into rules
		 if (rules!=null ) {
			 for (CategoryRuleDao rule:rules) {
				 Long key = rule.getCategoryId();
				 CategoryDao cat = cats.get(key);
				 if (cat!=null) {
					 rule.setCatDisplay(cat.getName());
				 }
			 }
		 }
		 // place list into model
		 uiModel.addAttribute("categoryruledaos",rules);
	 }
	
    @RequestMapping(produces = "text/html")
    public String list( Model uiModel) {
        populateRuleList(uiModel);
        return "categoryrule/list";
    }
 
	@RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid CategoryRuleDao categoryRuleDao, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, categoryRuleDao);
            return "categoryrule/create";
        }
        uiModel.asMap().clear();
        catService.createOrUpdCategoryRule(categoryRuleDao);
        return "redirect:/categoryrule/";
    }

	@RequestMapping(value = "/{id}", produces = "text/html")
	public String show(@PathVariable("id") Long id, Model uiModel) {
		CategoryRuleDao rule = categoryRuleRepository.findOne(id);
		if (rule != null) {
			CategoryDao cat = catRepo.findOne(rule.getCategoryId());
			rule.setCatDisplay(cat.getName());
		}
		uiModel.addAttribute("categoryruledao",
				categoryRuleRepository.findOne(id));
		uiModel.addAttribute("itemId", id);
		return "categoryrule/show";
	}

	@RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(@Valid CategoryRuleDao categoryRuleDao, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, categoryRuleDao);
            return "categoryrule/update";
        }
        uiModel.asMap().clear();
        catService.createOrUpdCategoryRule(categoryRuleDao);
        return "redirect:/categoryrule/" + encodeUrlPathSegment(categoryRuleDao.getId().toString(), httpServletRequest);
    }

	@RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        populateEditForm(uiModel, categoryRuleRepository.findOne(id));
        return "categoryrule/update";
    }
	
    
    private void populateEditForm(Model uiModel, CategoryRuleDao categoryRuleDao) {
        uiModel.addAttribute("categoryRuleDao", categoryRuleDao);
    }
	
	
	

	 
	    private String encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
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
