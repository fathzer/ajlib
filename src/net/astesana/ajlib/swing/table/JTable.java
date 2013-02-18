package net.astesana.ajlib.swing.table;

import javax.swing.table.TableModel;

/** A JTable that fixes the following bugs in the JTable.
 * <br>Bugs fixed:<ul>
 * <li>Unlike the original swing JTable class, the row height is set accordingly to the font (see setRowHeigth() method).
 * <br>The method setRowHeight is called in the constructor. You should call it again when changing the font.</li>
 * </ul>
 * @author Jean-Marc Astesana
 * Licence GPL v3
 */
@SuppressWarnings("serial")
public class JTable extends javax.swing.JTable {
	public JTable() {
		super();
		setRowHeight();
	}
	
	public JTable(TableModel tableModel) {
		super(tableModel);
		setRowHeight();
	}
	
	/** Sets the row height according to the font size.
	 * <br>The row height is set to getFont().getSize()*4/3.
	 */
	public void setRowHeight() {
		setRowHeight(getFont().getSize()*4/3);
	}
}
