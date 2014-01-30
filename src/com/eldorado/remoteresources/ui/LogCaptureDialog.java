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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.eldorado.remoteresources.android.common.connection.messages.control.ControlLogCaptureTypeMessage;
import com.eldorado.remoteresources.i18n.RemoteResourcesLocalization;
import com.eldorado.remoteresources.i18n.RemoteResourcesMessages;

/**
 * This dialog is responsible for capture logcat files
 * 
 * @author Michel Silva Fornaciali
 * 
 */
public class LogCaptureDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 8863129064446542491L;

	private static String log_file_name = null;

	private File directory;

	private JRadioButton verbose;

	private JRadioButton info;

	private JRadioButton warning;

	private JRadioButton error;

	private ControlLogCaptureTypeMessage logLevel;

	private boolean okPressed = false;

	private JTextField chosendirectory;

	private JFileChooser fileChooser;

	private JButton chooserDirectoryButton;

	private JButton okButton;

	private JButton cancelButton;

	private BasePanel content = null;

	private GridBagLayout contentLayout = null;

	static Point mouseDownCompCoords;

	public LogCaptureDialog(String log_file_name) {
		LogCaptureDialog.log_file_name = log_file_name;
		setUndecorated(true);
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setModal(true);
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

		// Title

		JLabel label = new JLabel(
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.LogCaptureDialog_Label));
		label.setFont(label.getFont().deriveFont(Font.BOLD, 14));

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.weightx = 1;
		contentLayout.addLayoutComponent(label, constraints);
		content.add(label);

		// Directory

		JLabel label1 = new JLabel(
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.LogCaptureDialog_DirectoryLabel));
		label1.setFont(label.getFont().deriveFont(Font.BOLD, 12));

		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		chooserDirectoryButton = new JButton("...");
		chooserDirectoryButton.setBackground(Color.LIGHT_GRAY);
		chooserDirectoryButton.addActionListener(this);

		chosendirectory = new JTextField();
		chosendirectory.setText(log_file_name);
		chosendirectory.setPreferredSize(new Dimension(250, 20));

		BasePanel directory = new BasePanel();
		directory.setBorderSize(1);
		directory.setArc(10);
		directory.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		directory.setLayout(new FlowLayout(FlowLayout.LEFT));

		directory.add(label1);
		directory.add(chosendirectory);
		directory.add(chooserDirectoryButton);

		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.insets = new Insets(2, 2, 2, 2);
		constraints.weightx = 1;
		constraints.gridy = 1;
		contentLayout.addLayoutComponent(directory, constraints);
		content.add(directory);

		// Level
		verbose = new JRadioButton("Verbose");
		info = new JRadioButton("Info");
		warning = new JRadioButton("Warning");
		error = new JRadioButton("Error");
		info.setSelected(true);

		ButtonGroup levelGroup = new ButtonGroup();
		levelGroup.add(verbose);
		levelGroup.add(info);
		levelGroup.add(warning);
		levelGroup.add(error);

		JLabel label2 = new JLabel(
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.LogCaptureDialog_LogLevel));
		label2.setFont(label.getFont().deriveFont(Font.BOLD, 12));

		BasePanel logLevel = new BasePanel();
		logLevel.setBorderSize(1);
		logLevel.setArc(10);
		logLevel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		logLevel.setLayout(new FlowLayout(FlowLayout.LEFT));
		logLevel.add(label2);
		logLevel.add(verbose);
		logLevel.add(info);
		logLevel.add(warning);
		logLevel.add(error);

		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(2, 2, 2, 2);
		constraints.weightx = 1;
		constraints.gridy = 2;
		contentLayout.addLayoutComponent(logLevel, constraints);
		content.add(logLevel);

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

		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.LAST_LINE_END;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.weightx = 1;
		constraints.weighty = 1;
		contentLayout.addLayoutComponent(buttonsPanel, constraints);
		content.add(buttonsPanel);

		setContentPane(content);
	}

	public boolean open() {
		pack();
		setVisible(true);
		return okPressed;
	}

	public File getLogFilePath() {
		return directory;
	}

	public String getLogFileName() {

		if (directory == null) {
			return chosendirectory.getText();
		}

		return chosendirectory.getText().replace(
				directory.toString() + File.separator, "");
	}

	public ControlLogCaptureTypeMessage getLogLevel() {
		return logLevel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == chooserDirectoryButton) {

			fileChooser.showDialog(this, null);

			directory = fileChooser.getSelectedFile();
			if (directory != null) {
				chosendirectory.setText(directory.toString() + File.separator
						+ log_file_name);
			}
		} else if (e.getSource() == okButton) {
			logLevel = getLevel();
			okPressed = true;
			dispose();
		} else if (e.getSource() == cancelButton) {
			okPressed = false;
			dispose();
		}

	}

	private ControlLogCaptureTypeMessage getLevel() {
		if (verbose.isSelected()) {
			return ControlLogCaptureTypeMessage.VERBOSE;
		} else if (info.isSelected()) {
			return ControlLogCaptureTypeMessage.INFO;
		} else if (warning.isSelected()) {
			return ControlLogCaptureTypeMessage.WARN;
		} else {
			return ControlLogCaptureTypeMessage.ERROR;
		}
	}
}
