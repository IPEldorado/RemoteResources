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
 * The adb command message. This message expects a Ack message with the response
 * data.
 * 
 * @author Marcelo Marzola Bossoni
 * 
 */
public abstract class DeviceCommandMessage extends ControlMessage {

	private static final long serialVersionUID = -5614708423309692638L;

	private final String serialNumber;

	private final DeviceCommandMessageType deviceCommandMessageType;

	/**
	 * Create a new device command message
	 * 
	 * @param sequenceNumber
	 *            the message sequence number
	 * @param serialNumber
	 *            the device serial number
	 * @param deviceCommandMessageType
	 *            the command type
	 */
	public DeviceCommandMessage(int sequenceNumber, String serialNumber,
			DeviceCommandMessageType deviceCommandMessageType) {
		super(sequenceNumber, ControlMessageType.EXECUTE_DEVICE_COMMAND);
		this.serialNumber = serialNumber;
		this.deviceCommandMessageType = deviceCommandMessageType;
	}

	/**
	 * Get this command message
	 * 
	 * @return the command message
	 */
	public DeviceCommandMessageType getDeviceCommandMessageType() {
		return deviceCommandMessageType;
	}

	/**
	 * Get device serial number
	 * 
	 * @return the device serial number
	 */
	public String getSerialNumber() {
		return serialNumber;
	}

}
