package net.astesana.ajlib.swing.demo;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import net.astesana.ajlib.swing.widget.date.DateWidget;
import net.astesana.ajlib.swing.widget.date.DateField;
import net.astesana.ajlib.swing.widget.date.CalendarWidget;
import javax.swing.border.TitledBorder;

@SuppressWarnings("serial")
public class DateDemoPanel extends JPanel {
	private DateField textField;

	/**
	 * Create the panel.
	 */
	public DateDemoPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		setLayout(gridBagLayout);
		
		JLabel lblNewLabel = new JLabel("DateWidget :");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 5, 5, 5);
		gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		add(lblNewLabel, gbc_lblNewLabel);
		
		DateWidget panel = new DateWidget();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.anchor = GridBagConstraints.NORTHWEST;
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 0;
		add(panel, gbc_panel);
		
		JLabel lblDatefield = new JLabel("DateField :");
		GridBagConstraints gbc_lblDatefield = new GridBagConstraints();
		gbc_lblDatefield.anchor = GridBagConstraints.WEST;
		gbc_lblDatefield.insets = new Insets(0, 5, 5, 5);
		gbc_lblDatefield.gridx = 0;
		gbc_lblDatefield.gridy = 1;
		add(lblDatefield, gbc_lblDatefield);
		
		textField = new DateField();
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 0);
		gbc_textField.anchor = GridBagConstraints.WEST;
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 1;
		add(textField, gbc_textField);
		textField.setColumns(10);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "CalendarWidget", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.anchor = GridBagConstraints.NORTHWEST;
		gbc_panel_1.weighty = 1.0;
		gbc_panel_1.weightx = 1.0;
		gbc_panel_1.gridwidth = 2;
		gbc_panel_1.insets = new Insets(0, 0, 0, 5);
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 2;
		add(panel_1, gbc_panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[]{0, 0, 0};
		gbl_panel_1.rowHeights = new int[]{0, 0, 0};
		gbl_panel_1.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gbl_panel_1.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		panel_1.setLayout(gbl_panel_1);
		
		CalendarWidget CalendarWidget = new CalendarWidget();
		GridBagConstraints gbc_CalendarWidget = new GridBagConstraints();
		gbc_CalendarWidget.anchor = GridBagConstraints.WEST;
		gbc_CalendarWidget.insets = new Insets(0, 0, 5, 0);
		gbc_CalendarWidget.gridwidth = 2;
		gbc_CalendarWidget.gridx = 0;
		gbc_CalendarWidget.gridy = 0;
		panel_1.add(CalendarWidget, gbc_CalendarWidget);

	}

}
