package net.astesana.ajlib.swing.dialog.urichooser;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URI;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.astesana.ajlib.swing.Utils;

/** An URI chooser.
 * <br>This is a extension to the JFileChooser concept. It allows the user to select not only files,
 * but uris that can be of any scheme (ftp, http, etc ...), even non standart schemes (for example, yapbam project
 * implements a dropbox scheme).
 * @author Jean-Marc Astesana
 * <BR>License : GPL v3
 */
@SuppressWarnings("serial")
public class URIChooser extends JTabbedPane {
	public static final String SELECTED_URI_PROPERTY = AbstractURIChooserPanel.SELECTED_URI_PROPERTY;
	private URI selectedURI;
	private URIChooserDialog dialog;
	
	/**
	 * Creates the chooser.
	 */
	public URIChooser(AbstractURIChooserPanel[] choosers) {
		setTabPlacement(JTabbedPane.TOP);
		for (AbstractURIChooserPanel uiChooser:choosers) {
			addTab(uiChooser.getName(), uiChooser.getIcon(), (Component)uiChooser, uiChooser.getTooltip());
			((Component)uiChooser).addPropertyChangeListener(AbstractURIChooserPanel.SELECTED_URI_PROPERTY, new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					URI old = selectedURI;
					selectedURI = (URI) evt.getNewValue();
					firePropertyChange(SELECTED_URI_PROPERTY, old, selectedURI);
				}
			});
			((AbstractURIChooserPanel)uiChooser).setURIChooser(this);
		}
		addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				URI old = selectedURI;
				selectedURI = ((AbstractURIChooserPanel)getSelectedComponent()).getSelectedURI();
				firePropertyChange(SELECTED_URI_PROPERTY, old, selectedURI);
				((AbstractURIChooserPanel)getSelectedComponent()).setUp();
			}
		});
	}

	private void setDialogType(boolean save) {
		for (int i = 0; i < this.getTabCount(); i++) {
			AbstractURIChooserPanel tab = (AbstractURIChooserPanel)this.getComponentAt(i);
			tab.setDialogType(save);
		}
	}
	
	/** Displays a dialog to open an uri. 
	 * @param parent The dialog's parent
	 * @return The selected URI (null if the user cancels).
	 */
	public URI showOpenDialog(Component parent) {
		setDialogType(false);
		return showDialog(parent);
	}
	
	/** Displays a dialog to save an uri. 
	 * @param parent The dialog's parent
	 * @return The selected URI (null if the user cancels).
	 */
	public URI showSaveDialog(Component parent) {
		setDialogType(true);
		return showDialog(parent);
	}
	
	private URI showDialog(Component parent) {
		Window owner = Utils.getOwnerWindow(parent);
		final URIChooserDialog dialog = new URIChooserDialog(owner, "URI Chooser", this);
		dialog.addWindowListener(new WindowAdapter() {
			/* (non-Javadoc)
			 * @see java.awt.event.WindowAdapter#windowOpened(java.awt.event.WindowEvent)
			 */
			@Override
			public void windowOpened(WindowEvent e) {
				((AbstractURIChooserPanel)getSelectedComponent()).setUp();
			}
		});
		dialog.setVisible(true);
		return dialog.getResult();
	}

	/** Gets the currently selected URI.
	 * @return an URI, or null if the user selected nothing.
	 */
	public URI getSelectedURI() {
		return selectedURI;
	}
	
	void setDialog(URIChooserDialog uriChooserDialog) {
		this.dialog = uriChooserDialog;
	}

	/** Validates the dialog (in any is opened).
	 * <br>This method allows the AbstractURIChooserPanel to validate the dialog when, for example,
	 * the user double clicks an uri.
	 */
	public void approveSelection() {
		if (this.dialog!=null) this.dialog.confirm();
	}
	
	public void setCurrent(URI uri) {
		//TODO
	}
}
