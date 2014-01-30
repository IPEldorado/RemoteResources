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

import java.io.Serializable;

/**
 * This is a representation of an Android Device from client point of view that
 * does not have an adb running, so fields must be as close as possible to
 * primitives since this object will go through the network.
 * 
 * @author Marcelo Marzola Bossoni
 * 
 */
public class AndroidDeviceClient implements Serializable {

	private static final long serialVersionUID = -4688390733043132636L;

	/**
	 * The device serial number
	 */
	private final String serialNumber;

	/**
	 * The device model
	 */
	private final String model;

	/**
	 * The device client id
	 */
	private String client;

	private boolean online = true;

	public AndroidDeviceClient(String serialNumber, String model) {
		this.model = model;
		this.serialNumber = serialNumber;
	}

	/**
	 * Get device serial number
	 * 
	 * @return the device serial number
	 */
	public String getSerialNumber() {
		return serialNumber;
	}

	/**
	 * The device model
	 * 
	 * @return the device model (pretty string to show to users)
	 */
	public String getModel() {
		return model;
	}

	/**
	 * This device online state
	 * 
	 * @return true if the device is online, false otherwise
	 */
	public boolean isOnline() {
		return online;
	}

	/**
	 * set this device online state. For now it has the same online definition
	 * as ddms
	 * 
	 * @param online
	 *            true if online, false otherwise
	 */
	public void setOnline(boolean online) {
		this.online = online;
	}

	/**
	 * Set this device client address
	 * 
	 * @param client
	 *            the client id
	 */
	public void setClient(String client) {
		this.client = client;
	}

	/**
	 * Get this device client id
	 * 
	 * @return the client id
	 */
	public String getClient() {
		return client;
	}

	@Override
	public boolean equals(Object obj) {
		boolean equals = false;
		if (obj instanceof AndroidDeviceClient) {
			AndroidDeviceClient deviceClient = (AndroidDeviceClient) obj;
			if (deviceClient.getModel().equals(model)
					&& deviceClient.getSerialNumber().equals(serialNumber)) {
				equals = true;
			}

		}
		return equals;
	}

	@Override
	public String toString() {
		return model + "(" + serialNumber + ")";
	}

}
