package com.fathzer.soft.ajlib.swing.widget.date;

import java.awt.GridBagLayout;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import java.awt.Insets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.swing.SwingConstants;

import com.fathzer.soft.ajlib.swing.Utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/** A month selector.
 * @author Jean-Marc Astesana
 * <BR>License: LGPL v3
 */
public class MonthWidget extends JPanel {
	private static final long serialVersionUID = 1L;
	static final String RES_PATH = "/com/fathzer/soft/ajlib/swing/widget/date/";

	public static final String DATE_PROPERTY = "MONTH";

	private JButton previousYear = null;
	private JButton previousMonth = null;
	private JLabel currentMonth = null;
	private JButton nextMonth = null;
	private JButton nextYear = null;
	
	private Calendar currentDate;
	private DateFormat formater;

	private JButton now = null;
	private JPanel buttonsPanel;

	/**
	 * This is the default constructor
	 */
	public MonthWidget() {
		this.currentDate = Calendar.getInstance();
		this.currentDate.set(this.currentDate.get(Calendar.YEAR), this.currentDate.get(Calendar.MONTH), 1, 0, 0, 0);
		this.currentDate.set(Calendar.MILLISECOND, 0);
		this.formater = new SimpleDateFormat("MMMM yyyy");
		initialize();
	}
	
	@Override
	public void setLocale(Locale l) {
		super.setLocale(l);
		this.formater = new SimpleDateFormat("MMMM yyyy", l);
		this.currentMonth.setText(this.formater.format(currentDate.getTime()));
	}

	@SuppressWarnings("deprecation")
	public void setMonth(Date date) {
		if ((date.getYear()+1900!=this.currentDate.get(Calendar.YEAR)) || (date.getMonth()!=this.currentDate.get(Calendar.MONTH))) {
			Date old = this.currentDate.getTime();
			this.currentDate.set(date.getYear()+1900, date.getMonth(), 1);
			this.currentMonth.setText(formater.format(date));
			this.firePropertyChange(DATE_PROPERTY, old, this.currentDate.getTime());
		}
	}
	
	/** Get the currently selected month.
	 * @return The first day of the selected month at 0:0:0 AM.
	 */
	public Date getMonth() {
		return this.currentDate.getTime();
	}
	
	/** Move forward or backward the currently selected month.
	 * @param months The number of months to increment the current month. This amount can be negative.
	 */
	public void add(int months) {
		Date old = this.currentDate.getTime();
		this.currentDate.add(Calendar.MONTH, months);
		Date newDate = this.currentDate.getTime();
		this.currentMonth.setText(formater.format(newDate));
		this.firePropertyChange(DATE_PROPERTY, old, newDate);
	}

	/**
	 * This method initializes this
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints2.weightx = 1.0D;
		gridBagConstraints2.anchor = GridBagConstraints.CENTER;
		gridBagConstraints2.insets = new Insets(5, 5, 5, 0);
		gridBagConstraints2.gridy = 0;
		currentMonth = new JLabel();
		currentMonth.setText(formater.format(this.currentDate.getTime()));
		currentMonth.setHorizontalTextPosition(SwingConstants.CENTER);
		currentMonth.setHorizontalAlignment(SwingConstants.CENTER);
		this.setLayout(new GridBagLayout());
		GridBagConstraints gbcButtonsPanel = new GridBagConstraints();
		gbcButtonsPanel.gridx = 0;
		gbcButtonsPanel.gridy = 1;
		add(getButtonsPanel(), gbcButtonsPanel);
		this.add(currentMonth, gridBagConstraints2);
	}
	
	/**
	 * This method initializes previousYear	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getPreviousYear() {
		if (previousYear == null) {
			previousYear = new JButton();
			previousYear.setIcon(Utils.createIcon(getClass().getResource(RES_PATH+"fast-rewind.png"), 16*getFont().getSize()/12));
			previousYear.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					add(-12);
				}
			});
		}
		return previousYear;
	}

	/**
	 * This method initializes previousMonth	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getPreviousMonth() {
		if (previousMonth == null) {
			previousMonth = new JButton();
			previousMonth.setIcon(Utils.createIcon(getClass().getResource(RES_PATH+"rewind.png"), 16*getFont().getSize()/12));
			previousMonth.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					add(-1);
				}
			});
		}
		return previousMonth;
	}

	/**
	 * This method initializes nextMonth	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getNextMonth() {
		if (nextMonth == null) {
			nextMonth = new JButton();
			nextMonth.setIcon(Utils.createIcon(getClass().getResource(RES_PATH+"forward.png"), 16*getFont().getSize()/12));
			nextMonth.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					add(1);
				}
			});
		}
		return nextMonth;
	}

	/**
	 * This method initializes nextYear	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getNextYear() {
		if (nextYear == null) {
			nextYear = new JButton();
			nextYear.setIcon(Utils.createIcon(getClass().getResource(RES_PATH+"fast-forward.png"), 16*getFont().getSize()/12));
			nextYear.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					add(12);
				}
			});
		}
		return nextYear;
	}

	/**
	 * This method initializes now	
	 * @return javax.swing.JButton	
	 */
	public JButton getNow() {
		if (now == null) {
			now = new JButton();
			now.setIcon(Utils.createIcon(getClass().getResource(RES_PATH+"stop.png"), 16*getFont().getSize()/12));
			now.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					setMonth(new Date());
				}
			});
		}
		return now;
	}
	private JPanel getButtonsPanel() {
		if (buttonsPanel == null) {
			buttonsPanel = new JPanel();
			GridBagLayout gblButtonsPanel = new GridBagLayout();
			buttonsPanel.setLayout(gblButtonsPanel);
			GridBagConstraints gbcPreviousYear = new GridBagConstraints();
			gbcPreviousYear.insets = new Insets(0, 0, 0, 5);
			gbcPreviousYear.gridx = 0;
			gbcPreviousYear.gridy = 0;
			buttonsPanel.add(getPreviousYear(), gbcPreviousYear);
			GridBagConstraints gbcPreviousMonth = new GridBagConstraints();
			gbcPreviousMonth.insets = new Insets(0, 0, 0, 5);
			gbcPreviousMonth.gridx = 1;
			gbcPreviousMonth.gridy = 0;
			buttonsPanel.add(getPreviousMonth(), gbcPreviousMonth);
			GridBagConstraints gbcNow = new GridBagConstraints();
			gbcNow.fill = GridBagConstraints.HORIZONTAL;
			gbcNow.insets = new Insets(0, 0, 0, 5);
			gbcNow.gridx = 2;
			gbcNow.gridy = 0;
			buttonsPanel.add(getNow(), gbcNow);
			GridBagConstraints gbcNextMonth = new GridBagConstraints();
			gbcNextMonth.insets = new Insets(0, 0, 0, 5);
			gbcNextMonth.gridx = 3;
			gbcNextMonth.gridy = 0;
			buttonsPanel.add(getNextMonth(), gbcNextMonth);
			GridBagConstraints gbcNextYear = new GridBagConstraints();
			gbcNextYear.anchor = GridBagConstraints.WEST;
			gbcNextYear.gridx = 4;
			gbcNextYear.gridy = 0;
			buttonsPanel.add(getNextYear(), gbcNextYear);
		}
		return buttonsPanel;
	}
}
