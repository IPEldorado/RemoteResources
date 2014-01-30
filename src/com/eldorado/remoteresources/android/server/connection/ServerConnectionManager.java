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

package com.eldorado.remoteresources.android.server.connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.AndroidDebugBridge.IDeviceChangeListener;
import com.android.ddmlib.IDevice;
import com.eldorado.remoteresources.android.server.AndroidDevice;

public class ServerConnectionManager implements IDeviceChangeListener {
	/**	 */
	private static ServerConnectionManager instance;

	/** */
	private AndroidDebugBridge debugBridge;

	/**
	 * 
	 */
	private final HashMap<String, AndroidDevice> devices;

	/**
	 * 
	 */
	private ServerConnectionManager() {
		devices = new HashMap<String, AndroidDevice>();
		AndroidDebugBridge
				.addDeviceChangeListener(ServerConnectionManager.this);
		AndroidDebugBridge.init(false);
	}

	/**
	 * 
	 * @return
	 */
	public static ServerConnectionManager getInstance() {
		if (instance == null) {
			instance = new ServerConnectionManager();
		}

		return instance;
	}

	/**
	 * 
	 * @param adbPath
	 */
	public void initializeDebugBridge(String adbPath) {
		if (debugBridge == null) {
			debugBridge = AndroidDebugBridge.createBridge(adbPath, false);
		}
	}

	/**
	 * 
	 */
	public void disposeDebugBridge() {
		if (debugBridge != null) {
			disposeDevices();
			AndroidDebugBridge.terminate();
			debugBridge = null;
		}
	}

	@Override
	public void deviceChanged(IDevice device, int changeMask) {
		if (device.isOnline() && device.arePropertiesSet()) {
			registerDevice(device);
		}
	}

	@Override
	public void deviceConnected(IDevice device) {
		// do nothing (we always get a device changed afterwards)
	}

	@Override
	public void deviceDisconnected(IDevice device) {
		String serialNumber = device.getSerialNumber();

		if (devices.containsKey(serialNumber)) {
			AndroidDevice androidDevice = devices.get(serialNumber);

			androidDevice.dispose();

			devices.remove(serialNumber);
		}
	}

	/**
	 * 
	 * @param device
	 */
	private void registerDevice(IDevice device) {
		String serialNumber = device.getSerialNumber();

		if (!devices.containsKey(serialNumber)) {
			AndroidDevice androidDevice = new AndroidDevice(device);
			devices.put(serialNumber, androidDevice);
		}
	}

	/**
	 * 
	 * @return
	 */
	public List<AndroidDevice> getDevices() {
		List<AndroidDevice> devices = new ArrayList<AndroidDevice>(
				this.devices.values());

		return devices;
	}

	/**
	 * 
	 */
	public void disposeDevices() {
		for (AndroidDevice device : getDevices()) {
			device.dispose();
		}
	}
}
