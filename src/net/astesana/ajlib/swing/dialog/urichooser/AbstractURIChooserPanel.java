package net.astesana.ajlib.swing.dialog.urichooser;

import java.net.URI;

import javax.swing.Icon;

/** An abstract component that allows the user to select a destination where to save/read his data.
 * <br><b><u>WARNING</u>:Although there is no indication that the class must be a subclass of Component, this is mandatory.</b>
 * @author Jean-Marc Astesana
 * <BR>License : GPL v3
 * @see URIChooser
 */
public interface AbstractURIChooserPanel {
	/** The name of the selected uri property.
	 * <br>This component should fire a property change event when the selected uri is modified.
	 * @see #getSelectedURI() 
	 */
	public static final String SELECTED_URI_PROPERTY = "selectedUri";

	/** The name of the panel.
	 * <br>This name will be used as tab name by the URIChooser. 
	 * @return a String
	 */
	public abstract String getName();
	/** The tooltip of the panel.
	 * <br>It will be used as tab tooltip by the URIChooser. 
	 * @return a String
	 */
	public abstract String getTooltip();
	/** The icon of the panel.
	 * <br>It will be used as tab icon by the URIChooser. 
	 * @return a String
	 */
	public abstract Icon getIcon();

	/** Gets the currently selected URI.
	 * @return an URI or null if no destination is currently selected
	 */
	public URI getSelectedURI();
	
	/** Sets the dialog type (save or read).
	 * @param save true if the dialog is for saving data, false for reading data.
	 */
	public void setDialogType(boolean save);
	
	/** Set up the panel.
	 * <br>This method is called the each time the panel is becoming selected.
	 * <br>It is the good place to set up the panel (connect to servers, for instance).
	 * <br>If the set up is a time consuming task, it is a good practice to use SwingUtilities.invokeLater
	 * in this method to perform the setup, in order the component to be displayed fast.
	 */
	public void setUp();

	public void setURIChooser(URIChooser chooser);
}
