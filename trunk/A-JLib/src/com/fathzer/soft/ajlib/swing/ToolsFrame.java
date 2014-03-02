package com.fathzer.soft.ajlib.swing;

import java.awt.Container;
import java.awt.Frame;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

/** A tool window.
 * <br>Such a window remains always on top of other windows and remains usable
 * while modal dialogs are opened.
 * <br>If it is attached to an owner window, it is automatically iconified/closed if the owner is.
 * @author Jean-Marc Astesana <BR>
 *         License: LGPL v3
 */
public class ToolsFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	private final class MyWindowListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			dispose();
			super.windowClosing(e);
		}

		@Override
		public void windowIconified(WindowEvent e) {
			setExtendedState(Frame.ICONIFIED);
			super.windowIconified(e);
		}

		@Override
		public void windowDeiconified(WindowEvent e) {
			setExtendedState(Frame.NORMAL);
			super.windowDeiconified(e);
		}
	}

	/** Constructor.
	 * @param owner The windows owner. The constructed instance will be closed/iconified when its owner window is.
	 * @param contentPane The content of the tool window.
	 */
	public ToolsFrame(Window owner, Container contentPane) {
		setContentPane(contentPane);
		pack();
		setResizable(false);
		setLocationRelativeTo(owner);
		setAlwaysOnTop(true);
		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		if (owner!=null) {
			owner.addWindowListener(new MyWindowListener());
		}
	}
}
