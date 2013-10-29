package com.fathzer.soft.ajlib.swing.widget;

import javax.swing.JPanel;

import javax.swing.JLabel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JCheckBox;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.ImageIcon;

import com.fathzer.soft.ajlib.swing.TextSearcher;
import com.fathzer.soft.ajlib.swing.framework.Application;
import com.fathzer.soft.ajlib.swing.widget.TextWidget;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/** A panel with every need to search text in a text component.
 * <br>It provides a text field to enter the searched text, buttons to navigate through occurrences and setting buttons.
 * <br>When text is entered in search the text field, the first occurrence is automatically highlight in the text component. 
 * @author Jean-Marc Astesana
 * <BR>License: LGPL v3
 */
@SuppressWarnings("serial")
public class SearchPanel extends JPanel {
	private JLabel lblNewLabel;
	private TextWidget searchedTextField;
	private JPanel settingsPanel;
	private JCheckBox chckbxCase;
	private JCheckBox chckbxDiacritical;
	private JTextField resultField;
	private JButton upButton;
	private JButton downButton;
	private JPanel resultPanel;

	private JTextComponent textComponent;
	private TextSearcher searcher;
	private int currentOffset;

	/**
	 * Constructor.
	 * @param textComponent The component in which to search.
	 */
	public SearchPanel(JTextComponent textComponent) {
		this.textComponent = textComponent;
		this.searcher = new TextSearcher(this.textComponent);
		initialize();
	}

	private void initialize() {
		GridBagLayout gbl_searchPanel = new GridBagLayout();
		setLayout(gbl_searchPanel);
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		add(getLblNewLabel(), gbc_lblNewLabel);
		GridBagConstraints gbc_searchedTextField = new GridBagConstraints();
		gbc_searchedTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_searchedTextField.weightx = 1.0;
		gbc_searchedTextField.gridx = 1;
		gbc_searchedTextField.gridy = 0;
		add(getSearchedTextField(), gbc_searchedTextField);
		GridBagConstraints gbc_resultPanel = new GridBagConstraints();
		gbc_resultPanel.fill = GridBagConstraints.BOTH;
		gbc_resultPanel.gridx = 2;
		gbc_resultPanel.gridy = 0;
		add(getResultPanel(), gbc_resultPanel);
		GridBagConstraints gbc_settingsPanel = new GridBagConstraints();
		gbc_settingsPanel.fill = GridBagConstraints.BOTH;
		gbc_settingsPanel.gridx = 3;
		gbc_settingsPanel.gridy = 0;
		add(getSettingsPanel(), gbc_settingsPanel);
	}
	
	private JLabel getLblNewLabel() {
		if (lblNewLabel == null) {
			lblNewLabel = new JLabel(Application.getString("SearchPanel.find", getLocale())); //$NON-NLS-1$
		}
		return lblNewLabel;
	}
	
