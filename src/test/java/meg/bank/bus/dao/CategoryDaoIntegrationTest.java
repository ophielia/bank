package meg.bank.bus.dao;
import java.util.Iterator;
import java.util.List;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import meg.bank.bus.repo.CategoryRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring/application-config*.xml"})
@Transactional
@Configurable
public class CategoryDaoIntegrationTest {

    @Test
    public void testMarkerMethod() {
    }

	@Autowired
    CategoryDaoDataOnDemand dod;

	@Autowired
    CategoryRepository categoryRepository;

	@Test
    public void testCount() {
        Assert.assertNotNull("Data on demand for 'CategoryDao' failed to initialize correctly", dod.getRandomCategoryDao());
        long count = categoryRepository.count();
        Assert.assertTrue("Counter for 'CategoryDao' incorrectly reported there were no entries", count > 0);
    }

	@Test
    public void testFind() {
        CategoryDao obj = dod.getRandomCategoryDao();
        Assert.assertNotNull("Data on demand for 'CategoryDao' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'CategoryDao' failed to provide an identifier", id);
        obj = categoryRepository.findOne(id);
        Assert.assertNotNull("Find method for 'CategoryDao' illegally returned null for id '" + id + "'", obj);
        Assert.assertEquals("Find method for 'CategoryDao' returned the incorrect identifier", id, obj.getId());
    }

	@Test
    public void testFindAll() {
        Assert.assertNotNull("Data on demand for 'CategoryDao' failed to initialize correctly", dod.getRandomCategoryDao());
        long count = categoryRepository.count();
        Assert.assertTrue("Too expensive to perform a find all test for 'CategoryDao', as there are " + count + " entries; set the findAllMaximum to exceed this value or set findAll=false on the integration test annotation to disable the test", count < 250);
        List<CategoryDao> result = categoryRepository.findAll();
        Assert.assertNotNull("Find all method for 'CategoryDao' illegally returned null", result);
        Assert.assertTrue("Find all method for 'CategoryDao' failed to return any data", result.size() > 0);
    }

	@Test
    public void testFindEntries() {
        Assert.assertNotNull("Data on demand for 'CategoryDao' failed to initialize correctly", dod.getRandomCategoryDao());
        long count = categoryRepository.count();
        if (count > 20) count = 20;
        int firstResult = 0;
        int maxResults = (int) count;
        List<CategoryDao> result = categoryRepository.findAll(new org.springframework.data.domain.PageRequest(firstResult / maxResults, maxResults)).getContent();
        Assert.assertNotNull("Find entries method for 'CategoryDao' illegally returned null", result);
        Assert.assertEquals("Find entries method for 'CategoryDao' returned an incorrect number of entries", count, result.size());
    }

	@Test
    public void testFlush() {
        CategoryDao obj = dod.getRandomCategoryDao();
        Assert.assertNotNull("Data on demand for 'CategoryDao' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'CategoryDao' failed to provide an identifier", id);
        obj = categoryRepository.findOne(id);
        Assert.assertNotNull("Find method for 'CategoryDao' illegally returned null for id '" + id + "'", obj);
        boolean modified =  dod.modifyCategoryDao(obj);
        Integer currentVersion = obj.getVersion();
        categoryRepository.flush();
        Assert.assertTrue("Version for 'CategoryDao' failed to increment on flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }

	@Test
    public void testSaveUpdate() {
        CategoryDao obj = dod.getRandomCategoryDao();
        Assert.assertNotNull("Data on demand for 'CategoryDao' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'CategoryDao' failed to provide an identifier", id);
        obj = categoryRepository.findOne(id);
        boolean modified =  dod.modifyCategoryDao(obj);
        Integer currentVersion = obj.getVersion();
        CategoryDao merged = categoryRepository.save(obj);
        categoryRepository.flush();
        Assert.assertEquals("Identifier of merged object not the same as identifier of original object", merged.getId(), id);
        Assert.assertTrue("Version for 'CategoryDao' failed to increment on merge and flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }

	@Test
    public void testSave() {
        Assert.assertNotNull("Data on demand for 'CategoryDao' failed to initialize correctly", dod.getRandomCategoryDao());
        CategoryDao obj = dod.getNewTransientCategoryDao(Integer.MAX_VALUE);
        Assert.assertNotNull("Data on demand for 'CategoryDao' failed to provide a new transient entity", obj);
        Assert.assertNull("Expected 'CategoryDao' identifier to be null", obj.getId());
        try {
            categoryRepository.save(obj);
        } catch (final ConstraintViolationException e) {
            final StringBuilder msg = new StringBuilder();
            for (Iterator<ConstraintViolation<?>> iter = e.getConstraintViolations().iterator(); iter.hasNext();) {
                final ConstraintViolation<?> cv = iter.next();
                msg.append("[").append(cv.getRootBean().getClass().getName()).append(".").append(cv.getPropertyPath()).append(": ").append(cv.getMessage()).append(" (invalid value = ").append(cv.getInvalidValue()).append(")").append("]");
            }
            throw new IllegalStateException(msg.toString(), e);
        }
        categoryRepository.flush();
        Assert.assertNotNull("Expected 'CategoryDao' identifier to no longer be null", obj.getId());
    }

	@Test
    public void testDelete() {
        CategoryDao obj = dod.getRandomCategoryDao();
        Assert.assertNotNull("Data on demand for 'CategoryDao' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'CategoryDao' failed to provide an identifier", id);
        obj = categoryRepository.findOne(id);
        categoryRepository.delete(obj);
        categoryRepository.flush();
        Assert.assertNull("Failed to remove 'CategoryDao' with identifier '" + id + "'", categoryRepository.findOne(id));
    }
}
