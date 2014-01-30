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
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import com.eldorado.remoteresources.android.client.ClientLogger;
import com.eldorado.remoteresources.android.common.connection.messages.data.DataMessage;
import com.eldorado.remoteresources.android.common.connection.messages.data.DataMessageType;
import com.eldorado.remoteresources.android.common.connection.messages.data.NewFrameMessage;
import com.eldorado.remoteresources.i18n.RemoteResourcesLocalization;
import com.eldorado.remoteresources.i18n.RemoteResourcesMessages;

/**
 * This class represents a data connection transfer for a certain device in a
 * certain server. It is responsible to receive massive data transfers. This
 * class is created after a client/server negotiation.
 * 
 * @author Marcelo Marzola Bossoni
 * 
 */
public class DataClient extends Thread {

	private final String deviceSerialNumber;

	private final Client client;

	private final InetAddress address;

	private final int port;

	private Socket dataConnectionSocket;

	private ObjectInputStream inputStream;

	DataClient(String deviceSerialNumber, Client client, SocketAddress address) {
		this.deviceSerialNumber = deviceSerialNumber;
		this.client = client;
		this.address = ((InetSocketAddress) address).getAddress();
		port = ((InetSocketAddress) address).getPort();
		setName(RemoteResourcesLocalization.getMessage(
				RemoteResourcesMessages.DataClient_UI_ThreadName, new Object[] {
						deviceSerialNumber, address, port }));
	}

	@Override
	public void run() {
		try {
			dataConnectionSocket = new Socket(address, port);
			inputStream = new ObjectInputStream(
					dataConnectionSocket.getInputStream());
			DataMessage message = (DataMessage) inputStream.readObject();
			while (message != null) {
				if (message.getDataMessageType() == DataMessageType.NEW_FRAME) {
					client.notifyDataReceived(deviceSerialNumber,
							((NewFrameMessage) message).getFrameContent());
				}
				message = (DataMessage) inputStream.readObject();
			}
		} catch (IOException e) {
			ClientLogger.error(DataClient.class, RemoteResourcesLocalization
					.getMessage(
							RemoteResourcesMessages.DataClient_Error_IOError,
							new Object[] { deviceSerialNumber,
									address.getHostName(), port }), e);
			ClientConnectionManager.getInstance().stopDataConnection(client,
					deviceSerialNumber);
		} catch (ClassNotFoundException e) {
			ClientLogger
					.error(DataClient.class,
							RemoteResourcesLocalization
									.getMessage(
											RemoteResourcesMessages.DataClient_Error_UnknownObjectType,
											new Object[] { deviceSerialNumber,
													address.getHostName(), port }),
							e);
			ClientConnectionManager.getInstance().stopDataConnection(client,
					deviceSerialNumber);
		}
	}

	void close() {
		try {
			inputStream.close();
			dataConnectionSocket.close();
		} catch (IOException e) {
			ClientLogger
					.error(DataClient.class,
							RemoteResourcesLocalization
									.getMessage(
											RemoteResourcesMessages.DataClient_Error_UnableToCloseProperly,
											new Object[] { deviceSerialNumber,
													address.getHostName(), port }),
							e);
		}
	}
}
