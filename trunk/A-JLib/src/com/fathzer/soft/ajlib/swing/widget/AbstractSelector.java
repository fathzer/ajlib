package com.fathzer.soft.ajlib.swing.widget;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.JPanel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import com.fathzer.soft.ajlib.swing.Utils;
import com.fathzer.soft.ajlib.utilities.NullUtils;


/** An abstract widget composed of an optional label, a combo box and a new button.
 * <br>It is typically used to select a value in a list of possible values.
 * <br>This widget defines a property that reflects the selection changes. Its name is defined by the method getPropertyName.
 * <br>It also allows the combo box display to be customized using method getDefaultRenderedValue.
 * @param <T> The class of the items selected by the selector.
 * @param <V> The class of the constructor argument.
 * @see #getPropertyName()
 * @see #getDefaultRenderedValue(Object)
 * @author Jean-Marc Astesana
 * <BR>License: LGPL v3
 */
public abstract class AbstractSelector<T,V> extends JPanel {
	private static final long serialVersionUID = 1L;
	private JLabel jLabel;
	private ComboBox combo;
	private JButton newButton;

	private T lastSelected;
	private V parameters;

	/**
	 * Constructor.
	 * @param parameters The init parameters of the selector (that object will be used to populate the combo).
	 */
	public AbstractSelector(V parameters) {
		this.parameters = parameters;
		initialize();
		internalPopulate();
		this.lastSelected = get();
	}

	private void internalPopulate() {
		getCombo().setActionEnabled(false);
		getCombo().removeAllItems();
		populateCombo();
		getCombo().setActionEnabled(true);
	}
	
	protected Icon getNewButtonIcon() {
		return null;
	}
	
	/** Gets the parameters of the widget.
	 * @return The argument passed to the constructor.
	 * @see #AbstractSelector(Object)
	 */
	public V getParameters() {
		return this.parameters;
	}
	
	/** Sets the parameters of the widget.
	 * <br>The refresh method is called.
	 * @param parameters The new parameters
	 */
	public void setParameters(V parameters) {
		this.parameters = parameters;
		refresh();
	}
	
	/** Populates the combo.
	 * <br>You should override this method to define how the combo is populated.
	 * <br>Usually, you will use the getParameters method in order to retrieve the argument of the constructor.
	 * <br>Note that this method is always called with a empty combo.
	 * @see #getParameters()
	 */
	protected abstract void populateCombo();
	
	/** Refreshes the widget when the parameters has changed.
	 * <br>This method is called when the widget parameters are changed by setParameters.
	 * <br>It removes all old combo items, then calls populateCombo and setSelectionAfterRefresh.
	 * <br>A property change is thrown if the selection changes (and the combo action is not disabled).
	 * <br>The actionEnabled attribute of the combo is unchanged.
	 * @see ComboBox#setActionEnabled(boolean)
	 * @see #populateCombo()
	 * @see #setSelectionAfterRefresh(Object)
	 */
	public void refresh() {
		T old = get();
		boolean oldEnabled = getCombo().isActionEnabled(); 
		getCombo().setActionEnabled(false);
		getCombo().removeAllItems();
		populateCombo();
		getCombo().setActionEnabled(oldEnabled);
		setSelectionAfterRefresh(old);
	}

	/** Sets the combo selection during a refresh.
	 * <br>By default, this method selects the last item selected, if it is still available.
	 * @param old the last selected item before refresh was performed.
	 */
	protected void setSelectionAfterRefresh(T old) {
		if (getCombo().contains(old)) {
			getCombo().setSelectedItem(old);
		}
	}

	private void initialize() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		setLayout(gridBagLayout);
		GridBagConstraints gbcJLabel = new GridBagConstraints();
		String label = getLabel();
		gbcJLabel.insets = new Insets(0, 0, 0, 5);
		gbcJLabel.anchor = GridBagConstraints.WEST;
		gbcJLabel.gridx = 0;
		gbcJLabel.gridy = 0;
		add(getJLabel(), gbcJLabel);
		GridBagConstraints gbcCombo = new GridBagConstraints();
		gbcCombo.weightx = 1.0;
		gbcCombo.fill = GridBagConstraints.HORIZONTAL;
		gbcCombo.gridx = 1;
		gbcCombo.gridy = 0;
		add(getCombo(), gbcCombo);
		GridBagConstraints gbcNewButton = new GridBagConstraints();
		gbcNewButton.gridx = 2;
		gbcNewButton.gridy = 0;

		add(getNewButton(), gbcNewButton);

		Dimension dimension = getCombo().getPreferredSize();
		getNewButton().setPreferredSize(new Dimension(dimension.height, dimension.height));

