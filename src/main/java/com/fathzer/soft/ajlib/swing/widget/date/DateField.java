package com.fathzer.soft.ajlib.swing.widget.date;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import com.fathzer.soft.ajlib.swing.widget.TextWidget;
import com.fathzer.soft.ajlib.utilities.CoolDateFormatter;
import com.fathzer.soft.ajlib.utilities.NullUtils;

/** This class allows the user to just enter a day, or a day and a month, instead of a complete date (day, month, year).
 * It auto completes the typed date with the current month and year.
 * This field allows you to define what an empty field means. By default, an empty field means a null date, but, using the
 * setEmptyDate method, you can change this behavior.
 * This field has an inputVerifier in order to prevent entering something that is not a date in the field. By default, an empty
 * field is allowed, you can change that calling setIsEmptyNullDateValid. Keep in mind that if you called the setEmptyDate method
 * with a non null argument, the empty field will always be valid.
 * The up/down arrow keys increments/decrements the date.
 */
public class DateField extends TextWidget {
	private static final long serialVersionUID = 1L;
	public static final String DATE_PROPERTY = "date";
	public static final String CONTENT_VALID_PROPERTY = "contentValid";
		
	private CoolDateFormatter formatter;
	private Date date;
	private boolean valid;
	private Date emptyValue;
	private boolean isEmptyNullDateValid;
	
	/** Constructor.
	 * Creates a new Date widget. The date is set to today, the empty date is set to null.
	 * @see #setEmptyDate(Date)
	 */
	public DateField() {
		this(null);
	}
	
	/** Constructor.
	 * Creates a new Date widget. The date is set to today.
	 * @param emptyDate The date to be set if the field becomes empty
	 * @see #setEmptyDate(Date)
	 */
	public DateField(Date emptyDate) {
		super();
		this.setColumns(7);
		this.isEmptyNullDateValid = true;
		this.emptyValue = emptyDate;
		formatter = new CoolDateFormatter(getLocale());
		// Set the field to today's date
		Calendar calendar = Calendar.getInstance(getLocale());
		CoolDateFormatter.eraseTime(calendar);
		this.setDate(calendar.getTime());
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				// Nothing to do, all the job is done in keyPressed
			}

