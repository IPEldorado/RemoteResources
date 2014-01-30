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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JOptionPane;

import com.eldorado.remoteresources.RemoteResourcesConfiguration;
import com.eldorado.remoteresources.android.client.AndroidDeviceClient;
import com.eldorado.remoteresources.android.client.ClientLogger;
import com.eldorado.remoteresources.android.common.Status;
import com.eldorado.remoteresources.android.common.connection.IConnectionConstants;
import com.eldorado.remoteresources.android.common.connection.messages.Message;
import com.eldorado.remoteresources.android.common.connection.messages.control.CommandMessageFactory;
import com.eldorado.remoteresources.android.common.connection.messages.control.ControlLogCaptureMessage;
import com.eldorado.remoteresources.android.common.connection.messages.control.ControlLogCaptureTypeMessage;
import com.eldorado.remoteresources.android.common.connection.messages.control.ControlPackageHandlingMessage;
import com.eldorado.remoteresources.android.common.connection.messages.control.ControlRunShellCommandMessage;
import com.eldorado.remoteresources.android.common.connection.messages.control.GetDeviceListMessage;
import com.eldorado.remoteresources.android.common.connection.messages.control.GetDeviceListMessageType;
import com.eldorado.remoteresources.android.common.connection.messages.control.GetNewFrameMessage;
import com.eldorado.remoteresources.android.common.connection.messages.control.SetupDeviceMessage;
import com.eldorado.remoteresources.android.common.connection.messages.control.SetupDeviceMessageType;
import com.eldorado.remoteresources.i18n.RemoteResourcesLocalization;
import com.eldorado.remoteresources.i18n.RemoteResourcesMessages;
import com.eldorado.remoteresources.utils.Command;
import com.eldorado.remoteresources.utils.DeviceUtils;

/**
 * This class represents a server connection, so we have multiple client classes
 * per Remote Resources client application.
 * 
 * This class handle connection with the server and have its own sequence
 * number.
 * 
 * Incoming message handling should be done in a separated thread
 * (ClientInputHandler) while output messages can be sent through this class
 * 
 * This class is also responsible to negotiate a data connection with server for
 * a certain device and make it available to the application using the
 * DataReceivedListener
 * 
 * @author Marcelo Marzola Bossoni
 * 
 */
public class Client {

	private static final String DEFAULT_LOGCAT_PATH = RemoteResourcesConfiguration
			.getRemoteResourcesDir() != null ? RemoteResourcesConfiguration
			.getRemoteResourcesDir() + File.separator + "logs" : System
			.getProperty("user.home");

	private File logFilePath;

	private String logFileName;

	private Socket connection;

	private final InetAddress address;

	private final String hostname;

	private final int port;

	private ObjectOutputStream outputStream;

	private ObjectInputStream inputStream;

	private final Map<String, AndroidDeviceClient> devices = new HashMap<String, AndroidDeviceClient>();

	private final AtomicInteger sequenceNumber = new AtomicInteger();

	private final Map<Integer, Message> messages = new HashMap<Integer, Message>();

	private final Map<String, DataReceivedListener> dataListeners = new HashMap<String, DataReceivedListener>();

	private final Map<String, DataClient> dataConnections = new HashMap<String, DataClient>();

	private final ClientMessageHandler messageHandler;

	private final Status status = Status.OK;

	private DataReceivedListener sshotListener;

	private ClientDeviceChangesThread deviceChangesThread;

	private static final String LOG_FILE_EXTENSION = ".txt";

	public Client(String hostname, int port) throws UnknownHostException {
		InetAddress address = InetAddress.getByName(hostname);
		this.hostname = hostname;
		this.address = address;
		this.port = port != 0 ? port
				: IConnectionConstants.CONTROL_SERVER_DEFAULT_PORT;
		messageHandler = new ClientMessageHandler(this);
	}

