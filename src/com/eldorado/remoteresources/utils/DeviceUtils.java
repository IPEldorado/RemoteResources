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

package com.eldorado.remoteresources.utils;

import java.util.HashMap;
import java.util.Map;

import com.eldorado.remoteresources.ui.model.Device;
import com.eldorado.remoteresources.ui.model.PersistentDeviceModel;

/**
 * Create a map with device (key) and nickname (value)
 * 
 * @author Luiz Rafael de Souza
 * 
 */
public class DeviceUtils {

	private static DeviceUtils instance = null;
	private final Map<String, String> devices = new HashMap<String, String>();

	public static DeviceUtils getInstance() {
		if (instance == null) {
			instance = new DeviceUtils();
		}

		return instance;
	}

	public void setDeviceNickname(String device, String nickname) {
		devices.put(device, nickname);
	}

	public String getDeviceNickname(String device) {
		return devices.get(device);
	}

	public boolean changeNickname(String device, String newNickname,
			PersistentDeviceModel model) {

		boolean isChanged = false;

		for (Device dev : model.getDevices()) {
			if (dev.getName().equals(newNickname)
					&& !dev.getSerialNumber().equals(device)) {
				return false;
			}
		}
		if (devices.containsKey(device)) {
			devices.put(device, newNickname);
			isChanged = true;
		}

		return isChanged;
	}

}
