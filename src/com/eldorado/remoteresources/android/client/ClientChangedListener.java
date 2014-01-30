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

package com.eldorado.remoteresources.android.client;

import com.eldorado.remoteresources.android.client.connection.Client;

/**
 * This interface intends to provide the common notifications
 * ClientConnectionManager can trigger
 * 
 * @author Marcelo Marzola Bossoni
 * 
 */
public interface ClientChangedListener {

	/**
	 * Client just connected.
	 * 
	 * @param client
	 *            the client connected
	 */
	public void clientConnected(Client client);

	/**
	 * Client just disconnected.
	 * 
	 * @param client
	 *            the client connected
	 */
	public void clientDisconnected(Client client);

	/**
	 * Device data connection took place in the client and device described
	 * 
	 * @param client
	 *            the client connected
	 * @param device
	 *            the device serial number
	 */
	public void deviceConnected(Client client, String device);

	/**
	 * Device data connection stopped in the client and device described
	 * 
	 * @param client
	 *            the client connected
	 * @param device
	 *            the device serial number
	 */
	public void deviceDisconnected(Client client, String device);

	/**
	 * Device added in the client
	 * 
	 * @param client
	 *            the client connected
	 * @param device
	 *            the device serial number
	 */
	public void deviceAdded(Client client, String device);

	/**
	 * Device removed from client
	 * 
	 * @param client
	 *            the client connected
	 * @param device
	 *            the device serial number
	 */
	public void deviceRemoved(Client client, AndroidDeviceClient device);

}
