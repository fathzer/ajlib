package net.astesana.ajlib.swing.dialog.urichooser;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URI;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
	public static final String URI_APPROVED_PROPERTY = AbstractURIChooserPanel.URI_APPROVED_PROPERTY;

	private URI selectedURI;
	private boolean isSave;
	/** The last tab that was set up (used to prevent a tab from being setup again when the selected uri is set before this component is made visible) */
	private int lastSetup;
	
	/**
	 * Creates the chooser.
	 */
	public URIChooser(AbstractURIChooserPanel[] choosers) {
		this.lastSetup = -1;
		setTabPlacement(JTabbedPane.TOP);
		for (AbstractURIChooserPanel uiChooser:choosers) {
			addTab(uiChooser.getName(), uiChooser.getIcon(), (Component)uiChooser, null);
			((Component)uiChooser).addPropertyChangeListener(AbstractURIChooserPanel.SELECTED_URI_PROPERTY, new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					URI old = selectedURI;
					selectedURI = (URI) evt.getNewValue();
					firePropertyChange(SELECTED_URI_PROPERTY, old, selectedURI);
				}
			});
			((Component)uiChooser).addPropertyChangeListener(AbstractURIChooserPanel.URI_APPROVED_PROPERTY, new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					firePropertyChange(URI_APPROVED_PROPERTY, evt.getOldValue(), evt.getNewValue());
				}
			});
		}
		setDialogType(false);
		ChangeListener listener = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				URI old = selectedURI;
				selectedURI = ((AbstractURIChooserPanel)getSelectedComponent()).getSelectedURI();
				firePropertyChange(SELECTED_URI_PROPERTY, old, selectedURI);
				setUp(getSelectedIndex());
			}
		};
		addChangeListener(listener);
	}
	
	void setUp(int index) {
		if (lastSetup!=index) {
			lastSetup = index;
			((AbstractURIChooserPanel)getComponent(index)).setUp();
		}
	}

	public void setDialogType(boolean save) {
		this.isSave = save;
		for (int i = 0; i < this.getTabCount(); i++) {
			AbstractURIChooserPanel tab = (AbstractURIChooserPanel)this.getComponentAt(i);
			this.setToolTipTextAt(i, tab.getTooltip(save));
			tab.setDialogType(save);
		}
	}
	
	public boolean isSaveDialogType() {
		return isSave;
	}

	/** Gets the currently selected URI.
	 * @return an URI, or null if the user selected nothing.
	 */
	public URI getSelectedURI() {
		return selectedURI;
	}

	/** Sets the current uri.
	 * @param uri
	 */
	public void setSelectedURI(URI uri) {
		if (uri!=null) {
			String scheme = uri.getScheme();
			for (int i=0; i<getComponentCount(); i++) {
				AbstractURIChooserPanel panel = (AbstractURIChooserPanel)getComponent(i);
				if (panel.getSchemes().contains(scheme)) {
					panel.setSelectedURI(uri);
					setSelectedIndex(i);
					break;
				}
			}
		} else {
			((AbstractURIChooserPanel)getSelectedComponent()).setSelectedURI(uri);
		}
	}
}
