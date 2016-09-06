package meg.bank.web;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import meg.bank.bus.CategoryService;
import meg.bank.bus.TargetService;
import meg.bank.bus.dao.CategoryDao;
import meg.bank.bus.dao.TargetDetailDao;
import meg.bank.bus.dao.TargetGroupDao;
import meg.bank.bus.repo.CategoryRepository;
import meg.bank.bus.repo.TargetDetailRepository;
import meg.bank.bus.repo.TargetGroupRepository;
import meg.bank.util.DateUtils;
import meg.bank.util.common.ColumnManagerService;
import meg.bank.util.common.db.ColumnValueDao;
import meg.bank.web.model.TargetModel;
import meg.bank.web.validation.TargetModelValidator;

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

@RequestMapping("/target")
@SessionAttributes("targetModel")
@Controller
public class TargetController {

	@Autowired
	CategoryRepository catRepo;

	@Autowired
	CategoryService categoryService;

	@Autowired
	TargetService targetService;

	@Autowired
	TargetModelValidator targetValidator;

	@Autowired
	TargetGroupRepository targetGrpRep;

	@Autowired
	TargetDetailRepository targetDetailRep;

	@Autowired
	ColumnManagerService cvManager;

	public final static class Action {
		public final static String AddDetail = "adddetail_action";
		public final static String PrepareEditDetail = "prepareedit_action";
		public final static String SaveEditDetail = "saveedit_action";
		public final static String DeleteDetail = "deletedetail_action";
	}

	protected void populateTargetList(Model uiModel) {
		List<TargetGroupDao> grouplist = targetService.getTargetGroupList();
		uiModel.addAttribute("targetList", grouplist);
		HashMap<Long, String> ttlkup = cvManager.getColumnDisplayByNumberHash(
				TargetService.TargetTypeLkup, false);
		uiModel.addAttribute("ttlkup", ttlkup);
	}

	protected void populateEditForm(Model uiModel, TargetModel model) {
		uiModel.addAttribute("targetModel", model);
	}

	@ModelAttribute("targettypelist")
	protected List<ColumnValueDao> referenceData(HttpServletRequest request,
			Object command, Errors errors) throws Exception {
		List<ColumnValueDao> reference = cvManager
				.getColumnValueList(TargetService.TargetTypeLkup);

		// return model
		return reference;
	}

	@ModelAttribute("categorylist")
	protected List<CategoryDao> referenceCategoryData(
			HttpServletRequest request, Object command, Errors errors)
			throws Exception {
		List<CategoryDao> list = categoryService.getCategories(false);

		// return model
		return list;
	}

	@ModelAttribute("yearlist")
	protected List<String> referenceYearlistData(HttpServletRequest request,
			Object command, Errors errors) throws Exception {
		Calendar oldcal = Calendar.getInstance();
		Calendar newcal = Calendar.getInstance();
		oldcal.add(Calendar.YEAR, -1);
		oldcal.set(Calendar.MONTH, Calendar.JANUARY);
		oldcal.set(Calendar.DAY_OF_MONTH, 1);
		newcal.set(newcal.get(Calendar.YEAR), Calendar.DECEMBER, 1);
		Date oldest = oldcal.getTime();
		Date newest = newcal.getTime();

		List<String> years = DateUtils.getYearsForSelect(oldest, newest);
		return years;

	}

	@ModelAttribute("monthlist")
	protected List<String> referenceMonthlistData(HttpServletRequest request,
			Object command, Errors errors) throws Exception {
		Calendar oldcal = Calendar.getInstance();
		Calendar newcal = Calendar.getInstance();
		oldcal.set(2007, Calendar.JANUARY, 1);
		newcal.set(newcal.get(Calendar.YEAR), Calendar.DECEMBER, 1);
		Date oldest = oldcal.getTime();
		Date newest = newcal.getTime();

		List<String> months = DateUtils.getMonthsForSelect(oldest, newest);
		return months;

	}

