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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;

import com.eldorado.remoteresources.android.client.AndroidDeviceClient;
import com.eldorado.remoteresources.android.client.ClientChangedListener;
import com.eldorado.remoteresources.android.client.connection.Client;
import com.eldorado.remoteresources.android.client.connection.ClientConnectionManager;
import com.eldorado.remoteresources.i18n.RemoteResourcesLocalization;
import com.eldorado.remoteresources.i18n.RemoteResourcesMessages;
import com.eldorado.remoteresources.ui.model.Device;
import com.eldorado.remoteresources.ui.model.Host;
import com.eldorado.remoteresources.ui.model.PersistentDeviceModel;
import com.eldorado.remoteresources.utils.ResourcesLoader;

/**
 * An expandable Host panel with several devices.
 * 
 * @author Marcelo Marzola Bossoni
 * 
 */
public class HostPanel extends BasePanel implements ClientChangedListener {

	private static final long serialVersionUID = 6859890293705676275L;

	private GridBagLayout headerLayout;

	private GridBagLayout contentLayout;

	private final Host host;

	private JButton expandButton;

	private boolean expanded = false;

	private Panel content;

	private final Map<String, DevicePanel> devicePanels = new HashMap<String, DevicePanel>();

	private final PersistentDeviceModel model;

	/**
	 * Creates a new panel with the desired host
	 * 
	 * @param model
	 *            the device model where the host lies
	 * @param host
	 *            the host
	 */
	public HostPanel(PersistentDeviceModel model, Host host) {
		this.host = host;
		this.model = model;
		createComponent();
	}

	private void createComponent() {

		setLayout(headerLayout = new GridBagLayout());

		createHeader();
		createContentPanel();

	}

	private void createHeader() {

		expandButton = new JButton();
		expandButton.setIcon(ResourcesLoader.getIcon("toggle-expand", 32));
		expandButton.setContentAreaFilled(false);
		expandButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				expanded = !expanded;
				content.setVisible(expanded);
				expandButton.setIcon(ResourcesLoader.getIcon(
						expanded ? "toggle-collapse" : "toggle-expand", 32));
				revalidate();
				repaint();
			}
		});

		GridBagConstraints expandButtonConstraints = new GridBagConstraints();
		expandButtonConstraints.gridx = 0;
		expandButtonConstraints.gridy = 0;
		expandButtonConstraints.gridwidth = 1;
		expandButtonConstraints.anchor = GridBagConstraints.LINE_START;
		expandButtonConstraints.fill = GridBagConstraints.NONE;
		expandButtonConstraints.ipadx = 0;
		expandButtonConstraints.ipady = 0;
		expandButtonConstraints.insets = new Insets(1, 1, 1, 1);

		headerLayout.addLayoutComponent(expandButton, expandButtonConstraints);
		add(expandButton);

		JLabel icon = new JLabel();
		icon.setIcon(ResourcesLoader.getIcon("computer", 32));

		GridBagConstraints iconConstraints = new GridBagConstraints();
		iconConstraints.gridx = 1;
		iconConstraints.gridy = 0;
		iconConstraints.gridwidth = 1;
		iconConstraints.anchor = GridBagConstraints.LINE_START;
		iconConstraints.fill = GridBagConstraints.NONE;
		iconConstraints.ipadx = 0;
		iconConstraints.ipady = 0;
		iconConstraints.insets = new Insets(1, 5, 1, 1);
		headerLayout.addLayoutComponent(icon, iconConstraints);
		add(icon);

		JLabel label = new JLabel(host.getName());

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

		headerLayout.addLayoutComponent(label, labelConstraints);
		add(label);

		addHeaderButton(
				ResourcesLoader.getIcon("information-frame", 32),
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.HostPanel_Tooltip_Information))
				.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						HostConfigurationDialog dialog = new HostConfigurationDialog(
								host);
						dialog.setLocationRelativeTo(HostPanel.this);
						dialog.open();
					}
				});

		addHeaderButton(
				ResourcesLoader.getIcon("button-cross", 32),
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.HostPanel_Tooltip_Remotion))
				.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						model.removeHost(host.getName());
					}
				});

	}

	private JButton addHeaderButton(Icon icon, String tooltip) {
		JButton button = new JButton();
		button.setIcon(icon);
		button.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
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

		headerLayout.addLayoutComponent(button, buttonConstraints);
		add(button);
		return button;
	}

	private void createContentPanel() {
		content = new Panel();
		content.setLayout(contentLayout = new GridBagLayout());
		GridBagConstraints contentConstraints = new GridBagConstraints();
		contentConstraints.gridx = 0;
		contentConstraints.gridy = 1;
		contentConstraints.gridwidth = GridBagConstraints.REMAINDER;
		contentConstraints.gridheight = GridBagConstraints.REMAINDER;
		contentConstraints.weightx = 1;
		contentConstraints.weighty = 1;
		contentConstraints.fill = GridBagConstraints.BOTH;
		headerLayout.addLayoutComponent(content, contentConstraints);
		add(content);
		content.setVisible(expanded);
	}

	/**
	 * Adds a new device to this host
	 * 
	 * @param device
	 *            the device to be added
	 */
	void addDevice(Device device) {
		// if user previously preferred not to delete remote device from list,
		// avoids creating two entries for same device
		DevicePanel dp = devicePanels.get(device.getSerialNumber());
		if (dp != null) {
			dp.setCaptureButton(true);
			return;
		}
		DevicePanel devicePanel = new DevicePanel(model, device);
		ClientConnectionManager.getInstance().addClientChangedListener(
				devicePanel);
		GridBagConstraints devicePanelConstraints = new GridBagConstraints();
		devicePanelConstraints.gridx = 0;
		devicePanelConstraints.gridwidth = GridBagConstraints.REMAINDER;
		devicePanelConstraints.weightx = 1;
		devicePanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		devicePanelConstraints.insets = new Insets(1, 60, 1, 1);

		contentLayout.addLayoutComponent(devicePanel, devicePanelConstraints);

		content.add(devicePanel);
		devicePanels.put(device.getSerialNumber(), devicePanel);

	}

	public DevicePanel getDevicePanel(String serialNumber) {
		return devicePanels.get(serialNumber);
	}

	boolean isEmpty() {
		return devicePanels.isEmpty();
	}

	void setExpanded(boolean expanded) {
		content.setVisible(expanded);
		expandButton.setIcon(ResourcesLoader.getIcon(
				expanded ? "toggle-collapse" : "toggle-expand", 32));
		this.expanded = expanded;
	}

	@Override
	public void repaint() {
		super.repaint();
		if ((content != null) && content.isVisible()) {
			content.repaint();
		}
	}

	public void removeDevice(Device device) {
		DevicePanel panel = devicePanels.remove(device.getSerialNumber());
		ClientConnectionManager.getInstance()
				.removeClientChangedListener(panel);
		content.remove(panel);
		revalidate();
		repaint();
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
		// do nothing

	}

	@Override
	public void deviceDisconnected(Client client, String device) {
		if (devicePanels.containsKey(device)) {
			setExpanded(false);
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

}
