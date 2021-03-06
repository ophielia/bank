package meg.bank.bus.dao;
import java.util.Iterator;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import meg.bank.bus.repo.QuickGroupRepository;

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
public class QuickGroupIntegrationTest {

    @Test
    public void testMarkerMethod() {
    }

	@Autowired
    QuickGroupDataOnDemand dod;

	@Autowired
    QuickGroupRepository quickGroupRepository;

	@Test
    public void testCount() {
        Assert.assertNotNull("Data on demand for 'QuickGroup' failed to initialize correctly", dod.getRandomQuickGroup());
        long count = quickGroupRepository.count();
        Assert.assertTrue("Counter for 'QuickGroup' incorrectly reported there were no entries", count > 0);
    }

	@Test
    public void testFind() {
        QuickGroup obj = dod.getRandomQuickGroup();
        Assert.assertNotNull("Data on demand for 'QuickGroup' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'QuickGroup' failed to provide an identifier", id);
        obj = quickGroupRepository.findOne(id);
        Assert.assertNotNull("Find method for 'QuickGroup' illegally returned null for id '" + id + "'", obj);
        Assert.assertEquals("Find method for 'QuickGroup' returned the incorrect identifier", id, obj.getId());
    }

	@Test
    public void testFindAll() {
        Assert.assertNotNull("Data on demand for 'QuickGroup' failed to initialize correctly", dod.getRandomQuickGroup());
        long count = quickGroupRepository.count();
        Assert.assertTrue("Too expensive to perform a find all test for 'QuickGroup', as there are " + count + " entries; set the findAllMaximum to exceed this value or set findAll=false on the integration test annotation to disable the test", count < 250);
        List<QuickGroup> result = quickGroupRepository.findAll();
        Assert.assertNotNull("Find all method for 'QuickGroup' illegally returned null", result);
        Assert.assertTrue("Find all method for 'QuickGroup' failed to return any data", result.size() > 0);
    }

	@Test
    public void testFindEntries() {
        Assert.assertNotNull("Data on demand for 'QuickGroup' failed to initialize correctly", dod.getRandomQuickGroup());
        long count = quickGroupRepository.count();
        if (count > 20) count = 20;
        int firstResult = 0;
        int maxResults = (int) count;
        List<QuickGroup> result = quickGroupRepository.findAll(new org.springframework.data.domain.PageRequest(firstResult / maxResults, maxResults)).getContent();
        Assert.assertNotNull("Find entries method for 'QuickGroup' illegally returned null", result);
        Assert.assertEquals("Find entries method for 'QuickGroup' returned an incorrect number of entries", count, result.size());
    }

	@Test
    public void testFlush() {
        QuickGroup obj = dod.getRandomQuickGroup();
        Assert.assertNotNull("Data on demand for 'QuickGroup' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'QuickGroup' failed to provide an identifier", id);
        obj = quickGroupRepository.findOne(id);
        Assert.assertNotNull("Find method for 'QuickGroup' illegally returned null for id '" + id + "'", obj);
        boolean modified =  dod.modifyQuickGroup(obj);
        Integer currentVersion = obj.getVersion();
        quickGroupRepository.flush();
        Assert.assertTrue("Version for 'QuickGroup' failed to increment on flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }

	@Test
    public void testSaveUpdate() {
        QuickGroup obj = dod.getRandomQuickGroup();
        Assert.assertNotNull("Data on demand for 'QuickGroup' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'QuickGroup' failed to provide an identifier", id);
        obj = quickGroupRepository.findOne(id);
        boolean modified =  dod.modifyQuickGroup(obj);
        Integer currentVersion = obj.getVersion();
        QuickGroup merged = quickGroupRepository.save(obj);
        quickGroupRepository.flush();
        Assert.assertEquals("Identifier of merged object not the same as identifier of original object", merged.getId(), id);
        Assert.assertTrue("Version for 'QuickGroup' failed to increment on merge and flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }

	@Test
    public void testSave() {
        Assert.assertNotNull("Data on demand for 'QuickGroup' failed to initialize correctly", dod.getRandomQuickGroup());
        QuickGroup obj = dod.getNewTransientQuickGroup(Integer.MAX_VALUE);
        Assert.assertNotNull("Data on demand for 'QuickGroup' failed to provide a new transient entity", obj);
        Assert.assertNull("Expected 'QuickGroup' identifier to be null", obj.getId());
        try {
            quickGroupRepository.save(obj);
        } catch (final ConstraintViolationException e) {
            final StringBuilder msg = new StringBuilder();
            for (Iterator<ConstraintViolation<?>> iter = e.getConstraintViolations().iterator(); iter.hasNext();) {
                final ConstraintViolation<?> cv = iter.next();
                msg.append("[").append(cv.getRootBean().getClass().getName()).append(".").append(cv.getPropertyPath()).append(": ").append(cv.getMessage()).append(" (invalid value = ").append(cv.getInvalidValue()).append(")").append("]");
            }
            throw new IllegalStateException(msg.toString(), e);
        }
        quickGroupRepository.flush();
        Assert.assertNotNull("Expected 'QuickGroup' identifier to no longer be null", obj.getId());
    }

	@Test
    public void testDelete() {
        QuickGroup obj = dod.getRandomQuickGroup();
        Assert.assertNotNull("Data on demand for 'QuickGroup' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'QuickGroup' failed to provide an identifier", id);
        obj = quickGroupRepository.findOne(id);
        quickGroupRepository.delete(obj);
        quickGroupRepository.flush();
        Assert.assertNull("Failed to remove 'QuickGroup' with identifier '" + id + "'", quickGroupRepository.findOne(id));
    }
}
