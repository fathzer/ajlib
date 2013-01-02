package net.astesana.ajlib.swing.widget;

import java.awt.GridBagLayout;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import javax.swing.JLabel;

import java.awt.GridBagConstraints;
import java.awt.Insets;

/** A very basic panel, composed of a header with an icon at its north and a component in the center.
 * @author Jean-Marc Astesana
 * @param <T> The class of the data used to fill the center panel.
 */
public abstract class AbstractTitledPanel<T> extends JPanel {
	private static final long serialVersionUID = 1L;
	private JPanel northPanel = null;
	private JLabel iconLabel = null;
	private JLabel textLabel = null;
	
	protected T data;

	/**
	 * Constructor.
	 * @param headerMessage The header message
	 * @param headerIcon The header icon (null for no icon)
	 * @param data The center component's data
	 */
	public AbstractTitledPanel(String headerMessage, Icon headerIcon, T data) {
		super();
		this.data = data;
		initialize();
		this.setHeaderMessage(headerMessage);
		this.setHeaderIcon(headerIcon);
	}

	/**
	 * This method initializes this
	 */
	private void initialize() {
		this.setLayout(new BorderLayout());
		this.add(getNorthPanel(), BorderLayout.NORTH);
		this.add(getCenterComponent(), BorderLayout.CENTER);
	}

	/**
	 * This method initializes northPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getNorthPanel() {
		if (northPanel == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 1;
			gridBagConstraints1.weightx = 1.0D;
			gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints1.gridy = 0;
			textLabel = new JLabel();
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.anchor = GridBagConstraints.WEST;
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridy = 0;
			northPanel = new JPanel();
			northPanel.setLayout(new GridBagLayout());
			northPanel.add(getIconLabel(), gridBagConstraints);
			northPanel.add(textLabel, gridBagConstraints1);
		}
		return northPanel;
	}

	/** Sets the header message.
	 * @param message The message
	 */
	public void setHeaderMessage(String message) {
		this.textLabel.setText(message);
	}
	
	/** Gets the header message.
	 * @return a String
	 */
	public String getHeaderMessage() {
		return this.textLabel.getText();
	}

	/**
	 * This method initializes iconLabel	
	 * 	
	 * @return javax.swing.JLabel	
	 */
	private JLabel getIconLabel() {
		if (iconLabel == null) {
			iconLabel = new JLabel();
		}
		return iconLabel;
	}
	
	/** Gets the icon.
	 * @return a Icon
	 */
	public Icon getHeaderIcon() {
		return this.getIconLabel().getIcon();
	}
	
	/** Sets the icon.
	 * <br>By default, the icon is null (no icon). You may want to set it to some of the standard dialog icons (example UIManager.getIcon("OptionPane.informationIcon"))
	 * @param icon null to remove the icon
	 */
	public void setHeaderIcon(Icon icon) {
		this.getIconLabel().setIcon(icon);
	}

	/**
	 * Gets the center Component.	
	 * <br>Please note that this method should always return the same JComponent instance.
	 * @return a JComponent
	 */
	public abstract JComponent getCenterComponent();
}  //  @jve:decl-index=0:visual-constraint="10,10"
