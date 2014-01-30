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

package com.eldorado.remoteresources.android.client.connection;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.eldorado.remoteresources.RemoteResourcesConfiguration;
import com.eldorado.remoteresources.android.client.AndroidDeviceClient;
import com.eldorado.remoteresources.android.client.ClientChangedListener;
import com.eldorado.remoteresources.android.common.Status;
import com.eldorado.remoteresources.android.common.connection.IConnectionConstants;

/**
 * This class is responsible to manage client connections.
 * 
 * @author Marcelo Marzola Bossoni
 * 
 */
public class ClientConnectionManager {

	public static final String LOCALHOST = IConnectionConstants.LOCALHOST;

	public static final int LOCALPORT = RemoteResourcesConfiguration
			.getInstance().getServerPort();

	private static ClientConnectionManager instance;

	private final Map<String, Client> clients = new HashMap<String, Client>();

	private final Set<ClientChangedListener> listeners;

	/**
	 * We currently support only one active device connected
	 */
	private Client activeClient = null;

	private String activeDevice = null;

	private ClientConnectionManager() {
		listeners = new HashSet<ClientChangedListener>();
	}

	/**
	 * Add a client changed listener. Has no effect if listener is already
	 * registered
	 * 
	 * @param listener
	 *            the listener
	 */
	public void addClientChangedListener(ClientChangedListener listener) {
		listeners.add(listener);
	}

	/**
	 * Remove a client changed listener
	 * 
	 * @param listener
	 *            the listener
	 */
	public void removeClientChangedListener(ClientChangedListener listener) {
		listeners.remove(listener);
	}

	public static ClientConnectionManager getInstance() {
		if (instance == null) {
			instance = new ClientConnectionManager();
		}
		return instance;
	}

	/**
	 * Connect to a server
	 * 
	 * @param hostName
	 *            the address to connect to
	 * @param port
	 *            the port
	 * @return the client connection
	 * @throws IOException
	 *             if no connection was possible
	 * @throws UnknownHostException
	 *             if was not possible to resolve host
	 */
	public Client connect(String hostName, int port)
			throws UnknownHostException, IOException {
		final Client client = new Client(hostName, port);
		client.connect();
		new Thread("Check device state") { //$NON-NLS-1$
			@Override
			public void run() {
				while (!client.isConnected()
						&& (client.getStatus() == Status.OK)) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// do nothing
					}
				}
				if (client.getStatus() == Status.OK) {
					List<ClientChangedListener> copy = new ArrayList<ClientChangedListener>();
					copy.addAll(listeners);
					for (ClientChangedListener listener : copy) {
						listener.clientConnected(client);
					}
				}
			};
		}.start();
		clients.put(hostName + ":" + port, client); //$NON-NLS-1$
		return client;
	}

	private void removeClient(String hostname, int port) {
		clients.remove(hostname + ":" + port); //$NON-NLS-1$
	}

	public void disconnect(String hostname, int port) {
		Client client = getClient(hostname, port);
		if (client != null) {
			removeClient(hostname, port);
			disconnect(client);
		}
		List<ClientChangedListener> copy = new ArrayList<ClientChangedListener>();
		copy.addAll(listeners);
		for (ClientChangedListener listener : copy) {
			listener.clientDisconnected(client);
		}
	}

	public void disconnect(Client client) {
		client.disconnect();
	}

	public Client getClient(String hostname, int port) {
		return clients.get(hostname + ":" + port); //$NON-NLS-1$
	}

	public Client getClient(String id) {
		return clients.get(id);
	}

	public void addDevice(Client c, AndroidDeviceClient device) {
		if (c.getDeviceList().get(device.getSerialNumber()) == null) {
			c.addDevice(device);
			List<ClientChangedListener> copy = new ArrayList<ClientChangedListener>();
			copy.addAll(listeners);
			for (ClientChangedListener listener : copy) {
				listener.deviceAdded(c, device.getSerialNumber());
			}
		}
	}

	public void removeDevice(Client c, AndroidDeviceClient device) {
		c.removeDevice(device);
		List<ClientChangedListener> copy = new ArrayList<ClientChangedListener>();
		copy.addAll(listeners);
		for (ClientChangedListener listener : copy) {
			listener.deviceRemoved(c, device);
		}
	}

	public void startDataConnection(Client c, String device) {
		if ((activeClient != null) && (activeDevice != null)) {
			stopDataConnection(activeClient, activeDevice);
		}
		c.startDataConnection(device);
		activeClient = c;
		activeDevice = device;
		List<ClientChangedListener> copy = new ArrayList<ClientChangedListener>();
		copy.addAll(listeners);
		for (ClientChangedListener listener : copy) {
			listener.deviceConnected(c, device);
		}
	}

	public void stopDataConnection(Client c, String device) {
		if (c.isDataConnectionActive(device)) {
			c.stopDataConnection(device);
			List<ClientChangedListener> copy = new ArrayList<ClientChangedListener>();
			copy.addAll(listeners);
			for (ClientChangedListener listener : copy) {
				listener.deviceDisconnected(c, device);
			}
		}
	}

	/**
	 * Close this manager
	 */
	public void close() {
		// do nothing
	}

}