	private TextWidget getSearchedTextField() {
		if (searchedTextField == null) {
			searchedTextField = new TextWidget();
			searchedTextField.addPropertyChangeListener(TextWidget.TEXT_PROPERTY, new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					doSearch();
				}
			});
		}
		return searchedTextField;
	}
	
	private JCheckBox getCaseInsensitiveCheckBox() {
		if (chckbxCase == null) {
			chckbxCase = new JCheckBox(Application.getString("SearchPanel.ingnoreCase", getLocale())); //$NON-NLS-1$
			chckbxCase.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					searcher.setCaseSentitive(!getCaseInsensitiveCheckBox().isSelected());
					doSearch();
				}
			});
			chckbxCase.setSelected(true);
		}
		return chckbxCase;
	}
	
	private JPanel getSettingsPanel() {
		if (settingsPanel == null) {
			settingsPanel = new JPanel();
			GridBagLayout gbl_settingsPanel = new GridBagLayout();
			settingsPanel.setLayout(gbl_settingsPanel);
			GridBagConstraints gbc_chckbxCase = new GridBagConstraints();
			gbc_chckbxCase.anchor = GridBagConstraints.WEST;
			gbc_chckbxCase.insets = new Insets(0, 0, 5, 5);
			gbc_chckbxCase.gridx = 0;
			gbc_chckbxCase.gridy = 0;
			settingsPanel.add(getCaseInsensitiveCheckBox(), gbc_chckbxCase);
			GridBagConstraints gbc_chckbxDiacritical = new GridBagConstraints();
			gbc_chckbxDiacritical.insets = new Insets(0, 0, 0, 5);
			gbc_chckbxDiacritical.anchor = GridBagConstraints.WEST;
			gbc_chckbxDiacritical.gridx = 0;
			gbc_chckbxDiacritical.gridy = 1;
			settingsPanel.add(getDiacriticalInsensitiveCheckbox(), gbc_chckbxDiacritical);
		}
		return settingsPanel;
	}
	private JCheckBox getDiacriticalInsensitiveCheckbox() {
		if (chckbxDiacritical == null) {
			chckbxDiacritical = new JCheckBox(Application.getString("SearchPanel.ignoreMarks", getLocale())); //$NON-NLS-1$
			chckbxDiacritical.setSelected(true);
			chckbxDiacritical.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					searcher.setDiacriticalSensitive(!getDiacriticalInsensitiveCheckbox().isSelected());
					doSearch();
				}
			});
		}
		return chckbxDiacritical;
	}
	private void doSearch() {
		String text = getSearchedTextField().getText();
		searcher.setSearchedText(text);
		int[] offsets = searcher.getOffsets();
		searchedTextField.setBackground((text.length()>0) && (offsets.length==0)?Color.red:Color.white);
		getResultPanel().setVisible(offsets.length>0);
		if (offsets.length>0) {
			setSelected(0);
		} else {
			textComponent.getHighlighter().removeAllHighlights();
		}
		SearchPanel.this.validate();
	}
	
	private void setSelected(int offsetNum) {
		try {
			int[] offsets = searcher.getOffsets();
			searcher.highlight(offsets[offsetNum], null);
			currentOffset = offsetNum;
			getDownButton().setEnabled(currentOffset!=offsets.length-1);
			getUpButton().setEnabled(currentOffset!=0);
			getResultField().setText((currentOffset+1)+"/"+offsets.length); //$NON-NLS-1$
		} catch (BadLocationException e) {
			throw new RuntimeException(e);
		}
	}
	
	private JTextField getResultField() {
		if (resultField == null) {
			resultField = new JTextField();
			resultField.setEditable(false);
			resultField.setFocusable(false);
			resultField.setColumns(5);
		}
		return resultField;
	}
	private JButton getUpButton() {
		if (upButton == null) {
			upButton = new JButton(new ImageIcon(getClass().getResource("/com/fathzer/soft/ajlib/swing/widget/up.png"))); //$NON-NLS-1$
			upButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setSelected(currentOffset-1);
				}
			});
			upButton.setContentAreaFilled(false);
			upButton.setPreferredSize(new Dimension(16,12));
		}
		return upButton;
	}
	private JButton getDownButton() {
		if (downButton == null) {
			downButton = new JButton(new ImageIcon(getClass().getResource("/com/fathzer/soft/ajlib/swing/widget/down.png"))); //$NON-NLS-1$
			downButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setSelected(currentOffset+1);
				}
			});
			downButton.setContentAreaFilled(false);
			downButton.setPreferredSize(new Dimension(16,12));
		}
		return downButton;
	}
	private JPanel getResultPanel() {
		if (resultPanel == null) {
			resultPanel = new JPanel();
			GridBagLayout gbl_resultPanel = new GridBagLayout();
			resultPanel.setLayout(gbl_resultPanel);
			GridBagConstraints gbc_resultField = new GridBagConstraints();
			gbc_resultField.fill = GridBagConstraints.HORIZONTAL;
			gbc_resultField.gridheight = 2;
			gbc_resultField.gridx = 0;
			gbc_resultField.gridy = 0;
			resultPanel.add(getResultField(), gbc_resultField);
			GridBagConstraints gbc_upButton = new GridBagConstraints();
			gbc_upButton.gridx = 1;
			gbc_upButton.gridy = 0;
			resultPanel.add(getUpButton(), gbc_upButton);
			GridBagConstraints gbc_downButton = new GridBagConstraints();
			gbc_downButton.gridx = 1;
			gbc_downButton.gridy = 1;
			resultPanel.add(getDownButton(), gbc_downButton);
		}
		return resultPanel;
	}
	
	/** Show/hide the search settings.
	 * @param visible true to show the settings, false to hide them.
	 */
	public void setSettingsVisible(boolean visible) {
		this.getDiacriticalInsensitiveCheckbox().setVisible(visible);
		this.getCaseInsensitiveCheckBox().setVisible(visible);
	}
}
