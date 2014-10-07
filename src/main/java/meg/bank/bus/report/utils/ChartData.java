package meg.bank.bus.report.utils;

import java.util.ArrayList;
import java.util.List;


public class ChartData {
	
	private ChartRow headers;
	private List rows;
	
	
	public ChartRow getHeaders() {
		return headers;
	}
	
	public List<ChartRow> getRows() {
		return rows;
	}
	
	public void setNextHeader() {
		
	}
	
	public void addRow(ChartRow row) {
		if (rows == null) {
			rows = new ArrayList();
		}
		if (row!=null) {
			rows.add(row);	
		}
	}

	public void setHeaders(ChartRow headers) {
		this.headers = headers;		
	}
	
}