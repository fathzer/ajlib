package net.astesana.ajlib.swing;

import java.awt.Component;
import java.awt.Image;
import java.awt.Window;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/** Some Swing utilities.
 * @author Jean-Marc Astesana
 * <BR>License : GPL v3
 */
public class Utils {
	/** Gets the window which contains a component.
	 * <br>Note that this method, unlike java.swing.SwingUtilities.getAncestor, this method returns the invoker window if the component is a menu item
	 * (java.swing.SwingUtilities.getAncestor returns null in such a case).
	 * @param component the component
	 * @return The window containing the component or null if no window contains the component.
	 */
	public static Window getOwnerWindow(Component component) {
		while ((component!=null) && !(component instanceof Window)) {
			if (component instanceof JPopupMenu) {
				component = ((JPopupMenu)component).getInvoker();
			} else {
				component = component.getParent();
			}
		}
		return (Window) component;
	}
	
	@Deprecated
	/** Centers a window relatively to another.
	 * @param window The window to center
	 * @param reference The reference window
	 * @see Window#setLocationRelativeTo(Component)
	 */
	public static void centerWindow(Window window, Window reference) {
		window.setLocationRelativeTo(reference);
	}
	
	/** Packs the columns of a JTable.
	 * @param table The Jtable to pack
	 * @param margin the margin in pixels
	 * @see #packColumn(JTable, int, int)
	 */
	public static void packColumns(JTable table, int margin) {
		for (int i = 0; i < table.getColumnCount(); i++) {
			packColumn(table, i, margin);
		}
	}
	
	/** Sets the preferred width of the visible column specified by vColIndex.
	 * <br>The column will be just wide enough to show the column head and the widest cell in the column.
	 * margin pixels are added to the left and right (resulting in an additional width of 2*margin pixels).
	 * @param table a table
	 * @param vColIndex the column index (model based not view based)
	 * @param margin The cells margin
	 * @return The width of the column. 
	 */
	public static int packColumn(JTable table, int vColIndex, int margin) {
		TableColumnModel colModel = table.getColumnModel();
		TableColumn col = colModel.getColumn(vColIndex);
		int width = 0; // Will get width of column
		//Get width of column header
		TableCellRenderer renderer = col.getHeaderRenderer();
		if (renderer == null) {
			renderer = table.getTableHeader().getDefaultRenderer();
		}
		Component comp = renderer.getTableCellRendererComponent(table, col.getHeaderValue(), false, false, 0, 0);
		width = comp.getPreferredSize().width;

		//Get maximum width of column data
		for (int r=0; r<table.getRowCount(); r++) {
			renderer = table.getCellRenderer(r, vColIndex);
			comp = renderer.getTableCellRendererComponent( table, table.getValueAt(r, vColIndex), false, false, r, vColIndex);
			width = Math.max(width, comp.getPreferredSize().width); 
		}
		
		// Add margin
		width += 2*margin;
		
		// Set the width
		col.setPreferredWidth(width);
		return width;
	}
	
	/** Gets an icon by its URL and resize it if needed.
	 * @param path The absolute icon path resource
	 * @param size the required size
	 * @return the icon or null if the path is 
	 */
	public static Icon createIcon(URL path, int size) {
    ImageIcon imageIcon = new ImageIcon(path);
		Image img = imageIcon.getImage();
		int currentSize = imageIcon.getIconHeight();
		if (size!=currentSize) {
		  imageIcon = new ImageIcon(img.getScaledInstance(size, size, java.awt.Image.SCALE_SMOOTH));
		}
		return imageIcon;
	}
}
