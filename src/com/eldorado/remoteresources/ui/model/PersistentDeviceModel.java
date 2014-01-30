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
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import com.eldorado.remoteresources.android.client.screencapture.CaptureManager;
import com.eldorado.remoteresources.i18n.RemoteResourcesLocalization;
import com.eldorado.remoteresources.i18n.RemoteResourcesMessages;
import com.eldorado.remoteresources.ui.DevicesContainer;
import com.eldorado.remoteresources.ui.HostsContainer;

/**
 * This class represents the device model for user made additions. So, this is
 * only intended to be used to manage devices in the UI, although its data can
 * be used to connect to clients.
 * 
 * 
 * @author Marcelo Marzola Bossoni
 * 
 */
public class PersistentDeviceModel {

	public static final String LOCALHOST = "localhost";

	private final Map<String, Host> hosts;

	// contains only the devices currently connected
	private final Map<String, Device> devices;

	// contains devices that are connected, the ones that were disconnected
	// while RR was running and the ones in device.cfg
	private final Map<String, Device> devicesHistory;

	private final Set<DeviceModelChangeListener> listeners;

	public PersistentDeviceModel() {
		hosts = new HashMap<String, Host>();
		devices = new HashMap<String, Device>();
		devicesHistory = new HashMap<String, Device>();
		listeners = new HashSet<DeviceModelChangeListener>();
	}

	public Host getHost(String hostname) {
		return hosts.get(hostname);
	}

	public Collection<Host> getHosts() {
		return hosts.values();
	}

	public Collection<Device> getDevices() {
		return devices.values();
	}

	public Device getDevice(String name) {
		return devices.get(name);
	}

	public void addDevice(Device device) {
		addDevice(device, true);
	}

	/**
	 * Adds a new device to this model
	 * 
	 * @param device
	 *            the device to be added
	 * @param notifyListeners
	 *            notify or not the listeners
	 */
	private void addDevice(Device device, boolean notifyListeners) {
		if (devicesHistory.keySet().contains(device.getSerialNumber())) {
			devices.put(devicesHistory.get(device.getSerialNumber()).getName(),
					devicesHistory.get(device.getSerialNumber()));
		} else {
			devices.put(device.getName(), device);
			devicesHistory.put(device.getSerialNumber(), device);
		}

		Host host = hosts.get(device.getHost());
		if (!host.getDevices().contains(device)) {
			host.addDevice(device);
		}

		if (notifyListeners) {
			for (DeviceModelChangeListener listener : listeners) {
				listener.deviceAdded(device);
			}
		}
	}

	public void addHost(Host host) {
		if (hosts.get(host.getName()) == null) {
			hosts.put(host.getName(), host);
		}

		for (DeviceModelChangeListener listener : listeners) {
			listener.hostAdded(host);
		}

		for (Device device : host.getDevices()) {
			addDevice(device, false);
		}

	}

	public void removeDevice(Device device) {
		removeDevice(device, true);
	}

	private void removeDevice(Device device, boolean notify) {
		boolean disconnectDevice = true;
		Host h = hosts.get(device.getHost());

		if (h != null) {
			int option = JOptionPane
					.showOptionDialog(
							null,
							RemoteResourcesLocalization
									.getMessage(RemoteResourcesMessages.ConnectDeviceAction_RemoveDevice),
							RemoteResourcesLocalization
									.getMessage(RemoteResourcesMessages.ConnectDeviceAction_RemoveDeviceTitle),
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE,
							null,
							new String[] {
									RemoteResourcesLocalization
											.getMessage(RemoteResourcesMessages.ConnectDeviceAction_RemoveDeviceYes),
									RemoteResourcesLocalization
											.getMessage(RemoteResourcesMessages.ConnectDeviceAction_RemoveDeviceNo) },
							null);

			disconnectDevice = option == JOptionPane.YES_OPTION ? true : false;
		}

		if (disconnectDevice) {

			Device d = devices.remove(device.getName());
			if (!d.getHost().equalsIgnoreCase(LOCALHOST)) {
				devicesHistory.remove(d.getSerialNumber());
			}

			if (h != null) {
				h.getDevices().remove(d.getName());
			}
			if ((d != null) && notify) {
				for (DeviceModelChangeListener listener : listeners) {
					listener.deviceRemoved(d);
				}
			}
		} else {
			Device d = devices.get(device.getName());
			if ((d != null) && notify) {
				for (DeviceModelChangeListener listener : listeners) {
					if (listener instanceof DevicesContainer) {
						((DevicesContainer) listener).disableCapture(d);
					} else if (listener instanceof HostsContainer) {
						((HostsContainer) listener).disableCapture(d);
					}
				}
			}
		}
	}

	public void changeDeviceName(Device device, String oldName) {
		Device d = devices.get(oldName);
		if (d != null) {

			devices.remove(oldName);
			devices.put(device.getName(), device);

			for (DeviceModelChangeListener listener : listeners) {
				listener.deviceChangedName(device, oldName);
			}
		}
	}

	public void removeHost(String name) {
		Host h = hosts.remove(name);

		boolean disconnectHost = true;
		int option = JOptionPane
				.showConfirmDialog(
						null,
						RemoteResourcesLocalization
								.getMessage(RemoteResourcesMessages.HostPanel_RemoveHost),
						"Remove Host", JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);

		disconnectHost = option == JOptionPane.YES_OPTION ? true : false;

		if (disconnectHost) {
			if (h != null) {
				for (Device device : h.getDevices()) {
					removeDevice(device, false);
					CaptureManager.getInstance().stopCapture();
				}
				for (DeviceModelChangeListener listener : listeners) {
					listener.hostRemoved(h);
				}
			}
		}
	}

	public void addModelChangedListener(DeviceModelChangeListener listener) {
		listeners.add(listener);
	}

	public void removeModelChangedListener(DeviceModelChangeListener listener) {
		listeners.remove(listeners);
	}

	public void save(File file) {
		FileOutputStream outputStream = null;
		try {
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			if (file.canWrite()) {
				outputStream = new FileOutputStream(file);
				for (Device device : devicesHistory.values()) {
					device.write(outputStream);
				}
				for (Host host : hosts.values()) {
					if (!LOCALHOST.equals(host.getHostname())) {
						host.write(outputStream);
					}
				}
			}
		} catch (IOException e) {

		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					// do nothing
				}
			}
		}
	}

	public void load(File file) {
		BufferedReader reader = null;
		try {
			if (file.canRead()) {
				reader = new BufferedReader(new FileReader(file));
				String line = reader.readLine();
				Map<String, Host> hosts = new HashMap<String, Host>();
				while (line != null) {
					if (line.trim().length() > 0) {
						if (Device.HEADER.equals(line)) {
							Device d = Device.read(reader);
							devicesHistory.put(d.getSerialNumber(), d);
							devices.put(d.getName(), d);
							Host h = hosts.get(d.getHost());
							if (h != null) {
								h.addDevice(d);
							} else {
								if (!LOCALHOST.equals(d.getHost())) {
									h = new Host(d.getHost());
									h.addDevice(d);
									hosts.put(h.getName(), h);
								}
							}

						} else if (Host.HEADER.equals(line)) {
							Host h = Host.read(reader);
							Host added = hosts.get(h.getName());
							if (added != null) {
								added.setHostname(h.getHostname());
								added.setPort(h.getPort());
							}
						}
					}
					line = reader.readLine();
				}
				for (Host h : hosts.values()) {
					addHost(h);
				}
			}
		} catch (IOException e) {

		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					// do nothing
				}
			}
		}
	}

}
