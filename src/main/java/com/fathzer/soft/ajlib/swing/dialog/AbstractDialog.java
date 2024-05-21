package com.fathzer.soft.ajlib.swing.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

import javax.swing.*;

import com.fathzer.soft.ajlib.swing.Utils;
import com.fathzer.soft.ajlib.swing.framework.Application;


/** An abstract dialog with a customizable center pane, an Ok and/or a Cancel button.
 * <br>By default, the dialog:<ul>
 * <li>is not resizable, call this.setResizable(true) to change this behavior (don't forget to call pack
 * and set the minimum size after calling setResizable).</li>
 * <li>has default close operation sets to DISPOSE_ON_CLOSE.</li>
 * </ul>
 * The dialog automatically wrap its center component in a JScollPane to guarantee that the dialog will
 * never be bigger than the screen (in such a case ok/cancel buttons would be hidden).
 * @param <T> The class of the parameter of the dialog (information that is useful to build the center pane).
 * @param <V> The class of the result of the dialog
 */
public abstract class AbstractDialog<T,V> extends JDialog {
	//FIXME The horizontal scroll bar is always shown when the vertical one is shown. 
	private static final long serialVersionUID = 1L;
	
	private V result;

	private JButton cancelButton;
	private JButton okButton;
	/** The data passed to the dialog's constructor.
	 */
	protected T data;

