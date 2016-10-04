package meg.bank.bus.dao;
import java.util.Iterator;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import meg.bank.bus.repo.TargetDetailRepository;

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
public class TargetDetailDaoIntegrationTest {

    @Test
    public void testMarkerMethod() {
    }

	@Autowired
    TargetDetailDaoDataOnDemand dod;

	@Autowired
    TargetDetailRepository targetDetailRepository;

	@Test
    public void testCount() {
        Assert.assertNotNull("Data on demand for 'TargetDetailDao' failed to initialize correctly", dod.getRandomTargetDetailDao());
        long count = targetDetailRepository.count();
        Assert.assertTrue("Counter for 'TargetDetailDao' incorrectly reported there were no entries", count > 0);
    }

	@Test
    public void testFind() {
        TargetDetailDao obj = dod.getRandomTargetDetailDao();
        Assert.assertNotNull("Data on demand for 'TargetDetailDao' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'TargetDetailDao' failed to provide an identifier", id);
        obj = targetDetailRepository.findOne(id);
        Assert.assertNotNull("Find method for 'TargetDetailDao' illegally returned null for id '" + id + "'", obj);
        Assert.assertEquals("Find method for 'TargetDetailDao' returned the incorrect identifier", id, obj.getId());
    }

	@Test
    public void testFindAll() {
        Assert.assertNotNull("Data on demand for 'TargetDetailDao' failed to initialize correctly", dod.getRandomTargetDetailDao());
        long count = targetDetailRepository.count();
        Assert.assertTrue("Too expensive to perform a find all test for 'TargetDetailDao', as there are " + count + " entries; set the findAllMaximum to exceed this value or set findAll=false on the integration test annotation to disable the test", count < 250);
        List<TargetDetailDao> result = targetDetailRepository.findAll();
        Assert.assertNotNull("Find all method for 'TargetDetailDao' illegally returned null", result);
        Assert.assertTrue("Find all method for 'TargetDetailDao' failed to return any data", result.size() > 0);
    }

	@Test
    public void testFindEntries() {
        Assert.assertNotNull("Data on demand for 'TargetDetailDao' failed to initialize correctly", dod.getRandomTargetDetailDao());
        long count = targetDetailRepository.count();
        if (count > 20) count = 20;
        int firstResult = 0;
        int maxResults = (int) count;
        List<TargetDetailDao> result = targetDetailRepository.findAll(new org.springframework.data.domain.PageRequest(firstResult / maxResults, maxResults)).getContent();
        Assert.assertNotNull("Find entries method for 'TargetDetailDao' illegally returned null", result);
        Assert.assertEquals("Find entries method for 'TargetDetailDao' returned an incorrect number of entries", count, result.size());
    }

	@Test
    public void testFlush() {
        TargetDetailDao obj = dod.getRandomTargetDetailDao();
        Assert.assertNotNull("Data on demand for 'TargetDetailDao' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'TargetDetailDao' failed to provide an identifier", id);
        obj = targetDetailRepository.findOne(id);
        Assert.assertNotNull("Find method for 'TargetDetailDao' illegally returned null for id '" + id + "'", obj);
        boolean modified =  dod.modifyTargetDetailDao(obj);
        Integer currentVersion = obj.getVersion();
        targetDetailRepository.flush();
        Assert.assertTrue("Version for 'TargetDetailDao' failed to increment on flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }

	@Test
    public void testSaveUpdate() {
        TargetDetailDao obj = dod.getRandomTargetDetailDao();
        Assert.assertNotNull("Data on demand for 'TargetDetailDao' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'TargetDetailDao' failed to provide an identifier", id);
        obj = targetDetailRepository.findOne(id);
        boolean modified =  dod.modifyTargetDetailDao(obj);
        Integer currentVersion = obj.getVersion();
        TargetDetailDao merged = targetDetailRepository.save(obj);
        targetDetailRepository.flush();
        Assert.assertEquals("Identifier of merged object not the same as identifier of original object", merged.getId(), id);
        Assert.assertTrue("Version for 'TargetDetailDao' failed to increment on merge and flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }

	@Test
    public void testSave() {
        Assert.assertNotNull("Data on demand for 'TargetDetailDao' failed to initialize correctly", dod.getRandomTargetDetailDao());
        TargetDetailDao obj = dod.getNewTransientTargetDetailDao(Integer.MAX_VALUE);
        Assert.assertNotNull("Data on demand for 'TargetDetailDao' failed to provide a new transient entity", obj);
        Assert.assertNull("Expected 'TargetDetailDao' identifier to be null", obj.getId());
        try {
            targetDetailRepository.save(obj);
        } catch (final ConstraintViolationException e) {
            final StringBuilder msg = new StringBuilder();
            for (Iterator<ConstraintViolation<?>> iter = e.getConstraintViolations().iterator(); iter.hasNext();) {
                final ConstraintViolation<?> cv = iter.next();
                msg.append("[").append(cv.getRootBean().getClass().getName()).append(".").append(cv.getPropertyPath()).append(": ").append(cv.getMessage()).append(" (invalid value = ").append(cv.getInvalidValue()).append(")").append("]");
            }
            throw new IllegalStateException(msg.toString(), e);
        }
        targetDetailRepository.flush();
        Assert.assertNotNull("Expected 'TargetDetailDao' identifier to no longer be null", obj.getId());
    }

	@Test
    public void testDelete() {
        TargetDetailDao obj = dod.getRandomTargetDetailDao();
        Assert.assertNotNull("Data on demand for 'TargetDetailDao' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'TargetDetailDao' failed to provide an identifier", id);
        obj = targetDetailRepository.findOne(id);
        targetDetailRepository.delete(obj);
        targetDetailRepository.flush();
        Assert.assertNull("Failed to remove 'TargetDetailDao' with identifier '" + id + "'", targetDetailRepository.findOne(id));
    }
}