	void connect() throws IOException {
		if (connection == null) {
			ClientLogger
					.info(Client.class,
							RemoteResourcesLocalization
									.getMessage(
											RemoteResourcesMessages.Client_Info_ConnectingToServer,
											new Object[] { hostname, port }),
							null);
			connection = new Socket(address, port);
			outputStream = new ObjectOutputStream(connection.getOutputStream());
			inputStream = new ObjectInputStream(connection.getInputStream());
			ClientInputHandler handler = new ClientInputHandler();
			handler.start();
			listDevices();
		}
	}

	void disconnect() {

		ClientLogger.info(Client.class, RemoteResourcesLocalization.getMessage(
				RemoteResourcesMessages.Client_Info_DisconnectingFromServer,
				new Object[] { hostname, port }), null);

		deviceChangesThread.cancel();
		for (String dataConnection : dataConnections.keySet()) {
			ClientConnectionManager.getInstance().stopDataConnection(this,
					dataConnection);
		}
		if (outputStream != null) {
			try {
				outputStream.close();
			} catch (IOException e) {
				// do nothing
			}
		}
		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e) {
				// do nothing
			}
		}
		if (connection != null) {
			try {
				connection.close();
			} catch (IOException e) {
				// do nothing
			}
		}
		connection = null;
		outputStream = null;
		inputStream = null;
	}

	/**
	 * Return this client device list
	 * 
	 * @return a copy of the list of devices of this client
	 */
	public synchronized Map<String, AndroidDeviceClient> getDeviceList() {
		return devices;
	}

	/**
	 * Add a new device. Only intended to be used by message handler
	 * 
	 * @param device
	 *            the device
	 */
	void addDevice(AndroidDeviceClient device) {
		device.setClient(getDisplayName());
		if (devices.get(device.getSerialNumber()) == null) {
			devices.put(device.getSerialNumber(), device);
		}
	}

	void removeDevice(AndroidDeviceClient device) {
		if (isDataConnectionActive(device.getSerialNumber())) {
			ClientConnectionManager.getInstance().stopDataConnection(this,
					device.getSerialNumber());
		}
		devices.remove(device.getSerialNumber());

	}

	/**
	 * dispatch a get device list message to the server
	 */
	private void listDevices() {
		sendMessage(new GetDeviceListMessage(getNextSequenceNumber(),
				GetDeviceListMessageType.ALL));
	}

	/**
	 * Get a message from message pool. Only intended to be used by message
	 * handler;
	 * 
	 * @param sequenceNumber
	 *            the message sequence number
	 * @return the message with the desired sequence number or null with we have
	 *         no message
	 */
	Message getMessage(Integer sequenceNumber) {
		return messages.get(sequenceNumber);
	}

	/**
	 * Discard a message with a desired sequence number
	 * 
	 * @param sequenceNumber
	 *            the sequence number
	 */
	void discardMessage(Integer sequenceNumber) {
		messages.remove(sequenceNumber);
	}

	/**
	 * Send a message to the server associated with this client
	 * 
	 * @param message
	 *            the message
	 */
	public void sendMessage(Message message) {
		messages.put(message.getSequenceNumber(), message);
		try {
			outputStream.writeObject(message);
		} catch (IOException e) {
			ClientLogger
					.error(Client.class,
							RemoteResourcesLocalization
									.getMessage(
											RemoteResourcesMessages.Client_Error_UnableToSendMessage,
											new Object[] { hostname, port }), e);
			disconnect();
		} catch (NullPointerException e) {
			disconnect();
			try {
				connect();
				outputStream.writeObject(message);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

	public int getNextSequenceNumber() {
		return sequenceNumber.getAndAdd(1);
	}

	/**
	 * Adds a new data received listener for a certain device
	 * 
	 * @param deviceSerialNumber
	 *            the device serial number
	 * @param dataReceivedListener
	 *            the listener
	 */
	public void addDataReceivedListener(String deviceSerialNumber,
			DataReceivedListener dataReceivedListener) {
		dataListeners.put(deviceSerialNumber, dataReceivedListener);
	}

	/**
	 * Remove a data received listener for a certain device
	 * 
	 * @param deviceSerialNumber
	 *            the device serial number
	 * @param dataReceivedListener
	 *            the listener to be removed
	 */
	public void removeDataReceivedListener(String deviceSerialNumber,
			DataReceivedListener dataReceivedListener) {
		dataListeners.remove(deviceSerialNumber);
	}

	/**
	 * Notify listeners about new data
	 * 
	 * @param serialNumber
	 *            the device serial number
	 * @param data
	 *            the received data
	 */
	void notifyDataReceived(String serialNumber, byte[] data) {
		DataReceivedListener listener = dataListeners.get(serialNumber);
		if (listener != null) {
			listener.newFrameReceived(serialNumber, data);
		}

	}

	/**
	 * Requests a new data connection with the device with the matching serial
	 * number. Does nothing if a connection is already done (in this case just
	 * register a new data listener)
	 * 
	 * Use the method {@link Client#isDataConnectionActive(String)} to check if
	 * the connection is already set
	 * 
	 * @param deviceSerialNumber
	 *            the device serial number
	 */
	void startDataConnection(String deviceSerialNumber) {
		if (!isDataConnectionActive(deviceSerialNumber)) {
			sendMessage(new SetupDeviceMessage(getNextSequenceNumber(),
					SetupDeviceMessageType.CONNECT_DEVICE, deviceSerialNumber,
					null));
		}
	}

	/**
	 * Stop device data connection.
	 * 
	 * @param deviceSerialNumber
	 *            the device serial number
	 */
	void stopDataConnection(String deviceSerialNumber) {
		if (isDataConnectionActive(deviceSerialNumber)) {
			dataConnections.remove(deviceSerialNumber).close();
			sendMessage(new SetupDeviceMessage(getNextSequenceNumber(),
					SetupDeviceMessageType.DISCONNECT_DEVICE,
					deviceSerialNumber, null));
		}
	}

	/**
	 * Connects the device data channel
	 * 
	 * @param deviceSerialNumber
	 *            the device serial number
	 * @param port
	 *            data connection port. The address will be the same as the
	 *            client
	 */
	void connectDeviceDataChannel(String deviceSerialNumber, Integer port) {
		DataClient dataClient = new DataClient(deviceSerialNumber, this,
				new InetSocketAddress(hostname, port));
		dataClient.start();
		dataConnections.put(deviceSerialNumber, dataClient);
	}

	/**
	 * Check if a data connection for a requested serial number is already in
	 * place
	 * 
	 * @param deviceSerialNumber
	 *            the device serial number
	 * @return true if data connection is active, false otherwise
	 */
	public boolean isDataConnectionActive(String deviceSerialNumber) {
		return dataConnections.get(deviceSerialNumber) != null;
	}

	/**
	 * Check if there is any data connection.
	 * 
	 * @return true if there is a data connection active, false otherwise
	 * */
	public boolean isAnyDataConnectionActive() {
		return !dataConnections.entrySet().isEmpty();
	}

	/**
	 * This class handles incoming messages from server side. This class is a
	 * thread so it could be ran in parallel.
	 * 
	 * @author Marcelo Marzola Bossoni
	 * 
	 */
	class ClientInputHandler extends Thread {

		@Override
		public void run() {
			// just read the next message in the stream and call the
			// appropriated handler
			try {
				Message message = (Message) inputStream.readObject();
				while (message != null) {
					messageHandler.handleMessage(message);
					message = (Message) inputStream.readObject();
				}
			} catch (ClassNotFoundException e) {
				ClientLogger
						.error(ClientInputHandler.class,
								RemoteResourcesLocalization
										.getMessage(
												RemoteResourcesMessages.Client_Error_UnknownObjectType,
												new Object[] { hostname, port }),
								e);
				disconnect();
			} catch (IOException e) {
				ClientLogger.error(ClientInputHandler.class,
						RemoteResourcesLocalization.getMessage(
								RemoteResourcesMessages.Client_Error_IOError,
								new Object[] { hostname, port }), e);
				disconnect();
			}
		}

	}

	class ClientDeviceChangesThread extends Thread {
		private boolean cancel = false;

		@Override
		public void run() {
			while (!cancel) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// do nothing
				}
				sendMessage(new GetDeviceListMessage(getNextSequenceNumber(),
						GetDeviceListMessageType.REMOVED));
				sendMessage(new GetDeviceListMessage(getNextSequenceNumber(),
						GetDeviceListMessageType.ADDED));
			}
		}

		public synchronized void cancel() {
			cancel = true;
		}

	}

	public synchronized boolean isConnected() {
		return deviceChangesThread != null;
	}

	synchronized void setConnected() {
		if (deviceChangesThread == null) {
			deviceChangesThread = new ClientDeviceChangesThread();
			deviceChangesThread.start();
		}
	}

	public int getPort() {
		return port;
	}

	public String getHostname() {
		return hostname;
	}

	/**
	 * Get a textual representation of this client name
	 * 
	 * @return the string representing this client name
	 */
	public String getDisplayName() {
		return hostname + ":" + port; //$NON-NLS-1$
	}

	/**
	 * Return this client status. Errors are expected on client connection
	 * errors
	 * 
	 * @return the actual client status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * Set the time between two screen captures
	 * 
	 * @param serialNumber
	 *            the device serial number
	 * @param value
	 *            the time (in ms) to wait before the next screen capture
	 */
	public void setPollingRate(String serialNumber, String value) {
		if (isDataConnectionActive(serialNumber)) {
			try {
				sendMessage(new SetupDeviceMessage(getNextSequenceNumber(),
						SetupDeviceMessageType.SET_POLLING_RATE, serialNumber,
						Long.parseLong(value)));
			} catch (NumberFormatException e) {
				ClientLogger.warning(Client.class,
						"Unable to set polling rate for device", e);
			}
		}
	}

	/**
	 * Set the image quality
	 * 
	 * @param serialNumber
	 *            the device serial number
	 * @param value
	 *            the value, between 0 and 100, of image quality
	 */
	public void setImageQuality(String serialNumber, String value) {
		if (isDataConnectionActive(serialNumber)) {
			try {
				sendMessage(new SetupDeviceMessage(getNextSequenceNumber(),
						SetupDeviceMessageType.SET_IMAGE_QUALITY, serialNumber,
						Integer.parseInt(value)));
			} catch (NumberFormatException e) {
				ClientLogger.warning(Client.class,
						"Unable to set image quality for device", e);
			}
		}
	}

	public void takeScreenshot(String serialNumber,
			DataReceivedListener listener) {
		sshotListener = listener;
		sendMessage(new GetNewFrameMessage(getNextSequenceNumber(),
				serialNumber, 75));
	}

	void saveScreenshot(String serialNumber, byte[] sshot) {
		sshotListener.newFrameReceived(serialNumber, sshot);
		sshotListener = null;
	}

	public String generateNameFileLog(String device) {

		String date_mask = "yyyyMMdd";

		String device_nickname = "LogFile_";
		if (DeviceUtils.getInstance().getDeviceNickname(device) != null) {
			device_nickname += DeviceUtils.getInstance().getDeviceNickname(
					device)
					+ "_";
		}

		java.util.Date today = new java.util.Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(today);
		SimpleDateFormat format_date = new SimpleDateFormat(date_mask);

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(device_nickname);
		stringBuilder.append(getHostname());
		stringBuilder.append("_");
		stringBuilder.append(String.valueOf(getPort()));
		stringBuilder.append("_");
		stringBuilder.append(format_date.format(today));
		stringBuilder.append("_");
		stringBuilder.append(cal.get(Calendar.HOUR_OF_DAY));
		stringBuilder.append(cal.get(Calendar.MINUTE));
		stringBuilder.append(cal.get(Calendar.SECOND));
		String logfile_name = stringBuilder.toString();

		return logfile_name + LOG_FILE_EXTENSION;
	}

	public String generateNameFileLog() {
		return generateNameFileLog("");
	}

	public void createFileLog(String deviceSerialNumber, String data) {

		try {
			String outputFile;
			if (logFilePath == null) {
				outputFile = DEFAULT_LOGCAT_PATH + File.separator + logFileName;
			} else {
				outputFile = logFilePath.getAbsolutePath() + File.separator
						+ logFileName;
			}

			FileWriter fw = new FileWriter(outputFile, true);
			fw.write(data);
			fw.close();
		} catch (IOException e) {
			// TODO: log exception
		}

	}

	public String getDefaultLogcatPath() {
		return DEFAULT_LOGCAT_PATH;
	}

	/**
	 * Handle the logcat Capture
	 * 
	 * @param serialNumber
	 *            the device serial number
	 * @param file
	 *            complete name of the log to be saved
	 */
	public void getLogCatCapture(String deviceSerialNumber, File file,
			String name, ControlLogCaptureTypeMessage level) {
		if (isDataConnectionActive(deviceSerialNumber)) {
			logFilePath = file;
			logFileName = name;
			sendMessage(new ControlLogCaptureMessage(getNextSequenceNumber(),
					deviceSerialNumber, level));
		}
	}

	/*
	 * public void handleFile(String deviceSerialNumber, String localFile,
	 * String remoteFile, ControlFileHandlingTypeMessage handlingType) { if
	 * (isDataConnectionActive(deviceSerialNumber)) { sendMessage(new
	 * ControlFileHandlingMessage(getNextSequenceNumber(), deviceSerialNumber,
	 * localFile, remoteFile, handlingType)); } }
	 * 
	 * public void runShellCommand(String deviceSerialNumber, String command) {
	 * if (isDataConnectionActive(deviceSerialNumber)) { sendMessage(new
	 * ControlRunShellCommandMessage( getNextSequenceNumber(),
	 * deviceSerialNumber, command)); } }
	 */

	public void runCommand(String deviceSerialNumber, Command command) {
		if (isDataConnectionActive(deviceSerialNumber)) {
			Message m = CommandMessageFactory.getCommandMessage(command,
					getNextSequenceNumber(), deviceSerialNumber);
			sendMessage(m);
		}
	}

	// TODO Handle the adb shell response. Needs some UI to display the results
	public void displayRunShellCommandResult(String serialNumber, String data) {
		ClientLogger.debug(ControlRunShellCommandMessage.class, data, null);

		JOptionPane.showMessageDialog(null, data, "ADB command result",
				JOptionPane.ERROR_MESSAGE);
	}

	// TODO Handle the file handling response. Needs some UI to display the
	// results
	public void displayFileHandlingResult(String serialNumber, String data) {
		ClientLogger.debug(ControlRunShellCommandMessage.class, data, null);

		JOptionPane.showMessageDialog(null, data, "File handling result",
				JOptionPane.ERROR_MESSAGE);
	}

	// TODO Handle the package handling response. Needs some UI to display the
	// results
	public void displayPackageHandlingResult(String serialNumber, String data) {
		ClientLogger.debug(ControlPackageHandlingMessage.class, data, null);

		JOptionPane.showMessageDialog(null, data, "Package handling result",
				JOptionPane.ERROR_MESSAGE);
	}

	// TODO Handle the reboot response. Needs some UI to display the
	// results
	public void displayRebootCommandResult(String serialNumber, String data) {
		ClientLogger.debug(ControlPackageHandlingMessage.class, data, null);

		JOptionPane.showMessageDialog(null, data, "Reboot result",
				JOptionPane.ERROR_MESSAGE);
	}
}
