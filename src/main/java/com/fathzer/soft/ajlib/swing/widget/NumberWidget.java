package com.fathzer.soft.ajlib.swing.widget;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;

import com.fathzer.soft.ajlib.utilities.NullUtils;


/** A widget to enter a number.
 * <br>This widget automatically format the value it contains according to its local.
 * <br>You can restrict the valid values by setting minimum and maximum values.
 * <br>This bean defines two properties:<ul>
 * <li>VALUE_PROPERTY: a double, the value currently entered in the field.</li>
 * <li>CONTENT_VALID_PROPERTY: a boolean, true if the text currently entered in the field is a valid value, false if it is not.
 * <br>The CONTENT_VALID_PROPERTY is a read only property.</li>
 * </ul>
 */
public class NumberWidget extends TextWidget {
	private static final long serialVersionUID = 1L;
	private static final boolean DEBUG = false;
	private static final char NON_BREAKING_SPACE = 160;
	private static final char SPACE = ' ';
	
	/** Value property identifier. */ 
	public static final String VALUE_PROPERTY = "value";
	/** Content validity property identifier. */
	public static final String CONTENT_VALID_PROPERTY = "contentValid";
	
	private Number value;
	/** The format used to parse the number. */
	private DecimalFormat format;
	private boolean isEmptyAllowed;
	private Number minValue;
	private Number maxValue;
	private boolean valid;
	
	/** Constructor.
	 *  The local is set to default locale.
	 *  @see #NumberWidget(Locale)
	 */
	public NumberWidget() {
		this(Locale.getDefault());
	}
	
