package org.shaolin.bmdp.utils;

import java.util.Date;
import java.util.Calendar;

/**
 * Developer friendly date parsing.
 */
public class DateParser {
	private Calendar calendar = null;

	private static final String SLASH = "/";
	private static final String MINUS = "-";
	private static final String SPACE = " ";
	private static final String COLON = ":";
	private static final String DOT = ".";

	/**
	 * construct a DateParser
	 * 
	 * @param aDate
	 *            the date
	 */
	public DateParser(Date aDate) {
		calendar = Calendar.getInstance();
		calendar.setTime(aDate);
	}

	/**
	 * get the date string in MM/DD/YYYY format
	 * 
	 * @return the formatted string
	 */
	public String getDateString() {
		int year = getYear();
		if (year > 9999) {
			return "12/31/9999";
		}
		return format(getMonth(), 2) + SLASH + format(getDays(), 2) + SLASH
				+ format(year, 4);
	}

	/**
	 * get the time string in YYYY-MM-DD HH24:MI:SS format
	 * 
	 * @return the string format
	 */
	public String getTimeString() {
		int year = getYear();
		if (year > 9999) {
			return "9999-12-31 23:59:59";
		}
		return format(year, 4) + MINUS + format(getMonth(), 2) + MINUS
				+ format(getDays(), 2) + SPACE + format(getHours(), 2) + COLON
				+ format(getMinutes(), 2) + COLON + format(getSeconds(), 2);
	}

	/**
	 * get the timestamp string in YYYY-MM-DD HH24:MI:SS.FF3 format
	 * 
	 * @return the formatted string
	 */
	public String getTimestampString() {
		int year = getYear();
		if (year > 9999) {
			return "9999-12-31 23:59:59.999";
		}
		return format(year, 4) + MINUS + format(getMonth(), 2) + MINUS
				+ format(getDays(), 2) + SPACE + format(getHours(), 2) + COLON
				+ format(getMinutes(), 2) + COLON + format(getSeconds(), 2)
				+ DOT + format(getMilliSeconds(), 3);
	}

	/**
	 * get the timestamp string in YYYY-MM-DD-HH24.MI.SS.ZZZZZZ format
	 * 
	 * @return the formatted string
	 */
	public String getDB2TimestampString() {
		int year = getYear();
		if (year > 9999) {
			return "9999-12-31-23.59.59.999999";
		}
		return format(year, 4) + MINUS + format(getMonth(), 2) + MINUS
				+ format(getDays(), 2) + MINUS + format(getHours(), 2) + DOT
				+ format(getMinutes(), 2) + DOT + format(getSeconds(), 2) + DOT
				+ format(getNanoSeconds(), 6);
	}

	/**
	 * get the year
	 * 
	 * @return the year
	 */
	public int getYear() {
		return calendar.get(Calendar.YEAR);
	}

	/**
	 * get the month
	 * 
	 * @return the month
	 */
	public int getMonth() {
		return calendar.get(Calendar.MONTH) + 1;
	}

	/**
	 * get the days
	 * 
	 * @return the days
	 */
	public int getDays() {
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * get the hours
	 * 
	 * @return the hours
	 */
	public int getHours() {
		return calendar.get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * get the minutes
	 * 
	 * @return the minutes
	 */

	public int getMinutes() {
		return calendar.get(Calendar.MINUTE);
	}

	/**
	 * get the seconds
	 * 
	 * @return the seconds
	 */
	public int getSeconds() {
		return calendar.get(Calendar.SECOND);
	}

	/**
	 * get the milli seconds
	 * 
	 * @return the milli seconds
	 */
	public int getMilliSeconds() {
		return calendar.get(Calendar.MILLISECOND);
	}

	/**
	 * get the nano seconds
	 * 
	 * @return the nano seconds
	 */
	public int getNanoSeconds() {
		return getMilliSeconds() * 1000;
	}

	private String format(int value, int fixLength) {
		String str = String.valueOf(value);
		int len = fixLength - str.length();
		if (len == 0) {
			return str;
		}
		if (len == 1) {
			return "0" + str;
		}
		if (len == 2) {
			return "00" + str;
		}
		if (len == 3) {
			return "000" + str;
		}
		return str;
	}
}
