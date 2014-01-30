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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.eldorado.remoteresources.RemoteResourcesConfiguration;
import com.eldorado.remoteresources.android.common.connection.IConnectionConstants;

/**
 * Model for device to save to/load from disk Intended to be used by UI
 * 
 * @author Marcelo Marzola Bossoni
 * 
 */
public class Device {

	public static final String HEADER = "[device]";

	public static final String NAME = "name";

	public static final String SERIAL = "sn";

	public static final String HOST = "host";

	public static final String POLLING_RATE = "polling";

	public static final String IMAGE_QUALITY = "quality";

	public static final String SCRIPT_GENERATION = "script";

	public static final String SCRIPT_PATH = "scriptPath";

	public static final String SCRIPT_NAME = "scriptName";

	private final Map<String, String> deviceProperties = new HashMap<String, String>();

	public Device(String name, String host, String serialNumber) {
		deviceProperties.put(NAME, name);
		deviceProperties.put(HOST, host);
		deviceProperties.put(SERIAL, serialNumber);
		deviceProperties.put(SCRIPT_GENERATION, "false");
		deviceProperties
				.put(SCRIPT_PATH,
						RemoteResourcesConfiguration.getRemoteResourcesDir() != null ? RemoteResourcesConfiguration
								.getRemoteResourcesDir()
								+ File.separator
								+ "scripts" : System //$NON-NLS-1$
								.getProperty("user.home"));
		deviceProperties.put(SCRIPT_NAME, generateNameScriptFile(name, host));
		deviceProperties.put(POLLING_RATE,
				Long.toString(IConnectionConstants.DEFAULT_FRAME_DELAY));
		deviceProperties.put(IMAGE_QUALITY,
				Integer.toString(IConnectionConstants.DEFAULT_IMAGE_QUALITY));
	}

	private String generateNameScriptFile(String name, String host) {

		String date_mask = "yyyyMMdd";

		String device_nickname = "ScriptFile_";
		if (name != null) {
			device_nickname += name + "_";
		}

		java.util.Date today = new java.util.Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(today);
		SimpleDateFormat format_date = new SimpleDateFormat(date_mask);

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(device_nickname);
		stringBuilder.append("_");
		stringBuilder.append(host);
		stringBuilder.append("_");
		stringBuilder.append(format_date.format(today));
		String script_name = stringBuilder.toString() + ".txt";

		return script_name;
	}

	public void setHost(String host) {
		deviceProperties.put(HOST, host);
	}

	public String getHost() {
		return deviceProperties.get(HOST);
	}

	public void setSerialNumber(String sn) {
		deviceProperties.put(SERIAL, sn);
	}

	public String getSerialNumber() {
		return deviceProperties.get(SERIAL);
	}

	public String getName() {
		return deviceProperties.get(NAME);
	}

	public void setName(String name) {
		deviceProperties.put(NAME, name);
	}

	public String getProperty(String property) {
		return deviceProperties.get(property);
	}

	public void setProperty(String property, String value) {
		deviceProperties.put(property, value);
	}

	public Map<String, String> getProperties() {
		return deviceProperties;
	}

	public boolean getScriptGeneration() {
		if (deviceProperties.get(SCRIPT_GENERATION).equals("true")) {
			return true;
		} else {
			return false;
		}
	}

	public void setScriptGeneration(boolean b) {
		if (b == true) {
			deviceProperties.put(SCRIPT_GENERATION, "true");
		} else {
			deviceProperties.put(SCRIPT_GENERATION, "false");
		}
	}

	public void write(FileOutputStream outputStream) throws IOException {
		StringBuilder builder = new StringBuilder();
		builder.append(HEADER);
		builder.append("\n");

		for (String key : deviceProperties.keySet()) {
			builder.append(key);
			builder.append("=");
			builder.append(deviceProperties.get(key));
			builder.append("\n");
		}

		outputStream.write(builder.toString().getBytes());
	}

	public static Device read(BufferedReader reader) throws IOException {

		boolean finished = false;
		String line;
		Map<String, String> properties = new HashMap<String, String>();

		reader.mark(1024);
		line = reader.readLine();
		if ((line != null) && line.startsWith("[")) {
			reader.reset();
			finished = true;
		}
		while ((line != null) && !finished) {
			properties.put(line.split("=")[0], line.split("=")[1]);
			reader.mark(1024);
			line = reader.readLine();
			if ((line != null) && line.startsWith("[")) {
				reader.reset();
				finished = true;
			}
		}
		Device dev = new Device(null, null, null);
		for (String prop : properties.keySet()) {
			dev.setProperty(prop, properties.get(prop));
		}
		return dev;
	}

	@Override
	public boolean equals(Object obj) {
		boolean equal = false;
		if (obj instanceof Device) {
			Device dev = (Device) obj;
			Map<String, String> otherDevProps = dev.getProperties();
			boolean areAllEqual = true;
			if (otherDevProps.size() == deviceProperties.size()) {
				for (String prop : deviceProperties.keySet()) {
					areAllEqual = areAllEqual
							&& deviceProperties.get(prop).equals(
									otherDevProps.get(prop));
				}
				equal = areAllEqual;
			}
		}
		return equal;
	}

	// copies the properties of this device into the argument
	public void copyProperties(Device d) {
		setHost(d.getHost());
		setName(d.getName());
		setProperty(POLLING_RATE, d.getProperty(POLLING_RATE));
		setProperty(IMAGE_QUALITY, d.getProperty(IMAGE_QUALITY));
		setProperty(SCRIPT_GENERATION, d.getProperty(SCRIPT_GENERATION));
		setProperty(SCRIPT_PATH, d.getProperty(SCRIPT_PATH));
		setProperty(SCRIPT_NAME, d.getProperty(SCRIPT_NAME));
	}
}
