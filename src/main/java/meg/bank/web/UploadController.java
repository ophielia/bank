package meg.bank.web;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import meg.bank.bus.dao.MediaUploadDao;
import meg.bank.bus.imp.ImportManager;
import meg.bank.bus.repo.MediaUploadRepository;
import meg.bank.util.common.ColumnManagerService;
import meg.bank.util.common.db.ColumnValueDao;

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
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

@RequestMapping("/import")
@Controller
public class UploadController {
	
    @Autowired
    ColumnManagerService cvManager;
	
    @Autowired
    ImportManager importManager;
    
    @Autowired
    MediaUploadRepository mediaUploadRepository;	
	
	@RequestMapping(value = "/upload",method = RequestMethod.POST, produces = "text/html")
	public String create(@Valid MediaUploadDao mediaUpload, BindingResult bindingResult, Model uiModel,
	       @RequestParam("content") CommonsMultipartFile content,
	       HttpServletRequest httpServletRequest) {
	   String filestr = "";
	   byte[] file = content.getBytes();
	   filestr = new String(file);
	      mediaUpload.setContentType(content.getContentType());

	   uiModel.asMap().clear();
	   mediaUploadRepository.save(mediaUpload);
	   
	   // import the file here....
	   importManager.importTransactions(mediaUpload.getImportClient().intValue(), filestr);
	   
	   return "redirect:/ruleassignment";
	   
	   //return "redirect:/import/" + encodeUrlPathSegment(mediaUpload.getId().toString(),
	    //  httpServletRequest);
	}
	
	@ModelAttribute("clientkeys")
	protected List<ColumnValueDao> referenceData(HttpServletRequest request, Object command,
			Errors errors) throws Exception {
		List<ColumnValueDao> reference = cvManager.getColumnValueList(ImportManager.ClientKeyLkup);
		
		// return model
		return reference;
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
	

	@RequestMapping(params = "form", produces = "text/html")
    public String createForm(Model uiModel) {
        populateEditForm(uiModel, new MediaUploadDao());
        return "import/create";
    }

	@RequestMapping(value = "/{id}", produces = "text/html")
    public String show(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("mediauploaddao", mediaUploadRepository.findOne(id));
        uiModel.addAttribute("itemId", id);
        return "import/show";
    }

	@RequestMapping(produces = "text/html")
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, @RequestParam(value = "sortFieldName", required = false) String sortFieldName, @RequestParam(value = "sortOrder", required = false) String sortOrder, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("mediauploaddaos", mediaUploadRepository.findAll(new org.springframework.data.domain.PageRequest(firstResult / sizeNo, sizeNo)).getContent());
            float nrOfPages = (float) mediaUploadRepository.count() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("mediauploaddaos", mediaUploadRepository.findAll());
        }
        return "import/list";
    }

	@RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(@Valid MediaUploadDao mediaUploadDao, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, mediaUploadDao);
            return "import/update";
        }
        uiModel.asMap().clear();
        mediaUploadRepository.save(mediaUploadDao);
        return "redirect:/import/" + encodeUrlPathSegment(mediaUploadDao.getId().toString(), httpServletRequest);
    }

	@RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        populateEditForm(uiModel, mediaUploadRepository.findOne(id));
        return "import/update";
    }

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        MediaUploadDao mediaUploadDao = mediaUploadRepository.findOne(id);
        mediaUploadRepository.delete(mediaUploadDao);
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/import";
    }

	void populateEditForm(Model uiModel, MediaUploadDao mediaUploadDao) {
        uiModel.addAttribute("mediaUploadDao", mediaUploadDao);
    }
}
