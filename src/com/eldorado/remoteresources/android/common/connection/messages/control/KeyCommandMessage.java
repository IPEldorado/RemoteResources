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

import com.eldorado.remoteresources.android.common.Keys;

/**
 * This class represents a key command to be executed
 * 
 * @author Marcelo Marzola Bossoni
 * 
 */
public class KeyCommandMessage extends DeviceCommandMessage {

	private static final long serialVersionUID = 7784219670587270710L;

	public final KeyCommandMessageType keyCommandMessageType;

	private final Keys key;

	/**
	 * Create a new key command message to be executed
	 * 
	 * @param sequenceNumber
	 *            the message sequence number
	 * @param device
	 *            the device
	 * @param key
	 *            the key to be pressed
	 * @param keyCommandMessageType
	 *            the command type
	 * 
	 * @see KeyCommandMessageType
	 */
	public KeyCommandMessage(int sequenceNumber, String device, Keys key,
			KeyCommandMessageType keyCommandMessageType) {
		super(sequenceNumber, device, DeviceCommandMessageType.KEY_COMMAND);
		this.key = key;
		this.keyCommandMessageType = keyCommandMessageType;
	}

	/**
	 * Get the key to be pressed/released
	 * 
	 * @return the key
	 */
	public Keys getKey() {
		return key;
	}

	/**
	 * Get the press command (up or down)
	 * 
	 * @return the command
	 */
	public KeyCommandMessageType getKeyCommandType() {
		return keyCommandMessageType;
	}

}
