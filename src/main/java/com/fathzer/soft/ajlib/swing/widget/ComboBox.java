package com.fathzer.soft.ajlib.swing.widget;

import javax.swing.JComboBox;

/** This subclass of JCombo is cool because it adds some useful functionalities, often asked in the forums.
 * <UL><LI>you can prevent instance from sending action event.<BR>
 * It could be very useful when you have to refresh the menu but don't want any event being sent.</LI>
 * <LI>The method <b>contains</b> tests whether the menu contains an item or not</LI>
 * </UL>
 */
public class ComboBox<T> extends JComboBox<T> {
	private static final long serialVersionUID = 1L;

	private boolean isActionEnabled = true;
	
	/** Enables/Disables the events.
	 * <br>Note: This method doesn't throw any event.
	 * @param isActionEnabled true to enable the events.
	 */
	public void setActionEnabled(boolean isActionEnabled) {
		this.isActionEnabled = isActionEnabled;
	}
	
	/** Tests whether the events are enabled or not.
	 * @return true if events are enabled.
	 */
	public boolean isActionEnabled() {
		return isActionEnabled;
	}
	
	/** Tests whether the menu contains an item or not
	 * @param item the item to test
	 * @return true if the item is in the menu.
	 */
	public boolean contains (T item) {
		for (int i = 0; i < this.getItemCount(); i++) {
			if (this.getItemAt(i).equals(item)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void fireActionEvent() {
		if (isActionEnabled) {
			super.fireActionEvent();
		}
	}
}
