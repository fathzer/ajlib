package com.fathzer.soft.ajlib.swing;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.UIManager;

/** Some utilities related to font management.
 * @author Jean-Marc Astesana <BR>
 *         License: LGPL v3
 */
public abstract class FontUtils {
	private static final String DEFAULT_FONT_PROPERTY_NAME = "defaultFont"; //$NON-NLS-1$

	private FontUtils() {
		super();
	}
	
	/** Gets the list of available text fonts.
	 * <br>A text font is a font able to display text. if a font is available in the system but only displays symbols
	 * it is not available as a text font. 
	 * <br><i>Please note that the returned list is approximated by the list of fonts able to display their own localized name. This could change in the future.</i>
	 * @param locale a locale used to display text.
	 * @return The list of fonts that are available and able to display text in the specified locale.
	 */
	public static List<Font> getAvailableTextFonts(Locale locale) {
		ArrayList<Font> result = new ArrayList<Font>();
		Font[] allfonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
		for (Font font : allfonts) {
		    if (font.canDisplayUpTo(font.getFontName(locale)) == -1) {
		    	result.add(font);
		    }
		}
		return result;
	}
	
	/** Tests whether a look and feel supports a default font 
	 * @param lookAndFeelName The look and feel name.
	 * @return true if the look and feel supports default font.
	 */
	public static boolean isDefaultFontSupportedByLookAndFeel(String lookAndFeelName) {
		return "Nimbus".equals(lookAndFeelName);
	}
	
	/** Gets the current default font.
	 * @return a Font or null if the current look and feel do not supports default font.
	 */
	public static Font getDefaultFont() {
		return isDefaultFontSupportedByLookAndFeel(UIManager.getLookAndFeel().getName()) ?
			(Font)UIManager.getDefaults().getFont(DEFAULT_FONT_PROPERTY_NAME) : null;
	}

	/** Sets the default font.
	 * <br>Note that if the current UI does not support default font, the method does nothing.
	 * @param font The new default font
	 * @see FontUtils#isDefaultFontSupportedByLookAndFeel(String)
	 */
	public static void setDefaultFont(Font font) {
		if (isDefaultFontSupportedByLookAndFeel(UIManager.getLookAndFeel().getName())) {
			UIManager.getLookAndFeelDefaults().put(DEFAULT_FONT_PROPERTY_NAME, font);
		}
	}
}