	@RequestMapping(produces = "text/html")
	public String showList(Model uiModel) {
		populateTargetList(uiModel);
		populateEditForm(uiModel, new TargetModel());
		return "target/list";
	}

	@RequestMapping(params = "action=setdefault", method = RequestMethod.GET, produces = "text/html")
	public String setDefaultGroup(@RequestParam("actionid") Long actionid,
			Model uiModel) {
		if (actionid != null) {
			targetService.updateDefaultTargetGroup(actionid);
		}

		return showList(uiModel);
	}

	@RequestMapping(params = "form=mon", method = RequestMethod.GET, produces = "text/html")
	public String createFormMonthly(Model uiModel) {
		Long newmonthlytargetid = targetService
				.copyTargetGroup(TargetService.TargetType.Month);

		TargetModel newmodel = targetService
				.loadTargetModel(newmonthlytargetid);
		populateEditForm(uiModel, newmodel);
		return "target/edit";
	}

	@RequestMapping(params = "form=yrl", method = RequestMethod.GET, produces = "text/html")
	public String createFormYearly(Model uiModel) {
		Long newyearlytargetid = targetService
				.copyTargetGroup(TargetService.TargetType.Year);

		TargetModel newmodel = targetService.loadTargetModel(newyearlytargetid);
		populateEditForm(uiModel, newmodel);
		return "target/edit";
	}

	@RequestMapping(value = "/{id}", produces = "text/html")
	public String show(@PathVariable("id") Long id, Model uiModel) {
		TargetModel model = targetService.loadTargetModel(id);
		uiModel.addAttribute("targetModel", model);
		uiModel.addAttribute("itemId", id);
		return "target/show";
	}

	@RequestMapping(value = "/edit/{id}", method = RequestMethod.PUT, produces = "text/html")
	public String edit(@PathVariable("id") Long id, TargetModel model,
			Model uiModel, BindingResult bindingResult,
			HttpServletRequest httpServletRequest) {
		// validate target detail entry
		targetValidator.validateGroup(model, bindingResult);
		// if errors, show
		if (bindingResult.hasErrors()) {
			populateEditForm(uiModel, model);
			return "target/edit";
		}
		// save any changes to group itself
		targetService.saveOrUpdateTargetGroup(model.getTargetgroup());

		directAction(model, uiModel, bindingResult, httpServletRequest);
		return "redirect:/target";
	}

	@RequestMapping(value = "/delete/{id}", method = RequestMethod.GET, produces = "text/html")
	public String showDeleteTargetGroup(@PathVariable("id") Long id,
			Model uiModel, HttpServletRequest httpServletRequest) {
		// pull target group to be edited
		TargetModel model = targetService.loadTargetModel(id);

		// place in model
		populateEditForm(uiModel, model);

		// check for default
		if (model.getIsdefault() != null && model.getIsdefault()) {
			uiModel.addAttribute("candelete", false);
		} else {
			uiModel.addAttribute("candelete", true);
		}
		// return edit view
		return "target/deletegroup";
	}

	@RequestMapping(value = "/delete/{id}", method = RequestMethod.POST, produces = "text/html")
	public String deleteTargetGroup(@PathVariable("id") Long id, Model uiModel,
			HttpServletRequest httpServletRequest) {
		// delete group
		targetService.deleteTargetGroup(id);
		// show list
		return showList(uiModel);
	}

	@RequestMapping(value = "/edit/{id}", params = "adddetail", method = RequestMethod.PUT, produces = "text/html")
	public String addTargetDetail(@PathVariable("id") Long id,
			TargetModel model, Model uiModel, BindingResult bindingResult,
			HttpServletRequest httpServletRequest) {
		// if action available, send to directAction
		if (model.getAction() != null) {
			return directAction(model, uiModel, bindingResult,
					httpServletRequest);
		}
		// if no action available, send directly to to SaveAddTargetDetail
		return saveAddTargetDetail(model, uiModel, bindingResult,
				httpServletRequest);
	}

