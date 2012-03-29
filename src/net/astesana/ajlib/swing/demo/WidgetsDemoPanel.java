package net.astesana.ajlib.swing.demo;

import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import net.astesana.ajlib.swing.widget.TextWidget;

public class WidgetsDemoPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JLabel lblTextwidget;
	private TextWidget textWidget;

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
		gbc_lblTextwidget.insets = new Insets(0, 0, 0, 5);
		gbc_lblTextwidget.gridx = 0;
		gbc_lblTextwidget.gridy = 0;
		add(getLblTextwidget(), gbc_lblTextwidget);
		GridBagConstraints gbc_textWidget = new GridBagConstraints();
		gbc_textWidget.fill = GridBagConstraints.BOTH;
		gbc_textWidget.gridx = 1;
		gbc_textWidget.gridy = 0;
		add(getTextWidget(), gbc_textWidget);
	}

	private JLabel getLblTextwidget() {
		if (lblTextwidget == null) {
			lblTextwidget = new JLabel("TextWidget :");
		}
		return lblTextwidget;
	}
	private TextWidget getTextWidget() {
		if (textWidget == null) {
			textWidget = new TextWidget(10);
			textWidget.setPredefined(new String[]{"a string", "another one", "dummy", "next one"}, new int[]{2});
			textWidget.addPropertyChangeListener(TextWidget.TEXT_PROPERTY, new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					AJLibDemo.setMessage("Text changed from "+evt.getOldValue()+" to "+evt.getNewValue());
				}
			});
		}
		return textWidget;
	}
}
