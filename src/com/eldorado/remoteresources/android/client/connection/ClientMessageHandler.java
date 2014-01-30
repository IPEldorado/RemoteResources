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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.eldorado.remoteresources.android.client.AndroidDeviceClient;
import com.eldorado.remoteresources.android.common.connection.messages.Message;
import com.eldorado.remoteresources.android.common.connection.messages.MessageType;
import com.eldorado.remoteresources.android.common.connection.messages.control.AcknowledgmentMessage;
import com.eldorado.remoteresources.android.common.connection.messages.control.ControlFileHandlingMessage;
import com.eldorado.remoteresources.android.common.connection.messages.control.ControlLogCaptureMessage;
import com.eldorado.remoteresources.android.common.connection.messages.control.ControlMessage;
import com.eldorado.remoteresources.android.common.connection.messages.control.ControlPackageHandlingMessage;
import com.eldorado.remoteresources.android.common.connection.messages.control.ControlRebootMessage;
import com.eldorado.remoteresources.android.common.connection.messages.control.ControlRunShellCommandMessage;
import com.eldorado.remoteresources.android.common.connection.messages.control.GetDeviceListMessage;
import com.eldorado.remoteresources.android.common.connection.messages.control.GetNewFrameMessage;
import com.eldorado.remoteresources.android.common.connection.messages.control.SetupDeviceMessage;
import com.eldorado.remoteresources.utils.StringUtils;

/**
 * This class is responsible for handle client received messages. This handler
 * is highly tied to its client.
 * 
 * @author Marcelo Marzola Bossoni
 * 
 */
public class ClientMessageHandler {

	private final Client client;

	public ClientMessageHandler(Client client) {
		this.client = client;
	}

	void handleMessage(Message m) {
		if (m instanceof ControlMessage) {
			ControlMessage cm = (ControlMessage) m;
			switch (cm.getControlMessageType()) {
			case ACKNOWLEDGMENT:
				handleMessage((AcknowledgmentMessage) cm);
				break;

			default:
				break;
			}

		}
	}

	private void handleMessage(AcknowledgmentMessage message) {
		Message originalMessage = client
				.getMessage(message.getSequenceNumber());
		client.discardMessage(message.getSequenceNumber());
		if (originalMessage.getMessageType() == MessageType.CONTROL_MESSAGE) {
			ControlMessage cMessage = (ControlMessage) originalMessage;
			switch (cMessage.getControlMessageType()) {
			case GET_DEVICE_LIST:
				handleGetDeviceList((GetDeviceListMessage) originalMessage,
						message);
				break;
			case SETUP_DEVICE:
				handleSetupDevice((SetupDeviceMessage) originalMessage, message);
				break;
			case FORCE_GET_NEW_FRAME:
				handleGetNewFrame((GetNewFrameMessage) originalMessage, message);
				break;
			case GET_LOGS:
				handleLogCatCapture((ControlLogCaptureMessage) originalMessage,
						message);
				break;
			case HANDLE_FILE:
				handleFileHandlingCommand(
						(ControlFileHandlingMessage) originalMessage, message);
				break;
			case HANDLE_PACKAGE:
				handlePackageHandlingCommand(
						(ControlPackageHandlingMessage) originalMessage,
						message);
				break;
			case RUN_SHELL_COMMAND:
				handleRunShellCommand(
						(ControlRunShellCommandMessage) originalMessage,
						message);
				break;
			case REBOOT_DEVICE:
				handleRebootCommand((ControlRebootMessage) originalMessage,
						message);
				break;
			default:
				break;
			}

		}
	}

	private void handleLogCatCapture(ControlLogCaptureMessage originalMessage,
			AcknowledgmentMessage message) {

		try {
			client.createFileLog(originalMessage.getSerialNumber(),
					StringUtils.decompress((byte[]) message.getData()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void handleFileHandlingCommand(
			ControlFileHandlingMessage originalMessage,
			AcknowledgmentMessage message) {

		try {
			client.displayFileHandlingResult(originalMessage.getSerialNumber(),
					StringUtils.decompress((byte[]) message.getData()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void handlePackageHandlingCommand(
			ControlPackageHandlingMessage originalMessage,
			AcknowledgmentMessage message) {

		try {
			client.displayPackageHandlingResult(
					originalMessage.getSerialNumber(),
					StringUtils.decompress((byte[]) message.getData()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void handleRunShellCommand(
			ControlRunShellCommandMessage originalMessage,
			AcknowledgmentMessage message) {

		try {
			client.displayRunShellCommandResult(
					originalMessage.getSerialNumber(),
					StringUtils.decompress((byte[]) message.getData()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void handleRebootCommand(ControlRebootMessage originalMessage,
			AcknowledgmentMessage message) {
		// TODO reboot device and do not crash
		try {
			client.displayRebootCommandResult(
					originalMessage.getSerialNumber(),
					StringUtils.decompress((byte[]) message.getData()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void handleGetNewFrame(GetNewFrameMessage originalMessage,
			AcknowledgmentMessage message) {
		client.saveScreenshot(originalMessage.getDeviceSerialNumber(),
				(byte[]) message.getData());
	}

	private void handleGetDeviceList(GetDeviceListMessage originalMessage,
			AcknowledgmentMessage message) {

		List<AndroidDeviceClient> receivedList = new ArrayList<AndroidDeviceClient>();
		if (message.getData() instanceof Collection<?>) {
			Collection<?> devices = (Collection<?>) message.getData();
			for (Object device : devices) {
				if (device instanceof AndroidDeviceClient) {
					receivedList.add((AndroidDeviceClient) device);
				}
			}
		}

		switch (originalMessage.getDeviceListMessageType()) {
		case ALL:
			for (AndroidDeviceClient device : receivedList) {
				ClientConnectionManager.getInstance().addDevice(client, device);
			}
			break;

		case ADDED:
			for (AndroidDeviceClient device : receivedList) {
				ClientConnectionManager.getInstance().addDevice(client, device);
			}
			break;

		case REMOVED:
			for (AndroidDeviceClient device : receivedList) {
				ClientConnectionManager.getInstance().removeDevice(client,
						device);
			}
			break;
		default:
			break;
		}

		client.setConnected();
	}

	private void handleSetupDevice(SetupDeviceMessage originalMessage,
			AcknowledgmentMessage message) {
		switch (originalMessage.getSetupDeviceMessageType()) {
		case CONNECT_DEVICE:
			handleConnectDevice(originalMessage, message);
			break;
		case SET_IMAGE_QUALITY:
		case SET_POLLING_RATE:
			// do nothing?
			break;

		default:
			break;
		}
	}

	private void handleConnectDevice(SetupDeviceMessage originalMessage,
			AcknowledgmentMessage message) {
		client.connectDeviceDataChannel(originalMessage.getSerialNumber(),
				(int) message.getData());
	}
}