	@RequestMapping(value = "/edit/{id}", params = "prepare_detail", method = RequestMethod.PUT, produces = "text/html")
	public String prepareEditTargetDetail(@PathVariable("id") Long id,
			TargetModel model, Model uiModel, BindingResult bindingResult,
			HttpServletRequest httpServletRequest) {
		// if action available, send to directAction
		if (model.getAction() != null) {
			return directAction(model, uiModel, bindingResult,
					httpServletRequest);
		}
		// if no action available, send directly to to SaveAddTargetDetail
		return prepareEditDetail(model, uiModel, bindingResult,
				httpServletRequest);
	}

	@RequestMapping(value = "/edit/{id}", params = "saveeditdetail", method = RequestMethod.PUT, produces = "text/html")
	public String saveEditTargetDetail(@PathVariable("id") Long id,
			TargetModel model, Model uiModel, BindingResult bindingResult,
			HttpServletRequest httpServletRequest) {
		// if action available, send to directAction
		if (model.getAction() != null) {
			return directAction(model, uiModel, bindingResult,
					httpServletRequest);
		}
		// if no action available, send directly to to SaveAddTargetDetail
		return saveEditTargetDetail(model, uiModel, bindingResult,
				httpServletRequest);
	}

	@RequestMapping(value = "/edit/{id}", params = "reseteditdetail", method = RequestMethod.PUT, produces = "text/html")
	public String resetEditTargetDetail(@PathVariable("id") Long id,
			TargetModel model, Model uiModel, BindingResult bindingResult,
			HttpServletRequest httpServletRequest) {
		// if action available, send to directAction
		if (model.getAction() != null) {
			return directAction(model, uiModel, bindingResult,
					httpServletRequest);
		}
		model = targetService.loadTargetModel(model.getTargetgroup().getId());
		populateEditForm(uiModel, model);
		model.setAction(null);
		model.setActionid(0);
		return "target/edit";
	}

	@RequestMapping(value = "/edit/{id}", params = "deletedet", method = RequestMethod.PUT, produces = "text/html")
	public String deleteTargetDetail(@PathVariable("id") Long id,
			TargetModel model, Model uiModel, BindingResult bindingResult,
			HttpServletRequest httpServletRequest) {
		// if action available, send to directAction
		if (model.getAction() != null) {
			return directAction(model, uiModel, bindingResult,
					httpServletRequest);
		}
		// if no action available, send directly to to SaveAddTargetDetail
		return deleteTargetDetail(model, uiModel, bindingResult,
				httpServletRequest);
	}

	@RequestMapping(params = "form", value = "/edit/{id}", method = RequestMethod.GET, produces = "text/html")
	public String createEditForm(@PathVariable("id") Long id, Model uiModel) {
		// pull target group to be edited
		TargetModel model = targetService.loadTargetModel(id);

		// place in model
		populateEditForm(uiModel, model);

		// return edit view
		return "target/edit";

	}

	private String directAction(TargetModel model, Model uiModel,
			BindingResult bindingResult, HttpServletRequest httpServletRequest) {
		if (model.getAction().equals(Action.AddDetail)) {
			// save add detail
			return saveAddTargetDetail(model, uiModel, bindingResult,
					httpServletRequest);
		} else if (model.getAction().equals(Action.DeleteDetail)) {
			return deleteTargetDetail(model, uiModel, bindingResult,
					httpServletRequest);
		} else if (model.getAction().equals(Action.PrepareEditDetail)) {
			// prepare edit detail
			return prepareEditDetail(model, uiModel, bindingResult,
					httpServletRequest);
		} else if (model.getAction().equals(Action.SaveEditDetail)) {
			// prepare edit detail
			return saveEditTargetDetail(model, uiModel, bindingResult,
					httpServletRequest);
		}
		// save global TargetModel edit
		return "target/edit";

	}

