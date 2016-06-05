package com.fathzer.soft.ajlib.swing;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.URI;

import javax.swing.JOptionPane;

import com.fathzer.jlocal.Formatter;
import com.fathzer.soft.ajlib.swing.framework.Application;


/** A class that is able to open a web browser to display an URI.
 *<br>The main advantage of this class is to display an alert dialog if java is not able to open the browser.
 * @author Jean-Marc Astesana
 * <BR>License: LGPL v3
 */
public abstract class Browser {
	private Browser() {
		//To prevent instance to be constructed
	}
	
	/** Displays an URI in a browser.
	 * @param uri The uri to display
	 * @param parent The parent component of the dialog displayed if browser is not available.
	 * @param errorDialogTitle The title of the dialog displayed if browser is not available.
	 */
	public static void show(URI uri, Component parent, String errorDialogTitle) {
		try {
			Desktop.getDesktop().browse(uri);
		} catch (IOException e) {
			error(uri, parent, errorDialogTitle);
		} catch (UnsupportedOperationException e) {
			error(uri, parent, errorDialogTitle);
		}
	}

	private static void error(URI uri, Component parent, String errorDialogTitle) {
		String url = uri.toString();
		String message = Formatter.format(Application.getString("Browser.unsupported.message", parent.getLocale()), url); //$NON-NLS-1$
		StringSelection stringSelection = new StringSelection(url);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, null);
		if (errorDialogTitle == null) {
			errorDialogTitle = "";
		}
		JOptionPane.showMessageDialog(Utils.getOwnerWindow(parent), message, errorDialogTitle, JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$
	}
}
