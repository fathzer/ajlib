package com.fathzer.soft.ajlib.swing.framework;

import java.awt.AWTEvent;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.WindowConstants;

import com.fathzer.soft.ajlib.swing.Utils;
import com.fathzer.soft.ajlib.utilities.LocalizationData;


/** A basic canvas to create simple java application.
 * <br>Here is a sample code of a very basic application.
 * <br><code>
 * import javax.swing.JLabel;<br>
 * import javax.swing.JPanel;<br>
 *<br>
 * import com.fathzer.soft.swing.framework.Application;<br>
 * <br>
 * public class HelloWorld extends Application {<br>
 * &nbsp;&nbsp;protected JPanel buildMainPanel() {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;JPanel panel = new JPanel();<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;panel.add(new JLabel ("The simplest panel"));<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;return panel;<br>
 * &nbsp;&nbsp;}<br>
 * <br>
 * &nbsp;&nbsp;public static void main (String[] args) {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;new HelloWorld().launch();<br>
 * &nbsp;&nbsp;}<br>
 * }<br>
 * </code>
 */
public abstract class Application {
	private JFrame frame;
	
	public static LocalizationData LOCALIZATION = LocalizationData.DEFAULT;

	/** Gets a message localized in the default locale.
	 * @param key The message key.
	 * @return The localized message.
	 * @see #getString(String, Locale)
	 */
	public static String getString(String key) {
		return LOCALIZATION.getString(key);
	}
	
	/** Gets a localized message.
	 * @param key The message key.
	 * @param locale The desired locale.
	 * @return The localized message.
	 * @see LocalizationData
	 */
	public static String getString(String key, Locale locale) {
		return LOCALIZATION.getString(key, locale);
	}

