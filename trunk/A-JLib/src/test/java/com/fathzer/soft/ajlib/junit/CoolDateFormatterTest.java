package com.fathzer.soft.ajlib.junit;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.junit.Test;

import com.fathzer.soft.ajlib.utilities.CoolDateFormatter;

public class CoolDateFormatterTest {
	private static final CoolDateFormatter FRENCH_FORMAT = new CoolDateFormatter(Locale.FRANCE);

	@Test
	public void testNow() throws ParseException {
		Calendar date = GregorianCalendar.getInstance();
		CoolDateFormatter.eraseTime(date);
		Date instant = date.getTime();
		String f = FRENCH_FORMAT.format(instant);
		assertEquals(instant, FRENCH_FORMAT.parse(f));
	}
	
	@Test
	public void testLessThan80YearsBeforeNow() throws ParseException {
		Calendar date = GregorianCalendar.getInstance();
		date.add(Calendar.YEAR, -85);
		CoolDateFormatter.eraseTime(date);
		Date instant = date.getTime();
		String f = FRENCH_FORMAT.format(instant);
		assertEquals(instant, FRENCH_FORMAT.parse(f));
	}
	
	@Test
	public void testMoreThan20YearsAfterNow() throws ParseException {
		Calendar date = GregorianCalendar.getInstance();
		date.add(Calendar.YEAR, 25);
		CoolDateFormatter.eraseTime(date);
		Date instant = date.getTime();
		String f = FRENCH_FORMAT.format(instant);
		assertEquals(instant, FRENCH_FORMAT.parse(f));
	}
}
