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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.android.chimpchat.adb.LoggingOutputReceiver;
import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.InstallException;
import com.android.ddmlib.SyncException;
import com.android.ddmlib.TimeoutException;
import com.eldorado.remoteresources.android.client.AndroidDeviceClient;
import com.eldorado.remoteresources.android.common.connection.IConnectionConstants;
import com.eldorado.remoteresources.android.common.connection.messages.Message;
import com.eldorado.remoteresources.android.common.connection.messages.control.GetDeviceListMessageType;
import com.eldorado.remoteresources.android.server.AndroidDevice;
import com.eldorado.remoteresources.android.server.ServerLogger;
import com.eldorado.remoteresources.utils.SubType;

//import com.android.chimpchat.adb.LoggingOutputReceiver;

/**
 * This is the Remote Resources server, intended to control the message flow and
 * devices
 * 
 * @author Marcelo Marzola Bossoni
 * 
 */
public class Server extends Thread {
	private final Socket connectionSocket;

	private AtomicInteger sequenceNumber;

	private ObjectInputStream inputStream;

	private ObjectOutputStream outputStream;

	private ServerMessageHandler messageHandler;

	private final Map<String, DataServer> dataServers = new HashMap<String, DataServer>();

	private final List<AndroidDeviceClient> previousDeviceListForRemoved = new ArrayList<AndroidDeviceClient>();

	private final List<AndroidDeviceClient> previousDeviceListForAdded = new ArrayList<AndroidDeviceClient>();

	private final LoggingOutputReceiver mCurrentLogCat = null;

	private final IDevice mCurrentLoggedDevice = null;

	/**
	 * Create a new server for a socket connection
	 * 
	 * @param connection
	 */
	public Server(Socket connection) {
		super("Server - " + connection.getRemoteSocketAddress());
		ServerLogger.info(Server.class, "Starting server for client ["
				+ connection.getRemoteSocketAddress() + "]", null);
		ServerLogger
				.debug(Server.class,
						"Local address is ["
								+ connection.getLocalSocketAddress() + "]",
						null);
		connectionSocket = connection;

	}

	@Override
	public void run() {
		// keep receiving messages and handling them. handlers are supposed to
		// send the acknowledgement message
		try {
			outputStream = new ObjectOutputStream(
					connectionSocket.getOutputStream());
			inputStream = new ObjectInputStream(
					connectionSocket.getInputStream());
			messageHandler = new ServerMessageHandler(this);
			Message message = (Message) inputStream.readObject();
			while (message != null) {
				// TODO: improve message handling to non-blocking
				messageHandler.handleMessage(message);
				message = (Message) inputStream.readObject();
			}
		} catch (IOException e) {
			// error trying to receive a new message. This means some network
			// problem occurred OR the remote socket was closed
			ServerLogger.error(Server.class,
					"Error on main server loop. Shutting server down.", e);

		} catch (ClassNotFoundException e) {
			ServerLogger
					.error(Server.class,
							"Unable to determine the class of received object. Shutting server down.",
							e);
		} finally {
			close();
		}

	}

	synchronized int getNextSequenceNumber() {
		return sequenceNumber.addAndGet(1);
	}

	void sendMessage(Message message) {
		// TODO: improve to retry at least one more time
		try {
			outputStream.writeObject(message);
		} catch (IOException e) {
			ServerLogger.error(
					Server.class,
					"Error sending message to client ["
							+ connectionSocket.getRemoteSocketAddress()
							+ "]. Shutting server down.", e);
		}
	}

	boolean isDeviceConnected(String deviceSerialNumber) {
		return dataServers.get(deviceSerialNumber) != null;
	}

	int setupDataServer(AndroidDevice device) {
		DataServer server = new DataServer(device, connectionSocket
				.getLocalAddress().getHostName());
		server.start();
		while (!server.isReady()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// do nothing
			}
		}
		dataServers.put(device.getSerialNumber(), server);

