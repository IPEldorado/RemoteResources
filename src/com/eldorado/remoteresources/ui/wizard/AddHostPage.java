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

package com.eldorado.remoteresources.ui.wizard;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import com.eldorado.remoteresources.android.client.connection.Client;
import com.eldorado.remoteresources.android.client.connection.ClientConnectionManager;
import com.eldorado.remoteresources.i18n.RemoteResourcesLocalization;
import com.eldorado.remoteresources.i18n.RemoteResourcesMessages;
import com.eldorado.remoteresources.ui.actions.IPValidator;
import com.eldorado.remoteresources.ui.model.Host;
import com.eldorado.remoteresources.ui.model.PersistentDeviceModel;
import com.eldorado.remoteresources.ui.wizard.IWizard.WizardStatus;

/**
 * This class is responsible to provide a way to let user choose which host to
 * add
 * 
 * @author Marcelo Marzola Bossoni
 * 
 */
public class AddHostPage extends ModelModificationWizardPage implements
		KeyListener {

	private static final long serialVersionUID = -2460333944369458505L;

	private static final int MAX_REMOTENAME_CHARS = 35;

	private static final int MAX_REMOTEHOST_CHARS = 15;

	private static final int MAX_REMOTEPORT_CHARS = 5;

	private final JTextField remoteNameText;

	private final JTextField remoteHostText;

	private final JTextField remotePortText;

	private final JRadioButton registredHostRadio, newHostRadio;

	private final JLabel remoteHostName, remoteHostLabel, remotePortLabel,
			existsHostComboBoxLabel;

	private final JComboBox<String> existsHostCombo;

	private final GridBagLayout layout;

	private final Host hostToAdd;

	private static final String COLON = " : "; //$NON-NLS-1$

	private static final String HYPHEN = " - "; //$NON-NLS-1$

	private boolean isChangedNickname = false;

	public AddHostPage(PersistentDeviceModel model, Host hostToAdd) {
		super(model);
		setDescription(RemoteResourcesLocalization
				.getMessage(RemoteResourcesMessages.AddHostPage_UI_PageDescription));
		this.hostToAdd = hostToAdd;
		setLayout(layout = new GridBagLayout());

		GridBagConstraints constraints;

		ActionListener radioActionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				AbstractButton radioAbstractButton = (AbstractButton) actionEvent
						.getSource();

				if (radioAbstractButton.getName().equals("newHostRadio")) { //$NON-NLS-1$
					registredHostRadio.setSelected(false);
					remoteNameText.setEnabled(true);
					remoteHostText.setEnabled(true);
					remotePortText.setEnabled(true);
					remoteHostName.setEnabled(true);
					remoteHostLabel.setEnabled(true);
					remotePortLabel.setEnabled(true);
					existsHostComboBoxLabel.setEnabled(false);
					existsHostCombo.setEnabled(false);
					validatePage();
				}

				if (radioAbstractButton.getName().equals("registredHostRadio")) { //$NON-NLS-1$
					newHostRadio.setSelected(false);
					remoteNameText.setEnabled(false);
					remoteHostText.setEnabled(false);
					remotePortText.setEnabled(false);
					remoteHostName.setEnabled(false);
					remoteHostLabel.setEnabled(false);
					remotePortLabel.setEnabled(false);
					existsHostComboBoxLabel.setEnabled(true);
					existsHostCombo.setEnabled(true);
					validatePage();
				}
			}

		};

		KeyListener keyListener = new KeyListener() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				isChangedNickname = true;

			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				isChangedNickname = true;

			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				isChangedNickname = true;

			}
		};

		newHostRadio = new JRadioButton(
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.AddHostPage_UI_Radio_NewHost_Label));

		newHostRadio.setBackground(Color.LIGHT_GRAY);
		newHostRadio.setName("newHostRadio"); //$NON-NLS-1$
		newHostRadio.addActionListener(radioActionListener);
		newHostRadio.setSelected(true);

		constraints = new GridBagConstraints();
		constraints.gridy = 0;
		constraints.insets = new Insets(2, 2, 2, 2);

		layout.addLayoutComponent(newHostRadio, constraints);
		constraints = new GridBagConstraints();
		constraints.gridy = 0;
		constraints.weightx = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(2, 2, 2, 2);
		add(newHostRadio);

		remoteHostLabel = new JLabel(
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.AddHostPage_UI_NewHost_HostAddress));
		remoteHostText = new JTextField();
		remoteHostText.getDocument().addDocumentListener(
				new ValidatorListener());
		remoteHostText.getDocument().addDocumentListener(
				new UnchangedNicknameNickname());
		remoteHostText.addKeyListener(this);
		constraints = new GridBagConstraints();
		constraints.gridy = 1;
		constraints.insets = new Insets(2, 2, 2, 2);

		layout.addLayoutComponent(remoteHostLabel, constraints);
		constraints = new GridBagConstraints();
		constraints.gridy = 1;
		constraints.weightx = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(2, 2, 2, 2);
		layout.addLayoutComponent(remoteHostText, constraints);
		add(remoteHostLabel);
		add(remoteHostText);

		remotePortLabel = new JLabel(
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.AddHostPage_UI_NewHost_HostPort));
		remotePortText = new JTextField("54321");
		remotePortText.getDocument().addDocumentListener(
				new ValidatorListener());
		remotePortText.getDocument().addDocumentListener(
				new UnchangedNicknameNickname());
		remotePortText.addKeyListener(this);
		constraints = new GridBagConstraints();
		constraints.gridy = 2;
		constraints.insets = new Insets(2, 2, 2, 2);

		layout.addLayoutComponent(remotePortLabel, constraints);
		constraints = new GridBagConstraints();
		constraints.gridy = 2;
		constraints.weightx = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(2, 2, 2, 2);
		layout.addLayoutComponent(remotePortText, constraints);
		add(remotePortLabel);
		add(remotePortText);

		remoteHostName = new JLabel(
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.AddHostPage_UI_NewHost_HostNickname));
		remoteNameText = new JTextField();
		remoteNameText.getDocument().addDocumentListener(
				new ValidatorListener());
		remoteNameText.addKeyListener(keyListener);
		remoteNameText.addKeyListener(this);
		constraints = new GridBagConstraints();
		constraints.gridy = 3;
		constraints.insets = new Insets(2, 2, 30, 2);

		layout.addLayoutComponent(remoteHostName, constraints);

		constraints = new GridBagConstraints();
		constraints.gridy = 3;
		constraints.weightx = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(2, 2, 30, 2);
		layout.addLayoutComponent(remoteNameText, constraints);
		add(remoteHostName);
		add(remoteNameText);

		constraints = new GridBagConstraints();
		constraints.gridy = 4;
		constraints.insets = new Insets(2, 2, 2, 2);

		registredHostRadio = new JRadioButton(
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.AddHostPage_UI_Radio_ExistingHost_Label));
		registredHostRadio.setBackground(Color.LIGHT_GRAY);
		registredHostRadio.setName("registredHostRadio"); //$NON-NLS-1$
		registredHostRadio.addActionListener(radioActionListener);

		layout.addLayoutComponent(registredHostRadio, constraints);
		constraints = new GridBagConstraints();
		constraints.gridy = 4;
		constraints.weightx = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(2, 2, 2, 2);
		add(registredHostRadio);

		existsHostComboBoxLabel = new JLabel(
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.AddHostPage_UI_HostsComboLabel));
		existsHostCombo = new JComboBox<String>(registredHosts(getModel()
				.getHosts(), getModel().getHosts().size()));

		existsHostCombo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				validatePage();
			}
		});
		constraints = new GridBagConstraints();
		constraints.gridy = 5;
		constraints.insets = new Insets(2, 0, 2, 2);

		layout.addLayoutComponent(existsHostComboBoxLabel, constraints);
		constraints = new GridBagConstraints();
		constraints.gridy = 5;
		constraints.weightx = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(2, 0, 2, 2);
		layout.addLayoutComponent(existsHostCombo, constraints);
		add(existsHostComboBoxLabel);
		add(existsHostCombo);
		existsHostComboBoxLabel.setEnabled(false);
		existsHostCombo.setEnabled(false);

		if (getModel().getHosts().size() == 0) {
			registredHostRadio.setEnabled(false);
			newHostRadio.setEnabled(false);
			existsHostComboBoxLabel.setEnabled(false);
			existsHostCombo.setEnabled(false);
		}
	}

	private String[] registredHosts(Collection<Host> listHosts, int amountHosts) {

		Iterator<Host> cHosts = listHosts.iterator();
		String[] vetHosts = new String[amountHosts];

		int pos = 0;
		while (cHosts.hasNext()) {
			Host h = cHosts.next();
			vetHosts[pos] = h.getName() + HYPHEN + h.getHostname() + COLON
					+ String.valueOf(h.getPort());
			pos++;
		}

		return vetHosts;
	}

	private class ValidatorListener implements DocumentListener {

		@Override
		public void insertUpdate(DocumentEvent e) {
			validatePage();

		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			validatePage();

		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			validatePage();

		}

	}

	private class UnchangedNicknameNickname implements DocumentListener {

		@Override
		public void insertUpdate(DocumentEvent e) {
			changeNickname();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			changeNickname();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			changeNickname();
		}

	}

	protected void changeNickname() {
		if (!isChangedNickname) {
			remoteNameText.setText(remoteHostText.getText() + COLON
					+ remotePortText.getText());
		}
	}

	@Override
	protected void validatePage() {
		String name = null;
		String host = null;
		String port = null;

		String errorMessage = null;
		WizardStatus level = WizardStatus.OK;

		if (newHostRadio.isSelected()) {

			name = remoteNameText.getText().trim();
			host = remoteHostText.getText().trim();
			port = remotePortText.getText().trim();

			if (host.trim().isEmpty()) {
				errorMessage = RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.AddHostPage_Error_EmptyHostname);
				level = WizardStatus.ERROR;
			}
			if (isExistingHost(name)) {
				errorMessage = RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.AddHostPage_Error_ExistingHostNickname);
				level = WizardStatus.ERROR;
			}
			if ((errorMessage == null)
					&& (!isPositiveNumber(port) || port.trim().isEmpty())) {
				errorMessage = RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.AddHostPage_Error_InvalidPort);
				level = WizardStatus.ERROR;
			}
			if (errorMessage == null) {
				if (!IPValidator.isIPFormat(host)) {
					errorMessage = RemoteResourcesLocalization
							.getMessage(RemoteResourcesMessages.AddHostPage_Warning_NoHostValidationPerformed);
					level = WizardStatus.WARNING;
				} else {
					if (!IPValidator.isIPV4(host) && !IPValidator.isIPV6(host)) {
						errorMessage = RemoteResourcesLocalization
								.getMessage(RemoteResourcesMessages.AddHostPage_Error_InvalidIPAddress);
						level = WizardStatus.ERROR;
					}
				}
			}

			if (!remoteNameText.isEnabled() && !remoteHostText.isEnabled()
					&& !remotePortText.isEnabled()) {
				level = WizardStatus.ERROR;
			}

		} else if (registredHostRadio.isSelected()) {

			String selectedHost = existsHostCombo.getSelectedItem().toString();
			name = selectedHost.split(HYPHEN)[0];
			host = selectedHost.split(HYPHEN)[1].split(COLON)[0];
			port = selectedHost.split(HYPHEN)[1].split(COLON)[1];
		}

		setErrorMessage(errorMessage);
		setErrorLevel(level);
		setActivatedOnce();
		if (level != WizardStatus.ERROR) {
			hostToAdd.setName(name.trim());
			hostToAdd.setHostname(host.trim());
			hostToAdd.setPort(Integer.parseInt(port.trim()));
		}

		super.validatePage();
	}

	private boolean isExistingHost(String nickname) {
		return (getModel().getHost(nickname) != null)
				|| PersistentDeviceModel.LOCALHOST.equalsIgnoreCase(nickname);
	}

	private boolean isPositiveNumber(String num) {
		boolean isPositive = false;

		if (num.length() != 0) {
			try {
				isPositive = Integer.parseInt(num) > 0 ? true : false;
			} catch (NumberFormatException e) {

			}
		}

		return isPositive;
	}

	@Override
	public List<SwingWorker<Boolean, Void>> getPreHideTasks() {
		List<SwingWorker<Boolean, Void>> tasks = new ArrayList<SwingWorker<Boolean, Void>>();
		SwingWorker<Boolean, Void> task = new SwingWorker<Boolean, Void>() {

			@Override
			protected Boolean doInBackground() throws Exception {
				setProgress(0);
				Client client = ClientConnectionManager
						.getInstance()
						.getClient(hostToAdd.getHostname(), hostToAdd.getPort());
				setProgress(20);
				if (client == null) {
					try {
						client = ClientConnectionManager.getInstance().connect(
								hostToAdd.getHostname(), hostToAdd.getPort());
						setProgress(80);
						while (!client.isConnected()) {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								// do nothing
							}
						}

					} catch (UnknownHostException e) {
						setErrorMessage(RemoteResourcesLocalization
								.getMessage(RemoteResourcesMessages.AddHostPage_Error_UnknownHost));
						setErrorLevel(WizardStatus.ERROR);
					} catch (IOException e) {
						setErrorMessage(RemoteResourcesLocalization
								.getMessage(RemoteResourcesMessages.AddHostPage_Error_UnableToConnect));
						setErrorLevel(WizardStatus.ERROR);
					}
				}

				setProgress(100);
				if (client != null) {
					validatePage();
					return true;
				}
				return false;
			}

		};
		tasks.add(task);
		return tasks;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
		JTextField source = (JTextField) e.getSource();
		int charLimit = 0;
		char c = e.getKeyChar();

		if (source == remoteHostText) {
			charLimit = MAX_REMOTEHOST_CHARS;
			if (((c < '0') || (c > '9')) && (c != '.')) {
				e.consume();
				return;
			}
		} else if (source == remotePortText) {
			charLimit = MAX_REMOTEPORT_CHARS;
			if ((c < '0') || (c > '9')) {
				e.consume();
				return;
			}
		} else if (source == remoteNameText) {
			charLimit = MAX_REMOTENAME_CHARS;
		} else {
			return;
		}

		if (source.getText().length() >= charLimit) {
			try {
				e.consume();
				source.setText(source.getText(0, charLimit));
			} catch (BadLocationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
}
