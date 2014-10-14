package meg.bank.bus.report.pdf;

import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class YearlyTargetsReportPDF extends PdfReport {

	@Override
	protected void buildPdfDocument(Map model, Document document,
			PdfWriter writer, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// get model
		Map<String, Object> mymodel = (Map<String, Object>) model.get("model");
		// page formatting
		document.setPageSize(PageSize.A4);
		document.setMargins(20, 20, 20, 20);

		// cell formatting
		Font font = FontFactory.getFont(FontFactory.HELVETICA, 7);
		Font tblheaderfont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
		Font rptheaderfont = FontFactory
				.getFont(FontFactory.HELVETICA_BOLD, 24);
		Font subheaderfont = FontFactory
				.getFont(FontFactory.HELVETICA_BOLD, 14);

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
		Image summarygraph = Image.getInstance(new URL(imgbase + imageUrl));
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

		// the targets for year table
		// Target Table
		PdfPTable targettable = getContainerTable(new float[] { 2f, 1.25f });

		// add Target Graph
		imageUrl = (String) mymodel.get("targetsummaryimg");
		Image Targetgraph = Image.getInstance(new URL(imgbase + imageUrl));
		targettable.addCell(Targetgraph);

		// add Target Summary chart,
		ChartData targetsummdata = (ChartData) mymodel.get("targetsumm");
		headers = targetsummdata.getHeaders();
		List<ChartRow> chartrows = targetsummdata.getRows();

		// add Target Summary chart,
		PdfPTable targetsummchart = getChartTable(headers.getColumnCount());
		colheaders = headers.getColumns();
		for (String header : colheaders) {
			addCellToTable(targetsummchart, tblheaderfont, header, true);
		}

		for (ChartRow row : chartrows) {
			for (String column : (List<String>) row.getColumns()) {
				addCellToTable(targetsummchart, font, column, false);
			}
		}
		// now add chart to table
		addChartToTable(targettable, targetsummchart, 2);
		// and table to document
		document.add(targettable);

		// the progresss for year image
		// add Target Graph
		imageUrl = (String) mymodel.get("targetdetsummaryimg");
		Image progressgraph = Image.getInstance(new URL(imgbase + imageUrl));
		progressgraph.scaleAbsoluteHeight(375);
		document.add(progressgraph);

		// Monthly Comparison
		// Month Comparison Table

		// add Month Comparison Graph
		imageUrl = (String) mymodel.get("monthcompareimg");
		Image monthgraph = Image.getInstance(new URL(imgbase + imageUrl));
		monthgraph.scaleAbsoluteHeight(375);
		document.add(monthgraph);

		// get data
		ChartData data = (ChartData) mymodel.get("monthcomparison");
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

		// Yearly Comparison
		// Year Comparison Table
		document.newPage();
		// add Year Comparison Graph
		imageUrl = (String) mymodel.get("yearcompareimg");
		Image yeargraph = Image.getInstance(new URL(imgbase + imageUrl));
		yeargraph.scaleAbsoluteHeight(275);
		document.add(yeargraph);

		// get data
		data = (ChartData) mymodel.get("yearcomparison");
		headers = data.getHeaders();
		List<ChartRow> yearcomparerows = data.getRows();

		// add Year Comparison chart,
		PdfPTable yearcomparechart = getChartTable(headers.getColumnCount());
		colheaders = headers.getColumns();
		for (String header : colheaders) {
			addCellToTable(yearcomparechart, tblheaderfont, header, true);
		}

		for (ChartRow row : yearcomparerows) {
			for (String column : (List<String>) row.getColumns()) {
				addCellToTable(yearcomparechart, font, column, false);
			}
		}
		// and table to document
		document.add(yearcomparechart);

		// ADD THIS YEAR CATEGORY DATA
		document.newPage();
		// Add Title
		paragraph = new Paragraph();
		p = new Phrase("Category Breakouts", subheaderfont);
		paragraph.setAlignment(Element.ALIGN_LEFT);
		paragraph.add(p);
		document.add(paragraph);
		addBlankLines(2, document);

		List<ReportElements> yearcategories = (List<ReportElements>) mymodel
				.get("categoryyear");
		for (ReportElements yearcategory : yearcategories) {
			List<String> images = yearcategory.getUrls();
			PdfPTable categoryimages = getContainerTable(new float[] { 1.5f,
					1.5f });
			for (String imagename : images) {
				Image graphimg = Image
						.getInstance(new URL(imgbase + imagename));
				yeargraph.scaleAbsoluteHeight(375);
				categoryimages.addCell(graphimg);
			}
			// add images to document
			document.add(categoryimages);

			// now, do the chart
			// get data
			data = yearcategory.getChartData();
			headers = data.getHeaders();
			chartrows = data.getRows();

			// add Year Comparison chart,
			PdfPTable yearcatchart = getChartTable(headers.getColumnCount());
			colheaders = headers.getColumns();
			for (String header : colheaders) {
				addCellToTable(yearcatchart, tblheaderfont, header, true);
			}

			for (ChartRow row : chartrows) {
				for (String column : (List<String>) row.getColumns()) {
					addCellToTable(yearcatchart, font, column, false);
				}
			}
			// and table to document
			document.add(yearcatchart);
			addBlankLines(3, document);
		}

		// END THIS YEAR CATEGORY DATA

		// ADD YEARLY CATEGORY DATA
		document.newPage();
		// Add Title
		paragraph = new Paragraph();
		p = new Phrase("Yearly Development", subheaderfont);
		paragraph.setAlignment(Element.ALIGN_LEFT);
		paragraph.add(p);
		document.add(paragraph);
		addBlankLines(2, document);

		yearcategories = (List<ReportElements>) mymodel.get("categoryallyears");
		for (ReportElements yearcategory : yearcategories) {
			PdfPTable yeartable = getContainerTable(new float[] { 1f });
			imageUrl = yearcategory.getUrl();
			yeargraph = Image.getInstance(new URL(imgbase + imageUrl));
			// add images to document
			PdfPCell cell = new PdfPCell(yeargraph, true);
			cell.setFixedHeight(300);
			cell.setBorderWidth(0);
			yeartable.addCell(cell);

			// now, do the chart
			// get data
			data = yearcategory.getChartData();
			headers = data.getHeaders();
			chartrows = data.getRows();

			// add Year Comparison chart,
			PdfPTable yearcatchart = getChartTable(headers.getColumnCount());
			colheaders = headers.getColumns();
			for (String header : colheaders) {
				addCellToTable(yearcatchart, tblheaderfont, header, true);
			}

			for (ChartRow row : chartrows) {
				for (String column : (List<String>) row.getColumns()) {
					addCellToTable(yearcatchart, font, column, false);
				}
			}
			yeartable.addCell(yearcatchart);
			// and table to document
			document.add(yeartable);
			addBlankLines(3, document);
		}

		// END YEARLY CATEGORY DATA

		// ADD OTHER DETAIL
		document.newPage();
		// Add Title
		paragraph = new Paragraph();
		p = new Phrase("Other Detail", subheaderfont);
		paragraph.setAlignment(Element.ALIGN_LEFT);
		paragraph.add(p);
		document.add(paragraph);
		addBlankLines(2, document);

		ReportElements yearcategory = (ReportElements) mymodel
				.get("detailedother");
		PdfPTable yeartable = getContainerTable(new float[] { 1f });
		imageUrl = yearcategory.getUrl();
		yeargraph = Image.getInstance(new URL(imgbase + imageUrl));
		// add images to document
		PdfPCell cell = new PdfPCell(yeargraph, true);
		cell.setFixedHeight(300);
		cell.setBorderWidth(0);
		yeartable.addCell(cell);

		// now, do the chart
		// get data
		data = yearcategory.getChartData();
		headers = data.getHeaders();
		chartrows = data.getRows();

		// add Year Comparison chart,
		PdfPTable yearcatchart = getChartTable(headers.getColumnCount());
		colheaders = headers.getColumns();
		for (String header : colheaders) {
			addCellToTable(yearcatchart, tblheaderfont, header, true);
		}

		for (ChartRow row : chartrows) {
			for (String column : (List<String>) row.getColumns()) {
				addCellToTable(yearcatchart, font, column, false);
			}
		}
		yeartable.addCell(yearcatchart);
		// and table to document
		document.add(yeartable);
		addBlankLines(3, document);

		// END OTHER DETAIL

	}
}
