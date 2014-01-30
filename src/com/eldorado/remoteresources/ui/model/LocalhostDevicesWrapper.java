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

package com.eldorado.remoteresources.ui.model;

import com.eldorado.remoteresources.android.client.AndroidDeviceClient;
import com.eldorado.remoteresources.android.client.ClientChangedListener;
import com.eldorado.remoteresources.android.client.connection.Client;
import com.eldorado.remoteresources.android.client.connection.ClientConnectionManager;

/**
 * This class is responsible to listen to locahost server and modify the
 * underlying model by automatically adding these devices to it
 * 
 * @author Marcelo Marzola Bossoni
 * 
 */
public class LocalhostDevicesWrapper implements ClientChangedListener {

	/**
	 * The underlying model
	 */
	private final PersistentDeviceModel model;

	public LocalhostDevicesWrapper(PersistentDeviceModel model) {
		this.model = model;
	}

	@Override
	public void clientConnected(Client client) {
		if (isLocalHost(client)) {
			boolean notifyHostAdded = (getLocalHost(false) == null);
			Host host = getLocalHost(true);
			for (AndroidDeviceClient device : client.getDeviceList().values()) {
				Device dev = createDevice(device);
				if (!host.getDevices().contains(dev)) {
					if (notifyHostAdded) {
						host.addDevice(dev);
					} else {
						model.addDevice(dev);
					}

				}
			}
			if (notifyHostAdded) {
				model.addHost(host);
			}
		}

	}

	@Override
	public void clientDisconnected(Client client) {
		if (isLocalHost(client)) {
			model.removeHost(PersistentDeviceModel.LOCALHOST);
		}

	}

	@Override
	public void deviceConnected(Client client, String device) {
		// do nothing

	}

	@Override
	public void deviceDisconnected(Client client, String device) {
		// do nothing

	}

	@Override
	public void deviceAdded(Client client, String device) {
		if (isLocalHost(client)) {
			boolean exists = getLocalHost(false) != null;
			Device dev = createDevice(client.getDeviceList().get(device));
			if (exists) {
				model.addDevice(dev);
			} else {
				Host h = getLocalHost(true);
				h.addDevice(dev);
				model.addHost(h);
			}
		}

	}

	@Override
	public void deviceRemoved(Client client, AndroidDeviceClient device) {
		if (isLocalHost(client)) {
			model.removeDevice(createDevice(device));
		}
	}

	private Device createDevice(AndroidDeviceClient device) {
		for (Device d : model.getDevices()) {
			if (device.getSerialNumber().equalsIgnoreCase(d.getSerialNumber())) {
				return d;
			}
		}
		return new Device(device.getModel() + "@"
				+ PersistentDeviceModel.LOCALHOST,
				PersistentDeviceModel.LOCALHOST, device.getSerialNumber());
	}

	private static boolean isLocalHost(Client client) {
		return ClientConnectionManager.LOCALHOST.equalsIgnoreCase(client
				.getHostname())
				&& (ClientConnectionManager.LOCALPORT == client.getPort());
	}

	private Host getLocalHost(boolean create) {
		Host host = model.getHost(PersistentDeviceModel.LOCALHOST);
		if ((host == null) && create) {
			host = new Host(PersistentDeviceModel.LOCALHOST,
					ClientConnectionManager.LOCALHOST,
					ClientConnectionManager.LOCALPORT);
		}
		return host;
	}

}
