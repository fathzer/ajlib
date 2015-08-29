package com.fathzer.soft.ajlib.swing.widget;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigInteger;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.fathzer.soft.ajlib.swing.Utils;
import com.fathzer.soft.ajlib.swing.widget.IntegerWidget;

/** A widget that allows to select a page in a specified range.
 * @author Jean-Marc Astesana
 * <BR>License: LGPL v3
 */
public class PageSelector extends JPanel {
	private static final String RES_PATH = "/com/fathzer/soft/ajlib/swing/widget/";
	public static final String PAGE_SELECTED_PROPERTY_NAME = "PageSelected";
	private static final long serialVersionUID = 1L;
	
	private IntegerWidget pageNumber;
	private JButton nextPage;
	private JButton lastPage;
	private JButton previousPage;
	private JButton firstPage;
	private int pageCount;
	private JLabel sizeLabel;

	/**
	 * Constructor.
	 */
	public PageSelector() {
		this.pageCount = 0;
		setLayout(new GridBagLayout());
		setOpaque(false);
		
		GridBagConstraints gbcFirstPage = new GridBagConstraints();
		gbcFirstPage.insets = new Insets(0, 0, 0, 5);
		gbcFirstPage.weighty = 1.0;
		gbcFirstPage.fill = GridBagConstraints.VERTICAL;
		gbcFirstPage.gridx = 1;
		gbcFirstPage.gridy = 0;
		add(getFirstPage(), gbcFirstPage);
		
		GridBagConstraints gbcPageNumber = new GridBagConstraints();
		gbcPageNumber.gridx = 3;
		gbcPageNumber.gridy = 0;
		gbcPageNumber.fill = GridBagConstraints.VERTICAL;
		add(getPageNumber(), gbcPageNumber);
		
		sizeLabel = new JLabel("/"+pageCount);
		GridBagConstraints gbcLabel = new GridBagConstraints();
		gbcLabel.insets = new Insets(0, 0, 0, 5);
		gbcLabel.gridx = 4;
		gbcLabel.gridy = 0;
		add(sizeLabel, gbcLabel);
		
		GridBagConstraints gbcNextPage = new GridBagConstraints();
		gbcNextPage.fill = GridBagConstraints.VERTICAL;
		gbcNextPage.insets = new Insets(0, 0, 0, 5);
		gbcNextPage.gridx = 5;
		gbcNextPage.gridy = 0;
		add(getNextPage(), gbcNextPage);
		
		GridBagConstraints gbcLastPage = new GridBagConstraints();
		gbcLastPage.fill = GridBagConstraints.VERTICAL;
		gbcLastPage.gridx = 6;
		gbcLastPage.gridy = 0;
		add(getLastPage(), gbcLastPage);
		
		GridBagConstraints gbcPreviousPage = new GridBagConstraints();
		gbcPreviousPage.fill = GridBagConstraints.VERTICAL;
		gbcPreviousPage.insets = new Insets(0, 0, 0, 5);
		gbcPreviousPage.gridx = 2;
		gbcPreviousPage.gridy = 0;
		add(getPreviousPage(), gbcPreviousPage);
		
		restoreButtonStates();
	}
	
	/** Gets "page number" field.
	 * @return an IntegerWidget
	 */
	public IntegerWidget getPageNumber() {
		if (pageNumber==null) {
			pageNumber = new IntegerWidget(pageCount==0?BigInteger.ZERO:BigInteger.ONE, BigInteger.valueOf(pageCount));
			pageNumber.setColumns(2);
			pageNumber.addPropertyChangeListener(IntegerWidget.VALUE_PROPERTY, new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					if (evt.getNewValue()!=null) {
						setPage(((BigInteger)evt.getNewValue()).intValue()-1);
					} else {
						setPage(-1);
					}
				}
			});
		}
		return pageNumber;
	}

	/** Gets "Go to next page" button.
	 * @return a JButton
	 */
	public JButton getNextPage() {
		if (nextPage==null) {
			nextPage = new JButton();
			nextPage.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setPage(getCurrentPage()+1);
				}
			});
			nextPage.setIcon(Utils.createIcon(getClass().getResource(RES_PATH+"next.png"), 16*getFont().getSize()/12));
			setSelectionButtonSize(nextPage);
		}
		return nextPage;
	}

	/** Gets "Go to last page" button.
	 * @return a JButton
	 */
	public JButton getLastPage() {
		if (lastPage==null) {
			lastPage = new JButton();
			lastPage.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setPage(pageCount-1);
				}
			});
			lastPage.setIcon(Utils.createIcon(getClass().getResource(RES_PATH+"last.png"), 16*getFont().getSize()/12));
			setSelectionButtonSize(lastPage);
		}
		return lastPage;
	}

	/** Gets "Go to previous page" button.
	 * @return a JButton
	 */
	public JButton getPreviousPage() {
		if (previousPage==null) {
			previousPage = new JButton();
			previousPage.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setPage(getCurrentPage()-1);
				}
			});
			previousPage.setIcon(Utils.createIcon(getClass().getResource(RES_PATH+"previous.png"), 16*getFont().getSize()/12));
			setSelectionButtonSize(previousPage);
		}
		return previousPage;
	}

	/** Gets "Go to first page" button.
	 * @return a JButton
	 */
	public JButton getFirstPage() {
		if (firstPage==null) {
			firstPage = new JButton();
			firstPage.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setPage(0);
				}
			});
			firstPage.setIcon(Utils.createIcon(getClass().getResource(RES_PATH+"first.png"), 16*getFont().getSize()/12));
			setSelectionButtonSize(firstPage);
		}
		return firstPage;
	}

	/** Sets the current page index.
	 * @param index The index of the page (should be in the range 0 - page count-1)
	 * or -1 to specify that no page is selected.
	 */
	public void setPage(int index) {
		int old = getCurrentPage();
		if (index!=old) {
			pageNumber.setValue(index<0?null:index+1);
			restoreButtonStates();
			firePropertyChange(PAGE_SELECTED_PROPERTY_NAME, old, index);
		}
	}
	
	private void restoreButtonStates() {
		int currentPage = getCurrentPage();
		firstPage.setEnabled(currentPage>0);
		previousPage.setEnabled(currentPage>0);
		nextPage.setEnabled(currentPage<pageCount-1);
		lastPage.setEnabled(currentPage<pageCount-1);
	}
	
	private void setSelectionButtonSize(JButton button) {
		Dimension preferredSize = button.getPreferredSize();
		preferredSize.width = preferredSize.height;
		button.setPreferredSize(preferredSize);
	}
	
	/** Sets the number of pages.
	 * @param pageCount The number of pages.
	 * @throws IllegalArgumentException if pageCount &lt; 0.
	 */
	public void setPageCount(int pageCount) {
		if (pageCount<0) {
			throw new IllegalArgumentException();
		}
		this.pageCount = pageCount;
		BigInteger min = pageCount==0?BigInteger.ZERO:BigInteger.ONE;
		BigInteger max = BigInteger.valueOf(pageCount);
		getPageNumber().setRange(min, max);
		sizeLabel.setText("/"+pageCount);
		if (getCurrentPage()>=pageCount) {
			setPage(pageCount-1);
		} else {
			restoreButtonStates();
		}
	}
	
	/** Gets the current page number.
	 * @return the current page number (0 based) or -1 if no page is currently selected.
	 */
	public int getCurrentPage() {
		BigInteger value = getPageNumber().getValue();
		return value==null?-1:value.intValue()-1;
	}
	
	/** Gets the page count.
	 * @return a positive or null integer.
	 */
	public int getPageCount() {
		return this.pageCount;
	}
}
