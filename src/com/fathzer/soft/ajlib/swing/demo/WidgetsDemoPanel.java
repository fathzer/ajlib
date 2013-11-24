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
		GridBagConstraints gbcLblTextwidget = new GridBagConstraints();
		gbcLblTextwidget.fill = GridBagConstraints.BOTH;
		gbcLblTextwidget.insets = new Insets(0, 0, 5, 5);
		gbcLblTextwidget.gridx = 0;
		gbcLblTextwidget.gridy = 0;
		add(getLblTextwidget(), gbcLblTextwidget);
		GridBagConstraints gbcTextWidget = new GridBagConstraints();
		gbcTextWidget.fill = GridBagConstraints.HORIZONTAL;
		gbcTextWidget.insets = new Insets(0, 0, 5, 0);
		gbcTextWidget.gridx = 1;
		gbcTextWidget.gridy = 0;
		add(getTextWidget(), gbcTextWidget);
		GridBagConstraints gbcLblNumberWidget = new GridBagConstraints();
		gbcLblNumberWidget.anchor = GridBagConstraints.WEST;
		gbcLblNumberWidget.insets = new Insets(0, 0, 5, 5);
		gbcLblNumberWidget.gridx = 0;
		gbcLblNumberWidget.gridy = 1;
		add(getLblNumberWidget(), gbcLblNumberWidget);
		GridBagConstraints gbcNumberWidget = new GridBagConstraints();
		gbcNumberWidget.insets = new Insets(0, 0, 5, 0);
		gbcNumberWidget.weightx = 1.0;
		gbcNumberWidget.fill = GridBagConstraints.HORIZONTAL;
		gbcNumberWidget.gridx = 1;
		gbcNumberWidget.gridy = 1;
		add(getNumberWidget(), gbcNumberWidget);
		GridBagConstraints gbcLblNewLabel = new GridBagConstraints();
		gbcLblNewLabel.anchor = GridBagConstraints.EAST;
		gbcLblNewLabel.insets = new Insets(0, 0, 0, 5);
		gbcLblNewLabel.gridx = 0;
		gbcLblNewLabel.gridy = 2;
		add(getLblNewLabel(), gbcLblNewLabel);
		GridBagConstraints gbcCurrencyWidget = new GridBagConstraints();
		gbcCurrencyWidget.fill = GridBagConstraints.HORIZONTAL;
		gbcCurrencyWidget.gridx = 1;
		gbcCurrencyWidget.gridy = 2;
		add(getCurrencyWidget(), gbcCurrencyWidget);
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
