package com.fathzer.soft.ajlib.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.prefs.Preferences;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * Some Swing utilities.
 * 
 * @author Jean-Marc Astesana <BR>
 *         License: LGPL v3
 */
public abstract class Utils {
	private Utils() {
		//To prevent instance to be constructed
	}

	/**
	 * Gets the window which contains a component. <br>
	 * Note that this method, unlike java.swing.SwingUtilities.getAncestor, this
	 * method returns the invoker window if the component is a menu item
	 * (java.swing.SwingUtilities.getAncestor returns null in such a case).
	 * 
	 * @param component
	 *            the component
	 * @return The window containing the component or null if no window contains
	 *         the component.
	 */
	public static Window getOwnerWindow(Component component) {
		while ((component != null) && !(component instanceof Window)) {
			if (component instanceof JPopupMenu) {
				component = ((JPopupMenu) component).getInvoker();
			} else {
				component = component.getParent();
			}
		}
		return (Window) component;
	}

	/** Centers a window relatively to another.
	 * @param window The window to center
	 * @param reference The reference window
	 * @see Window#setLocationRelativeTo(Component)
	 * @deprecated
	 */
	@Deprecated
	public static void centerWindow(Window window, Window reference) {
		window.setLocationRelativeTo(reference);
	}

	/**
	 * Packs the columns of a JTable.
	 * 
	 * @param table
	 *            The Jtable to pack
	 * @param margin
	 *            the margin in pixels
	 * @see #packColumn(JTable, int, int)
	 */
	public static void packColumns(JTable table, int margin) {
		for (int i = 0; i < table.getColumnCount(); i++) {
			packColumn(table, i, margin);
		}
	}

	/**
	 * Sets the preferred width of the visible column specified by vColIndex. <br>
	 * The column will be just wide enough to show the column head and the
	 * widest cell in the column. margin pixels are added to the left and right
	 * (resulting in an additional width of 2*margin pixels).
	 * 
	 * @param table
	 *            a table
	 * @param colIndex
	 *            the column index (model based not view based)
	 * @param margin
	 *            The cells margin
	 * @return The width of the column or -1 if the column is not displayed at
	 *         this time.
	 */
	public static int packColumn(JTable table, int colIndex, int margin) {
		TableColumnModel colModel = table.getColumnModel();
		int viewIndex = table.convertColumnIndexToView(colIndex);
		if (viewIndex < 0) {
			return -1;
		}
		TableColumn col = colModel.getColumn(viewIndex);
		int width = 0; // Will get width of column
		// Get width of column header
		TableCellRenderer renderer = col.getHeaderRenderer();
		if (renderer == null) {
			renderer = table.getTableHeader().getDefaultRenderer();
		}
		Component comp = renderer.getTableCellRendererComponent(table,
				col.getHeaderValue(), false, false, 0, 0);
		width = comp.getPreferredSize().width;

		// Get maximum width of column data
		for (int r = 0; r < table.getRowCount(); r++) {
			renderer = table.getCellRenderer(r, colIndex);
			comp = renderer.getTableCellRendererComponent(table,
					table.getValueAt(r, colIndex), false, false, r, colIndex);
			width = Math.max(width, comp.getPreferredSize().width);
		}

		// Add margin
		width += 2 * margin;

		// Set the width
		col.setPreferredWidth(width);
		return width;
	}

	/**
	 * Gets an icon by its URL and resize it if needed.
	 * 
	 * @param path
	 *            The absolute icon path resource
	 * @param size
	 *            the required size
	 * @return the icon or null if the path is
	 */
	public static Icon createIcon(URL path, int size) {
		ImageIcon imageIcon = new ImageIcon(path);
		int currentSize = imageIcon.getIconHeight();
		if (size != currentSize) {
			Image img = imageIcon.getImage();
			imageIcon = new ImageIcon(img.getScaledInstance(size, size, java.awt.Image.SCALE_SMOOTH));
		}
		return imageIcon;
	}

	public static Icon createIcon(URL path, float sizeRatio) {
		ImageIcon imageIcon = new ImageIcon(path);
		if (sizeRatio != 1.0f) {
			Image img = imageIcon.getImage();
			imageIcon = new ImageIcon(img.getScaledInstance(
					(int) (imageIcon.getIconWidth() * sizeRatio),
					(int) (imageIcon.getIconHeight() * sizeRatio),
					java.awt.Image.SCALE_SMOOTH));
		}
		return imageIcon;
	}

