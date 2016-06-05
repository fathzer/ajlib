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
		GridBagLayout gblSearchPanel = new GridBagLayout();
		setLayout(gblSearchPanel);
		GridBagConstraints gbcLblNewLabel = new GridBagConstraints();
		gbcLblNewLabel.insets = new Insets(0, 0, 0, 5);
		gbcLblNewLabel.anchor = GridBagConstraints.EAST;
		gbcLblNewLabel.gridx = 0;
		gbcLblNewLabel.gridy = 0;
		add(getLblNewLabel(), gbcLblNewLabel);
		GridBagConstraints gbcSearchedTextField = new GridBagConstraints();
		gbcSearchedTextField.fill = GridBagConstraints.HORIZONTAL;
		gbcSearchedTextField.weightx = 1.0;
		gbcSearchedTextField.gridx = 1;
		gbcSearchedTextField.gridy = 0;
		add(getSearchedTextField(), gbcSearchedTextField);
		GridBagConstraints gbcResultPanel = new GridBagConstraints();
		gbcResultPanel.fill = GridBagConstraints.BOTH;
		gbcResultPanel.gridx = 2;
		gbcResultPanel.gridy = 0;
		add(getResultPanel(), gbcResultPanel);
		GridBagConstraints gbcSettingsPanel = new GridBagConstraints();
		gbcSettingsPanel.fill = GridBagConstraints.BOTH;
		gbcSettingsPanel.gridx = 3;
		gbcSettingsPanel.gridy = 0;
		add(getSettingsPanel(), gbcSettingsPanel);
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
			GridBagLayout gblSettingsPanel = new GridBagLayout();
			settingsPanel.setLayout(gblSettingsPanel);
			GridBagConstraints gbcChckbxCase = new GridBagConstraints();
			gbcChckbxCase.anchor = GridBagConstraints.WEST;
			gbcChckbxCase.insets = new Insets(0, 0, 5, 5);
			gbcChckbxCase.gridx = 0;
			gbcChckbxCase.gridy = 0;
			settingsPanel.add(getCaseInsensitiveCheckBox(), gbcChckbxCase);
			GridBagConstraints gbcChckbxDiacritical = new GridBagConstraints();
			gbcChckbxDiacritical.insets = new Insets(0, 0, 0, 5);
			gbcChckbxDiacritical.anchor = GridBagConstraints.WEST;
			gbcChckbxDiacritical.gridx = 0;
			gbcChckbxDiacritical.gridy = 1;
			settingsPanel.add(getDiacriticalInsensitiveCheckbox(), gbcChckbxDiacritical);
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
			GridBagLayout gblResultPanel = new GridBagLayout();
			resultPanel.setLayout(gblResultPanel);
			GridBagConstraints gbcResultField = new GridBagConstraints();
			gbcResultField.fill = GridBagConstraints.HORIZONTAL;
			gbcResultField.gridheight = 2;
			gbcResultField.gridx = 0;
			gbcResultField.gridy = 0;
			resultPanel.add(getResultField(), gbcResultField);
			GridBagConstraints gbcUpButton = new GridBagConstraints();
			gbcUpButton.gridx = 1;
			gbcUpButton.gridy = 0;
			resultPanel.add(getUpButton(), gbcUpButton);
			GridBagConstraints gbcDownButton = new GridBagConstraints();
			gbcDownButton.gridx = 1;
			gbcDownButton.gridy = 1;
			resultPanel.add(getDownButton(), gbcDownButton);
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
