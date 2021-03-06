package meg.bank.bus.dao;
import java.util.Iterator;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import meg.bank.bus.repo.CatRelationshipRepository;

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
public class CatRelationshipDaoIntegrationTest {

    @Test
    public void testMarkerMethod() {
    }

	@Autowired
    CatRelationshipDaoDataOnDemand dod;

	@Autowired
    CatRelationshipRepository catRelationshipRepository;

	@Test
    public void testCount() {
        Assert.assertNotNull("Data on demand for 'CatRelationshipDao' failed to initialize correctly", dod.getRandomCatRelationshipDao());
        long count = catRelationshipRepository.count();
        Assert.assertTrue("Counter for 'CatRelationshipDao' incorrectly reported there were no entries", count > 0);
    }

	@Test
    public void testFind() {
        CatRelationshipDao obj = dod.getRandomCatRelationshipDao();
        Assert.assertNotNull("Data on demand for 'CatRelationshipDao' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'CatRelationshipDao' failed to provide an identifier", id);
        obj = catRelationshipRepository.findOne(id);
        Assert.assertNotNull("Find method for 'CatRelationshipDao' illegally returned null for id '" + id + "'", obj);
        Assert.assertEquals("Find method for 'CatRelationshipDao' returned the incorrect identifier", id, obj.getId());
    }

	@Test
    public void testFindAll() {
        Assert.assertNotNull("Data on demand for 'CatRelationshipDao' failed to initialize correctly", dod.getRandomCatRelationshipDao());
        long count = catRelationshipRepository.count();
        Assert.assertTrue("Too expensive to perform a find all test for 'CatRelationshipDao', as there are " + count + " entries; set the findAllMaximum to exceed this value or set findAll=false on the integration test annotation to disable the test", count < 250);
        List<CatRelationshipDao> result = catRelationshipRepository.findAll();
        Assert.assertNotNull("Find all method for 'CatRelationshipDao' illegally returned null", result);
        Assert.assertTrue("Find all method for 'CatRelationshipDao' failed to return any data", result.size() > 0);
    }

	@Test
    public void testFindEntries() {
        Assert.assertNotNull("Data on demand for 'CatRelationshipDao' failed to initialize correctly", dod.getRandomCatRelationshipDao());
        long count = catRelationshipRepository.count();
        if (count > 20) count = 20;
        int firstResult = 0;
        int maxResults = (int) count;
        List<CatRelationshipDao> result = catRelationshipRepository.findAll(new org.springframework.data.domain.PageRequest(firstResult / maxResults, maxResults)).getContent();
        Assert.assertNotNull("Find entries method for 'CatRelationshipDao' illegally returned null", result);
        Assert.assertEquals("Find entries method for 'CatRelationshipDao' returned an incorrect number of entries", count, result.size());
    }

	@Test
    public void testFlush() {
        CatRelationshipDao obj = dod.getRandomCatRelationshipDao();
        Assert.assertNotNull("Data on demand for 'CatRelationshipDao' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'CatRelationshipDao' failed to provide an identifier", id);
        obj = catRelationshipRepository.findOne(id);
        Assert.assertNotNull("Find method for 'CatRelationshipDao' illegally returned null for id '" + id + "'", obj);
        boolean modified =  dod.modifyCatRelationshipDao(obj);
        Integer currentVersion = obj.getVersion();
        catRelationshipRepository.flush();
        Assert.assertTrue("Version for 'CatRelationshipDao' failed to increment on flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }

	@Test
    public void testSaveUpdate() {
        CatRelationshipDao obj = dod.getRandomCatRelationshipDao();
        Assert.assertNotNull("Data on demand for 'CatRelationshipDao' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'CatRelationshipDao' failed to provide an identifier", id);
        obj = catRelationshipRepository.findOne(id);
        boolean modified =  dod.modifyCatRelationshipDao(obj);
        Integer currentVersion = obj.getVersion();
        CatRelationshipDao merged = catRelationshipRepository.save(obj);
        catRelationshipRepository.flush();
        Assert.assertEquals("Identifier of merged object not the same as identifier of original object", merged.getId(), id);
        Assert.assertTrue("Version for 'CatRelationshipDao' failed to increment on merge and flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }

	@Test
    public void testSave() {
        Assert.assertNotNull("Data on demand for 'CatRelationshipDao' failed to initialize correctly", dod.getRandomCatRelationshipDao());
        CatRelationshipDao obj = dod.getNewTransientCatRelationshipDao(Integer.MAX_VALUE);
        Assert.assertNotNull("Data on demand for 'CatRelationshipDao' failed to provide a new transient entity", obj);
        Assert.assertNull("Expected 'CatRelationshipDao' identifier to be null", obj.getId());
        try {
            catRelationshipRepository.save(obj);
        } catch (final ConstraintViolationException e) {
            final StringBuilder msg = new StringBuilder();
            for (Iterator<ConstraintViolation<?>> iter = e.getConstraintViolations().iterator(); iter.hasNext();) {
                final ConstraintViolation<?> cv = iter.next();
                msg.append("[").append(cv.getRootBean().getClass().getName()).append(".").append(cv.getPropertyPath()).append(": ").append(cv.getMessage()).append(" (invalid value = ").append(cv.getInvalidValue()).append(")").append("]");
            }
            throw new IllegalStateException(msg.toString(), e);
        }
        catRelationshipRepository.flush();
        Assert.assertNotNull("Expected 'CatRelationshipDao' identifier to no longer be null", obj.getId());
    }

	@Test
    public void testDelete() {
        CatRelationshipDao obj = dod.getRandomCatRelationshipDao();
        Assert.assertNotNull("Data on demand for 'CatRelationshipDao' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'CatRelationshipDao' failed to provide an identifier", id);
        obj = catRelationshipRepository.findOne(id);
        catRelationshipRepository.delete(obj);
        catRelationshipRepository.flush();
        Assert.assertNull("Failed to remove 'CatRelationshipDao' with identifier '" + id + "'", catRelationshipRepository.findOne(id));
    }
}