	/**
	 * Returns a utility MouseListener to set custom toolTip delays on
	 * component. <BR>
	 * To use custom delays, add the return adapter as a MouseListener to the
	 * component.
	 * <code>component.addMouseListener(Utils.getToolTipAdpater(...))</code>
	 * 
	 * @param initialDelay
	 *            see javax.swing.ToolTipManager documentation. A negative int
	 *            to use the default delay.
	 * @param dismissDelay
	 *            see javax.swing.ToolTipManager documentation. A negative int
	 *            to use the default delay. Zero to disable the tooltip.
	 * @param reshowDelay
	 *            see javax.swing.ToolTipManager documentation. A negative int
	 *            to use the default delay.
	 * @return a MouseListener
	 */
	public static MouseListener getToolTipAdapter(final int initialDelay,
			final int dismissDelay, final int reshowDelay) {
		return new MouseAdapter() {
			private int originalDismissDelay;
			private int originalInitialDelay;
			private int originalReshowDelay;
			private boolean originalEnabled;

			@Override
			public void mouseEntered(MouseEvent e) {
				ToolTipManager ttm = ToolTipManager.sharedInstance();
				this.originalInitialDelay = ttm.getInitialDelay();
				this.originalDismissDelay = ttm.getDismissDelay();
				this.originalReshowDelay = ttm.getReshowDelay();
				this.originalEnabled = ttm.isEnabled();
				if (dismissDelay > 0) {
					ttm.setDismissDelay(dismissDelay);
				} else if (dismissDelay == 0) {
					ttm.setEnabled(false);
				}
				if (initialDelay >= 0) {
					ttm.setInitialDelay(initialDelay);
				}
				if (reshowDelay >= 0) {
					ttm.setReshowDelay(reshowDelay);
				}
				super.mouseEntered(e);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				ToolTipManager ttm = ToolTipManager.sharedInstance();
				ttm.setInitialDelay(originalInitialDelay);
				ttm.setDismissDelay(originalDismissDelay);
				ttm.setReshowDelay(originalReshowDelay);
				ttm.setEnabled(originalEnabled);
				super.mouseExited(e);
			}
		};
	}
	
	/** Converts a look and feel name to its class name.
	 * @param lookAndFeelName The name of the look and feel
	 * @return The class name of the look and feel or null if the LAF is unknown.
	 */
	public static String getLFClassFromName(String lookAndFeelName) {
		LookAndFeelInfo[] installedLookAndFeels = UIManager.getInstalledLookAndFeels();
		String lookAndFeelClass = null;
		for (LookAndFeelInfo lookAndFeelInfo : installedLookAndFeels) {
			if (lookAndFeelInfo.getName().equals(lookAndFeelName)) {
				lookAndFeelClass = lookAndFeelInfo.getClassName();
				break;
			}
		}
		return lookAndFeelClass;
	}
	
	private static final String LOCATION_Y_PROPERTY = "y"; //$NON-NLS-1$
	private static final String LOCATION_X_PROPERTY = "x"; //$NON-NLS-1$
	private static final String SIZE_X_PROPERTY = "size.x"; //$NON-NLS-1$
	private static final String SIZE_Y_PROPERTY = "size.y"; //$NON-NLS-1$
	
	/** Saves the state (size, position, maximized/minimized state) of a frame.
	 * @param frame The frame we want to save the state
	 * @param prefs The preferences where to save the state
	 * @see #restoreState(Frame, Preferences)
	 */
	public static void saveState(Frame frame, Preferences prefs) {
		prefs.put(LOCATION_X_PROPERTY, Integer.toString(frame.getLocation().x));
		prefs.put(LOCATION_Y_PROPERTY, Integer.toString(frame.getLocation().y));

		Dimension size = frame.getSize();
		int h = ((frame.getExtendedState() & Frame.MAXIMIZED_VERT) == 0) ? size.height : -1;
		int w = ((frame.getExtendedState() & Frame.MAXIMIZED_HORIZ) == 0) ? size.width : -1;
		
		prefs.put(SIZE_X_PROPERTY, Integer.toString(w));
		prefs.put(SIZE_Y_PROPERTY, Integer.toString(h));
	}

	/** Restores the state (size, position, maximized/minimized state) of a frame.
	 * <br>State should have been previously saved with {@link #saveState}.
	 * @param frame The frame we want to save the state
	 * @param prefs The preferences where to save the state
	 * @see #saveState(Frame, Preferences)
	 */
	public static void restoreState(Frame frame, Preferences prefs) {
		Dimension dimension = new Dimension(prefs.getInt(SIZE_X_PROPERTY, 0), prefs.getInt(SIZE_Y_PROPERTY, 0));
		Point location = new Point(prefs.getInt(LOCATION_X_PROPERTY, 0), prefs.getInt(LOCATION_Y_PROPERTY, 0));
		setSafeBounds(frame, new Rectangle(location, dimension));
	}

	/** Carefully sets the rectangle of a frame.
	 * <br>This means it ensure the frame is visible on the screen.
	 * @param frame The frame to resize/move
	 * @param bounds The future frame rectangle. If its height or its width is &lt; 0, the frame is maximized vertically/horizontally.
	 * If it is not visible on the current available screens, the frame's location is set to the middle of the default screen.
	 */
	public static void setSafeBounds(Frame frame, Rectangle bounds) {
		final Dimension dimension = bounds.getSize();
		int extendedState = Frame.NORMAL;
		if (dimension.width!=0 && dimension.height!=0) {
			if (dimension.height<0) {
				extendedState = extendedState | Frame.MAXIMIZED_VERT;
				dimension.height = frame.getSize().height;
			}
			if (dimension.width<0) {
				extendedState = extendedState | Frame.MAXIMIZED_HORIZ;
				dimension.width = frame.getSize().width;
			}
			frame.setSize(dimension);
			frame.setExtendedState(extendedState);
		}
		final Point location;
		if (!isVisible(bounds)) {
			final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			location = new Point(screenSize.width/4, screenSize.height/4);
		} else {
			location = bounds.getLocation();
		}
		frame.setLocation(location);
	}
    
    public static boolean isVisible(Rectangle rec) {
        for (GraphicsDevice gd : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
            Rectangle bounds = gd.getDefaultConfiguration().getBounds();
            if (bounds.intersects(rec)) {
                return true;
            }
        }
        return false;
    }
}
