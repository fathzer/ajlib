package net.astesana.ajlib.swing.framework;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Locale;
import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import net.astesana.ajlib.utilities.LocalizationData;

/** A basic canvas to create simple java application.
 * @author Jean-Marc Astesana
 * <BR>License : GPL v3
 */
public abstract class Application {
	private static final String LOCATION_Y_PROPERTY = "y"; //$NON-NLS-1$
	private static final String LOCATION_X_PROPERTY = "x"; //$NON-NLS-1$
	private static final String SIZE_X_PROPERTY = "size.x"; //$NON-NLS-1$
	private static final String SIZE_Y_PROPERTY = "size.y"; //$NON-NLS-1$
	
	private JFrame frame;
	
	public static LocalizationData LOCALIZATION = LocalizationData.DEFAULT;
	
	public static String getString(String key) {
		return LOCALIZATION.getString(key);
	}

	/** Launches the application.
	 */
	public final void launch() {
		// Set the look and feel
		setLookAndFeel();
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				start();
			}
		});
	}

	/** Gets the application name.
	 * <br>You should implement this method to define the name of your application.
	 * <br>This name will be displayed in the main window of the application
	 * @return The application's name
	 */
	public abstract String getName();

	/** Sets the look and feel.
	 * <br>This implementation try to install the LAF whose name is returned by getDefaultLookAndFeelName method.
	 * If an error occurs, it switches to the system LAF.
	 * @see #getDefaultLookAndFeelName()
	 */
	private void setLookAndFeel() {
		// Prior the 0.9.8, the class name were used instead of the generic name.
		// It caused problem when changing java version (ie: Nimbus in java 1.6 was implemented by a class in com.sun.etc and in javax.swing in java 1.7)
		// So, we will find which LAF has the searched name.
		String lookAndFeelName = getDefaultLookAndFeelName();
		String lookAndFeelClass = null;
		LookAndFeelInfo[] installedLookAndFeels = UIManager.getInstalledLookAndFeels();
		for (LookAndFeelInfo lookAndFeelInfo : installedLookAndFeels) {
			if (lookAndFeelInfo.getName().equals(lookAndFeelName)) {
				lookAndFeelClass = lookAndFeelInfo.getClassName();
				break;
			}
		}
		if (lookAndFeelClass==null) lookAndFeelClass = UIManager.getSystemLookAndFeelClassName();
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
	 * <br>This implementation saves the frame's location.
	 */
	protected void saveState() {
		Preferences prefs = Preferences.userRoot().node(getClass().getCanonicalName());
		prefs.put(LOCATION_X_PROPERTY, Integer.toString(frame.getLocation().x));
		prefs.put(LOCATION_Y_PROPERTY, Integer.toString(frame.getLocation().y));

		Dimension size = frame.getSize();
		int h = ((frame.getExtendedState() & Frame.MAXIMIZED_VERT) == 0) ? size.height : -1;
		int w = ((frame.getExtendedState() & Frame.MAXIMIZED_HORIZ) == 0) ? size.width : -1;
		
		prefs.put(SIZE_X_PROPERTY, Integer.toString(w));
		prefs.put(SIZE_Y_PROPERTY, Integer.toString(h));
	}

	/** Restores the application state.
	 * <br>This implementation restores the frame's location.
	 */
	protected void restoreState() {
		Preferences prefs = Preferences.userRoot().node(getClass().getCanonicalName());
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Point location = new Point(prefs.getInt(LOCATION_X_PROPERTY, 0), prefs.getInt(LOCATION_Y_PROPERTY, 0));
		if (location.x+frame.getWidth()>screenSize.width) location.x = screenSize.width-frame.getWidth();
		if (location.y+frame.getHeight()>screenSize.height) location.y = screenSize.height-frame.getHeight();
		frame.setLocation(location);
		Dimension dimension = new Dimension(prefs.getInt(SIZE_X_PROPERTY, 0), prefs.getInt(SIZE_Y_PROPERTY, 0));
		if ((dimension.width!=0) && (dimension.height!=0)) frame.setSize(dimension);
	}
	
	/** Tests whether the application frame is resizable or not.
	 * <br>By default, the frame is resizable, you should override this method to return false if you want the frame being not resizable. 
	 * @return true if the false is resizable.
	 */
	protected boolean isFrameResizable() {
		return true;
	}

	/** Creates the main panel.
	 * <br>Be aware that the returned panel should be instantiated by this method and not before (for example in the constructor).
	 * It should be instantiated there because, before, the look and feel may not have been set.
	 * @return The panel that will by displayed in the application frame.
	 */
	protected abstract JPanel buildMainPanel();
	
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

	protected void start() {
		frame = new JFrame();
		frame.setTitle(getName());
		frame.add(buildMainPanel());
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				saveState();
				event.getWindow().dispose();
			}
		});
		// CheckNewReleaseAction.doAutoCheck(frame);
		frame.setJMenuBar(buildMenuBar());
		frame.setResizable(isFrameResizable());
		frame.pack();
		frame.setMinimumSize(frame.getSize());
		restoreState();
		frame.setVisible(true);
	}
	
	/** Gets the main application frame.
	 * @return a JFrame
	 */
	protected JFrame getJFrame() {
		return this.frame;
	}
	
	/** Quits the application.
	 * <br>By default this method closes the main application frame.
	 * You should override this method if you have to close other frames.
	 */
	protected void quit() {
		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
	}
}
