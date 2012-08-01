package net.astesana.ajlib.swing.widget;

import javax.swing.JPanel;

import net.astesana.ajlib.swing.TextSearcher;
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

import net.astesana.ajlib.swing.widget.TextWidget;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class SearchPanel extends JPanel {
	private JLabel lblNewLabel;
	private TextWidget searchedTextField;
	private JPanel panel;
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
	 * Create the panel.
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
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 3;
		gbc_panel.gridy = 0;
		add(getPanel(), gbc_panel);
	}
	
	private JLabel getLblNewLabel() {
		if (lblNewLabel == null) {
			lblNewLabel = new JLabel("Chercher :");
		}
		return lblNewLabel;
	}
	
	public TextWidget getSearchedTextField() {
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
	
	public JCheckBox getCaseInsensitiveCheckBox() {
		if (chckbxCase == null) {
			chckbxCase = new JCheckBox("Ignorer la casse");
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
	
	private JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
			GridBagLayout gbl_panel = new GridBagLayout();
			panel.setLayout(gbl_panel);
			GridBagConstraints gbc_chckbxCase = new GridBagConstraints();
			gbc_chckbxCase.anchor = GridBagConstraints.WEST;
			gbc_chckbxCase.insets = new Insets(0, 0, 5, 5);
			gbc_chckbxCase.gridx = 0;
			gbc_chckbxCase.gridy = 0;
			panel.add(getCaseInsensitiveCheckBox(), gbc_chckbxCase);
			GridBagConstraints gbc_chckbxDiacritical = new GridBagConstraints();
			gbc_chckbxDiacritical.insets = new Insets(0, 0, 0, 5);
			gbc_chckbxDiacritical.anchor = GridBagConstraints.WEST;
			gbc_chckbxDiacritical.gridx = 0;
			gbc_chckbxDiacritical.gridy = 1;
			panel.add(getDiacriticalInsensitiveCheckbox(), gbc_chckbxDiacritical);
		}
		return panel;
	}
	public JCheckBox getDiacriticalInsensitiveCheckbox() {
		if (chckbxDiacritical == null) {
			chckbxDiacritical = new JCheckBox("Ignorer les accents");
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
			getResultField().setText((currentOffset+1)+"/"+offsets.length);
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
			upButton = new JButton(new ImageIcon(SearchPanel.class.getResource("/net/astesana/ajlib/swing/widget/up.png")));
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
			downButton = new JButton(new ImageIcon(SearchPanel.class.getResource("/net/astesana/ajlib/swing/widget/down.png")));
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
}
