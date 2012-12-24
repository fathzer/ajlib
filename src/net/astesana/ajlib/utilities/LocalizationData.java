package net.astesana.ajlib.utilities;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/** This class is the main entry point for localization concerns.
 * @author Jean-Marc Astesana
 * <BR>License : GPL v3
 */
public class LocalizationData {
	/** @deprecated. */
	public static final Locale SYS_LOCALE = getSystemLocale();
	/** @deprecated. */
	public static final LocalizationData DEFAULT = new LocalizationData(getDefaultBundle(SYS_LOCALE));
		
	private List<ResourceBundle> bundle;
	private boolean translatorMode;
	
	public static ResourceBundle getDefaultBundle(Locale locale) {
		return ResourceBundle.getBundle("net.astesana.ajlib.Resources", locale); //$NON-NLS-1$
	}
	
	public static Locale getSystemLocale() {
		try {
			return new Locale(System.getProperty("user.language"), System.getProperty("user.country")); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (SecurityException e) {
			return Locale.getDefault();
		}
	}
		
	public LocalizationData (String bundlePath) {
		this (ResourceBundle.getBundle(bundlePath));
	}
		
	public LocalizationData (ResourceBundle bundle) {
		translatorMode = false;
		this.bundle = new ArrayList<ResourceBundle>();
		this.bundle.add(bundle);
	}
	
	public void add(ResourceBundle bundle) {
		this.bundle.add(bundle);
	}

	public String getString(String key) {
		// If translator mode is on, return the key
		if (translatorMode) return key;
		// Check key in additional bundles
		for (int i = this.bundle.size()-1; i > 0; i--) {
			if (this.bundle.get(i).containsKey(key)) return this.bundle.get(i).getString(key);
		}
		return this.bundle.get(0).getString(key);
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
