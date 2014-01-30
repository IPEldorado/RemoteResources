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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Model for host to save to/load from disk Intended to be used by UI
 * 
 * @author Marcelo Marzola Bossoni
 * 
 */
public class Host {

	public static final String HEADER = "[host]";

	private static final String NAME = "name";

	private static final String HOSTNAME = "hostname";

	private static final String PORT = "port";

	private final List<Device> devices;

	private final Map<String, String> hostProperties = new HashMap<String, String>();

	public Host(String name, String hostname, int port) {
		hostProperties.put(NAME, name);
		hostProperties.put(HOSTNAME, hostname);
		hostProperties.put(PORT, Integer.toString(port));
		devices = new ArrayList<Device>();
	}

	public void addDevice(Device device) {
		devices.add(device);
	}

	public Host(String name) {
		this(name, null, 0);
	}

	public String getHostname() {
		return hostProperties.get(HOSTNAME);
	}

	public void setHostname(String hostname) {
		hostProperties.put(HOSTNAME, hostname);
	}

	public int getPort() {
		String portStr = hostProperties.get(PORT);
		int port = 0;
		try {
			port = Integer.parseInt(portStr);
		} catch (NumberFormatException e) {

		}
		return port;
	}

	public void setPort(int port) {
		hostProperties.put(PORT, Integer.toString(port));
	}

	public List<Device> getDevices() {
		return devices;
	}

	public String getName() {
		return hostProperties.get(NAME);
	}

	public void setName(String name) {
		hostProperties.put(NAME, name);
	}

	public void setProperty(String property, String value) {
		hostProperties.put(property, value);
	}

	public String getProperty(String property) {
		return hostProperties.get(property);
	}

	public Map<String, String> getProperties() {
		return hostProperties;
	}

	public void write(FileOutputStream outputStream) throws IOException {
		StringBuilder builder = new StringBuilder();
		builder.append(HEADER);
		builder.append("\n");

		for (String key : hostProperties.keySet()) {
			builder.append(key);
			builder.append("=");
			builder.append(hostProperties.get(key));
			builder.append("\n");
		}

		outputStream.write(builder.toString().getBytes());

	}

	public static Host read(BufferedReader reader) throws IOException {

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
		Host host = new Host(null, null, 0);
		for (String prop : properties.keySet()) {
			host.setProperty(prop, properties.get(prop));
		}
		return host;
	}

	@Override
	public boolean equals(Object obj) {
		boolean equal = false;
		if (obj instanceof Host) {
			Host host = (Host) obj;
			Map<String, String> otherHostProps = host.getProperties();
			boolean areAllEqual = true;
			if (otherHostProps.size() == hostProperties.size()) {
				for (String prop : hostProperties.keySet()) {
					areAllEqual = areAllEqual
							&& hostProperties.get(prop).equals(
									otherHostProps.get(prop));
				}
				equal = areAllEqual;
			}
		}
		return equal;
	}
}
