package net.astesana.ajlib.swing.dialog.urichooser;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URI;

import javax.swing.JPanel;

import net.astesana.ajlib.swing.dialog.AbstractDialog;
import net.astesana.ajlib.swing.dialog.FileChooser;
import net.astesana.ajlib.swing.framework.Application;

@SuppressWarnings("serial")
public class MultipleURIChooserDialog extends AbstractDialog<AbstractURIChooserPanel[], URI> {
	private MultipleURIChooserPanel multiplePanel;
	private boolean saveDialog;
	
	/** Constructor.
	 * <br>The created instance is an open dialog. You may call setSaveDialog if you need a save dialog
	 * @param owner The owner window of the dialog
	 * @param title The dialog's title
	 * @param choosers The abstract URI choosers. The elements of this array should be subclasses of java.awt.Component
	 */
	public MultipleURIChooserDialog(Window owner, String title, AbstractURIChooserPanel[] choosers) {
		super(owner, title, choosers);
		saveDialog = false; // To force setSaveDialog to do something
		setSaveDialog(false);
	}
	
	private AbstractURIChooserPanel getSelectedPanel() {
		if (!isVisible()) return null;
		if (multiplePanel==null) return this.data[0];
		return (AbstractURIChooserPanel) multiplePanel.getSelectedComponent();
	}

	@Override
	protected JPanel createCenterPane() {
		addWindowListener(new WindowAdapter() {
			/* (non-Javadoc)
			 * @see java.awt.event.WindowAdapter#windowOpened(java.awt.event.WindowEvent)
			 */
			@Override
			public void windowOpened(WindowEvent e) {
				getSelectedPanel().setUp();
			}
		});
		PropertyChangeListener selectListener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				updateOkButtonEnabled();
			}
		};
		PropertyChangeListener confirmListener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if ((Boolean)evt.getNewValue()) {
					confirm();
				}
			}
		};
		JPanel result = new JPanel(new BorderLayout());
		this.multiplePanel = data.length==1 ? null : new MultipleURIChooserPanel(data);
		Component cp = multiplePanel==null ? (Component) data[0] : multiplePanel;
		result.add(cp, BorderLayout.CENTER);
		cp.addPropertyChangeListener(AbstractURIChooserPanel.SELECTED_URI_PROPERTY, selectListener);
		cp.addPropertyChangeListener(MultipleURIChooserPanel.URI_APPROVED_PROPERTY, confirmListener);
		return result;
	}

	@Override
	protected URI buildResult() {
		return getSelectedURI();
	}

	@Override
	protected String getOkDisabledCause() {
		if (getSelectedURI()==null) return Application.getString("URIChooserDialog.noFileSelected"); //$NON-NLS-1$
		return null;
	}

	/* (non-Javadoc)
	 * @see net.astesana.ajlib.swing.dialog.AbstractDialog#confirm()
	 */
	@Override
	protected void confirm() {
		URI selectedURI = getSelectedURI();
		boolean exists = selectedURI!=null && this.saveDialog && getSelectedPanel().isSelectedExist();
		if (exists && FileChooser.showSaveDisplayQuestion(this)) return;
		super.confirm();
	}
	
	private URI getSelectedURI() {
		AbstractURIChooserPanel panel = getSelectedPanel();
		return panel!=null?panel.getSelectedURI():null;
	}
	
	/** Sets the dialog's type.
	 * @param save true for a save dialog, false for an open dialog
	 */
	public void setSaveDialog(boolean save) {
		if (save!=saveDialog) {
			getOkButton().setText(save?Application.getString("URIChooserDialog.saveButton.title"):Application.getString("URIChooserDialog.openButton.title")); //$NON-NLS-1$ //$NON-NLS-2$
			for (AbstractURIChooserPanel panel : data) {
				panel.setDialogType(save);
			}
		}
	}

	/** Shows the dialog and gets its result.
	 * @return an URI
	 */
	public URI showDialog() {
		setVisible(true);
		return getResult();
	}

	/** Sets the current uri.
	 * @param uri
	 */
	public void setSelectedURI(URI uri) {
		String scheme = uri.getScheme();
		for (AbstractURIChooserPanel panel : data) {
			if (panel.getSchemes().contains(scheme)) {
				panel.setSelectedURI(uri);
				if (multiplePanel!=null) {
					multiplePanel.setSelectedComponent((Component) panel);
				}
			}
		}
	}
}
