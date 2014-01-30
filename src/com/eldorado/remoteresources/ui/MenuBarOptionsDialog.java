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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ProgressMonitor;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.eldorado.remoteresources.android.client.ClientLogger;
import com.eldorado.remoteresources.android.client.connection.Client;
import com.eldorado.remoteresources.android.client.connection.DataReceivedListener;
import com.eldorado.remoteresources.android.common.connection.messages.control.KeyCommandMessageType;
import com.eldorado.remoteresources.android.common.connection.messages.control.TypeCommandMessage;
import com.eldorado.remoteresources.i18n.RemoteResourcesLocalization;
import com.eldorado.remoteresources.i18n.RemoteResourcesMessages;
import com.eldorado.remoteresources.ui.model.Script;
import com.eldorado.remoteresources.utils.Command;
import com.eldorado.remoteresources.utils.CommandFactory;
import com.eldorado.remoteresources.utils.ImageUtils;
import com.eldorado.remoteresources.utils.ResourcesLoader;

public class MenuBarOptionsDialog extends JDialog {

	private static final long serialVersionUID = -5406368546124383299L;

	private Client client = null;

	private String serialNumber = null;

	private JButton keyBoardButton = null;

	private JButton cameraButton = null;

	private JButton logButton = null;

	// private JButton runnerButton = null;

	private BasePanel content = null;

	private boolean isDisplayed = false;

	// private boolean isWellLocated = false;

	private final ActionListener inputTextListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			dispose();

