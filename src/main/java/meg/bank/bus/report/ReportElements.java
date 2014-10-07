package meg.bank.bus.report;

import java.util.ArrayList;
import java.util.List;

import meg.bank.bus.report.utils.ChartData;

public class ReportElements {
		private List summaries;

		private ChartData chartdata;
		
		private String url;

		private String name;

		private List<String> urls;
		
		public List getSummaries() {
			return summaries;
		}

		public void setSummaries(List summaries) {
			this.summaries = summaries;
		}

		public String getUrl() {
			if (urls!=null && urls.size()>0) {
				// returns only the first - legacy behavior
				return urls.get(0);
			}
			return null;
		}

		public void setUrl(String url) {
			if (urls==null) {
				urls = new ArrayList<String>();
			}
			if (url!=null) {
				urls.add(url);
			}
		}

		public void setName(String name) {
this.name = name;
			
		}

		public String getName() {
			return name;
		}

		public void setChartData(ChartData chartdata) {
			this.chartdata =chartdata;
		}

		public ChartData getChartData() {
			return this.chartdata;
		}

		public void addUrls(List<String> urls) {
			this.urls = urls;
		}
		
		public List<String> getUrls() {
			return this.urls;
		}
	}