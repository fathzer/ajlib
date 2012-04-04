package net.astesana.ajlib.swing.widget.date;

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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;

public class MonthWidget extends JPanel {
	private static final long serialVersionUID = 1L;
	
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
		this.setSize(400, 200);
		this.setLayout(new GridBagLayout());
		GridBagConstraints gbc_buttonsPanel = new GridBagConstraints();
		gbc_buttonsPanel.gridx = 0;
		gbc_buttonsPanel.gridy = 1;
		add(getButtonsPanel(), gbc_buttonsPanel);
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
			previousYear.setIcon(new ImageIcon(getClass().getResource("/net/astesana/ajlib/swing/widget/date/fast-rewind.png")));
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
			previousMonth.setIcon(new ImageIcon(getClass().getResource("/net/astesana/ajlib/swing/widget/date/rewind.png")));
			previousMonth.setPreferredSize(new Dimension(41, 26));
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
			nextMonth.setText("");
			nextMonth.setIcon(new ImageIcon(getClass().getResource("/net/astesana/ajlib/swing/widget/date/forward.png")));
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
			nextYear.setText("");
			nextYear.setIcon(new ImageIcon(getClass().getResource("/net/astesana/ajlib/swing/widget/date/fast-forward.png")));
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
			now.setText("");
			now.setIcon(new ImageIcon(getClass().getResource("/net/astesana/ajlib/swing/widget/date/stop.png")));
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
			GridBagLayout gbl_buttonsPanel = new GridBagLayout();
			gbl_buttonsPanel.columnWidths = new int[]{0, 0, 0, 0, 0, 0};
			gbl_buttonsPanel.rowHeights = new int[]{0, 0};
			gbl_buttonsPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
			gbl_buttonsPanel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
			buttonsPanel.setLayout(gbl_buttonsPanel);
			GridBagConstraints gbc_previousYear = new GridBagConstraints();
			gbc_previousYear.insets = new Insets(0, 0, 0, 5);
			gbc_previousYear.gridx = 0;
			gbc_previousYear.gridy = 0;
			buttonsPanel.add(getPreviousYear(), gbc_previousYear);
			GridBagConstraints gbc_previousMonth = new GridBagConstraints();
			gbc_previousMonth.insets = new Insets(0, 0, 0, 5);
			gbc_previousMonth.gridx = 1;
			gbc_previousMonth.gridy = 0;
			buttonsPanel.add(getPreviousMonth(), gbc_previousMonth);
			GridBagConstraints gbc_now = new GridBagConstraints();
			gbc_now.fill = GridBagConstraints.HORIZONTAL;
			gbc_now.insets = new Insets(0, 0, 0, 5);
			gbc_now.gridx = 2;
			gbc_now.gridy = 0;
			buttonsPanel.add(getNow(), gbc_now);
			GridBagConstraints gbc_nextMonth = new GridBagConstraints();
			gbc_nextMonth.insets = new Insets(0, 0, 0, 5);
			gbc_nextMonth.gridx = 3;
			gbc_nextMonth.gridy = 0;
			buttonsPanel.add(getNextMonth(), gbc_nextMonth);
			GridBagConstraints gbc_nextYear = new GridBagConstraints();
			gbc_nextYear.anchor = GridBagConstraints.WEST;
			gbc_nextYear.gridx = 4;
			gbc_nextYear.gridy = 0;
			buttonsPanel.add(getNextYear(), gbc_nextYear);
		}
		return buttonsPanel;
	}
}