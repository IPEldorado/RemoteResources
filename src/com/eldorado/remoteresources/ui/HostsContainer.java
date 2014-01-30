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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.eldorado.remoteresources.android.client.connection.ClientConnectionManager;
import com.eldorado.remoteresources.ui.model.Device;
import com.eldorado.remoteresources.ui.model.DeviceModelChangeListener;
import com.eldorado.remoteresources.ui.model.Host;
import com.eldorado.remoteresources.ui.model.PersistentDeviceModel;

/**
 * A device container class grouped by hosts
 * 
 * @author Marcelo Marzola Bossoni
 * 
 */
public class HostsContainer extends BasePanel implements
		DeviceModelChangeListener {

	private static final long serialVersionUID = -5880806310788196376L;

	private GridBagLayout hostListLayout;

	private final Map<String, HostPanel> hostsMap = new HashMap<String, HostPanel>();

	private final List<HostPanel> hostList = new ArrayList<HostPanel>();

	private final PersistentDeviceModel model;

	/**
	 * Create a new container listening to the model
	 * 
	 * @param model
	 *            the model to listen to changes
	 */
	public HostsContainer(PersistentDeviceModel model) {
		setLayout(hostListLayout = new GridBagLayout());
		this.model = model;
		model.addModelChangedListener(this);
	}

	/**
	 * Add a new host to this container. All its devices will be added as well
	 * 
	 * @param host
	 *            the host to add
	 * @return the panel created as the result of this add operation
	 */
	HostPanel addHost(Host host) {
		HostPanel hostpanel = new HostPanel(model, host);
		// the host isn't in the panel, then add it.
		if (!hostsMap.keySet().contains(host.getName())) {
			ClientConnectionManager.getInstance().addClientChangedListener(
					hostpanel);
			GridBagConstraints constraints = new GridBagConstraints();
			constraints.anchor = GridBagConstraints.PAGE_START;
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.gridx = 0;
			constraints.weightx = 1;

			if (hostList.size() > 0) {
				GridBagConstraints lastConstraints = hostListLayout
						.getConstraints(hostList.get(hostList.size() - 1));
				lastConstraints.weighty = 0;
				hostListLayout.setConstraints(
						hostList.get(hostList.size() - 1), lastConstraints);
			}
			constraints.weighty = 1;
			hostList.add(hostpanel);

			hostListLayout.addLayoutComponent(hostpanel, constraints);
			add(hostpanel);
			hostsMap.put(host.getName(), hostpanel);
		} // The host is already in the list, just return it.
		else {
			hostpanel = hostsMap.get(host.getName());
		}
		return hostpanel;
	}

	/**
	 * Remove a host
	 * 
	 * @param host
	 */
	private void removeHost(Host host) {
		HostPanel hp = hostsMap.remove(host.getName());
		if (hp != null) {
			ClientConnectionManager.getInstance().removeClientChangedListener(
					hp);

			if ((hostList.size() > 1)
					&& (hostList.indexOf(hp) == (hostList.size() - 1))) {
				GridBagConstraints newLastConstraints = hostListLayout
						.getConstraints(hostList.get(hostList.size() - 2));
				newLastConstraints.weighty = 1;
				hostListLayout.setConstraints(
						hostList.get(hostList.size() - 2), newLastConstraints);
			}
			remove(hp);
			revalidate();
			repaint();
		}
	}

	@Override
	public void deviceAdded(Device device) {
		hostsMap.get(device.getHost()).addDevice(device);
	}

	@Override
	public void deviceRemoved(Device device) {
		HostPanel panel = hostsMap.get(device.getHost());
		if (panel != null) {
			panel.removeDevice(device);
		}
	}

	@Override
	public void hostAdded(Host host) {
		HostPanel panel = addHost(host);
		for (Device d : host.getDevices()) {
			panel.addDevice(d);
		}
	}

	@Override
	public void hostRemoved(Host host) {
		removeHost(host);
	}

	@Override
	public void deviceChangedName(Device device, String oldName) {
		HostPanel hp = hostsMap.get(device.getHost());
		if (hp != null) {
			hp.getDevicePanel(device.getSerialNumber()).setDeviceNameLabel(
					device.getName());
		}
	}

	public void disableCapture(Device d) {
		HostPanel hp = hostsMap.get(d.getHost());
		DevicePanel dp = hp.getDevicePanel(d.getSerialNumber());
		if (dp != null) {
			dp.setCaptureButton(false);
		}
	}

}
