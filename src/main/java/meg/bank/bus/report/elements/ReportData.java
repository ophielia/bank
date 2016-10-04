package meg.bank.bus.report.elements;

import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBException;



public interface ReportData {

	public void crunchNumbers() ;
	
	public String getXslTransformFilename();
	
	public String getJspViewname();
	
	public List<ReportElement> getElements();
	
	public List<ReportLabel> getReportLabels();
	
	public HashMap<String,Object> resultsAsHashMap();
	
	public String resultsAsXml() throws JAXBException;
	}