		if (label!=null) {
			getJLabel().setText(getLabel());
		}
		if (getComboTip()!=null) {
			getCombo().setToolTipText(getComboTip());
		}
		if (getNewButtonTip()!=null) {
			getNewButton().setToolTipText(getNewButtonTip());
		}
	}

	/** Gets the label.
	 * @return a JLabel
	 */
	public JLabel getJLabel() {
		if (jLabel == null) {
			jLabel = new JLabel();
		}
		return jLabel;
	}
	
	/** Gets the new button.
	 * @return a JButton
	 */
	public JButton getNewButton() {
		if (newButton == null) {
			newButton = new JButton();
			Icon icon = getNewButtonIcon();
			newButton = new JButton(icon);
			newButton.setFocusable(false);
			newButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					T c = createNew();
					if (c != null) {
						internalPopulate();
						getCombo().setSelectedItem(c);
						Utils.getOwnerWindow(AbstractSelector.this).pack();
					}
				}
			});
			newButton.setVisible(isNewButtonVisible());
		}
		return newButton;
	}
	
	protected boolean isNewButtonVisible() {
		return true;
	}

	/** Gets the ComboBox.
	 * @return a CoolJComboBox.
	 */
	public ComboBox getCombo() {
		if (combo == null) {
			combo = new ComboBox();
			combo.setRenderer(new Renderer());
			combo.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					T old = lastSelected;
					lastSelected = get();
					if (!NullUtils.areEquals(old, lastSelected)) {
						firePropertyChange(getPropertyName(), old, lastSelected);
					}
				}
			});
		}
		return combo;
	}
	
	@SuppressWarnings("serial")
	private class Renderer extends DefaultListCellRenderer {
		@SuppressWarnings("unchecked")
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			T v = (T)value;
			Component renderer = super.getListCellRendererComponent(list, getDefaultRenderedValue(v), index, isSelected, cellHasFocus);
			return getCustomizedRenderer (renderer, v, index, isSelected, cellHasFocus);
    }
	}
	
	/** Returns a customized renderer used to display a specific value.
	 * <br>Please note that this method is never called when combo's renderer is set by getCombo().setRenderer(...) method.
	 * @param renderer The default renderer
	 * @param value The value to be rendered
	 * @param index The index of the value in the combo
	 * @param isSelected True if the value is selected 
	 * @param cellHasFocus True if the value has the focus
	 * @return a customized renderer, by default this method returns the renderer argument. 
	 */
	protected Component getCustomizedRenderer (Component renderer, T value, int index, boolean isSelected, boolean cellHasFocus) {
		return renderer;
	}
	
	/** Returns the default value displayed in the combo box.
	 * <br><i><b>Default</b></i> means that this value is used by the default renderer. If you define your own ListCellRenderer, this method will never been called.
	 * The default implementation returns the value itself (the default renderer will display the toString of that object).
	 * You may override this method in order to customized the combo box displayed
	 * @param value The value to be displayed (it can be null)
	 * @return The value that will be displayed.
	 */
	protected Object getDefaultRenderedValue (T value) {
		return value;
	}
	
	/** Creates a new element.
	 *  This method is called when the new button is clicked. It should ask for the new instance to be created, then, adds it to the object used
	 *  by method populateCombo, then, returns the created object.
	 *  @return The created object, null if the object creation was cancelled.
	 */
	protected abstract T createNew();
	
	/** Gets the widget's label.
	 * @return a String, null to have no label.
	 */
	protected String getLabel() {
		return null;
	}
	
	/** Gets the combo's initial tooltip.
	 * @return a String, null to have no tooltip.
	 */
	protected String getComboTip() {
		return null;
	}
	
	/** Gets the new button's initial tooltip.
	 * @return a String, null to have no tooltip.
	 */
	protected String getNewButtonTip() {
		return null;
	}
	
	/** Gets the name of the property that changes when the selection changes.
	 * @return a String
	 */
	protected abstract String getPropertyName(); 
	
	/** Gets the selected value.
	 * @return the selected value.
	 */
	@SuppressWarnings("unchecked")
	public T get() {
		return (T)getCombo().getSelectedItem();
	}
	
	/** Sets the selected value.
	 * @param value The value to select
	 */
	public void set(T value) {
		T oldValue = this.get();
		if (!NullUtils.areEquals(value,oldValue)) {
			getCombo().setSelectedItem(value);
		}
	}
	
	/** Sets the combo tooltip.
	 * <br>This method is a shortcut to this.getCombo().setToolTipText(text)
	 * @param tip The tooltip text.
	 */
	public void setToolTipText(String tip) {
		getCombo().setToolTipText(tip);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		getJLabel().setEnabled(enabled);
		getNewButton().setEnabled(enabled);
		getCombo().setEnabled(enabled);
	}
}
