package meg.bank.bus.dao;
import java.util.Iterator;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import meg.bank.bus.repo.TargetGroupRepository;

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
public class TargetGroupDaoIntegrationTest {

    @Test
    public void testMarkerMethod() {
    }

	@Autowired
    TargetGroupDaoDataOnDemand dod;

	@Autowired
    TargetGroupRepository targetGroupRepository;

	@Test
    public void testCount() {
        Assert.assertNotNull("Data on demand for 'TargetGroupDao' failed to initialize correctly", dod.getRandomTargetGroupDao());
        long count = targetGroupRepository.count();
        Assert.assertTrue("Counter for 'TargetGroupDao' incorrectly reported there were no entries", count > 0);
    }

	@Test
    public void testFind() {
        TargetGroupDao obj = dod.getRandomTargetGroupDao();
        Assert.assertNotNull("Data on demand for 'TargetGroupDao' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'TargetGroupDao' failed to provide an identifier", id);
        obj = targetGroupRepository.findOne(id);
        Assert.assertNotNull("Find method for 'TargetGroupDao' illegally returned null for id '" + id + "'", obj);
        Assert.assertEquals("Find method for 'TargetGroupDao' returned the incorrect identifier", id, obj.getId());
    }

	@Test
    public void testFindAll() {
        Assert.assertNotNull("Data on demand for 'TargetGroupDao' failed to initialize correctly", dod.getRandomTargetGroupDao());
        long count = targetGroupRepository.count();
        Assert.assertTrue("Too expensive to perform a find all test for 'TargetGroupDao', as there are " + count + " entries; set the findAllMaximum to exceed this value or set findAll=false on the integration test annotation to disable the test", count < 250);
        List<TargetGroupDao> result = targetGroupRepository.findAll();
        Assert.assertNotNull("Find all method for 'TargetGroupDao' illegally returned null", result);
        Assert.assertTrue("Find all method for 'TargetGroupDao' failed to return any data", result.size() > 0);
    }

	@Test
    public void testFindEntries() {
        Assert.assertNotNull("Data on demand for 'TargetGroupDao' failed to initialize correctly", dod.getRandomTargetGroupDao());
        long count = targetGroupRepository.count();
        if (count > 20) count = 20;
        int firstResult = 0;
        int maxResults = (int) count;
        List<TargetGroupDao> result = targetGroupRepository.findAll(new org.springframework.data.domain.PageRequest(firstResult / maxResults, maxResults)).getContent();
        Assert.assertNotNull("Find entries method for 'TargetGroupDao' illegally returned null", result);
        Assert.assertEquals("Find entries method for 'TargetGroupDao' returned an incorrect number of entries", count, result.size());
    }

	@Test
    public void testFlush() {
        TargetGroupDao obj = dod.getRandomTargetGroupDao();
        Assert.assertNotNull("Data on demand for 'TargetGroupDao' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'TargetGroupDao' failed to provide an identifier", id);
        obj = targetGroupRepository.findOne(id);
        Assert.assertNotNull("Find method for 'TargetGroupDao' illegally returned null for id '" + id + "'", obj);
        boolean modified =  dod.modifyTargetGroupDao(obj);
        Integer currentVersion = obj.getVersion();
        targetGroupRepository.flush();
        Assert.assertTrue("Version for 'TargetGroupDao' failed to increment on flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }

	@Test
    public void testSaveUpdate() {
        TargetGroupDao obj = dod.getRandomTargetGroupDao();
        Assert.assertNotNull("Data on demand for 'TargetGroupDao' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'TargetGroupDao' failed to provide an identifier", id);
        obj = targetGroupRepository.findOne(id);
        boolean modified =  dod.modifyTargetGroupDao(obj);
        Integer currentVersion = obj.getVersion();
        TargetGroupDao merged = targetGroupRepository.save(obj);
        targetGroupRepository.flush();
        Assert.assertEquals("Identifier of merged object not the same as identifier of original object", merged.getId(), id);
        Assert.assertTrue("Version for 'TargetGroupDao' failed to increment on merge and flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }

	@Test
    public void testSave() {
        Assert.assertNotNull("Data on demand for 'TargetGroupDao' failed to initialize correctly", dod.getRandomTargetGroupDao());
        TargetGroupDao obj = dod.getNewTransientTargetGroupDao(Integer.MAX_VALUE);
        Assert.assertNotNull("Data on demand for 'TargetGroupDao' failed to provide a new transient entity", obj);
        Assert.assertNull("Expected 'TargetGroupDao' identifier to be null", obj.getId());
        try {
            targetGroupRepository.save(obj);
        } catch (final ConstraintViolationException e) {
            final StringBuilder msg = new StringBuilder();
            for (Iterator<ConstraintViolation<?>> iter = e.getConstraintViolations().iterator(); iter.hasNext();) {
                final ConstraintViolation<?> cv = iter.next();
                msg.append("[").append(cv.getRootBean().getClass().getName()).append(".").append(cv.getPropertyPath()).append(": ").append(cv.getMessage()).append(" (invalid value = ").append(cv.getInvalidValue()).append(")").append("]");
            }
            throw new IllegalStateException(msg.toString(), e);
        }
        targetGroupRepository.flush();
        Assert.assertNotNull("Expected 'TargetGroupDao' identifier to no longer be null", obj.getId());
    }

	@Test
    public void testDelete() {
        TargetGroupDao obj = dod.getRandomTargetGroupDao();
        Assert.assertNotNull("Data on demand for 'TargetGroupDao' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'TargetGroupDao' failed to provide an identifier", id);
        obj = targetGroupRepository.findOne(id);
        targetGroupRepository.delete(obj);
        targetGroupRepository.flush();
        Assert.assertNull("Failed to remove 'TargetGroupDao' with identifier '" + id + "'", targetGroupRepository.findOne(id));
    }
}
