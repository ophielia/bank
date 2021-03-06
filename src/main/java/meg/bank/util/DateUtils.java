package meg.bank.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtils {
	private static SimpleDateFormat dateformat = new SimpleDateFormat("MM-yyyy");

	public static List<String> getMonthsForSelect(Date oldest, Date newest) {
		List<String> months = new ArrayList<String>();

		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		start.setTime(oldest);
		end.setTime(newest);
		start.set(Calendar.DAY_OF_MONTH, 1);
		end.set(Calendar.DAY_OF_MONTH, 1);

		// cycle from end to start, adding as values to list
		dateformat = new SimpleDateFormat("MM-yyyy");
		while (end.after(start)) {
			String value = dateformat.format(end.getTime());
			months.add(value);
			end.add(Calendar.MONTH, -1);
			end.set(Calendar.DAY_OF_MONTH, 1);
		}
		// add first month
		String value = dateformat.format(end.getTime());
		months.add(value);
		return months;
	}

	public static Date getEndOfCalendarYear(Date start) {
		Calendar end = Calendar.getInstance();
		end.setTime(start);
		end.set(Calendar.DAY_OF_MONTH, Calendar.DECEMBER);
		end.set(Calendar.DAY_OF_MONTH, 31);
		return end.getTime();
		
	}
	public static List<String> getYearsForSelect(Date oldest, Date newest) {
		List<String> years = new ArrayList<String>();

		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		start.setTime(oldest);
		end.setTime(newest);
		start.set(Calendar.DAY_OF_MONTH, 1);
		end.set(Calendar.DAY_OF_MONTH, 1);

		// cycle from end to start, adding as values to list
		dateformat = new SimpleDateFormat("yyyy");
		while (end.after(start)) {
			String value = dateformat.format(end.getTime());
			years.add(value);
			end.add(Calendar.YEAR, -1);
			end.set(Calendar.DAY_OF_MONTH, 1);
		}
		// add first month
		String value = dateformat.format(end.getTime());
		years.add(value);
		return years;
	}
	
}
