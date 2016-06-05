package com.fathzer.soft.ajlib.utilities;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/** A date format that guarantees that its {@link #format(Date)} method returns a String that will return the right date
 *  when parsed with {@link #parse(String)} method.
 * <br>Note: This is not the case of SimpleDateFormat.getInstance(SimpleDateFormat.SHORT) 
 */
public class CoolDateFormatter {
	private DateFormat format;

	/** Constructor.
	 * @param locale The locale used to format the date.
	 */
	public CoolDateFormatter(Locale locale) {
		format = (SimpleDateFormat) SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT, locale);
		format.setLenient(false);
	}
	
	/** Formats a date.
	 * @param date The date to format
	 * @return a string. {@link #parse(String)} will return the original date when called with this string as argument.
	 */
	public String format(Date date) {
		String candidate = format.format(date);
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		eraseTime(calendar);
		try {
			if (!calendar.getTime().equals(format.parse(candidate))) {
				StringBuffer buf = new StringBuffer();
			    FieldPosition yearPosition = new FieldPosition(DateFormat.YEAR_FIELD);
			    buf = format.format(date.getTime(), buf, yearPosition);
			    buf.replace(yearPosition.getBeginIndex(), yearPosition.getEndIndex(), String.valueOf(calendar.get(Calendar.YEAR)));
			    candidate = buf.toString();
			}
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		return candidate;
	}
	
	/** Parse a string as a date.
	 * <br>Behavior is the same as the one of SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT);
	 * @param string The string to parse.
	 * @return The parsed date
	 * @throws ParseException if the string is not a date.
	 */
	public Date parse(String string) throws ParseException {
		return format.parse(string);
	}
	
	/** Sets all time related fields of a calendar to 0 (hour, minute, second, millisecond).
	 * @param date The calendar to change.
	 */
	public static void eraseTime(Calendar date) {
		date.set(Calendar.HOUR_OF_DAY, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);
	}
}
