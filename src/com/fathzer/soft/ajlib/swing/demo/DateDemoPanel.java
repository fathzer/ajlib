package com.fathzer.soft.ajlib.swing.demo;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.border.TitledBorder;

import com.fathzer.soft.ajlib.swing.widget.date.CalendarWidget;
import com.fathzer.soft.ajlib.swing.widget.date.DateField;
import com.fathzer.soft.ajlib.swing.widget.date.DateWidget;

@SuppressWarnings("serial")
public class DateDemoPanel extends JPanel {
	private DateField textField;

	/**
	 * Create the panel.
	 */
	public DateDemoPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		setLayout(gridBagLayout);
		
		JLabel lblNewLabel = new JLabel("DateWidget:");
		GridBagConstraints gbcLblNewLabel = new GridBagConstraints();
		gbcLblNewLabel.insets = new Insets(0, 5, 5, 5);
		gbcLblNewLabel.anchor = GridBagConstraints.WEST;
		gbcLblNewLabel.gridx = 0;
		gbcLblNewLabel.gridy = 0;
		add(lblNewLabel, gbcLblNewLabel);
		
		DateWidget panel = new DateWidget();
		GridBagConstraints gbcPanel = new GridBagConstraints();
		gbcPanel.insets = new Insets(0, 0, 5, 0);
		gbcPanel.anchor = GridBagConstraints.NORTHWEST;
		gbcPanel.gridx = 1;
		gbcPanel.gridy = 0;
		add(panel, gbcPanel);
		
		JLabel lblDatefield = new JLabel("DateField:");
		GridBagConstraints gbcLblDatefield = new GridBagConstraints();
		gbcLblDatefield.anchor = GridBagConstraints.WEST;
		gbcLblDatefield.insets = new Insets(0, 5, 5, 5);
		gbcLblDatefield.gridx = 0;
		gbcLblDatefield.gridy = 1;
		add(lblDatefield, gbcLblDatefield);
		
		textField = new DateField();
		GridBagConstraints gbcTextField = new GridBagConstraints();
		gbcTextField.insets = new Insets(0, 0, 5, 0);
		gbcTextField.anchor = GridBagConstraints.WEST;
		gbcTextField.gridx = 1;
		gbcTextField.gridy = 1;
		add(textField, gbcTextField);
		textField.setColumns(10);
		
		JPanel panel1 = new JPanel();
		panel1.setBorder(new TitledBorder(null, "CalendarWidget", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbcPanel1 = new GridBagConstraints();
		gbcPanel1.anchor = GridBagConstraints.NORTHWEST;
		gbcPanel1.weighty = 1.0;
		gbcPanel1.weightx = 1.0;
		gbcPanel1.gridwidth = 2;
		gbcPanel1.insets = new Insets(0, 0, 0, 5);
		gbcPanel1.gridx = 0;
		gbcPanel1.gridy = 2;
		add(panel1, gbcPanel1);
		GridBagLayout gblPanel1 = new GridBagLayout();
		panel1.setLayout(gblPanel1);
		
		CalendarWidget calendarWidget = new CalendarWidget();
		GridBagConstraints gbcCalendarWidget = new GridBagConstraints();
		gbcCalendarWidget.anchor = GridBagConstraints.WEST;
		gbcCalendarWidget.insets = new Insets(0, 0, 5, 0);
		gbcCalendarWidget.gridwidth = 2;
		gbcCalendarWidget.gridx = 0;
		gbcCalendarWidget.gridy = 0;
		panel1.add(calendarWidget, gbcCalendarWidget);

	}

}
