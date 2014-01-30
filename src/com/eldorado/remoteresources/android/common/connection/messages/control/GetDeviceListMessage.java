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

package com.eldorado.remoteresources.android.common.connection.messages.control;

/**
 * This class represents a command to gets server's device list
 * 
 * @author Marcelo Marzola Bossoni
 * 
 */
public class GetDeviceListMessage extends ControlMessage {

	private static final long serialVersionUID = -6138178183884160327L;

	private final GetDeviceListMessageType deviceListMessageType;

	/**
	 * Create a new GetDeviceList message
	 * 
	 * @param sequenceNumber
	 *            this message sequence number
	 * @param changesOnly
	 *            if the server must send entire device list, or just the
	 *            difference from the last GetDeviceList message
	 */
	public GetDeviceListMessage(int sequenceNumber,
			GetDeviceListMessageType deviceListMessageType) {
		super(sequenceNumber, ControlMessageType.GET_DEVICE_LIST);
		this.deviceListMessageType = deviceListMessageType;
	}

	/**
	 * Get this message type
	 * 
	 * @return this message type
	 * @see GetDeviceListMessageType
	 */
	public GetDeviceListMessageType getDeviceListMessageType() {
		return deviceListMessageType;
	}
}