	/** Launches the application.
	 */
	public final void launch() {
		// Set the look and feel
		setLookAndFeel();
		// Warning the new event queue may ABSOLUTLY be installed by the event dispatch thread under java 1.7+ or the program will never exit
		// Warning (2), if the new event queue is installed by the Runnable that launches start, it sometimes cause a NullPointerException
			// at java.awt.EventQueue.getCurrentEventImpl(EventQueue.java:796), for instance when setting the selectedItem of a JComboBox in the start method
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						installEventQueue();
					}
				});
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e.getCause());
			}
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				start();
			}
		});
	}
	
	private void installEventQueue() {
		EventQueue queue = Toolkit.getDefaultToolkit().getSystemEventQueue();
		queue.push(new EventQueue() {
			@Override
			protected void dispatchEvent(AWTEvent event) {
				try {
					super.dispatchEvent(event);
				} catch (Throwable t) {
					processException(t);
					// The following portion of code closes the main window if it is not visible.
					// This solves the case where a exception is thrown during the call to frame.setVisible(true)
					// If we do nothing, the window is there, but is invisible, so, the user has no way to close
					// the window for exiting from application.
					Window[] windows = Window.getWindows();
					for (int i = 0; i < windows.length; i++) {
						if ((windows[i] == frame) && !windows[i].isVisible()) {
							windows[i].dispatchEvent(new WindowEvent(windows[i],WindowEvent.WINDOW_CLOSING));
						}
					}
				}
			}
		});
	}

	/** Process an exception.
	 * <br>When an exception happens in the dispatching of a swing event, this method is called.
	 * <br>The default implementation prints the exception stack trace to the error output.
	 * You may override it to display an alert dialog, to send a bug report by email, etc ...
	 * @param e The exception
	 */
	protected void processException(Throwable e) {
		e.printStackTrace();
	}

	/** Gets the application name.
	 * <br>You should override this method to define the name of your application.
	 * <br>This name will be displayed as the main window's title.
	 * @return The application's name
	 */
	public String getName() {
		return "";
	}

	/** Sets the look and feel.
	 * <br>This implementation try to install the LAF whose name is returned by getDefaultLookAndFeelName method.
	 * If an error occurs, it switches to the system LAF.
	 * @see #getDefaultLookAndFeelName()
	 */
	private void setLookAndFeel() {
		// In a previous version, the class name were used instead of the generic name.
		// It caused problem when changing java version (ie: Nimbus in java 1.6 was implemented by a class in com.sun.etc and in javax.swing in java 1.7)
		// So, we now use the LAF name.
		String lookAndFeelName = getDefaultLookAndFeelName();
		String lookAndFeelClass = null;
		LookAndFeelInfo[] installedLookAndFeels = UIManager.getInstalledLookAndFeels();
		for (LookAndFeelInfo lookAndFeelInfo : installedLookAndFeels) {
			if (lookAndFeelInfo.getName().equals(lookAndFeelName)) {
				lookAndFeelClass = lookAndFeelInfo.getClassName();
				break;
			}
		}
		if (lookAndFeelClass==null) {
			lookAndFeelClass = UIManager.getSystemLookAndFeelClassName();
		}
		try {
			UIManager.setLookAndFeel(lookAndFeelClass);
			UIManager.getLookAndFeelDefaults().setDefaultLocale(Locale.getDefault());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/** Gets the default look and feel name.
	 * <br>This default implementation return "Nimbus".
	 * <br>You can override this method to change the startup look and feel of your application.
	 * <br>If the returned LAF is not available on the platform the system LAF will be used. 
	 * @return a Look and feel name.
	 * @see #setLookAndFeel()
	 */
	protected String getDefaultLookAndFeelName() {
		return "Nimbus";
	}

	/** Saves the application state.
	 * <br>This implementation saves the frame's location and dimension.
	 */
	protected void saveState() {
		Utils.saveState(getJFrame(), getPreferences());
	}

	/** Restores the application state.
	 * <br>This implementation restores the frame's location and dimension.
	 */
	protected void restoreState() {
		Utils.restoreState(getJFrame(), getPreferences());
	}
	
	/** Gets the preferences used store the application's preferences.
	 * @return a Preferences instance
	 */
	protected Preferences getPreferences() {
		return Preferences.userRoot().node(getClass().getCanonicalName());
	}
	
	/** Creates the main panel.
	 * <br>Be aware that the returned panel should be instantiated by this method and not before (for example in the constructor).
	 * It should be instantiated there because, before, the look and feel may not have been set.
	 * @return The panel that will by displayed in the application frame.
	 */
	protected abstract Container buildMainPanel();
	
	/** Builds the application menu bar.
	 * <br>This implementation returns a menu bar with a single menu and a single item File/Quit that calls the quit method.
	 * You should override this method to build your own menu bar.
	 * <br>Be aware that the returned menu bar should be instantiated by this method and not before (for example in the constructor).
	 * It should be instantiated there because, before, the look and feel may not have been set.
	 * @return a JMenuBar or null if we want the application not to have one.
	 * @see #quit()
	 */
	protected JMenuBar buildMenuBar() {
		return new MainMenu(this);
	}

	private void start() {
		frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.setContentPane(buildMainPanel());
		frame.setTitle(getName());
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				onClose(event);
			}
		});
		frame.setJMenuBar(buildMenuBar());
		frame.pack();
		restoreState();
		boolean quit = !onStart();
		if (quit) {
			quit();
		} else {
			frame.setVisible(true);
			onVisible();
		}
	}
	
	/** Gets the main application frame.
	 * @return a JFrame
	 */
	protected JFrame getJFrame() {
		return this.frame;
	}
	
	/** Asks the application to quit.
	 * <br>By default this method closes the main application frame.
	 * You should override this method if you have to close other frames.
	 */
	protected void quit() {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {		
			@Override
			public void run() {
				frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
			}
		});
	}

	/** This method is called one time, at startup, before the frame is made visible, on the eventDispatchThread.
	 * <br>This default implementation does nothing.
	 * @return false to quit the application (it means the start process has failed), true to continue.
	 */
	protected boolean onStart() {
		return true;
	}
	
	/** This method is called one time, at startup, after the frame is made visible, on the eventDispatchThread.
	 * <br>This default implementation does nothing.
	 */
	protected void onVisible() {
		// By default, this method does nothing
	}
	
	/** This method is called when the application frame is closed.
	 * <br>The default implementation calls saveState, then disposes the window.
	 * @param event the Window event received
	 * @see #saveState()
	 */
	protected void onClose(WindowEvent event) {
		saveState();
		event.getWindow().dispose();
	}
}
