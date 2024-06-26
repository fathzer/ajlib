/* This is a modified version of the original DateChooserPanel.java from JFree.
 * Changes:<UL>
 * <LI>The month selection is implemented with buttons instead of the originals JComboBox.
 * This allows the component to be used in a popup.</LI>
 * <LI>The "today button" is now in the month selection panel and hasn't an non localized english label</LI>
 * <LI>Null dates are now allowed (no date is selected in the panel)</LI>
 * </UL>
 * 
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation; either version 3 of the License, or 
 * (at your option) any later version.

 * ========================================================================
 * JCommon: a free general purpose class library for the Java(tm) platform
 * ========================================================================
 *
 * (C) Copyright 2000-2005, by Object Refinery Limited and Contributors.
 * 
 * Project Info:  http://www.jfree.org/jcommon/index.html
 *
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation; either version 2.1 of the License, or 
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, 
 * USA.  
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 * 
 * ---------------------
 * DateChooserPanel.java
 * ---------------------
 * (C) Copyright 2000-2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: DateChooserPanel.java,v 1.11 2007/11/02 17:50:36 taqua Exp $
 *
 * Changes (from 26-Oct-2001)
 * --------------------------
 * 26-Oct-2001: Changed package to com.jrefinery.ui.* (DG);
 * 08-Dec-2001: Dropped the getMonths() method (DG);
 * 13-Oct-2002: Fixed errors reported by Checkstyle (DG);
 * 02-Nov-2005: Fixed a bug where the current day-of-the-month is past
 *               the end of the newly selected month when the month or year
 *               combo boxes are changed - see bug id 1344319 (DG);
 *
 */

package com.fathzer.soft.ajlib.swing.widget.date;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.fathzer.soft.ajlib.utilities.NullUtils;

/**
 * A panel that allows the user to select a date.
 * @author David Gilbert (modified by Jean-Marc Astesana)
 */
public class CalendarWidget extends JPanel {
	private static final long serialVersionUID = 1L;
	public static final String DATE_PROPERTY = "DATE_PROPERTY";

	/**
	 * The date selected in the panel.
	 */
	private Calendar chosenDate;

	/**
	 * The color for the selected date.
	 */
	private Color chosenDateButtonColor;

	/**
	 * The color for dates in the current month.
	 */
	private Color chosenMonthButtonColor;

	/**
	 * The color for dates that are visible, but not in the current month.
	 */
	private Color chosenOtherButtonColor;

	/**
	 * The first day-of-the-week.
	 */
	private int firstDayOfWeek;

	/**
	 * The font used to display the date.
	 */
	private Float fontRatio = 0.85f;

	/**
	 * An array of buttons used to display the days-of-the-month.
	 */
	private JButton[] buttons;

	/**
	 * An array of labels used to display day of week names.
	 */
	private JLabel[] days;

	private MonthWidget monthSelector;

	/**
	 * The ordered set of all seven days of a week, beginning with the
	 * 'firstDayOfWeek'.
	 */
	private int[] weekDays;
		
