package com.fathzer.soft.ajlib.swing.table;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

/** This is a standard JTable mouse listener.
 * <br>It has action attributes (typically new/edit/delete/duplicate row)
 * <br>When a double click occurs on the JTable, a default action is invoked (typically an edit action).
 * <br>A pop-up menu is shown when needed (when a right click occurs under windows).
 * <br>You should register this with a JTable in order to receive interesting events (using JTable.addMouseListener).
 * <br>Please note that this class doesn't guarantee that actions will be enabled/disabled when rows are selected/deselected.
 * @author Jean-Marc Astesana
 * <BR>License: LGPL v3
 */
public class JTableListener extends MouseAdapter {
	private Action[] actions;
	private Action defaultAction;

	/** Constructor.
	 * @param actions The actions that will appear in the popupMenu. Insert a null in this array to have a separator in the pop-up menu.
	 * If actions is null or contains no item, no pop-up menu is shown.
	 * @param defaultAction The default action which will be invoked when a double click occurs. This action is not necessarily one of the actions array.
	 * If defaultAction is null, nothing occurs on a double click.
	 */
	public JTableListener(Action[] actions, Action defaultAction) {
		super();
		this.actions = actions;
		this.defaultAction = defaultAction;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		maybeShowPopup(e);
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		JTable jTable = (JTable) e.getComponent();
		if ((e.getClickCount() == 2) && (e.getButton() == MouseEvent.BUTTON1)) {
			Point p = e.getPoint();
			int row = jTable.rowAtPoint(p);
			if (row >= 0) {
				Action action = getDoubleClickAction();
				if (action != null) action.actionPerformed(new ActionEvent(e.getSource(), e.getID(), ""));
			}
		} else {
			maybeShowPopup(e);
		}
	}
	
	/** When a double click occurs, the action returned by this method is invoked.
	 * This implementation returns the defaultButton. 
	 * You can override this method in order to change this action, for instance if the JTable has a mode switch that
	 * trigger another action when it is set.
	 * @return The action to invoke.
	 */
	protected Action getDoubleClickAction() {
		return defaultAction;
	}
	
	private void maybeShowPopup(MouseEvent e) {
		JTable jTable = (JTable) e.getComponent();
		if (e.isPopupTrigger()) {
			Point p = e.getPoint();
		    int row = jTable.rowAtPoint(p);
	    	if (!jTable.isRowSelected(row)) {
		    	jTable.getSelectionModel().setSelectionInterval(row, row);
		    }
		    if ((actions!=null) && (actions.length>0)) {
				JPopupMenu popup = new JPopupMenu();
				fillPopUp(popup);
			    popup.show(e.getComponent(), e.getX(), e.getY());
		    }
		}
	}

	/** Fill the popup menu.
	 * This implementation put all the actions in the popup. You may override it in order to add
	 * more actions ... or less.
	 * @param popup the pop up to be filled.
	 */
	protected void fillPopUp(JPopupMenu popup) {
		for (int i = 0; i < actions.length; i++) {
			if (actions[i]==null) {
				popup.addSeparator();
			} else {
				popup.add(new JMenuItem(actions[i]));
			}
		}
	}
}
