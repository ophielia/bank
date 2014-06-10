package meg.bank.web;


import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import meg.bank.bus.CategoryService;
import meg.bank.bus.dao.CatRelationshipDao;
import meg.bank.bus.dao.CategoryDao;
import meg.bank.bus.dao.CategoryDaoDataOnDemand;
import meg.bank.bus.repo.CategoryRepository;
import meg.bank.web.validation.CategoryModelValidator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;










import junit.framework.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("classpath*:/META-INF/spring/applicationContext*.xml")
public class CategoryControllerTest {

	@Mock
	CategoryModelValidator cmv;
	
	@Mock
	CategoryService catService;
	
	@Mock
	CategoryRepository catRepo;

    @InjectMocks
    CategoryController controllerUnderTest;
    private MockMvc mockMvc;

    @Before
    public void setup() {
    	// this must be called for the @Mock annotations above to be processed
        // and for the mock service to be injected into the controller under
        // test.
        MockitoAnnotations.initMocks(this);

    	this.mockMvc = MockMvcBuilders.standaloneSetup(controllerUnderTest)
    			.build();
    }


    @Test
    public void getListOfCategories() throws Exception {

        this.mockMvc.perform(get("/categories")
        		.accept(MediaType.TEXT_HTML)
        		.header("content-type", "application/x-www-form-urlencoded"))
                .andExpect(status().isOk())
                .andExpect(view().name("categories/list"));

    }

    @Test
    public void getCreateForm() throws Exception {

    	when(catService.getCategoriesAsMap()).thenReturn(new HashMap<Long,CategoryDao>());
    	
        this.mockMvc.perform(get("/categories")
        		.accept(MediaType.TEXT_HTML)
        		.param("form","form")
        		.header("content-type", "application/x-www-form-urlencoded"))
                .andExpect(status().isOk())
                .andExpect(view().name("categories/create"));

 
    }

    @Test
    public void createCategory() throws Exception {
    	CategoryDaoDataOnDemand cdod = new CategoryDaoDataOnDemand();
    	CategoryDao cat = cdod.getNewTransientCategoryDao(0);
    	cat.setId(2222L);
    	
    	when(catService.getCategoriesAsMap()).thenReturn(new HashMap<Long,CategoryDao>());
    	when(catService.addCategory("form",	"form", new Boolean(true), new Boolean(true))).thenReturn(cat);
    	when(catService.changeCatMembership(0L,0L)).thenReturn(new CatRelationshipDao());
    	
        this.mockMvc.perform(post("/categories")
        		.accept(MediaType.TEXT_HTML)
        		.param("name","form")
        		.param("description","form")
        		.param("nonexpense","true")
        		.param("displayinlist","true")
        		.param("parentcatid","0")
        		.header("content-type", "application/x-www-form-urlencoded"))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/categories"));

    }
    
    @Test
    public void getEditForm() throws Exception {
    	 
    	
    	when(catService.getCategoriesAsMap()).thenReturn(new HashMap<Long,CategoryDao>());
    	when(catRepo.findOne(12345L)).thenReturn(new CategoryDao());
    	
        this.mockMvc.perform(get("/categories/edit/{id}",12345L)
        		.accept(MediaType.TEXT_HTML)
        		.param("form","form")
        		.header("content-type", "application/x-www-form-urlencoded"))
                .andExpect(status().isOk())
                .andExpect(view().name("categories/edit"));

 
    }

    @Test
    public void editCategory() throws Exception {
    	CategoryDaoDataOnDemand cdod = new CategoryDaoDataOnDemand();
    	CategoryDao cat = cdod.getNewTransientCategoryDao(0);
    	cat.setId(2222L);
    	
    	when(catService.getCategoriesAsMap()).thenReturn(new HashMap<Long,CategoryDao>());
    	when(catService.addCategory("form",	"form", new Boolean(true), new Boolean(true))).thenReturn(cat);
    	when(catService.changeCatMembership(0L,0L)).thenReturn(new CatRelationshipDao());
    	
        this.mockMvc.perform(put("/categories")
        		.accept(MediaType.TEXT_HTML)
        		.param("name","form")
        		.param("description","form")
        		.param("nonexpense","true")
        		.param("displayinlist","true")
        		.param("parentcatid","0")
        		.header("content-type", "application/x-www-form-urlencoded"))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/categories"));

    }    
}