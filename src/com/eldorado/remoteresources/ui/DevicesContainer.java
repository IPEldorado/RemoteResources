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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;

import com.eldorado.remoteresources.android.client.connection.ClientConnectionManager;
import com.eldorado.remoteresources.ui.model.Device;
import com.eldorado.remoteresources.ui.model.DeviceModelChangeListener;
import com.eldorado.remoteresources.ui.model.Host;
import com.eldorado.remoteresources.ui.model.PersistentDeviceModel;

/**
 * Devices container that shows devices in plain list. This class listen to
 * model changes an update itself
 * 
 * @author Marcelo Marzola Bossoni
 * 
 */
public class DevicesContainer extends BasePanel implements
		DeviceModelChangeListener {

	private static final long serialVersionUID = 3805929184064963051L;

	private GridBagLayout deviceListLayout;

	private BasePanel headerPanel;

	private final Map<String, DevicePanel> devicesMap = new HashMap<String, DevicePanel>();

	private final List<DevicePanel> panelList = new ArrayList<DevicePanel>();

	private final PersistentDeviceModel model;

	private String hostToIgnore;

	private String hostToShow;

	/**
	 * Creates a new container
	 * 
	 * @param model
	 *            the device model to listen to
	 */
	public DevicesContainer(PersistentDeviceModel model, String headerText) {
		this.model = model;
		model.addModelChangedListener(this);
		setLayout(deviceListLayout = new GridBagLayout());
		createHeader(headerText);
	}

	private void createHeader(String header) {
		headerPanel = new BasePanel();
		headerPanel.setBackground(Color.LIGHT_GRAY);
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.gridx = 0;
		constraints.gridy = 0;
		deviceListLayout.addLayoutComponent(headerPanel, constraints);
		add(headerPanel);

		JLabel title = new JLabel();
		title.setText(header);
		Font currentFont = title.getFont();
		title.setFont(currentFont.deriveFont(Font.BOLD, 14));
		headerPanel.add(title);

	}

	/**
	 * Adds a new device to this container. Not intended to be used outside this
	 * package
	 * 
	 * @param device
	 */
	void addDevice(Device device) {

		// if device was in list of saved localhost devices, copy saved
		// preferences to this device
		for (Device d : model.getDevices()) {
			if (device.getSerialNumber().equals(d.getSerialNumber())) {
				device.copyProperties(d);
				break;
			}
		}

		// if user previously preferred not to delete local device from list,
		// avoids creating two entries for same device
		for (DevicePanel panel : panelList) {
			if (panel.getDevice().getSerialNumber()
					.equals(device.getSerialNumber())) {
				panel.setCaptureButton(true);
				return;
			}
		}

		DevicePanel devicePanel = new DevicePanel(model, device);
		ClientConnectionManager.getInstance().addClientChangedListener(
				devicePanel);
		GridBagConstraints devicePanelConstraints = new GridBagConstraints();
		devicePanelConstraints.gridx = 0;
		devicePanelConstraints.anchor = GridBagConstraints.PAGE_START;
		devicePanelConstraints.gridwidth = GridBagConstraints.REMAINDER;
		devicePanelConstraints.weightx = 1;
		devicePanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		devicePanelConstraints.insets = new Insets(1, 1, 1, 1);

		if (panelList.size() > 0) {
			GridBagConstraints lastConstraints = deviceListLayout
					.getConstraints(panelList.get(panelList.size() - 1));
			lastConstraints.weighty = 0;
			deviceListLayout.setConstraints(
					panelList.get(panelList.size() - 1), lastConstraints);
		}
		devicePanelConstraints.weighty = 1;

		if (devicesMap.isEmpty()) {
			GridBagConstraints headerC = deviceListLayout
					.getConstraints(headerPanel);
			headerC.weighty = 0;
			deviceListLayout.setConstraints(headerPanel, headerC);
		}

		deviceListLayout
				.addLayoutComponent(devicePanel, devicePanelConstraints);

		add(devicePanel);
		devicesMap.put(device.getName(), devicePanel);
		panelList.add(devicePanel);
		revalidate();
		repaint();

	}

	/**
	 * Set hostname to ignore. Cleans host to show.
	 * 
	 * @param hostToIgnore
	 */
	public void setHostToIgnore(String hostToIgnore) {
		this.hostToIgnore = hostToIgnore;
		hostToShow = null;
	}

	/**
	 * Set hostname to show. Cleans host to ignore.
	 * 
	 * @param hostToShow
	 *            the host to show.
	 */
	public void setHostToShow(String hostToShow) {
		this.hostToShow = hostToShow;
		hostToIgnore = null;
	}

	/**
	 * Remove a device from this container
	 * 
	 * @param device
	 */
	void removeDevice(Device device) {
		DevicePanel dp = devicesMap.remove(device.getName());
		if (dp != null) {
			ClientConnectionManager.getInstance().removeClientChangedListener(
					dp);
			if ((panelList.size() > 1)
					&& (panelList.indexOf(dp) == (panelList.size() - 1))) {
				GridBagConstraints newLastConstraints = deviceListLayout
						.getConstraints(panelList.get(panelList.size() - 2));
				newLastConstraints.weighty = 1;
				deviceListLayout
						.setConstraints(panelList.get(panelList.size() - 2),
								newLastConstraints);
			} else if (panelList.size() == 1) {
				GridBagConstraints headerC = deviceListLayout
						.getConstraints(headerPanel);
				headerC.weighty = 1;
				deviceListLayout.setConstraints(headerPanel, headerC);
			}
			panelList.remove(dp);
			remove(dp);
			revalidate();
			repaint();
		}
	}

	@Override
	public void deviceAdded(Device device) {
		if (isNotFiltered(device.getHost())) {
			addDevice(device);
		}
	}

	@Override
	public void deviceRemoved(Device device) {
		if (isNotFiltered(device.getHost())) {
			removeDevice(device);
		}
	}

	@Override
	public void hostAdded(Host host) {
		if (isNotFiltered(host.getHostname())) {
			for (Device device : host.getDevices()) {
				addDevice(device);
			}
		}
	}

	@Override
	public void hostRemoved(Host host) {
		if (isNotFiltered(host.getHostname())) {
			for (Device device : host.getDevices()) {
				removeDevice(device);
			}
		}
	}

	private boolean isNotFiltered(String host) {
		return ((hostToIgnore == null) && (hostToShow == null))
				|| ((hostToIgnore != null) && !hostToIgnore.equals(host))
				|| ((hostToShow != null) && hostToShow.equals(host));
	}

	@Override
	public void deviceChangedName(Device device, String oldName) {
		DevicePanel dp = devicesMap.remove(oldName);
		if (dp != null) {
			dp.setDeviceNameLabel(device.getName());
			devicesMap.put(device.getName(), dp);
		}
	}

	public void disableCapture(Device d) {
		DevicePanel dp = devicesMap.get(d.getName());
		if (dp != null) {
			dp.setCaptureButton(false);
		}
	}

}