		return ((InetSocketAddress) server.getAddress()).getPort();
	}

	/**
	 * Close this server instance by closing all streams
	 */
	public void close() {
		ServerLogger.info(Server.class, "Closing server", null);
		stopDataServers();
		try {
			outputStream.close();
		} catch (IOException e) {
			// do nothing
		}

		try {
			inputStream.close();
		} catch (IOException e) {
			// do nothing
		}

		try {
			connectionSocket.close();
		} catch (IOException e) {
			// do nothing
		}
	}

	private void stopDataServers() {
		for (String dataServedDevice : dataServers.keySet()) {
			stopDataServer(dataServedDevice);
		}
	}

	void stopDataServer(String serialNumber) {
		if (dataServers.get(serialNumber) != null) {
			dataServers.get(serialNumber).close();
		}
	}

	public synchronized List<AndroidDeviceClient> getDeviceList(
			GetDeviceListMessageType messageType) {
		List<AndroidDevice> devices = ServerConnectionManager.getInstance()
				.getDevices();
		List<AndroidDeviceClient> currentDeviceList = new ArrayList<AndroidDeviceClient>();
		List<AndroidDeviceClient> returnList = new ArrayList<AndroidDeviceClient>();

		for (AndroidDevice device : devices) {
			currentDeviceList.add(new AndroidDeviceClient(device
					.getSerialNumber(), device.getDeviceModel()));
		}
		switch (messageType) {
		case ALL:
			previousDeviceListForAdded.clear();
			previousDeviceListForAdded.addAll(currentDeviceList);
			previousDeviceListForRemoved.clear();
			previousDeviceListForRemoved.addAll(currentDeviceList);
			returnList.addAll(previousDeviceListForAdded);
			break;
		case ADDED:
			returnList.addAll(currentDeviceList);
			returnList.removeAll(previousDeviceListForAdded);
			previousDeviceListForAdded.clear();
			previousDeviceListForAdded.addAll(currentDeviceList);
			break;
		case REMOVED:
			returnList.addAll(previousDeviceListForRemoved);
			returnList.removeAll(currentDeviceList);
			previousDeviceListForRemoved.clear();
			previousDeviceListForRemoved.addAll(currentDeviceList);
			break;

		default:
			break;
		}

		return returnList;
	}

	int getImageQuality(String serialNumber) {
		int quality = IConnectionConstants.DEFAULT_IMAGE_QUALITY;
		DataServer dataServer = dataServers.get(serialNumber);
		if (dataServer != null) {
			quality = dataServer.getImageQuality();
		}
		return quality;
	}

	long getPollingRate(String serialNumber) {
		long rate = IConnectionConstants.DEFAULT_FRAME_DELAY;
		DataServer dataServer = dataServers.get(serialNumber);
		if (dataServer != null) {
			rate = dataServer.getFrameDelay();
		}
		return rate;
	}

	int setImageQuality(String serialNumber, int data) {
		int quality = IConnectionConstants.DEFAULT_IMAGE_QUALITY;
		DataServer dataServer = dataServers.get(serialNumber);
		if (dataServer != null) {
			dataServer.setImageQuality(data);
			quality = data;
		}
		return quality;
	}

	long setPollingRate(String serialNumber, long data) {
		long rate = IConnectionConstants.DEFAULT_FRAME_DELAY;
		DataServer dataServer = dataServers.get(serialNumber);
		if (dataServer != null) {
			dataServer.setFrameDelay(data);
			rate = data;
		}
		return rate;
	}

	String getLogCatCapture(final AndroidDevice aDevice, String level) {
		String log = null;
		switch (level) {
		case "VERBOSE":
			log = aDevice.getShellCommand("logcat -d long *:V");
			break;
		case "ERROR":
			log = aDevice.getShellCommand("logcat -d long *:E");
			break;
		case "INFO":
			log = aDevice.getShellCommand("logcat -d long *:I");
			break;
		case "WARN":
			log = aDevice.getShellCommand("logcat -d long *:W");
			break;
		default:
			log = aDevice.getShellCommand("logcat -d long");
			break;

		}
		return log;
	}

	String getShellCommandResult(final AndroidDevice aDevice, String command) {
		return aDevice.getShellCommand(command);
	}

	// TODO better handling of exception / execution results
	String getHandleFileResult(final AndroidDevice aDevice, SubType subType,
			String localFile, String remoteFile) {

		try {
			switch (subType) {
			case PULL:
				aDevice.getDevice().pullFile(remoteFile, localFile);
				break;
			case PUSH:
				aDevice.getDevice().pushFile(localFile, remoteFile);
				break;
			default:
				break;
			}
		} catch (SyncException | IOException | AdbCommandRejectedException
				| TimeoutException e) {
			e.printStackTrace();

			return "Command failed:" + e.getLocalizedMessage();
		}

		return "Command success";
	}

	// TODO better handling of exception / execution results
	String getHandlePackageResult(final AndroidDevice aDevice, SubType subType,
			String fileName) {

		switch (subType) {
		case INSTALL:
			try {
				aDevice.getDevice().installPackage(fileName, true);
			} catch (InstallException e) {
				e.printStackTrace();

				return "Install failed:" + e.getLocalizedMessage();
			}
		case UNINSTALL:
			try {
				aDevice.getDevice().uninstallPackage(fileName);
			} catch (InstallException e) {
				e.printStackTrace();

				return "Uninstall failed:" + e.getLocalizedMessage();
			}
		default:
			break;
		}

		return "Command success";
	}

	// TODO better handling of exception / execution results
	String rebootDevice(final AndroidDevice aDevice, String optionalParameter) {
		try {
			aDevice.getDevice().reboot(optionalParameter);
		} catch (TimeoutException | AdbCommandRejectedException | IOException e) {
			e.printStackTrace();

			return "Command failed:" + e.getLocalizedMessage();
		}

		return "Command success";
	}
}
