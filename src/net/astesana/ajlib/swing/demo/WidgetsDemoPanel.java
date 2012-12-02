package net.astesana.ajlib.swing.demo;

import javax.swing.JPanel;
import javax.swing.JLabel;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import net.astesana.ajlib.swing.widget.TextWidget;
import net.astesana.ajlib.swing.widget.NumberWidget;
import net.astesana.javaluator.DoubleEvaluator;

public class WidgetsDemoPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JLabel lblTextwidget;
	private TextWidget textWidget;
	private JLabel lblNumberWidget;
	private NumberWidget numberWidget;

	/**
	 * Create the panel.
	 */
	public WidgetsDemoPanel() {

		initialize();
	}
	private void initialize() {
		GridBagLayout gridBagLayout = new GridBagLayout();
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
		gbc_lblNumberWidget.anchor = GridBagConstraints.EAST;
		gbc_lblNumberWidget.insets = new Insets(0, 0, 0, 5);
		gbc_lblNumberWidget.gridx = 0;
		gbc_lblNumberWidget.gridy = 1;
		add(getLblNumberWidget(), gbc_lblNumberWidget);
		GridBagConstraints gbc_numberWidget = new GridBagConstraints();
		gbc_numberWidget.weightx = 1.0;
		gbc_numberWidget.fill = GridBagConstraints.HORIZONTAL;
		gbc_numberWidget.gridx = 1;
		gbc_numberWidget.gridy = 1;
		add(getNumberWidget(), gbc_numberWidget);
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
	@SuppressWarnings("serial")
	private NumberWidget getNumberWidget() {
		if (numberWidget == null) {
			numberWidget = new NumberWidget() {
				/* (non-Javadoc)
				 * @see net.astesana.ajlib.swing.widget.NumberWidget#parseValue(java.lang.String)
				 */
				@Override
				protected Number parseValue(String text) {
					if (text.startsWith("=")) {
						text = text.substring(1);
						Number result;
						try {
							result = new DoubleEvaluator().evaluate(text);
						} catch (IllegalArgumentException e) {
							result = null;
						}
						System.out.println ("evaluating "+text + "="+result);
						return result;
					} else {
						return super.parseValue(text);
					}
				}
			};
			numberWidget.setToolTipText("<html>This widget allows you to enter a double.<br>As in Excel, if the field starts with =, the field is evaluated as a formula</html>");
			numberWidget.setColumns(10);
			numberWidget.addPropertyChangeListener(NumberWidget.VALUE_PROPERTY, new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					AJLibDemo.setMessage("Number changed from "+evt.getOldValue()+" to "+evt.getNewValue());
				}
			});
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
}
