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
 * This class represents a setup device command and its responsible to request a
 * setup for a certain device
 * 
 * @author Marcelo Marzola Bossoni
 * 
 */
public class SetupDeviceMessage extends ControlMessage {

	private static final long serialVersionUID = 8656929098871780942L;

	private final String serialNumber;

	private final Object data;

	private final SetupDeviceMessageType setupDeviceMessageType;

	/**
	 * Create a new setup message
	 * 
	 * @param sequenceNumber
	 *            the message sequence number
	 * @param setupDeviceMessageType
	 *            this message type
	 * @param serialNumber
	 *            the device serial number
	 * @param data
	 *            the data (if needed)
	 * 
	 * @see SetupDeviceMessageType
	 */
	public SetupDeviceMessage(int sequenceNumber,
			SetupDeviceMessageType setupDeviceMessageType, String serialNumber,
			Object data) {
		super(sequenceNumber, ControlMessageType.SETUP_DEVICE);
		this.serialNumber = serialNumber;
		this.data = data;
		this.setupDeviceMessageType = setupDeviceMessageType;
	}

	/**
	 * Get this message data
	 * 
	 * @return this message data
	 */
	public Object getData() {
		return data;
	}

	/**
	 * get device serial number
	 * 
	 * @return the device serial number
	 */
	public String getSerialNumber() {
		return serialNumber;
	}

	/**
	 * get setup message type
	 * 
	 * @return the setup message type
	 */
	public SetupDeviceMessageType getSetupDeviceMessageType() {
		return setupDeviceMessageType;
	}

}
