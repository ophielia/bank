package meg.bank.bus.dao;
import java.util.Iterator;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import meg.bank.bus.repo.QuickGroupDetailRepository;

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
public class QuickGroupDetailIntegrationTest {

    @Test
    public void testMarkerMethod() {
    }

	@Autowired
    QuickGroupDetailDataOnDemand dod;

	@Autowired
    QuickGroupDetailRepository quickGroupDetailRepository;

	@Test
    public void testCount() {
        Assert.assertNotNull("Data on demand for 'QuickGroupDetail' failed to initialize correctly", dod.getRandomQuickGroupDetail());
        long count = quickGroupDetailRepository.count();
        Assert.assertTrue("Counter for 'QuickGroupDetail' incorrectly reported there were no entries", count > 0);
    }

	@Test
    public void testFind() {
        QuickGroupDetail obj = dod.getRandomQuickGroupDetail();
        Assert.assertNotNull("Data on demand for 'QuickGroupDetail' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'QuickGroupDetail' failed to provide an identifier", id);
        obj = quickGroupDetailRepository.findOne(id);
        Assert.assertNotNull("Find method for 'QuickGroupDetail' illegally returned null for id '" + id + "'", obj);
        Assert.assertEquals("Find method for 'QuickGroupDetail' returned the incorrect identifier", id, obj.getId());
    }

	@Test
    public void testFindAll() {
        Assert.assertNotNull("Data on demand for 'QuickGroupDetail' failed to initialize correctly", dod.getRandomQuickGroupDetail());
        long count = quickGroupDetailRepository.count();
        Assert.assertTrue("Too expensive to perform a find all test for 'QuickGroupDetail', as there are " + count + " entries; set the findAllMaximum to exceed this value or set findAll=false on the integration test annotation to disable the test", count < 250);
        List<QuickGroupDetail> result = quickGroupDetailRepository.findAll();
        Assert.assertNotNull("Find all method for 'QuickGroupDetail' illegally returned null", result);
        Assert.assertTrue("Find all method for 'QuickGroupDetail' failed to return any data", result.size() > 0);
    }

	@Test
    public void testFindEntries() {
        Assert.assertNotNull("Data on demand for 'QuickGroupDetail' failed to initialize correctly", dod.getRandomQuickGroupDetail());
        long count = quickGroupDetailRepository.count();
        if (count > 20) count = 20;
        int firstResult = 0;
        int maxResults = (int) count;
        List<QuickGroupDetail> result = quickGroupDetailRepository.findAll(new org.springframework.data.domain.PageRequest(firstResult / maxResults, maxResults)).getContent();
        Assert.assertNotNull("Find entries method for 'QuickGroupDetail' illegally returned null", result);
        Assert.assertEquals("Find entries method for 'QuickGroupDetail' returned an incorrect number of entries", count, result.size());
    }

	@Test
    public void testFlush() {
        QuickGroupDetail obj = dod.getRandomQuickGroupDetail();
        Assert.assertNotNull("Data on demand for 'QuickGroupDetail' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'QuickGroupDetail' failed to provide an identifier", id);
        obj = quickGroupDetailRepository.findOne(id);
        Assert.assertNotNull("Find method for 'QuickGroupDetail' illegally returned null for id '" + id + "'", obj);
        boolean modified =  dod.modifyQuickGroupDetail(obj);
        Integer currentVersion = obj.getVersion();
        quickGroupDetailRepository.flush();
        Assert.assertTrue("Version for 'QuickGroupDetail' failed to increment on flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }

	@Test
    public void testSaveUpdate() {
        QuickGroupDetail obj = dod.getRandomQuickGroupDetail();
        Assert.assertNotNull("Data on demand for 'QuickGroupDetail' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'QuickGroupDetail' failed to provide an identifier", id);
        obj = quickGroupDetailRepository.findOne(id);
        boolean modified =  dod.modifyQuickGroupDetail(obj);
        Integer currentVersion = obj.getVersion();
        QuickGroupDetail merged = quickGroupDetailRepository.save(obj);
        quickGroupDetailRepository.flush();
        Assert.assertEquals("Identifier of merged object not the same as identifier of original object", merged.getId(), id);
        Assert.assertTrue("Version for 'QuickGroupDetail' failed to increment on merge and flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }

	@Test
    public void testSave() {
        Assert.assertNotNull("Data on demand for 'QuickGroupDetail' failed to initialize correctly", dod.getRandomQuickGroupDetail());
        QuickGroupDetail obj = dod.getNewTransientQuickGroupDetail(Integer.MAX_VALUE);
        Assert.assertNotNull("Data on demand for 'QuickGroupDetail' failed to provide a new transient entity", obj);
        Assert.assertNull("Expected 'QuickGroupDetail' identifier to be null", obj.getId());
        try {
            quickGroupDetailRepository.save(obj);
        } catch (final ConstraintViolationException e) {
            final StringBuilder msg = new StringBuilder();
            for (Iterator<ConstraintViolation<?>> iter = e.getConstraintViolations().iterator(); iter.hasNext();) {
                final ConstraintViolation<?> cv = iter.next();
                msg.append("[").append(cv.getRootBean().getClass().getName()).append(".").append(cv.getPropertyPath()).append(": ").append(cv.getMessage()).append(" (invalid value = ").append(cv.getInvalidValue()).append(")").append("]");
            }
            throw new IllegalStateException(msg.toString(), e);
        }
        quickGroupDetailRepository.flush();
        Assert.assertNotNull("Expected 'QuickGroupDetail' identifier to no longer be null", obj.getId());
    }

	@Test
    public void testDelete() {
        QuickGroupDetail obj = dod.getRandomQuickGroupDetail();
        Assert.assertNotNull("Data on demand for 'QuickGroupDetail' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'QuickGroupDetail' failed to provide an identifier", id);
        obj = quickGroupDetailRepository.findOne(id);
        quickGroupDetailRepository.delete(obj);
        quickGroupDetailRepository.flush();
        Assert.assertNull("Failed to remove 'QuickGroupDetail' with identifier '" + id + "'", quickGroupDetailRepository.findOne(id));
    }
}