	private String saveAddTargetDetail(TargetModel model, Model uiModel,
			BindingResult bindingResult, HttpServletRequest httpServletRequest) {
		// validate target detail entry
		targetValidator.validate(model, bindingResult);
		// if no errors, put detail into TargetDetailDao object
		if (bindingResult.hasErrors()) {
			populateEditForm(uiModel, model);
			return "target/edit";
		}
		// create new target detail
		TargetDetailDao newdetail = new TargetDetailDao();
		newdetail.setCatid(model.getCatid());
		newdetail.setAmount(model.getAmount());
		// service call to add target detail
		targetService.addTargetDetailToGroup(newdetail, model.getTargetgroup());
		// save any changes to group itself
		targetService.saveOrUpdateTargetGroup(model.getTargetgroup());
		// populate edit form
		Long groupid = model.getTargetgroup().getId();
		model = targetService.loadTargetModel(groupid);
		model.setAction(null);
		model.setActionid(0);
		// return edit jsp
		populateEditForm(uiModel, model);
		return "target/edit";

	}

	private String saveEditTargetDetail(TargetModel model, Model uiModel,
			BindingResult bindingResult, HttpServletRequest httpServletRequest) {
		// validate target detail entry
		targetValidator.validate(model, bindingResult);
		// if no errors, put detail into TargetDetailDao object
		if (bindingResult.hasErrors()) {
			populateEditForm(uiModel, model);
			return "target/edit";
		}
		// save any changes to group itself
		targetService.saveOrUpdateTargetGroup(model.getTargetgroup());
		// get target detail
		Long detailid = model.getDetailid();
		TargetDetailDao editdetail = targetDetailRep.findOne(detailid);
		editdetail.setCatid(model.getCatid());
		editdetail.setAmount(model.getAmount());
		// service call to add target detail
		targetService.mergeTargetDetail(detailid, editdetail);

		// populate edit form
		Long groupid = model.getTargetgroup().getId();
		model = targetService.loadTargetModel(groupid);
		model.setAction(null);
		model.setActionid(0);
		// return edit jsp
		populateEditForm(uiModel, model);
		return "target/edit";

	}

	private String deleteTargetDetail(TargetModel model, Model uiModel,
			BindingResult bindingResult, HttpServletRequest httpServletRequest) {
		Long targetgroupid = model.getTargetgroup().getId();
		// validate group changes
		targetValidator.validateGroup(model, bindingResult);
		if (bindingResult.hasErrors()) {
			populateEditForm(uiModel, model);
			return "target/edit";
		}
		// save any group changes
		targetService.saveOrUpdateTargetGroup(model.getTargetgroup());

		// get id
		int deleteid = model.getActionid();
		// make service call
		targetService.deleteTargetDetail(new Long(deleteid));

		// reload model, and populate form
		model = targetService.loadTargetModel(targetgroupid);
		populateEditForm(uiModel, model);
		model.setAction(null);
		model.setActionid(0);
		// return
		return "target/edit";
	}

	private String prepareEditDetail(TargetModel model, Model uiModel,
			BindingResult bindingResult, HttpServletRequest httpServletRequest) {
		// validate group
		targetValidator.validateGroup(model, bindingResult);
		if (bindingResult.hasErrors()) {
			populateEditForm(uiModel, model);
			return "target/edit";
		}
		// save group
		targetService.saveOrUpdateTargetGroup(model.getTargetgroup());
		// retrieve selected detail
		int detailid = model.getActionid();
		TargetDetailDao detail = targetDetailRep.findOne(new Long(detailid));
		// populate id, amount and catid
		model.setCatid(detail.getCatid());
		model.setAmount(detail.getAmount());
		model.setDetailid(detail.getId());
		// populateEditModel
		populateEditForm(uiModel, model);
		// return
		return "target/edit";

	}
}
