package net.astesana.ajlib.utilities;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/** This class is the main entry point for localization concerns.
 * @author Jean-Marc Astesana
 * <BR>License : GPL v3
 */
public class LocalizationData {
	public static final Locale SYS_LOCALE;
	public static final LocalizationData DEFAULT = new LocalizationData("net.astesana.ajlib.Resources"); //$NON-NLS-1$
	
	static {
		Locale locale;
		try {
			locale = new Locale(System.getProperty("user.language"), System.getProperty("user.country")); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (SecurityException e) {
			locale = Locale.getDefault();
			System.out.println (locale);
		}
		SYS_LOCALE = locale;
	}
	
	private ResourceBundle bundle;
	private boolean translatorMode;
		
	public LocalizationData (String bundlePath) {
		this (ResourceBundle.getBundle(bundlePath));
	}
		
	public LocalizationData (ResourceBundle bundle) {
		translatorMode = false;
		this.bundle = bundle;
	}

	public String getString(String key) {
		return translatorMode?key:bundle.getString(key);
	}
	
	public char getChar(String key) {
		return getString(key).charAt(0);
	}

	public Locale getLocale() {
		return Locale.getDefault();
	}

	public DecimalFormat getCurrencyInstance() {
		return (DecimalFormat) NumberFormat.getCurrencyInstance(getLocale());
	}
	
	public void setTranslatorMode(boolean translatorMode) {
		this.translatorMode = translatorMode; 
	}
}