			@Override
			public void keyPressed(KeyEvent e) {
				int increment = 0;
				if (e.getKeyCode()==KeyEvent.VK_DOWN) {
					// Set the date to next day after the current date
					increment = -1;
				} else if (e.getKeyCode()==KeyEvent.VK_UP) {
					// Set the date to previous day before the current date
					increment = 1;						
				}
				if ((increment!=0) && (getDate()!=null)) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(getDate());
					calendar.add(Calendar.DATE, increment);
					setDate(calendar.getTime());
				}
			}
			
		});
		this.addPropertyChangeListener(TEXT_PROPERTY, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				updateDate();
			}
		});
		this.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				if (!e.isTemporary()) {
					DateField.super.setText(date==null?"":formatter.format(date));
				}
			}
			@Override
			public void focusGained(FocusEvent e) {
				// Nothing to do
			}
		});
	}
	
	@Override
	public void setLocale(Locale locale) {
		super.setLocale(locale);
		this.formatter = new CoolDateFormatter(locale);
		if (date!=null) {
			this.setText(formatter.format(date));
		}
	}

	/** Set the meaning of an empty field.
	 * @param date The date that a is equivalent to an empty field.
	 * By default, this date is null.
	 */
	public void setEmptyDate(Date date) {
		this.emptyValue = date;
		if (this.getText().trim().length()==0) {
			updateDate();
		}
	}
	
	/** Allow/Disallow this field to be empty (if it means a null date).
	 * <br>Keep in mind that a non null date for an empty field is always valid.
	 * @param valid true if null is a valid date.
	 * @see #setEmptyDate(Date)
	 */
	public void setIsEmptyNullDateIsValid(boolean valid) {
		this.isEmptyNullDateValid = valid;
		if (this.getText().trim().isEmpty()) {
			updateDate();
		}
	}
	
	private static Date parseRelativeDate(CharSequence text) {
		Date result = null;
		if (text.length()>1) {
			char c = text.charAt(0);
			if (c=='+' || c=='-') {
				int count = 1;
				while (text.length()>count && text.charAt(count)==c) {
					count++;
				}
				String num =text.toString().substring(count);
				if (count<=3 && num.matches("\\d+")) {
					int nb = Integer.parseInt(num);
					GregorianCalendar cal = new GregorianCalendar();
					cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), 0,0,0);
					cal.add(new int[]{Calendar.DATE, Calendar.MONTH, Calendar.YEAR}[count-1], c=='+'?nb:-nb);
					return cal.getTime();
				}
			}
		}
		return result;
	}

	@SuppressWarnings("deprecation")
	private void updateDate() {
		String text = this.getText().trim();
		if (text.isEmpty()) {
			internalSetDate(emptyValue, (emptyValue!=null) || isEmptyNullDateValid);
		} else {
			Date changed = parseRelativeDate(text);
			if (changed==null) {
				try {
					changed = formatter.parse(text);
					int year = changed.getYear()+1900;
					if (year<10) {
						// When the user enters a date with only one char for the year, it is interpreted as the full year (ie 9 -> year 9)
						// So, we have to add the right century to this year
						SimpleDateFormat simpleFormat = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT);
						Date formatterStartYear = simpleFormat.get2DigitYearStart();
						year += ((formatterStartYear.getYear()+1900)/100)*100;
						changed.setYear(year-1900);
						// If that date is not in the 100 year period of the formatter, add one century
						// Note: I compare the getTime() results, because, sometime, an exception is thrown that tells that instances are not of the same class
						if (changed.getTime()-formatterStartYear.getTime()<0) {
							changed.setYear(year-1800);
						}
					}
				} catch (ParseException e) {
					try {
						int day = Integer.parseInt(text);
						GregorianCalendar today = new GregorianCalendar();
						if ((day>0) && (day<=today.getActualMaximum(Calendar.DAY_OF_MONTH))) {
							changed = new GregorianCalendar(today.get(Calendar.YEAR),today.get(Calendar.MONTH),day).getTime();
						}
					} catch (NumberFormatException e1) {
						try {
							text = text+"/"+new GregorianCalendar().get(Calendar.YEAR);
							changed = formatter.parse(text+"/"+new GregorianCalendar().get(Calendar.YEAR));
						} catch (ParseException e2) {
						}
					}
				}
			}
			internalSetDate(changed, changed!=null);
		}
	}

	/** Get the widget current date.
	 * @return The date. If the value contained by the field is not valid, returns null.
	 */
	public Date getDate() {
		return this.date;
	}

	/** Set the widget date.
	 * The text field is updated.
	 * @param date The date to set.
	 */
	public void setDate(Date date) {
		super.setText(date==null?"":formatter.format(date));
		if (date==null) {
			date = emptyValue;
		}
		internalSetDate(date, date!=null || isEmptyNullDateValid);
	}

	@Override
	public void setText(String t) {
		super.setText(t);
		updateDate();
	}

	/** Set the date without changing the content of the TextField.
	 * This method does nothing if the date is equals to the current widget date.
	 * @param date
	 * @return true if the value was changed
	 */
	private boolean internalSetDate(Date date, boolean isValid) {
		boolean changed = ! NullUtils.areEquals(date, this.date);
		if (changed) {
			Date old = this.date;
			this.date = date;
			firePropertyChange(DATE_PROPERTY, old, date);
		}
		if (isValid!=this.valid) {
			this.valid = isValid;
			firePropertyChange(CONTENT_VALID_PROPERTY, !this.valid, this.valid);
		}
		return changed;
	}
	
	/** Gets the content validity.
	 * @return true if the content is valid, false if it is not.
	 */
	public boolean isContentValid() {
		return this.valid;
	}
}
