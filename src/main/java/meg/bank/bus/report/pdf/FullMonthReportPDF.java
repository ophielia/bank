package meg.bank.bus.report.pdf;

import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import meg.bank.bus.report.ReportCriteria;
import meg.bank.bus.report.ReportElements;
import meg.bank.bus.report.utils.ChartData;
import meg.bank.bus.report.utils.ChartRow;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class FullMonthReportPDF extends PdfReport {

	@Override
	protected void buildPdfDocument(Map model, Document document,
			PdfWriter writer, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// get model
		Map<String, Object> mymodel = (Map<String, Object>) model.get("results");
		
		// get image base
		ReportCriteria crit = (ReportCriteria) mymodel.get("rCriteria");
		String contextbase = crit.getContextPath() + "/"; 
		// page formatting
		document.setPageSize(PageSize.A4);
		document.setMargins(20, 20, 20, 20);

		// cell formatting
		Font font = FontFactory.getFont(FontFactory.HELVETICA, 7);
		Font tblheaderfont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
		Font rptheaderfont = FontFactory
				.getFont(FontFactory.HELVETICA_BOLD, 24);

		// Add Title
		Paragraph paragraph = new Paragraph();
		Phrase p = new Phrase((String) mymodel.get("title"), rptheaderfont);
		paragraph.setAlignment(Element.ALIGN_LEFT);
		paragraph.add(p);
		document.add(paragraph);

		// blank lines
		// add a couple of blank lines
		addBlankLines(3, document);

		// Summary Table
		float[] colsWidth = { 1.75f, 1f };
		PdfPTable summarytable = getContainerTable(colsWidth);

		// add Summary Graph
		String imageUrl = (String) mymodel.get("summaryimg");
		Image summarygraph = Image.getInstance(new URL(contextbase + imageUrl));
		summarytable.addCell(summarygraph);

		// add Summary chart,
		ChartData summarydata = (ChartData) mymodel.get("summary");
		ChartRow headers = summarydata.getHeaders();
		List<ChartRow> yearcatrows = summarydata.getRows();

		// add Summary chart,
		PdfPTable summarychart = getChartTable(headers.getColumnCount());
		List<String> colheaders = headers.getColumns();
		for (String header : colheaders) {
			addCellToTable(summarychart, tblheaderfont, header, true);
		}

		for (ChartRow row : yearcatrows) {
			for (String column : (List<String>) row.getColumns()) {
				addCellToTable(summarychart, font, column, false);
			}
		}

		// now add chart to table
		addChartToTable(summarytable, summarychart, 1);
		// and table to document
		document.add(summarytable);

		// add a couple of blank lines
		addBlankLines(2, document);

		// Targets Table
		PdfPTable targetstable = getContainerTable(colsWidth);
		ReportElements targetsreport = (ReportElements) mymodel.get("targets");

		// add Targets Graph
		imageUrl = targetsreport.getUrl();
		summarygraph = Image.getInstance(new URL(contextbase + imageUrl));
		targetstable.addCell(summarygraph);

		// add Targets chart,
		ChartData targetsdata = targetsreport.getChartData();
		headers = targetsdata.getHeaders();
		yearcatrows = targetsdata.getRows();

		// add Targets chart,
		PdfPTable targetschart = getChartTable(headers.getColumnCount());
		colheaders = headers.getColumns();
		for (String header : colheaders) {
			addCellToTable(targetschart, tblheaderfont, header, true);
		}

		for (ChartRow row : yearcatrows) {
			for (String column : (List<String>) row.getColumns()) {
				addCellToTable(targetschart, font, column, false);
			}
		}

		// now add chart to table
		addChartToTable(targetstable, targetschart, 1);
		// and table to document
		document.add(targetstable);

		
		// add Month Comparison Graph
		ReportElements elements = (ReportElements) mymodel.get("yeartodate");
		imageUrl = elements.getUrl();
		Image monthgraph = Image.getInstance(new URL(contextbase + imageUrl));
		monthgraph.scaleAbsoluteHeight(375);
		document.add(monthgraph);		
		// Add Monthly Comparison Table
		// get data
		ChartData data = (ChartData) elements.getChartData();
		headers = data.getHeaders();
		List<ChartRow> mthcomparerows = data.getRows();

		// add Month Comparison chart,
		PdfPTable mthcomparechart = getChartTable(headers.getColumnCount());
		colheaders = headers.getColumns();
		for (String header : colheaders) {
			addCellToTable(mthcomparechart, tblheaderfont, header, true);
		}

		for (ChartRow row : mthcomparerows) {
			for (String column : (List<String>) row.getColumns()) {
				addCellToTable(mthcomparechart, font, column, false);
			}
		}
		// and table to document
		document.add(mthcomparechart);

		// Add list of expenses
		// add Targets chart,
		data = (ChartData) mymodel.get("allexpenses");
		headers = data.getHeaders();
		List<ChartRow> rows = data.getRows();

		PdfPTable allexpchart = getChartTable(headers.getColumnCount());
		allexpchart.setWidthPercentage(100f);
		allexpchart.setWidths(new float[] { 1, 1, 1, 4, 1 });
		colheaders = headers.getColumns();
		for (String header : colheaders) {
			addCellToTable(allexpchart, tblheaderfont, header, true);
		}

		for (ChartRow row : rows) {
			for (String column : (List<String>) row.getColumns()) {
				addCellToTable(allexpchart, font, column, false);
			}
		}
		// and table to document
		document.add(allexpchart);
	}

}
