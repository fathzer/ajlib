package net.astesana.ajlib.swing.dialog.urichooser;

import java.awt.BorderLayout;
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
public class URIChooserDialog extends AbstractDialog<MultipleURIChooserPanel, URI> {
	public URIChooserDialog(Window owner, String title, AbstractURIChooserPanel[] choosers) {
		super(owner, title, new MultipleURIChooserPanel(choosers));
		setSaveDialogType(false);
	}

	@Override
	protected JPanel createCenterPane() {
		data.addPropertyChangeListener(MultipleURIChooserPanel.URI_APPROVED_PROPERTY, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if ((Boolean)evt.getNewValue()) {
					confirm();
				}
			}
		});
		addWindowListener(new WindowAdapter() {
			/* (non-Javadoc)
			 * @see java.awt.event.WindowAdapter#windowOpened(java.awt.event.WindowEvent)
			 */
			@Override
			public void windowOpened(WindowEvent e) {
				((AbstractURIChooserPanel)data.getSelectedComponent()).setUp();
			}
		});
		this.data.addPropertyChangeListener(AbstractURIChooserPanel.SELECTED_URI_PROPERTY, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				updateOkButtonEnabled();
			}
		});
		JPanel result = new JPanel(new BorderLayout());
		result.add(this.data, BorderLayout.CENTER);
		return result;
	}

	@Override
	protected URI buildResult() {
		return this.data.getSelectedURI();
	}

	@Override
	protected String getOkDisabledCause() {
		if (this.data.getSelectedURI()==null) return Application.getString("URIChooserDialog.noFileSelected"); //$NON-NLS-1$
		return null;
	}

	/* (non-Javadoc)
	 * @see net.astesana.ajlib.swing.dialog.AbstractDialog#confirm()
	 */
	@Override
	protected void confirm() {
		AbstractURIChooserPanel panel = (AbstractURIChooserPanel)this.data.getSelectedComponent();
		URI selectedURI = panel.getSelectedURI();
		boolean exists = selectedURI!=null && data.isSaveDialogType() && panel.isSelectedExist();
		if (exists && FileChooser.showSaveDisplayQuestion(this)) return;
		super.confirm();
	}
	
	/** Sets the dialog's type.
	 * @param save true for a save dialog, false for an open dialog
	 */
	public void setSaveDialogType(boolean save) {
		if (save!=data.isSaveDialogType()) {
			getOkButton().setText(save?Application.getString("URIChooserDialog.saveButton.title"):Application.getString("URIChooserDialog.openButton.title")); //$NON-NLS-1$ //$NON-NLS-2$
			data.setDialogType(save);
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
		data.setSelectedURI(uri);
	}
}
