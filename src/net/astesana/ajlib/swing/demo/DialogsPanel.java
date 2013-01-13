package net.astesana.ajlib.swing.demo;

import javax.swing.JPanel;
import javax.swing.JButton;

import net.astesana.ajlib.swing.Utils;
import net.astesana.ajlib.swing.dialog.AbstractDialog;
import net.astesana.ajlib.swing.widget.HTMLPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class DialogsPanel extends JPanel {
	private JButton btnOpenDialog;

	/**
	 * Create the panel.
	 */
	public DialogsPanel() {
		add(getBtnOpenDialog());
	}

	private JButton getBtnOpenDialog() {
		if (btnOpenDialog == null) {
			btnOpenDialog = new JButton("Open dialog");
			btnOpenDialog.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					AbstractDialog<Void, Boolean> dialog = new AbstractDialog<Void, Boolean>(Utils.getOwnerWindow(btnOpenDialog),"a very simple panel",null) {
						@Override
						protected JPanel createCenterPane() {
							JPanel panel = new JPanel();
							HTMLPane htmlPane = new HTMLPane("<html>This is a very simple dialog, displaying a <a href=\"http://ajlib.sourceforge.net/javadoc/net/astesana/ajlib/swing/dialog/AbstractDialog.html\">link</a>." +
									"<br>You should override createCentePane and buildResult methods to create more complex dialogs.</html>");
							panel.add(htmlPane);
							return panel;
						}

						@Override
						protected Boolean buildResult() {
							return Boolean.TRUE;
						}
					};
					dialog.setVisible(true);
					if (dialog.getResult()!=null) {
						AJLibDemo.setMessage("The dialog was validated");
					} else {
						AJLibDemo.setMessage("The dialog was cancelled");
					}
				}
			});
		}
		return btnOpenDialog;
	}
}
