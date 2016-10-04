package meg.bank.bus.dao;
import java.util.Iterator;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import meg.bank.bus.repo.CategoryTARepository;

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
public class CategoryTADaoIntegrationTest {

    @Test
    public void testMarkerMethod() {
    }

	@Autowired
    CategoryTADaoDataOnDemand dod;

	@Autowired
    CategoryTARepository catTARepository;

	@Test
    public void testCount() {
        Assert.assertNotNull("Data on demand for 'CategoryTADao' failed to initialize correctly", dod.getRandomCategoryTADao());
        long count = catTARepository.count();
        Assert.assertTrue("Counter for 'CategoryTADao' incorrectly reported there were no entries", count > 0);
    }

	@Test
    public void testFind() {
        CategoryTADao obj = dod.getRandomCategoryTADao();
        Assert.assertNotNull("Data on demand for 'CategoryTADao' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'CategoryTADao' failed to provide an identifier", id);
        obj = catTARepository.findOne(id);
        Assert.assertNotNull("Find method for 'CategoryTADao' illegally returned null for id '" + id + "'", obj);
        Assert.assertEquals("Find method for 'CategoryTADao' returned the incorrect identifier", id, obj.getId());
    }

	@Test
    public void testFindAll() {
        Assert.assertNotNull("Data on demand for 'CategoryTADao' failed to initialize correctly", dod.getRandomCategoryTADao());
        long count = catTARepository.count();
        Assert.assertTrue("Too expensive to perform a find all test for 'CategoryTADao', as there are " + count + " entries; set the findAllMaximum to exceed this value or set findAll=false on the integration test annotation to disable the test", count < 250);
        List<CategoryTADao> result = catTARepository.findAll();
        Assert.assertNotNull("Find all method for 'CategoryTADao' illegally returned null", result);
        Assert.assertTrue("Find all method for 'CategoryTADao' failed to return any data", result.size() > 0);
    }

	@Test
    public void testFindEntries() {
        Assert.assertNotNull("Data on demand for 'CategoryTADao' failed to initialize correctly", dod.getRandomCategoryTADao());
        long count = catTARepository.count();
        if (count > 20) count = 20;
        int firstResult = 0;
        int maxResults = (int) count;
        List<CategoryTADao> result = catTARepository.findAll(new org.springframework.data.domain.PageRequest(firstResult / maxResults, maxResults)).getContent();
        Assert.assertNotNull("Find entries method for 'CategoryTADao' illegally returned null", result);
        Assert.assertEquals("Find entries method for 'CategoryTADao' returned an incorrect number of entries", count, result.size());
    }

	@Test
    public void testFlush() {
        CategoryTADao obj = dod.getRandomCategoryTADao();
        Assert.assertNotNull("Data on demand for 'CategoryTADao' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'CategoryTADao' failed to provide an identifier", id);
        obj = catTARepository.findOne(id);
        Assert.assertNotNull("Find method for 'CategoryTADao' illegally returned null for id '" + id + "'", obj);
        boolean modified =  dod.modifyCategoryTADao(obj);
        Integer currentVersion = obj.getVersion();
        catTARepository.flush();
        Assert.assertTrue("Version for 'CategoryTADao' failed to increment on flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }

	@Test
    public void testSaveUpdate() {
        CategoryTADao obj = dod.getRandomCategoryTADao();
        Assert.assertNotNull("Data on demand for 'CategoryTADao' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'CategoryTADao' failed to provide an identifier", id);
        obj = catTARepository.findOne(id);
        boolean modified =  dod.modifyCategoryTADao(obj);
        Integer currentVersion = obj.getVersion();
        CategoryTADao merged = catTARepository.save(obj);
        catTARepository.flush();
        Assert.assertEquals("Identifier of merged object not the same as identifier of original object", merged.getId(), id);
        Assert.assertTrue("Version for 'CategoryTADao' failed to increment on merge and flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }

	@Test
    public void testSave() {
        Assert.assertNotNull("Data on demand for 'CategoryTADao' failed to initialize correctly", dod.getRandomCategoryTADao());
        CategoryTADao obj = dod.getNewTransientCategoryTADao(Integer.MAX_VALUE);
        Assert.assertNotNull("Data on demand for 'CategoryTADao' failed to provide a new transient entity", obj);
        Assert.assertNull("Expected 'CategoryTADao' identifier to be null", obj.getId());
        try {
            catTARepository.save(obj);
        } catch (final ConstraintViolationException e) {
            final StringBuilder msg = new StringBuilder();
            for (Iterator<ConstraintViolation<?>> iter = e.getConstraintViolations().iterator(); iter.hasNext();) {
                final ConstraintViolation<?> cv = iter.next();
                msg.append("[").append(cv.getRootBean().getClass().getName()).append(".").append(cv.getPropertyPath()).append(": ").append(cv.getMessage()).append(" (invalid value = ").append(cv.getInvalidValue()).append(")").append("]");
            }
            throw new IllegalStateException(msg.toString(), e);
        }
        catTARepository.flush();
        Assert.assertNotNull("Expected 'CategoryTADao' identifier to no longer be null", obj.getId());
    }

	@Test
    public void testDelete() {
        CategoryTADao obj = dod.getRandomCategoryTADao();
        Assert.assertNotNull("Data on demand for 'CategoryTADao' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'CategoryTADao' failed to provide an identifier", id);
        obj = catTARepository.findOne(id);
        catTARepository.delete(obj);
        catTARepository.flush();
        Assert.assertNull("Failed to remove 'CategoryTADao' with identifier '" + id + "'", catTARepository.findOne(id));
    }
}
