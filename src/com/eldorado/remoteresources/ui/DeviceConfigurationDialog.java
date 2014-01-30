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
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;

import com.eldorado.remoteresources.android.common.connection.IConnectionConstants;
import com.eldorado.remoteresources.i18n.RemoteResourcesLocalization;
import com.eldorado.remoteresources.i18n.RemoteResourcesMessages;
import com.eldorado.remoteresources.ui.model.Device;
import com.eldorado.remoteresources.ui.model.PersistentDeviceModel;
import com.eldorado.remoteresources.utils.DeviceUtils;

/**
 * This dialog is responsible for show and configure devices
 * 
 * @author Marcelo Marzola Bossoni
 * 
 */
public class DeviceConfigurationDialog extends JDialog implements
		ActionListener {

	private static final long serialVersionUID = 8863129064446542491L;

	private final Device device;

	private boolean okPressed = false;

	private JButton okButton;

	private JButton cancelButton;

	private JButton editNameButton;

	private BasePanel content = null;

	private GridBagLayout contentLayout = null;

	private JComboBox<String> pollingRateCombo;

	private JComboBox<String> imageQualityCombo;

	private final Map<String, String> reverseRateMap = new HashMap<String, String>();

	private final Map<String, String> reverseQualityMap = new HashMap<String, String>();

	private JCheckBox generateScript;

	private JTextField deviceName;

	// Based on the size of the text field
	private final int MAX_DEVICE_NAME_CHARS = 35;

	private String deviceOldName = "";

	private JFileChooser fileChooser;

	private JButton chooserDirectoryButton;

	private JTextField chosenDirectory;

	private File directory;

	private final PersistentDeviceModel model;

	static Point mouseDownCompCoords;

	public DeviceConfigurationDialog(Device device, PersistentDeviceModel model) {
		setUndecorated(true);
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setModal(true);
		this.device = device;
		this.model = model;
		initComponents();

		// Methods to move the window
		mouseDownCompCoords = null;

		addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				mouseDownCompCoords = null;
			}

			@Override
			public void mousePressed(MouseEvent e) {
				mouseDownCompCoords = e.getPoint();
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});

		addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent e) {
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				Point currCoords = e.getLocationOnScreen();
				setLocation(currCoords.x - mouseDownCompCoords.x, currCoords.y
						- mouseDownCompCoords.y);
			}
		});
	}

	private void initComponents() {
		content = new BasePanel();
		content.setBorderSize(0);
		content.setArc(0);
		content.setBorderColor(Color.BLACK);
		content.setBackground(Color.LIGHT_GRAY);
		content.setOpaque(false);
		content.setLayout(contentLayout = new GridBagLayout());
		content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JLabel label = new JLabel(
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.DeviceConfigurationDialog_Title));
		label.setFont(label.getFont().deriveFont(Font.BOLD, 14));

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.weightx = 1;
		constraints.gridy = 0;
		contentLayout.addLayoutComponent(label, constraints);
		content.add(label);

		createInfoSection();
		createConfigSection();
		createConfigScriptSection();

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

		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.LAST_LINE_END;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.weightx = 1;
		constraints.weighty = 1;
		contentLayout.addLayoutComponent(buttonsPanel, constraints);
		content.add(buttonsPanel);

		setContentPane(content);
	}

	private void createConfigSection() {
		BasePanel configSection = new BasePanel();
		configSection.setBorderSize(1);
		configSection.setArc(10);
		configSection.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		GridBagLayout configSectionLayout;
		configSection.setLayout(configSectionLayout = new GridBagLayout());

		JLabel pollingRateLabel = new JLabel(
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.DeviceConfigurationDialog_PollingLabel));
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.gridx = 0;
		constraints.insets = new Insets(2, 2, 2, 2);
		configSectionLayout.addLayoutComponent(pollingRateLabel, constraints);
		configSection.add(pollingRateLabel);

		pollingRateCombo = new JComboBox<String>();
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1;
		constraints.insets = new Insets(2, 2, 2, 2);
		configSectionLayout.addLayoutComponent(pollingRateCombo, constraints);
		configSection.add(pollingRateCombo);

		JLabel imageQualityLabel = new JLabel(
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.DeviceConfigurationDialog_QualityLabel));
		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.gridy = 1;
		constraints.insets = new Insets(2, 2, 2, 2);
		constraints.gridx = 0;
		configSectionLayout.addLayoutComponent(imageQualityLabel, constraints);
		configSection.add(imageQualityLabel);

		imageQualityCombo = new JComboBox<String>();
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridy = 1;
		constraints.weightx = 1;
		constraints.insets = new Insets(2, 2, 2, 2);
		configSectionLayout.addLayoutComponent(imageQualityCombo, constraints);
		configSection.add(imageQualityCombo);

		populateCombos();

		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 0;
		constraints.insets = new Insets(4, 4, 4, 4);
		constraints.weightx = 1;

		contentLayout.addLayoutComponent(configSection, constraints);
		content.add(configSection);
	}

	private void createConfigScriptSection() {
		BasePanel configScriptSection = new BasePanel();
		configScriptSection.setBorderSize(1);
		configScriptSection.setArc(10);
		configScriptSection.setBorder(BorderFactory.createEmptyBorder(6, 6, 6,
				6));
		configScriptSection.setLayout(new GridBagLayout());

		generateScript = new JCheckBox(
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.DeviceConfigurationDialog_ActivateScriptGeneration));
		generateScript.setSelected(device.getScriptGeneration());
		generateScript.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				if (generateScript.isSelected()) {
					chooserDirectoryButton.setEnabled(true);
					chosenDirectory.setEnabled(true);
				} else {
					chooserDirectoryButton.setEnabled(false);
					chosenDirectory.setEnabled(false);
				}
			}
		});

		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		JLabel label = new JLabel(
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.DeviceConfigurationDialog_ScriptLocation));

		chosenDirectory = new JTextField();
		chosenDirectory.setPreferredSize(new Dimension(280, 20));
		chosenDirectory.setEnabled(device.getScriptGeneration());
		chosenDirectory.setText(device.getProperty(Device.SCRIPT_PATH)
				+ File.separator + device.getProperty(Device.SCRIPT_NAME));

		chooserDirectoryButton = new JButton("...");
		chooserDirectoryButton.addActionListener(this);
		chooserDirectoryButton.setEnabled(device.getScriptGeneration());

		JPanel panel = new JPanel();
		panel.add(label);
		panel.add(chosenDirectory);
		panel.add(chooserDirectoryButton);

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridy = 0;
		constraints.weightx = 1;
		// configScriptSection.add(generateScript, constraints);

		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridy = 1;
		constraints.weightx = 1;
		// configScriptSection.add(panel, constraints);

		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.insets = new Insets(4, 4, 4, 4);
		contentLayout.addLayoutComponent(configScriptSection, constraints);
		// content.add(configScriptSection);
	}

	private void populateCombos() {
		String imgQuality = device.getProperty(Device.IMAGE_QUALITY);
		String pollRate = device.getProperty(Device.POLLING_RATE);
		int qualityPosition = 0;
		int pollPosition = 0;

		if (imgQuality == null) {
			imgQuality = Integer
					.toString(IConnectionConstants.DEFAULT_IMAGE_QUALITY);
		}

		if (pollRate == null) {
			pollRate = Long.toString(IConnectionConstants.DEFAULT_FRAME_DELAY);
		}

		int i = 0;
		for (String rate : IConnectionConstants.pollingRates.keySet()) {
			reverseRateMap.put(IConnectionConstants.pollingRates.get(rate),
					rate);
			pollingRateCombo.addItem(IConnectionConstants.pollingRates
					.get(rate));
			if (rate.equals(pollRate)) {
				pollPosition = i;
			}
			i++;

		}

		i = 0;
		for (String quality : IConnectionConstants.qualities.keySet()) {
			reverseQualityMap.put(IConnectionConstants.qualities.get(quality),
					quality);
			imageQualityCombo.addItem(IConnectionConstants.qualities
					.get(quality));
			if (quality.equals(imgQuality)) {
				qualityPosition = i;
			}
			i++;
		}

		pollingRateCombo.setSelectedIndex(pollPosition);
		imageQualityCombo.setSelectedIndex(qualityPosition);
	}

	private void createInfoSection() {

		BasePanel infoSection = new BasePanel();
		infoSection.setBorderSize(1);
		infoSection.setArc(10);
		infoSection.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		GridBagLayout infoSectionLayout;
		infoSection.setLayout(infoSectionLayout = new GridBagLayout());

		createNameLine(
				infoSection,
				infoSectionLayout,
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.DeviceConfigurationDialog_DeviceName),
				device.getName());
		createInfoLine(
				infoSection,
				infoSectionLayout,
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.DeviceConfigurationDialog_DeviceSerialNumber),
				device.getSerialNumber());
		createInfoLine(
				infoSection,
				infoSectionLayout,
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.DeviceConfigurationDialog_DeviceHost),
				device.getHost());

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 0;
		constraints.weightx = 1;
		constraints.insets = new Insets(4, 4, 4, 4);

		contentLayout.addLayoutComponent(infoSection, constraints);
		content.add(infoSection);
	}

	private void createNameLine(JPanel parent, GridBagLayout parentLayout,
			String label, String content) {
		JLabel infoLineLabel = new JLabel(label);
		infoLineLabel.setFont(infoLineLabel.getFont().deriveFont(Font.BOLD,
				infoLineLabel.getFont().getSize() + 2));
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(2, 2, 2, 2);
		parentLayout.addLayoutComponent(infoLineLabel, constraints);
		parent.add(infoLineLabel);

		deviceName = new JTextField(content);
		deviceName.setFont(deviceName.getFont().deriveFont(
				(float) (deviceName.getFont().getSize() + 2)));
		deviceName.setBorder(null);
		deviceName.setEditable(false);
		deviceName.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				if (deviceName.getText().length() >= MAX_DEVICE_NAME_CHARS) {
					try {
						arg0.consume();
						deviceName.setText(deviceName.getText(0,
								MAX_DEVICE_NAME_CHARS));
					} catch (BadLocationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		});

		constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.weightx = 1;
		constraints.insets = new Insets(2, 2, 2, 2);
		constraints.fill = GridBagConstraints.HORIZONTAL;

		if (!DeviceUtils.getInstance().changeNickname(device.getSerialNumber(),
				deviceName.getText(), model)) {
			DeviceUtils.getInstance().setDeviceNickname(
					device.getSerialNumber(), deviceName.getText());

		}

		parentLayout.addLayoutComponent(deviceName, constraints);
		parent.add(deviceName);

		editNameButton = new JButton(
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.DeviceConfigurationDialog_EditNicknameButton));
		editNameButton.setBackground(null);
		editNameButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if ("OK".equals(editNameButton.getText())) {
					deviceName.setEditable(false);
					editNameButton.setText(RemoteResourcesLocalization
							.getMessage(RemoteResourcesMessages.DeviceConfigurationDialog_EditNicknameButton));
				} else {
					deviceName.setEditable(true);
					editNameButton.setText("OK");
				}
			}
		});
		parent.add(editNameButton);

		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(2, 2, 2, 2);
		constraints.gridx = 0;
		constraints.weightx = 1;
	}

	private JLabel createInfoLine(JPanel parent, GridBagLayout parentLayout,
			String label, String content) {
		JLabel infoLineLabel = new JLabel(label);
		infoLineLabel.setFont(infoLineLabel.getFont().deriveFont(Font.BOLD,
				infoLineLabel.getFont().getSize() + 2));
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(2, 2, 2, 2);
		parentLayout.addLayoutComponent(infoLineLabel, constraints);
		parent.add(infoLineLabel);

		JLabel infoLineText = new JLabel(content);
		infoLineText.setFont(infoLineText.getFont().deriveFont(
				(float) (infoLineText.getFont().getSize() + 2)));

		constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.weightx = 1;
		constraints.insets = new Insets(2, 2, 2, 2);
		constraints.fill = GridBagConstraints.HORIZONTAL;

		parentLayout.addLayoutComponent(infoLineText, constraints);
		parent.add(infoLineText);

		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(2, 2, 2, 2);
		constraints.gridx = 0;
		constraints.weightx = 1;

		return infoLineText;
	}

	public boolean open() {
		pack();
		setMinimumSize(new Dimension(getSize().width + 40,
				getSize().height + 40));

		pack();
		setVisible(true);
		return okPressed;
	}

	public String getDeviceOldName() {
		return deviceOldName;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == okButton) {
			okPressed = true;
			device.setProperty(Device.IMAGE_QUALITY,
					reverseQualityMap.get(imageQualityCombo.getSelectedItem()));
			device.setProperty(Device.POLLING_RATE,
					reverseRateMap.get(pollingRateCombo.getSelectedItem()));
			deviceOldName = device.getName();

			device.setScriptGeneration(generateScript.isSelected());
			if (directory != null) {
				device.setProperty(Device.SCRIPT_PATH, directory.toString());
				device.setProperty(
						Device.SCRIPT_NAME,
						chosenDirectory
								.getText()
								.replace(directory.toString() + File.separator,
										"")
								.replace(device.getName(), deviceName.getText()));
			} else {
				device.setProperty(
						Device.SCRIPT_NAME,
						chosenDirectory
								.getText()
								.replace(
										device.getProperty(Device.SCRIPT_PATH)
												+ File.separator, "")
								.replace(device.getName(), deviceName.getText()));
			}

			if (DeviceUtils.getInstance().changeNickname(
					device.getSerialNumber(), deviceName.getText(), model)) {
				device.setName(deviceName.getText());
				dispose();
			} else {
				JOptionPane
						.showConfirmDialog(
								null,
								RemoteResourcesLocalization
										.getMessage(RemoteResourcesMessages.DeviceConfigurationDialog_EditNickNameError),
								"Device name already registered",
								JOptionPane.DEFAULT_OPTION,
								JOptionPane.ERROR_MESSAGE);
			}
		} else if (e.getSource() == cancelButton) {
			okPressed = false;
			dispose();
		} else if (e.getSource() == chooserDirectoryButton) {
			fileChooser.showDialog(this, null);
			directory = fileChooser.getSelectedFile();
			if (directory != null) {
				chosenDirectory.setText(directory.toString() + File.separator
						+ device.getProperty(Device.SCRIPT_NAME));
			}
		}
	}
}
