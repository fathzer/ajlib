package net.astesana.ajlib.utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/** This class is the main entry point for localization concerns.
 * @author Jean-Marc Astesana
 * <BR>License : GPL v3
 */
public class LocalizationData {
	public static final String DEFAULT_BUNDLE_NAME = "net.astesana.ajlib.Resources";
	public static LocalizationData DEFAULT = new LocalizationData(DEFAULT_BUNDLE_NAME);
	
	private HashMap<Locale, List<ResourceBundle>> bundles;
	private List<String> bundleNames;
	private boolean translatorMode;
	
	public static Locale getSystemLocale() {
		try {
			return new Locale(System.getProperty("user.language"), System.getProperty("user.country")); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (SecurityException e) {
			return Locale.getDefault();
		}
	}
	
	/** Constructor.
	 * @param bundlePath The main bundlePath
	 * @see #add(String)
	 */
	public LocalizationData(String bundlePath) {
		this.bundleNames = new ArrayList<String>(); this.bundleNames.add(bundlePath);
		this.bundles = new HashMap<Locale, List<ResourceBundle>>();
		this.translatorMode = false;
	}
	
	/** Adds a bundle path to this.
	 * <br>The application wordings may not be all the same bundle. This method allows you to declare additional bundles.
	 * <br>If a key is present in two or more bundles, the last added has the priority and its wording will be returned by getString methods.
	 * This allows developers to redefine some wordings.
	 * @param bundlePath
	 */
	public void add(String bundlePath) {
		for(Locale locale : bundles.keySet()) {
			List<ResourceBundle> bundles = this.bundles.get(locale);
			bundles.add(ResourceBundle.getBundle(bundlePath, locale));
		}
		this.bundleNames.add(bundlePath);
	}
	
	/** Gets a wording for a locale.
	 * @param key The wording's key
	 * @param locale The locale
	 * @return The wording
	 * @throws MissingResourceException if the key is unknown 
	 */
	public String getString(String key, Locale locale) {
		// If translator mode is on, return the key
		if (translatorMode) return key;
		// Check key in additional bundles
		List<ResourceBundle> bundle = this.getBundle(locale);
		for (int i = bundle.size()-1; i > 0; i--) {
			if (bundle.get(i).containsKey(key)) return bundle.get(i).getString(key);
		}
		return bundle.get(0).getString(key);
	}
	
	/** Gets a wording for default locale.
	 * <br>Same as getString(key, Locale.getDefault())
	 * @param key The wording's key
	 * @return a String
	 * @throws MissingResourceException if the key is unknown
	 * @see #getString(String, Locale)
	 */
	public String getString(String key) {
		return getString(key, Locale.getDefault());
	}
	
	private List<ResourceBundle> getBundle(Locale locale) {
		List<ResourceBundle> result = bundles.get(locale);
		if (result==null) {
			System.out.println ("Loading bundle for locale "+locale);
			result = new ArrayList<ResourceBundle>();
			for (String bundleName : this.bundleNames) {
				result.add(ResourceBundle.getBundle(bundleName, locale));
			}
			bundles.put(locale, result);
		}
		return result;
	}
	
	public void setTranslatorMode(boolean translatorMode) {
		this.translatorMode = translatorMode; 
	}
}
