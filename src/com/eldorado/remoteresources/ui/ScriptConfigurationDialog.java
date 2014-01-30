/*
 * (C) Copyright 2014 Instituto de Pesquisas Eldorado (http://www.eldorado.org.br/).
 *
 * This file is part of the software Remote Resources
 *
 * All rights reserved. This file and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */

package com.eldorado.remoteresources.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import com.eldorado.remoteresources.android.client.connection.Client;
import com.eldorado.remoteresources.android.common.connection.messages.control.DeviceCommandMessage;
import com.eldorado.remoteresources.android.common.connection.messages.control.KeyCommandMessage;
import com.eldorado.remoteresources.android.common.connection.messages.control.KeyCommandMessageType;
import com.eldorado.remoteresources.android.common.connection.messages.control.ScriptCommandMessage;
import com.eldorado.remoteresources.android.common.connection.messages.control.ScriptCommandMessageType;
import com.eldorado.remoteresources.android.common.connection.messages.control.TouchCommandMessage;
import com.eldorado.remoteresources.android.common.connection.messages.control.TouchCommandMessageType;
import com.eldorado.remoteresources.i18n.RemoteResourcesLocalization;
import com.eldorado.remoteresources.i18n.RemoteResourcesMessages;
import com.eldorado.remoteresources.ui.actions.ClientManipulationScript;
import com.eldorado.remoteresources.ui.model.Script;

/**
 * This dialog is responsible for choose the script file that will run.
 * 
 * @author Michel Silva Fornaciali
 * 
 */
