package meg.bank.bus.newreport;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Date;

import javax.xml.bind.JAXBException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import meg.bank.bus.report.ReportCriteria;
import meg.bank.bus.report.ReportDataFactory;
import meg.bank.bus.report.elements.ReportData;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXException;

@ContextConfiguration(locations = { "classpath*:/spring/application-config*.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
@Configurable
public class ReportTest {

	@Autowired
	ReportDataFactory rdFact;


	
	@Test
	public void testBlowup() {
		ReportCriteria criteria = new ReportCriteria();
		// set criteria - for MonthlyTargetsReport
		criteria.setReportType(ReportDataFactory.ReportType.FullMonth);
		criteria.setMonth("09-2016");
		criteria.setExcludeNonExpense(true);
		ReportData results = rdFact.createReportData(criteria);
		Assert.assertNotNull(results);

	}

	@Test
	public void testBlowupXml() throws JAXBException, FileNotFoundException {
		ReportCriteria criteria = new ReportCriteria();
		criteria.setReportType(ReportDataFactory.ReportType.Yearly);
		criteria.setYear("2016");
		criteria.setExcludeNonExpense(true);
		criteria.setUseFullImageLink(true);
		ReportData results = rdFact.createReportData(criteria);

		String xml = results.resultsAsXml();
		Assert.assertNotNull(xml);

		try (PrintWriter out = new PrintWriter("C:/Temp/bank/yearly.xml")) {
			out.println(xml);
		}
	}

	@Test
	public void testMonthlyTargetsPDF() throws IOException, TransformerException,
			SAXException, JAXBException {

		//FopFactory fopFactory = FopFactory.newInstance(new File(
			//	"C:/Temp/bank/xmlfoconfig.xml"));
		FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
		TransformerFactory tFactory = TransformerFactory.newInstance();

		ReportCriteria criteria = new ReportCriteria();
		// set criteria - for MonthlyTargetsReport
		criteria.setReportType(ReportDataFactory.ReportType.MonthlyTarget);
		criteria.setMonth("09-2016");
		criteria.setComparetype(new Long(ReportCriteria.CompareType.CALYEAR));
		criteria.setExcludeNonExpense(true);
		criteria.setUseFullImageLink(true);
		ReportData results = rdFact.createReportData(criteria);

		String xml = results.resultsAsXml();
		Assert.assertNotNull(xml);
		// Step 2: Set up output stream.
		// Note: Using BufferedOutputStream for performance reasons (helpful
		// with FileOutputStreams).
		String name = "C:/Temp/bank/report";
		Date millis = new Date();
		name += millis.getTime() + ".pdf";
		OutputStream out = new BufferedOutputStream(new FileOutputStream(
				new File(name)));

		try {

			// Setup FOP
			Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);

			// Setup Transformer
			Source xsltSrc = new StreamSource(new File(
					"C:/Temp/bank/monthlytarget.xsl"));
			Transformer transformer = tFactory.newTransformer(xsltSrc);

			// Make sure the XSL transformation's result is piped through to FOP
			Result res = new SAXResult(fop.getDefaultHandler());
			// StreamResult res = new StreamResult(System.out);

			// Setup input
			StringReader reader = new StringReader(xml);
			Source src = new StreamSource(reader);

			// Start the transformation and rendering process
			transformer.transform(src, res);

		} catch (FOPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// Clean-up
			out.close();
		}

	}

	@Test
	public void testFullMonthPDF() throws IOException, TransformerException,
			SAXException, JAXBException {

		FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
		TransformerFactory tFactory = TransformerFactory.newInstance();

		ReportCriteria criteria = new ReportCriteria();
		// set criteria - for MonthlyTargetsReport
		criteria.setReportType(ReportDataFactory.ReportType.FullMonth);
		criteria.setMonth("09-2016");
		criteria.setComparetype(new Long(ReportCriteria.CompareType.CALYEAR));
		criteria.setExcludeNonExpense(true);
		criteria.setUseFullImageLink(true);
		ReportData results = rdFact.createReportData(criteria);

		String xml = results.resultsAsXml();
		Assert.assertNotNull(xml);
		// Step 2: Set up output stream.
		// Note: Using BufferedOutputStream for performance reasons (helpful
		// with FileOutputStreams).
		String name = "C:/Temp/bank/report";
		Date millis = new Date();
		name += millis.getTime() + ".pdf";
		OutputStream out = new BufferedOutputStream(new FileOutputStream(
				new File(name)));

		try {

			// Setup FOP
			Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);

			// Setup Transformer
			Source xsltSrc = new StreamSource(new File(
					"C:/Temp/bank/fullmonth.xsl"));
			Transformer transformer = tFactory.newTransformer(xsltSrc);

			// Make sure the XSL transformation's result is piped through to FOP
			Result res = new SAXResult(fop.getDefaultHandler());
			// StreamResult res = new StreamResult(System.out);

			// Setup input
			StringReader reader = new StringReader(xml);
			Source src = new StreamSource(reader);

			// Start the transformation and rendering process
			transformer.transform(src, res);

		} catch (FOPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// Clean-up
			out.close();
		}

	}

	
	
	@Test 
	public void testYearlyTargetsReport() throws JAXBException, TransformerException, IOException {

		FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
		TransformerFactory tFactory = TransformerFactory.newInstance();

		ReportCriteria criteria = new ReportCriteria();
		// set criteria - for MonthlyTargetsReport
		criteria.setReportType(ReportDataFactory.ReportType.YearlyTargetStatus);
		criteria.setYear("2016");
		criteria.setExcludeNonExpense(true);
		criteria.setUseFullImageLink(true);
		ReportData results = rdFact.createReportData(criteria);

		String xml = results.resultsAsXml();
		Assert.assertNotNull(xml);
		// Step 2: Set up output stream.
		// Note: Using BufferedOutputStream for performance reasons (helpful
		// with FileOutputStreams).
		String name = "C:/Temp/bank/report";
		Date millis = new Date();
		name += millis.getTime() + ".pdf";
		OutputStream out = new BufferedOutputStream(new FileOutputStream(
				new File(name)));

		try {

			// Setup FOP
			Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);

			// Setup Transformer
			Source xsltSrc = new StreamSource(new File(
					"C:/Temp/bank/yearlytarget.xsl"));
			Transformer transformer = tFactory.newTransformer(xsltSrc);

			// Make sure the XSL transformation's result is piped through to FOP
			Result res = new SAXResult(fop.getDefaultHandler());
			// StreamResult res = new StreamResult(System.out);

			// Setup input
			StringReader reader = new StringReader(xml);
			Source src = new StreamSource(reader);

			// Start the transformation and rendering process
			transformer.transform(src, res);

		} catch (FOPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// Clean-up
			out.close();
		}

		
		
		
		
	}
	
	
	@Test 
	public void testYearlyReport() throws JAXBException, TransformerException, IOException {

		FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
		TransformerFactory tFactory = TransformerFactory.newInstance();

		ReportCriteria criteria = new ReportCriteria();
		// set criteria - for MonthlyTargetsReport
		criteria.setReportType(ReportDataFactory.ReportType.Yearly);
		criteria.setYear("2016");
		criteria.setExcludeNonExpense(true);
		criteria.setUseFullImageLink(true);
		ReportData results = rdFact.createReportData(criteria);

		String xml = results.resultsAsXml();
		Assert.assertNotNull(xml);
		// Step 2: Set up output stream.
		// Note: Using BufferedOutputStream for performance reasons (helpful
		// with FileOutputStreams).
		String name = "C:/Temp/bank/report";
		Date millis = new Date();
		name += millis.getTime() + ".pdf";
		OutputStream out = new BufferedOutputStream(new FileOutputStream(
				new File(name)));

		try {

			// Setup FOP
			Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);

			// Setup Transformer
			Source xsltSrc = new StreamSource(new File(
					"C:/Temp/bank/yearlyreport.xsl"));
			Transformer transformer = tFactory.newTransformer(xsltSrc);

			// Make sure the XSL transformation's result is piped through to FOP
			Result res = new SAXResult(fop.getDefaultHandler());
			// StreamResult res = new StreamResult(System.out);

			// Setup input
			StringReader reader = new StringReader(xml);
			Source src = new StreamSource(reader);

			// Start the transformation and rendering process
			transformer.transform(src, res);

		} catch (FOPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// Clean-up
			out.close();
		}

		
		
		
		
	}	
	/**
	 * 
	 addTransaction(BankTADao) assignCategory(Long, Long)
	 * assignExpensesFromCategories(Long, List<ExpenseDao>)
	 * assignFromCategories(List<TransToCategory>) assignFromCategories(Long,
	 * List<BankTADao>) clearCategoryAssignment(Long) deleteBankTA(Long)
	 * deleteCategoryExpense(Long) deleteCategoryExpenseByTransaction(Long)
	 * deleteCategoryExpenses(List<Long>) distributeAmounts(Double, int)
	 * doesDuplicateExist(BankTADao) getAllBankTransactions()
	 * getAssignedCategoryList() getCategoryExpForTrans(Long)
	 * getExpenseTotal(ExpenseCriteria, String)
	 * getExpenseTotalByMonth(ExpenseCriteria, String)
	 * getExpenseTotalByYear(ExpenseCriteria, String)
	 * getExpenses(ExpenseCriteria) getFirstTransDate() getMostRecentTransDate()
	 * getNewCategoryExpense(Long) getNoCategoryExpenses() getTransaction(Long)
	 * saveTransaction(BankTADao, List<CategoryTADao>) updateCategoryExp
	 */
}