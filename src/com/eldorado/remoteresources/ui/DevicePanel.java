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
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.eldorado.remoteresources.android.client.AndroidDeviceClient;
import com.eldorado.remoteresources.android.client.ClientChangedListener;
import com.eldorado.remoteresources.android.client.connection.Client;
import com.eldorado.remoteresources.android.client.connection.ClientConnectionManager;
import com.eldorado.remoteresources.i18n.RemoteResourcesLocalization;
import com.eldorado.remoteresources.i18n.RemoteResourcesMessages;
import com.eldorado.remoteresources.ui.actions.ConnectDeviceAction;
import com.eldorado.remoteresources.ui.model.Device;
import com.eldorado.remoteresources.ui.model.Host;
import com.eldorado.remoteresources.ui.model.PersistentDeviceModel;
import com.eldorado.remoteresources.utils.ResourcesLoader;

/**
 * A device panel with its device and commands
 * 
 * @author Marcelo Marzola Bossoni
 * 
 */
public class DevicePanel extends JPanel implements ClientChangedListener {

	private static final long serialVersionUID = 1971374306762119589L;

	private GridBagLayout layout;

	private final Device device;

	private JButton startCaptureButton;

	private JLabel deviceName;

	private final PersistentDeviceModel model;

	public DevicePanel(PersistentDeviceModel model, Device device) {
		this.model = model;
		this.device = device;
		createComponent();
	}

	private void createComponent() {

		setLayout(layout = new GridBagLayout());
		JLabel icon = new JLabel();
		icon.setIcon(ResourcesLoader.getIcon("cellphone", 32));

		GridBagConstraints iconConstraints = new GridBagConstraints();
		iconConstraints.gridx = 1;
		iconConstraints.gridy = 0;
		iconConstraints.gridwidth = 1;
		iconConstraints.anchor = GridBagConstraints.LINE_START;
		iconConstraints.fill = GridBagConstraints.NONE;
		iconConstraints.ipadx = 0;
		iconConstraints.ipady = 0;
		iconConstraints.insets = new Insets(1, 5, 1, 1);
		layout.addLayoutComponent(icon, iconConstraints);
		add(icon);

		deviceName = new JLabel(device.getName());

		GridBagConstraints labelConstraints = new GridBagConstraints();
		labelConstraints.gridx = 2;
		labelConstraints.gridy = 0;
		labelConstraints.gridwidth = 1;
		labelConstraints.anchor = GridBagConstraints.CENTER;
		labelConstraints.fill = GridBagConstraints.HORIZONTAL;
		labelConstraints.ipadx = 0;
		labelConstraints.ipady = 0;
		labelConstraints.insets = new Insets(1, 5, 1, 1);
		labelConstraints.weightx = 0.2;

		layout.addLayoutComponent(deviceName, labelConstraints);
		deviceName.setFont(new Font("SansSerif", Font.PLAIN, 14));
		add(deviceName);

		addButton(
				ResourcesLoader.getIcon("gears", 32),
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.DevicePanel_Tooltip_Configuration))
				.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						DeviceConfigurationDialog dialog = new DeviceConfigurationDialog(
								device, model);
						dialog.setLocationRelativeTo(DevicePanel.this);
						if (dialog.open()) {
							applyConfigChanges(dialog.getDeviceOldName());
						}
					}

				});

		if (!PersistentDeviceModel.LOCALHOST.equals(device.getHost())) {
			addButton(
					ResourcesLoader.getIcon("button-cross", 32),
					RemoteResourcesLocalization
							.getMessage(RemoteResourcesMessages.DevicePanel_Tooltip_Remotion))
					.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent arg0) {
							model.removeDevice(device);
						}
					});
		}

		startCaptureButton = addButton(
				ResourcesLoader.getIcon("button-play", 32),
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.DevicePanel_Tooltip_StartCapture));
		startCaptureButton.setAction(new ConnectDeviceAction(model, device,
				this));

	}

	private JButton addButton(Icon icon, String tooltip) {
		JButton button = new JButton();
		button.setIcon(icon);
		button.setBorder(BorderFactory.createEmptyBorder());
		button.setContentAreaFilled(false);
		button.setToolTipText(tooltip);

		GridBagConstraints buttonConstraints = new GridBagConstraints();
		buttonConstraints.gridx = GridBagConstraints.RELATIVE;
		buttonConstraints.gridy = 0;
		buttonConstraints.gridwidth = 1;
		buttonConstraints.anchor = GridBagConstraints.LINE_END;
		buttonConstraints.fill = GridBagConstraints.NONE;
		buttonConstraints.ipadx = 0;
		buttonConstraints.ipady = 0;
		buttonConstraints.insets = new Insets(1, 1, 1, 1);

		layout.addLayoutComponent(button, buttonConstraints);
		add(button);
		return button;
	}

	private void applyConfigChanges(String deviceOldName) {
		model.changeDeviceName(device, deviceOldName);

		Host host = model.getHost(device.getHost());
		Client client;
		if ((host != null)
				&& ((client = ClientConnectionManager.getInstance().getClient(
						host.getHostname(), host.getPort())) != null)) {
			if (client.isDataConnectionActive(device.getSerialNumber())) {
				client.setPollingRate(device.getSerialNumber(),
						device.getProperty(Device.POLLING_RATE));
				client.setImageQuality(device.getSerialNumber(),
						device.getProperty(Device.IMAGE_QUALITY));
			}
		}
	}

	public void setDeviceNameLabel(String name) {
		deviceName.setText(name);
	}

	@Override
	public void clientConnected(Client client) {
		// do nothing

	}

	@Override
	public void clientDisconnected(Client client) {
		// do nothing

	}

	@Override
	public void deviceConnected(Client client, String device) {
		if (this.device.getSerialNumber().equals(device)) {
			setBackground(Color.LIGHT_GRAY);
		}
	}

	@Override
	public void deviceDisconnected(Client client, String device) {
		if (this.device.getSerialNumber().equals(device)) {
			setBackground(null);
		}

	}

	@Override
	public void deviceAdded(Client client, String device) {
		// do nothing

	}

	@Override
	public void deviceRemoved(Client client, AndroidDeviceClient device) {
		// do nothing

	}

	public Device getDevice() {
		return device;
	}

	public void setCaptureButton(boolean enabled) {
		if (startCaptureButton != null) {
			startCaptureButton.setEnabled(enabled);
		}
	}

}
