package meg.bank.bus.report.pdf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

public abstract class PdfReport extends AbstractPdfView {
	// imgbase
    @Value("${document.image.weblinkbase}")
	public String imgbase;
	public PdfReport() {
		super();
	}

	protected void addChartToTable(PdfPTable table, PdfPTable chart, int newlines) {
		PdfPCell chartcell = new PdfPCell();
		chartcell.setBorderWidth(0f);
		for (int i=1;i<=newlines;i++) {
			chartcell.addElement(new Chunk(Chunk.NEWLINE));
		}
		chartcell.addElement(chart);
		table.addCell(chartcell);
	}

	protected PdfPTable getChartTable(int colcount) {
		PdfPTable charttable = new PdfPTable(colcount);
		charttable.setWidthPercentage(100f);
		charttable.setExtendLastRow(false);
		charttable.setSpacingBefore(20f);
		return charttable;
	}

	protected PdfPTable getContainerTable(float[] colsWidth) {
		PdfPTable table = new PdfPTable(colsWidth);
		table.setWidthPercentage(100f);
		table.getDefaultCell().setBorderWidth(0f);
		table.setSpacingBefore(0f);
		table.setSpacingAfter(0f);
		return table;
	}

	protected void addCellToTable(PdfPTable table, Font font, String value,
			boolean nowrap) {
				Phrase p = new Phrase(value, font);
				PdfPCell cell = new PdfPCell(p);
				cell.setNoWrap(true);
			
				table.addCell(p);
			}

	protected void addBlankLines(int count, Document document)
			throws DocumentException {
				for (int i = 1; i < count; i++) {
					document.add(Chunk.NEWLINE);
				}
			}

}