			if ((client != null) && (serialNumber != null)) {
				InputTextDialog inputText = new InputTextDialog();
				inputText.setLocationRelativeTo(getParent());
				if (inputText.open()) {
					client.sendMessage(new TypeCommandMessage(client
							.getNextSequenceNumber(), serialNumber, inputText
							.getText()));

					Script.registerActionKey("KEYBOARD", inputText.getText(),
							KeyCommandMessageType.KEY_DOWN);
					Script.registerActionKey("KEYBOARD", inputText.getText(),
							KeyCommandMessageType.KEY_UP);
				}
			}
		}
	};

	private File lastUsedDirectory = null;

	private final ActionListener getScreenshotListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			dispose();

			if ((client != null) && (serialNumber != null)) {
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						"JPG", "jpg");
				JFileChooser chooser = new JFileChooser(lastUsedDirectory);
				chooser.setFileFilter(filter);
				int returnVal = chooser.showSaveDialog(null);
				lastUsedDirectory = chooser.getSelectedFile() != null ? chooser
						.getSelectedFile().getParentFile() : null;
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					final File saveFile = chooser.getSelectedFile();
					final File saveFile2 = new File(saveFile.getAbsolutePath()
							+ ".jpg");
					final ProgressMonitor monitor = new ProgressMonitor(null,
							"Waiting for screenshot", "Waiting", 0, 100);
					monitor.setMillisToDecideToPopup(1);
					monitor.setMillisToPopup(100);
					monitor.setProgress(10);

					client.takeScreenshot(serialNumber,
							new DataReceivedListener() {

								@Override
								public void newFrameReceived(
										String serialNumber, byte[] frameContent) {
									try {
										monitor.setProgress(50);
										ImageIO.write(
												ImageIO.read(ImageUtils
														.decompress(new ByteArrayInputStream(
																frameContent))),
												"jpg", saveFile2);
										monitor.setProgress(99);
									} catch (IOException e) {
										ClientLogger.error(this.getClass(),
												"Unable to decode screenshot",
												e);
									} finally {
										monitor.close();
									}
								}
							});

					Script.registerActionKey("CAMERA", saveFile.toString(),
							KeyCommandMessageType.KEY_DOWN);
					Script.registerActionKey("CAMERA", saveFile.toString(),
							KeyCommandMessageType.KEY_UP);
				}

			}
		}
	};

	// TODO Create a nice ui for adb shell command execution / responses
	private final ActionListener adbshellListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			dispose();

			if ((client != null) && (serialNumber != null)) {

				String command = JOptionPane.showInputDialog(null,
						"Specify adb shell command");

				if (command != null) {
					Command c = CommandFactory.getCommand(command);
					/*
					 * JOptionPane.showMessageDialog(null, c.getCommandType()
					 * .toString() + " " + c.getSubType().toString(),
					 * "Command identified", JOptionPane.INFORMATION_MESSAGE);
					 */
					if (null != c) {

						client.runCommand(serialNumber, c);
						// client.runShellCommand(serialNumber, command);
					} else {
						JOptionPane.showMessageDialog(null, "Invalid command",
								"invalid command", JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(null,
							"Specify an ADB command", "ADB command",
							JOptionPane.ERROR_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(null,
						"Error: there is no device connected.", "ADB command",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	};

	private final ActionListener logcatListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			dispose();

			if ((client != null) && (serialNumber != null)) {
				LogCaptureDialog c = new LogCaptureDialog(
						client.generateNameFileLog(serialNumber));
				c.setLocationRelativeTo(getParent());

				if (c.open()) {
					File path = c.getLogFilePath();
					client.getLogCatCapture(serialNumber, path,
							c.getLogFileName(), c.getLogLevel());

					if (path != null) {
						JOptionPane.showMessageDialog(null,
								"LogCat File was created successfully at ["
										+ path.toString() + "]", "LogCat File",
								JOptionPane.INFORMATION_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(null,
								"LogCat File was created successfully at ["
										+ client.getDefaultLogcatPath() + "]",
								"LogCat File", JOptionPane.INFORMATION_MESSAGE);
					}
				}

				Script.registerActionKey("LOGCAT", c.getLogFileName(),
						KeyCommandMessageType.KEY_DOWN);
				Script.registerActionKey("LOGCAT", c.getLogFileName(),
						KeyCommandMessageType.KEY_UP);

			} else {
				JOptionPane.showMessageDialog(null,
						"Error: there is no device connected.", "LogCat File",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	};

	private final ActionListener runnerListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {

			dispose();

			if ((client != null) && (serialNumber != null)) {
				ScriptConfigurationDialog dialog = new ScriptConfigurationDialog(
						client);
			}
		}
	};

	private final WindowFocusListener windowListener = new WindowFocusListener() {

		@Override
		public void windowLostFocus(WindowEvent e) {
			dispose();

		}

		@Override
		public void windowGainedFocus(WindowEvent e) {

		}
	};

	public MenuBarOptionsDialog() {
		setUndecorated(true);
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setModal(false);
		initComponents();
	}

	private void initComponents() {
		content = new BasePanel();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

		keyBoardButton = addButton(
				ResourcesLoader.getIcon("keyboard", 48),
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.MenuBarOptionsDialog_Tooltip_ImputText),
				content);
		keyBoardButton.addActionListener(inputTextListener);

		cameraButton = addButton(
				ResourcesLoader.getIcon("camera", 48),
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.MenuBarOptionsDialog_Tooltip_GetScreenshot),
				content);
		cameraButton.addActionListener(getScreenshotListener);

		logButton = addButton(
				ResourcesLoader.getIcon("log", 48),
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.MenuBarOptionsDialog_Tooltip_GetLog),
				content);
		logButton.addActionListener(logcatListener);

		/*
		 * Script feature disabled for now
		 * 
		 * runnerButton = addButton( ResourcesLoader.getIcon("playScript", 48),
		 * RemoteResourcesLocalization .getMessage(RemoteResourcesMessages.
		 * MenuBarOptionsDialog_Tooltip_PlayScript), content);
		 * runnerButton.addActionListener(runnerListener);
		 */

		setContentPane(content);

	}

	private JButton addButton(Icon icon, String tooltip, JPanel panel) {
		JButton button = new JButton();
		button.setIcon(icon);
		button.setToolTipText(tooltip);
		panel.add(button);
		return button;
	}

	public void open(Component c) {
		isDisplayed = true;
		pack();
		setLocationRelativeTo(c);
		setVisible(true);
		addWindowFocusListener(windowListener);

	}

	public void setConection(Client client, String serialNumber) {
		this.client = client;
		this.serialNumber = serialNumber;
	}

	public boolean isDisplayed() {
		return isDisplayed;
	}

	@Override
	public void dispose() {
		super.dispose();
		isDisplayed = false;
		removeWindowFocusListener(windowListener);
	}

	@Override
	public void setLocationRelativeTo(Component c) {
		// if (isWellLocated == false) {
		// super.setLocationRelativeTo(c);
		setLocation(c.getLocationOnScreen().x, c.getLocationOnScreen().y - 175);
		// isWellLocated = true;
		// }
	}
}
