package meg.bank.web;
import meg.bank.bus.dao.CategoryRuleDao;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/categoryrule")
@Controller
@RooWebScaffold(path = "categoryrule", formBackingObject = CategoryRuleDao.class)
public class CategoryRuleController {
}