public class ScriptConfigurationDialog extends JDialog implements
		ActionListener {

	private static final long serialVersionUID = 8863129064446542491L;

	// UI components
	private JTextField chosenFile;
	private JTextField sleepTime;
	private JComboBox<Integer> pauseLine;
	private JComboBox<Integer> sleepLine;
	private JFileChooser fileChooser;
	private JButton chooserDirectoryButton;
	private JButton addPause;
	private JButton addSleep;
	private JButton saveScriptFile;
	private JButton debugButton;
	private JButton runScriptButton;
	private JButton okButton;
	private JButton cancelButton;
	private JList<String> scriptVisualization;
	private DefaultListModel<String> dataModel;
	private BasePanel content = null;
	private GridBagLayout contentLayout = null;

	// Controllers
	private final static ClientManipulationScript cScript = new ClientManipulationScript();
	private Vector<DeviceCommandMessage> actions;
	private Vector<String> vetScript;
	private final Client client;
	private boolean hasChanges = false;
	private File scriptFile;
	private int index = -1;

	// Constructor
	public ScriptConfigurationDialog(Client client) {
		this.client = client;

		setResizable(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setModal(true);
		initComponents();

		pack();
		setVisible(true);
	}

	private void initComponents() {
		content = new BasePanel();
		content.setBorderSize(2);
		content.setArc(30);
		content.setBorderColor(Color.BLACK);
		content.setBackground(Color.LIGHT_GRAY);
		content.setOpaque(false);
		content.setLayout(contentLayout = new GridBagLayout());
		content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		createTitle();
		createScriptFileSection();
		createScriptVisualizationSection();
		createScriptEditionSection();
		createScriptExecutionSection();
		createButtonsSection();

		setContentPane(content);
	}

	private void createTitle() {
		// Title
		JLabel label = new JLabel(
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.ScriptConfigurationDialog_Title));
		label.setFont(label.getFont().deriveFont(Font.BOLD, 14));

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.weightx = 1;
		contentLayout.addLayoutComponent(label, constraints);
		content.add(label);
	}

	private void createScriptFileSection() {
		// File
		JLabel label1 = new JLabel(
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.ScriptConfigurationDialog_FileLabel));
		label1.setFont(label1.getFont().deriveFont(Font.BOLD, 12));

		fileChooser = new JFileChooser();

		chooserDirectoryButton = new JButton("...");
		chooserDirectoryButton.addActionListener(this);

		chosenFile = new JTextField();
		chosenFile.setPreferredSize(new Dimension(250, 20));
		chosenFile.setEditable(false);

		BasePanel directory = new BasePanel();
		directory.setBorderSize(1);
		directory.setArc(10);
		directory.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		directory.setLayout(new GridBagLayout());

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1;

		directory.add(label1);
		directory.add(chosenFile, constraints);
		directory.add(chooserDirectoryButton);

		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.insets = new Insets(2, 2, 2, 2);
		constraints.weightx = 1;
		constraints.gridy = 1;
		contentLayout.addLayoutComponent(directory, constraints);
		content.add(directory);
	}

	private void createScriptVisualizationSection() {
		// Script visualization.
		scriptVisualization = new JList<String>(
				dataModel = new DefaultListModel<String>());
		scriptVisualization.setEnabled(false);

		JScrollPane areaScrollPane = new JScrollPane(scriptVisualization);
		areaScrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		areaScrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		areaScrollPane.setPreferredSize(new Dimension(370, 400));

		BasePanel scriptViewer = new BasePanel();
		scriptViewer.setBorderSize(1);
		scriptViewer.setArc(10);
		scriptViewer.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		scriptViewer.setLayout(new GridBagLayout());

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1;
		constraints.weighty = 1;
		scriptViewer.add(areaScrollPane, constraints);

		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(2, 2, 2, 2);
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.gridy = 2;
		contentLayout.addLayoutComponent(scriptViewer, constraints);
		content.add(scriptViewer);
	}

	private void createScriptEditionSection() {

		// Checkbok
		final JCheckBox enableEdition = new JCheckBox(
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.ScriptConfigurationDialog_EnableEditionLabel));
		enableEdition.setSelected(false);
		enableEdition.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if ("".equals(chosenFile.getText())) {
					enableEdition.setSelected(false);
					return;
				} else {
					if (enableEdition.isSelected()) {
						enableEditionComponentes(true);
					} else {
						enableEditionComponentes(false);
					}
				}
			}
		});

		// Labels
		JLabel pauseLabel = new JLabel(
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.ScriptConfigurationDialog_PauseLabel));
		JLabel sleepLabel_1 = new JLabel(
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.ScriptConfigurationDialog_SleepLabel));
		JLabel sleepLabel_2 = new JLabel(",");
		JLabel sleepLabel_3 = new JLabel("s");

		// Fields
		pauseLine = new JComboBox<Integer>();
		pauseLine.setEnabled(false);

		sleepLine = new JComboBox<Integer>();
		sleepLine.setEnabled(false);

		sleepTime = new JTextField();
		sleepTime.setEnabled(false);
		sleepTime.setPreferredSize(new Dimension(50, 20));

		// Buttons
		addPause = new JButton(
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.ScriptConfigurationDialog_AddActionButton));
		addPause.addActionListener(this);
		addSleep = new JButton(
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.ScriptConfigurationDialog_AddActionButton));
		addSleep.addActionListener(this);

		saveScriptFile = new JButton(
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.ScriptConfigurationDialog_SaveButton));
		saveScriptFile.addActionListener(this);

		enableEditionComponentes(false);

		// Pause panel
		JPanel pausePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pausePanel.add(pauseLabel);
		pausePanel.add(pauseLine);
		pausePanel.add(addPause);

		// Sleep panel
		JPanel sleepPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		sleepPanel.add(sleepLabel_1);
		sleepPanel.add(sleepLine);
		sleepPanel.add(sleepLabel_2);
		sleepPanel.add(sleepTime);
		sleepPanel.add(sleepLabel_3);
		sleepPanel.add(addSleep);

		BasePanel edition = new BasePanel();
		edition.setBorderSize(1);
		edition.setArc(10);
		edition.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		edition.setLayout(new GridBagLayout());

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.PAGE_END;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(2, 2, 2, 2);
		constraints.weightx = 1;
		constraints.gridy = 1;
		edition.add(enableEdition, constraints);

		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.LINE_END;
		constraints.insets = new Insets(2, 2, 2, 2);
		constraints.weightx = 1;
		constraints.gridy = 1;
		edition.add(saveScriptFile, constraints);

		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1;
		constraints.gridy = 2;
		edition.add(pausePanel, constraints);

		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1;
		constraints.gridy = 3;
		edition.add(sleepPanel, constraints);

		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.PAGE_END;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(2, 2, 2, 2);
		constraints.weightx = 1;
		constraints.gridy = 3;
		contentLayout.addLayoutComponent(edition, constraints);
		content.add(edition);
	}

	private void createScriptExecutionSection() {
		// Labels
		JLabel label = new JLabel(
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.ScriptConfigurationDialog_ExecuteLabel));

		// Buttons
		debugButton = new JButton(
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.ScriptConfigurationDialog_ExecuteActionButton));
		debugButton.addActionListener(this);

		runScriptButton = new JButton(
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.ScriptConfigurationDialog_ExecuteScriptButton));
		runScriptButton.addActionListener(this);

		enableExecutionComponentes(false);

		// Pause panel
		JPanel debugPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		debugPanel.add(label);
		debugPanel.add(debugButton);
		debugPanel.add(runScriptButton);

		// Sleep panel
		BasePanel edition = new BasePanel();
		edition.setBorderSize(1);
		edition.setArc(10);
		edition.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		edition.setLayout(new GridBagLayout());

		GridBagConstraints constraints = new GridBagConstraints();
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1;
		constraints.gridy = 2;
		edition.add(debugPanel, constraints);

		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.LAST_LINE_END;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(2, 2, 2, 2);
		constraints.weightx = 1;
		constraints.gridy = 4;
		contentLayout.addLayoutComponent(edition, constraints);
		content.add(edition);
	}

	private void createButtonsSection() {
		// Buttons
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setBackground(Color.LIGHT_GRAY);
		buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

		cancelButton = new JButton(
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.UI_Cancel));
		okButton = new JButton(
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.UI_OK));

		cancelButton.addActionListener(this);
		okButton.addActionListener(this);

		cancelButton.setBackground(Color.LIGHT_GRAY);
		okButton.setBackground(Color.LIGHT_GRAY);

		buttonsPanel.add(cancelButton);
		buttonsPanel.add(okButton);

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.LAST_LINE_END;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 5;
		constraints.weightx = 1;
		contentLayout.addLayoutComponent(buttonsPanel, constraints);
		content.add(buttonsPanel);
	}

	public File getScriptFile() {
		return scriptFile;
	}

	private void executeScript() {
		Script.clean();
		Script.loadScript(cScript.readScriptFile(getScriptFile()));
		Iterator<DeviceCommandMessage> it = null;
		it = Script.getMessages().iterator();

		index = -1;

		while (it.hasNext()) {

			index++;

			DeviceCommandMessage d = it.next();
			if (d instanceof KeyCommandMessage) {
				KeyCommandMessage km = ((KeyCommandMessage) d);
				client.sendMessage(km);
				if (km.getKeyCommandType() == KeyCommandMessageType.KEY_UP) {
					wait4Action(1);
				}
			} else if (d instanceof TouchCommandMessage) {
				TouchCommandMessage tm = ((TouchCommandMessage) d);
				client.sendMessage(tm);
				if (tm.getTouchCommandType() == TouchCommandMessageType.TOUCH_UP) {
					wait4Action(1);
				}
			} else if (d instanceof ScriptCommandMessage) {
				ScriptCommandMessage sm = ((ScriptCommandMessage) d);
				if (sm.getActionType() == ScriptCommandMessageType.SLEEP) {
					wait4Action(Long.valueOf(sm.getTimeSeg()));
				} else if (sm.getActionType() == ScriptCommandMessageType.PAUSE) {

					scriptVisualization.setSelectedIndex(index);
					scriptVisualization.ensureIndexIsVisible(index);

					JOptionPane.showMessageDialog(null, "Continue...");

					scriptVisualization.clearSelection();
				}
			}
		}

		index = -1;
	}

	private void executeAction() {

		index++;
		if (index == actions.size()) {
			return;
		}

		scriptVisualization.setSelectedIndex(index);
		scriptVisualization.ensureIndexIsVisible(index);

		DeviceCommandMessage d = actions.elementAt(index);
		if (d instanceof KeyCommandMessage) {
			KeyCommandMessage km = ((KeyCommandMessage) d);
			client.sendMessage(km);
			if (km.getKeyCommandType() == KeyCommandMessageType.KEY_DOWN) {
				executeAction();
			}
		} else if (d instanceof TouchCommandMessage) {
			TouchCommandMessage tm = ((TouchCommandMessage) d);
			client.sendMessage(tm);
			if (tm.getTouchCommandType() != TouchCommandMessageType.TOUCH_UP) {
				executeAction();
			}
		} else if (d instanceof ScriptCommandMessage) {
			ScriptCommandMessage sm = ((ScriptCommandMessage) d);
			if ((sm.getActionType() == ScriptCommandMessageType.SLEEP)
					|| (sm.getActionType() == ScriptCommandMessageType.PAUSE)) {
				executeAction();
			}
		}
	}

	private void updateScriptFile() {
		if (hasChanges) {
			int response = JOptionPane
					.showConfirmDialog(
							null,
							RemoteResourcesLocalization
									.getMessage(
											RemoteResourcesMessages.ScriptConfigurationDialog_SaveFileMessage,
											scriptFile.toString()),
							RemoteResourcesLocalization
									.getMessage(RemoteResourcesMessages.ScriptConfigurationDialog_InformationLabel),
							JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.INFORMATION_MESSAGE);

			if (response == JOptionPane.YES_OPTION) {
				cScript.updateScriptFile(scriptFile, vetScript);
				hasChanges = false;
				saveScriptFile.setEnabled(false);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == chooserDirectoryButton) {
			fileChooser.showDialog(this, null);
			scriptFile = fileChooser.getSelectedFile();

			if (scriptFile == null) {
				return;
			}

			chosenFile.setText(scriptFile.toString());
			vetScript = cScript.readScriptFile(scriptFile);
			refreshScriptLines();
			refreshScriptActions();
			enableExecutionComponentes(true);
		} else if (e.getSource() == okButton) {
			updateScriptFile();
			dispose();
		} else if (e.getSource() == cancelButton) {
			dispose();
		} else if (e.getSource() == addPause) {

			hasChanges = true;
			saveScriptFile.setEnabled(true);

			vetScript.insertElementAt(Script.TAG_PAUSE,
					(int) pauseLine.getSelectedItem());

			refreshScriptLines();
			refreshScriptActions();

		} else if (e.getSource() == addSleep) {
			try { // Is a valid number?
				int x = Integer.parseInt(sleepTime.getText());
				if (x < 0) {
					throw new NumberFormatException();
				}
			} catch (NumberFormatException exp) {
				JOptionPane
						.showMessageDialog(
								null,
								RemoteResourcesLocalization
										.getMessage(RemoteResourcesMessages.ScriptConfigurationDialog_ErrorTimeMessage),
								RemoteResourcesLocalization
										.getMessage(RemoteResourcesMessages.ScriptConfigurationDialog_ErrorLabel),
								JOptionPane.ERROR_MESSAGE);
				return;
			}

			hasChanges = true;
			saveScriptFile.setEnabled(true);

			vetScript.insertElementAt(
					Script.TAG_SLEEP + ":" + sleepTime.getText(),
					(int) sleepLine.getSelectedItem());

			refreshScriptLines();
			refreshScriptActions();
		} else if (e.getSource() == saveScriptFile) {
			if (hasChanges) {
				cScript.updateScriptFile(scriptFile, vetScript);
				hasChanges = false;
				saveScriptFile.setEnabled(false);
			}
		} else if (e.getSource() == debugButton) {
			updateScriptFile();
			if (index == -1) {
				Script.clean();
				Script.loadScript(cScript.readScriptFile(getScriptFile()));
				actions = Script.getMessages();
				runScriptButton.setEnabled(false);
			} else if (index == (actions.size() - 1)) {
				scriptVisualization.setSelectedIndex(0);
				scriptVisualization.ensureIndexIsVisible(0);
				runScriptButton.setEnabled(true);
				index = -1;

				JOptionPane
						.showMessageDialog(
								null,
								RemoteResourcesLocalization
										.getMessage(RemoteResourcesMessages.ScriptConfigurationDialog_FinishedExecutionMessage));

				return;
			}
			executeAction();

		} else if (e.getSource() == runScriptButton) {
			updateScriptFile();
			executeScript();

			JOptionPane
					.showMessageDialog(
							null,
							RemoteResourcesLocalization
									.getMessage(RemoteResourcesMessages.ScriptConfigurationDialog_FinishedExecutionMessage));
		}
	}

	private void wait4Action(long time) {
		try {
			Thread.sleep(time * 1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	private void refreshScriptLines() {
		pauseLine.removeAllItems();
		sleepLine.removeAllItems();
		for (int i = 0; i < vetScript.size(); i++) {
			pauseLine.addItem(i + 1);
			sleepLine.addItem(i + 1);
		}
	}

	private void refreshScriptActions() {
		Iterator<String> it = vetScript.iterator();

		// Skip "serialNumber".
		if (it.hasNext()) {
			it.next();
		}

		// Get number of actions and discovers how many digits it has.
		int total = vetScript.size();
		int nDigits = 0;
		while (total > 0) {
			total = total / 10;
			nDigits++;
		}

		// Aux variables.
		int exp = 1, index = 1;
		nDigits--;
		dataModel.removeAllElements();

		// Add actions.
		while (it.hasNext()) {
			String s = "";
			while (s.length() < (nDigits)) {
				s = s + "0";
			}
			dataModel.add(index - 1, s + index + ". " + it.next());
			index++;
			if (index == Math.pow(10, exp)) {
				nDigits--;
				exp++;
			}
		}
	}

	private void enableEditionComponentes(boolean b) {
		pauseLine.setEnabled(b);
		sleepLine.setEnabled(b);
		sleepTime.setEnabled(b);
		addPause.setEnabled(b);
		addSleep.setEnabled(b);
		scriptVisualization.setEnabled(b);
		saveScriptFile.setEnabled(hasChanges);
	}

	private void enableExecutionComponentes(boolean b) {
		debugButton.setEnabled(b);
		runScriptButton.setEnabled(b);
	}
}