	/**
	 * Constructor
	 * @param owner Dialog's parent frame
	 * @param title Dialog's title
	 * @param data optional data (will be transfered to createContentPane)
	 */
	 protected AbstractDialog(Window owner, String title, T data) {
		super(owner, title, ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.data = data;
		this.result = null;
		final AbstractAction escapeAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent ae) {
				cancel();
			}
		};
		String actionMapKey = "ESCAPE_KEY"; //$NON-NLS-1$
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), actionMapKey);
		getRootPane().getActionMap().put(actionMapKey, escapeAction);
		this.setContentPane(this.createContentPane());
		this.setResizable(false);
		this.pack();
		this.setLocationRelativeTo(owner);
	}
	
	private Container createContentPane() {
		//Create the content pane.
		JPanel contentPane = new JPanel(new BorderLayout(5,5));
		contentPane.setOpaque(true);
		contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JPanel southPane = new JPanel(new BorderLayout());
		southPane.setOpaque(false);
		JPanel buttonsPane = createButtonsPane();

		southPane.add(buttonsPane, BorderLayout.EAST);
		JComponent extra = createExtraComponent();
		if (extra!=null) {
			southPane.add(extra, BorderLayout.WEST);
		}
		contentPane.add(southPane, BorderLayout.SOUTH);

		JPanel centerPane = this.createCenterPane();
		if (centerPane != null) {
			// As component can be bigger than screen ... and we always want the "ok", "cancel" button to be present,
			// we will wrap the center pane into a scrollPane
			Component component = (centerPane instanceof Scrollable) ? centerPane : new DefaultScrollablePanel(centerPane);
			JScrollPane scrollPane = new JScrollPane(component);
			scrollPane.setBorder(null);
			contentPane.add(scrollPane, BorderLayout.CENTER);
		}
		
		this.updateOkButtonEnabled();
		
		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource().equals(okButton)) {
					confirm();
				} else {
					cancel();
				}
			}
		};
		getOkButton().addActionListener(listener);
		getCancelButton().addActionListener(listener);
		return contentPane;
	}

	/** Gets the panel that contains the ok and cancel buttons.
	 * <br>This method creates a new JPanel with flow layout and ok and cancel buttons. Then, it defines ok has the default button.
	 * <br>It can be override to add more buttons.
	 * @return a JPanel
	 */
	protected JPanel createButtonsPane() {
		JPanel buttonsPane = new JPanel();
		buttonsPane.setOpaque(false);
		buttonsPane.add(getOkButton());
		buttonsPane.add(getCancelButton());
		getRootPane().setDefaultButton(okButton);
		return buttonsPane;
	}
	
	/** Gets an extra component displayed on the same line as the buttons panel (see {@link #createButtonsPane()}) on the left side.
	 * <br>By default, this method returns null.
	 * @return A JComponent or null if no component is provided.
	 */
	protected JComponent createExtraComponent() {
		return null;
	}

	/** Gets the ok button.
	 * <br>If you want the dialog not to have an ok button, you may set the visibility of this button to false with
	 * (getOkButton.setVisible(false)).
	 * @return The ok button.
	 */
	protected JButton getOkButton() {
		if (okButton==null) {
			okButton = new JButton(Application.getString("GenericButton.ok", getLocale())); //$NON-NLS-1$
			okButton.setOpaque(false);
		}
		return okButton;
	}
	
	/** Gets the cancel button.
	 * If you want the dialog not to have a cancel button, you may set the visibility of this button to false
	 * (getCancelButton.setVisible(false)).
	 * @return the cancel button.
	 */
	protected JButton getCancelButton() {
		if (cancelButton==null) {
			cancelButton = new JButton(Application.getString("GenericButton.cancel", getLocale())); //$NON-NLS-1$
			cancelButton.setToolTipText(Application.getString("GenericButton.cancel.toolTip", getLocale())); //$NON-NLS-1$
			cancelButton.setOpaque(false);
		}
		return cancelButton;
	}
	
	/** Gets the center pane of this dialog.
	 * This method is called once by the constructor of the dialog.
	 * The data attribute is already set to the data parameter passed to the constructor.
	 * @return a panel
	 */
	protected abstract JPanel createCenterPane();
	
	/** This method is called when the user clicks the ok button.
	 * <br>This method should return the object, result of the dialog, that will be returned
	 * by getResult.
	 * <br>Note that it is not a good practice to override this method and set its visibility to public.
	 * You should prefer calling the getResult method as buildResult may instantiate a new object each
	 * time it is called.
	 * @return an object
	 * @see #getResult()
	 */
	protected abstract V buildResult();

	/** This method is called when the user clicks the ok button.
	 * <br>It calls the buildResult method, stores the result, then closes the dialog.
	 * @see #getResult()
	 */
	protected void confirm() {
		result = buildResult();
		close();
	}

	/** This method is called when the user clicks the cancel button.
	 * <br>This default implementation closes the dialog.
	 */
	protected void cancel() {
		result = null;
		close();
	}
	
	private void close() {
		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

	/** Checks if the user input is consistent and return a short explanation of why it is not.
	 * <br>In this implementation, this method returns null. You should override this method if the input can be inconsistent.
	 * @return a short message explaining why the ok button is disabled or null if the ok button has to be enabled.
	 * This message will be displayed in the ok button toolTip.
	 */
	protected String getOkDisabledCause() {
		return null;
	}
	
	/** Gets the result of this dialog.
	 * @return an object, or null if the dialog was cancelled.
	 */
	public V getResult() {
		return result;
	}
	
	/** Forces the state of the users input to be evaluated and updates the state of the ok button.
	 * @see #getOkDisabledCause()
	 */
	public void updateOkButtonEnabled() {
		String cause = getOkDisabledCause();
		this.getOkButton().setEnabled(cause==null);
		this.getOkButton().setToolTipText(cause==null?Application.getString("GenericButton.ok.toolTip", getLocale()):cause); //$NON-NLS-1$
	}
	
	@Override
	public Dimension getPreferredSize() {
		//We never want the dialog to be bigger than the available screen space
		//So, we will return the smaller of the preferred size and the available one.
		Dimension preferred = super.getPreferredSize();
		Rectangle availableSpace = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		preferred.height = Math.min(preferred.height, availableSpace.height);
		preferred.width = Math.min(preferred.width, availableSpace.width);
		return preferred;
	}


	/** Gets the window which contains a component.
	 * @deprecated
	 * @param component the component
	 * @return The window containing the component or null if no window contains the component.
	 * @see Utils#getOwnerWindow(Component)
	 */
	@Deprecated
	public static Window getOwnerWindow(Component component) {
		return Utils.getOwnerWindow(component);
	}
}
