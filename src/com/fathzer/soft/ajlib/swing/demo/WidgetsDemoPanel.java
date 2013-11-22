package com.fathzer.soft.ajlib.swing.demo;

import javax.swing.JPanel;
import javax.swing.JLabel;

import com.fathzer.soft.ajlib.swing.widget.CurrencyWidget;
import com.fathzer.soft.ajlib.swing.widget.NumberWidget;
import com.fathzer.soft.ajlib.swing.widget.TextWidget;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;


public class WidgetsDemoPanel extends JPanel {
	private static final class DefaultPropertyChangeListener implements PropertyChangeListener {
		private String what;
		private DefaultPropertyChangeListener(String what) {
			this.what = what;
		}
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			String message;
			if (evt.getOldValue()==null) {
				message = MessageFormat.format("{0} is set to {1}", what, evt.getNewValue());
			} else if (evt.getNewValue()==null) {
				message = MessageFormat.format("{0} is wrong", what);
			} else {
				message = MessageFormat.format("{0} changed from {1} to {2}", what, evt.getOldValue(), evt.getNewValue());
			}
			AJLibDemo.setMessage(message);
		}
	}
	private static final long serialVersionUID = 1L;
	private JLabel lblTextwidget;
	private TextWidget textWidget;
	private JLabel lblNumberWidget;
	private NumberWidget numberWidget;
	private JLabel lblNewLabel;
	private CurrencyWidget currencyWidget;

	/**
	 * Create the panel.
	 */
	public WidgetsDemoPanel() {

		initialize();
	}
	private void initialize() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[]{0.0, 1.0};
		setLayout(gridBagLayout);
		GridBagConstraints gbc_lblTextwidget = new GridBagConstraints();
		gbc_lblTextwidget.fill = GridBagConstraints.BOTH;
		gbc_lblTextwidget.insets = new Insets(0, 0, 5, 5);
		gbc_lblTextwidget.gridx = 0;
		gbc_lblTextwidget.gridy = 0;
		add(getLblTextwidget(), gbc_lblTextwidget);
		GridBagConstraints gbc_textWidget = new GridBagConstraints();
		gbc_textWidget.fill = GridBagConstraints.HORIZONTAL;
		gbc_textWidget.insets = new Insets(0, 0, 5, 0);
		gbc_textWidget.gridx = 1;
		gbc_textWidget.gridy = 0;
		add(getTextWidget(), gbc_textWidget);
		GridBagConstraints gbc_lblNumberWidget = new GridBagConstraints();
		gbc_lblNumberWidget.anchor = GridBagConstraints.WEST;
		gbc_lblNumberWidget.insets = new Insets(0, 0, 5, 5);
		gbc_lblNumberWidget.gridx = 0;
		gbc_lblNumberWidget.gridy = 1;
		add(getLblNumberWidget(), gbc_lblNumberWidget);
		GridBagConstraints gbc_numberWidget = new GridBagConstraints();
		gbc_numberWidget.insets = new Insets(0, 0, 5, 0);
		gbc_numberWidget.weightx = 1.0;
		gbc_numberWidget.fill = GridBagConstraints.HORIZONTAL;
		gbc_numberWidget.gridx = 1;
		gbc_numberWidget.gridy = 1;
		add(getNumberWidget(), gbc_numberWidget);
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 2;
		add(getLblNewLabel(), gbc_lblNewLabel);
		GridBagConstraints gbc_currencyWidget = new GridBagConstraints();
		gbc_currencyWidget.fill = GridBagConstraints.HORIZONTAL;
		gbc_currencyWidget.gridx = 1;
		gbc_currencyWidget.gridy = 2;
		add(getCurrencyWidget(), gbc_currencyWidget);
	}

	private JLabel getLblTextwidget() {
		if (lblTextwidget == null) {
			lblTextwidget = new JLabel("TextWidget:");
		}
		return lblTextwidget;
	}
	private TextWidget getTextWidget() {
		if (textWidget == null) {
			textWidget = new TextWidget(10);
			textWidget.setPredefined(new String[]{"dummy", "this string", "another one", "next one", "last one", "One more"}, 2);
			textWidget.addPropertyChangeListener(TextWidget.TEXT_PROPERTY, new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					AJLibDemo.setMessage("Text changed from "+evt.getOldValue()+" to "+evt.getNewValue());
				}
			});
		}
		return textWidget;
	}
	private JLabel getLblNumberWidget() {
		if (lblNumberWidget == null) {
			lblNumberWidget = new JLabel("Number widget:");
		}
		return lblNumberWidget;
	}
	private NumberWidget getNumberWidget() {
		if (numberWidget == null) {
			numberWidget = new NumberWidget();
			numberWidget.setToolTipText("<html>This widget allows you to enter a double.</html>");
			numberWidget.setColumns(10);
			numberWidget.addPropertyChangeListener(NumberWidget.VALUE_PROPERTY, new DefaultPropertyChangeListener("Number"));
			numberWidget.addPropertyChangeListener(NumberWidget.CONTENT_VALID_PROPERTY, new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					Color color = (Boolean) evt.getNewValue()?Color.WHITE:Color.RED;
					numberWidget.setBackground(color);
				}
			});
		}
		return numberWidget;
	}
	private JLabel getLblNewLabel() {
		if (lblNewLabel == null) {
			lblNewLabel = new JLabel("Currency Widget:");
		}
		return lblNewLabel;
	}
	private CurrencyWidget getCurrencyWidget() {
		if (currencyWidget == null) {
			currencyWidget = new CurrencyWidget();
			currencyWidget.setColumns(10);
			currencyWidget.addPropertyChangeListener(NumberWidget.VALUE_PROPERTY, new DefaultPropertyChangeListener("Amount"));
			currencyWidget.addPropertyChangeListener(NumberWidget.CONTENT_VALID_PROPERTY, new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					Color color = (Boolean) evt.getNewValue()?Color.WHITE:Color.RED;
					currencyWidget.setBackground(color);
				}
			});
		}
		return currencyWidget;
	}
}
