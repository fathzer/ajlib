package com.fathzer.soft.ajlib.swing.widget;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

/** A widget to enter a monetary value.
 * <br>This widget automatically format the value it contains according to its local's currency.
 * <br>You can restrict the valid values by setting minimum and maximum values.
 * <br>This bean defines two properties:<ul>
 * <li>VALUE_PROPERTY: a double, the value currently entered in the field.</li>
 * <li>CONTENT_VALID_PROPERTY: a boolean, true if the text currently entered in the field is a valid value, false if it is not.
 * <br>The CONTENT_VALID_PROPERTY is a read only property.</li>
 * </ul>
 * @author Jean-Marc Astesana
 * <BR>License: LGPL v3
 */
public class CurrencyWidget extends NumberWidget {
	private static final long serialVersionUID = 1L;
	
	private DecimalFormat numberFormat;
	
	/** Constructor.
	 *  The local is set to default locale.
	 *  @see #CurrencyWidget(Locale)
	 */
	public CurrencyWidget() {
		this(Locale.getDefault());
	}
	
	/** Constructor.
	 * Empty field is not allowed.
	 * minValue is equals to Double.NEGATIVE_INFINITY, maxValue to Double.POSITIVE_INFINITY.
	 * The value is set to null.
	 * @param locale The locale to apply to the widget (use to format the amount typed).
	 */
	public CurrencyWidget(Locale locale) {
		super(locale);
	}
	
	@Override
	protected DecimalFormat buildFormat(Locale locale) {
		this.numberFormat = super.buildFormat(locale);
		return patchJavaBug4510618((DecimalFormat) NumberFormat.getCurrencyInstance(locale));
	}

	
	/** Gets the widget's currency.
	 * @return the windget's currency
	 */
	public Currency getCurrency() {
		return getFormat().getCurrency();
	}
	
	/** Sets the currency.
	 * <br>By default, the currency is the one of the widget's locale.
	 * @param currency The new currency.
	 */
	public void setCurrency (Currency currency) {
		getFormat().setCurrency(currency);
		int digits = currency.getDefaultFractionDigits();
		getFormat().setMaximumFractionDigits(digits);
		getFormat().setMinimumFractionDigits(digits);
		refreshText();
	}

	/* (non-Javadoc)
	 * @see NumberWidget#parseValue(java.lang.String)
	 */
	@Override
	protected Number parseValue(String text) {
		Number result = super.parseValue(text);
		if (result==null) {
			result = safeParse(numberFormat, text);
		}
		return result;
	}
}
