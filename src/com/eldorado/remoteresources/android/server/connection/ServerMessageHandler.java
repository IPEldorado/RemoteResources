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
import java.util.Iterator;

import com.eldorado.remoteresources.android.common.Status;
import com.eldorado.remoteresources.android.common.connection.messages.Message;
import com.eldorado.remoteresources.android.common.connection.messages.MessageType;
import com.eldorado.remoteresources.android.common.connection.messages.control.AcknowledgmentMessage;
import com.eldorado.remoteresources.android.common.connection.messages.control.ControlFileHandlingMessage;
import com.eldorado.remoteresources.android.common.connection.messages.control.ControlLogCaptureMessage;
import com.eldorado.remoteresources.android.common.connection.messages.control.ControlMessage;
import com.eldorado.remoteresources.android.common.connection.messages.control.ControlPackageHandlingMessage;
import com.eldorado.remoteresources.android.common.connection.messages.control.ControlRebootMessage;
import com.eldorado.remoteresources.android.common.connection.messages.control.ControlRunShellCommandMessage;
import com.eldorado.remoteresources.android.common.connection.messages.control.DeviceCommandMessage;
import com.eldorado.remoteresources.android.common.connection.messages.control.GetDeviceListMessage;
import com.eldorado.remoteresources.android.common.connection.messages.control.GetNewFrameMessage;
import com.eldorado.remoteresources.android.common.connection.messages.control.KeyCommandMessage;
import com.eldorado.remoteresources.android.common.connection.messages.control.SetupDeviceMessage;
import com.eldorado.remoteresources.android.common.connection.messages.control.SetupDeviceMessageType;
import com.eldorado.remoteresources.android.common.connection.messages.control.TouchCommandMessage;
import com.eldorado.remoteresources.android.common.connection.messages.control.TypeCommandMessage;
import com.eldorado.remoteresources.android.server.AndroidDevice;
import com.eldorado.remoteresources.android.server.ServerLogger;
import com.eldorado.remoteresources.utils.ImageUtils;
import com.eldorado.remoteresources.utils.StringUtils;

/**
 * This class is responsible for handle the server messages. It is highly tied
 * with the server instance since message send methods are defined within
 * server.
 * 
 * @author Marcelo Marzola Bossoni
 * 
 */
public class ServerMessageHandler {

	private final Server server;

	public ServerMessageHandler(Server server) {
		this.server = server;
	}

	/**
	 * handle incoming message
	 * 
	 * @param message
	 *            the message
	 */
	void handleMessage(Message message) {
		if (message.getMessageType() == MessageType.CONTROL_MESSAGE) {
			ControlMessage controlMessage = (ControlMessage) message;
			switch (controlMessage.getControlMessageType()) {
			case GET_DEVICE_LIST:
				handleMessage((GetDeviceListMessage) message);
				break;

			case EXECUTE_DEVICE_COMMAND:
				handleMessage((DeviceCommandMessage) message);
				break;

			case SETUP_DEVICE:
				handleMessage((SetupDeviceMessage) message);
				break;

			case FORCE_GET_NEW_FRAME:
				handleMessage((GetNewFrameMessage) message);
				break;

			case GET_LOGS:
				handleMessage((ControlLogCaptureMessage) message);
				break;

			case HANDLE_FILE:
				handleMessage((ControlFileHandlingMessage) message);
				break;

			case HANDLE_PACKAGE:
				handleMessage((ControlPackageHandlingMessage) message);
				break;

			case RUN_SHELL_COMMAND:
				handleMessage((ControlRunShellCommandMessage) message);
				break;

			case REBOOT_DEVICE:
				handleMessage((ControlRebootMessage) message);
				break;

			default:
				break;
			}
		} else {
			// TODO: report error because you are doing it wrong
		}
	}

