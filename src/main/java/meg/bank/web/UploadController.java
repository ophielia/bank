package meg.bank.web;
import java.io.File;
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
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

@RequestMapping("/import")
@Controller
@RooWebScaffold(path = "import", formBackingObject = MediaUploadDao.class)
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
	
}
