package com.fathzer.soft.ajlib.swing.demo;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.fathzer.soft.ajlib.swing.Utils;
import com.fathzer.soft.ajlib.swing.dialog.AbstractDialog;
import com.fathzer.soft.ajlib.swing.dialog.FileChooser;
import com.fathzer.soft.ajlib.swing.widget.HTMLPane;


import java.awt.event.ActionListener;
import java.io.File;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class DialogsPanel extends JPanel {
	private JButton btnOpenDialog;
	private JButton btnOpenDirectoryChooser;

	/**
	 * Create the panel.
	 */
	public DialogsPanel() {
		add(getBtnOpenDialog());
		add(getBtnOpenDirectoryChooser());
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
							HTMLPane htmlPane = new HTMLPane("<html>This is a very simple dialog, displaying a <a href=\"http://com.fathzer.soft.ajlib/com/fathzer/soft/ajlib/swing/dialog/AbstractDialog.html\">link</a>." +
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
	private String path;
	private JButton getBtnOpenDirectoryChooser() {
		if (btnOpenDirectoryChooser == null) {
			btnOpenDirectoryChooser = new JButton("Open directory chooser");
			btnOpenDirectoryChooser.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
			    JFileChooser chooser = new FileChooser(path);
			    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					File file = chooser.showOpenDialog(DialogsPanel.this)==JFileChooser.APPROVE_OPTION ? chooser.getSelectedFile() : null;
					if (file!=null) {
						path = file.getPath();
						JOptionPane.showMessageDialog(DialogsPanel.this, "You selected "+path);
					}
				}
			});
		}
		return btnOpenDirectoryChooser;
	}
}
