package com.fathzer.soft.ajlib.swing;

import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import javax.swing.AbstractButton;
 
/**
 * This class is, like javax.swing.ButtonGroup, used to create a multiple-exclusion scope for
 * a set of buttons. Creating a set of buttons with the same <code>ButtonGroup</code> object means that
 * turning "on" one of those buttons turns off all other buttons in the group.
 * <p>
 * The main difference between this group and the swing one is that you can deselected buttons by clicking on them
 * and have a group without no selected button (see {@link #setAutoDeselect(boolean)}).<br>
 * Another difference is that this class extends the java.util.Observable class and calls its observers update method
 * when the selected button changes. 
 * </p>
 */
public class ButtonGroup extends Observable {
	/**
	 *  The buttons list
	 */
	private List<AbstractButton> buttons;
	/**
	 * The current selection.
	 */
	private AbstractButton selected;
	private ItemListener listener;
	private boolean autoDeselect;

	/**
	 * Constructor.
	 */
	public ButtonGroup() {
		this.autoDeselect = false;
		this.buttons = new ArrayList<>();
		this.selected = null;
		this.listener = new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				AbstractButton b = (AbstractButton) e.getItem();
				if (e.getStateChange()==ItemEvent.SELECTED) {
					setSelected(b);
				} else if (selected==b) {
					if (autoDeselect) {
						setSelected(null);
					} else {
						selected.setSelected(true);
					}
				}
			}
		};
	}

	/**
	 * Adds a button to this group.
	 * @param b the button to be added
	 * @exception NullPointerException if the button is null
	 */
	public void add(AbstractButton b) {
		if (b==null) {
			throw new NullPointerException();
		}
		buttons.add(b);
		b.addItemListener(listener);
		if (b.isSelected()) {
			setSelected(b);
		}
	}

	/**
	 * Removes a button from this group.
	 * @param b the button to be removed
	 * @exception IllegalArgumentException if the button is unknown in this group
	 * @exception NullPointerException if the button is null
	 */
	public void remove(AbstractButton b) {
		if (b == null) {
			throw new NullPointerException();
		}
		if (this.buttons.remove(b)) {
			b.removeItemListener(this.listener);
			if (this.selected==b) {
				setSelected(null);
			}
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Clears the selection such that none of the buttons in the
	 * <code>ButtonGroup</code> are selected.
	 */
	public void clearSelection() {
		if (selected != null) {
			setSelected(null);
		}
	}

	/**
	 * Returns the selected button.
	 * @return the selected button
	 */
	public AbstractButton getSelected() {
		return this.selected;
	}
	
	/** Changes the selected button.
	 * @param b the button to be selected (null deselects all buttons)
	 * @exception IllegalArgumentException if the button is not in this group
	 */
	public void setSelected(AbstractButton b) {
		if (b==this.selected) {
			return;
		}
		if ((b!=null) && (!this.buttons.contains(b))) {
			throw new IllegalArgumentException();
		}
		AbstractButton old = this.selected;
		this.selected = b;
		if (b!=null) {
			b.setSelected(true);
		}
		if (old!=null) {
			old.setSelected(false);
		}
		this.setChanged();
		this.notifyObservers(this.selected);
	}
	
	/** Activates/deactivates the ability to deselect a selected button by cliking onto it.
	 * <br>The default behavior is "a click on a selected button does nothing". 
	 * @param autoDeselect true to have selected button to be deactivated when clicked
	 */
	public void setAutoDeselect(boolean autoDeselect) {
		this.autoDeselect = autoDeselect;
	}
}