	/** Constructor.
	 * Empty field is not allowed.
	 * minValue is equals to Double.NEGATIVE_INFINITY, maxValue to Double.POSITIVE_INFINITY.
	 * The value is set to null.
	 * @param locale The locale to apply to the widget (use to format the amount typed).
	 */
	public NumberWidget(Locale locale) {
		super();
		this.isEmptyAllowed = false;
		this.minValue = Double.NEGATIVE_INFINITY;
		this.maxValue = Double.POSITIVE_INFINITY;
		format = buildFormat(locale);
		this.addPropertyChangeListener(TEXT_PROPERTY, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				updateValue();
			}
		});
		this.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				if (!e.isTemporary()) {
					refreshText(value);
				}
			}
			@Override
			public void focusGained(FocusEvent e) {
				// Nothing to do
			}
		});
	}
	
	/** Workaround of a weird java implementation (see http://bugs.sun.com/view_bug.do?bug_id=4510618).
	 * <br>In some locals, the grouping or decimal separators are a non breaking space ... not a single space.
	 * Users may be very surprised to see that in France, "1 000,00" is not a number.
	 * <br>This method changes non breaking spaces in the format symbol by simple spaces.
	 * <br>When the user type a non breaking space, it is automatically converted to a simple space before to be passed
	 * to the parseValue method. So, it is highly recommended that this method is applied on the format returned by buildFormat.
	 * @param format The format to patch
	 * @return the modified input format.
	 * @see #buildFormat(Locale)
	 * @see #parseValue(String)
	 */
	protected DecimalFormat patchJavaBug4510618 (DecimalFormat format) {
		DecimalFormatSymbols decimalFormatSymbols = format.getDecimalFormatSymbols();
		if (decimalFormatSymbols.getGroupingSeparator()==NON_BREAKING_SPACE) {
			decimalFormatSymbols.setGroupingSeparator(SPACE);
		}
		if (decimalFormatSymbols.getDecimalSeparator()==NON_BREAKING_SPACE) {
			decimalFormatSymbols.setDecimalSeparator(SPACE);
		}
		format.setDecimalFormatSymbols(decimalFormatSymbols);
		format.setPositiveSuffix(format.getPositiveSuffix().replace(NON_BREAKING_SPACE, SPACE));
		format.setNegativeSuffix(format.getNegativeSuffix().replace(NON_BREAKING_SPACE, SPACE));
		format.setPositivePrefix(format.getPositivePrefix().replace(NON_BREAKING_SPACE, SPACE));
		format.setNegativePrefix(format.getNegativePrefix().replace(NON_BREAKING_SPACE, SPACE));
		return format;
	}
	
	protected DecimalFormat buildFormat(Locale locale) {
		return patchJavaBug4510618((DecimalFormat) NumberFormat.getNumberInstance(locale));
	}
	
	protected void setFormat (DecimalFormat format) {
		this.format = format;
	}
	
	protected DecimalFormat getFormat() {
		return this.format;
	}
	
	protected void refreshText() {
		refreshText(value);
	}
	
	private void refreshText(Number value) {
		this.setText(value==null?"":format.format(value));
	}
	
	private void updateValue() {
		boolean oldValid = this.valid;
		Number changed = null;
		String text = this.getText().trim();
		if (text.length()==0) {
			this.valid = isEmptyAllowed;
		} else {
			changed = parseValue(text.replace(NON_BREAKING_SPACE, SPACE));
			this.valid = (changed!=null) && (changed.doubleValue()>=minValue.doubleValue()) && (changed.doubleValue()<=maxValue.doubleValue());
			if (DEBUG) {
				System.out.println (text+"->"+changed+" => this.valid:"+oldValid+"->"+valid);
			}
		}
		internalSetValue(changed==null?null:changed.doubleValue());
		if (this.valid!=oldValid) {
			firePropertyChange(CONTENT_VALID_PROPERTY, oldValid, this.valid);
		}
	}

	/** Parses the text contained in the field.
	 * <br>This method is called each time the field is modified. 
	 * @param text The trimmed text contained in the field 
	 * @return The value of the text or null if the text can't be parsed as a number.
	 */
	protected Number parseValue(String text) {
		return safeParse(format, text);
	}

	/** Parses a text with a format and ensures the text has no extra characters after valid data.
	 * <br>We have to be cautious with parsing. If the text begins with a valid number but is followed by garbage,
	 * format.parse(String) will succeed !!! This method uses format.parse(String, ParsePosition) in order to detect
	 * garbage placed after a valid number.
	 * @param format The format to use for parsing
	 * @param text The text to be parsed
	 * @return A number, or null if the text is not valid according to the format
	 */
	public static Number safeParse(NumberFormat format, String text) {
		ParsePosition pos = new ParsePosition(0);
		Number candidate = format.parse(text, pos);
		if (pos.getIndex()==text.length()) {
			return candidate;
		} else {
			return null;
		}
	}
	
	/** Determines whether empty text (or blank) are considered as valid or not. 
	 * @return true if blank field is valid.
	 */
	public boolean isEmptyAllowed() {
		return isEmptyAllowed;
	}

	/** Sets the validity of blank text.
	 * @param isEmptyAllowed true if blank text are allowed, false if not.
	 * The value associated with a blank field is always null.
	 */
	public void setEmptyAllowed(boolean isEmptyAllowed) {
		this.isEmptyAllowed = isEmptyAllowed;
		if (this.getText().trim().length()==0) {
			updateValue();
		}
	}

	/** Gets the minimum value allowed in the field.
	 * @return a double (Double.NEGATIVE_INFINITY if there is no limit)
	 */
	public Double getMinValue() {
		return minValue.doubleValue();
	}

	/** Sets the minimum value allowed in the field.
	 * @param minValue a double (Double.NEGATIVE_INFINITY if there is no limit)
	 */
	public void setMinValue(Double minValue) {
		this.minValue = minValue;
	}

	/** Gets the maximum value allowed in the field.
	 * @return a double (Double.POSITIVE_INFINITY if there is no limit)
	 */
	public Double getMaxValue() {
		return maxValue.doubleValue();
	}

	/** Sets the maximum value allowed in the field.
	 * @param maxValue a double (Double.POSITIVE_INFINITY if there is no limit)
	 */
	public void setMaxValue(Double maxValue) {
		this.maxValue = maxValue;
	}

	/** Gets the current value.
	 * @return a number or null.
	 */
	public Double getValue() {
		updateValue();
		if (DEBUG) {
			System.out.println ("AmountWidget.getValue returns "+value);
		}
		return this.value==null?null:new Double(this.value.doubleValue());
	}

	/** Sets the current value.
	 * @param value a value, or null.
	 */
	public void setValue(Double value) {
		refreshText(value);
	}
	
	@Override
	public void setText(String t) {
		super.setText(t);
		updateValue();
	}

	/** Set the value without changing the content of the TextField.
	 * This method does nothing if the value is equals to the current widget value.
	 * @param value
	 * @return true if the value was changed
	 */
	private boolean internalSetValue(Double value) {
		// Does nothing if amount is equals to current widget amount
		// Be aware of null values
		if (NullUtils.areEquals(value, this.value)) {
			return false;
		}
		Number old = this.value;
		this.value = value;
		firePropertyChange(VALUE_PROPERTY, old, value);
		return true;
	}
	
	/** Gets the content validity.
	 * @return true if the content is valid, false if it is not.
	 */
	public boolean isContentValid() {
		return this.valid;
	}
}
