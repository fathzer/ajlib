package com.fathzer.soft.ajlib.swing.widget.date;

import java.awt.GridBagLayout;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import java.awt.GridBagConstraints;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;
import java.util.Locale;

import javax.swing.JLabel;

import com.fathzer.soft.ajlib.swing.Utils;


/** This panel contains a DateField and a button that shows a calendar popup.
 * As this widget (especially the DateWidget it contains) represents years with two digits, it can only represent dates near today (ie, impossible to represent a date before 1900) 
 * @see DateField
 * @see CalendarWidget
 * @author Jean-Marc Astesana
 * <BR>License: LGPL v3
 */
public class DateWidget extends JPanel {
	private static final long serialVersionUID = 1L;
	/** Date change property name.
	 * The panel is a bean. Every time the chosen date changed, it sends a PropertyChangedEvent.
	 */
	public static final String DATE_PROPERTY = DateField.DATE_PROPERTY;  //  @jve:decl-index=0:
	/** Content validity property name.
	 * The panel is a bean. Every time the content validity changed, it sends a PropertyChangedEvent.
	 */
	public static final String CONTENT_VALID_PROPERTY = DateField.CONTENT_VALID_PROPERTY;
	
	private DateField dateField = null;
	private CalendarWidget dateChooser;
	private JPopupMenu popup;
	private JLabel jLabel = null;

	/**
	 * This is the default constructor.
	 * Creates a new panel with the system default locale.
	 * The date is set to today
	 */
	public DateWidget() {
		super();
		popup = new JPopupMenu();
		dateChooser = new CalendarWidget();
		popup.add(dateChooser);
		initialize();
		dateChooser.addPropertyChangeListener(CalendarWidget.DATE_PROPERTY, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				getDateField().setDate((Date)evt.getNewValue());
				popup.setVisible(false);
			}
		});
	}
	
	/** Get the currently chosen date.
	 * @return the currently chosen date (null if the date is wrong). It is guaranteed that the hours, minutes, seconds,
	 * milliseconds of the date are set to 0.
	 */
	public Date getDate() {
		return getDateField().getDate();
	}
	
	/** Set the currently chosen date.
	 * @param date the date to be set.
	 */
	public void setDate(Date date) {
		getDateField().setDate(date);
		// No need to fire a property change.
		// The change property event will be sent by the property change listener
		// that is waiting for change of the DateWidget
	}
	
	/** Set the number of columns of the date text field.
	 * @param nb number of columns of the text field
	 */
	public void setColumns(int nb) {
		this.getDateField().setColumns(nb);
	}
	
	/** Set the locale.
	 * Changes the calendar popup appearence and the text field format.
	 */
	public void setLocale(Locale locale) {
		getDateField().setLocale(locale);
		dateChooser.setLocale(locale);
	}

	/** Set the DateWidget tooltip.
	 * @param text the new tooltip text
	 */
	@Override
	public void setToolTipText(String text) {
		getDateField().setToolTipText(text);
	}

	/**
	 * This method initializes this
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
		gridBagConstraints11.gridx = 1;
		gridBagConstraints11.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints11.gridy = 0;
		jLabel = new JLabel();
		jLabel.setIcon(Utils.createIcon(DateWidget.class.getResource("calendar.png"), 16*getFont().getSize()/12));
		jLabel.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent e) {
				if (jLabel.isEnabled() && !popup.isVisible()) {
					DateField widget = getDateField();
					dateChooser.setDate(widget.getDate());
					popup.show(widget, 0, widget.getHeight());
				}
			}
		});
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.gridx = 0;
		this.setLayout(new GridBagLayout());
		this.add(getDateField(), gridBagConstraints);
		this.add(jLabel, gridBagConstraints11);
	}

	/**
	 * Gets the DateWidget used by this component.
	 * @return a DateWidget instance
	 */
	public DateField getDateField() {
		if (dateField == null) {
			dateField = new DateField();
			PropertyChangeListener listener = new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
				}
			};
			dateField.addPropertyChangeListener(DateField.DATE_PROPERTY, listener);
			dateField.addPropertyChangeListener(DateField.CONTENT_VALID_PROPERTY, listener);
		}
		return dateField;
	}

	/**
	 * @see DateField#setIsEmptyNullDateIsValid(boolean)
	 */
	public void setIsEmptyNullDateIsValid(boolean valid) {
		this.getDateField().setIsEmptyNullDateIsValid(valid);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		dateField.setEnabled(enabled);
		jLabel.setEnabled(enabled);
	}
	
	/** Gets the content validity.
	 * @return true if the content is valid, false if it is not.
	 */
	public boolean isContentValid() {
		return this.dateField.isContentValid();
	}
}