	private void handleMessage(ControlLogCaptureMessage message) {
		Message m;
		try {
			m = new AcknowledgmentMessage(message.getSequenceNumber(),
					Status.OK, StringUtils.compress(server.getLogCatCapture(
							findDevice(message.getSerialNumber()),
							message.getLogLevel())));
			server.sendMessage(m);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void handleMessage(ControlFileHandlingMessage message) {
		Message m;
		try {
			m = new AcknowledgmentMessage(message.getSequenceNumber(),
					Status.OK, StringUtils.compress(server.getHandleFileResult(
							findDevice(message.getSerialNumber()),
							message.getSubType(), message.getLocalFile(),
							message.getRemoteFile())));
			server.sendMessage(m);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void handleMessage(ControlPackageHandlingMessage message) {
		Message m;
		try {
			m = new AcknowledgmentMessage(message.getSequenceNumber(),
					Status.OK,
					StringUtils.compress(server.getHandlePackageResult(
							findDevice(message.getSerialNumber()),
							message.getSubType(), message.getFileName())));
			server.sendMessage(m);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void handleMessage(ControlRunShellCommandMessage message) {
		Message m;
		try {
			m = new AcknowledgmentMessage(message.getSequenceNumber(),
					Status.OK, StringUtils.compress(server
							.getShellCommandResult(
									findDevice(message.getSerialNumber()),
									message.getCommand())));
			server.sendMessage(m);
		} catch (IOException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void handleMessage(ControlRebootMessage message) {
		Message m;
		try {
			m = new AcknowledgmentMessage(message.getSequenceNumber(),
					Status.OK, StringUtils.compress(server.rebootDevice(
							findDevice(message.getSerialNumber()),
							message.getRebootParameter())));
			server.sendMessage(m);
		} catch (IOException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * // TODO handle status fail /* Handle file handling message private void
	 * handleMessage(ControlFileHandlingMessage message) { Message m; try { m =
	 * new AcknowledgmentMessage(message.getSequenceNumber(), Status.OK,
	 * StringUtils.compress(server .getFileHandlingResult(
	 * findDevice(message.getSerialNumber()), message.getLocalFile(),
	 * message.getRemoteFile(), message.getHandlingType())));
	 * server.sendMessage(m); } catch (IOException e) { // TODO Auto-generated
	 * catch block e.printStackTrace(); } }
	 * 
	 * /* Handle run shell command message private void
	 * handleMessage(ControlRunShellCommandMessage message) { Message m; try { m
	 * = new AcknowledgmentMessage(message.getSequenceNumber(), Status.OK,
	 * StringUtils.compress(server .getShellCommandResult(
	 * findDevice(message.getSerialNumber()), message.getCommand())));
	 * server.sendMessage(m); } catch (IOException e) { // TODO Auto-generated
	 * catch block e.printStackTrace(); } }
	 */
	private void handleMessage(GetDeviceListMessage message) {

		Message m = new AcknowledgmentMessage(message.getSequenceNumber(),
				Status.OK, server.getDeviceList(message
						.getDeviceListMessageType()));
		server.sendMessage(m);
	}

	private void handleMessage(DeviceCommandMessage message) {
		AndroidDevice device = findDevice(message.getSerialNumber());

		switch (message.getDeviceCommandMessageType()) {
		case KEY_COMMAND:
			handleMessage((KeyCommandMessage) message, device);
			break;
		case TOUCH_COMMAND:
			handleMessage((TouchCommandMessage) message, device);
			break;
		case TYPE_COMMAND:
			handleMessage((TypeCommandMessage) message, device);
			break;
		default:
			break;
		}
	}

	private void handleMessage(SetupDeviceMessage message) {
		AndroidDevice device = findDevice(message.getSerialNumber());
		try {
			device.getSerialNumber();
		} catch (NullPointerException e) {
			// this exception may be thrown if device is unplugged while
			// capturing. Since "device" variable is only used when message type
			// is CONNECT_DEVICE, ignore this exception if different
			if (message.getSetupDeviceMessageType() == SetupDeviceMessageType.CONNECT_DEVICE) {
				throw e;
			}
		}

		switch (message.getSetupDeviceMessageType()) {
		case CONNECT_DEVICE:
			server.sendMessage(new AcknowledgmentMessage(message
					.getSequenceNumber(), Status.OK, server
					.setupDataServer(device)));

			break;
		case DISCONNECT_DEVICE:
			server.stopDataServer(message.getSerialNumber());
			server.sendMessage(new AcknowledgmentMessage(message
					.getSequenceNumber(), Status.OK, null));
			break;
		case GET_IMAGE_QUALITY:
			if (server.isDeviceConnected(message.getSerialNumber())) {
				server.sendMessage(new AcknowledgmentMessage(message
						.getSequenceNumber(), Status.OK, server
						.getImageQuality(message.getSerialNumber())));
			} else {
				server.sendMessage(new AcknowledgmentMessage(message
						.getSequenceNumber(), Status.ERROR,
						"Device not connected"));
			}

			break;
		case GET_POLLING_RATE:
			if (server.isDeviceConnected(message.getSerialNumber())) {
				server.sendMessage(new AcknowledgmentMessage(message
						.getSequenceNumber(), Status.OK, server
						.getPollingRate(message.getSerialNumber())));
			} else {
				server.sendMessage(new AcknowledgmentMessage(message
						.getSequenceNumber(), Status.ERROR,
						"Device not connected"));
			}
			break;
		case SET_IMAGE_QUALITY:

			if (server.isDeviceConnected(message.getSerialNumber())) {
				server.sendMessage(new AcknowledgmentMessage(message
						.getSequenceNumber(), Status.OK, server
						.setImageQuality(message.getSerialNumber(),
								(int) message.getData())));
			} else {
				server.sendMessage(new AcknowledgmentMessage(message
						.getSequenceNumber(), Status.ERROR,
						"Device not connected"));
			}

			break;
		case SET_POLLING_RATE:

			if (server.isDeviceConnected(message.getSerialNumber())) {
				server.sendMessage(new AcknowledgmentMessage(message
						.getSequenceNumber(), Status.OK, server.setPollingRate(
						message.getSerialNumber(), (long) message.getData())));
			} else {
				server.sendMessage(new AcknowledgmentMessage(message
						.getSequenceNumber(), Status.ERROR,
						"Device not connected"));
			}

			break;

		default:
			break;
		}
	}

	private void handleMessage(GetNewFrameMessage message) {

		try {
			server.sendMessage(new AcknowledgmentMessage(message
					.getSequenceNumber(), Status.OK, ImageUtils
					.compress(
							findDevice(message.getDeviceSerialNumber())
									.getScreenshot(), message.getQuality())
					.toByteArray()));
		} catch (IOException e) {
			ServerLogger.error(ServerMessageHandler.class,
					"Unable to compress image to be sent to server", e);
			// error: send nack
			server.sendMessage(new AcknowledgmentMessage(message
					.getSequenceNumber(), Status.ERROR, null));
		}

	}

	private void handleMessage(KeyCommandMessage message, AndroidDevice device) {
		switch (message.getKeyCommandType()) {

		case KEY_DOWN:
			device.keyDown(message.getKey());
			server.sendMessage(new AcknowledgmentMessage(message
					.getSequenceNumber(), Status.OK, null));
			break;

		case KEY_UP:
			device.keyUp(message.getKey());
			server.sendMessage(new AcknowledgmentMessage(message
					.getSequenceNumber(), Status.OK, null));
			break;

		default:
			break;
		}
	}

	private void handleMessage(TouchCommandMessage message, AndroidDevice device) {
		switch (message.getTouchCommandType()) {
		case TOUCH_DOWN:
			device.touchDown(message.getX(), message.getY());
			server.sendMessage(new AcknowledgmentMessage(message
					.getSequenceNumber(), Status.OK, null));
			break;

		case TOUCH_MOVE:
			device.touchMove(message.getX(), message.getY());
			server.sendMessage(new AcknowledgmentMessage(message
					.getSequenceNumber(), Status.OK, null));
			break;

		case TOUCH_UP:
			device.touchUp(message.getX(), message.getY());
			server.sendMessage(new AcknowledgmentMessage(message
					.getSequenceNumber(), Status.OK, null));
			break;

		default:
			break;
		}
	}

	private void handleMessage(TypeCommandMessage message, AndroidDevice device) {
		device.type(message.getText());
		server.sendMessage(new AcknowledgmentMessage(message
				.getSequenceNumber(), Status.OK, null));
	}

	private AndroidDevice findDevice(String serialNumber) {
		AndroidDevice device = null;
		Iterator<AndroidDevice> androidDevicesIterator = ServerConnectionManager
				.getInstance().getDevices().iterator();
		while ((device == null) && androidDevicesIterator.hasNext()) {
			AndroidDevice next = androidDevicesIterator.next();
			if (next.getSerialNumber().compareTo(serialNumber) == 0) {
				device = next;
			}
		}
		return device;
	}
}