	/**
	 * Constructs a new date chooser panel, using today's date as the initial
	 * selection.
	 */
	public CalendarWidget() {
		super(new BorderLayout());

		//TODO It would be cool to choose colors from the current Look and Feel ... but I didn't succeed in doing that :-(
		this.chosenDateButtonColor = Color.red;
		this.chosenMonthButtonColor = Color.white;
		this.chosenOtherButtonColor = Color.gray;
		
		// the default date is today...
		this.chosenDate = Calendar.getInstance(getLocale());
		initializeDays();

		monthSelector = new MonthWidget();
		add(monthSelector, BorderLayout.NORTH);
		add(getCalendarPanel(), BorderLayout.CENTER);
		monthSelector.addPropertyChangeListener(MonthWidget.DATE_PROPERTY, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				refreshButtons();
			}
		});
		monthSelector.getNow().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setDate(new Date());
			}
		});
		refreshButtons();
	}

	@Override
	public void setLocale(Locale l) {
		super.setLocale(l);
		monthSelector.setLocale(l);
		initializeDays();
		updateDays();
		refreshButtons();
	}

	private void initializeDays() {
		this.firstDayOfWeek = Calendar.getInstance(getLocale()).getFirstDayOfWeek();
		this.weekDays = new int[7];
		for (int i = 0; i < 7; i++) {
			this.weekDays[i] = ((this.firstDayOfWeek + i - 1) % 7) + 1;
		}
	}

	/**
	 * Sets the date chosen in the panel.
	 * 
	 * @param theDate
	 *          the new date.
	 */
	public void setDate(final Date theDate) {
		Date old = this.getDate();
		if (!NullUtils.areEquals(theDate, old)) {
			if (theDate == null) {
				this.chosenDate = null;
			} else {
				if (this.chosenDate == null) {
					this.chosenDate = Calendar.getInstance(getLocale());
				}
				this.chosenDate.setTime(theDate);
				this.monthSelector.setMonth(theDate);
			}
			refreshButtons();
			this.firePropertyChange(DATE_PROPERTY, old, theDate);
		}
	}

	/**
	 * Returns the date selected in the panel.
	 * 
	 * @return the selected date.
	 */
	public Date getDate() {
		return this.chosenDate == null ? null : this.chosenDate.getTime();
	}

	/**
	 * Returns a panel of buttons, each button representing a day in the month.
	 * This is a sub-component of the DatePanel.
	 * 
	 * @return the panel.
	 */
	private JPanel getCalendarPanel() {
		final JPanel p = new JPanel(new GridLayout(7, 7));
		final DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(getLocale());
		final String[] weekDays = dateFormatSymbols.getShortWeekdays();
		this.days = new JLabel[this.weekDays.length];

		for (int i = 0; i < this.weekDays.length; i++) {
			this.days[i] = new JLabel(weekDays[this.weekDays[i]], SwingConstants.CENTER);
			p.add(this.days[i]);
		}

		this.buttons = new JButton[42];
		ActionListener listener = new ActionListener() {
			/**
			 * Handles action-events from the date panel.
			 * @param e information about the event that occurred.
			 */
			public void actionPerformed(final ActionEvent e) {
				final JButton b = (JButton) e.getSource();
				final int i = Integer.parseInt(b.getName());
				final Calendar cal = getFirstVisibleDate();
				cal.add(Calendar.DATE, i);
				setDate(cal.getTime());
			}
		};
		Font bFont = null;
		for (int i = 0; i < 42; i++) {
			final JButton b = new JButton("");
			if (bFont==null) {
				bFont = b.getFont().deriveFont(fontRatio*b.getFont().getSize());
			}
			b.setMargin(new Insets(1, 1, 1, 1));
			b.setName(Integer.toString(i));
			b.setFont(bFont);
			b.setFocusPainted(false);
			b.setActionCommand("dateButtonClicked");
			b.addActionListener(listener);
			this.buttons[i] = b;
			p.add(b);
		}
		return p;
	}

	private void updateDays() {
		final DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(getLocale());
		final String[] weekDaysWordings = dateFormatSymbols.getShortWeekdays();
		for (int i = 0; i < days.length; i++) {
			days[i].setText(weekDaysWordings[this.weekDays[i]]);
		}
	}

	/**
	 * Returns true if the two dates are equal (time of day is ignored).
	 * 
	 * @param c1
	 *          the first date.
	 * @param c2
	 *          the second date.
	 * @return boolean.
	 */
	private boolean equalDates(final Calendar c1, final Calendar c2) {
		// Be aware that c1 or c2 can be null
		if ((c1 == null) && (c2 == null)) {
			return true;
		}
		if ((c1 == null) || (c2 == null)) {
			return false;
		}
		return (c1.get(Calendar.DATE) == c2.get(Calendar.DATE)) && (c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH))
				&& (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR));
	}

	/**
	 * Returns the first date that is visible in the grid. This should always be
	 * in the month preceding the month of the selected date.
	 * 
	 * @return the date.
	 */
	private Calendar getFirstVisibleDate() {
		final Calendar c = Calendar.getInstance(getLocale());
		c.setTime(this.monthSelector.getMonth());
		c.add(Calendar.DATE, -1);
		while (c.get(Calendar.DAY_OF_WEEK) != getFirstDayOfWeek()) {
			c.add(Calendar.DATE, -1);
		}
		return c;
	}

	/**
	 * Returns the first day of the week (controls the labels in the date panel).
	 * 
	 * @return the first day of the week.
	 */
	private int getFirstDayOfWeek() {
		return this.firstDayOfWeek;
	}

	/**
	 * Update the button labels and colors to reflect date selection.
	 */
	private void refreshButtons() {
		final Calendar c = getFirstVisibleDate();
		final Calendar current = Calendar.getInstance(getLocale());
		current.setTime(this.monthSelector.getMonth());
		for (int i = 0; i < 42; i++) {
			final JButton b = this.buttons[i];
			setButtonAppearance(b, c, current);
			c.add(Calendar.DATE, 1);
		}
	}
	
	/** Sets a date button appearance.
	 * @param button The button to be set up
	 * @param date The date represented by the button
	 * @param currentMonth The currently displayed month
	 */
	private void setButtonAppearance(JButton button, Calendar date, Calendar currentMonth) {
		String day = Integer.toString(date.get(Calendar.DATE));
		Color color;
		if (equalDates(date, this.chosenDate)) {
			color = this.chosenDateButtonColor;
		} else if ((date.get(Calendar.MONTH) == currentMonth.get(Calendar.MONTH))
				&& (date.get(Calendar.YEAR) == currentMonth.get(Calendar.YEAR))) {
			color = this.chosenMonthButtonColor;
		} else {
			color = this.chosenOtherButtonColor;
		}
		button.setBackground(color);
		if (equalDates(date, Calendar.getInstance())) {
			day = "<html><u><b>"+day+"</b></u></html>";
		}
		button.setText(day);
	}

	/**
	 * Returns the color for the currently selected date.
	 * 
	 * @return a color.
	 */
	public Color getChosenDateButtonColor() {
		return this.chosenDateButtonColor;
	}

	/**
	 * Redefines the color for the currently selected date.
	 * 
	 * @param chosenDateButtonColor
	 *          the new color
	 */
	public void setChosenDateButtonColor(final Color chosenDateButtonColor) {
		checkButtonColor(chosenDateButtonColor);
		final Color oldValue = this.chosenDateButtonColor;
		this.chosenDateButtonColor = chosenDateButtonColor;
		refreshButtons();
		firePropertyChange("chosenDateButtonColor", oldValue, chosenDateButtonColor);
	}

	/**
	 * Returns the color for the buttons representing the current month.
	 * 
	 * @return the color for the current month.
	 */
	public Color getChosenMonthButtonColor() {
		return this.chosenMonthButtonColor;
	}

	/**
	 * Defines the color for the buttons representing the current month.
	 * 
	 * @param chosenMonthButtonColor
	 *          the color for the current month.
	 */
	public void setChosenMonthButtonColor(final Color chosenMonthButtonColor) {
		checkButtonColor(chosenMonthButtonColor);
		final Color oldValue = this.chosenMonthButtonColor;
		this.chosenMonthButtonColor = chosenMonthButtonColor;
		refreshButtons();
		firePropertyChange("chosenMonthButtonColor", oldValue, chosenMonthButtonColor);
	}

	/**
	 * Returns the color for the buttons representing the other months.
	 * 
	 * @return a color.
	 */
	public Color getChosenOtherButtonColor() {
		return this.chosenOtherButtonColor;
	}

	/**
	 * Redefines the color for the buttons representing the other months.
	 * 
	 * @param chosenOtherButtonColor
	 *          a color.
	 */
	public void setChosenOtherButtonColor(final Color chosenOtherButtonColor) {
		checkButtonColor(chosenOtherButtonColor);
		final Color oldValue = this.chosenOtherButtonColor;
		this.chosenOtherButtonColor = chosenOtherButtonColor;
		refreshButtons();
		firePropertyChange("chosenOtherButtonColor", oldValue, chosenOtherButtonColor);
	}
	
	private static void checkButtonColor(Color color) {
		if (color == null) {
			throw new NullPointerException("UIColor must not be null.");
		}
	}
}
